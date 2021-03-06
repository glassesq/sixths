package com.example.sixths.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sixths.R;
import com.example.sixths.activity.MainActivity;
import com.example.sixths.activity.WelcomeActivity;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.adapter.PostPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private TabLayout tablayout;
    private ViewPager2 pager;

    private PostListAdapter.postListener listener;

    public void setListener(PostListAdapter.postListener listener) {
        this.listener = listener;
    }

    public MainFragment() {
//        card_listener = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allpost_view, container, false);
        tablayout = view.findViewById(R.id.main_tablayout);
        pager = view.findViewById(R.id.main_pager);
        if (this.getActivity() != null) {
            PostPagerAdapter pager_adapter = new PostPagerAdapter(this.getActivity());
            pager_adapter.setListener(listener);
            pager.setAdapter(pager_adapter);
            TabLayoutMediator mediator = new TabLayoutMediator(tablayout, pager,
                    (tab, position) -> {
                        if (position == 0) tab.setText("所有人");
                        else tab.setText("已关注");
                    });
            mediator.attach();
        } else {
            Log.e(LOG_TAG, "this.activity() is null");
        }
        return view;
    }

}
