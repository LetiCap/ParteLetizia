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
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * Adapter per gestire e visualizzare i risultati climatici in un'interfaccia utente Swing.
 * Questo adapter si occupa di aggiornare la lista dei risultati, aprire il pannello dei dettagli
 * e ripristinare lo stato dell'interfaccia.
 *
 * @author Tahir Agalliu
 */
public class AdapterResults {
    private final ServerInterface server; //istanza server per chiamata metodi
    private final CardLayout cardLayout; //campi per interfaccia...
    private final JPanel mainPanel;
    private final JList<ResultWrapper> resultList; //result list per visualizzazione risultati

    /**
     * Costruttore per inizializzare l'adapter con le dipendenze necessarie.
     *
     * @param server       L'interfaccia del server per ottenere i dati climatici.
     * @param cardLayout   Il layout a schede per la navigazione tra i pannelli.
     * @param mainPanel    Il pannello principale dell'applicazione per la navigazione.
     * @param resultList   La lista dei risultati da visualizzare.
     * @author Tahir Agalliu
     */
    public AdapterResults(ServerInterface server, CardLayout cardLayout, JPanel mainPanel, JList<ResultWrapper> resultList) {
        this.server = server;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.resultList = resultList;
    }

    /**
     * Aggiorna la lista dei risultati con i nuovi dati forniti.
     *
     * @param results Una lista di risultati climatici da visualizzare.
     * @author Tahir Agalliu
     */
    void updateResults(LinkedList<Result> results) {
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        int count = 1;
        for (Result result : results) {
            model.addElement(new ResultWrapper(result, count++));
        }
    }

    /**
     * Apre il pannello dei dettagli per visualizzare i parametri climatici della città selezionata.
     *
     * @param parentFrame  La finestra principale dell'applicazione.
     * @param selectedResult L'oggetto ResultWrapper che contiene i dettagli climatici selezionati.
     * @throws RemoteException Se si verifica un errore nella comunicazione con il server.
     * @author Tahir Agalliu
     */
    void openDetailsPanel(JFrame parentFrame, ResultWrapper selectedResult) throws RemoteException {
        String cityName = selectedResult.getName();
        Object[] options = {"Yes", "No"};
        int choice = JOptionPane.showOptionDialog(parentFrame,
                String.format("Si vuole procedere con la visualizzazione dei parametri climatici della zona: %f,%f (%s)",
                        selectedResult.getLatitude(), selectedResult.getLongitude(), cityName),
                "Dettagli",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            // Crea e aggiunge il pannello per i dettagli climatici alla vista
            ClimatePanel climatePanel = new ClimatePanel(selectedResult.getName(), mainPanel, selectedResult, server);
            mainPanel.add(climatePanel, "ClimatePanel");
            cardLayout.show(mainPanel, "ClimatePanel");
        }
    }

    /**
     * Ripristina il campo di ricerca e la lista dei risultati.
     *
     * @param searchField   Il campo di testo utilizzato per la ricerca.
     * @param resultCountLabel Il label che mostra il numero di risultati.
     * @author Tahir Agalliu
     */
    public void reset(JTextField searchField, JLabel resultCountLabel) {
        searchField.setText("");
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        resultCountLabel.setText("");
    }
}
