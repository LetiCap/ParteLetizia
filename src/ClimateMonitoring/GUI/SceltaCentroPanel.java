package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class SceltaCentroPanel extends JPanel {

    private ServerInterface server;
    private LinkedList<String> listaElementi;
    private JTextArea textArea;


    public SceltaCentroPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;

        setLayout(new BorderLayout());

        // Pannello per la visualizzazione dei centri
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Etichetta per il titolo della sezione
        JLabel titleLabel = new JLabel("Scelta Centro", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(titleLabel, BorderLayout.NORTH);

        // TextArea per mostrare la lista di centri disponibili
        textArea = new JTextArea(5, 15);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Carica la lista di centri disponibili
        try {
            listaElementi = server.mostraElementiDisponibili("CentriMonitoraggio",  "NomeCentro", false);
            StringBuilder sb = new StringBuilder();
            for (String elemento : listaElementi) {
                sb.append(elemento).append("\n");
            }
            textArea.setText(sb.toString());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nel caricamento della lista", "Errore", JOptionPane.ERROR_MESSAGE);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Pannello per l'inserimento o la selezione di un centro
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel inputLabel = new JLabel("Inserisci o seleziona il centro:");
        JTextField centroField = new JTextField(20);

        inputPanel.add(inputLabel);
        inputPanel.add(centroField);

        add(inputPanel, BorderLayout.NORTH);

        // Pannello per i pulsanti Invia e Indietro
        JPanel bottomButtonPanel = new JPanel(new BorderLayout());

        // Pannello per il pulsante Indietro
        JPanel buttonPanelIndietro = new JPanel(new FlowLayout(FlowLayout.LEFT));
      //  JButton btnIndietro = new JButton("Indietro");
      //  buttonPanelIndietro.add(btnIndietro);

        bottomButtonPanel.add(buttonPanelIndietro, BorderLayout.WEST);

        // Pannello per il pulsante Invia
        JPanel sendButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnInvia = new JButton("Invia");
        sendButtonPanel.add(btnInvia);

        bottomButtonPanel.add(sendButtonPanel, BorderLayout.EAST);

        add(bottomButtonPanel, BorderLayout.SOUTH);

        // Azione per il pulsante Invia
        btnInvia.addActionListener(e -> {
            String nomeCentro = centroField.getText();
            System.out.println(nomeCentro);

            if (!nomeCentro.isEmpty()) {
                try {
                    if (server.richiestaInserimentoCentro(nomeCentro, listaElementi)) {
                        JOptionPane.showMessageDialog(this, "Centro inserito con successo!");
                        centroField.setText("");
                        cardLayout.show(mainPanel, "Home");
                    } else {

                        JOptionPane.showMessageDialog(this, "Registrazione centro in corso...");
                        centroField.setText("");
                        cardLayout.show(mainPanel, "RegistraCentroNuovo");
                    }


                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Errore durante l'inserimento del centro", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Il nome del centro non può essere vuoto", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Azione per il pulsante Indietro
     //   btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "Registrazione"));
    }

}
