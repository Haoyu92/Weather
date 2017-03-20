package com.haoyu.weather;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by haoyu on 3/5/17.
 */

public class WeatherHttpClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String IMG_URL = "https://openweathermap.org/img/w/";


    public String getWeatherData(String location) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + location + "&appid=d44195ccaa0bd1ff912fadec27f8c41f")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public InputStream getImage(String code) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()

                .url(IMG_URL + code + ".png")
                //.url("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLocalWeatherData(final String location) {

        String s = new String(location);
        String a[] = s.split(",");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?" + "lat=" + a[0] + "&lon=" + a[1] + "&appid=d44195ccaa0bd1ff912fadec27f8c41f")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}

