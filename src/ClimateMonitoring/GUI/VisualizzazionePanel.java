package ClimateMonitoring.GUI;
import ClimateMonitoring.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VisualizzazionePanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public VisualizzazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        // Testo iniziale
        JLabel welcomeLabel = new JLabel("Benvenuto nella sezione utenti non registrati. È possibile ricercare una città con le seguenti metodologie :");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // Pannello per i pulsanti
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Pulsanti per i vari tipi di ricerca
        JButton btnRicercaNomeCitta = new JButton("Ricerca tramite nome città");
        btnRicercaNomeCitta.setPreferredSize(new Dimension(200, 25));
        JButton btnRicercaCoordinate = new JButton("Ricerca tramite coordinate");
        btnRicercaCoordinate.setPreferredSize(new Dimension(200, 25));
        JButton btnRicercaStato = new JButton("Ricerca tramite stato di appartenenza");
        btnRicercaStato.setPreferredSize(new Dimension(200, 25));

        buttonPanel.add(btnRicercaNomeCitta);
        buttonPanel.add(btnRicercaCoordinate);
        buttonPanel.add(btnRicercaStato);

        add(buttonPanel, BorderLayout.CENTER);

        // Pulsante "Back"
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(80, 25));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Azione per il pulsante "Ricerca tramite nome città"
        VisualizzaRisultatiTramiteNomePanel risultatiTramiteNomePanel = new VisualizzaRisultatiTramiteNomePanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteNomePanel, "VisualizzaRisultatiTramiteNomePanel");
        btnRicercaNomeCitta.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaRisultatiTramiteNomePanel");
            risultatiTramiteNomePanel.reset();
        });

        // Azione per il pulsante "Ricerca tramite coordinate"
        VisualizzaTramiteCoordinatePanel risultatiTramiteCoordinatePanel = new VisualizzaTramiteCoordinatePanel(server, mainPanel,cardLayout);
        mainPanel.add(risultatiTramiteCoordinatePanel, "VisualizzaTramiteCoordinatePanel");
        btnRicercaCoordinate.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaTramiteCoordinatePanel");
            risultatiTramiteCoordinatePanel.reset();
        });

        // Azione per il pulsante "Ricerca tramite stato di appartenenza"
        VisualizzaTramiteStatoPanel risultatiTramiteStatoPanel = new VisualizzaTramiteStatoPanel(server, mainPanel,cardLayout);
        mainPanel.add(risultatiTramiteStatoPanel, "VisualizzaTramiteStatoPanel");
        btnRicercaStato.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaTramiteStatoPanel");
            risultatiTramiteStatoPanel.reset();
        });

        // Azione per il pulsante "Back"
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Home");
        });
    }

    public static void main(String[] args) {
        ServerInterface server;
        try {
            Registry reg = LocateRegistry.getRegistry(1099);
            server = (ServerInterface) reg.lookup("Server");

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            JPanel homePanel = new JPanel(new BorderLayout());
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(null);
            JButton btnVisualizzazione = new JButton("Visualizzazione");
            btnVisualizzazione.setBounds(280, 300, 200, 50);
            buttonsPanel.add(btnVisualizzazione);
            homePanel.add(buttonsPanel, BorderLayout.CENTER);

            mainPanel.add(homePanel, "Home");

            VisualizzazionePanel visualizzatorePanel = new VisualizzazionePanel(server, cardLayout, mainPanel);
            mainPanel.add(visualizzatorePanel, "Visualizzazione");

            btnVisualizzazione.addActionListener(e -> cardLayout.show(mainPanel, "Visualizzazione"));

            JFrame frame = new JFrame("Applicazione");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.getContentPane().add(mainPanel);
            frame.setVisible(true);

            cardLayout.show(mainPanel, "Home");

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.err.println("Client terminato. Server non trovato.");
        }
    }
}
