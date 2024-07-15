package ClimateMonitoring.GUI;


import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class VisualizzaTramiteCoordinatePanel extends JPanel {

    private final CardLayout cardLayout;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JButton searchButton;
    private JList<ResultWrapper> resultList;

    private JButton backButtonBottom;
    private JLabel resultCountLabel;

    private ServerInterface server;
    private JPanel mainPanel;

    public VisualizzaTramiteCoordinatePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        this.server = server;
        this.cardLayout=cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        latitudeField = new JTextField(10);
        longitudeField = new JTextField(10);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Latitudine:"));
        searchPanel.add(latitudeField);
        searchPanel.add(new JLabel("Longitudine:"));
        searchPanel.add(longitudeField);
        searchPanel.add(searchButton);

        resultList = new JList<>(new DefaultListModel<>());
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        add(searchPanel, BorderLayout.NORTH);
        add(resultScrollPane, BorderLayout.CENTER);

        backButtonBottom = new JButton("Back");
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
        resultCountLabel = new JLabel("", JLabel.CENTER);
        statusPanel.add(resultCountLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double latitude, longitude;

                try {
                    latitude = Double.parseDouble(latitudeField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VisualizzaTramiteCoordinatePanel.this, "Inserisci una latitudine valida.");
                    return;
                }

                try {
                    longitude = Double.parseDouble(longitudeField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VisualizzaTramiteCoordinatePanel.this, "Inserisci una longitudine valida.");
                    return;
                }

                LinkedList<Result> results = null;
                try {
                    results = server.cercaAreaGeograficaCoordinate(latitude, longitude);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                resultCountLabel.setText(String.format("La ricerca ha prodotto %d risultati", results.size()));

                updateResults(results);
            }
        });

        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<ResultWrapper> list = (JList<ResultWrapper>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    ResultWrapper selectedResult = list.getSelectedValue();
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
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
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
            ClimatePanel climatePanel = new ClimatePanel(selectedResult.getName(),mainPanel,selectedResult,server);

            // Aggiunta di climatePanel a mainPanel
            mainPanel.add(climatePanel, "ClimatePanel");

            // Mostra il nuovo pannello ClimatePanel
            cardLayout.show(mainPanel, "ClimatePanel");
        } else if (choice == JOptionPane.NO_OPTION) {
            // L'utente ha scelto di non fare nulla
        }
    }


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
        latitudeField.setText("");
        longitudeField.setText("");
        DefaultListModel<ResultWrapper> model = (DefaultListModel<ResultWrapper>) resultList.getModel();
        model.clear();
        resultCountLabel.setText("");
    }
}
