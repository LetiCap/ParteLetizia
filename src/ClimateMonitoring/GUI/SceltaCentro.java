package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class SceltaCentro extends JPanel {

    private ServerInterface server;
    private LinkedList<String> listaElementi;

    public SceltaCentro(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;

        setLayout(new BorderLayout());

        // Pannello per la visualizzazione dei centri
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Etichetta per il titolo della sezione
        JLabel titleLabel = new JLabel("Scelta Centro", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(titleLabel, BorderLayout.NORTH);

        // TextArea per mostrare la lista di centri disponibili
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // Impedisce all'utente di modificare il testo
        JScrollPane scrollPane = new JScrollPane(textArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Carica la lista di centri disponibili
        try {
            listaElementi = server.mostraElementiDisponibili("CentriMonitoraggio", null, "NomeCentro", false);
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

        // Pannello per l'inserimento di un nuovo centro
        JPanel insertPanel = new JPanel(new BorderLayout());
        JLabel insertLabel = new JLabel("Inserisci il nome del nuovo centro:");
        JTextField centroField = new JTextField(20);
        JButton btnInserisci = new JButton("Inserisci");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnInserisci);

        insertPanel.add(insertLabel, BorderLayout.WEST);
        insertPanel.add(centroField, BorderLayout.CENTER);
        insertPanel.add(buttonPanel, BorderLayout.EAST);

        // Aggiungo un margine al pannello di inserimento per migliorare la visibilità
        insertPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(insertPanel, BorderLayout.SOUTH);

        // Azione per il pulsante Inserisci
        btnInserisci.addActionListener(e -> {
            String nomeCentro = centroField.getText().trim();

            if (!nomeCentro.isEmpty()) {
                try {
                    server.richiestaInserimentoCentro(nomeCentro, listaElementi);
                    JOptionPane.showMessageDialog(this, "Centro inserito con successo!");
                    // Aggiorna la lista dei centri dopo l'inserimento
                    aggiornaListaCentri(textArea);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Errore durante l'inserimento del centro", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Il nome del centro non può essere vuoto", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Pannello per il pulsante Indietro
        JPanel buttonPanelIndietro = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnIndietro = new JButton("Indietro");
        buttonPanelIndietro.add(btnIndietro);

        add(buttonPanelIndietro, BorderLayout.NORTH);

        // Azione per il pulsante Indietro
        btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "Registrazione"));
    }

    // Metodo per aggiornare la lista dei centri visualizzati
    private void aggiornaListaCentri(JTextArea textArea) {
        try {
            listaElementi = server.mostraElementiDisponibili("CentriMonitoraggio", null, "NomeCentro", false);
            StringBuilder sb = new StringBuilder();
            for (String elemento : listaElementi) {
                sb.append(elemento).append("\n");
            }
            textArea.setText(sb.toString());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nel caricamento della lista", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
