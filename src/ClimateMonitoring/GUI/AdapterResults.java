package ClimateMonitoring.GUI;

import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class AdapterResults {
    ServerInterface server;
    CardLayout cardLayout;
    JPanel mainPanel;
    private JList<ResultWrapper> resultList;
    public AdapterResults(ServerInterface server, CardLayout cardLayout, JPanel mainPanel, JList<ResultWrapper> resultList){
        this.server=server;
        this.cardLayout=cardLayout;
        this.resultList=resultList;
        this.mainPanel=mainPanel;
    }
    void updateResults(LinkedList<Result> results) {
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        int count = 1;
        for (Result result : results) {
            model.addElement(new ResultWrapper(result, count++));
        }
    }
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
            // Supponiamo che mainPanel, cardLayout, e server siano accessibili
            ClimatePanel climatePanel = new ClimatePanel(selectedResult.getName(), mainPanel, selectedResult, server);
            mainPanel.add(climatePanel, "ClimatePanel");
            cardLayout.show(mainPanel, "ClimatePanel");
        }
    }


    public void reset(JTextField searchField,JLabel resultCountLabel) {
        searchField.setText("");
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        resultCountLabel.setText("");
    }



}
