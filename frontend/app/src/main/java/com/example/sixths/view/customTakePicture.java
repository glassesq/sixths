package com.example.sixths.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

public class customTakePicture extends ActivityResultContracts.TakePicture {

    @NonNull
    @Override
    public Intent createIntent(Context context, Uri input) {
        Intent intent = super.createIntent(context, input);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 30000);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        System.out.println("size limit");
        return intent;
    }

}
