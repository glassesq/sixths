package com.example.sixths.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixths.R;


public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {
    private static final String LOG_TAG = PostListAdapter.class.getSimpleName();

    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_ARTICLE = 1;

    private boolean if_more = true;

    private final LayoutInflater inflater;
//    private final OnArticleCardClickListener card_listener;
//    private final ArticleManager manager;

    public PostListAdapter(Context context/*, OnArticleCardClickListener _card_listener*/) {
        inflater = LayoutInflater.from(context);
//        card_listener = _card_listener;
        //       manager = new ArticleManager();
        //       manager.setAdapter(this);
        Log.d(LOG_TAG, "hi card list adapter");
    }

/*    public PostListAdapter(Context context, OnArticleCardClickListener _card_listener, ArticleManager manager) {
        inflater = LayoutInflater.from(context);
        card_listener = _card_listener;
        this.manager = manager;
        this.manager.setAdapter(this);
        Log.d(LOG_TAG, "hi card list adapter");
    } */

    @Override
    public int getItemViewType(int position) {
        Log.d(LOG_TAG, "getItemViewType" + position);
        //if (position == getItemCount() - 1) {
        //    return TYPE_FOOTER;
        //}
        return TYPE_ARTICLE;
    }

/*    public void setIfMore(boolean arg) {
        if (if_more != arg) {
            if_more = arg;
            this.notifyItemChanged(getItemCount() - 1);
        }
    }*/

    // public void showTenMoreArticle() {
    //     manager.showTenMoreArticle();
    // }

    // public interface OnArticleCardClickListener {
    //     void onCardClick(String id);
    // }

    class PostViewHolder extends RecyclerView.ViewHolder {
        //        public TextView title_view;
        public TextView content_view;
        public TextView nickname_view;
        public TextView username_view;
        //        public CardView card_view;
        public PostListAdapter adapter;
        private int view_type;

        public PostViewHolder(View item_view, PostListAdapter _adapter, int view_type) {
            super(item_view);
            this.view_type = view_type;
            if (this.view_type == PostListAdapter.TYPE_ARTICLE) {
                content_view = item_view.findViewById(R.id.post_content);
                nickname_view = item_view.findViewById(R.id.post_nickname);
                username_view = item_view.findViewById(R.id.post_username);
                adapter = _adapter;
                Log.d(LOG_TAG, "hi view holder");
            }
/*            } else {
//                footer_view = item_view.findViewById(R.id.view_more);
                adapter = _adapter;
                Log.d(LOG_TAG, "hi view holder - footer");
            } */
        }

    }

    @NonNull
    @Override
    public PostListAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view;
    //    if (viewType == TYPE_ARTICLE) {
            item_view = inflater.inflate(R.layout.post_simple, parent, false);
/*        } else {
            item_view = inflater.inflate(R.layout.post_simple, parent, false);
        } */
        return new PostViewHolder(item_view, this, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.PostViewHolder holder, int position) {
//        if (position != getItemCount() - 1) {
//            String title = manager.getByIndex(position).getTitle();
//            holder.title_view.setText(title);
//        String content = manager.getByIndex(position).getContent();
        holder.content_view.setText("天生我才必有用");
//            String id = manager.getByIndex(position).getId();
//            holder.card_view.setOnClickListener(view -> card_listener.onCardClick(id));
        holder.nickname_view.setText("amaza");
        holder.username_view.setText("123847s");
//        } else {
        // if (if_more) {
        //     holder.footer_view.setText("查看更多");
        //     holder.footer_view.setClickable(true);
        //     holder.footer_view.setOnClickListener(view -> showTenMoreArticle());
        // } else {
        //     holder.footer_view.setText("没有更多了");
        //     holder.footer_view.setClickable(false);
        // }
        Log.d(LOG_TAG, "onBindViewHolder - footer");
//        }
    }

    @Override
    public int getItemCount() {
        return 10;
//        return manager.count() + 1; // TODO
    }
}

