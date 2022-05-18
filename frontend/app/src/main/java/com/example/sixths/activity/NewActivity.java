package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sixths.R;
import com.example.sixths.service.Service;

import java.util.List;

public class NewActivity extends AppCompatActivity {
    // 新建post的activity

    boolean enableLocation = false;
    boolean enableImage = false;
    String locationText = null;
    private TextView content_view;
    private LinearLayout position_view;
    private TextView position_text;
    private ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        content_view = findViewById(R.id.content_view);
        position_view = findViewById(R.id.position_view);
        position_text = findViewById(R.id.position_text);
        image_view = findViewById(R.id.new_image_view);

        position_view.setVisibility(View.GONE);
        image_view.setVisibility(View.GONE);
    }

    public void makePost(View view) {
        String content = content_view.getText().toString();
        System.out.println(content);
        Service.makeArticle(content, locationText);
        this.finish();
    }

    public void switchLocation(View view) {
        if (enableLocation) {
            position_view.setVisibility(View.GONE);
            enableLocation = false;
            locationText = null;
        } else {
            Location location = getLocation();
            String address = getAddress(location);
            locationText = address;
            if (address != null) {
                position_view.setVisibility(View.VISIBLE);
                position_text.setText(address);
                enableLocation = true;
            } else {
                Toast.makeText(this, "获取位置失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        /* https://blog.csdn.net/ming54ming/article/details/118853081 */
        Location location = null;
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }

        System.out.println(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        System.out.println(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            return null;
        }
        System.out.println("good here");


        System.out.println("location manager get");
        List<String> providers = locationManager.getProviders(true);
        System.out.println(providers);
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            System.out.println("location:");
            System.out.println(l);
            if (l == null) {
                continue;
            }
            if (location == null || l.getAccuracy() < location.getAccuracy()) { // Found best last known location: %s", l);
                location = l;
            }
        }
        System.out.println(location);
        return location;
    }

    public String getAddress(Location location) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this.getApplicationContext());
        try {
            System.out.println(location.getLatitude());
            System.out.println(location.getLongitude());
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (addressList != null && addressList.size() >= 1) {
            for (Address address : addressList) {
                System.out.println(String.format("address: %s", address.toString()));
            }
            return addressList.get(0).getAddressLine(0);
        }
        return null;
    }


    public void cancel(View view) {
        this.finish();
    }
}