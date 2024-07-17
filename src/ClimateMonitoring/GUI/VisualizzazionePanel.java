package ClimateMonitoring.GUI;

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;

public class VisualizzazionePanel extends JPanel {


    public VisualizzazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        InterfaceCreatorComponent creator=new InterfaceCreatorComponent();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel welcomeLabel=creator.creatorTileWindow("Benvenuto nella sezione di ricerca, scegliere un tipo di ricerca:");
        add(welcomeLabel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton btnRicercaNomeCitta=creator.createButton(false,"Ricerca tramite nome citta");
        JButton btnRicercaCoordinate = creator.createButton(false,"Ricerca tramite coordinate");
        JButton btnRicercaStato = creator.createButton(false,"Ricerca tramite stato di appartenenza");
        buttonPanel.add(btnRicercaNomeCitta);
        buttonPanel.add(btnRicercaCoordinate);
        buttonPanel.add(btnRicercaStato);

        JPanel internalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        internalPanel.add(buttonPanel);

        add(internalPanel, BorderLayout.CENTER);

        JButton backButton = creator.createButton(true,"Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Azioni per i pulsanti
        VisualizzaRisultatiTramiteNomePanel risultatiTramiteNomePanel = new VisualizzaRisultatiTramiteNomePanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteNomePanel, "VisualizzaRisultatiTramiteNomePanel");
        btnRicercaNomeCitta.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaRisultatiTramiteNomePanel");
        });
        VisualizzaTramiteCoordinatePanel risultatiTramiteCoordinatePanel = new VisualizzaTramiteCoordinatePanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteCoordinatePanel, "VisualizzaTramiteCoordinatePanel");
        btnRicercaCoordinate.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaTramiteCoordinatePanel");
        });

        VisualizzaTramiteStatoPanel risultatiTramiteStatoPanel = new VisualizzaTramiteStatoPanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteStatoPanel, "VisualizzaTramiteStatoPanel");
        btnRicercaStato.addActionListener(e -> {
            cardLayout.show(mainPanel, "VisualizzaTramiteStatoPanel");
        });

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Home");
        });
    }
}