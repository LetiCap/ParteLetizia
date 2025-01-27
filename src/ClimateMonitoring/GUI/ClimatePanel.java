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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.rmi.RemoteException;

/**
 * <strong>Pannello Swing per visualizzare i parametri climatici di una città selezionata</strong>.
 * Questo pannello mostra una tabella con i parametri climatici e un pulsante per tornare al pannello principale.
 *
 * @author Tahir Agalliu
 */
public class ClimatePanel extends JPanel {

    /**
     * Costruttore per creare un pannello di visualizzazione dei parametri climatici.
     *
     * @param cityName       <strong>Il nome della città</strong> per la quale visualizzare i parametri climatici.
     * @param mainPanel      <strong>Il pannello principale</strong> dell'applicazione per la navigazione.
     * @param selectedResult <strong>Il risultato selezionato</strong> contenente i dati climatici.
     * @param server         <strong>L'interfaccia del server</strong> per ottenere i dati climatici.
     * @throws RemoteException <strong>Se si verifica un errore</strong> nella comunicazione con il server.
     * @author Tahir Agalliu
     */
    public ClimatePanel(String cityName, JPanel mainPanel, Result selectedResult, ServerInterface server) throws RemoteException {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 800)); // Imposta la dimensione preferita del pannello
        setBackground(Color.WHITE); // Imposta il colore di sfondo del pannello

        InterfaceCreatorComponent creator = new InterfaceCreatorComponent();

        // Titolo del pannello
        JLabel titleLabel = creator.creatorTileWindow("Parametri climatici per: " + cityName);
        add(titleLabel, BorderLayout.NORTH);

        // Pulsante "Back"
        JButton backButton = creator.createButton(true, "Back");
        backButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            layout.show(mainPanel, "Visualizzazione"); // Mostra il pannello "Visualizzazione"
        });

        JPanel bottomPanel = createBottomPanel(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Crea e configura la tabella dei parametri climatici
        JTable climateTable = createClimateTable(cityName, server);

        JScrollPane scrollPane = new JScrollPane(climateTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Imposta il bordo del pannello di scorrimento
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea il pannello inferiore con il pulsante "Back".
     *
     * @param backButton <strong>Il pulsante "Back"</strong> da aggiungere al pannello.
     * @return Il pannello inferiore configurato.
     * @author Tahir Agalliu
     */
    private JPanel createBottomPanel(JButton backButton) {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE); // Imposta il colore di sfondo del pannello inferiore
        bottomPanel.add(backButton);
        return bottomPanel;
    }

    /**
     * <strong>Crea e configura la tabella</strong> dei parametri climatici.
     *
     * @param cityName Il nome della <strong>città</strong> per la quale visualizzare i parametri climatici.
     * @param server   <strong>L'interfaccia del server</strong> per ottenere i dati climatici.
     * @return La tabella configurata.
     * @throws RemoteException Se si <strong>verifica un errore</strong> nella comunicazione con il server.
     * @author Tahir Agalliu
     */
    private JTable createClimateTable(String cityName, ServerInterface server) throws RemoteException {
        String[] columnNames = {"Categoria", "Descrizione", "Valore", "Note"};
        Object[][] data = {
                {"Vento", "Velocità del vento (km/h), suddivisa in fasce", server.getModa(cityName, "vento_val"), server.getNote(cityName, "vento_notes")},
                {"Umidità", "% di Umidità, suddivisa in fasce", server.getModa(cityName, "umidita_val"), server.getNote(cityName, "umidita_notes")},
                {"Pressione", "In hPa, suddivisa in fasce", server.getModa(cityName, "pressione_val"), server.getNote(cityName, "pressione_notes")},
                {"Temperatura", "In °C, suddivisa in fasce", server.getModa(cityName, "temperatura_val"), server.getNote(cityName, "temperatura_notes")},
                {"Precipitazioni", "In mm di pioggia, suddivisa in fasce", server.getModa(cityName, "precipitazioni_val"), server.getNote(cityName, "precipitazioni_notes")},
                {"Altitudine ghiacciai", "In m, suddivisa in fasce", server.getModa(cityName, "altitudineghiacci_val"), server.getNote(cityName, "altitudineghiacci_notes")},
                {"Massa ghiacciai", "In kg, suddivisa in fasce", server.getModa(cityName, "massaghiacci_val"), server.getNote(cityName, "massaghiacci_notes")}
        };

        JTable climateTable = getTable(data, columnNames);

        // Configura la tabella
        climateTable.setFont(new Font("Arial", Font.PLAIN, 18)); // Font per il testo della tabella
        climateTable.setRowHeight(30); // Altezza delle righe della tabella
        climateTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20)); // Font per l'intestazione della tabella
        climateTable.getTableHeader().setBackground(new Color(0, 102, 204)); // Colore di sfondo dell'intestazione
        climateTable.getTableHeader().setForeground(Color.WHITE); // Colore del testo dell'intestazione
        climateTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disabilita il ridimensionamento automatico delle colonne

        // Imposta un renderer personalizzato per la colonna "Note"
        climateTable.getColumnModel().getColumn(3).setCellRenderer(new TextAreaRenderer());

        // Imposta la larghezza delle colonne
        for (int i = 0; i < climateTable.getColumnCount(); i++) {
            TableColumn column = climateTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(i == 0 ? 200 : 300); // Larghezza maggiore per la prima colonna (Categoria)
        }

        // Colore alternato delle righe della tabella
        climateTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    cell.setBackground(new Color(255, 215, 0)); // Colore di sfondo per la riga selezionata
                } else {
                    cell.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE); // Colore alternato delle righe
                }
                return cell;
            }
        });

        return climateTable;
    }

    /**
     * <strong>Crea una tabella configurata</strong> con i dati e i nomi delle colonne forniti.
     *
     * @param data         <strong>I dati</strong> da visualizzare nella tabella.
     * @param columnNames  <strong>I nomi delle colonne</strong> della tabella.
     * @return <strong>La tabella</strong> configurata.
     * @author Tahir Agalliu
     */
    private static JTable getTable(Object[][] data, String[] columnNames) {
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendi tutte le celle non modificabili
            }
        };

        JTable climateTable = new JTable(tableModel) {
            @Override
            public JTableHeader getTableHeader() {
                JTableHeader header = super.getTableHeader();
                header.setReorderingAllowed(false); // Impedisce lo scambio di colonne
                return header;
            }
        };
        return climateTable;
    }

    /**
     * Renderer personalizzato per <strong>supportare il wrapping</strong> del testo nella colonna "Note".
     * <strong>Estende JTextArea</strong> per consentire il wrapping del testo e l'adeguamento dell'altezza delle righe.
     *
     * @author Tahir Agalliu
     */
    private static class TextAreaRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        /**
         * Costruttore per inizializzare il renderer.
         */
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 18)); // Font per il testo nella cella
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            // Imposta l'altezza della riga in base al testo
            int rowHeight = getPreferredSize().height;
            if (rowHeight < table.getRowHeight()) {
                rowHeight = table.getRowHeight();
            }
            table.setRowHeight(row, rowHeight);
            if (isSelected) {
                setBackground(new Color(255, 215, 0)); // Colore di sfondo per la riga selezionata
            } else {
                setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE); // Colore alternato delle righe
            }
            return this;
        }
    }
}
