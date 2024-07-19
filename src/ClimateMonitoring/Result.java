package ClimateMonitoring;
/*
Tahir Agalliu 753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto 752504 VA
*/
import java.io.Serial;
import java.io.Serializable;

/**
 * Classe che rappresenta un risultato climatico per una località specifica.
 * Questa classe contiene informazioni relative alla posizione geografica e al nome della località.
 * Implementa l'interfaccia {@link java.io.Serializable} per permettere la serializzazione degli oggetti di questa classe.
 *
 * <p>Gli attributi della classe includono:
 * <ul>
 *   <li>geoname: un identificatore univoco per la località.</li>
 *   <li>name: il nome della località.</li>
 *   <li>asciiName: il nome della località in formato ASCII.</li>
 *   <li>countryCode: il codice del paese in cui si trova la località.</li>
 *   <li>countryName: il nome del paese.</li>
 *   <li>latitude: la latitudine della località.</li>
 *   <li>longitude: la longitudine della località.</li>
 * </ul>
 * </p>
 *
 * <p>La classe fornisce metodi per accedere a questi attributi e per rappresentare l'oggetto come una stringa.</p>
 *
 * <p>Gli attributi sono definiti come finali e vengono inizializzati tramite il costruttore.</p>
 *
 * <p>Per una rappresentazione in formato stringa dell'oggetto, viene utilizzato il metodo {@link #toString()}.</p>
 *
 * @see java.io.Serializable
 */
public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Aggiunto per la serializzazione

    /** Identificatore univoco per la località. */
    private final Integer geoname;

    /** Nome della località. */
    private final String name;

    /** Nome della località in formato ASCII. */
    private final String asciiName;

    /** Codice del paese in cui si trova la località. */
    private final String countryCode;

    /** Nome del paese in cui si trova la località. */
    private final String countryName;

    /** Latitudine della località. */
    private final Double latitude;

    /** Longitudine della località. */
    private final Double longitude;

    /**
     * Costruttore per inizializzare un oggetto Result.
     *
     * @param geoname      L'identificatore univoco della località.
     * @param name         Il nome della località.
     * @param asciiName    Il nome della località in formato ASCII.
     * @param countryCode  Il codice del paese in cui si trova la località.
     * @param countryName  Il nome del paese in cui si trova la località.
     * @param latitude     La latitudine della località.
     * @param longitude    La longitudine della località.
     */
    public Result(Integer geoname, String name, String asciiName, String countryCode, String countryName, Double latitude, Double longitude) {
        this.geoname = geoname;
        this.name = name;
        this.asciiName = asciiName;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Restituisce l'identificatore univoco della località.
     *
     * @return L'identificatore univoco della località.
     */
    public Integer getGeoname() {
        return geoname;
    }

    /**
     * Restituisce il nome della località.
     *
     * @return Il nome della località.
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce il nome della località in formato ASCII.
     *
     * @return Il nome della località in formato ASCII.
     */
    public String getAsciiName() {
        return asciiName;
    }

    /**
     * Restituisce il codice del paese in cui si trova la località.
     *
     * @return Il codice del paese.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Restituisce il nome del paese in cui si trova la località.
     *
     * @return Il nome del paese.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Restituisce la latitudine della località.
     *
     * @return La latitudine della località.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Restituisce la longitudine della località.
     *
     * @return La longitudine della località.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Restituisce una rappresentazione in formato stringa dell'oggetto Result.
     * La rappresentazione include la latitudine, la longitudine e il nome della località.
     *
     * @return Una stringa che rappresenta l'oggetto Result.
     */
    @Override
    public String toString() {
        return String.format("%s,%s(%s)", latitude, longitude, name);
    }
}
