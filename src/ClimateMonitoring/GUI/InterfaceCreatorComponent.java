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
 *
 * <p><strong>Questa classe fornisce metodi per creare e configurare componenti grafici come etichette, pannelli, campi di testo,
 * pulsanti e liste. I componenti sono personalizzati per adattarsi all'aspetto dell'applicazione.</strong></p>
 *
 * @author Tahir Agalliu
 * @author Letizia Capitanio
 */
public class InterfaceCreatorComponent {

    /**
     * Crea un'etichetta per il titolo della finestra con il testo specificato.
     * <p><strong>Viene utilizzata per ottimizzazione codice e per personalizzazione ottimizzata.</strong></p>
     *
     * @param title Il testo del titolo.
     * @return Un {@link JLabel} configurato con il testo del titolo.
     * @author Tahir Agalliu
     */
    public JLabel creatorTileWindow(String title) {
        JLabel titleLabel = new JLabel(title, JLabel.CENTER); // Creazione finestra personalizzata
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x2E86C1));
        return titleLabel;
    }

    /**
     * Configura il layout e il bordo per il pannello specificato.
     * <p>
     * <strong>Questo metodo imposta il layout del pannello</strong> su {@link BorderLayout} con
     * spaziatura di 10 pixel tra i componenti e applica un bordo vuoto di 10 pixel
     * su tutti i lati del pannello.
     * </p>
     *
     * @param panel Il pannello da configurare; non può essere {@code null}.
     * @throws NullPointerException se {@code panel} è {@code null}.
     * @author Tahir Agalliu
     */
    public void setLayoutCustom(JPanel panel) {
        if (panel == null) {
            throw new NullPointerException("<strong>Il pannello non può essere null.</strong>");
        }
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    /**
     * Modifica le impostazioni del {@link GridBagConstraints} con le coordinate specificate.
     *
     * @param gbc L'oggetto {@link GridBagConstraints} da modificare.
     * @param x   La posizione <strong>x</strong> nella griglia.
     * @param y   La posizione <strong>y</strong> nella griglia.
     * @author Tahir Agalliu
     */
    public void modifyGridBagConstraints(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; // Modifica valore posizionale di un elemento di una classe su cui viene usato
        gbc.gridy = y;
    }

    /**
     * Crea un campo di testo con il numero di colonne specificato.
     *
     * @param column Il numero di colonne del campo di testo.
     * @return Un {@link JTextField} configurato con il <strong>numero di colonne</strong>.
     * @author Tahir Agalliu
     */
    public JTextField createNormaleField(int column) {
        JTextField field = new JTextField(column); // Colonne create dipendenti da argomento passato
        field.setFont(new Font("Serif", Font.PLAIN, 18));
        return field;
    }

    /**
     * Crea un pulsante con il testo specificato. Il pulsante può essere configurato come un pulsante "Indietro" o un pulsante normale.
     *
     * @param backButton Se true,crea un <strong> pulsante "Indietro" con uno stile specifico</strong>. Altrimenti, crea un <strong>pulsante normale</strong>.
     * @param text       Il <strong>testo</strong> del pulsante.
     * @return Un {@link JButton} configurato con il testo e il colore specificati.
     * @author Tahir Agalliu
     */
    public JButton createButton(boolean backButton, String text) {
        JButton returner = new JButton(text); // Crea un bottone personalizzato
        returner.setFont(new Font("Serif", Font.BOLD, 18));
        returner.setForeground(Color.WHITE);
        if (backButton) { // Se back button sfondo rosso
            returner.setBackground(new Color(0xE5050E));
        } else {
            returner.setBackground(new Color(0x5DADE2));
        }
        return returner;
    }

    /**
     * <strong>Crea una lista di risultati</strong> configurata con un modello predefinito e impostazioni di visualizzazione.
     *
     * @return Un {@link JList} configurato per contenere <strong> oggetti di tipo {@link ResultWrapper}</strong>.
     * @author Tahir Agalliu
     */
    public JList<ResultWrapper> createResultList() {
        JList<ResultWrapper> resultList = new JList<>(new DefaultListModel<>()); // Inizializziamo lista contenimento result
        resultList.setFont(new Font("Serif", Font.PLAIN, 16)); // Modello predefinito
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setBackground(new Color(0x5DE2D2));
        return resultList;
    }
}
