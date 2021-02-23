package com.example.wheatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class weatherData {


    private String mIcon , mCity , mTemperature , mWeatherType;
    private int mCondition;

    public static weatherData fromJson(JSONObject jsonObject){
        try{

            weatherData weatherD = new weatherData();
            weatherD.mCity=jsonObject.getString("name");
            weatherD.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherD.mWeatherType =jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherD.mIcon = updateWeatherIcon(weatherD.mCondition);
            //for temperature
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;   //-273.15 coz it shows in kelvin
            int roundedValue = (int)Math.rint(tempResult); //to show in points

            weatherD.mTemperature = Integer.toString(roundedValue);
            return weatherD;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int mCondition) {

        if (mCondition >= 0 && mCondition <= 300){
            return "thunderStorm" ;
        }
        else if (mCondition >= 300 && mCondition <= 500){
            return "nightrain" ;
        }
        else if (mCondition >= 500 && mCondition <= 600){
            return "rainy" ;
        }else if (mCondition >= 600 && mCondition <= 700){
            return "snow" ;
        }else if (mCondition >= 701 && mCondition <= 771){
            return "breeze" ;
        }else if (mCondition >= 772 && mCondition <= 779 ){
            return "cloudy" ;
        }else if (mCondition == 800){
            return "sunny" ;
        }else if (mCondition >= 801 && mCondition <= 804){
            return "cloudy" ;
        }
        else if (mCondition >= 900 && mCondition <= 902){
            return "storm" ;
        }
        else if (mCondition == 903){
            return "snow" ;
        }else if (mCondition == 904){
            return "sunny" ;
        }
        else if (mCondition >= 905 && mCondition <= 1000){
            return "storm" ;
        }

        return "dunno";

    }

    public String getmTemperature() {
        return mTemperature + "°C";
    }

    public String getmIcon() {
        return mIcon;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }


}
