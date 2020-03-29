package com.thyme.yaslan99.routeplannerapplication.Map.InputMap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.Utils.Cons;
import com.thyme.yaslan99.routeplannerapplication.Utils.HandyFunctions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Yaroslava Landyga
 */

public class RPAOnInputMapReadyCallback implements OnMapReadyCallback {
    public OnMapInteractionCallBack onMapInteractionCallBack;
    private MainActivity mActivity;
    private LocationDetail mLocationDetail;
    private Vibrator mVibrate;
    private LocationManager mLocationManager;
    private String mProvider;
    private GoogleMap mGoogleMap;

    public RPAOnInputMapReadyCallback(MainActivity activity) {
        this.mActivity = activity;
        mLocationDetail = new LocationDetail();
        mVibrate = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void markCurrentLocation() {
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(mProvider);

        if(location != null) {
            getLocationDetail(location.getLatitude(),location.getLongitude(),mActivity);
            putMarker(mLocationDetail);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 14.0f));
        } else {
            Toast.makeText(mActivity,"Current location not found. Please check location settings",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initializeMap();
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mVibrate.vibrate(100);
                mGoogleMap.clear();
                getLocationDetail(latLng.latitude,latLng.longitude,mActivity);
                putMarker(mLocationDetail);
                putPreviousMarkers();
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMapInteractionCallBack.onMapClick();
            }
        });
    }

    private void getLocationDetail(final double latitude, final double longitude, final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            Log.d("getLocationDetail", String.format("get location from: latitude=%f, longitude=%f",
                            latitude, longitude));
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                LocationDetail locationDetail = prepareLocationDetailModel(address);
                onMapInteractionCallBack.onMapLongClick(locationDetail);
            }

        } catch(IOException e) {
            Log.e("getLocationDetail", "failed to get location");
        }
    }

    private LocationDetail prepareLocationDetailModel(Address address) {
        mLocationDetail.setAddressLine(address.getAddressLine(0));
        String subLocality = address.getSubLocality();
        if(subLocality == null) {
            mLocationDetail.setLocationTitle("Unknown");
        } else {
            mLocationDetail.setLocationTitle(address.getSubLocality());
        }
        mLocationDetail.setLat(String.valueOf(address.getLatitude()));
        mLocationDetail.setLng(String.valueOf(address.getLongitude()));
        mLocationDetail.setDistance("1.5 MILES");
        mLocationDetail.setIdentifierColor(HandyFunctions.getRandomColor());
        return mLocationDetail;
    }

    private void putPreviousMarkers() {
        for(LocationDetail locationDetail : mActivity.locationDetails) {
            putMarker(locationDetail);
        }
    }

    private void initializeMap() {
        Log.d("initializeMap", String.format("locations' size=%d", mActivity.locationDetails.size()));
        if(mActivity.locationDetails.size() == 0) {
            mGoogleMap.clear();
            mGoogleMap.setMinZoomPreference(13.0f);
            mGoogleMap.setMaxZoomPreference(16.0f);
//            mGoogleMap.setLatLngBoundsForCameraTarget(Cons.KYIV_BOUND);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Cons.KYIV_LATLNG));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cons.KYIV_LATLNG, 14.0f));
        } else {
            for(LocationDetail locationDetail : mActivity.locationDetails) {
                putMarker(locationDetail);
            }
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mActivity.locationDetails.
                    get(mActivity.locationDetails.size() - 1).getLatLng()));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mActivity.locationDetails.
                    get(mActivity.locationDetails.size() - 1).getLatLng(), 14.0f));
        }
    }

    private void putMarker(LocationDetail locationDetail) {
        String firstCharacterOfLocationNameNew = HandyFunctions.getFirstCharacter(locationDetail.getLocationTitle());
        Log.d("putMarker", String.format("Make latLngSel from x1=%s, x2=%s", locationDetail.getLat(), locationDetail.getLng()));
        LatLng latLngSel = new LatLng(Double.parseDouble(locationDetail.getLat()),Double.parseDouble(locationDetail.getLng()));
        mGoogleMap.addMarker(new MarkerOptions().position(latLngSel)
                .title("Marker")
                .icon(BitmapDescriptorFactory.fromBitmap(HandyFunctions.getMarkerIcon(mActivity,
                        firstCharacterOfLocationNameNew,locationDetail.getIdentifierColor()))));
    }
}
