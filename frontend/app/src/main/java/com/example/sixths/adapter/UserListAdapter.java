package com.example.sixths.adapter;

import android.content.Context;
import android.net.Uri;
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
import com.example.sixths.service.User;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.PostViewHolder> {

    public interface userListener {
        void gotoUserPage(int userid);
    }

    private userListener listener;

    public static final int TYPE_LIKE = 1;
    public static final int TYPE_FOLLOW = 2;

    public Service.USER_LIST_TYPE type = Service.USER_LIST_TYPE.LIKE;

    private final LayoutInflater inflater;

    public UserListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.listener = null;
        setType(null);
    }

    public UserListAdapter(Context context, userListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        setType(null);
    }

    public UserListAdapter(Context context, userListener listener, Service.USER_LIST_TYPE type) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        setType(type);
    }

    public void setType(Service.USER_LIST_TYPE type) {
        if (type != null) this.type = type;
        Service.setUserAdapter(this, this.type);
    }

    @Override
    public int getItemViewType(int position) {
        if (type == Service.USER_LIST_TYPE.FOLLOW) return TYPE_FOLLOW;
        return TYPE_LIKE;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView nickname_view;
        public TextView username_view;
        public TextView bio_view;

        public LinearLayout touch_area;

        public UserListAdapter adapter;
        private int view_type;

        public PostViewHolder(View item_view, UserListAdapter _adapter, int view_type) {
            super(item_view);
            this.view_type = view_type;
            adapter = _adapter;
            if (this.view_type == UserListAdapter.TYPE_FOLLOW) {
                username_view = item_view.findViewById(R.id.follow_username);
                nickname_view = item_view.findViewById(R.id.follow_nickname);
                bio_view = item_view.findViewById(R.id.follow_bio);
                touch_area = item_view.findViewById(R.id.touch_area);
            } else if (this.view_type == TYPE_LIKE) {
                username_view = item_view.findViewById(R.id.like_name);
                nickname_view = item_view.findViewById(R.id.like_nickname);

                touch_area = item_view.findViewById(R.id.touch_area);
            }
        }
    }

    @NonNull
    @Override
    public UserListAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = null;
        if (viewType == TYPE_FOLLOW) {
            item_view = inflater.inflate(R.layout.follow_simple, parent, false);
        }
        if (viewType == TYPE_LIKE) {
            item_view = inflater.inflate(R.layout.like_simple, parent, false);
        }
        return new PostViewHolder(item_view, this, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.PostViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_LIKE) {
            User user = Service.getUser(position, type);
            if (user == null) return;
            holder.nickname_view.setText(user.nickname);
            holder.username_view.setText(user.name);

            if (listener != null) {
                holder.touch_area.setOnClickListener(view -> listener.gotoUserPage(user.id));
            }
        } else if (getItemViewType(position) == TYPE_FOLLOW) {
            User user = Service.getUser(position, type);
            if (user == null) return;
            holder.nickname_view.setText(user.nickname);
            holder.username_view.setText(user.name);
            holder.bio_view.setText(user.bio);

            if (listener != null) {
                holder.touch_area.setOnClickListener(view -> listener.gotoUserPage(user.id));
            }
        }
    }

    @Override
    public int getItemCount() {
        return Service.getUserCount(type);
    }
}

