package com.thyme.smalam119.routeplannerapplication.LocationList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thyme.smalam119.routeplannerapplication.Model.Direction.Example;
import com.thyme.smalam119.routeplannerapplication.Model.LocationDetail;
import com.thyme.smalam119.routeplannerapplication.NetworkCalls.ApiInterface;
import com.thyme.smalam119.routeplannerapplication.NetworkCalls.RetroFitClient;
import com.thyme.smalam119.routeplannerapplication.R;
import com.thyme.smalam119.routeplannerapplication.ResultLocationList.ResultLocationListActivity;
import com.thyme.smalam119.routeplannerapplication.Utils.HandyFunctions;
import com.thyme.smalam119.routeplannerapplication.Utils.LocationDetailSharedPrefUtils;
import com.thyme.smalam119.routeplannerapplication.Utils.Permission.RuntimePermissionsActivity;
import com.thyme.smalam119.routeplannerapplication.Utils.TSPEngine.TSPEngine;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationListActivity extends AppCompatActivity implements OnAdapterValueChanged {

    //view
    private RecyclerView mLocationRecyclerView;
    private Button mOptimizeButton;

    //managers
    private LocationDetailSharedPrefUtils mLocationDetailSharedPrefUtils;
    private ApiInterface apiService;
    private LocationListAdapter locationListAdapter;
    private TSPEngine mTspEng;

    //lists
    public ArrayList<LocationDetail> mLocationDetails;
    private ArrayList<String> mDistanceList;
    private ArrayList<String> mDurationList;
    public ArrayList<LocationDetail> optimizedLocationListDistance;
    public ArrayList<LocationDetail> optimizedLocationListDuration;

    //others
    private int numberOfLocations;
    private int[][] mInputMatrixForTspDistance;
    private int[][] mInputMatrixForTspDuration;
    public int totalDistance = 0;
    public int totalDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        prepareUtils();
        prepareLists();
        prepareView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void prepareUtils() {
        apiService = RetroFitClient.getClient().create(ApiInterface.class);
        mTspEng = new TSPEngine();
        mLocationDetailSharedPrefUtils = new LocationDetailSharedPrefUtils(getApplicationContext());
    }

    private void resetLists() {
        mLocationDetails = mLocationDetailSharedPrefUtils.getLocationDataFromSharedPref();
        numberOfLocations = mLocationDetails.size();
        mDistanceList.clear();
        mDurationList.clear();
        optimizedLocationListDistance.clear();
        optimizedLocationListDuration.clear();
        mInputMatrixForTspDistance = new int[numberOfLocations][numberOfLocations];
        mInputMatrixForTspDuration = new int[numberOfLocations][numberOfLocations];
        prepareDistanceDurationList();
    }

    private void prepareLists() {
        mLocationDetails = mLocationDetailSharedPrefUtils.getLocationDataFromSharedPref();
        numberOfLocations = mLocationDetails.size();
        Log.d("LocationListActivity", String.format("location size=%d", numberOfLocations));
        mDistanceList = new ArrayList<>();
        mDurationList = new ArrayList<>();
        optimizedLocationListDistance = new ArrayList<>();
        optimizedLocationListDuration = new ArrayList<>();
        mInputMatrixForTspDistance = new int[numberOfLocations][numberOfLocations];
        mInputMatrixForTspDuration = new int[numberOfLocations][numberOfLocations];
        prepareDistanceDurationList();
    }

    private void prepareView() {
        mLocationRecyclerView = findViewById(R.id.location_recycler_view);
        mOptimizeButton = findViewById(R.id.optimize_button);
        mOptimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LocationListActivity", "optimize onClick");
                getOptimizeRoute();
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mLocationRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));
        locationListAdapter = new LocationListAdapter(this,mLocationDetails);
        locationListAdapter.onAdapterValueChanged = this;
        mLocationRecyclerView.setLayoutManager(llm);
        mLocationRecyclerView.setAdapter(locationListAdapter);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(this, LoginActivity.class));
