package ClimateMonitoring.GUI;



import ClimateMonitoring.Result;

public class ResultWrapper extends Result {
    private final int number;

    public ResultWrapper(Result result, int number) {
        super(result.getGeoname(), result.getName(), result.getAsciiName(), result.getCountryCode(),
                result.getCountryName(), result.getLatitude(), result.getLongitude());
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("%d. %s", number, super.toString());
    }
}
