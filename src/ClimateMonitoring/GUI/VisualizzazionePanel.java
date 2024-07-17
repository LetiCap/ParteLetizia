package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class VisualizzazionePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public VisualizzazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Benvenuto nella sezione utenti non registrati.", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Icone per i pulsanti




        JButton btnRicercaNomeCitta = new JButton("Ricerca tramite nome città");
        customizeButton(btnRicercaNomeCitta);

        JButton btnRicercaCoordinate = new JButton("Ricerca tramite coordinate");
        customizeButton(btnRicercaCoordinate);

        JButton btnRicercaStato = new JButton("Ricerca tramite stato di appartenenza");
        customizeButton(btnRicercaStato);

        buttonPanel.add(btnRicercaNomeCitta);
        buttonPanel.add(btnRicercaCoordinate);
        buttonPanel.add(btnRicercaStato);

        JPanel internalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        internalPanel.add(buttonPanel);

        add(internalPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Serif", Font.BOLD, 18));
        backButton.setBackground(Color.RED); // Impostazione del colore rosso
        backButton.setForeground(Color.WHITE);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Azioni per i pulsanti
        VisualizzaRisultatiTramiteNomePanel risultatiTramiteNomePanel = new VisualizzaRisultatiTramiteNomePanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteNomePanel, "VisualizzaRisultatiTramiteNomePanel");
        btnRicercaNomeCitta.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaRisultatiTramiteNomePanel");
            risultatiTramiteNomePanel.reset();
        });

        VisualizzaTramiteCoordinatePanel risultatiTramiteCoordinatePanel = new VisualizzaTramiteCoordinatePanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteCoordinatePanel, "VisualizzaTramiteCoordinatePanel");
        btnRicercaCoordinate.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaTramiteCoordinatePanel");
            risultatiTramiteCoordinatePanel.reset();
        });

        VisualizzaTramiteStatoPanel risultatiTramiteStatoPanel = new VisualizzaTramiteStatoPanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteStatoPanel, "VisualizzaTramiteStatoPanel");
        btnRicercaStato.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaTramiteStatoPanel");
            risultatiTramiteStatoPanel.reset();
        });

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Home");
        });
    }

    private void customizeButton(JButton button) {
        button.setPreferredSize(new Dimension(300, 40)); // Ridotte le dimensioni del pulsante
        button.setFont(new Font("Serif", Font.BOLD, 16)); // Ridotto il carattere
        button.setBackground(new Color(0x5DADE2));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
    }

    /*
    public static void main(String[] args) {
        ServerInterface server;
        try {
            Registry reg = LocateRegistry.getRegistry(1099);
            server = (ServerInterface) reg.lookup("Server");

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            VisualizzazionePanel visualizzazionePanel = new VisualizzazionePanel(server, cardLayout, mainPanel);

            JFrame frame = new JFrame("Applicazione di Monitoraggio del Clima");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.getContentPane().add(mainPanel);
            frame.setVisible(true);

            cardLayout.show(mainPanel, "Home");

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Client terminato. Server non trovato.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    */
}