package com.example.sixths.activity;

import com.example.sixths.R;
import com.example.sixths.adapter.CommentListAdapter;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.service.Article;
import com.example.sixths.service.Service;
import com.example.sixths.view.AutoMediaController;
import com.example.sixths.view.StretchVideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ArticleActivity extends AppCompatActivity {

    public Article article;
    public int article_id;

    public NestedScrollView scroll_view;

    public TextView content_view;
    public TextView nickname_view;
    public TextView time_view;
    public TextView username_view;
    public TextView likes_view;
    public TextView comments_view;
    public TextView position_text;
    public TextView title_view;

    public TextView follow_tag;
    public TextView audio_tag;
    public TextView video_tag;
    public TextView photo_tag;

    public ImageView image_view;
    public ImageView profile_view;
    public ImageView position_icon;

    private StretchVideoView video_view;
    private StretchVideoView audio_view;

    private FrameLayout video_frame;
    private FrameLayout audio_frame;

    AutoMediaController video_controller;
    AutoMediaController audio_controller;

    public RecyclerView recycler_view;

    boolean info_init = false;
    boolean media_init = false;

    public ImageView like_icon;
    public ImageView comment_icon;

    public static int ARTICLE_SUCCESS = 1;
    public static int ARTICLE_RESOURCE = 2;
    public static int ARTICLE_FRESH = 3;

    AutoMediaController.Checker videoChecker = new AutoMediaController.Checker() {
        @Override
        public boolean check() {
            return checkVideo();
        }
    };

    AutoMediaController.Checker audioChecker = new AutoMediaController.Checker() {
        @Override
        public boolean check() {
            return checkAudio();
        }
    };

    public boolean checkVideo() {
        System.out.println("check video");
        Rect scrollBounds = new Rect();
        scroll_view.getDrawingRect(scrollBounds);

        float top = video_frame.getY();
        float bottom = top + video_frame.getHeight();
        System.out.println(scrollBounds.top + " " + scrollBounds.bottom);
        System.out.println(top + " " + bottom);

        /* if fully visible */
        return scrollBounds.top <= top && scrollBounds.bottom >= bottom;
    }

    public boolean checkAudio() {
        System.out.println("check audio");
        Rect scrollBounds = new Rect();
        scroll_view.getDrawingRect(scrollBounds);

        float top = audio_frame.getY();
        float bottom = top + audio_frame.getHeight();
        System.out.println(scrollBounds.top + " " + scrollBounds.bottom);
        System.out.println(top + " " + bottom);

        /* if fully visible */
        return scrollBounds.top <= top && scrollBounds.bottom >= bottom;
    }


    private final PostListAdapter.postListener listener = new PostListAdapter.postListener() {
        @Override
        public void gotoUserPage(int userid) {
            Intent intent = new Intent(ArticleActivity.this, UserActivity.class);
            intent.putExtra("id", userid);
            startActivity(intent);
        }

        @Override
        public void switchLike(int article_id) {
            Service.switchLike(article_id);
        }

        @Override
        public void gotoArticlePage(int article_id) {
        }
    };

    private final CommentListAdapter.CommentListener commentListener = new CommentListAdapter.CommentListener() {
        @Override
        public void deleteComment(int comment_id) {
            Service.removeComment(comment_id);
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == ARTICLE_SUCCESS) {
                successArticleInfo(msg.getData().getString("data"));
            } else if (msg.what == ARTICLE_RESOURCE) {
                System.out.println("resource got");
                successArticleResource();
            } else if (msg.what == ARTICLE_FRESH) {
                Service.getArticleInfo(article_id);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();
        article_id = intent.getIntExtra("article_id", 0);

        Service.setArticleHandler(handler);
        Service.getArticleInfo(article_id);

        /* prepare layout */
        scroll_view = findViewById(R.id.scroll_view);

        content_view = findViewById(R.id.post_content);

        nickname_view = findViewById(R.id.post_nickname);
        username_view = findViewById(R.id.center_username_view);

        time_view = findViewById(R.id.time_view);

        image_view = findViewById(R.id.image_view);
        profile_view = findViewById(R.id.user_profile_view);

        position_text = findViewById(R.id.delete_tag);
        position_icon = findViewById(R.id.position_icon);

        likes_view = findViewById(R.id.likes_view);
        like_icon = findViewById(R.id.like_icon);

        comments_view = findViewById(R.id.comments_view);
        comment_icon = findViewById(R.id.comment_icon);

        follow_tag = findViewById(R.id.follow_tag);
        audio_tag = findViewById(R.id.audio_tag);
        video_tag = findViewById(R.id.video_tag);
        photo_tag = findViewById(R.id.photo_tag);

        video_view = findViewById(R.id.video_view);
        audio_view = findViewById(R.id.audio_view);

        video_frame = findViewById(R.id.video_frame);
        audio_frame = findViewById(R.id.audio_frame);

        title_view = findViewById(R.id.post_title);

        video_controller = new AutoMediaController(this);
        video_controller.setChecker(videoChecker);

        audio_controller = new AutoMediaController(this);
        audio_controller.setChecker(audioChecker);
        audio_view.setAudio(true);

        scroll_view.smoothScrollTo(0, 0);

        scroll_view.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                video_controller.hide();
                audio_controller.hide();
            }
        });

        recycler_view = findViewById(R.id.recycler_view);


    }

    public void successArticleInfo(String data) {
        article = Service.decodeArticle(data);
        if (article != null) {
            if (!info_init) {
                successArticleResource(); // TODO

                nickname_view.setText(article.author_nickname);
                username_view.setText(article.author_username);
                content_view.setText(article.content);
                title_view.setText(article.title);
                time_view.setText(article.time);

                if (article.position != null) {
                    position_text.setText(article.position);
                    position_text.setVisibility(View.VISIBLE);
                    position_icon.setVisibility(View.VISIBLE);
                } else {
                    position_text.setVisibility(View.INVISIBLE);
                    position_icon.setVisibility(View.INVISIBLE);
                }

                if (listener != null) {
                    profile_view.setOnClickListener(view -> listener.gotoUserPage(article.author_id));

                    like_icon.setOnClickListener(view -> listener.switchLike(article.id));
                    likes_view.setOnClickListener(view -> listener.switchLike(article.id));
                    System.out.println("listener set ok");

                    // TODO
//                comments_view.setOnClickListener(view -> listener.gotoArticlePage(article.id));
//                comment_icon.setOnClickListener(view -> listener.gotoArticlePage(article.id));
                }

                info_init = true;
            }

            if (Service.isLike(article.id)) {
                like_icon.setImageResource(R.drawable.ic_like_blue);
                likes_view.setTextColor(Service.COLOR_BLUE);
            } else {
                like_icon.setImageResource(R.drawable.ic_like);
                likes_view.setTextColor(Service.COLOR_GREY);
            }

            if (Service.isFollow(article.author_id)) {
                follow_tag.setVisibility(View.VISIBLE);
            } else {
                follow_tag.setVisibility(View.GONE);
            }

            likes_view.setText(Service.wrapInt(article.likes));
            comments_view.setText(Service.wrapInt(article.comments));
        }
    }

    public void successArticleResource() {
        try {
            if (article == null) return;

            System.out.print("media_init:");
            System.out.println(media_init);
            if (media_init) return;
            if (article.image_fetched && article.video_fetched && article.audio_fetched && article.profile_fetched)
                media_init = true;
            else {
                Service.fetchImage(article);
                Service.fetchMedia(article);
                return;
            }

            /* profile image */
            System.out.println(article.author_profile);
            if (article.author_profile != null && article.profile_fetched) {
                Uri u = Service.getResourceUri(article.author_profile);
                System.out.println(u);
                if (u != null) profile_view.setImageURI(u);
                else {
                    profile_view.setImageResource(R.drawable.default_profile);
                }
            } else {
                profile_view.setImageResource(R.drawable.default_profile);
            }

            /* image */
            System.out.println(article.image);
            if (article.image != null && article.image_fetched) {
                Uri u = Service.getResourceUri(article.image);
                System.out.println(u);
                if (u != null) {
                    image_view.setImageURI(u);
                    image_view.setVisibility(View.VISIBLE);
                    photo_tag.setVisibility(View.VISIBLE);
                }
            } else {
                image_view.setVisibility(View.GONE);
                photo_tag.setVisibility(View.GONE);
            }


            /* audio */
            System.out.println("resource fetched");
            System.out.println(article.audio);
            System.out.println(article.audio_fetched);
            if (article.audio != null && article.audio_fetched) {
                Uri u = Service.getResourceUri(article.audio);
                System.out.println("audio fetched");
                if (u != null) {
                    audio_controller.setMediaPlayer(audio_view);
                    audio_controller.setAnchorView(audio_view);
                    audio_view.setMediaController(audio_controller);

                    audio_view.setVideoURI(u);
                    audio_view.setVisibility(View.VISIBLE);
                    audio_tag.setVisibility(View.VISIBLE);
                }
            } else {
                audio_view.setVisibility(View.GONE);
                audio_tag.setVisibility(View.GONE);
            }

            /* video */
            System.out.println(article.video);
            System.out.println(article.video_fetched);
            if (article.video != null && article.video_fetched) {
                Uri u = Service.getResourceUri(article.video);
                if (u != null) {

                    MediaPlayer media = new MediaPlayer();
                    media.setDataSource(getContentResolver().openAssetFileDescriptor(u, "r"));
                    media.prepare();
                    video_view.setDimensions(media.getVideoWidth(), media.getVideoHeight());
                    media.release();

                    video_controller.setMediaPlayer(video_view);
                    video_controller.setAnchorView(video_view);
                    video_view.setMediaController(video_controller);

                    video_view.setVideoURI(u);
                    video_view.setVisibility(View.VISIBLE);
                    video_tag.setVisibility(View.VISIBLE);
                }
            } else {
                video_view.setVisibility(View.GONE);
                video_tag.setVisibility(View.GONE);
            }

            System.out.print("media_init done:");
            System.out.println(media_init);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel(View view) {
        this.finish();
    }

    public void makeComment(View view) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("article_id", article.id);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Service.setArticleHandler(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Service.setArticleHandler(handler);
        Service.getArticleInfo(article_id);

        CommentListAdapter adapter = new CommentListAdapter(this, listener, commentListener);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        Service.setCommentArticle(article_id);
        Service.fetchComment();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Service.setArticleHandler(null);
    }
}