package ClimateMonitoring.GUI;

/*
Tahir Agalliu 753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto 752504 VA
*/

import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * Pannello per la visualizzazione e ricerca di risultati tramite coordinate geografiche.
 * <p>
 * <strong>Questo pannello consente all'utente di inserire latitudine e longitudine per cercare
 * aree geografiche e visualizzare i risultati in una lista.</strong> Gli utenti possono anche
 * tornare al pannello di visualizzazione principale.
 * </p>
 *
 * @author Tahir Agalliu
 */
public class VisualizzaTramiteCoordinatePanel extends JPanel {


    private LinkedList<String> lonlatInserite = new LinkedList<>(); // Lista per memorizzare le coordinate selezionate
    private final AdapterResults adapter; // Adapter per risultati ottenuti
    private final JTextField latitudeField; // Campo per inserimento latitudine
    private final JTextField longitudeField; // Campo per inserimento longitudine
    private final JLabel resultCountLabel; // Counter risultati
    private JList<ResultWrapper> resultList;
    // Istanza per la creazione dei componenti GUI
    private InterfaceCreatorComponent creator= new InterfaceCreatorComponent();
    private JButton backButtonBottom;
    private BackButtonListener backButtonListener; // Listener per il pulsante "Back"

    /**
     * <strong>Costruisce un pannello </strong>per la visualizzazione tramite coordinate geografiche.
     * <p>
     * <strong>Inizializza i componenti</strong> dell'interfaccia grafica e configura i layout e i listener
     * per gestire l'interazione dell'utente.
     * </p>
     *
     * @param server     <strong>l'interfaccia del server</strong> da cui ottenere i risultati della ricerca.
     * @param cardLayout <strong>il layout del pannello</strong> che consente di passare tra i pannelli.
     * @param mainPanel  <strong>il pannello principale</strong> in cui visualizzare la scheda.
     * @param UtenteRegistrato boolean per indicare se serve per l'area degli utenti registrati oppure no
     * @author Tahir Agalliu
     */
    public VisualizzaTramiteCoordinatePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel, Boolean UtenteRegistrato) {

        creator.setLayoutCustom(this);

        // Aggiunge un titolo al pannello
        add(creator.creatorTileWindow("Ricerca area tramite coordinate"), BorderLayout.NORTH);

        // Crea il pannello di ricerca con GridBagLayout per la modifica dinamica delle posizioni dei componenti
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Aggiunge l'etichetta e il campo di input per la latitudine
        creator.modifyGridBagConstraints(gbc, 0, 0);
        searchPanel.add(new JLabel("Latitudine:"), gbc);
        creator.modifyGridBagConstraints(gbc, 1, 0);
        latitudeField = creator.createNormaleField(10);
        searchPanel.add(latitudeField, gbc);

        // Aggiunge l'etichetta e il campo di input per la longitudine
        creator.modifyGridBagConstraints(gbc, 0, 1);
        searchPanel.add(new JLabel("Longitudine:"), gbc);
        creator.modifyGridBagConstraints(gbc, 1, 1);
        longitudeField = creator.createNormaleField(10);
        searchPanel.add(longitudeField, gbc);

        // Aggiunge il pulsante di ricerca
        creator.modifyGridBagConstraints(gbc, 0, 2);
        gbc.gridwidth = 2;
        JButton searchButton = creator.createButton(false, "Search");
        searchPanel.add(searchButton, gbc);


        // Crea una lista di risultati con uno JScrollPane
        resultList = creator.createResultList();
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        // Crea il pannello centrale e aggiunge il pannello di ricerca e la lista dei risultati
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Crea il pulsante "Back" e aggiunge l'etichetta per il conteggio dei risultati
        backButtonBottom = creator.createButton(true, "Back");
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);
        resultCountLabel = creator.creatorTileWindow("");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        statusPanel.add(resultCountLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Aggiunge i listener per il pulsante di ricerca
        searchButton.addActionListener(createSearchActionListener(server));

        // Aggiunge un listener per il doppio clic sulla lista dei risultati
        resultList.addMouseListener(createResultListMouseListener(UtenteRegistrato));

        // Inizializza l'adapter per i risultati
        this.adapter = new AdapterResults(server, cardLayout, mainPanel, resultList);

        // Aggiunge il listener per il pulsante "Back"
        backButtonBottom.addActionListener(e -> handleBackButtonAction(cardLayout, mainPanel, UtenteRegistrato));
    }

    /**
     * Crea un {@link ActionListener} per il pulsante di ricerca.
     * <p>
     * <strong>Questo listener esegue la ricerca dell'area geografica basata sulle coordinate
     * inserite dall'utente.</strong> Gestisce le eccezioni se i valori non sono numeri validi
     * e aggiorna la lista dei risultati e il conteggio dei risultati.
     * </p>
     *
     * @param server <strong>l'interfaccia del server</strong> da cui ottenere i risultati della ricerca.
     * @return <strong>l'ActionListener creato</strong> per il pulsante di ricerca.
     * @author Tahir Agalliu
     */
    private ActionListener createSearchActionListener(ServerInterface server) {
        return e -> {
            double latitude;
            double longitude;

            // Verifica e converte la latitudine
            try {
                String latitudeText = latitudeField.getText().trim();
                if (latitudeText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La latitudine non può essere vuota.");
                    return;
                }
                latitude = Double.parseDouble(latitudeText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Inserisci una latitudine valida.");
                return;
            }

            // Verifica e converte la longitudine
            try {
                String longitudeText = longitudeField.getText().trim();
                if (longitudeText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La longitudine non può essere vuota.");
                    return;
                }
                longitude = Double.parseDouble(longitudeText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Inserisci una longitudine valida.");
                return;
            }

            // Esegue la ricerca e aggiorna i risultati
            LinkedList<Result> results;
            try {
                results = server.cercaAreaGeograficaCoordinate(latitude, longitude);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            // Aggiorniamo la GUI per visualizzazione counter e risultati
            resultCountLabel.setText(String.format("La ricerca ha prodotto %d %s", results.size(), results.size() == 1 ? "risultato" : "risultati"));
            adapter.updateResults(results);
        };
    }

    /**
     * Crea un {@link java.awt.event.MouseListener} per la lista dei risultati.
     * <p>
     * <strong>Questo listener gestisce il doppio clic sugli elementi</strong> della lista dei risultati
     * per aprire i dettagli dell'elemento selezionato.
     * </p>
     *
     * @return <strong>il MouseListener creato per la lista dei risultati</strong>
     * @author Tahir Agalliu
     */
    private java.awt.event.MouseListener createResultListMouseListener(Boolean UtenteRegistrato) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    JList<ResultWrapper> list = (JList<ResultWrapper>) evt.getSource();
                    ResultWrapper selectedResult = list.getSelectedValue();
                    try {
                        if(UtenteRegistrato){
                            adapter.openDetailsPanel((JFrame) SwingUtilities.getWindowAncestor(list), selectedResult, lonlatInserite);
                        }else{
                            adapter.openDetailsPanel((JFrame) SwingUtilities.getWindowAncestor(list), selectedResult); // Visualizziamo panel per visualizzazione result

                        }


                        // Doppio reset in quanto abbiamo due campi di inserimento e NON aggiunto metodo con segnatura diversa in quanto questo e unico caso
                        adapter.reset(longitudeField, resultCountLabel);
                        adapter.reset(latitudeField, resultCountLabel);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }

    /**
     * Gestisce l'azione del pulsante "Back".
     * <p>
     * <strong>Questo metodo resetta i campi di input e il conteggio dei risultati</strong>,
     * e mostra il pannello di visualizzazione principale.
     * </p>
     *
     * @param cardLayout       <strong>il layout del pannello</strong> che consente di passare tra i pannelli
     * @param mainPanel        <strong>il pannello principale</strong> in cui visualizzare la scheda
     * @param UtenteRegistrato
     * @author Tahir Agalliu
     */
    private void handleBackButtonAction(CardLayout cardLayout, JPanel mainPanel, Boolean UtenteRegistrato) {
        adapter.reset(longitudeField, resultCountLabel);
        adapter.reset(latitudeField, resultCountLabel);
        if(UtenteRegistrato) {

                // Chiamata al listener per passare lonlatInserite al pannello principale
                if (backButtonListener != null) {
                    backButtonListener.onBackButtonClicked(lonlatInserite);
                }
            // Utilizza il cardLayout passato come argomento per tornare indietro
            cardLayout.show(mainPanel, "RegistraCentroNuovo");

        }else{
            cardLayout.show(mainPanel, "Visualizzazione");
        }

    }
    /**
     * Imposta il listener per gestire l'evento di ritorno al pannello di registrazione di un nuovo centro.
     * <p>
     * Questo metodo consente di collegare un listener che sarà notificato quando l'utente
     * seleziona di tornare al pannello di registrazione. Il listener dovrà gestire il passaggio
     * dei dati selezionati, come le coordinate, al pannello di registrazione.
     * </p>
     *
     * @param listener Il listener che gestirà l'evento di ritorno. Deve implementare
     *                 l'interfaccia {@link BackButtonListener} e sarà chiamato quando l'utente
     *                 fa clic sul pulsante "Back".
     */
    public void setBackButtonListener(BackButtonListener listener) {
        this.backButtonListener = listener;
    }
}
