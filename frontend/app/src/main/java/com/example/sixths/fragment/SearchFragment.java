package com.example.sixths.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sixths.R;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.service.Service;
import com.google.android.material.tabs.TabLayout;

public class SearchFragment extends Fragment {


    public TextView search_view;
    public ImageView search_button;

    public RecyclerView recycler_view;

    private PostListAdapter.postListener listener;

    public void setListener(PostListAdapter.postListener listener) {
        this.listener = listener;
    }

    public SearchFragment() {
//        card_listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
        search_view = view.findViewById(R.id.search_view);
        search_button = view.findViewById(R.id.search_button);
        search_button.setOnClickListener(_view -> search());


        recycler_view = view.findViewById(R.id.search_recycler_view);

        /* 设计 recycle view 的 adapter */
        PostListAdapter adapter = new PostListAdapter(this.getContext(), listener, Service.POST_LIST_TYPE.SEARCH); // TODO
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext()));


        Service.clearSearch();
        // TODO
        return view;
    }

    public void search() {
        String text = search_view.getText().toString();
        Service.search(text);
    }


}
