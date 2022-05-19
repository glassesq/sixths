package com.example.sixths.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixths.R;
import com.example.sixths.service.Article;
import com.example.sixths.service.Service;

import java.net.URI;
import java.util.WeakHashMap;


public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    public interface postListener {
        void gotoUserPage(int userid);
    }

    private postListener listener;

    private static final String LOG_TAG = PostListAdapter.class.getSimpleName();

    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_CLASSIC = 1;

    public Service.POST_LIST_TYPE type = Service.POST_LIST_TYPE.ALL;

    private boolean if_more = true;

    private final LayoutInflater inflater;
//    private final OnArticleCardClickListener card_listener;

    public PostListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.listener = null;
        setType(null);
//        card_listener = _card_listener;
    }

    public PostListAdapter(Context context, postListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        setType(null);
//        card_listener = _card_listener;
    }

    public PostListAdapter(Context context, postListener listener, Service.POST_LIST_TYPE type) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        setType(type);
//        card_listener = _card_listener;
    }

    public void setType(Service.POST_LIST_TYPE type) {
        if (type != null) this.type = type;
        Service.setArticleAdapter(this, this.type);
    }

    @Override
    public int getItemViewType(int position) {
        //if (position == getItemCount() - 1) {
        //    return TYPE_FOOTER;
        //}
        return TYPE_CLASSIC;
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
        public TextView content_view;
        public TextView nickname_view;
        public TextView time_view;
        public TextView username_view;
        public TextView likes_view;
        public TextView comments_view;
        public TextView position_text;
        public TextView title_view;

        public TextView follow_tag;

        public ImageView image_view;
        public ImageView profile_view;
        public ImageView position_icon;

        //        public TextView title_view;
        //        public CardView card_view;
        public PostListAdapter adapter;
        private int view_type;

        public PostViewHolder(View item_view, PostListAdapter _adapter, int view_type) {
            super(item_view);
            this.view_type = view_type;
            adapter = _adapter;
            if (this.view_type == PostListAdapter.TYPE_CLASSIC) {

                content_view = item_view.findViewById(R.id.post_content);

                nickname_view = item_view.findViewById(R.id.post_nickname);
                username_view = item_view.findViewById(R.id.center_username_view);

                time_view = item_view.findViewById(R.id.time_view);

                image_view = item_view.findViewById(R.id.image_view);
                profile_view = item_view.findViewById(R.id.profile_view);

                position_text = item_view.findViewById(R.id.position_text);
                position_icon = item_view.findViewById(R.id.position_icon);

                likes_view = item_view.findViewById(R.id.likes_view);
                comments_view = item_view.findViewById(R.id.comments_view);

                follow_tag = item_view.findViewById(R.id.follow_tag);

                title_view = item_view.findViewById(R.id.post_title);
            }
        }

    }

    @NonNull
    @Override
    public PostListAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = null;
        if (viewType == TYPE_CLASSIC) {
            item_view = inflater.inflate(R.layout.post_simple, parent, false);
        }
/*        } else {
            item_view = inflater.inflate(R.layout.post_simple, parent, false);
        } */
        return new PostViewHolder(item_view, this, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.PostViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CLASSIC) {
            Article article = Service.getArticle(position, type);
            if (article == null) return;
            holder.nickname_view.setText(article.author_nickname);
            holder.username_view.setText(article.author_username);
            holder.content_view.setText(article.content);
            holder.title_view.setText(article.title);
            holder.time_view.setText(article.time);

            if (article.position != null) {
                holder.position_text.setText(article.position);
                holder.position_text.setVisibility(View.VISIBLE);
                holder.position_icon.setVisibility(View.VISIBLE);
            } else {
                holder.position_text.setVisibility(View.INVISIBLE);
                holder.position_icon.setVisibility(View.INVISIBLE);
            }

            if (Service.isFollow(article.author_id)) {
                holder.follow_tag.setVisibility(View.VISIBLE);
            } else {
                holder.follow_tag.setVisibility(View.GONE);
            }

            if (article.author_profile != null && article.profile_fetched) {
//                Bitmap b = Service.getImageBitmap(article.author_profile);
//                if( b != null) holder.profile_view.setImageBitmap(b);
                Uri u = Service.getImageUri(article.author_profile);
                if (u != null) holder.profile_view.setImageURI(u);
            } else {
                holder.profile_view.setImageResource(R.drawable.default_profile);
            }

            if (article.image != null && article.image_fetched) {
                Uri u = Service.getImageUri(article.image);
                if (u != null) {
                    holder.image_view.setImageURI(u);
                    holder.image_view.setVisibility(View.VISIBLE);
                }
            } else {
                holder.image_view.setVisibility(View.GONE);
            }

            holder.likes_view.setText(Service.wrapInt(article.likes));
            holder.comments_view.setText(Service.wrapInt(article.comments));

            // TODO: else
            if (listener != null)
                holder.profile_view.setOnClickListener(view -> listener.gotoUserPage(article.author_id));
        }
//        if (position != getItemCount() - 1) {
//            String title = manager.getByIndex(position).getTitle();
//            holder.title_view.setText(title);
//        String content = manager.getByIndex(position).getContent();
//        holder.content_view.setText("天生我才必有用");
//            String id = manager.getByIndex(position).getId();
//            holder.card_view.setOnClickListener(view -> card_listener.onCardClick(id));

//        } else {
        // if (if_more) {
        //     holder.footer_view.setText("查看更多");
        //     holder.footer_view.setClickable(true);
        //     holder.footer_view.setOnClickListener(view -> showTenMoreArticle());
        // } else {
        //     holder.footer_view.setText("没有更多了");
        //     holder.footer_view.setClickable(false);
        // }
//        Log.d(LOG_TAG, "onBindViewHolder - footer");
//        }
    }

    @Override
    public int getItemCount() {
        return Service.getArticleCount(type);
    }
}

