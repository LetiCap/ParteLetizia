package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InserimentoParametriPanel extends JPanel {
    private ServerInterface server;
    private JTextArea textArea;
    private JList<String> resultList;
    private Map<String, JTextField> parametriTextFields;

    private String areaScelta;
    private static String[] OutputSchermoParametriPAR = {
            "valutazione per il Vento",
            "valutazione per l'Umidita",
            "valutazione per le Precipitazioni",
            "valutazione per la Pressione",
            "valutazione per la Temperatura",
            "valutazione per l'altitudine dei ghiacciai",
            "valutazione per la Massa dei Ghiacciai"
    };
    private static String[] OutputSchermoParametriNotes = {
            "note per il Vento",
            "note per l'Umidita",
            "note per le Precipitazioni",
            "note per la Pressione",
            "note per la Temperatura",
            "note per l'altitudine dei ghiacciai",
            "note per la Massa dei Ghiacciai"
    };
    private static String[] NomiColonneParametriPAR = {
            "vento_val" ,
            "umidita_val",
            "precipitazioni_val" ,
            "pressione_val" ,
            "temperatura_val" ,
            "altitudineghiacchi_val" ,
            "massaghiacci_val"
    };
    private static String[] NomiColonneParametriNOT = {
            "vento_notes",
            "umidita_notes",
            "precipitazioni_notes" ,
            "pressione_notes",
            "temperatura_notes",
            "altitudineghiacchi_notes",
            "massaghiacci_notes"
    };
    private LinkedList<String> listaElementi;

    public InserimentoParametriPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;
        this.parametriTextFields = new HashMap<>();

        setLayout(new BorderLayout());

        // Pannello per la visualizzazione dei centri
        JPanel sceltaAreaDiLavoro = new JPanel();
        sceltaAreaDiLavoro.setLayout(new BorderLayout());

        resultList = new JList<>(new DefaultListModel<>());
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permette una sola selezione
        JScrollPane resultScrollPane = new JScrollPane(resultList);
        sceltaAreaDiLavoro.add(resultScrollPane, BorderLayout.CENTER);
        add(sceltaAreaDiLavoro, BorderLayout.CENTER);

        // Etichetta per il titolo della sezione
        JLabel titleLabel = new JLabel("Selezione dell'area di lavoro", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sceltaAreaDiLavoro.add(titleLabel, BorderLayout.NORTH);

        // Area di visualizzazione
        textArea = new JTextArea(30, 10);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        sceltaAreaDiLavoro.add(scrollPane, BorderLayout.SOUTH);

        // Pannello per i campi di input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 2, 10, 10)); // 8 righe, 2 colonne, 10px di spaziatura

        // Aggiungi le 7 coppie di campi di testo per i parametri e le note
        for (int i = 0; i < OutputSchermoParametriPAR.length; i++) {
            String paramName = OutputSchermoParametriPAR[i];
            JLabel paramLabel = new JLabel(paramName + ":");
            JTextField paramField = new JTextField(15);
            parametriTextFields.put(NomiColonneParametriPAR[i], paramField);
            inputPanel.add(paramLabel);
            inputPanel.add(paramField);

            String notesName = OutputSchermoParametriNotes[i];
            JLabel notesLabel = new JLabel(notesName + ":");
            JTextField notesField = new JTextField(15);
            parametriTextFields.put(NomiColonneParametriNOT[i], notesField);
            inputPanel.add(notesLabel);
            inputPanel.add(notesField);
        }

        // Aggiungi il pulsante di invio
        JButton invioButton = new JButton("Invio");
        inputPanel.add(new JLabel()); // Etichetta vuota per allineare il pulsante
        inputPanel.add(invioButton);

        sceltaAreaDiLavoro.add(inputPanel, BorderLayout.SOUTH);




        // Pulsante "Back"
        JButton backButtonBottom = new JButton("Back"); // Pulsante "Back" in basso
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
        // Azione per il pulsante Indietro
        backButtonBottom.addActionListener(e -> cardLayout.show(mainPanel, "Login"));






        // Listener per il pulsante di invio
        invioButton.addActionListener(e -> {
            Map<String, Object> parametriMap = new HashMap<>();
            for (Map.Entry<String, JTextField> entry : parametriTextFields.entrySet()) {
                String value = entry.getValue().getText();
                if (!value.isEmpty()) {
                    parametriMap.put(entry.getKey(), entry.getValue().getText());
                }
            }
            // Esegui l'azione desiderata con la mappa dei parametri
            inviaParametri(parametriMap);

            try {
                server.inserisciParametriClimatici(areaScelta, parametriMap);
                JOptionPane.showMessageDialog(this, "Parametri salvati con successo!");
                resetFields();
                cardLayout.show(mainPanel, "Home");
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

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

    private void resetFields() {
        for (JTextField field : parametriTextFields.values()) {
            field.setText("");
        }

    }
}
