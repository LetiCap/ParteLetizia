package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class RegistrazionePanel extends JPanel {

    public RegistrazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new GridLayout(8, 2, 10, 10)); // Imposto il layout con margine di 10 pixel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiungo margine esterno

        // Testo sopra la tabella
        JLabel topLabel = new JLabel("Inserisci i dati utente");
        topLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(topLabel); // Aggiungo il label al pannello

        // Spazio vuoto per creare una riga di spazio
        add(new JPanel()); // Aggiungo un pannello vuoto per creare una riga di spazio

        // Crea i campi di testo
        JTextField txtField1 = new JTextField(15);
        JTextField txtField2 = new JTextField(15);
        JTextField txtField3 = new JTextField(15);
        JTextField txtField4 = new JTextField(15);
        JTextField txtField5 = new JTextField(15);

        // Crea i pulsanti
        JButton btnSalva = new JButton("Inserisci Dati");
        JButton btnIndietro = new JButton("Indietro");

        // Aggiungi i campi di testo e i pulsanti al pannello
        add(new JLabel("Id:"));
        add(txtField1);
        add(new JLabel("Password:"));
        add(txtField2);
        add(new JLabel("Nome cognome:"));
        add(txtField3);
        add(new JLabel("Codice Fiscale:"));
        add(txtField4);
        add(new JLabel("Email:"));
        add(txtField5);
        add(btnSalva);
        add(btnIndietro);

        // Azione per il pulsante di salvataggio
        btnSalva.addActionListener(e -> {
            String id = txtField1.getText();
            String password = txtField2.getText();
            String nomeCognome = txtField3.getText();
            String codiceFiscale = txtField4.getText();
            String email = txtField5.getText();

            if (codiceFiscale.length() != 16) {
                JOptionPane.showMessageDialog(this, "Il Codice Fiscale deve essere di 16 caratteri.", "Errore", JOptionPane.ERROR_MESSAGE);
                return; // Esce dall'azione senza salvare i dati
            }

            LinkedList<String> dati = new LinkedList<>();
            dati.add(password);
            dati.add(nomeCognome);
            dati.add(codiceFiscale);
            dati.add(email);

            try {
                if (server.registrazione(id, dati)) {
                    JOptionPane.showMessageDialog(this, "Dati salvati con successo!");

                    cardLayout.show(mainPanel, "SceltaCentro");
                } else {
                    JOptionPane.showMessageDialog(this, "Id già registrato, inserirne un altro", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            // cardLayout.show(mainPanel, "Home"); // Torna alla schermata principale
        });

        // Azione per il pulsante Indietro
        btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }
}
