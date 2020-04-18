package com.thyme.yaslan99.routeplannerapplication.Map.ResultMap;

import android.util.Log;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thyme.yaslan99.routeplannerapplication.LocationList.OptimizationType;
import com.thyme.yaslan99.routeplannerapplication.Model.Direction.Example;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.NetworkCalls.ApiInterface;
import com.thyme.yaslan99.routeplannerapplication.NetworkCalls.RetroFitClient;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.Cons;
import com.thyme.yaslan99.routeplannerapplication.Utils.HandyFunctions;
import com.thyme.yaslan99.routeplannerapplication.Utils.JsonParserForDirection;
import com.thyme.yaslan99.routeplannerapplication.Utils.LocationDetailSharedPrefUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yaroslava Landiga.
 */

public class RpaOnResultMapReadyCallBack implements OnMapReadyCallback {
    private ResultMapActivity mActivity;
    private GoogleMap mGoogleMap;
    private ArrayList<LocationDetail> optimizedLocationListDistance;
    private ArrayList<LocationDetail> optimizedLocationListDuration;
    private boolean isAntAlgo;
    private ApiInterface apiService;
    public RpaLocationListener rpaLocationListener;


    public RpaOnResultMapReadyCallBack(ResultMapActivity activity) {
        this.mActivity = activity;
        isAntAlgo = (boolean) mActivity.getIntent().getSerializableExtra("isAntAlgo");
        optimizedLocationListDistance = (ArrayList<LocationDetail> )mActivity.getIntent().getSerializableExtra("optimizedLocationListDistance");
        optimizedLocationListDuration = (ArrayList<LocationDetail>) mActivity.getIntent().getSerializableExtra("optimizedLocationListDuration");
        apiService = RetroFitClient.getClient().create(ApiInterface.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        prepareMap();
        drawRoute(OptimizationType.BY_DISTANCE);
        rpaLocationListener = new RpaLocationListener(mActivity,googleMap);
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

        Call<Example> call = apiService.getDistanceDuration("metric", origin.getLat() + "," + origin.getLng(),dest.getLat() + "," + dest.getLng(), "driving", this.mActivity.getResources().getString(R.string.google_maps_key));
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                try {
                    assert response.body() != null;
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = JsonParserForDirection.decodePoly(encodedString);
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

                        mGoogleMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(20)
                                .color(origin.getIdentifierColor())
                                .geodesic(true)
                        );
                    }
                    mActivity.mNextButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
            }
        });
    }

}
