package com.thyme.yaslan99.routeplannerapplication.CustomView;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.thyme.yaslan99.routeplannerapplication.R;

/**
 * Created by Yaroslava Landyga
 */

public class LocationNotificationLabel extends RelativeLayout {
    private View mRootView;
    private TextView mNotificationCountTV;
    private ImageButton mMarkerIV;

    private String mNotificationCount;
    private Image mImage;

    public LocationNotificationLabel(Context context) {
        super(context);
        prepareView(context);
    }

    public LocationNotificationLabel(Context context,AttributeSet attrs) {
        super(context, attrs);
        prepareView(context);
    }

    public View getmRootView() {
        return mRootView;
    }

    public void setmRootView(View mRootView) {
        this.mRootView = mRootView;
    }

    public String getmNotificationCount() {
        return mNotificationCount;
    }

    public void setmNotificationCount(String mNotificationCount) {
        this.mNotificationCount = mNotificationCount;
    }

    public Image getmImage() {
        return mImage;
    }

    public void setmImage(Image mImage) {
        this.mImage = mImage;
    }

    private void prepareView(Context context) {
        mRootView = inflate(context, R.layout.location_notification_label, this);
        mNotificationCountTV = mRootView.findViewById(R.id.notification_count);
        mMarkerIV = mRootView.findViewById(R.id.marker_image);
    }
}
