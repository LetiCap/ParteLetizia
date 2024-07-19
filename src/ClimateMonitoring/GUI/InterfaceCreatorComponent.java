package ClimateMonitoring.GUI;
/*
Tahir Agalliu 753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto 752504 VA
*/
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Classe per la creazione e personalizzazione di componenti dell'interfaccia utente.
 */
public class InterfaceCreatorComponent {//classe per creazione di componenti predefiniti

    /**
     * Crea un'etichetta per il titolo della finestra con il testo specificato.
     * Viene utilizzata per ottimizzazione codice e per pesonalizzazione ottimizzata
     * @param title Il testo del titolo.
     * @return Un {@link JLabel} configurato con il testo del titolo.
     */
    public JLabel creatorTileWindow(String title) {
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);//creazione finestra personalizzata
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x2E86C1));
        return titleLabel;
    }
    /**
     * Configura il layout e il bordo per il pannello specificato.
     * <p>
     * Questo metodo imposta il layout del pannello su {@link BorderLayout} con
     * spaziatura di 10 pixel tra i componenti e applica un bordo vuoto di 10 pixel
     * su tutti i lati del pannello.
     * </p>
     *
     * @param panel il pannello da configurare; non può essere {@code null}
     *
     * @throws NullPointerException se {@code panel} è {@code null}
     */
    public void setLayoutCustom(JPanel panel) {
        if (panel == null) {
            throw new NullPointerException("Il pannello non può essere null.");
        }
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }


    /**
     * Modifica le impostazioni del {@link GridBagConstraints} con le coordinate specificate.
     *
     * @param gbc L'oggetto {@link GridBagConstraints} da modificare.
     * @param x   La posizione x nella griglia.
     * @param y   La posizione y nella griglia.
     */
    public void modifyGridBagConstraints(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;//modifica valore posizionale di un elemento di una classe su cui viene usato
        gbc.gridy = y;
    }

    /**
     * Crea un campo di testo con il numero di colonne specificato.
     *
     * @param column Il numero di colonne del campo di testo.
     * @return Un {@link JTextField} configurato con il numero di colonne.
     */
    public JTextField createNormaleField(int column) {
        JTextField field = new JTextField(column);//colonne create diepndenti da argomento passato
        field.setFont(new Font("Serif", Font.PLAIN, 18));
        return field;
    }

    /**
     * Crea un pulsante con il testo specificato. Il pulsante può essere configurato come un pulsante "Indietro" o un pulsante normale.
     *
     * @param backButton Se true, crea un pulsante "Indietro" con uno stile specifico. Altrimenti, crea un pulsante normale.
     * @param text       Il testo del pulsante.
     * @return Un {@link JButton} configurato con il testo e il colore specificati.
     */
    public JButton createButton(boolean backButton, String text) {
        JButton returner = new JButton(text);//crea un botton personalizzato
        returner.setFont(new Font("Serif", Font.BOLD, 18));
        returner.setForeground(Color.WHITE);
        if (backButton) {//se back button sfondo rosso
            returner.setBackground(new Color(0xE5050E));
            return returner;
        }
        returner.setBackground(new Color(0x5DADE2));
        return returner;
    }

    /**
     * Crea una lista di risultati configurata con un modello predefinito e impostazioni di visualizzazione.
     *
     * @return Un {@link JList} configurato per contenere oggetti di tipo {@link ResultWrapper}.
     */
    public JList<ResultWrapper> createResultList() {
        JList<ResultWrapper> resultList = new JList<>(new DefaultListModel<>());//inizializziamo lista contenimento result
        resultList.setFont(new Font("Serif", Font.PLAIN, 16));//modello predefinito
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setBackground(new Color(0x5DE2D2));
        return resultList;
    }
}
