package ClimateMonitoring.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RegistrazioneFrame extends JFrame {

    public RegistrazioneFrame(ArrayList<String[]> registrazioni) {
        // Imposta il titolo della finestra
        setTitle("Registrazione");
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Crea i campi di testo
        JTextField txtField1 = new JTextField(15);
        JTextField txtField2 = new JTextField(15);
        JTextField txtField3 = new JTextField(15);
        JTextField txtField4 = new JTextField(15);
        JTextField txtField5 = new JTextField(15);
        JTextField txtField6 = new JTextField(15);

        // Crea il pulsante di salvataggio
        JButton btnSalva = new JButton("Salva");

        // Aggiungi i campi di testo e il pulsante alla finestra
        setLayout(new GridLayout(7, 2));
        add(new JLabel("Campo 1:"));
        add(txtField1);
        add(new JLabel("Campo 2:"));
        add(txtField2);
        add(new JLabel("Campo 3:"));
        add(txtField3);
        add(new JLabel("Campo 4:"));
        add(txtField4);
        add(new JLabel("Campo 5:"));
        add(txtField5);
        add(new JLabel("Campo 6:"));
        add(txtField6);
        add(btnSalva);

        // Azione per il pulsante di salvataggio
        btnSalva.addActionListener(e -> {
            String[] dati = new String[6];
            dati[0] = txtField1.getText();
            dati[1] = txtField2.getText();
            dati[2] = txtField3.getText();
            dati[3] = txtField4.getText();
            dati[4] = txtField5.getText();
            dati[5] = txtField6.getText();

            registrazioni.add(dati);
            JOptionPane.showMessageDialog(RegistrazioneFrame.this, "Dati salvati con successo!");
            dispose();  // Chiudi la finestra di registrazione
        });

        // Visualizza la finestra di registrazione
        setVisible(true);
    }
}
