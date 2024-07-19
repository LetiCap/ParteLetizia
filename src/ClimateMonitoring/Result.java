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
 * Classe che rappresenta un <strong>risultato climatico</strong> per una località specifica.
 * Questa classe contiene informazioni relative alla <strong>posizione geografica</strong> e al <strong>nome</strong> della località.
 * Implementa l'interfaccia {@link java.io.Serializable} per permettere la <strong>serializzazione</strong> degli oggetti di questa classe.
 *
 * <p>Gli attributi della classe includono:
 * <ul>
 *   <li><strong>geoname</strong>: un identificatore univoco per la località.</li>
 *   <li><strong>name</strong>: il nome della località.</li>
 *   <li><strong>asciiName</strong>: il nome della località in formato ASCII.</li>
 *   <li><strong>countryCode</strong>: il codice del paese in cui si trova la località.</li>
 *   <li><strong>countryName</strong>: il nome del paese.</li>
 *   <li><strong>latitude</strong>: la latitudine della località.</li>
 *   <li><strong>longitude</strong>: la longitudine della località.</li>
 * </ul>
 * </p>
 *
 * <p>La classe fornisce metodi per accedere a questi attributi e per rappresentare l'oggetto come una <strong>stringa</strong>.</p>
 *
 * <p>Gli attributi sono definiti come <strong>finali</strong> e vengono inizializzati tramite il <strong>costruttore</strong>.</p>
 *
 * <p>Per una rappresentazione in formato stringa dell'oggetto, viene utilizzato il metodo {@link #toString()}.</p>
 *
 * @see java.io.Serializable
 * @author Tahir Agalliu
 */
public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Aggiunto per la serializzazione

    /** <strong>Identificatore univoco</strong> per la località. */
    private final Integer geoname;

    /** <strong>Nome</strong> della località. */
    private final String name;

    /** <strong>Nome</strong> della località in formato ASCII. */
    private final String asciiName;

    /** <strong>Codice del paese</strong> in cui si trova la località. */
    private final String countryCode;

    /** <strong>Nome del paese</strong> in cui si trova la località. */
    private final String countryName;

    /** <strong>Latitudine</strong> della località. */
    private final Double latitude;

    /** <strong>Longitudine</strong> della località. */
    private final Double longitude;

    /**
     * Costruttore per inizializzare un oggetto <strong>Result</strong>.
     *
     * @param geoname      L'<strong>identificatore univoco</strong> della località.
     * @param name         Il <strong>nome</strong> della località.
     * @param asciiName    Il <strong>nome</strong> della località in formato ASCII.
     * @param countryCode  Il <strong>codice del paese</strong> in cui si trova la località.
     * @param countryName  Il <strong>nome del paese</strong> in cui si trova la località.
     * @param latitude     La <strong>latitudine</strong> della località.
     * @param longitude    La <strong>longitudine</strong> della località.
     * @author Tahir Agalliu
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
     * Restituisce l'<strong>identificatore univoco</strong> della località.
     *
     * @return L'<strong>identificatore univoco</strong> della località.
     * @author Tahir Agalliu
     */
    public Integer getGeoname() {
        return geoname;
    }

    /**
     * Restituisce il <strong>nome</strong> della località.
     *
     * @return Il <strong>nome</strong> della località.
     * @author Tahir Agalliu
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce il <strong>nome</strong> della località in formato ASCII.
     *
     * @return Il <strong>nome</strong> della località in formato ASCII.
     * @author Tahir Agalliu
     */
    public String getAsciiName() {
        return asciiName;
    }

    /**
     * Restituisce il <strong>codice del paese</strong> in cui si trova la località.
     *
     * @return Il <strong>codice del paese</strong>.
     * @author Tahir Agalliu
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Restituisce il <strong>nome del paese</strong> in cui si trova la località.
     *
     * @return Il <strong>nome del paese</strong>.
     * @author Tahir Agalliu
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Restituisce la <strong>latitudine</strong> della località.
     *
     * @return La <strong>latitudine</strong> della località.
     * @author Tahir Agalliu
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Restituisce la <strong>longitudine</strong> della località.
     *
     * @return La <strong>longitudine</strong> della località.
     * @author Tahir Agalliu
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Restituisce una rappresentazione in formato stringa dell'oggetto <strong>Result</strong>.
     * La rappresentazione include la <strong>latitudine</strong>, la <strong>longitudine</strong> e il <strong>nome</strong> della località.
     *
     * @return Una stringa che rappresenta l'oggetto <strong>Result</strong>.
     * @author Tahir Agalliu
     */
    @Override
    public String toString() {
        return String.format("%s,%s(%s)", latitude, longitude, name);
    }
}
