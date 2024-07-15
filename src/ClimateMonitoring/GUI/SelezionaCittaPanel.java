package ClimateMonitoring.GUI;

import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class SelezionaCittaPanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JList<ResultWrapper> resultList;
    private LinkedList<String> lonlatInserite = new LinkedList<>(); // Lista per memorizzare le coordinate selezionate

    private JButton backButtonBottom; // Pulsante "Back" in basso
    private JLabel resultCountLabel; // JLabel per visualizzare il conteggio dei risultati

    private ServerInterface server;

    private CardLayout cardLayout;
    private JPanel mainPanel; // Riferimento al pannello principale

    private BackButtonListener backButtonListener; // Listener per il pulsante "Back"

    public SelezionaCittaPanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel; // Inizializza il riferimento al mainPanel

        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchPanel.add(new JLabel("Inserire la città da inserire:"));
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        resultList = new JList<>(new DefaultListModel<>());
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        add(searchPanel, BorderLayout.NORTH);
        add(resultScrollPane, BorderLayout.CENTER);

        backButtonBottom = new JButton("Back"); // Pulsante "Back" in basso
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        resultCountLabel = new JLabel("", JLabel.CENTER); // Inizializzazione del JLabel per il conteggio dei risultati
        statusPanel.add(resultCountLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
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
                JOptionPane.showMessageDialog(SelezionaCittaPanel.this, "Inserisci un nome di città valido.");
                return;
            }

            // Aggiorna il conteggio dei risultati
            resultCountLabel.setText(String.format("La ricerca ha prodotto %d risultati", results.size()));

            // Aggiorna la lista dei risultati
            updateResults(results);
        });

        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<ResultWrapper> list = (JList<ResultWrapper>) evt.getSource();
                if (evt.getClickCount() == 2) { // Doppio clic
                    ResultWrapper selectedResult = list.getSelectedValue();
                    // Apri un JOptionPane personalizzato per mostrare i dettagli del risultato
                    openDetailsPanel(selectedResult);
                }
            }
        });

        backButtonBottom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Utilizza il cardLayout passato come argomento per tornare indietro
                cardLayout.show(mainPanel, "RegistraCentroNuovo");
                // Chiamata al listener per passare lonlatInserite al pannello principale
                if (backButtonListener != null) {
                    backButtonListener.onBackButtonClicked(lonlatInserite);
                }
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

    private void openDetailsPanel(ResultWrapper selectedResult) {
        String cityName = selectedResult.getName();
        Object[] options = {"Yes", "No"};
        int choice = JOptionPane.showOptionDialog(this,
                String.format("Si vuole procedere con l'inserimento di questa area? %f,%f (%s)",
                        selectedResult.getLatitude(), selectedResult.getLongitude(), cityName),
                "Dettagli",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            // Aggiungi l'elemento selezionato alla lista lonlatInserite
            lonlatInserite.add(selectedResult.getLatitude() + "," + selectedResult.getLongitude()+ " ("+ selectedResult.getName()+")");
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
    public void setBackButtonListener(BackButtonListener listener) {
        this.backButtonListener = listener;
    }
}
