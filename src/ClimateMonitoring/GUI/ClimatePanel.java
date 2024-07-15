package ClimateMonitoring.GUI;

import ClimateMonitoring.DatabaseConnection;
import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ClimatePanel extends JPanel {

    private String cityName;
    private ServerInterface server;
    private JPanel mainPanel;

    public ClimatePanel(String cityName, JPanel mainPanel, Result selectedResult, ServerInterface server) throws RemoteException {
        this.cityName = cityName;
        this.mainPanel = mainPanel;
        this.server=server;

        setLayout(new BorderLayout());

        // Titolo del pannello
        JLabel titleLabel = new JLabel("Parametri climatici per " + cityName);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Pulsante "Back"
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) mainPanel.getLayout();
                layout.show(mainPanel, "Visualizzazione"); // Presumendo che "Visualizzazione" sia il pannello principale
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);


        // Ottenere le note del vento
        String ventoNotes = server.getNote(cityName,"vento_notes");
        String umitida_notes=server.getNote(cityName,"umidita_notes");
        String precipitazioni_notes=server.getNote(cityName,"precipitazioni_notes");
        String pressione_notes=server.getNote(cityName,"pressione_notes");
        String temperatura_notes=server.getNote(cityName,"temperatura_notes");
        String altitudineghiacci_notes=server.getNote(cityName,"altitudineghiacci_notes");
        String massaghiacci_notes=server.getNote(cityName,"massaghiacci_notes");




        // Tabella per i parametri climatici
        String[] columnNames = {"Climate Category", "Explanation", "Score", "Notes"};
        Object[][] data = {
                {"Vento", "Velocità del vento (km/h), suddivisa in fasce",server.getMediana(cityName,"vento_val"), server.getNote(cityName,"vento_notes")},
                {"Umidità", "% di Umidità, suddivisa in fasce",server.getMediana(cityName,"umidita_val"), server.getNote(cityName,"umidita_notes")},
                {"Pressione", "In hPa, suddivisa in fasce",server.getMediana(cityName,"pressione_val"), server.getNote(cityName,"pressione_notes")},
                {"Temperatura", "In °C, suddivisa in fasce", server.getMediana(cityName,"temperatura_val"),server.getNote(cityName,"temperatura_notes")},
                {"Precipitazioni", "In mm di pioggia, suddivisa in fasce",server.getMediana(cityName,"precipitazioni_val"), server.getNote(cityName,"precipitazioni_notes")},
                {"Altitudine dei ghiacciai", "In m, suddivisa in fasce",  server.getMediana(cityName,"altitudinegiacci_val"),server.getNote(cityName,"altitudineghiacci_notes")},
                {"Massa dei ghiacciai", "In kg, suddivisa in fasce",server.getMediana(cityName,"massaghiacci_val"), server.getNote(cityName,"massaghiacci_notes")}
        };

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendi tutte le celle non modificabili
            }
        };

        JTable climateTable = new JTable(tableModel);
        climateTable.setCellSelectionEnabled(false); // Disabilita la selezione delle celle
        climateTable.setFocusable(false); // Disabilita il focus sulla tabella

        // Imposta un renderer personalizzato per la colonna "Notes"
        climateTable.getColumnModel().getColumn(3).setCellRenderer(new TextAreaRenderer());

        JScrollPane scrollPane = new JScrollPane(climateTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Renderer personalizzato per supportare il wrapping del testo nella colonna "Notes"
    private static class TextAreaRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }
}
