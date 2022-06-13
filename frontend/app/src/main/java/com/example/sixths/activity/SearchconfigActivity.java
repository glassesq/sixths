package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class SearchconfigActivity extends AppCompatActivity {

    public SwitchCompat switch_text;
    public SwitchCompat switch_image;
    public SwitchCompat switch_audio;
    public SwitchCompat switch_video;

    public TextView type_view;
    public Service.SEARCH_TYPE search_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchconfig);

        switch_text = findViewById(R.id.switch_sort);
        switch_image = findViewById(R.id.switch_image);
        switch_audio = findViewById(R.id.switch_audio);
        switch_video = findViewById(R.id.switch_video);

        int filter = Service.getSearchFilter();
        switch_text.setChecked((filter & Service.FILTER_TEXT) != 0);
        switch_image.setChecked((filter & Service.FILTER_IMAGE) != 0);
        switch_audio.setChecked((filter & Service.FILTER_AUDIO) != 0);
        switch_video.setChecked((filter & Service.FILTER_VIDEO) != 0);

        type_view = findViewById(R.id.type_view);
        setType(Service.getSearchType());
    }

    public void setType(Service.SEARCH_TYPE type) {
        this.search_type = type;
        if (type == Service.SEARCH_TYPE.TITLE) {
            type_view.setText("动态标题");
        }
        else if (type == Service.SEARCH_TYPE.USER) {
            type_view.setText("用户昵称");
        }
        else type_view.setText("动态内容");
    }

    public void cancel(View view) {
        finish();
    }

    public void submit(View view) {
        Service.setSearchConfig(switch_text.isChecked(), switch_image.isChecked(),
                switch_audio.isChecked(), switch_video.isChecked(), search_type);
        finish();
    }

    public void changeType(View view) {
        if (search_type == Service.SEARCH_TYPE.CONTENT) {
            setType(Service.SEARCH_TYPE.TITLE);
        } else if (search_type == Service.SEARCH_TYPE.TITLE) {
            setType(Service.SEARCH_TYPE.USER);
        } else if (search_type == Service.SEARCH_TYPE.USER) {
            setType(Service.SEARCH_TYPE.CONTENT);
        }
    }
}