//    }

    private void prepareDistanceDurationList() {
        for (int i = 0; i < numberOfLocations; i++) {
            LatLng origin = mLocationDetails.get(i).getLatLng();
            assert origin != null;
            Log.d("prepareDistanceDurationList", String.format("origin=%s", origin.toString()));

            for (int j = 0; j < numberOfLocations; j++) {
                LatLng dest = mLocationDetails.get(j).getLatLng();
                assert dest != null;
                Log.d("prepareDistanceDurationList", String.format("dest=%s", dest.toString()));
                getDistanceAndDuration(origin, dest);
            }
        }
    }

    private void getDistanceAndDuration(LatLng origin, LatLng dest) {
        // TODO doesn't work
//        final ProgressDialog mProgressDialog = ProgressDialog.show(this,"Please Wait",
//                "collecting data...");
        Call<Example> call = apiService.getDistanceDuration("metric", origin.latitude + "," + origin.longitude,dest.latitude + "," + dest.longitude, "driving", getResources().getString(R.string.google_maps_key));
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                try {
//                    mProgressDialog.hide();
                    assert response.body() != null;
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        mDistanceList.add(distance);
                        mDurationList.add(time);
                    }
                } catch (Exception e) {
//                    mProgressDialog.hide();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
//                mProgressDialog.hide();
            }
        });
    }

    public void getOptimizeRoute() {
        // TODO: fix this
        int row = -1;

        Log.d("getOptimizeRoute", String.format("distance list size=%d", mDistanceList.size()));
        for (int i = 0; i < mDistanceList.size(); i++) {
            int column = i % numberOfLocations;
            if (column == 0)
                row++;
            mInputMatrixForTspDistance[row][column] = HandyFunctions.convertDistanceToMeter(mDistanceList
                    .get(i));
            totalDistance = totalDistance + HandyFunctions.convertDistanceToMeter(mDistanceList
                    .get(i));
            totalDuration = totalDuration + HandyFunctions.convertHourToMinute(mDurationList.get(i));
        }

        ArrayList<Integer> pointOrderByDistance = mTspEng.computeTSP(mInputMatrixForTspDistance,
                numberOfLocations);

        for(int i = 0; i < pointOrderByDistance.size() - 1; i++){
            optimizedLocationListDistance.add(mLocationDetails.get(pointOrderByDistance.get(i)));
        }




//        for (int i = 0; i < mDurationList.size(); i++) {
//            int column = i % numberOfLocations;
//            if (column == 0)
//                row++;
//            mInputMatrixForTspDuration[row][column] = HandyFunctions.convertHourToMinute(mDurationList.get(i));
//            totalDuration = totalDuration + HandyFunctions.convertHourToMinute(mDurationList.get(i));
//
//            Log.d("total duration", totalDuration + "");
//        }
//
//        ArrayList<Integer> pointOrderByDuration = mTspEng.computeTSP(mInputMatrixForTspDuration,
//                numberOfLocations);
//
//        Log.d("point_order_duration_size", pointOrderByDuration.size() + "");
//
//        for(int i = 0; i < pointOrderByDuration.size() - 1; i++){
//            Log.d("point_order_duration", pointOrderByDuration.get(i) + "");
//            optimizedLocationListDuration.add(mLocationDetails.get(pointOrderByDuration.get(i)));
//        }

        gotoResultLists();

    }

    private void gotoResultLists() {
        Log.d("LocationListActivity", "gotoResultsActivity");
        Intent intent = new Intent(this, ResultLocationListActivity.class);
        intent.putExtra("optimizedLocationListDistance", optimizedLocationListDistance);
        intent.putExtra("optimizedLocationListDuration", optimizedLocationListDuration);
        Log.d("gotoResultLists", String.format("totalDistance=%d", totalDistance));
        Log.d("gotoResultLists", String.format("totalDuration=%d", totalDuration));
        intent.putExtra("totalDistance", HandyFunctions.convertMeterToKiloMeter(totalDistance));
        intent.putExtra("totalDuration", HandyFunctions.convertMinuteToHour(totalDuration));
        startActivity(intent);
        finish();
    }

    @Override
    public void onRemoveLocation(int position) {
        resetLists();
    }
}
