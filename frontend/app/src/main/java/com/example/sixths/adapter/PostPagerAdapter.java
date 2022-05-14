package com.example.sixths.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sixths.fragment.PostListFragment;
import com.example.sixths.service.Service;

public class PostPagerAdapter extends FragmentStateAdapter {
    // private final CardListAdapter.OnArticleCardClickListener card_listener;

    public PostPagerAdapter(@NonNull FragmentActivity fragmentActivity/*, CardListAdapter.OnArticleCardClickListener card_listener */) {
        super(fragmentActivity);
//        this.card_listener = card_listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
//        return new PostListFragment();
        if( position == 0 ) return new PostListFragment(Service.POST_LIST_TYPE.ALL);
        if( position == 1 ) return new PostListFragment(Service.POST_LIST_TYPE.FOLLOW);
        System.out.println("should not go here");
        return null;
//        if (position == 0) return new PostListFragment(card_listener, true);
//        return new ArticleListFragment(card_listener);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
