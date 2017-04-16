package com.haoyu.weather;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class InputActivity extends Activity {

    private Button confirm;
    private TextView cityName;
    private TextView countryName;
    private Button current;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private int networkStatus;

    public int getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(int networkStatus) {
        this.networkStatus = networkStatus;
    }

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        cityName = (TextView) findViewById(R.id.cityName);
        countryName = (TextView) findViewById(R.id.countryName);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(isConfirm);
        current = (Button) findViewById(R.id.current);
        current.setOnClickListener(isCurrent);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "Location provider is unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener isConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (networkStatus == 0) {
                Toast.makeText(InputActivity.this, "Network is unavailable", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(InputActivity.this, MainActivity.class);
                String[] information = {cityName.getText().toString(), countryName.getText().toString()};
                intent.putExtra("Info", information);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener isCurrent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (networkStatus == 0) {
                Toast.makeText(InputActivity.this, "Network is unavailable", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(InputActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InputActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(InputActivity.this, "Please enable location service in Settings", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    showLocation(location);
                }
                locationManager.requestLocationUpdates(provider, 60000, 100, locationListener);

                Intent intent = new Intent(InputActivity.this, LocalActivity.class);
                String[] information = {String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())};
                intent.putExtra("Info", information);
                startActivity(intent);
            }
        }
    };

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                setNetworkStatus(1);
            } else {
                Toast.makeText(context, "Network is unavailable", Toast.LENGTH_SHORT).show();
                setNetworkStatus(0);
            }
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
        }
    };

    private void showLocation(Location location) {
        String currentPosition = "latitude is " + location.getLatitude() + "\n" + "longitude is " + location.getLongitude();
        Toast.makeText(this, currentPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

}
