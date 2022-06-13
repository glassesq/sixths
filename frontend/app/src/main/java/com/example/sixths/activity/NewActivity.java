package com.example.sixths.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.BitmapCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sixths.R;
import com.example.sixths.service.Article;
import com.example.sixths.service.Service;
import com.example.sixths.view.AutoMediaController;
import com.example.sixths.view.StretchVideoView;
import com.example.sixths.view.customTakePicture;
import com.example.sixths.view.customVideo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;


public class NewActivity extends AppCompatActivity {
    // 新建post的activity

    public int article_id = -1;
    public boolean media_init = false;
    public Article article = null;

    public boolean post_made = false;

    boolean enableLocation = false;
    String locationText = null;
    private Location realLocation = null;

    private TextView content_view;
    private TextView title_view;
    private LinearLayout position_view;
    private TextView position_text;
    private ImageView image_view;

    private ImageView record_button;
    private ImageView camera_button;
    private ImageView video_button;
    private ImageView location_button;

    private ImageView profile_view;

    public ImageView delete_button;

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
    public static int ARTICLE_SUCCESS = 4;
    public static int ARTICLE_RESOURCE = 5;
    public static int ARTICLE_DELETE = 6;
    public static int DRAFT = 7;
    public static int SUCCESS_LOCATION = 8;
    public static int FAIL_LOCATION = 9;

    private LocationManager locationManager;

    ActivityResultLauncher<Uri> launcher;
    ActivityResultLauncher<Uri> video_launcher;
    MediaRecorder recorder;
    /* https://dolby.io/blog/recording-audio-on-android-with-examples/ */

