package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class LoginPanel extends JPanel {
    private JTextArea messageArea; // JTextArea per i messaggi
    private JTextField txtField1;
    private JTextField txtField2;

    public LoginPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout()); // Layout principale come BorderLayout

        // JPanel per contenere il form di login
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new GridLayout(3, 2, 10, 10)); // GridLayout per il form di login

        // Campi di testo e pulsanti per il form di login
        txtField1 = new JTextField(15);
        txtField2 = new JTextField(15);
        JButton btnSalva = new JButton("Accedi");
        JButton btnIndietro = new JButton("Indietro");

        // Aggiungi i componenti al pannello del form di login
        loginFormPanel.add(new JLabel("Id:"));
        loginFormPanel.add(txtField1);
        loginFormPanel.add(new JLabel("Password:"));
        loginFormPanel.add(txtField2);
        loginFormPanel.add(btnSalva);
        loginFormPanel.add(btnIndietro);

        // Aggiungi il pannello del form di login al BorderLayout al centro
        add(loginFormPanel, BorderLayout.CENTER);

        // Crea JTextArea per i messaggi
        messageArea = new JTextArea(10, 30);
        messageArea.setEditable(false); // Rendi JTextArea non editabile
        JScrollPane scrollPane = new JScrollPane(messageArea); // Aggiungi barre di scorrimento

        // Aggiungi JTextArea con i messaggi al BorderLayout a sud
        add(scrollPane, BorderLayout.SOUTH);


        // Azione per il pulsante di accedi
        btnSalva.addActionListener(e -> {

            String id = txtField1.getText();
            String password = txtField2.getText();
            try {
                if (server.login(id, password)) {
                    JOptionPane.showMessageDialog(this, "Accesso Effettuato");
                    resetFields();
                    cardLayout.show(mainPanel, "InseritoreParametri");

                } else {
                    showMessage("Credenziali errate");
                }

            } catch (RemoteException ex) {
                showMessage("Errore durante il login: " + ex.getMessage());
            }
        });

        // Azione per il pulsante Indietro
        btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }

    // Metodo per mostrare un messaggio nella JTextArea
    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n"); // Aggiungi il messaggio al JTextArea
            messageArea.setCaretPosition(messageArea.getDocument().getLength()); // Scorrimento automatico alla fine del testo
        });
    }
    private void resetFields() {
        txtField1.setText("");
        txtField2.setText("");
    }
}
