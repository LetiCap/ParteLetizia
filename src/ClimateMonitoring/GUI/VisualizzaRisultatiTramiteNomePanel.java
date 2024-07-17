package ClimateMonitoring.GUI;

import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class VisualizzaRisultatiTramiteNomePanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JList<ResultWrapper> resultList;

    private JButton backButtonBottom; // Pulsante "Back" in basso
    private JLabel resultCountLabel; // JLabel per visualizzare il conteggio dei risultati

    private ServerInterface server;

    private CardLayout cardLayout;
    private JPanel mainPanel; // Riferimento al pannello principale

    public VisualizzaRisultatiTramiteNomePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel; // Inizializza il riferimento al mainPanel

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Aggiungi un sottotitolo
        JLabel subtitleLabel = new JLabel("Ricerca area tramite nome:", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        subtitleLabel.setForeground(new Color(0x2E86C1));
        add(subtitleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Serif", Font.PLAIN, 18));
         searchButton = new JButton("Search");
        searchButton.setFont(new Font("Serif", Font.BOLD, 18));
        searchButton.setBackground(new Color(0x5DADE2));
        searchButton.setForeground(Color.WHITE);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        resultList = new JList<>(new DefaultListModel<>());
        resultList.setFont(new Font("Serif", Font.PLAIN, 16));
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setBackground(new Color(0xEBF5FB));
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);


        backButtonBottom = new JButton("Back"); // Pulsante "Back" in basso
        backButtonBottom.setFont(new Font("Serif", Font.BOLD, 18));
        backButtonBottom.setBackground(Color.RED); // Impostazione del colore rosso
        backButtonBottom.setForeground(Color.WHITE);

        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);

        resultCountLabel = new JLabel("", JLabel.CENTER); // Inizializzazione del JLabel per il conteggio dei risultati
        resultCountLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        resultCountLabel.setForeground(new Color(0x2E86C1));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        statusPanel.add(resultCountLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                // Esegui la ricerca in base all'input dell'utente
                LinkedList<Result> results = null;
                if (searchTerm.length() > 1) {
                    try {
                        results = server.ricercaTramiteNome(searchTerm);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    // Gestione caso in cui l'input sia troppo corto
                    JOptionPane.showMessageDialog(VisualizzaRisultatiTramiteNomePanel.this, "Inserisci un nome di città valido.");
                    return;
                }

                // Aggiorna il conteggio dei risultati
                resultCountLabel.setText(String.format("La ricerca ha prodotto %d risultati", results.size()));

                // Aggiorna la lista dei risultati
                updateResults(results);
            }
        });

        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<ResultWrapper> list = (JList<ResultWrapper>) evt.getSource();
                if (evt.getClickCount() == 2) { // Doppio clic
                    ResultWrapper selectedResult = list.getSelectedValue();
                    // Apri un JOptionPane personalizzato per mostrare i dettagli del risultato
                    try {
                        openDetailsPanel(selectedResult);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        backButtonBottom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Utilizza il cardLayout passato come argomento per tornare indietro
                cardLayout.show(mainPanel, "Visualizzazione");
            }
        });
    }

    private void updateResults(LinkedList<Result> results) {
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        int count = 1;
        for (Result result : results) {
            model.addElement(new ResultWrapper(result, count++));
        }
    }

    private void openDetailsPanel(ResultWrapper selectedResult) throws RemoteException {
        String cityName = selectedResult.getName();
        Object[] options = {"Yes", "No"};
        int choice = JOptionPane.showOptionDialog(this,
                String.format("Si vuole procedere con la visualizzazione dei parametri climatici della zona: %f,%f (%s)",
                        selectedResult.getLatitude(), selectedResult.getLongitude(), cityName),
                "Dettagli",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            // Creazione di una nuova istanza di ClimatePanel
            ClimatePanel climatePanel = new ClimatePanel(selectedResult.getName(), mainPanel, selectedResult, server);

            // Aggiunta di climatePanel a mainPanel
            mainPanel.add(climatePanel, "ClimatePanel");

            // Mostra il nuovo pannello ClimatePanel
            cardLayout.show(mainPanel, "ClimatePanel");
        } else if (choice == JOptionPane.NO_OPTION) {
            // L'utente ha scelto di non fare nulla
        }
    }

    // Classe wrapper per risultato con numero
    private static class ResultWrapper extends Result {
        private final int number;

        public ResultWrapper(Result result, int number) {
            super(result.getGeoname(), result.getName(), result.getAsciiName(), result.getCountryCode(),
                    result.getCountryName(), result.getLatitude(), result.getLongitude());
            this.number = number;
        }

        @Override
        public String toString() {
            return String.format("%d. %s", number, super.toString());
        }
    }

    public void reset() {
        // Metodo per resettare il pannello quando viene mostrato
        searchField.setText("");
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        resultCountLabel.setText("");
    }
}
