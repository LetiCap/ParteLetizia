package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InserimentoParametriPanel extends JPanel {
    private ServerInterface server;
    private JList<String> resultList;
    private String areaScelta;
    private JTable climateTable;

    private static String[] NomiColonneParametriPAR = {
            "vento_val",
            "umidita_val",
            "precipitazioni_val",
            "pressione_val",
            "temperatura_val",
            "altitudineghiacci_val",
            "massaghiacci_val"
    };
    private static String[] NomiColonneParametriNOT = {
            "vento_notes",
            "umidita_notes",
            "precipitazioni_notes",
            "pressione_notes",
            "temperatura_notes",
            "altitudineghiacci_notes",
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

        sceltaAreaDiLavoro.add(titleLabel, BorderLayout.NORTH);
        sceltaAreaDiLavoro.add(resultScrollPane, BorderLayout.CENTER);

        add(sceltaAreaDiLavoro, BorderLayout.NORTH);

        // Tabella per i parametri climatici
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 2) {
                    return true; // La colonna "Score" è sempre modificabile
                } else if (column == 3) {
                    String score = (String) getValueAt(row, 2);
                    if (!isValidScore(score)) {
                        // Mostra un messaggio di avviso se lo score non è valido
                        JOptionPane.showMessageDialog(InserimentoParametriPanel.this,
                                "Inserisci uno Score valido (1-5) per poter inserire una nota",
                                "Score non valido", JOptionPane.WARNING_MESSAGE);
                        return false; // La cella delle "Notes" non è modificabile se lo score non è valido
                    }
                    return true; // La cella delle "Notes" è modificabile solo se lo score è valido
                }
                return false; // Altre colonne non sono modificabili
            }
        };

        climateTable = new JTable(tableModel);

        // Disabilita il ridimensionamento delle colonne
        climateTable.getTableHeader().setResizingAllowed(false);
        climateTable.setColumnSelectionAllowed(false);
        climateTable.getTableHeader().setReorderingAllowed(false);

        // Applica il TableCellEditor alla colonna "Score"
        climateTable.getColumnModel().getColumn(2).setCellEditor(new ScoreCellEditor(new JTextField()));





        // Aggiungi il listener per avviare l'editor di popup per le Notes
        climateTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked on table. Click count: " + e.getClickCount());
                int row = climateTable.rowAtPoint(e.getPoint());
                int column = climateTable.columnAtPoint(e.getPoint());
                System.out.println("Clicked row: " + row + ", column: " + column);


                    if (row >= 0 && column == 3) {

                        editNotes(row); // Avvia l'editor di popup per le Notes
                    }

            }
        });






        // Aggiungi la tabella al pannello centrale
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

            if (areaScelta == null) {
                JOptionPane.showMessageDialog(this, "Scegliere un'area");
                return; // Esci dal metodo in caso di areaScelta null
            }

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

    private void editNotes(int row) {
        System.out.println("Editing Notes for row: " + row); // Log di debug

        String currentNotes = (String) climateTable.getValueAt(row, 3);

        JTextArea textArea = new JTextArea(currentNotes);
        textArea.setRows(4);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String newNotes = textArea.getText();
            climateTable.setValueAt(newNotes, row, 3);
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(okButton);
            dialog.dispose(); // Chiudi il dialogo dopo aver applicato le modifiche
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Notes", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);


        // Quando si chiude il dialog, controlla se è stato premuto OK
        if (!dialog.isVisible()) {
            String newNotes = textArea.getText();
            climateTable.setValueAt(newNotes, row, 3);
        }

    }

    private void loadAndDisplayData() {
        SwingUtilities.invokeLater(() -> {
            try {
                LinkedList<String> listaElementi = server.mostraElementiDisponibili("aree", "lonlat", false);
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (String elemento : listaElementi) {
                    listModel.addElement(elemento);
                }
                resultList.setModel(listModel);
                resultList.revalidate();
                resultList.repaint();

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

    private boolean isValidScore(String scoreStr) {
        try {
            int score = Integer.parseInt(scoreStr); // Converti la stringa in int
            return score >= 1 && score <= 5; // Restituisce true se il valore è nel range 1-5
        } catch (NumberFormatException ex) {
            return false; // Restituisce false se la conversione fallisce (non è un numero)
        }
    }





    private class ScoreCellEditor extends DefaultCellEditor {
        private JTextField textField;
        private String originalValue;

        public ScoreCellEditor(JTextField textField) {
            super(textField);
            this.textField = textField;

            // Aggiungi un listener per il focus per monitorare quando l'utente inizia a editare
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    validateAndStopEditing();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Imposta il valore originale per il confronto successivo
            originalValue = (value == null) ? "" : value.toString();
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public boolean stopCellEditing() {
            validateAndStopEditing();
            return super.stopCellEditing(); // Prosegui con la conferma dell'editing
        }

        // Metodo per validare il valore di Score e terminare l'editing se valido
        private void validateAndStopEditing() {
            String scoreStr = textField.getText().trim();
            if (isValidScore(scoreStr)) {
                // Aggiorna il modello della tabella solo se stai editando una cella valida
                int editingRow = climateTable.getEditingRow();
                int editingColumn = climateTable.getEditingColumn();
                if (editingRow != -1 && editingColumn != -1) {
                    climateTable.getModel().setValueAt(scoreStr, editingRow, editingColumn);
                }
            } else {
                // Mostra il messaggio di errore
                JOptionPane.showMessageDialog(InserimentoParametriPanel.this,
                        "Inserisci un valore numerico compreso tra 1 e 5 per Score",
                        "Input non valido", JOptionPane.ERROR_MESSAGE);

                // Ripristina il valore originale nella cella
                textField.setText(originalValue);
                textField.requestFocusInWindow(); // Rimetti il focus nella cella per permettere la modifica

            }
        }
    }
}
