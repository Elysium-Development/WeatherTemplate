package elysium.com.weathertemplate;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.instantapps.ActivityCompat;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import elysium.com.weathertemplate.Model.DailyWeatherReport;
import elysium.com.weathertemplate.Model.UVIndexReport;

import static com.google.android.gms.R.id.date;
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    // URL strings for weather report
    final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    final String COORD_URL = "/?lat="; //40.739656&lon=-74.248329";
    final String UNITS_URL = "&units=imperial";
    final String UNITS_METRIC_URL = "&units=metric";
    final String API_KEY_URL = "&APPID=2d7e2efc3327662820e224d9906655bf";

    /**
     * SAMPLE UVI call - http://api.openweathermap.org/data/2.5/uvi/forecast?appid={appid}&lat={lat}&lon={lon}&cnt={cnt}
     */

    // URL strings for UVI report
    final String UVI_BASE = "http://api.openweathermap.org/data/2.5/uvi/forecast?";
    final String UVI_API = "appid=2d7e2efc3327662820e224d9906655bf";
    final String UVI_COORD = "&lat=40.739656&lon=-74.248329";
    final String CNT_VAL = "&cnt=1";

    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;

    private TextView dateDay;
    private TextView dateMonth;
    private TextView dateDate;

    private ArrayList<DailyWeatherReport> weatherReportList = new ArrayList<>();
    private ArrayList<UVIndexReport> uviReportList = new ArrayList<>();

    // View declarations
    private ImageView weatherIcon;
    private TextClock clock_12hr;
    private TextView todayWeatherTxt, tempTxt, currentLow, conditionsTxt, cityTxt, locationTxt,
            uviValueTxt, humidityPercent, lookAheadHeaderTxt, lookAheadBlurbTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO - ADD THESE TO STRINGS.XML
