package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private ArrayList<String[]> registrazioni;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel testoBenvenuto;
    private JButton btnRegistrazione;
    private JButton btnLogin;
    private JButton btnVisualizzazione;
    ServerInterface server;
    Font font = new Font("Arial", Font.PLAIN, 20);

    public MainFrame(ServerInterface server) {
        this.server=server;
        registrazioni = new ArrayList<>();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Schermata Home
        JPanel homePanel = new JPanel(new BorderLayout());

        // Testo benvenuto
        testoBenvenuto = new JLabel("Benvenuti nell'applicazione di Climate Monitoring.", JLabel.CENTER);
        testoBenvenuto.setFont(font);
        homePanel.add(testoBenvenuto, BorderLayout.NORTH);

        // Pannello per i pulsanti con layout nullo
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null);

        btnRegistrazione = new JButton("Registrazione");
        btnLogin = new JButton("Login");
        btnVisualizzazione = new JButton("Visualizzazione");

        // Imposta le coordinate e dimensioni dei pulsanti
        btnRegistrazione.setBounds(280, 100, 200, 50);
        btnLogin.setBounds(280, 200, 200, 50);
        btnVisualizzazione.setBounds(280, 300, 200, 50);

        // Aggiungi i pulsanti al pannello
        buttonsPanel.add(btnRegistrazione);
        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnVisualizzazione);

        // Aggiungi il pannello dei pulsanti al centro del homePanel
        homePanel.add(buttonsPanel, BorderLayout.CENTER);

        RegistrazioneNuovoCentroPanel registraCentroPanel = new RegistrazioneNuovoCentroPanel(server, cardLayout, mainPanel);
        SelezionaCittaPanel selezionaCittaPanel=  new SelezionaCittaPanel(server, cardLayout, mainPanel);
        SelezionaCoordinatePanel SelezionaCoordinatePanel =  new SelezionaCoordinatePanel(server, cardLayout, mainPanel);

        selezionaCittaPanel.setBackButtonListener(registraCentroPanel);
        SelezionaCoordinatePanel.setBackButtonListener(registraCentroPanel);

        // Aggiungi pannelli al card layout
        mainPanel.add(homePanel, "Home");
        mainPanel.add(new RegistrazionePanel( server,cardLayout, mainPanel), "Registrazione");
        mainPanel.add(new LoginPanel(server,cardLayout, mainPanel), "Login");
        mainPanel.add(new SceltaCentroPanel(server,cardLayout, mainPanel), "SceltaCentro");
        mainPanel.add(new VisualizzazionePanel(server, cardLayout, mainPanel), "Visualizzazione");
        mainPanel.add(registraCentroPanel, "RegistraCentroNuovo");
        mainPanel.add(new VisualizzaTramiteCoordinatePanel(server, cardLayout, mainPanel), "VisualizzaCoordinate");
        mainPanel.add(new VisualizzaRisultatiTramiteNomePanel(server, cardLayout, mainPanel), "VisualizzaNome");
        mainPanel.add(new VisualizzaTramiteStatoPanel(server, cardLayout, mainPanel), "VisualizzazioneStato");
        mainPanel.add(new InserimentoParametriPanel(server,cardLayout,mainPanel), "InseritoreParametri");
        mainPanel.add(selezionaCittaPanel, "AggiuntaCittàCentro");
        mainPanel.add(SelezionaCoordinatePanel, "AggiuntaCoordinateCentro");



        // Aggiungi azioni ai pulsanti
        btnRegistrazione.addActionListener(e -> cardLayout.show(mainPanel, "Registrazione"));
        btnLogin.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        btnVisualizzazione.addActionListener(e -> cardLayout.show(mainPanel, "Visualizzazione"));

        // Configura il frame principale
        add(mainPanel);
        setTitle("Climate Monitoring");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

   /* public static void main(String[] args) {
        new MainFrame(server);
    }

    */
}
