package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InserimentoParametriPanel extends JPanel {
    private ServerInterface server;
    private JTextArea textArea;
    private JList<String> resultList;
    private String areaScelta;
    private JTable climateTable;


    private static String[] NomiColonneParametriPAR = {
            "vento_val",
            "umidita_val",
            "precipitazioni_val",
            "pressione_val",
            "temperatura_val",
            "altitudineghiacchi_val",
            "massaghiacci_val"
    };
    private static String[] NomiColonneParametriNOT = {
            "vento_notes",
            "umidita_notes",
            "precipitazioni_notes",
            "pressione_notes",
            "temperatura_notes",
            "altitudineghiacchi_notes",
            "massaghiacci_notes"
    };
    private static String[] columnNames = {"Climate Category", "Explanation", "Score: 1 ….. 5", "Notes"};
    private static Object[][] data = {
            {"Vento", "Velocità del vento (km/h)", "", ""},
            {"Umidità", "% di Umidità, suddivisa in fasce", "", ""},
            {"Pressione", "In hPa, suddivisa in fasce", "", ""},
            {"Temperatura", "In °C, suddivisa in fasce", "", ""},
            {"Precipitazioni", "In mm di pioggia, suddivisa in fasce", "", ""},
            {"Altitudine dei ghiacciai", "In m, suddivisa in fasce", "", ""},
            {"Massa dei ghiacciai", "In kg, suddivisa in fasce", "", ""}
    };
    private LinkedList<String> listaElementi;

    public InserimentoParametriPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;

        setLayout(new BorderLayout());

        // Pannello per la selezione dell'area di lavoro
        JPanel sceltaAreaDiLavoro = new JPanel(new BorderLayout());
        resultList = new JList<>(new DefaultListModel<>());
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        JLabel titleLabel = new JLabel("Selezione dell'area di lavoro", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        textArea = new JTextArea(10, 10);
        textArea.setEditable(false);

        sceltaAreaDiLavoro.add(titleLabel, BorderLayout.NORTH);
        sceltaAreaDiLavoro.add(resultScrollPane, BorderLayout.CENTER);

        add(sceltaAreaDiLavoro, BorderLayout.NORTH);

        // Tabella per i parametri climatici
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3; // Rende editabile solo le colonne "Score" e "Notes"
            }
        };

        climateTable = new JTable(tableModel);

        // Disabilita il ridimensionamento delle colonne
        climateTable.getTableHeader().setResizingAllowed(false);
        climateTable.setColumnSelectionAllowed(false);
        climateTable.getTableHeader().setReorderingAllowed(false);



        TableCellEditor scoreEditor = new ScoreCellEditor(new JTextField());

        // Applica il TableCellEditor alla colonna "Score"
        climateTable.getColumnModel().getColumn(2).setCellEditor(scoreEditor);



        JScrollPane scrollTable = new JScrollPane(climateTable);
        add(scrollTable, BorderLayout.CENTER);

        // Pannello per i pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton invioButton = new JButton("Invio");
        buttonPanel.add(invioButton);
        JButton backButtonBottom = new JButton("Back");
        buttonPanel.add(backButtonBottom);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listener per il pulsante di invio
        invioButton.addActionListener(e -> {
            Map<String, Object> parametriMap = new HashMap<>();
            for (int row = 0; row < data.length; row++) {
                String score = (String) tableModel.getValueAt(row, 2);
                String notes = (String) tableModel.getValueAt(row, 3);

                if (score != null && !score.isEmpty()) {
                    parametriMap.put(NomiColonneParametriPAR[row], score);
                }
                if (notes != null && !notes.isEmpty()) {
                    parametriMap.put(NomiColonneParametriNOT[row], notes);
                }
            }
            // Invia i parametri al server
            inviaParametri(parametriMap);

            try {
                server.inserisciParametriClimatici(areaScelta, parametriMap);
                JOptionPane.showMessageDialog(this, "Parametri salvati con successo!");
                cardLayout.show(mainPanel, "Home");
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Listener per il pulsante Indietro
        backButtonBottom.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        // Listener per la selezione nella JList
        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = resultList.getSelectedIndex();
                if (selectedIndex != -1) {
                    areaScelta = resultList.getModel().getElementAt(selectedIndex);
                    JOptionPane.showMessageDialog(InserimentoParametriPanel.this,
                            "Hai selezionato: " + areaScelta, "Selezione", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Carica le aree solo dopo che il pannello è completamente inizializzato
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                climateTable.getColumnModel().getColumn(0).setPreferredWidth(10);
                climateTable.getColumnModel().getColumn(1).setPreferredWidth(10);
                climateTable.getColumnModel().getColumn(2).setPreferredWidth(10);
                climateTable.getColumnModel().getColumn(3).setPreferredWidth(10);
                loadAndDisplayData();
            }
        });
    }

    private void loadAndDisplayData() {
        SwingUtilities.invokeLater(() -> {
            try {
                listaElementi = server.mostraElementiDisponibili("aree", "lonlat", false);
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (String elemento : listaElementi) {
                    listModel.addElement(elemento);
                }
                resultList.setModel(listModel);
                resultList.revalidate();
                resultList.repaint();

                StringBuilder sb = new StringBuilder();
                for (String elemento : listaElementi) {
                    sb.append(elemento).append("\n");
                }
                textArea.setText(sb.toString());
                textArea.revalidate();
                textArea.repaint();

                System.out.println("Elementi nel modello della JList:");
                for (int i = 0; i < listModel.getSize(); i++) {
                    System.out.println(listModel.getElementAt(i));
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errore nel caricamento della lista", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void inviaParametri(Map<String, Object> parametriMap) {
        System.out.println("Parametri inviati:");
        for (Map.Entry<String, Object> entry : parametriMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // TableCellEditor personalizzato per la colonna "Score"
    private class ScoreCellEditor extends DefaultCellEditor {
        private JTextField textField;
        private String originalValue;
        private boolean editingStarted; // Flag per indicare se l'editing è iniziato

        public ScoreCellEditor(JTextField textField) {
            super(textField);
            this.textField = textField;

            // Inizializza il flag a false perché l'editing non è ancora iniziato
            editingStarted = false;

            // Aggiungi un listener per controllare l'input durante l'editing
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (editingStarted) {
                        validateInput();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (editingStarted) {
                        validateInput();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (editingStarted) {
                        validateInput();
                    }
                }

                private void validateInput() {
                    String scoreStr = textField.getText().trim();
                    if (!isValidScore(scoreStr)) {
                        // Mostra un messaggio di errore o esegui azioni appropriate
                        JOptionPane.showMessageDialog(InserimentoParametriPanel.this,
                                "Inserisci un valore numerico compreso tra 1 e 5 per Score",
                                "Input non valido", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Aggiungi un listener per il focus per monitorare quando l'utente inizia a editare
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    editingStarted = true;
                }

                @Override
                public void focusLost(FocusEvent e) {
                    editingStarted = false;
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            // Imposta il valore originale per il confronto successivo
            originalValue = (value == null) ? "" : value.toString();
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public boolean stopCellEditing() {
            // Esegui ulteriori controlli se necessario prima di confermare l'editing
            String scoreStr = textField.getText().trim();
            if (!isValidScore(scoreStr)) {
                // Se il valore non è valido, ripristina il valore originale
                textField.setText(originalValue);
                return false; // Annulla l'editing senza confermare il cambiamento
            }
            return super.stopCellEditing(); // Prosegui con la conferma dell'editing
        }

        // Metodo per validare il valore di Score
        private boolean isValidScore(String scoreStr) {
            try {
                int score = Integer.parseInt(scoreStr); // Converti la stringa in int
                return score >= 1 && score <= 5; // Restituisce true se il valore è nel range 1-5
            } catch (NumberFormatException ex) {
                return false; // Restituisce false se la conversione fallisce (non è un numero)
            }
        }
    }



}
