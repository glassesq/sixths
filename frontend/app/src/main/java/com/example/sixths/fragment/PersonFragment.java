package com.example.sixths.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class PersonFragment extends Fragment {

    private TextView bio_view = null;
    private TextView nickname_view = null;
    private TextView username_view = null;

    public PersonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.person_center, container, false);
        bio_view = view.findViewById(R.id.bio_view);
        username_view = view.findViewById(R.id.center_username_view);
        nickname_view = view.findViewById(R.id.nickname_view);



        return view;
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
    }

}

