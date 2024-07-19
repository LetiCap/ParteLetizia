package ClimateMonitoring.GUI;
/*
Tahir Agalliu 753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto 752504 VA
*/

import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;

/**
 * Il pannello di visualizzazione principale per la sezione di ricerca.
 * Questo pannello consente all'utente di selezionare il tipo di ricerca da effettuare
 * e navigare verso i pannelli di ricerca specifici.
 *
 * <p>Il pannello include pulsanti per la ricerca basata su <strong>nome città</strong>, <strong>coordinate</strong>
 * o <strong>stato</strong>. Inoltre, fornisce un pulsante "Back" per tornare alla schermata principale e gestisce la
 * navigazione tra i pannelli utilizzando un {@link CardLayout}.</p>
 *
 * @author Tahir Agalliu
 */
public class VisualizzazionePanel extends JPanel {

    /**
     * Costruttore della classe {@code VisualizzazionePanel}.
     * Crea un pannello di visualizzazione con opzioni per la ricerca basata su <strong>nome città</strong>,
     * <strong>coordinate</strong> o <strong>stato</strong>.
     *
     * @param server       L'<strong>interfaccia</strong> con il server per la comunicazione dei dati.
     * @param cardLayout   Il <strong>layout delle schede</strong> utilizzato per la navigazione tra i pannelli.
     * @param mainPanel    Il <strong>pannello principale</strong> che contiene tutti i pannelli delle schede.
     * @author Tahir Agalliu
     */
    public VisualizzazionePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        // Creazione dell'oggetto per generare componenti dell'interfaccia
        InterfaceCreatorComponent creator = new InterfaceCreatorComponent();

        // Imposta il layout del pannello principale e i bordi
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Aggiunge un'etichetta di benvenuto nella parte superiore del pannello
        JLabel welcomeLabel = creator.creatorTileWindow("Benvenuto nella sezione di ricerca, scegliere un tipo di ricerca:");
        add(welcomeLabel, BorderLayout.NORTH);

        // Crea un pannello per i pulsanti di ricerca
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crea i pulsanti di ricerca e li aggiunge al pannello dei pulsanti
        JButton btnRicercaNomeCitta = creator.createButton(false, "Ricerca tramite nome citta");
        JButton btnRicercaCoordinate = creator.createButton(false, "Ricerca tramite coordinate");
        JButton btnRicercaStato = creator.createButton(false, "Ricerca tramite stato di appartenenza");
        buttonPanel.add(btnRicercaNomeCitta);
        buttonPanel.add(btnRicercaCoordinate);
        buttonPanel.add(btnRicercaStato);

        // Crea un pannello interno per centrare il pannello dei pulsanti
        JPanel internalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        internalPanel.add(buttonPanel);
        add(internalPanel, BorderLayout.CENTER);

        // Crea un pulsante "Back" e aggiungilo al pannello inferiore
        JButton backButton = creator.createButton(true, "Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Aggiunge i pannelli per le diverse opzioni di ricerca al pannello principale
        VisualizzaTramiteNomePanel risultatiTramiteNomePanel = new VisualizzaTramiteNomePanel(server, cardLayout, mainPanel, false);
        mainPanel.add(risultatiTramiteNomePanel, "VisualizzaTramiteNomePanel");

        VisualizzaTramiteCoordinatePanel risultatiTramiteCoordinatePanel = new VisualizzaTramiteCoordinatePanel(server, cardLayout, mainPanel ,false);
        mainPanel.add(risultatiTramiteCoordinatePanel, "VisualizzaTramiteCoordinatePanel");

        VisualizzaTramiteStatoPanel risultatiTramiteStatoPanel = new VisualizzaTramiteStatoPanel(server, cardLayout, mainPanel);
        mainPanel.add(risultatiTramiteStatoPanel, "VisualizzaTramiteStatoPanel");

        // Aggiunge i listener per i pulsanti che cambiano il pannello visualizzato
        btnRicercaNomeCitta.addActionListener(e -> cardLayout.show(mainPanel, "VisualizzaTramiteNomePanel"));
        btnRicercaCoordinate.addActionListener(e -> cardLayout.show(mainPanel, "VisualizzaTramiteCoordinatePanel"));
        btnRicercaStato.addActionListener(e -> cardLayout.show(mainPanel, "VisualizzaTramiteStatoPanel"));

        // Aggiunge il listener per il pulsante "Back" che ritorna alla schermata principale
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
    }
}
