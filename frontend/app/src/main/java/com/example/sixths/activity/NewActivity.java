package com.example.sixths.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sixths.R;
import com.example.sixths.service.Service;
import com.example.sixths.view.AutoMediaController;
import com.example.sixths.view.StretchVideoView;

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

    private ImageView record_button;
    private ImageView camera_button;
    private ImageView video_button;
    private ImageView location_button;

    private StretchVideoView video_view;
    private StretchVideoView audio_view;

    private FrameLayout video_frame;
    private FrameLayout audio_frame;

    private ScrollView scroll_view;

    public boolean isRecording = false;

    private Uri photo_uri;
    private Uri video_uri;
    private Uri audio_uri;
    private String photo_src = null;
    private String video_src = null;
    private String audio_src = null;

    public static int PHOTO = 1;
    public static int VIDEO = 2;
    public static int AUDIO = 3;

    ActivityResultLauncher<Uri> launcher;
    ActivityResultLauncher<Uri> video_launcher;
    MediaRecorder recorder;
    /* https://dolby.io/blog/recording-audio-on-android-with-examples/ */

    AutoMediaController video_controller;
    AutoMediaController audio_controller;

    private Bitmap bitmap;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == PHOTO) {
                successPhoto(msg.getData().getString("data"));
            } else if (msg.what == VIDEO) {
                successVideo(msg.getData().getString("data"));
            } else if (msg.what == AUDIO) {
                successAudio(msg.getData().getString("data"));
            }
        }
    };

    AutoMediaController.Checker videoChecker = new AutoMediaController.Checker() {
        @Override
        public boolean check() {
            return checkVideo();
        }
    };

    AutoMediaController.Checker audioChecker = new AutoMediaController.Checker() {
        @Override
        public boolean check() {
            return checkAudio();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        title_view = findViewById(R.id.title_view);
        content_view = findViewById(R.id.content_view);

        position_view = findViewById(R.id.position_view);
        position_text = findViewById(R.id.delete_tag);

        image_view = findViewById(R.id.new_image_view);
        video_view = findViewById(R.id.video_view);
        audio_view = findViewById(R.id.audio_view);

        video_frame = findViewById(R.id.video_frame);
        audio_frame = findViewById(R.id.audio_frame);

        scroll_view = findViewById(R.id.scroll_view);
//        frame_layout = findViewById(R.id.frame_layout);

        record_button = findViewById(R.id.record_button);
        location_button = findViewById(R.id.location_button);
        camera_button = findViewById(R.id.camera_button);
        video_button = findViewById(R.id.video_button);

        /* hide elements that not using */
        position_view.setVisibility(View.GONE);
        image_view.setVisibility(View.GONE);
        video_view.setVisibility(View.GONE);
        audio_view.setVisibility(View.GONE);

        /* image */
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

        /* video */
        video_controller = new AutoMediaController(this);
        video_controller.setChecker(videoChecker);
        video_launcher = registerForActivityResult(
                new ActivityResultContracts.TakeVideo(), new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap result) {
                        try {
                            System.out.println("video:" + getContentResolver().getType(video_uri));

                            /* first resize, then set controller */
                            /* video view resize */
                            MediaPlayer media = new MediaPlayer();
                            media.setDataSource(getContentResolver().openAssetFileDescriptor(video_uri, "r"));
                            media.prepare();
                            video_view.setDimensions(media.getVideoWidth(), media.getVideoHeight());
                            media.release();

                            /* media controller */
                            video_controller.setMediaPlayer(video_view);
                            video_controller.setAnchorView(video_view);
                            video_view.setMediaController(video_controller);

                            /* button */
                            video_button.setImageResource(R.drawable.ic_video_grey);

                            /* upload video */
                            InputStream is = getContentResolver().openInputStream(video_uri);
                            Service.uploadVideo(VIDEO, handler, is);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        /* audio */
        audio_controller = new AutoMediaController(this);
        audio_controller.setChecker(audioChecker);

/*        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        }); */
    }

    public boolean checkVideo() {
        System.out.println("check video");
        Rect scrollBounds = new Rect();
        scroll_view.getDrawingRect(scrollBounds);

        float top = video_frame.getY();
        float bottom = top + video_frame.getHeight();
        System.out.println(scrollBounds.top + " " + scrollBounds.bottom);
        System.out.println(top + " " + bottom);

        /* if fully visible */
        return scrollBounds.top <= top && scrollBounds.bottom >= bottom;
    }

    public boolean checkAudio() {
        System.out.println("check audio");
        Rect scrollBounds = new Rect();
        scroll_view.getDrawingRect(scrollBounds);

        float top = audio_frame.getY();
        float bottom = top + audio_frame.getHeight();
        System.out.println(scrollBounds.top + " " + scrollBounds.bottom);
        System.out.println(top + " " + bottom);


        /* if fully visible */
        return scrollBounds.top <= top && scrollBounds.bottom >= bottom;
    }

    public void capturePhoto(View view) {
        if( photo_src == null ) {
            File file = Service.makeEmptyFile("image", "jpeg");
            if (file == null) return;

            camera_button.setImageResource(R.drawable.ic_camera_red);

            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            launcher.launch(uri);
            photo_uri = uri;
        } else {
            camera_button.setImageResource(R.drawable.ic_camera_grey);
            photo_src = null;
            photo_uri = null;
            image_view.setVisibility(View.GONE);
            Toast.makeText(this, "移除图片", Toast.LENGTH_SHORT).show();
        }
    }


    public void recordVideo(View view) {
        if( video_src == null ) {
            File file = Service.makeEmptyFile("video", "video");
            if (file == null) return;

            video_button.setImageResource(R.drawable.ic_video_red);

            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            video_uri = uri;
            video_launcher.launch(uri);
        } else {
            video_button.setImageResource(R.drawable.ic_video_grey);
            video_src = null;
            video_uri = null;
            video_view.setVisibility(View.GONE);
            Toast.makeText(this, "移除视频", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchRecord(View view) {
        System.out.println("switch record");
        if (!isRecording) {
            recordAudio();
        } else {
            stopRecordAudio();
        }
    }

    public void recordAudio() {
        try {
            if( audio_src == null ) {
                recorder = new MediaRecorder();
                File file = Service.makeEmptyFile("audio", "audio");

                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
                audio_uri = uri;

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFile(file);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // needed.
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.prepare();
                recorder.start();
                isRecording = true;
                record_button.setImageResource(R.drawable.ic_record_red);
            } else {
                record_button.setImageResource(R.drawable.ic_record_grey);
                audio_view.setVisibility(View.GONE);
                audio_src = null;
                audio_uri = null;
                Toast.makeText(this, "移除音频", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecordAudio() {
        try {
            recorder.stop();
            recorder.release();

            isRecording = false;
            record_button.setImageResource(R.drawable.ic_record_grey);

            audio_controller.setMediaPlayer(audio_view);
            audio_controller.setAnchorView(audio_view);
            audio_view.setAudio(true);
            audio_view.setMediaController(audio_controller);

            InputStream is = getContentResolver().openInputStream(audio_uri);
            Service.uploadAudio(AUDIO, handler, is);

            recorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makePost(View view) {
        String content = content_view.getText().toString();
        String title = title_view.getText().toString();
        String image = photo_src;

        String video = video_src;
        String audio = audio_src;

        System.out.println(content);
        Service.makeArticle(content, locationText, title, image, video, audio);
        this.finish();
    }

    public void switchLocation(View view) {
        if (enableLocation) {
            location_button.setImageResource(R.drawable.ic_location);
            position_view.setVisibility(View.GONE);
            enableLocation = false;
            locationText = null;
        } else {
            location_button.setImageResource(R.drawable.ic_location_red);
            Location location = getLocation();
            String address = getAddress(location);
            locationText = address;
            if (address != null) {
                position_view.setVisibility(View.VISIBLE);
                position_text.setText(address);
                enableLocation = true;
                location_button.setImageResource(R.drawable.ic_location_blue);
            } else {
                location_button.setImageResource(R.drawable.ic_location);
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

    public void successPhoto(String src) {
        if (src == null) {
            Toast.makeText(this, "上传照片失败", Toast.LENGTH_SHORT).show();
            image_view.setVisibility(View.GONE);
            photo_uri = null;
            photo_src = null;
            return;
        }
        image_view.setImageURI(photo_uri);
        image_view.setVisibility(View.VISIBLE);

        camera_button.setImageResource(R.drawable.ic_camera);

        photo_src = src;

        System.out.println("photo set ok");
    }

    public void successVideo(String src) {
        if (src == null) {
            Toast.makeText(this, "上传视频失败", Toast.LENGTH_SHORT).show();
            video_view.setVisibility(View.GONE);
            video_uri = null;
            video_src = null;
            return;
        }
        video_view.setVideoURI(video_uri);
//        video_view.setVideoURI(Service.getResourceUri(video_src));
        video_view.setVisibility(View.VISIBLE);
        video_view.start();

        video_button.setImageResource(R.drawable.ic_video);

        video_src = src;

        System.out.println("video set ok");
    }

    public void successAudio(String src) {
        if (src == null) {
            Toast.makeText(this, "上传音频失败", Toast.LENGTH_SHORT).show();
            audio_view.setVisibility(View.GONE);
            audio_uri = null;
            audio_src = null;
            return;
        }
        audio_src = src;

        audio_view.setVideoURI(audio_uri);
        audio_view.setVisibility(View.VISIBLE);

        audio_view.start();
        record_button.setImageResource(R.drawable.ic_record);

        System.out.println("audio set ok");
    }

    public void stopAll() {
        video_view.pause();
        audio_view.pause();
    }

    public void cancel(View view) {
        this.finish();
    }
}