    AutoMediaController video_controller;
    AutoMediaController audio_controller;

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
            } else if (msg.what == ARTICLE_SUCCESS) {
                loadArticle(msg.getData().getString("data"));
            } else if (msg.what == ARTICLE_RESOURCE) {
                loadArticleResource();
            } else if (msg.what == ARTICLE_DELETE) {
                successDelete();
            } else if (msg.what == DRAFT) {
                Toast.makeText(NewActivity.this.getApplicationContext(),
                        "草稿暂存成功", Toast.LENGTH_SHORT).show();
                article_id = Integer.parseInt(msg.getData().getString("data"));
            } else if (msg.what == SUCCESS_LOCATION) {
                successLocation();
            } else if (msg.what == FAIL_LOCATION) {
                failLocation();
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

    protected final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) NewActivity.this.realLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private ActivityResultLauncher<Intent> draft_launcher;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Service.setNewHandler(handler);

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

        delete_button = findViewById(R.id.delete_button);

        profile_view = findViewById(R.id.user_profile_view);
        Uri u = Service.getResourceUri(Service.myself.profile);
        if (u != null) {
            profile_view.setImageURI(u);
            profile_view.setVisibility(View.VISIBLE);
        }

        /* image */
        launcher = registerForActivityResult(
                new customTakePicture(),
//                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            System.out.println("picture taken");
                            try {
                                InputStream is = getContentResolver().openInputStream(photo_uri);
                                Bitmap bit = Service.getBitmap(is);

                                ByteArrayOutputStream baos = null;
                                int x = 10;
                                while (x >= 1 && (baos == null || baos.toByteArray().length >= 1024 * 50)) {
                                    baos = new ByteArrayOutputStream();
                                    bit.compress(Bitmap.CompressFormat.JPEG, x, baos);
                                    x = x / 2;
                                }
                                byte[] bytes = baos.toByteArray();

                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                image_view.setImageBitmap(bitmap);

                                InputStream is2 = new ByteArrayInputStream(bytes);
                                Service.uploadImage(PHOTO, handler, is2, getContentResolver().getType(photo_uri));
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
                new customVideo(), new ActivityResultCallback<Bitmap>() {
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

        locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        article_id = getIntent().getIntExtra("article_id", -1);
        if (article_id != -1) {
            Service.getArticleInfo(article_id);
            System.out.println("article_id got here" + article_id);
            delete_button.setVisibility(View.VISIBLE);
        } else {
            delete_button.setVisibility(View.GONE);
        }

        draft_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        System.out.println("hello activity result");
                        System.out.println(result.getResultCode());
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            System.out.println("hello activity result");
                            Intent data = result.getData();
                            if (data == null) return;
                            article_id = data.getIntExtra("article_id", -1);
                            if (article_id != -1) {
                                delete_button.setVisibility(View.VISIBLE);
                                Service.getArticleInfo(article_id);
                            } else {
                                delete_button.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        scroll_view.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                audio_controller.hide();
                video_controller.hide();
            }
        });
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
        if (photo_src == null) {
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
        if (video_src == null) {
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
            if (audio_src == null) {
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
        post_made = true;
        String content = content_view.getText().toString();
        String title = title_view.getText().toString();
        String image = photo_src;

        String video = video_src;
        String audio = audio_src;

        System.out.println(content);
        this.finish();
        Service.makeArticle(article_id, content, locationText, title, image, video, audio);
    }

    public void makeDraft(View view) {
        String content = content_view.getText().toString();
        String title = title_view.getText().toString();
        String image = photo_src;

        String video = video_src;
        String audio = audio_src;

        System.out.println(content);
        Service.makeDraft(article_id, content, locationText, title, image, video, audio);
        delete_button.setVisibility(View.VISIBLE);
    }

    public void switchLocation(View view) {
        if (enableLocation) {
            location_button.setImageResource(R.drawable.ic_location);
            position_view.setVisibility(View.GONE);
            enableLocation = false;
            locationText = null;
        } else {
            System.out.println("try get location here");
            location_button.setImageResource(R.drawable.ic_location_red);
            getLocation();
        }
    }

    private void successLocation() {
        position_view.setVisibility(View.VISIBLE);
        position_text.setText(locationText);
        enableLocation = true;
        location_button.setImageResource(R.drawable.ic_location_blue);
    }

    private void failLocation() {
        location_button.setImageResource(R.drawable.ic_location);
        Toast.makeText(this, "获取位置失败", Toast.LENGTH_SHORT).show();
    }

    private void getLocation() {
        Thread thread = new Thread(() -> {
            /* https://blog.csdn.net/ming54ming/article/details/118853081 */
            Location location = realLocation;

            if (locationManager == null) {
                return;
            }

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                return;
            }
            System.out.println("good here");

            System.out.println("location manager get");
            List<String> providers = locationManager.getProviders(true);
            System.out.println(providers);

            for (String provider : providers) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (location == null || l.getAccuracy() < location.getAccuracy()) { // Found best last known location: %s", l);
                    location = l;
                }
            }
            if( location == null ) {
                Service.sendMessage(handler, FAIL_LOCATION);
            }
            DecimalFormat format = new DecimalFormat("0.00");
            locationText = "经度：" + format.format(location.getLongitude()) +
                    " 纬度:" + format.format(location.getLatitude());
            String address = getAddress(location);
            if (address != null) {
                locationText = address;
            }
            if (locationText != null) {
                Service.sendMessage(handler, SUCCESS_LOCATION);
            } else {
                Service.sendMessage(handler, FAIL_LOCATION);
            }
        });
        thread.start();
    }

    public String getAddress(Location location) {
        try {
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
                return addressList.get(0).getAddressLine(0);
            }
        } catch (Exception ignored) {
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

    public void gotoDraft(View view) {
        Intent intent = new Intent(NewActivity.this, DraftActivity.class);
        draft_launcher.launch(intent);
// You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    }

    public void loadArticle(String str) {
        article = Service.decodeArticle(str);
        media_init = false;
        Service.fetchImage(article);
        Service.fetchMedia(article);

        if (article == null) return;

        title_view.setText(article.title);
        content_view.setText(article.content);

        if (article.position != null) {
            position_view.setVisibility(View.VISIBLE);
            position_text.setText(article.position);
            enableLocation = true;
            location_button.setImageResource(R.drawable.ic_location_blue);
        }
    }

    public void loadArticleResource() {
        try {
            if (article == null) return;

            System.out.print("media_init:");
            System.out.println(media_init);
            if (media_init) return;
            if (article.image_fetched && article.video_fetched && article.audio_fetched)
                media_init = true;
            else {
                Service.fetchImage(article);
                Service.fetchMedia(article);
                return;
            }

            /* image */
            System.out.println(article.image);
            if (article.image != null && article.image_fetched) {
                Uri u = Service.getResourceUri(article.image);
                if (u != null) {
                    photo_uri = u;
                    image_view.setImageURI(photo_uri);
                    successPhoto(article.image);
                }
            } else {
                camera_button.setImageResource(R.drawable.ic_camera_grey);
                image_view.setVisibility(View.GONE);
            }


            /* audio */
            System.out.println("resource fetched");
            System.out.println(article.audio);
            System.out.println(article.audio_fetched);
            if (article.audio != null && article.audio_fetched) {
                Uri u = Service.getResourceUri(article.audio);
                System.out.println("audio fetched");
                if (u != null) {
                    audio_uri = u;
                    successAudio(article.audio);
                }
            } else {
                record_button.setImageResource(R.drawable.ic_record_grey);
                audio_view.setVisibility(View.GONE);
            }

            /* video */
            System.out.println(article.video);
            System.out.println(article.video_fetched);
            if (article.video != null && article.video_fetched) {
                Uri u = Service.getResourceUri(article.video);
                if (u != null) {
                    video_uri = u;
                    successVideo(article.video);
                }
            } else {
                video_button.setImageResource(R.drawable.ic_video_grey);
                video_view.setVisibility(View.GONE);
            }

            System.out.print("media_init done:");
            System.out.println(media_init);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDraft(View view) {
        if (article_id <= -1) return;
        post_made = true;
        Toast.makeText(NewActivity.this.getApplicationContext(), "删除草稿成功", Toast.LENGTH_SHORT).show();
        Service.deleteDraft(article_id);
        this.finish();
    }

    public void successDelete() {
        Toast.makeText(this.getApplicationContext(), "删除草稿成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void cancel(View view) {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Service.setNewHandler(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Service.setNewHandler(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Service.setNewHandler(handler);
        if (!post_made) {
            if (photo_src != null || audio_src != null || video_src != null || enableLocation) {
                makeDraft(null);
            } else if (title_view.getText().toString().length() > 0 || content_view.getText().toString().length() > 0) {
                System.out.println("#" + title_view.getText().toString().length() + "#");
                System.out.println("#" + content_view.getText().toString().length() + "#");
                makeDraft(null);
            } else {
                Toast.makeText(this.getApplicationContext(), "不自动保存空内容草稿", Toast.LENGTH_SHORT).show();
            }
        }
    }
}