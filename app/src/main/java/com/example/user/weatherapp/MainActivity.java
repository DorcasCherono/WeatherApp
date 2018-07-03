package com.example.user.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView icon;
    TextView temperature, summary, time, locationText;
    ProgressBar progressBar;
    Toolbar toolbar;

    private FusedLocationProviderClient mFusedLocationClient;

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mResultReceiver = new AddressResultReceiver(null);

        icon = findViewById(R.id.icon);
        temperature = findViewById(R.id.temperature);
        summary = findViewById(R.id.summary);
        progressBar = findViewById(R.id.progress);
        time = findViewById(R.id.time);
        toolbar = findViewById(R.id.toolbar);
        locationText = findViewById(R.id.location);

        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.refresh:
                        progressBar.setVisibility(View.VISIBLE);
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        progressBar.setVisibility(View.GONE);
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            // Logic to handle location object
                                            mLastLocation = location;
                                            // Start service and update UI to reflect new location
                                            startIntentService();

                                            locationText.setText(String.format(Locale.getDefault(), "%s, %s", location.getLatitude(), location.getLongitude()));
                                            new FetchWeather(location.getLatitude(), location.getLongitude()).execute();
                                        } else {
                                            locationText.setText("Failed to get location.");
                                        }
                                    }
                                });
                        break;

                    case R.id.history:
                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                        startActivity(intent);
                        //Toast.makeText(MainActivity.this, "History", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        progressBar.setVisibility(View.GONE);
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mLastLocation = location;
                            // Start service and update UI to reflect new location
                            startIntentService();

                            locationText.setText(String.format(Locale.getDefault(), "%s, %s", location.getLatitude(), location.getLongitude()));
                            new FetchWeather(location.getLatitude(), location.getLongitude()).execute();
                        } else {
                            locationText.setText("Failed to get location.");
                        }
                    }
                });
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    class FetchWeather extends AsyncTask<Void, Void, String> {

        private static final String WEATHER_URL = "https://api.darksky.net/forecast/6b908d36e73b53299adcd9957dd194a9/%s,%s?units=si&exclude=minutely,hourly,daily,alerts,flags";
        private double latitude;
        private double longitude;

        FetchWeather(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(String.format(Locale.getDefault(), WEATHER_URL, latitude, longitude));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                Log.d("MainActivity", "Opening " + WEATHER_URL);

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("MainActivity", "Response: \n" + response);

            progressBar.setVisibility(View.GONE);
            if (response != null) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject currently = jsonResponse.getJSONObject("currently");

                    switch (currently.getString("icon")) {
                        case "clear-day":
                            icon.setImageResource(R.drawable.ic_clear_day);
                            break;

                        case "clear-night":
                            icon.setImageResource(R.drawable.ic_clear_night);
                            break;

                        case "rain":
                            icon.setImageResource(R.drawable.ic_rain);
                            break;

                        case "snow":
                            icon.setImageResource(R.drawable.ic_snow);
                            break;

                        case "sleet":
                            icon.setImageResource(R.drawable.ic_sleet);
                            break;

                        case "wind":
                            icon.setImageResource(R.drawable.ic_wind);
                            break;

                        case "fog":
                            icon.setImageResource(R.drawable.ic_fog);
                            break;

                        case "cloudy":
                            icon.setImageResource(R.drawable.ic_cloudy);
                            break;

                        case "partly-cloudy-day":
                            icon.setImageResource(R.drawable.ic_cloudy_day);
                            break;

                        case "partly-cloudy-night":
                            icon.setImageResource(R.drawable.ic_cloudy_night);
                            break;

                        case "hail":
                            icon.setImageResource(R.drawable.ic_hail);
                            break;

                        case "thunderstorm":
                            icon.setImageResource(R.drawable.ic_thunderstorm);
                            break;

                        case "tornado":
                            icon.setImageResource(R.drawable.ic_tornado);
                            break;
                    }

                    temperature.setText(currently.getDouble("temperature") + " °C");
                    summary.setText(currently.getString("summary"));

                    long jsonTime = currently.getLong("time");
                    String stringTime = new SimpleDateFormat("EEEEEEEEEE MMMMMMMMMM, yyyy 'at' HH:mm", Locale.getDefault()).format(new Date(jsonTime * 1000));

                    time.setText(stringTime);

                    new Summary(latitude, longitude, currently.getDouble("temperature"), currently.getString("summary"), currently.getLong("time")).save();
                } catch (JSONException e) {
                    Log.e("MainActivity", e.getMessage(), e);
                }
            } else {
                temperature.setText("Error");
                summary.setText("Error");
                time.setText("Error");
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locationText.setText(resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY));
                }
            });

            /*if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }*/

        }
    }
}

