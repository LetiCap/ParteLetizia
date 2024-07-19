package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * Pannello in cui si sceglie se registrare un nuovo centro, o associarsi a uno già registrato
 * @author Letizia Capitanio
 */
public class SceltaCentroPanel extends JPanel {
    private ServerInterface server;
    private LinkedList<String> listaElementi;
    private JTextArea textArea;
    private InterfaceCreatorComponent creator = new InterfaceCreatorComponent();

    /**
     * Imposta il panel per scegliere il centro a cui associarsi o inserendone uno non presente nella lista,
     * partirà un'altro panel per completarne la registrazione
     *
     * @param server     <strong>l'interfaccia del server</strong> da cui ottenere i risultati della ricerca.
     * @param cardLayout <strong>il layout del pannello</strong> che consente di passare tra i pannelli.
     * @param mainPanel  <strong>il pannello principale</strong> in cui visualizzare la scheda.
     */
    public SceltaCentroPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;

        setLayout(new BorderLayout());

        // Pannello per la visualizzazione dei centri
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Etichetta per il titolo della sezione
        JLabel titleLabel = creator.creatorTileWindow("Scelta Centro");
        centerPanel.add(titleLabel, BorderLayout.NORTH);

        // TextArea per mostrare la lista di centri disponibili
        textArea = new JTextArea(5, 15);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Pannello per l'inserimento o la selezione di un centro
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel inputLabel = new JLabel("Inserisci o seleziona il centro:");
        JTextField centroField = creator.createNormaleField(20);
        inputPanel.add(inputLabel);
        inputPanel.add(centroField);
        add(inputPanel, BorderLayout.NORTH);

        // Pannello per i pulsanti Invia e Indietro
        JPanel ButtonPanel = new JPanel(new BorderLayout());

        // Pannello per il pulsante Indietro
        JPanel buttonPanelIndietro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonPanel.add(buttonPanelIndietro, BorderLayout.WEST);

        // Pannello per il pulsante Invia
        JPanel sendButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnInvia = creator.createButton(false, "Invia");
        sendButtonPanel.add(btnInvia);
        ButtonPanel.add(sendButtonPanel, BorderLayout.EAST);
        add(ButtonPanel, BorderLayout.SOUTH);

        // Azione per il pulsante Invia
        btnInvia.addActionListener(e -> {
            String nomeCentro = centroField.getText();
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

        // Aggiungi un ComponentListener per aggiornare la lista quando il pannello diventa visibile
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                aggiornaListaCentri();
            }
        });
    }

    private void aggiornaListaCentri() {
        // Carica la lista di centri disponibili
        try {
            listaElementi = server.mostraElementiDisponibili("CentriMonitoraggio", "NomeCentro", false);
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