//    Today is Saturday, July 8th. There is a
//    high of 85\u2109 and a low of 64\u2109.
        final String welcomStart = "Today is ";
        final String thereIs = "There is a high of ";
        final String maxTemp = "Max";
        final String minTemp = " and a low of Min";
        final String degreeF = "\u2109";
        final String degreeC = "\u2103";

        final String WELCOME_TEXT = welcomStart + dateDay + "," + dateMonth + dateDate + "." + thereIs + maxTemp + degreeF + minTemp + degreeF + ".";

        // TODO - FIND OUT HOW TO DYNAMICALLY UPDATE @STRING VALUES IN TEXTVIEWS!!!!!!!
        clock_12hr = (TextClock) findViewById(R.id.clock_12hr);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        todayWeatherTxt = (TextView) findViewById(R.id.today_weather_text);
        todayWeatherTxt.setText(WELCOME_TEXT);
        tempTxt = (TextView) findViewById(R.id.tempTxt);
        currentLow = (TextView) findViewById(R.id.currentLow);
        conditionsTxt = (TextView) findViewById(R.id.conditionsTxt);
        locationTxt = (TextView) findViewById(R.id.location_txt);
        uviValueTxt = (TextView) findViewById(R.id.uviValueTxt);
        humidityPercent = (TextView) findViewById(R.id.humidityPercent);
        lookAheadHeaderTxt = (TextView) findViewById(R.id.lookingAheadTxt);
        lookAheadBlurbTxt = (TextView) findViewById(R.id.lookAdheadBlurbTxt);
        dateDay = (TextView) findViewById(R.id.date_day_txt);
        dateMonth = (TextView) findViewById(R.id.date_month_txt);
        dateDate = (TextView) findViewById(R.id.date_date_txt);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void downloadWeatherData(final Location location) {

        /**
         * Getting weather data and parsing weather JSON
         */

        final String fullCoords = COORD_URL + location.getLatitude() + "&lon=" + location.getLongitude();

        final String url = BASE_URL + fullCoords + UNITS_URL + API_KEY_URL;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject city = response.getJSONObject("city");
                    String cityName = city.getString("name");
                    String country = city.getString("country");

                    JSONArray list = response.getJSONArray("list");
                    for (int i = 0; i < 5; i++) {
                        JSONObject obj = list.getJSONObject(i);
                        JSONObject main = obj.getJSONObject("main");
                        Double currentTemp = main.getDouble("temp");
                        Double minTemp = main.getDouble("temp_min");
                        Double maxTemp = main.getDouble("temp_max");
                        Integer humidity = main.getInt("humidity");

                        JSONArray weatherArray = obj.getJSONArray("weather");
                        JSONObject weather = weatherArray.getJSONObject(0);
                        String weatherType = weather.getString("main");
                        String weatherDescription = weather.getString("description");

                        String rawDate = obj.getString("dt_txt");
                        String dateDay = obj.getString("dt_txt");
                        String dateDate = obj.getString("dt_txt");
                        String dateMonth = obj.getString("dt_txt");

                        DailyWeatherReport report = new DailyWeatherReport(cityName, country, weatherType, rawDate, dateDay, dateDate, dateMonth, currentTemp.intValue(), minTemp.intValue(), maxTemp.intValue(), humidity.intValue());
                        Log.v("WEATHER REPORT", "WR " + report.getWeather());
                        Log.v("DATE", "DATE: " + report.getDateDay() + report.getDateMonth() + report.getDateDate());
                        weatherReportList.add(report);
                    }

                    Log.v("JSON", "Name: " + cityName + "-" + "Country: " + country);

                } catch (JSONException e) {
                }

                updateUI();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("HELLO", "Err: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    /**
     * The below (until onLocationChanged is called) is getting and parsing the UV Index JOSN data
     * from openweathermap.org. Logs have been included. CODE IS WORKING AS EXPECTED!
     * @param
     */
    public void downloadUVIndexData(Location location) {

        final String uvIndex = UVI_BASE + UVI_API + UVI_COORD + CNT_VAL;
        final String date = getString(R.string.date);

        final JsonArrayRequest uvIndexRequest = new JsonArrayRequest(Request.Method.GET, uvIndex, null, new Response.Listener< JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject uvIndex = response.getJSONObject(0);
                    Double uvi = uvIndex.getDouble("value");

                    UVIndexReport uviReport = new UVIndexReport(uvi);
                    Log.v("UVI REPORT", "UVIrep: " + uviReport.getUvIndex());
                    uviReportList.add(uviReport);

                    Log.v("UVINDEX", "UVI: " + uvi);

                } catch (JSONException e) {
                }

                updateUI();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("UVINDEX", "Err: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(uvIndexRequest);
    }

    public void updateUI() {
        if (weatherReportList.size() > 0) {
            if (uviReportList.size() > 0) {
                DailyWeatherReport weatherReport = weatherReportList.get(0);
                UVIndexReport uviReport = uviReportList.get(0);

                switch (weatherReport.getWeather()) {

                    // TODO: DOWNLOAD SOME NICE WEATHER ICONS TO USE HERE
                    case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cast_album_art_placeholder));
                        break;
                    case DailyWeatherReport.WEATHER_TYPE_RAIN:
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cast_album_art_placeholder));
                        break;
                    case DailyWeatherReport.WEATHER_TYPE_SNOW:
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cast_album_art_placeholder));
                        break;
                    case DailyWeatherReport.WEATHER_TYPE_WIND:
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cast_album_art_placeholder));
                        break;
                    default:
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cast_album_art_placeholder));
                }

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("EEEE MMMM d", Locale.getDefault());
                String strDate = format.format(calendar.getTime());

                String[] values = strDate.split("/", 0);

                dateDay.setText(values[2]);
                dateMonth.setText(values[1] + ",");
                dateDate.setText(values[0]);
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM d");
//        String strDate = sdf.format(cal.getTime());
//        date.setText(strDate);

                tempTxt.setText(Integer.toString(weatherReport.getCurrentTemp()));
                currentLow.setText(Integer.toString(weatherReport.getMinTemp()));
                cityTxt.setText(weatherReport.getCityName());
                conditionsTxt.setText(weatherReport.getWeather());
                humidityPercent.setText(Integer.toString(weatherReport.getHumidity()));
                uviValueTxt.setText(Double.toString(uviReport.getUvIndex()));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        downloadWeatherData(location);
        downloadUVIndexData(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            android.support.v4.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationServices();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void startLocationServices() {
        try {
            LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        } catch (SecurityException exception) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                } else {
                    // show a dialog saying "I can't locate you without permission, dummy!"
                    Toast.makeText(this, "You denied permissions. Location unavailable.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
