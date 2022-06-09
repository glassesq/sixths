package com.example.sixths.adapter;

import android.content.Context;
import android.net.Uri;
import android.net.sip.SipSession;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixths.R;
import com.example.sixths.service.Article;
import com.example.sixths.service.Service;


public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    public interface postListener {
        void gotoUserPage(int userid);

        void switchLike(int article_id);

        void gotoArticlePage(int article_id);

        void shareArticle(String str);
    }

    private postListener listener;

    private static final String LOG_TAG = PostListAdapter.class.getSimpleName();

    public static final int TYPE_DRAFT = 0;
    public static final int TYPE_CLASSIC = 1;

    public Service.POST_LIST_TYPE type = Service.POST_LIST_TYPE.ALL;

    private boolean if_more = true;

    private final LayoutInflater inflater;
//    private final OnArticleCardClickListener card_listener;

    public PostListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.listener = null;
        setType(null);
    }

    public PostListAdapter(Context context, postListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        setType(null);
    }

    public PostListAdapter(Context context, postListener listener, Service.POST_LIST_TYPE type) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        setType(type);
        if( type == Service.POST_LIST_TYPE.DRAFT) System.out.println("draft set ok");
    }

    public void setType(Service.POST_LIST_TYPE type) {
        if (type != null) this.type = type;
        Service.setArticleAdapter(this, this.type);
    }

    @Override
    public int getItemViewType(int position) {
        if (type == Service.POST_LIST_TYPE.DRAFT) return TYPE_DRAFT;
        return TYPE_CLASSIC;
    }

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
        public TextView image_tag;
        public TextView audio_tag;
        public TextView video_tag;

        public ImageView image_view;
        public ImageView profile_view;
        public ImageView position_icon;

        public ImageView like_icon;
        public ImageView comment_icon;
        public ImageView share_icon;

        public LinearLayout touch_area;

        public LinearLayout multi_line;

        public PostListAdapter adapter;
        private int view_type;

        public PostViewHolder(View item_view, PostListAdapter _adapter, int view_type) {
            super(item_view);
            this.view_type = view_type;
            adapter = _adapter;
            if (this.view_type == PostListAdapter.TYPE_CLASSIC) {

                content_view = item_view.findViewById(R.id.post_content);

                nickname_view = item_view.findViewById(R.id.follow_nickname);
                username_view = item_view.findViewById(R.id.follow_username);

                time_view = item_view.findViewById(R.id.time_view);

                image_view = item_view.findViewById(R.id.image_view);
                profile_view = item_view.findViewById(R.id.user_profile_view);

                position_text = item_view.findViewById(R.id.delete_tag);
                position_icon = item_view.findViewById(R.id.position_icon);

                likes_view = item_view.findViewById(R.id.likes_view);
                like_icon = item_view.findViewById(R.id.like_icon);

                comments_view = item_view.findViewById(R.id.comments_view);
                comment_icon = item_view.findViewById(R.id.comment_icon);

                share_icon = item_view.findViewById(R.id.share_icon);

                follow_tag = item_view.findViewById(R.id.follow_tag);
                audio_tag = item_view.findViewById(R.id.audio_tag);
                video_tag = item_view.findViewById(R.id.video_tag);

                title_view = item_view.findViewById(R.id.post_title);

                touch_area = item_view.findViewById(R.id.touch_area);
                multi_line = item_view.findViewById(R.id.multi_line);
            } else if (this.view_type == TYPE_DRAFT) {
                content_view = item_view.findViewById(R.id.post_content);

                time_view = item_view.findViewById(R.id.time_view);

                image_view = item_view.findViewById(R.id.image_view);
                profile_view = item_view.findViewById(R.id.user_profile_view);

                position_text = item_view.findViewById(R.id.delete_tag);
                position_icon = item_view.findViewById(R.id.position_icon);

                image_tag = item_view.findViewById(R.id.image_tag);
                audio_tag = item_view.findViewById(R.id.audio_tag);
                video_tag = item_view.findViewById(R.id.video_tag);

                title_view = item_view.findViewById(R.id.draft_post_title);

                touch_area = item_view.findViewById(R.id.touch_area);
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
        if (viewType == TYPE_DRAFT) {
            item_view = inflater.inflate(R.layout.draft_simple, parent, false);
        }
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
                Uri u = Service.getResourceUri(article.author_profile);
                if (u != null) holder.profile_view.setImageURI(u);
            } else {
                holder.profile_view.setImageResource(R.drawable.default_profile);
            }

            if (article.image != null && article.image_fetched) {
                Uri u = Service.getResourceUri(article.image);
                if (u != null) {
                    holder.image_view.setImageURI(u);
                    holder.image_view.setVisibility(View.VISIBLE);
                }
            } else {
                holder.image_view.setVisibility(View.GONE);
            }

            if (article.audio != null) {
                holder.audio_tag.setVisibility(View.VISIBLE);
            } else {
                holder.audio_tag.setVisibility(View.GONE);
            }

            if (article.video != null) {
                holder.video_tag.setVisibility(View.VISIBLE);
            } else {
                holder.video_tag.setVisibility(View.GONE);
            }

            if( type != Service.POST_LIST_TYPE.SEARCH ) {
                holder.multi_line.setVisibility(View.VISIBLE);
                holder.likes_view.setText(Service.wrapInt(article.likes));
                holder.comments_view.setText(Service.wrapInt(article.comments));
                if (Service.isLike(article.id)) {
                    holder.like_icon.setImageResource(R.drawable.ic_like_blue);
                    holder.likes_view.setTextColor(Service.COLOR_BLUE);
                } else {
                    holder.like_icon.setImageResource(R.drawable.ic_like);
                    holder.likes_view.setTextColor(Service.COLOR_GREY);
                }
            } else {
                holder.multi_line.setVisibility(View.GONE);
            }


            // TODO: else
            if (listener != null) {
                holder.profile_view.setOnClickListener(view -> listener.gotoUserPage(article.author_id));

                holder.like_icon.setOnClickListener(view -> listener.switchLike(article.id));
                holder.likes_view.setOnClickListener(view -> listener.switchLike(article.id));

                holder.touch_area.setOnClickListener(view -> listener.gotoArticlePage(article.id));
                holder.comments_view.setOnClickListener(view -> listener.gotoArticlePage(article.id));
                holder.comment_icon.setOnClickListener(view -> listener.gotoArticlePage(article.id));

                holder.share_icon.setOnClickListener(view -> listener.shareArticle(article.getShareText()));
            }
        } else if (getItemViewType(position) == TYPE_DRAFT) {
            Article article = Service.getArticle(position, type);
            if (article == null) return;
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

            if (article.image != null) {
                holder.image_tag.setVisibility(View.VISIBLE);
            } else {
                holder.image_tag.setVisibility(View.GONE);
            }

            if (article.audio != null) {
                holder.audio_tag.setVisibility(View.VISIBLE);
            } else {
                holder.audio_tag.setVisibility(View.GONE);
            }

            if (article.video != null) {
                holder.video_tag.setVisibility(View.VISIBLE);
            } else {
                holder.video_tag.setVisibility(View.GONE);
            }

            if (listener != null) {
                holder.touch_area.setOnClickListener(view -> listener.gotoArticlePage(article.id));
            }

        }
    }

    @Override
    public int getItemCount() {
        return Service.getArticleCount(type);
    }
}

