package com.example.sixths.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sixths.R;
import com.example.sixths.service.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class NewActivity extends AppCompatActivity {
    // 新建post的activity

    boolean enableLocation = false;
    boolean enableImage = false;
    String locationText = null;

    private TextView content_view;
    private TextView title_view;
    private LinearLayout position_view;
    private TextView position_text;
    private ImageView image_view;

    private Uri photo_uri;
    private String photo_src = null;

    public static int PHOTO = 1;
    public static int UPLOAD_PHOTO = 1;

    ActivityResultLauncher<Uri> launcher;

    private Bitmap bitmap;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == PHOTO) {
                photo_src = msg.getData().getString("data");

                // Uri u = Service.getImageUri(photo_src);
                image_view.setImageURI(photo_uri);
                image_view.setVisibility(View.VISIBLE);
                // TODO: delete photo_uri
                // image_view.setImageURI(photo_uri);
                System.out.println("set ok");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        content_view = findViewById(R.id.content_view);
        position_view = findViewById(R.id.position_view);
        position_text = findViewById(R.id.position_text);

        title_view = findViewById(R.id.title_view);

        image_view = findViewById(R.id.new_image_view);

        position_view.setVisibility(View.GONE);
        image_view.setVisibility(View.GONE);


        launcher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            System.out.println("picture taken");
                            try {
                                InputStream is = getContentResolver().openInputStream(photo_uri);
                                Service.uploadImage(PHOTO, handler, is, getContentResolver().getType(photo_uri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

        );
    }

    public void capturePhoto(View view) {
        System.out.println("capture");
        File file = Service.makeEmptyFile("image", "jpeg");
        if (file == null) return;
        System.out.println("capture: " + file.getName());
        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        System.out.println("capture: " + uri);
        launcher.launch(uri);
        System.out.println("capture: " + uri);
        photo_uri = uri;
    }

    public void makePost(View view) {
        String content = content_view.getText().toString();
        String title = title_view.getText().toString();
        String image = photo_src;
        System.out.println(content);
        Service.makeArticle(content, locationText, title, image);
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