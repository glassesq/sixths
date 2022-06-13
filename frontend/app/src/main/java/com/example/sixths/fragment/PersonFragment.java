package com.example.sixths.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class PersonFragment extends Fragment {

    private TextView bio_view = null;
    private TextView nickname_view = null;
    private TextView username_view = null;

    private TextView normal_noti;
    private TextView shock_noti;

    private ImageView image_view = null;

    public PersonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_center, container, false);
        bio_view = view.findViewById(R.id.bio_view);
        username_view = view.findViewById(R.id.follow_username);
        nickname_view = view.findViewById(R.id.username_set);
        image_view = view.findViewById(R.id.user_profile_view);
        normal_noti = view.findViewById(R.id.normal_noti);
        shock_noti = view.findViewById(R.id.shock_noti);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        fresh();
    }

    public void fresh() {
        bio_view.setText(Service.myself.bio);
        username_view.setText(Service.myself.name);
        nickname_view.setText(Service.myself.nickname);
        if (Service.myself.profile_fetched) freshProfile();
        else {
            Service.fetchImage(Service.myself);
        }
    }

    public void freshProfile() {
        if (Service.myself.profile == null) return;
        Uri u = Service.getResourceUri(Service.myself.profile);
        if (u != null) {
            image_view.setImageURI(u);
            image_view.setVisibility(View.VISIBLE);
        }
    }

    public void freshNoti() {
        if (Service.notiUncheck()) {
            normal_noti.setVisibility(View.GONE);
            shock_noti.setVisibility(View.VISIBLE);
        } else {
            normal_noti.setVisibility(View.VISIBLE);
            shock_noti.setVisibility(View.GONE);
        }
    }

}

