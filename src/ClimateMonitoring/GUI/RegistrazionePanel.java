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
import java.util.LinkedList;



/**
 * Pannello per la registrazione dell'utente.
 * <p>
 * Questo pannello consente all'utente di inserire i propri dati di registrazione, inclusi ID, password, nome e cognome,
 * codice fiscale ed email. Include pulsanti per inviare i dati o tornare alla schermata principale.
 * </p>
 *
 * @author Letizia Capitanio
 */
public class RegistrazionePanel extends JPanel {
    private JTextField txtField1;
    private JTextField txtField2;
    private JTextField txtField3;
    private JTextField txtField4;
    private JTextField txtField5;
    private InterfaceCreatorComponent creator=new InterfaceCreatorComponent();

    /**
     * Imposta il panel della registrazione con textfield, bottoni di invio e back.
     * @param server     <strong>l'interfaccia del server</strong> da cui ottenere i risultati della ricerca.
     * @param cardLayout <strong>il layout del pannello</strong> che consente di passare tra i pannelli.
     * @param mainPanel  <strong>il pannello principale</strong> in cui visualizzare la scheda.
     */
    public RegistrazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new GridLayout(8, 2, 10, 10)); // Imposto il layout con margine di 10 pixel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiungo margine esterno

        // Testo sopra la tabella
        JLabel topLabel= creator.creatorTileWindow("Registrazione");
        add(topLabel); // Aggiungo il label al pannello

        // Spazio vuoto per creare una riga di spazio
        add(new JPanel()); // Aggiungo un pannello vuoto per creare una riga di spazio

        // Crea i campi di testo
        txtField1 = creator.createNormaleField(15);
        txtField2 = creator.createNormaleField(15);
        txtField3 = creator.createNormaleField(15);
        txtField4 = creator.createNormaleField(15);
        txtField5 = creator.createNormaleField(15);

        // Crea i pulsanti

        JButton btnSalva = creator.createButton(false,"Invio" );
        JButton btnIndietro = creator.createButton(true,"Back" );

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


            if (id.isEmpty() || password.isEmpty() || nomeCognome.isEmpty() ||  email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Per favore, inserisci tutti i campi.", "Errore", JOptionPane.ERROR_MESSAGE);

            } else {
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
                        resetFields();
                        cardLayout.show(mainPanel, "SceltaCentro");
                    } else {
                        JOptionPane.showMessageDialog(this, "Id già registrato, inserirne un altro", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }

        });

        // Azione per il pulsante Indietro
        btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }


    private void resetFields() {
        txtField1.setText("");
        txtField2.setText("");
        txtField3.setText("");
        txtField4.setText("");
        txtField5.setText("");
    }
}
