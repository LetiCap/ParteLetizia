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

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Imposta il valore originale per il confronto successivo
        originalValue = (value == null) ? "" : value.toString();
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean stopCellEditing() {
        validateAndStopEditing();
        return super.stopCellEditing(); // Prosegui con la conferma dell'editing
    }

    // Metodo per validare il valore di Score e terminare l'editing se valido
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

    private boolean isValidScore(String scoreStr) {
        try {
            int score = Integer.parseInt(scoreStr);
            return score >= 1 && score <= 5;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
