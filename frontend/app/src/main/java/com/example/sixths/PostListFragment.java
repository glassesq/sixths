package com.example.sixths;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostListFragment extends Fragment {

    private static final String LOG_TAG = PostListFragment.class.getSimpleName();
//    private boolean if_test_manager = false;

//    private final CardListAdapter.OnArticleCardClickListener card_listener;

    public PostListFragment() {
        /*card_listener = null;*/
    }

/*    public PostListFragment(CardListAdapter.OnArticleCardClickListener card_listener) {
        this.card_listener = card_listener;
        Log.d(LOG_TAG, "start article list fragment");
    }

    public PostListFragment(CardListAdapter.OnArticleCardClickListener card_listener, boolean test_manager) {
        this.card_listener = card_listener;
        this.if_test_manager = test_manager;
        Log.d(LOG_TAG, "start article list fragment");
    } */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        Log.d(LOG_TAG, "view got");
        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);
        PostListAdapter adapter;
        adapter = new PostListAdapter(view.getContext());
/*        if (if_test_manager) {
            adapter = new CardListAdapter(view.getContext(), card_listener, MainActivity.test_manager);
        } else {
            adapter = new CardListAdapter(view.getContext(), card_listener);
        } */
        Log.d(LOG_TAG, "card list adapter");
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }


}