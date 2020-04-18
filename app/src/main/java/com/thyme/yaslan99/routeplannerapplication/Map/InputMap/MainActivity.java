package com.thyme.yaslan99.routeplannerapplication.Map.InputMap;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.SupportMapFragment;
import com.thyme.yaslan99.routeplannerapplication.CustomView.LocationInfoCard;
import com.thyme.yaslan99.routeplannerapplication.LocationList.LocationListActivity;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.Alerts;
import com.thyme.yaslan99.routeplannerapplication.Utils.Cons;
import com.thyme.yaslan99.routeplannerapplication.Utils.LocationDetailSharedPrefUtils;
import java.util.ArrayList;


import android.widget.Toast;
import com.thyme.yaslan99.routeplannerapplication.Utils.Permission.RuntimePermissionsActivity;

import es.dmoral.toasty.Toasty;

public class MainActivity extends RuntimePermissionsActivity implements OnMapInteractionCallBack {

    //view
    private LocationInfoCard mLocationInfoCard;
    private TextView mNotificationCountTV;
    private ImageButton mNumberOfLocationAddedButton;
    private SupportMapFragment mMapFragment;

    //interface
    private RPAOnInputMapReadyCallback mOnMapReadyCallback;

    //others
    private int notificationCount = 0;
    public ArrayList<LocationDetail> locationDetails;
    private LocationDetail mGlobalLocationDetail;
    private LocationDetailSharedPrefUtils mLocationDetailSharedPrefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareUtils();
        prepareView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Toast.makeText(this,"Location Permission Granted",Toast.LENGTH_LONG).show();
    }

    public boolean checkLocationDetailDuplicate(LocationDetail locationDetail) {
        boolean found = false;
        for (LocationDetail location : locationDetails) {
            if (location.getLatLng().equals(locationDetail.getLatLng())) {
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean performLocationCheks(LocationDetail locationDetail) {
        if (checkLocationDetailDuplicate(locationDetail)) {
            Alerts.showSimpleWarning(
                    MainActivity.this,
                    "warning",
                    "The place was already added to the list."
            );
            return false;
        }

        if (locationDetails.size() >= Cons.MAX_LOCATION_COUNT) {
            Alerts.showSimpleWarning(
                    MainActivity.this,
                    "warning",
                    String.format("The max location count is %d", Cons.MAX_LOCATION_COUNT)
            );
            return false;
        }

        return true;
    }

    private void prepareView() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(mOnMapReadyCallback);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setCustomView(R.layout.location_notification_label);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);


        mLocationInfoCard = findViewById(R.id.location_info);
        mLocationInfoCard.setVisibility(View.GONE);
        mNotificationCountTV =  findViewById(R.id.notification_count);
        mNumberOfLocationAddedButton = findViewById(R.id.marker_image);
        mNumberOfLocationAddedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notificationCount >= Cons.MIN_LOCATION_COUNT) {
                    Intent intent = new Intent(MainActivity.this, LocationListActivity.class);
                    mLocationDetailSharedPrefUtils.setLocationDataToSharedPref(locationDetails);
                    startActivity(intent);
                    finish();
                } else {
                    Alerts.showSimpleWarning(MainActivity.this,
                            "warning",
                            String.format("Choose %d or more locations first", Cons.MIN_LOCATION_COUNT));
                }
            }
        });

        mLocationInfoCard.getSelectButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDetail locationDetail = mLocationInfoCard.getLocationData();

                if (!performLocationCheks(locationDetail)) {
                    return;
                }

                locationDetails.add(locationDetail);
                notificationCount = locationDetails.size();
                mNotificationCountTV.setText(String.valueOf(notificationCount));

                Toast toasty = Toasty.normal( view.getContext(),
                        "The location was added successfully",
                        Toast.LENGTH_SHORT);
                toasty.setGravity(Gravity.TOP, 0, 160);
                toasty.show();
            }
        });

        if(locationDetails.size() == 0) {
            mNotificationCountTV.setText(String.valueOf(0));
        } else {
            notificationCount = locationDetails.size();
            mNotificationCountTV.setText(String.valueOf(notificationCount));
        }

		MainActivity.super.checkPermission();
    }

    private void prepareUtils() {
        mOnMapReadyCallback = new RPAOnInputMapReadyCallback(this);
        mOnMapReadyCallback.onMapInteractionCallBack = this;
        mLocationDetailSharedPrefUtils = new LocationDetailSharedPrefUtils(this);

        if(mLocationDetailSharedPrefUtils.getLocationDataFromSharedPref() == null) {
            locationDetails = new ArrayList<>();
        } else {
            locationDetails = mLocationDetailSharedPrefUtils.getLocationDataFromSharedPref();
        }
    }

    private void showLocationInfoCard() {
        mLocationInfoCard.setVisibility(View.VISIBLE);
    }

    private void hideLocationInfoCard() {
        mLocationInfoCard.setVisibility(View.GONE);
    }

    private void bindLocationDataToView(LocationDetail locationDetail) {
        mLocationInfoCard.setLocationTitle(locationDetail.getLocationTitle());
        mLocationInfoCard.setAddressLine(locationDetail.getAddressLine());
        mLocationInfoCard.setLatlng(locationDetail.getLat() + " , " + locationDetail.getLng());
        mLocationInfoCard.setDistance("");
        mLocationInfoCard.setOpenTime("");
        mLocationInfoCard.setLat(locationDetail.getLat());
        mLocationInfoCard.setLng(locationDetail.getLng());
        mLocationInfoCard.setIdentifierColor(locationDetail.getIdentifierColor());
    }

    @Override
    public void onMapLongClick(LocationDetail locationDetail) {
        bindLocationDataToView(locationDetail);
        showLocationInfoCard();
        mGlobalLocationDetail = locationDetail;
    }

    @Override
    public void onMapClick() {
       hideLocationInfoCard();
    }
}
