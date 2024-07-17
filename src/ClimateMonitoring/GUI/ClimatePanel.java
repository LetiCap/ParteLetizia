package ClimateMonitoring.GUI;

import ClimateMonitoring.Result;
import ClimateMonitoring.ServerInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
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
        this.server = server;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 800)); // Aumenta l'altezza del pannello
        setBackground(Color.WHITE); // Sfondo bianco per il pannello principale

        // Titolo del pannello
        JLabel titleLabel = new JLabel("Parametri climatici per " + cityName);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32)); // Font più grande e in grassetto
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0)); // Spaziatura interna intorno al titolo
        add(titleLabel, BorderLayout.NORTH);

        // Pulsante "Back"
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Font per il pulsante
        backButton.setBackground(new Color(229, 5, 14)); // Colore di sfondo rosso
        backButton.setForeground(Color.WHITE); // Testo bianco
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) mainPanel.getLayout();
                layout.show(mainPanel, "Visualizzazione"); // Presumendo che "Visualizzazione" sia il pannello principale
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE); // Sfondo bianco per il pannello inferiore
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Tabella per i parametri climatici
        String[] columnNames = {"Categoria", "Descrizione", "Valore", "Note"};
        Object[][] data = {
                {"Vento", "Velocità del vento (km/h), suddivisa in fasce", server.getModa(cityName, "vento_val"), server.getNote(cityName, "vento_notes")},
                {"Umidità", "% di Umidità, suddivisa in fasce", server.getModa(cityName, "umidita_val"), server.getNote(cityName, "umidita_notes")},
                {"Pressione", "In hPa, suddivisa in fasce", server.getModa(cityName, "pressione_val"), server.getNote(cityName, "pressione_notes")},
                {"Temperatura", "In °C, suddivisa in fasce", server.getModa(cityName, "temperatura_val"), server.getNote(cityName, "temperatura_notes")},
                {"Precipitazioni", "In mm di pioggia, suddivisa in fasce", server.getModa(cityName, "precipitazioni_val"), server.getNote(cityName, "precipitazioni_notes")},
                {"Altitudine ghiacciai", "In m, suddivisa in fasce", server.getModa(cityName, "altitudineghiacchi_val"), server.getNote(cityName, "altitudineghiacchi_notes")},
                {"Massa ghiacciai", "In kg, suddivisa in fasce", server.getModa(cityName, "massaghiacci_val"), server.getNote(cityName, "massaghiacci_notes")}
        };

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendi tutte le celle non modificabili
            }
        };
        JTable climateTable = new JTable(tableModel) {
            @Override
            public JTableHeader getTableHeader() {
                return new JTableHeader(columnModel) {
                    @Override
                    public boolean getReorderingAllowed() {
                        return false; // Impedisce lo scambio di colonne
                    }
                };
            }
        };

        climateTable.setFont(new Font("Arial", Font.PLAIN, 18)); // Font per il testo della tabella
        climateTable.setRowHeight(30); // Altezza delle righe della tabella
        climateTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20)); // Font per l'intestazione della tabella
        climateTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disabilita il ridimensionamento automatico delle colonne

        // Imposta un renderer personalizzato per la colonna "Note"
        climateTable.getColumnModel().getColumn(3).setCellRenderer(new TextAreaRenderer());

        // Imposta la larghezza delle colonne in base al contenuto
        TableColumn column;
        for (int i = 0; i < climateTable.getColumnCount(); i++) {
            column = climateTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(200); // Larghezza maggiore per la prima colonna (Categoria)
            } else {
                column.setPreferredWidth(300); // Larghezza maggiore per le altre colonne
            }
        }

        JScrollPane scrollPane = new JScrollPane(climateTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Spaziatura esterna intorno alla tabella
        add(scrollPane, BorderLayout.CENTER);
    }

    // Renderer personalizzato per supportare il wrapping del testo nella colonna "Note"
    private static class TextAreaRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 18)); // Font per il testo nella cella
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
