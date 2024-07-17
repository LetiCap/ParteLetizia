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


public class VisualizzaTramiteCoordinatePanel extends JPanel {

    private final AdapterResults adapter;
    private final JTextField latitudeField;
    private final JTextField longitudeField;

    private final JLabel resultCountLabel;

    public VisualizzaTramiteCoordinatePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        InterfaceCreatorComponent creator=new InterfaceCreatorComponent();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Aggiungi un titolo

        add(creator.creatorTileWindow("Ricerca area tramite coordinate"), BorderLayout.NORTH);

        // Pannello di ricerca con layout GridBagLayout
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        creator.modifyGridBagConstraints(gbc,0,0);
        searchPanel.add(new JLabel("Latitudine:"), gbc);
        creator.modifyGridBagConstraints(gbc,1,0);
        latitudeField = creator.createNormaleField(10);
        searchPanel.add(latitudeField, gbc);
        creator.modifyGridBagConstraints(gbc,0,1);
        searchPanel.add(new JLabel("Longitudine:"), gbc);
        creator.modifyGridBagConstraints(gbc,1,1);
        longitudeField = creator.createNormaleField(10);
        searchPanel.add(longitudeField, gbc);
        creator.modifyGridBagConstraints(gbc,0,2);

        gbc.gridwidth = 2;
        JButton searchButton = creator.createButton(false, "Search");
        searchPanel.add(searchButton, gbc);

        JList<ResultWrapper> resultList = new JList<>(new DefaultListModel<>());
        resultList.setFont(new Font("Serif", Font.PLAIN, 16));
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setBackground(new Color(0xEBF5FB));
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        JButton backButtonBottom = creator.createButton(true, "Back");
        JPanel buttonPanelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanelBottom.add(backButtonBottom);
        resultCountLabel=creator.creatorTileWindow("");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(buttonPanelBottom, BorderLayout.EAST);
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

                adapter.updateResults(results);
            }
        });

        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<ResultWrapper> list = (JList<ResultWrapper>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    ResultWrapper selectedResult = list.getSelectedValue();
                    try {
                        adapter.openDetailsPanel((JFrame) SwingUtilities.getWindowAncestor(list), selectedResult);
                        adapter.reset(longitudeField,resultCountLabel);
                        adapter.reset(latitudeField,resultCountLabel);

                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        this.adapter=new AdapterResults(server,cardLayout,mainPanel, resultList);
        backButtonBottom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adapter.reset(longitudeField,resultCountLabel);
                adapter.reset(latitudeField,resultCountLabel);
                cardLayout.show(mainPanel, "Visualizzazione");
            }
        });
    }
}
