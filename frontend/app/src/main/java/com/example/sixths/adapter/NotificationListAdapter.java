package com.example.sixths.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixths.R;
import com.example.sixths.service.Notification;
import com.example.sixths.service.Service;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private PostListAdapter.postListener listener;

    public static final int TYPE_NOTI = 3;

    private final LayoutInflater inflater;

    public NotificationListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.listener = null;
        Service.setNotificationAdapter(this);
    }

    public NotificationListAdapter(Context context, PostListAdapter.postListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        Service.setNotificationAdapter(this);
    }

    public void setNoti(int id) {
        Service.setNoti(id);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NOTI;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView content_view;
        public TextView time_view;

        public ImageView image_view;

        public LinearLayout touch_area;

        public NotificationListAdapter adapter;
        private int view_type;

        public NotificationViewHolder(View item_view, NotificationListAdapter _adapter, int view_type) {
            super(item_view);
            this.view_type = view_type;
            adapter = _adapter;
            if (this.view_type == NotificationListAdapter.TYPE_NOTI) {
                content_view = item_view.findViewById(R.id.follow_bio);
                time_view = item_view.findViewById(R.id.notification_time_view);
                image_view = item_view.findViewById(R.id.notification_image_view);

                touch_area = item_view.findViewById(R.id.touch_area);
            }
        }
    }

    @NonNull
    @Override
    public NotificationListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = null;
        if (viewType == TYPE_NOTI) {
            item_view = inflater.inflate(R.layout.notice_simple, parent, false);
        }
        return new NotificationViewHolder(item_view, this, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.NotificationViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NOTI) {
            Notification noti = Service.getNotification(position);
            if (noti == null) return;
            holder.content_view.setText(noti.content);
            holder.time_view.setText(noti.time);

            // TODO: else
            if (listener != null) {
                holder.touch_area.setOnClickListener(view -> {
                            noti.checked = true;
                            if (noti.type.equals("like")) {
                                holder.image_view.setImageResource(R.drawable.ic_like);
                            } else if (noti.type.equals("comment")) {
                                holder.image_view.setImageResource(R.drawable.ic_comment);
                            } else {
                                holder.image_view.setImageResource(R.drawable.ic_edit);
                            }
                            setNoti(noti.id);
                            listener.gotoArticlePage(noti.article_id);
                        }
                );
            }

            if (noti.type == null) return;
            if (!noti.checked) {
                if (noti.type.equals("like")) {
                    holder.image_view.setImageResource(R.drawable.ic_like_red);
                } else if (noti.type.equals("comment")) {
                    holder.image_view.setImageResource(R.drawable.ic_comment_red);
                } else {
                    holder.image_view.setImageResource(R.drawable.ic_edit_red);
                }
                holder.image_view.setOnClickListener(view -> {
                            noti.checked = true;
                            if (noti.type.equals("like")) {
                                holder.image_view.setImageResource(R.drawable.ic_like);
                            } else if (noti.type.equals("comment")) {
                                holder.image_view.setImageResource(R.drawable.ic_comment);
                            } else {
                                holder.image_view.setImageResource(R.drawable.ic_edit);
                            }
                            setNoti(noti.id);
                        }
                );
            } else {
                if (noti.type.equals("like")) {
                    holder.image_view.setImageResource(R.drawable.ic_like);
                } else if (noti.type.equals("comment")) {
                    holder.image_view.setImageResource(R.drawable.ic_comment);
                } else {
                    holder.image_view.setImageResource(R.drawable.ic_edit);
                }
                holder.image_view.setOnClickListener(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return Service.getNotificationCount();
    }
}

