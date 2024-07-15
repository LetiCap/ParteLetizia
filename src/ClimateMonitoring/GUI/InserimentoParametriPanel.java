package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InserimentoParametriPanel extends JPanel {
    private ServerInterface server;
    private JTextArea textArea;
    private JList<String> resultList;
    private String areaScelta;
    private JTable climateTable;
    private Map<String, Object> para = new HashMap<>(); // Mappa per memorizzare i valori modificati nella tabella

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
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permette una sola selezione
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
                return column == 2 || column == 3; // Rendi modificabili solo le colonne "Score" e "Notes"
            }
        };

        climateTable = new JTable(tableModel);

        // Disabilita il ridimensionamento delle colonne
        climateTable.getTableHeader().setResizingAllowed(false);
        // Disabilita la selezione delle colonne
        climateTable.setColumnSelectionAllowed(false);
        climateTable.getTableHeader().setReorderingAllowed(false);

        // Imposta un editor personalizzato per le colonne "Score" e "Notes"
        climateTable.getColumnModel().getColumn(2).setCellEditor(new JTextFieldEditor());
        climateTable.getColumnModel().getColumn(3).setCellEditor(new JTextFieldEditor());
        // Imposta un renderer personalizzato per le colonne "Score" e "Notes"
        climateTable.getColumnModel().getColumn(2).setCellRenderer(new JTextFieldRenderer());
        climateTable.getColumnModel().getColumn(3).setCellRenderer(new JTextFieldRenderer());
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
            if (!e.getValueIsAdjusting()) { // Assicura che l'evento sia completato
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
                // Imposta le larghezze preferite delle colonne dopo che il pannello è stato visualizzato
                climateTable.getColumnModel().getColumn(0).setPreferredWidth(10); // Climate Category
                climateTable.getColumnModel().getColumn(1).setPreferredWidth(10); // Explanation
                climateTable.getColumnModel().getColumn(2).setPreferredWidth(10); // Score
                climateTable.getColumnModel().getColumn(3).setPreferredWidth(10); // Notes
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
                resultList.setModel(listModel); // Imposta il modello sulla JList
                resultList.revalidate();
                resultList.repaint();

                // Aggiorna la JTextArea con il testo desiderato
                StringBuilder sb = new StringBuilder();
                for (String elemento : listaElementi) {
                    sb.append(elemento).append("\n"); // Aggiungi ogni elemento alla JTextArea
                }
                textArea.setText(sb.toString());
                textArea.revalidate();
                textArea.repaint();

                // Verifica il contenuto del modello
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
        // Implementa l'azione desiderata con la mappa dei parametri
        System.out.println("Parametri inviati:");
        for (Map.Entry<String, Object> entry : parametriMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // Editor personalizzato per supportare l'editing di JTextField nelle colonne "Score" e "Notes"
    public class JTextFieldEditor extends DefaultCellEditor {
        private JTextField textField;
        private int maxCharacters = 250; // Limite massimo di caratteri per la colonna "Notes"

        public JTextFieldEditor() {
            super(new JTextField());
            textField = (JTextField) getComponent();
            textField.setBorder(null);
            textField.setOpaque(true); // Assicura che il background sia visibile

            // Aggiungi un listener per catturare i cambiamenti nel testo
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateData();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateData();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateData();
                }

                private void updateData() {
                    SwingUtilities.invokeLater(() -> {
                        String input = textField.getText().trim();
                        if (textField.getName().equals("score")) { // Verifica se l'editor è per "Score"
                            if (!isValidScore(input)) {
                                // Mostra un messaggio di errore
                                JOptionPane.showMessageDialog(climateTable, "Inserire un numero tra 1 e 5", "Errore di input", JOptionPane.ERROR_MESSAGE);
                                // Annulla l'editing e rimani nella cella
                                cancelCellEditing();
                            } else {
                                // Notifica il modello della tabella che l'editing è stato completato
                                fireEditingStopped();
                            }
                        } else if (textField.getName().equals("notes")) { // Verifica se l'editor è per "Notes"
                            if (input.length() > maxCharacters) {
                                // Limita il testo inserito al massimo di 250 caratteri
                                textField.setText(input.substring(0, maxCharacters));
                                // Mostra un messaggio di avviso se il limite viene superato
                                JOptionPane.showMessageDialog(climateTable, "Massimo " + maxCharacters + " caratteri per la nota", "Limite superato", JOptionPane.WARNING_MESSAGE);
                            }
                            // Notifica il modello della tabella che l'editing è stato completato
                            fireEditingStopped();
                        }

                        // Aggiorna il tooltip per mostrare il testo completo
                        updateTooltip();
                    });
                }

                private boolean isValidScore(String input) {
                    try {
                        int value = Integer.parseInt(input);
                        return value >= 1 && value <= 5;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }


            });

            // Aggiungi un listener KeyListener per aggiornare il testo mentre si scrive
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        // Aggiorna la visualizzazione del testo mentre si sta scrivendo
                        updateTooltip();
                    });
                }
            });

        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            textField.setText(value != null ? value.toString() : "");
            // Imposta un nome per identificare se l'editor è per "Score" o "Notes"
            textField.setName(column == 2 ? "score" : "notes");
            // Imposta il tooltip con il testo completo attualmente inserito
            textField.setToolTipText(textField.getText().trim());
            return textField;
        }
        // Metodo per aggiornare il tooltip con il testo completo
        private void updateTooltip() {
            SwingUtilities.invokeLater(() -> {
                String input = textField.getText().trim();
                textField.setToolTipText(input); // Imposta il tooltip con il testo completo attualmente inserito
                climateTable.repaint(); // Ridisegna la tabella per aggiornare il tooltip
            });
        }
    }


    // Renderer personalizzato per visualizzare JTextField nelle colonne "Score" e "Notes"
    private static class JTextFieldRenderer extends JTextField implements TableCellRenderer {
        public JTextFieldRenderer() {
            setBorder(null);
            setOpaque(true); // Assicura che il background sia visibile
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");

            // Imposta un tool tip con il testo completo quando il mouse passa sopra la cella
            setToolTipText(getText());

            return this;
        }
    }
}
