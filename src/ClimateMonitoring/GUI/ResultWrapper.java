package ClimateMonitoring.GUI;

/*
Tahir Agalliu 753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto 752504 VA
*/

import ClimateMonitoring.Result;

/**
 * Classe di avvolgimento per il risultato di una ricerca.
 * <p>
 * <strong>Estende la classe {@link Result} e aggiunge un numero di indice per ordinare i risultati.</strong>
 * </p>
 *
 * @author Tahir Agalliu
 */
public class ResultWrapper extends Result {
    private final int number; // Numero di indice per il risultato

    /**
     * Costruttore per creare un'istanza di {@link ResultWrapper}.
     * <p>
     * <strong>Inizializza il risultato avvolto</strong> e imposta il numero di indice associato al risultato.
     * </p>
     *
     * @param result <strong>Il risultato originale</strong> da avvolgere.
     * @param number <strong>Il numero di indice</strong> associato al risultato.
     *
     * @author Tahir Agalliu
     */
    public ResultWrapper(Result result, int number) {
        // Chiama il costruttore della classe base {@link Result} per inizializzare i campi
        super(result.getGeoname(), result.getName(), result.getAsciiName(), result.getCountryCode(),
                result.getCountryName(), result.getLatitude(), result.getLongitude());
        this.number = number; // Imposta il numero di indice
    }

    /**
     * Restituisce una rappresentazione in formato stringa del risultato avvolto.
     * <p>
     * <strong>La stringa include il numero di indice e la rappresentazione stringa del risultato originale.</strong>
     * </p>
     *
     * @return Una stringa che rappresenta il <strong>risultato con il numero di indice</strong>.
     *
     * @author Tahir Agalliu
     */
    @Override
    public String toString() {
        // Format del risultato includendo il numero di indice
        return String.format("%d. %s", number, super.toString());
    }
}
