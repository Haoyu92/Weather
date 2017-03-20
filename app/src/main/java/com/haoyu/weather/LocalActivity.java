package com.haoyu.weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haoyu.weather.model.Weather;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalActivity extends Activity {

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private ImageView imgView;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView press;
    private TextView hum;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView longitude;
    private TextView latitude;
    public SwipeRefreshLayout swipeRefresh;
    private ImageView picImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        String[] Info = intent.getStringArrayExtra("Info");
        final String city = Info[0] + "," + Info[1];

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        cityText = (TextView) findViewById(R.id.cityText);
        condDescr = (TextView) findViewById(R.id.condDescr);
        imgView = (ImageView) findViewById(R.id.condIcon);
        temp = (TextView) findViewById(R.id.temp);

        minTemp = (TextView) findViewById(R.id.min_temp);
        maxTemp = (TextView) findViewById(R.id.max_temp);

        press = (TextView) findViewById(R.id.press);
        hum = (TextView) findViewById(R.id.hum);
        windSpeed = (TextView) findViewById(R.id.wind_speed);
        windDeg = (TextView) findViewById(R.id.wind_degree);

        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);

        picImg = (ImageView) findViewById(R.id.pic_img);

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPic();
                JSONWeatherTask task = new JSONWeatherTask();
                task.execute(new String[]{city});;
            }
        });

        loadPic();

    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getLocalWeatherData(params[0]));

            try {
                weather = JsonWeatherParser.getWeather(data);
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if(weather.location == null){
                Toast.makeText(LocalActivity.this, "Failed to identify city information", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LocalActivity.this, InputActivity.class);
                startActivity(intent);
            }
            else {
                cityText.setText(weather.location.getCity() + " , " + weather.location.getCountry());
                Bitmap bitmap = BitmapFactory.decodeStream(weather.iconData);
                imgView.setImageBitmap(bitmap);
                condDescr.setText("" + weather.currentCondition.getDescr());
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");

                minTemp.setText("" + Math.round(weather.temperature.getMinTemp() - 273.15) + "°C");
                maxTemp.setText("" + Math.round(weather.temperature.getMaxTemp() - 273.15) + "°C");

                press.setText("Atmospheric pressure: " + weather.currentCondition.getPressure() + " Pa");
                hum.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
                windSpeed.setText("Wind speed: " + weather.wind.getSpeed() + " mps");
                windDeg.setText("Wind direction: " + weather.wind.getDeg() + "°");


                longitude.setText("Longitude: " + weather.location.getLongitude());
                latitude.setText("Latitude: " + weather.location.getLatitude());

                swipeRefresh.setRefreshing(false);
            }

        }

    }

    private void loadPic() {

        List<String> list = new ArrayList<>();
        list.add("https://cdn.pixabay.com/photo/2016/12/09/09/22/san-francisco-1893985_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/01/07/20/40/candy-1961536_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/02/19/15/28/italy-2080072_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/02/20/18/03/cat-2083492_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/01/20/15/06/orange-1995056_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/01/19/23/46/panorama-1993645_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/01/16/15/17/hot-air-balloons-1984308_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2017/01/06/20/43/soap-bubble-1958841_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2014/04/07/05/25/gummibarchen-318362_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2016/10/18/08/13/travel-1749508_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2016/10/13/16/40/green-1738220_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2016/03/27/22/22/fox-1284512_960_720.jpg");
        Collections.shuffle(list);
        final String pic = list.get(0);

        //final String pic = "";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(LocalActivity.this).load(pic).into(picImg);
            }
        });

    }

}
