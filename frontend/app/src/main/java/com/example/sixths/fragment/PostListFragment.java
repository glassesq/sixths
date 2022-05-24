package com.example.sixths.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sixths.R;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.service.Service;

public class PostListFragment extends Fragment {

    public Service.POST_LIST_TYPE type;

    public PostListAdapter.postListener listener;

    public PostListFragment() {
    }

    public void setListner(PostListAdapter.postListener listener) {
        this.listener = listener;
    }

    public PostListFragment(Service.POST_LIST_TYPE type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);

        /* 设计 recycle view 的 adapter */
        PostListAdapter adapter = new PostListAdapter(view.getContext(), listener, type);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext()));

        /* 从后端获取信息 */
        Service.fetchArticle(adapter.type);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}