package elysium.com.weathertemplate.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Elysium Development - 7/10/17.
 */

public class DailyWeatherReport {

    // Constants
    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_SNOW = "Snow";
    public static final String WEATHER_TYPE_WIND = "Wind";

    private String cityName;
    private String country;
    private String weather;
    private String formattedDate;
    private String dateDay;
    private String dateMonth;
    private String dateDate;
    private int currentTemp, minTemp, maxTemp, humidity;

    public String getDateDay() {
        return dateDay;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public String getDateDate() {
        return dateDate;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public String getWeather() {
        return weather;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getHumidity() {
        return humidity;
    }

    public DailyWeatherReport(String cityName, String country, String weather, String rawDate,
                              String dateDay, String dateDate, String dateMonth, int currentTemp, int minTemp, int maxTemp, int humidity) {

        this.cityName = cityName;
        this.country = country;
        this.weather = weather;
        this.formattedDate = rawDateToConverted(rawDate);
        this.currentTemp = currentTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.dateDay = dateDay;
        this.dateMonth = dateMonth;
        this.dateDate = dateDate;
    }

    public String rawDateToConverted(String rawDate) {
        //convert raw date into formatted date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("h:mm"); // E, MMM d Locale.getDefault()
        String strDate = format.format(calendar.getTime());

//        dateDay = values[0];
//        dateDate = values[1];

        return strDate;

//        return "July 11th";
    }
}
