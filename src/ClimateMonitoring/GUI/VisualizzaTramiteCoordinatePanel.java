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
 * Questo pannello consente all'utente di inserire latitudine e longitudine per cercare
 * aree geografiche e visualizzare i risultati in una lista. Gli utenti possono anche
 * tornare al pannello di visualizzazione principale.
 * </p>
 */
public class VisualizzaTramiteCoordinatePanel extends JPanel {

    private final AdapterResults adapter;//adapter per risultati ottenuti
    private final JTextField latitudeField;//campo per inserimento latitudine
    private final JTextField longitudeField;//campo per inserimento longitudine
    private final JLabel resultCountLabel;//counter risultati

    /**
     * Costruisce un pannello per la visualizzazione tramite coordinate geografiche.
     * <p>
     * Inizializza i componenti dell'interfaccia grafica e configura i layout e i listener
     * per gestire l'interazione dell'utente.
     * </p>
     *
     * @param server       l'interfaccia del server da cui ottenere i risultati della ricerca
     * @param cardLayout   il layout del pannello che consente di passare tra i pannelli
     * @param mainPanel    il pannello principale in cui visualizzare la scheda
     */
    public VisualizzaTramiteCoordinatePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        InterfaceCreatorComponent creator = new InterfaceCreatorComponent(); // Istanza per la creazione dei componenti GUI
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
        JList<ResultWrapper> resultList = creator.createResultList();
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        // Crea il pannello centrale e aggiunge il pannello di ricerca e la lista dei risultati
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Crea il pulsante "Back" e aggiunge l'etichetta per il conteggio dei risultati
        JButton backButtonBottom = creator.createButton(true, "Back");
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
        resultList.addMouseListener(createResultListMouseListener());

        // Inizializza l'adapter per i risultati
        this.adapter = new AdapterResults(server, cardLayout, mainPanel, resultList);

        // Aggiunge il listener per il pulsante "Back"
        backButtonBottom.addActionListener(e -> handleBackButtonAction(cardLayout, mainPanel));
    }

    /**
     * Crea un {@link ActionListener} per il pulsante di ricerca.
     * <p>
     * Questo listener esegue la ricerca dell'area geografica basata sulle coordinate
     * inserite dall'utente. Gestisce le eccezioni se i valori non sono numeri validi
     * e aggiorna la lista dei risultati e il conteggio dei risultati.
     * </p>
     *
     * @param server l'interfaccia del server da cui ottenere i risultati della ricerca
     * @return l'ActionListener creato per il pulsante di ricerca
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
            //aggiorniamo la GUI per visualizzazione counter e risultati
            resultCountLabel.setText(String.format("La ricerca ha prodotto %d %s", results.size(), results.size() == 1 ? "risultato" : "risultati"));
            adapter.updateResults(results);
        };
    }

    /**
     * Crea un {@link java.awt.event.MouseListener} per la lista dei risultati.
     * <p>
     * Questo listener gestisce il doppio clic sugli elementi della lista dei risultati
     * per aprire i dettagli dell'elemento selezionato.
     * </p>
     *
     * @return il MouseListener creato per la lista dei risultati
     */
    private java.awt.event.MouseListener createResultListMouseListener() {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    JList<ResultWrapper> list = (JList<ResultWrapper>) evt.getSource();
                    ResultWrapper selectedResult = list.getSelectedValue();
                    try {
                        adapter.openDetailsPanel((JFrame) SwingUtilities.getWindowAncestor(list), selectedResult);//visualizziamo panel per visualizzazione result

                        //doppio reset in quanto abbiamo due campi di inserimento e NON aggiunto metodo con segnatura diversa in quanto questo e unico caso
                        adapter.reset(longitudeField, resultCountLabel);
                        adapter.reset(latitudeField, resultCountLabel);//
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
     * Questo metodo resetta i campi di input e il conteggio dei risultati,
     * e mostra il pannello di visualizzazione principale.
     * </p>
     *
     * @param cardLayout il layout del pannello che consente di passare tra i pannelli
     * @param mainPanel  il pannello principale in cui visualizzare la scheda
     */
    private void handleBackButtonAction(CardLayout cardLayout, JPanel mainPanel) {
        adapter.reset(longitudeField, resultCountLabel);
        adapter.reset(latitudeField, resultCountLabel);
        cardLayout.show(mainPanel, "Visualizzazione");
    }
}
