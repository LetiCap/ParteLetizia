package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class RegistrazioneNuovoCentroPanel extends JPanel implements BackButtonListener {

    private JTextField civico;
    private JTextField CAP;
    private JTextField Comune;
    private JTextField ViaPiazza;
    private JTextField Provincia;
    private LinkedList<String> inserimenti = new LinkedList<>();
    private InterfaceCreatorComponent creator= new InterfaceCreatorComponent();
    private LinkedList<String> lonlatInserite = new LinkedList<>();
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ServerInterface server;

    public RegistrazioneNuovoCentroPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10)); // Utilizzo di spaziatura tra le celle
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiungo margine esterno


        civico = creator.createNormaleField(15);
        CAP = creator.createNormaleField(15);
        Comune = creator.createNormaleField(15);
        ViaPiazza = creator.createNormaleField(15);
        Provincia = creator.createNormaleField(15);

        inputPanel.add(new JLabel("Numero civico:"));
        inputPanel.add(civico);
        inputPanel.add(new JLabel("CAP:"));
        inputPanel.add(CAP);
        inputPanel.add(new JLabel("Comune:"));
        inputPanel.add(Comune);
        inputPanel.add(new JLabel("Via/piazza:"));
        inputPanel.add(ViaPiazza);
        inputPanel.add(new JLabel("Provincia:"));
        inputPanel.add(Provincia);

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        JButton apriPanel1Button =creator.createButton(false,"Ricerca tramite città");
        JButton apriPanel2Button =creator.createButton(false,"Ricerca tramite longitudine latitudine");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnIndietro = creator.createButton(true, "Back");
        JButton mainButton = creator.createButton(false,"Invio");
        buttonPanel.add(btnIndietro);
        buttonPanel.add(mainButton);
        buttonPanel.add(apriPanel1Button);
        buttonPanel.add(apriPanel2Button);

        btnIndietro.addActionListener(e -> cardLayout.show(mainPanel, "SceltaCentro"));

        mainButton.addActionListener(e -> {
            try {
                inserire();
                server.registraCentroAree(inserimenti, lonlatInserite);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        apriPanel1Button.addActionListener(e -> cardLayout.show(mainPanel, "AggiuntaCittàCentro"));
        apriPanel2Button.addActionListener(e -> cardLayout.show(mainPanel, "AggiuntaCoordinateCentro"));

        return buttonPanel;
    }

    private void inserire() throws RemoteException {
        String civicoText = civico.getText();
        String CAPText = CAP.getText();
        String comuneText = Comune.getText();
        String viaPiazzaText = ViaPiazza.getText();
        String provinciaText = Provincia.getText();

        if (civicoText.isEmpty() || CAPText.isEmpty() || comuneText.isEmpty() || viaPiazzaText.isEmpty() || provinciaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Per favore, inserisci tutti i campi.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            inserimenti.add(civicoText);
            inserimenti.add(CAPText);
            inserimenti.add(comuneText);
            inserimenti.add(viaPiazzaText);
            inserimenti.add(provinciaText);
        }

        // Esempio di output dei dati inseriti
        JOptionPane.showMessageDialog(this, "Registrazione Centro effettuata", "Registrazione Centro", JOptionPane.INFORMATION_MESSAGE);

        // Esempio di output delle coordinate selezionate
        StringBuilder lonlatOutput = new StringBuilder("Coordinate selezionate:\n\n");
        for (String lonlat : lonlatInserite) {
            lonlatOutput.append(lonlat).append("\n");
        }
        JOptionPane.showMessageDialog(this, lonlatOutput.toString(), "Coordinate Selezionate", JOptionPane.INFORMATION_MESSAGE);

        // Reset dei campi dopo la registrazione
        resetFields();
        cardLayout.show(mainPanel, "Home");
    }

    private void resetFields() {
        civico.setText("");
        CAP.setText("");
        Comune.setText("");
        ViaPiazza.setText("");
        Provincia.setText("");
    }

    // Implementazione del metodo dell'interfaccia BackButtonListener
    @Override
    public void onBackButtonClicked(LinkedList<String> lonlatInserite) {
        this.lonlatInserite = lonlatInserite;
    }
}
