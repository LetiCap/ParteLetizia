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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Pannello Swing per visualizzare i risultati della ricerca tramite stato.
 *
 * <p>Questo pannello consente all'utente di cercare aree tramite il loro stato e visualizza i risultati in una lista.
 * Include un campo di testo per l'inserimento dello stato, un pulsante per avviare la ricerca e un'etichetta
 * che mostra il conteggio dei risultati trovati. Inoltre, gestisce la selezione di un risultato per visualizzare
 * i dettagli e la navigazione tra i pannelli.</p>
 *
 * @author Tahir Agalliu
 */
public class VisualizzaTramiteStatoPanel extends JPanel {

    private final JTextField searchField; // Campo di testo per inserire lo stato da ricercare
    private final JLabel resultCountLabel; // Etichetta per mostrare il conteggio dei risultati ottenuti
    private final AdapterResults adapter; // Adapter per la visualizzazione dei risultati

    /**
     * Costruttore per creare il pannello di visualizzazione tramite stato.
     *
     * @param server      L'interfaccia del server per effettuare la ricerca.
     * @param cardLayout  Il layout manager del pannello principale.
     * @param mainPanel   Il pannello principale dell'applicazione.
     * @author Tahir Agalliu
     */
    public VisualizzaTramiteStatoPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        InterfaceCreatorComponent creator = new InterfaceCreatorComponent(); // Componenti generali per l'interfaccia utente

        // Configura il layout e il bordo del pannello
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Aggiunge il titolo della finestra
        add(creator.creatorTileWindow("Ricerca area tramite stato"), BorderLayout.NORTH);

        // Pannello per il campo di ricerca e il pulsante di ricerca
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = creator.createNormaleField(20); // Campo di testo per l'inserimento dello stato
        JButton searchButton = creator.createButton(false, "Search"); // Pulsante per avviare la ricerca
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Lista per visualizzare i risultati e pannello di scorrimento
        JList<ResultWrapper> resultList = creator.createResultList();
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        // Pannello centrale per contenere il pannello di ricerca e la lista dei risultati
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Pulsante per tornare alla schermata precedente
        JButton backButtonBottom = creator.createButton(true, "Back");
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);

        // Etichetta per mostrare il numero di risultati
        resultCountLabel = creator.creatorTileWindow("");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        statusPanel.add(resultCountLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Inizializza l'adapter per aggiornare e visualizzare i risultati
        this.adapter = new AdapterResults(server, cardLayout, mainPanel, resultList);

        // Configura l'azione del pulsante di ricerca
        searchButton.addActionListener(e -> performSearch(server)); // Utilizza una lambda expression per gestire l'azione del pulsante

        // Configura la selezione della lista dei risultati
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    // Gestisce il doppio clic per aprire i dettagli del risultato selezionato
                    ResultWrapper selectedResult = resultList.getSelectedValue();
                    try {
                        adapter.openDetailsPanel((JFrame) SwingUtilities.getWindowAncestor(resultList), selectedResult);
                        adapter.reset(searchField, resultCountLabel); // Resetta i campi dopo aver aperto i dettagli
                    } catch (RemoteException ex) {
                        showErrorDialog("Errore nella comunicazione con il server: " + ex.getMessage());
                    }
                }
            }
        });

        // Configura l'azione del pulsante di ritorno
        backButtonBottom.addActionListener(e -> {
            // Torna alla schermata di visualizzazione principale e resetta l'adapter
            cardLayout.show(mainPanel, "Visualizzazione");
            adapter.reset(searchField, resultCountLabel);
        });
    }

    /**
     * Effettua una ricerca di risultati tramite stato utilizzando l'interfaccia del server.
     * Vengono effettuati controlli su ricerca e gestione delle principali eccezioni.
     * Ogni valore di ricerca deve essere almeno di due caratteri e possibilmente con lettera iniziale maiuscola.
     *
     * @param server L'interfaccia del server per effettuare la ricerca.
     * @author Tahir Agalliu
     */
    private void performSearch(ServerInterface server) {
        try {
            String searchTerm = searchField.getText().trim(); // Rimuove spazi inutili all'inizio e alla fine

            // Assicura che ogni parola nella stringa inizi con una lettera maiuscola
            if (!searchTerm.isEmpty()) {
                searchTerm = formatSearchTerm(searchTerm);
            }

            if (searchTerm.length() <= 1) { // Verifica che il termine di ricerca sia valido
                showErrorDialog("Inserisci un nome di stato valido.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LinkedList<Result> results;
            try {
                results = server.ricercaTramiteStato(searchTerm); // Esegue la ricerca tramite stato
            } catch (RemoteException ex) {
                showErrorDialog("Errore nella comunicazione con il server: " + ex.getMessage());
                return;
            } catch (IllegalArgumentException ex) {
                showErrorDialog("Argomento di ricerca non valido: " + ex.getMessage());
                return;
            }

            if (results != null && !results.isEmpty()) { // Controlla e mostra il numero di risultati trovati
                resultCountLabel.setText(String.format("La ricerca ha prodotto %d %s", results.size(), results.size() == 1 ? "risultato" : "risultati"));
            } else {
                resultCountLabel.setText("La ricerca non ha prodotto risultati.");
            }
            adapter.updateResults(results); // Aggiunge i risultati all'adapter
        } catch (NullPointerException | HeadlessException ex) {
            showErrorDialog("Errore interno: un oggetto necessario è nullo.");
        } catch (Exception ex) {
            showErrorDialog("Si è verificato un errore inaspettato: " + ex.getMessage());
        }
    }

    /**
     * Mostra un dialogo di errore con il messaggio specificato.
     *
     * @param message Il messaggio di errore da visualizzare.
     * @author Tahir Agalliu
     */
    private void showErrorDialog(String message) {
        showErrorDialog(message, JOptionPane.ERROR_MESSAGE); // Mostra un dialogo di errore con tipo di messaggio predefinito
    }

    /**
     * Mostra un dialogo di errore con il messaggio e il tipo di messaggio specificati.
     *
     * @param message     Il messaggio di errore da visualizzare.
     * @param messageType Il tipo di messaggio (ad esempio, JOptionPane.ERROR_MESSAGE).
     * @author Tahir Agalliu
     */
    private void showErrorDialog(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, "Errore", messageType); // Mostra un dialogo di errore con tipo di messaggio specificato
    }

    /**
     * Formatta la stringa di ricerca con la prima lettera di ogni parola in maiuscolo e il resto in minuscolo.
     *
     * @param searchTerm La stringa di ricerca da formattare.
     * @return La stringa formattata.
     * @author Tahir Agalliu
     */
    private String formatSearchTerm(String searchTerm) {
        return Arrays.stream(searchTerm.split(" ")) // Suddivide la stringa in parole
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase()) // Capitalizza la prima lettera di ogni parola e rende il resto minuscolo
                .collect(Collectors.joining(" ")); // Riunisce le parole formattate in una singola stringa
    }
}
