package com.thyme.yaslan99.routeplannerapplication.Map.ResultMap;

import android.util.Log;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.thyme.yaslan99.routeplannerapplication.LocationList.OptimizationType;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.Cons;
import com.thyme.yaslan99.routeplannerapplication.Utils.HandyFunctions;
import com.google.maps.DirectionsApiRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaroslava Landiga.
 */

public class RpaOnResultMapReadyCallBack implements OnMapReadyCallback {
    private ResultMapActivity mActivity;
    private GoogleMap mGoogleMap;
    private ArrayList<LocationDetail> optimizedLocationListDistance;
    private ArrayList<LocationDetail> optimizedLocationListDuration;
    private boolean isAntAlgo;
    // location listener example, currently not used
//    public RpaLocationListener rpaLocationListener;


    public RpaOnResultMapReadyCallBack(ResultMapActivity activity) {
        this.mActivity = activity;
        isAntAlgo = (boolean) mActivity.getIntent().getSerializableExtra("isAntAlgo");
        optimizedLocationListDistance = (ArrayList<LocationDetail> )mActivity.getIntent().getSerializableExtra("optimizedLocationListDistance");
        optimizedLocationListDuration = (ArrayList<LocationDetail>) mActivity.getIntent().getSerializableExtra("optimizedLocationListDuration");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        prepareMap();
        drawRoute(OptimizationType.BY_DISTANCE);
        // location listener example, currently not used
//        rpaLocationListener = new RpaLocationListener(mActivity,googleMap);
    }

    private void prepareMap() {
        mGoogleMap.setMinZoomPreference(Cons.MIN_ZOOM);
        mGoogleMap.setMaxZoomPreference(Cons.MAX_ZOOM);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Cons.KYIV_LATLNG));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cons.KYIV_LATLNG, Cons.CAMERA_ZOOM));
    }

    public void drawRoute(OptimizationType optimizationType) {

        switch (optimizationType) {

            case BY_DISTANCE:
                mGoogleMap.clear();
                for(int i = 0; i < optimizedLocationListDistance.size() - 1; i++) {
                    drawRoute( optimizedLocationListDistance.get(i),optimizedLocationListDistance.get(i+1), i + 1);
                }
                break;

            case BY_DURATION:
                mGoogleMap.clear();
                for(int i = 0; i < optimizedLocationListDuration.size() - 1; i++) {
                    drawRoute(optimizedLocationListDuration.get(i),optimizedLocationListDuration.get(i+1),i + 1);
                }
                break;
        }


    }

    private void drawRoute(final LocationDetail origin, final LocationDetail dest, final int locationIndex) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(mActivity.getResources().getString(R.string.google_maps_key))
                .build();

        DirectionsApiRequest req = new DirectionsApiRequest(geoApiContext)
                .units(Unit.METRIC)
                .mode(TravelMode.DRIVING)
                .origin(origin.getLat() + "," + origin.getLng())
                .destination(dest.getLat() + "," + dest.getLng());

        DirectionsResult result = null;
        try {
            result = req.await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            throw new AssertionError("failed to get direction");
        }

        for (int i = 0; i < result.routes.length; i++) {
            String marker = String.valueOf(locationIndex);
            mGoogleMap.addMarker(new MarkerOptions().position(origin.getLatLng())
                    .title(marker)
                    .icon(BitmapDescriptorFactory.fromBitmap(HandyFunctions.getMarkerIcon(
                            mActivity,
                            marker,
                            origin.getIdentifierColor()))));

            if (!isAntAlgo || (locationIndex + 1) != optimizedLocationListDistance.size()) {
                marker = String.valueOf(locationIndex+1);
                mGoogleMap.addMarker(new MarkerOptions().position(dest.getLatLng())
                        .title(marker)
                        .icon(BitmapDescriptorFactory.fromBitmap(HandyFunctions.getMarkerIcon(
                                mActivity,
                                marker,
                                dest.getIdentifierColor())) ));
            }

            List<com.google.android.gms.maps.model.LatLng> latLngs = new ArrayList<>();
            for (LatLng latLng : result.routes[i].overviewPolyline.decodePath()) {
                latLngs.add(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
            }
            mGoogleMap.addPolyline(new PolylineOptions()
                    .addAll(latLngs)
                    .width(20)
                    .color(origin.getIdentifierColor())
                    .geodesic(true)
            );
        }
        mActivity.mNextButton.setVisibility(View.VISIBLE);

    }

}
