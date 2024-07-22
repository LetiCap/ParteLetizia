package ClimateMonitoring.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Editor per le celle di punteggio nella tabella dei parametri climatici.
 * <p>
 * Questa classe estende {@link DefaultCellEditor} e fornisce la funzionalità per l'editing delle celle
 * della colonna "Score" della tabella. Include la validazione del punteggio e la gestione degli eventi di focus.
 * </p>
 */
public class ScoreCellEditor extends DefaultCellEditor {
    private JTextField textField;
    private String originalValue;
    private JTable climateTable;

    /**
     * Costruttore per inizializzare l'editor delle celle.
     *
     * @param textField Il componente {@link JTextField} utilizzato per l'editing delle celle.
     * @param climateTable La tabella dei parametri climatici in cui si trovano le celle da editare.
     */
    public ScoreCellEditor(JTextField textField, JTable climateTable) {
        super(textField);
        this.textField = textField;
        this.climateTable = climateTable;

        // Aggiungi un listener per il focus per monitorare quando l'utente inizia a editare
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateAndStopEditing();
            }
        });
    }

    /**
     * Restituisce il componente dell'editor per la cella della tabella.
     *
     * @param table La tabella in cui si trova la cella da editare.
     * @param value Il valore attuale della cella.
     * @param isSelected Indica se la cella è selezionata.
     * @param row L'indice della riga della cella.
     * @param column L'indice della colonna della cella.
     * @return Il componente dell'editor per la cella.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Imposta il valore originale per il confronto successivo
        originalValue = (value == null) ? "" : value.toString();
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    /**
     * Termina l'editing della cella e applica le modifiche se il valore è valido.
     *
     * @return true se l'editing è stato confermato, false altrimenti.
     */
    @Override
    public boolean stopCellEditing() {
        validateAndStopEditing();
        return super.stopCellEditing(); // Prosegui con la conferma dell'editing
    }

    /**
     * Valida il valore inserito nella cella e termina l'editing se il valore è valido.
     * Se il valore non è valido, viene mostrato un messaggio di errore e l'editor rimane aperto.
     */
    private void validateAndStopEditing() {
        String scoreStr = textField.getText().trim();
        if (isValidScore(scoreStr)) {
            // Aggiorna il modello della tabella solo se stai editando una cella valida
            int editingRow = climateTable.getEditingRow();
            int editingColumn = climateTable.getEditingColumn();
            if (editingRow != -1 && editingColumn != -1) {
                climateTable.getModel().setValueAt(scoreStr, editingRow, editingColumn);
            }
        } else {
            // Mostra il messaggio di errore
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(textField), // Mostra il dialogo nella finestra principale
                    "Inserisci un valore numerico compreso tra 1 e 5 per Score",
                    "Input non valido", JOptionPane.ERROR_MESSAGE);

            // Ripristina il valore originale nella cella
            textField.setText(originalValue);
            textField.requestFocusInWindow(); // Rimetti il focus nella cella per permettere la modifica
        }
    }

    /**
     * Verifica se il valore fornito è un punteggio valido (compreso tra 1 e 5).
     *
     * @param scoreStr Il valore del punteggio da verificare.
     * @return true se il valore è valido, false altrimenti.
     */
    private boolean isValidScore(String scoreStr) {
        try {
            int score = Integer.parseInt(scoreStr);
            return score >= 1 && score <= 5;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
