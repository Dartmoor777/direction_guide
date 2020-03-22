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
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;

import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
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
import com.thyme.smalam119.routeplannerapplication.Utils.TSPEngine.Algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
//        apiService = RetroFitClient.getClient().create(ApiInterface.class);
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
        if (numberOfLocations <= 0) {
            Log.d("prepDistanceDurList", "zero location number");
            return;
        }

        // prepare places uri for google maps request
        StringBuilder placesBuilder = new StringBuilder("");
        for (int i = 0; numberOfLocations > 0
                && i < numberOfLocations-1; i++) {
            placesBuilder.append(mLocationDetails.get(i).getLatLng().latitude);
            placesBuilder.append(",");
            placesBuilder.append(mLocationDetails.get(i).getLatLng().longitude);
            placesBuilder.append("|");
        }
        placesBuilder.append(mLocationDetails.get(numberOfLocations-1).getLatLng().latitude);
        placesBuilder.append(",");
        placesBuilder.append(mLocationDetails.get(numberOfLocations-1).getLatLng().longitude);

        String places = placesBuilder.toString();
        // should be something like that: 41.43206,-81.38992|-33.86748,151.20699|41.43896,-81.21982
        Log.d("prepareDistanceDuratio", places);

        GeoApiContext distCalcer = new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.google_maps_key))
                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer);
        DistanceMatrix result = null;
        try {
            result = req.origins(places)
                    .destinations(places)
                    .units(Unit.METRIC)
                    .mode(TravelMode.DRIVING)
                    .language("en-US")
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        {
            StringBuilder builderStr = new StringBuilder("");
            assert result != null;
            for (int y = 0; y < result.rows.length; y++) {
                for (int x = 0; x < result.rows[y].elements.length; x++) {
                    mInputMatrixForTspDistance[y][x] = (int) result.rows[y].elements[x].distance.inMeters;
                    mInputMatrixForTspDuration[y][x] = (int) result.rows[y].elements[x].duration.inSeconds;
                    builderStr.append(result.rows[y].elements[x].distance.inMeters);
                    builderStr.append(" ");
                }
                builderStr.append("\n");
            }
            Log.d("matrix", String.format("The matrix is\n%s", builderStr.toString()));
        }
    }

    private double[][] convInt2DobuleMatrix(int[][] matrix) {
        double[][] doubleMatrix = new double[matrix.length][matrix[0].length];
        for (int y = 0; y < mInputMatrixForTspDistance.length; y++) {
            for (int x = 0; x < mInputMatrixForTspDistance[y].length; x++) {
                doubleMatrix[y][x] = matrix[y][x];
            }
        }
        return doubleMatrix;
    }


    private List<Integer> reArrangeRoute(List bestChain)
    {
        List<Integer> reArrangedBestChain = new ArrayList<Integer>();

        bestChain.remove(bestChain.get(bestChain.size()-1));
        int indexOfFirstElement = bestChain.indexOf(0);
        for (int i = indexOfFirstElement
             ; i < bestChain.size() ; i++) {
            reArrangedBestChain.add((Integer)bestChain.get(i));
        }
        for (int i = 0; i < indexOfFirstElement;i++)
        {
            reArrangedBestChain.add((Integer)bestChain.get(i));
        }
        reArrangedBestChain.add((Integer)reArrangedBestChain.get(0));

        return reArrangedBestChain;
    }

    public void getOptimizeRoute() {
        Log.d("getOptimizeRoute", String.format("distance list size=%d", mDistanceList.size()));

        for (int y = 0; y < mInputMatrixForTspDistance.length; y++) {
            for (int x = 0; x < mInputMatrixForTspDistance[y].length; x++) {
                totalDistance += mInputMatrixForTspDistance[y][x];
                totalDuration += mInputMatrixForTspDuration[y][x] / 60; // convert seconds to minutes
            }
        }
        Algorithm.newMatrix(convInt2DobuleMatrix(mInputMatrixForTspDistance));
        Algorithm.iterate();
        List bestChain = Arrays.stream( Algorithm.getBestChain() ).boxed().collect( Collectors.toList() );
        bestChain = reArrangeRoute(bestChain);
        Log.d("best chain", Arrays.toString(bestChain.toArray()));


        for(int i = 0; i < bestChain.size(); i++){
            optimizedLocationListDistance.add(mLocationDetails.get((int)bestChain.get(i)));
        }

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
