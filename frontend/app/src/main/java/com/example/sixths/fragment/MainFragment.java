package com.example.sixths.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sixths.R;
import com.example.sixths.adapter.PostPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private TabLayout tablayout;
    private ViewPager2 pager;

//    private final CardListAdapter.OnArticleCardClickListener card_listener;

    public MainFragment() {
//        card_listener = null;
    }

    //   public MainFragment(CardListAdapter.OnArticleCardClickListener listener) {
    //       card_listener = listener;
    //   }

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
