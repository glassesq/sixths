package com.example.sixths.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixths.R;
import com.example.sixths.service.Comment;
import com.example.sixths.service.Service;


public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {

    private PostListAdapter.postListener listener;

    public interface CommentListener {
        void deleteComment(int article_id);
    }

    private CommentListener commentListener;

    public static final int TYPE_COMMENT = 2;

    private final LayoutInflater inflater;

    public CommentListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.listener = null;
        this.commentListener = null;
        Service.setCommentAdapter(this);
    }

    public CommentListAdapter(Context context, PostListAdapter.postListener listener, CommentListener commentListener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.commentListener = commentListener;
        Service.setCommentAdapter(this);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_COMMENT;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView content_view;
        public TextView nickname_view;
        public TextView time_view;
        public TextView username_view;

        public TextView follow_tag;
        public TextView delete_tag;

        public ImageView profile_view;

        public CommentListAdapter adapter;
        private int view_type;

        public CommentViewHolder(View item_view, CommentListAdapter _adapter, int view_type) {
            super(item_view);
            this.view_type = view_type;
            adapter = _adapter;
            if (this.view_type == CommentListAdapter.TYPE_COMMENT) {
                content_view = item_view.findViewById(R.id.post_content);

                nickname_view = item_view.findViewById(R.id.follow_nickname);
                username_view = item_view.findViewById(R.id.follow_username);

                time_view = item_view.findViewById(R.id.time_view);

                profile_view = item_view.findViewById(R.id.user_profile_view);

                follow_tag = item_view.findViewById(R.id.follow_tag);
                delete_tag = item_view.findViewById(R.id.delete_tag);
            }
        }
    }

    @NonNull
    @Override
    public CommentListAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = null;
        if (viewType == TYPE_COMMENT) {
            item_view = inflater.inflate(R.layout.comment_simple, parent, false);
        }
        return new CommentViewHolder(item_view, this, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.CommentViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_COMMENT) {
            Comment comment = Service.getComment(position);
            if (comment == null) return;
            holder.nickname_view.setText(comment.author_nickname);
            holder.username_view.setText(comment.author_username);
            holder.content_view.setText(comment.content);
            holder.time_view.setText(comment.time);

            if (Service.isFollow(comment.author_id)) {
                holder.follow_tag.setVisibility(View.VISIBLE);
            } else {
                holder.follow_tag.setVisibility(View.GONE);
            }

            if (comment.author_profile != null && comment.profile_fetched) {
                Uri u = Service.getResourceUri(comment.author_profile);
                if (u != null) holder.profile_view.setImageURI(u);
            } else {
                holder.profile_view.setImageResource(R.drawable.default_profile);
            }

            if (Service.myself.id == comment.author_id) {
                holder.delete_tag.setVisibility(View.VISIBLE);
            } else {
                holder.delete_tag.setVisibility(View.INVISIBLE);
            }

            if (listener != null) {
                holder.profile_view.setOnClickListener(view -> listener.gotoUserPage(comment.author_id));
            }
            if (commentListener != null) {
                holder.delete_tag.setOnClickListener(view -> commentListener.deleteComment(comment.id));
            }
        }
    }

    @Override
    public int getItemCount() {
        return Service.getCommentCount();
    }
}

