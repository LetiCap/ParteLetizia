package ClimateMonitoring;/*Tahir Agalliu	753550 VA
Letizia Capitanio 752465 VA
Alessandro D'Urso 753578 VA
Francesca Ziggiotto	752504 VA
*/


public class Result {
    private Integer geoname;
    private String name;
    private String asciiName;
    private String countryCode;
    private String countryName;
    private Double latitude;
    private Double longitude;

    public Result(Integer geoname, String name, String asciiName, String countryCode, String countryName, Double latitude, Double longitude) {
        this.geoname = geoname;
        this.name = name;
        this.asciiName = asciiName;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getGeoname() {
        return geoname;
    }

    public String getName() {
        return name;
    }

    public String getAsciiName() {
        return asciiName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    @Override
    public String toString() {
        return String.format("%s,%s(%s)", latitude, longitude, name);
    }
}
