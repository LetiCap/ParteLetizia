package ClimateMonitoring.GUI;
import ClimateMonitoring.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClimatePanel extends JPanel {

    private String cityName;
    private JPanel mainPanel;

    public ClimatePanel(String cityName, JPanel mainPanel, Result selectedResult) {
        this.cityName = cityName;
        this.mainPanel = mainPanel;

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

        // Tabella per i parametri climatici
        String[] columnNames = {"Climate Category", "Explanation", "Score", "Notes"};
        Object[][] data = {
                {"Vento", "Velocità del vento (km/h), suddivisa in fasce", "1 ….. 5", ""},
                {"Umidità", "% di Umidità, suddivisa in fasce", "1 ….. 5", ""},
                {"Pressione", "In hPa, suddivisa in fasce", "1 ….. 5", ""},
                {"Temperatura", "In °C, suddivisa in fasce", "1 ….. 5", ""},
                {"Precipitazioni", "In mm di pioggia, suddivisa in fasce", "1 ….. 5", ""},
                {"Altitudine dei ghiacciai", "In m, suddivisa in fasce", "1 ….. 5", ""},
                {"Massa dei ghiacciai", "In kg, suddivisa in fasce", "1 ….. 5", ""}
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
