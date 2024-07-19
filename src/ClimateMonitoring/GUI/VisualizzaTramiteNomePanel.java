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
 * Pannello Swing per visualizzare i risultati della ricerca tramite nome dell'area.
 *
 * <p>Questo pannello consente all'utente di cercare aree tramite il loro nome e visualizza i risultati in una lista.
 * Include anche un campo di testo per l'inserimento del nome, un pulsante per avviare la ricerca, e un'etichetta
 * che mostra il conteggio dei risultati trovati. Inoltre, gestisce la selezione di un risultato per visualizzare
 * i dettagli e la navigazione tra i pannelli.</p>
 */
public class VisualizzaTramiteNomePanel extends JPanel {
    private final JTextField searchField; // Campo di testo per inserire il nome dell'area da ricercare
    private final JLabel resultCountLabel; // Etichetta per mostrare il conteggio dei risultati trovati
    private final AdapterResults adapter; // Adapter per la visualizzazione dei risultati

    /**
     * Costruttore per creare il pannello di visualizzazione dei risultati tramite nome dell'area.
     *
     * @param server      L'interfaccia del server per effettuare la ricerca.
     * @param cardLayout  Il layout manager del pannello principale.
     * @param mainPanel   Il pannello principale dell'applicazione.
     */
    public VisualizzaTramiteNomePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        // Imposta il layout del pannello con uno spazio di 10 pixel tra i componenti
        setLayout(new BorderLayout(10, 10));
        // Aggiunge un bordo vuoto di 10 pixel intorno al pannello
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Crea e aggiunge un'etichetta con il titolo della finestra
        InterfaceCreatorComponent creator = new InterfaceCreatorComponent();
        JLabel subtitleLabel = creator.creatorTileWindow("Ricerca area tramite nome ");
        add(subtitleLabel, BorderLayout.NORTH);

        // Pannello per il campo di ricerca e il pulsante di ricerca
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        // Crea un campo di testo per l'inserimento del nome dell'area
        searchField = new JTextField(20);
        searchField.setFont(new Font("Serif", Font.PLAIN, 18));
        // Crea un pulsante per avviare la ricerca
        JButton searchButton = creator.createButton(false, "Search");
        // Aggiunge il campo di testo e il pulsante al pannello di ricerca
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Crea una lista per visualizzare i risultati e un pannello di scorrimento
        JList<ResultWrapper> resultList = creator.createResultList();
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        // Pannello centrale per contenere il pannello di ricerca e la lista dei risultati
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        // Aggiunge il pannello centrale al pannello principale
        add(centerPanel, BorderLayout.CENTER);

        // Crea un pulsante per tornare alla schermata precedente
        JButton backButtonBottom = creator.createButton(true, "Back");
        // Etichetta per mostrare il numero di risultati
        resultCountLabel = creator.creatorTileWindow("");

        // Pannello per il pulsante di ritorno e l'etichetta di conteggio risultati
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        statusPanel.add(resultCountLabel, BorderLayout.CENTER);
        // Aggiunge il pannello di stato alla parte inferiore del pannello principale
        add(statusPanel, BorderLayout.SOUTH);

        // Inizializza l'adapter per aggiornare e visualizzare i risultati
        this.adapter = new AdapterResults(server, cardLayout, mainPanel, resultList);

        // Configura l'azione del pulsante di ricerca
        searchButton.addActionListener(e -> {
            // Ottiene e formatta il termine di ricerca
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                searchTerm = formatSearchTerm(searchTerm);

                LinkedList<Result> results;
                try {
                    // Effettua la ricerca tramite nome dell'area e aggiorna la lista dei risultati
                    results = server.ricercaTramiteNome(searchTerm);
                    resultCountLabel.setText(String.format("La ricerca ha prodotto %d %s", results.size(), results.size() == 1 ? "risultato" : "risultati"));
                    adapter.updateResults(results);
                } catch (RemoteException ex) {
                    // Mostra un messaggio di errore se la comunicazione con il server fallisce
                    JOptionPane.showMessageDialog(this, "Errore nella comunicazione con il server.");
                }
            } else {
                // Mostra un messaggio di errore se il campo di ricerca è vuoto
                JOptionPane.showMessageDialog(this, "Inserisci un nome di città.");
            }
        });

        // Configura la selezione della lista dei risultati
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    // Gestisce il doppio clic per aprire i dettagli del risultato selezionato
                    ResultWrapper selectedResult = resultList.getSelectedValue();
                    try {
                        adapter.openDetailsPanel((JFrame) SwingUtilities.getWindowAncestor(resultList), selectedResult);
                        adapter.reset(searchField, resultCountLabel);
                    } catch (RemoteException e) {
                        // Mostra un messaggio di errore se la comunicazione con il server fallisce
                        JOptionPane.showMessageDialog(VisualizzaTramiteNomePanel.this, "Errore nella comunicazione con il server.");
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
     * Format il termine di ricerca per garantire che la prima lettera di ogni parola sia maiuscola.
     *
     * @param searchTerm Il termine di ricerca da formattare.
     * @return Il termine di ricerca formattato con la prima lettera di ogni parola in maiuscolo.
     */
    private String formatSearchTerm(String searchTerm) {
        return Arrays.stream(searchTerm.split(" ")) // Suddivide la stringa in parole
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase()) // Capitalizza la prima lettera di ogni parola e rende il resto minuscolo
                .collect(Collectors.joining(" ")); // Riunisce le parole formattate in una singola stringa
    }
}
