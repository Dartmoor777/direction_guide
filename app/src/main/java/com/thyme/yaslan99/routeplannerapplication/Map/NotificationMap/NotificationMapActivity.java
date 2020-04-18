package com.thyme.yaslan99.routeplannerapplication.Map.NotificationMap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.SupportMapFragment;
import com.thyme.yaslan99.routeplannerapplication.R;

public class NotificationMapActivity extends AppCompatActivity {

    private RpaOnNotificationMapReadyCallBack mRpaOnNotificationMapReadyCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_map);
        prepareView();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(mRpaOnNotificationMapReadyCallBack);
    }

    private void prepareView() {
        mRpaOnNotificationMapReadyCallBack = new RpaOnNotificationMapReadyCallBack(this);
    }

}
