package com.example.wheatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String APP_ID = "3769c545e4cb31b5f58654b82c763102";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    //1000 is 1 mtr
    final float MIN_DIS = 1000;
    final int REQ_CODE = 101;


    String Location_Provider = LocationManager.GPS_PROVIDER;

    ImageView mweatherIcon;
    TextView mtemperature, mweatherCondition, mcityName;
    Button mcityFinder;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mweatherCondition = findViewById(R.id.weatherCondition);
        mtemperature = findViewById(R.id.temperature);
        mcityName = findViewById(R.id.cityName);

        mweatherIcon = findViewById(R.id.weatherIcon);
        mcityFinder = findViewById(R.id.cityFinder);


        mcityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cityFInder.class);
                startActivity(intent);
            }
        });


    }

    /*@Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    } */


    //this on resume method automatically run and chek condition is right or not
    @Override
    protected void onResume() {
        super.onResume();

        Intent mIntent = getIntent();
        String city = mIntent.getStringExtra("City");


        //user entered any city name
        if(city != null){
            getWeatherForNewLocation(city);
        }
        else{
            getWeatherForCurrentLocation();
        }

    }

    private void getWeatherForNewLocation(String city) {

        RequestParams  params = new RequestParams();
        params.put("q" , city);
        params.put("appid" , APP_ID);
        letsDoSomeNetworking(params);
    }


    private void getWeatherForCurrentLocation() {


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());


                RequestParams params= new RequestParams();
                params.put("lat" , Latitude);
                params.put("lon" , Longitude);
                params.put("appid" , APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Toast.makeText(MainActivity.this, "Turn on Location", Toast.LENGTH_SHORT).show();

            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DIS, mLocationListener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        //if user allow the location or not
        if (requestCode == REQ_CODE){

            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "LocationGet Successfully", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else{
                // user denied the permission
            }

        }
    }

    private void letsDoSomeNetworking(RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL , params , new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(MainActivity.this, "Data Get Success", Toast.LENGTH_SHORT).show();


                        weatherData weatherD = weatherData.fromJson(response);
                        updateUI(weatherD);
                        
                        //super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        //super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }
        );
    }

    private void updateUI(weatherData weatherD) {

        mtemperature.setText(weatherD.getmTemperature());
        mcityName.setText(weatherD.getmCity());
        mweatherCondition.setText(weatherD.getmWeatherType());
        int resourceID = getResources().getIdentifier(weatherD.getmIcon(), "drawable" , getPackageName());
        //set the image
        mweatherIcon.setImageResource(resourceID);
    }


    // we dont want to tell location& weather again and again
    @Override
    protected void onPause() {

        super.onPause();
        if (mLocationManager!= null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
