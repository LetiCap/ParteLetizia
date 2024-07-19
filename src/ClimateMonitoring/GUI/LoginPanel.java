/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/
package ClimateMonitoring.GUI;
import ClimateMonitoring.ServerInterface;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;


/**
 * Pannello di login per l'utente.
 * <p>
 * Questo pannello fornisce un'interfaccia per l'accesso dell'utente al sistema. Consente di inserire un identificativo e una password,
 * e include pulsanti per effettuare il login o tornare alla schermata principale.
 * </p>
 *
 * @author Letizia Capitanio
 */
public class LoginPanel extends JPanel {
    private JTextField txtField1;
    private JTextField txtField2;
    private JButton btnSalva;
    private JButton btnIndietro;
    private InterfaceCreatorComponent creator = new InterfaceCreatorComponent();


    /**
     * Imposta il panel di login con textfield per inserire username e password.
     * @param server     <strong>l'interfaccia del server</strong> da cui ottenere i risultati della ricerca.
     * @param cardLayout <strong>il layout del pannello</strong> che consente di passare tra i pannelli.
     * @param mainPanel  <strong>il pannello principale</strong> in cui visualizzare la scheda.
     */
    public LoginPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout()); // Layout principale come BorderLayout

        // JPanel per il titolo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Layout per il titolo centrato
        JLabel titleLabel = creator.creatorTileWindow("Login");
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH); // Aggiungi il pannello del titolo al BorderLayout al nord

        // JPanel per il form di login
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new GridLayout(3, 2, 20, 20)); // GridLayout per il form di login
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 40, 100)); // Margine esterno

        // Campi di testo e pulsanti per il form di login
        txtField1 = new JTextField(2);
        txtField1.setPreferredSize(new Dimension(10, 3));
        txtField2 = new JTextField(2);
        txtField2.setPreferredSize(new Dimension(10, 3));
        btnSalva = creator.createButton(false, "Log");
        btnSalva.setPreferredSize(new Dimension(10, 30));
        btnIndietro = creator.createButton(true, "Back");
        btnIndietro.setPreferredSize(new Dimension(10, 30));

        // Aggiungi i componenti al pannello del form di login
        loginFormPanel.add(new JLabel("Id:"));
        loginFormPanel.add(txtField1);
        loginFormPanel.add(new JLabel("Password:"));
        loginFormPanel.add(txtField2);
        loginFormPanel.add(btnSalva);
        loginFormPanel.add(btnIndietro);

        // Aggiungi il pannello del form di login al BorderLayout al centro
        add(loginFormPanel, BorderLayout.CENTER);

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
                    JOptionPane.showMessageDialog(this, "Credenziali errate");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Errore durante il login: " + ex.getMessage());
            }
        });

        // Azione per il pulsante Indietro
        btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }

    // Metodo per reimpostare i campi di testo del form di login
    private void resetFields() {
        txtField1.setText("");
        txtField2.setText("");
    }
}
