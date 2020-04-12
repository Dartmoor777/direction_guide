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
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.Utils.Cons;
import com.thyme.yaslan99.routeplannerapplication.Utils.HandyFunctions;
import java.io.IOException;
import java.util.Arrays;
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
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), Cons.CAMERA_ZOOM));
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
                try {
                    getLocationDetail(latLng.latitude,latLng.longitude,mActivity);
                    onMapInteractionCallBack.onMapLongClick(mLocationDetail);
                } catch (RuntimeException ex) {
                    // do nothing
                }
                putMarker(mLocationDetail);
                putPreviousMarkers();
            }
        });

        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {

                Log.d("onPoiClick", String.format("poi, name: %s", pointOfInterest.name));
                mGoogleMap.clear();
                try {
                    getLocationDetail(pointOfInterest.latLng.latitude, pointOfInterest.latLng.longitude, mActivity);

                    // take only two first lines of name
                    String[] nameLines = pointOfInterest.name.split("\n");
                    StringBuilder titleBld = new StringBuilder("");
                    for (int i = 0; i < nameLines.length && i < 2; i++) {
                        titleBld.append(nameLines[i]);
                        if (i == 0) titleBld.append("\n");
                        if (i == 1 && nameLines.length > 2
                                && !nameLines[i].endsWith("...")) {
                            titleBld.append("...");
                        }
                    }

                    mLocationDetail.setLocationTitle(titleBld.toString());
                    onMapInteractionCallBack.onMapLongClick(mLocationDetail);
                } catch ( RuntimeException ex) {
                    // do nothing
                }
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
                LocationDetail locationDetail = prepareLocationDetailModel(address, latitude, longitude);
            } else {
                Log.e("getLocationDetail: ", "failed to set up address");
                throw new RuntimeException("failed to set up address");
            }

        } catch(IOException e) {
            Log.e("getLocationDetail", "failed to get location");
        }
    }

    private LocationDetail prepareLocationDetailModel(Address address, double latitude, double longitude) {
        mLocationDetail.setAddressLine(address.getAddressLine(0));


//        GeoApiContext geoContext = new GeoApiContext.Builder()
//                .apiKey(this.mActivity.getResources().getString(R.string.google_maps_key))
//                .build();
//
//        PlaceAutocompleteRequest request = PlacesApi.placeAutocomplete(
//                geoContext, String.format("%f,%f", latitude, longitude)
//        );
//        request.location(new com.google.maps.model.LatLng(latitude, longitude));
//
//        AutocompletePrediction[] locations;
//        try {
//            AutocompletePrediction[] locations = request.await();
//            if (locations.length != 0) {
////                titles.add(locations[0].description);
//                Log.d("AutocompletePrediction", String.format("received locations:\n%s", Arrays.toString(locations)));
//                Log.d("AutocompletePrediction", String.format("first location desc: %s", locations[0].description));
//
//                PlaceDetailsRequest detailsRequest = new PlaceDetailsRequest(geoContext);
//                PlaceDetails details = detailsRequest.placeId(locations[0].placeId).await();
////                titles.add(details.name);
//                Log.d("PlaceDetails", String.format("details.name: %s", details.name));
//            } else {
//                Log.d("AutocompletePrediction", "received empty autocomplete locations");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("Shit", "shit happened");
//        }


        List<String> titles = Arrays.asList(
                (address.getThoroughfare() != null && address.getSubThoroughfare() != null)
                        ? address.getThoroughfare() + ", " + address.getSubThoroughfare()
                        : null,
                address.getThoroughfare(),
                address.getFeatureName(),
                address.getSubLocality(),
                address.getLocality()
        );
        Log.d(
            "titles",
                titles.toString()
        );

        String mainTitle = "Unknown";
        for (String title : titles) {
            if (title == null) continue;
            mainTitle = title;
            break;
        }

        mLocationDetail.setLocationTitle(mainTitle);
        mLocationDetail.setLat(String.valueOf(latitude));
        mLocationDetail.setLng(String.valueOf(longitude));
        mLocationDetail.setDistance("");
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
            mGoogleMap.setMinZoomPreference(Cons.MIN_ZOOM);
            mGoogleMap.setMaxZoomPreference(Cons.MAX_ZOOM);
//            mGoogleMap.setLatLngBoundsForCameraTarget(Cons.KYIV_BOUND);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Cons.KYIV_LATLNG));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cons.KYIV_LATLNG, Cons.CAMERA_ZOOM));
        } else {
            for(LocationDetail locationDetail : mActivity.locationDetails) {
                putMarker(locationDetail);
            }
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mActivity.locationDetails.
                    get(mActivity.locationDetails.size() - 1).getLatLng()));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mActivity.locationDetails.
                    get(mActivity.locationDetails.size() - 1).getLatLng(), Cons.CAMERA_ZOOM));
        }
    }

    private void putMarker(LocationDetail locationDetail) {
        String firstCharacterOfLocationNameNew = HandyFunctions.getFirstCharacter(locationDetail.getLocationTitle());
        Log.d("putMarker", String.format("Make latLngSel from x1=%s, x2=%s", locationDetail.getLat(), locationDetail.getLng()));
        LatLng latLngSel = new LatLng(Double.parseDouble(locationDetail.getLat()),Double.parseDouble(locationDetail.getLng()));
        mGoogleMap.addMarker(new MarkerOptions().position(latLngSel)
                .title(locationDetail.getLocationTitle())
                .icon(BitmapDescriptorFactory.fromBitmap(HandyFunctions.getMarkerIcon(mActivity,
                        firstCharacterOfLocationNameNew,locationDetail.getIdentifierColor()))));
    }
}
