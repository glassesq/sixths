package com.example.sixths.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/* https://gist.github.com/raultm/3733246 */
public class StretchVideoView extends VideoView {
    int width = 400;
    int height = 400;
    boolean isAudio = false;

    public StretchVideoView(Context context) {
        super(context);
    }

    public StretchVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StretchVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void setAudio(boolean b) {
        isAudio = b;
    }

    public void setDimensions(int w, int h) {
        if( isAudio ) return;
        if( w < 100 ) w = 100;
        if( h < 100 ) h = 100;
        if (w > h || w > 1280 || h > 960) {
            w = (int) ((double) w / 1.5);
            h = (int) ((double) h / 1.5);
            // TODO: adjust with screen.
            System.out.println(" resize ");
        }
        this.width = w;
        this.height = h;
    }


}

