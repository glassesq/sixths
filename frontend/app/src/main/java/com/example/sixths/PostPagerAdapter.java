package com.example.sixths;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PostPagerAdapter extends FragmentStateAdapter {
    // private final CardListAdapter.OnArticleCardClickListener card_listener;

    public PostPagerAdapter(@NonNull FragmentActivity fragmentActivity/*, CardListAdapter.OnArticleCardClickListener card_listener */) {
        super(fragmentActivity);
//        this.card_listener = card_listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new PostListFragment();
//        if (position == 0) return new PostListFragment(card_listener, true);
//        return new ArticleListFragment(card_listener);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
