package com.thyme.yaslan99.routeplannerapplication.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.R;

/**
 * Created by Yaroslava Landyga
 */

public class LocationInfoCard extends LinearLayout {

    private String mLocationTitle;
    private String mAddressLine;
    private String mLat;
    private String mLng;
    private String mDistance;
    private String mOpenTime;
    private int identifierColor;

    private View mRootView;
    private TextView mLocationTitleTV;
    private TextView mAddressLineTV;
    private TextView mLatLngTV;
    private TextView mDistanceTV;
    private TextView mOpenTimeTV;
    private Button mSelectButton;

    public LocationInfoCard(Context context) {
        super(context);
        prepareView(context);
    }

    public LocationInfoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareView(context);
    }

    public String getmLocationTitle() {
        return mLocationTitle;
    }

    public void setLocationTitle(String mLocationTitle) {
        this.mLocationTitle = mLocationTitle;
        mLocationTitleTV.setText(mLocationTitle);
    }

    public String getAddressLine() {
        return mAddressLine;
    }

    public void setAddressLine(String mAddressLine) {
        this.mAddressLine = mAddressLine;
        mAddressLineTV.setText(mAddressLine);
    }

    public String getLng() {
        return mLng;
    }

    public void setLng(String mLng) {
        this.mLng = mLng;

    }

    public void setLat(String lat) {
        this.mLat = lat;
    }

    public String getLat() {
        return mLat;
    }

    public void setLatlng(String mLatlng) {
        mLatLngTV.setText(mLatlng);
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String mDistance) {
        this.mDistance = mDistance;
        mDistanceTV.setText(mDistance);
    }

    public String getOpenTime() {
        return mOpenTime;
    }

    public void setOpenTime(String mOpenTime) {
        this.mOpenTime = mOpenTime;
        mOpenTimeTV.setText(mOpenTime);
    }

    public int getIdentifierColor() {
        return identifierColor;
    }

    public void setIdentifierColor(int identifierColor) {
        this.identifierColor = identifierColor;
    }

    public Button getSelectButton() {
        return mSelectButton;
    }

    public LocationDetail getLocationData() {
        LocationDetail locationDetail = new LocationDetail();
        locationDetail.setLocationTitle(getmLocationTitle());
        locationDetail.setAddressLine(getAddressLine());
        locationDetail.setLat(getLat());
        locationDetail.setLng(getLng());
        locationDetail.setDistance("");
        locationDetail.setIdentifierColor(getIdentifierColor());
        return locationDetail;
    }

    private void prepareView(Context context) {

        mRootView = inflate(context, R.layout.location_info_card_view, this);
        mLocationTitleTV = mRootView.findViewById(R.id.location_title);
        mAddressLineTV = mRootView.findViewById(R.id.address_line);
        mLatLngTV = mRootView.findViewById(R.id.lat_lng);
        mDistanceTV = mRootView.findViewById(R.id.distance);
        mOpenTimeTV = mRootView.findViewById(R.id.open_time);
        mSelectButton = mRootView.findViewById(R.id.select_button);
    }
}
