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
    private final JTextField searchField;
    private final JLabel resultCountLabel;
    private final AdapterResults adapter;
    public VisualizzaRisultatiTramiteNomePanel(ServerInterface server, CardLayout cardLayout, JPanel mainPanel) {
        InterfaceCreatorComponent creator=new InterfaceCreatorComponent();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel subtitleLabel=creator.creatorTileWindow("Ricerca area tramite nome ");
        add(subtitleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Serif", Font.PLAIN, 18));
        JButton searchButton =creator.createButton(false,"Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JList<ResultWrapper> resultList = new JList<>(new DefaultListModel<>());
        resultList.setFont(new Font("Serif", Font.PLAIN, 16));
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setBackground(new Color(0xEBF5FB));
        JScrollPane resultScrollPane = new JScrollPane(resultList);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JButton backButtonBottom = creator.createButton(true,"Back");

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
                String searchTerm = searchField.getText().trim();

                // Ensure the first letter is uppercase
                if (searchTerm.length() > 0) {
                    searchTerm = searchTerm.substring(0, 1).toUpperCase() + searchTerm.substring(1);
                }

                LinkedList<Result> results = null;
                if (searchTerm.length() > 1) {
                    try {
                        results = server.ricercaTramiteNome(searchTerm);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(VisualizzaRisultatiTramiteNomePanel.this,
                            "Inserisci un nome di città valido.");
                    return;
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
                        adapter.reset(searchField,resultCountLabel);
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
                cardLayout.show(mainPanel, "Visualizzazione");
                adapter.reset(searchField,resultCountLabel);
            }
        });
    }
}
