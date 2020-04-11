package com.thyme.yaslan99.routeplannerapplication.Utils;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Yaroslava Landyga
 */

public class BasicProgressBar {

    private ProgressDialog progress;
    private AppCompatActivity mActivity;

    public BasicProgressBar(AppCompatActivity activity, String message) {
        this.mActivity = activity;
        progress=new ProgressDialog(activity);
        progress.setMessage(message);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgress(0);

    }

    public void show() {
        progress.show();
    }

    public void hide() {
        progress.hide();
    }
}
