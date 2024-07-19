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
 * Estende la classe {@link Result} e aggiunge un numero di indice per ordinare i risultati.
 */
public class ResultWrapper extends Result {
    private final int number; // Numero di indice per il risultato

    /**
     * Costruttore per creare un'istanza di {@link ResultWrapper}.
     *
     * @param result Il risultato originale da avvolgere.
     * @param number Il numero di indice associato al risultato.
     */
    public ResultWrapper(Result result, int number) {
        // Chiama il costruttore della classe base {@link Result} per inizializzare i campi
        super(result.getGeoname(), result.getName(), result.getAsciiName(), result.getCountryCode(),
                result.getCountryName(), result.getLatitude(), result.getLongitude());
        this.number = number; // Imposta il numero di indice
    }

    /**
     * Restituisce una rappresentazione in formato stringa del risultato avvolto.
     * La stringa include il numero di indice e la rappresentazione stringa del risultato originale.
     *
     * @return Una stringa che rappresenta il risultato con il numero di indice.
     */
    @Override
    public String toString() {
        // Format del risultato includendo il numero di indice
        return String.format("%d. %s", number, super.toString());
    }
}
