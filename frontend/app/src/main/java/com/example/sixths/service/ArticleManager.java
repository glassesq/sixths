package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.sixths.adapter.PostListAdapter;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ArticleManager {

    private int person_userid = -1;

    private boolean follow = false;

    private boolean draft = false;

    private boolean enableSearch = false;
    private String search_text = null;
    private Service.SEARCH_TYPE search_type = null;
    private int search_filter = Service.FILTER_ALL;

    public int allowSize = Service.START_ARTICLE_NUM;
    private ArrayList<Article> article_list = new ArrayList<Article>();
    private PostListAdapter adapter = null;

    public int getSearchFilter() {
        return search_filter;
    }

    public Service.SEARCH_TYPE getSearchType() {
        return search_type;
    }

    public void setSearchType(Service.SEARCH_TYPE type) {
        this.search_type = type;
    }

    public void setSearchText(String text) {
        this.search_text = text;
    }

    public void setSearchFilter(int f) {
        search_filter = f;
    }

    public void setEnableSearch() {
        enableSearch = true;
    }

    public void setPerson(int id) {
        person_userid = id;
    }

    public void setFollow() {
        follow = true;
    }

    public void setDraft() {
        draft = true;
    }

    //        @SuppressLint("NotifyDataSetChanged")
    public void setAdapter(PostListAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void fresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            fresh();
        }
    };

    public void fetchArticle() {
        System.out.println("fetch Article");
        Thread thread = new Thread(() -> {
            try {
                String params = "start=" + URLEncoder.encode(String.valueOf(0), "UTF-8")
                        + "&num=" + URLEncoder.encode(String.valueOf(allowSize), "UTF-8");
                System.out.println("person userid");
                System.out.println(person_userid);
                if (person_userid >= 0) {
                    params = params.concat("&userid=" + URLEncoder.encode(String.valueOf(person_userid), "UTF-8"));
                } else if (follow) {
                    params = params.concat("&follow=true");
                    System.out.println("follow");
                } else if (draft) {
                    params = params.concat("&draft=true");
                    System.out.println("draft");
                } else if (enableSearch) {
                    if (search_filter != Service.FILTER_ALL) {
                        if ((search_filter & Service.FILTER_TEXT) != 0) {
                            params = params.concat("&filter_text=true");
                        }
                        if ((search_filter & Service.FILTER_IMAGE) != 0) {
                            params = params.concat("&filter_image=true");
                        }
                        if ((search_filter & Service.FILTER_AUDIO) != 0) {
                            params = params.concat("&filter_audio=true");
                        }
                        if ((search_filter & Service.FILTER_VIDEO) != 0) {
                            params = params.concat("&filter_video=true");
                        }
                    }
                    if (search_text != null && !search_text.equals("")) {
                        System.out.println(search_type);
                        if (search_type == Service.SEARCH_TYPE.TITLE) {
                            params = params.concat("&search_title=true"
                                    + "&search_text=" + URLEncoder.encode(search_text, "UTF-8"));
                        } else if (search_type == Service.SEARCH_TYPE.CONTENT) {
                            params = params.concat("&search_content=true"
                                    + "&search_text=" + URLEncoder.encode(search_text, "UTF-8"));
                        } else if (search_type == Service.SEARCH_TYPE.USER) {
                            params = params.concat("&search_user=true"
                                    + "&search_text=" + URLEncoder.encode(search_text, "UTF-8"));
                        }
                    }
                }
                System.out.println(params);

                HttpURLConnection conn =
                        Service.getConnectionWithToken("/article/get_list", "GET", params);
                System.out.println("fetch Article conn established");

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = Service.is2String(in);
                    System.out.println(result);

                    ArrayList<Article> list = new ArrayList<Article>();
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        Article article = Service.decodeArticle(arr.getJSONObject(i));
                        if (article != null) {
                            Service.fetchImage(article);
                            list.add(article);
                        }
                    }
                    article_list = list;

                    Message msg = new Message();
                    msg.setTarget(handler);
                    msg.sendToTarget();

                    /*
                    for (int i = 0; i < arr.length(); i++) {
                        Service.fetchResourceFromSrc(article_list.get(i).author_profile);
                        adapter.notifyItemChanged(i);
                    } */

                } else {
                    System.out.println(conn.getResponseCode());
                    InputStream in = conn.getErrorStream();

                    String result = Service.is2String(in);
                    System.out.println(result);
                }
                System.out.println("notify data set changed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void showMoreArticle(int more) {
        allowSize += more;
        fetchArticle();
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // TODO
//                adapter.setIfMore(allowSize < article_list.size());
        }
    }

    public Article getByIndex(int index) {
        if (index < 0 || index >= count()) return null;
        return article_list.get(index);
    }

    public int count() {
        return Math.min(allowSize, article_list.size());
    }


    public void clear() {
        article_list.clear();
        Message msg = new Message();
        msg.setTarget(handler);
        msg.sendToTarget();
    }
}
