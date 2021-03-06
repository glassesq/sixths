package com.example.sixths.view;

import android.content.Context;
import android.widget.MediaController;

public class AutoMediaController extends MediaController {

    public interface Checker {
        boolean check();
    }

    public Checker checker;

    public AutoMediaController(Context context) {
        super(context);
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void show() {
        if (!checker.check()) {
            /* refuse to show if video_view is not fully visible */
            return;
        }
        super.show();
    }
}
