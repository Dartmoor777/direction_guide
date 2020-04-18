package com.thyme.yaslan99.routeplannerapplication.LocationList;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;

import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.thyme.yaslan99.routeplannerapplication.Map.InputMap.MainActivity;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.ResultLocationList.ResultLocationListActivity;
import com.thyme.yaslan99.routeplannerapplication.Utils.DijkstraEngine.DijkstrasV2;
import com.thyme.yaslan99.routeplannerapplication.Utils.HandyFunctions;
import com.thyme.yaslan99.routeplannerapplication.Utils.LocationDetailSharedPrefUtils;
import com.thyme.yaslan99.routeplannerapplication.Utils.TSPEngine.Algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class LocationListActivity extends AppCompatActivity implements OnAdapterValueChanged {

    //view
    private RecyclerView mLocationRecyclerView;
    private Button mOptimizeButton;
    private Button mDijkstraButton;

    //managers
    private LocationDetailSharedPrefUtils mLocationDetailSharedPrefUtils;
    private LocationListAdapter locationListAdapter;

    //lists
    public ArrayList<LocationDetail> mLocationDetails;
    private ArrayList<String> mDistanceList;
    private ArrayList<String> mDurationList;
    public ArrayList<LocationDetail> optimizedLocationListDistance;
    public ArrayList<LocationDetail> optimizedLocationListDuration;
    public boolean isAntAlgo;


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
    }

    private void prepareView() {
        mLocationRecyclerView = findViewById(R.id.location_recycler_view);
        mOptimizeButton = findViewById(R.id.tsp_button);
        mOptimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LocationListActivity", "optimize onClick");
                isAntAlgo = true;
                prepareDistanceDurationList();
                getOptimizeRoute();
            }
        });

        mDijkstraButton = findViewById(R.id.dijkstras_button);
        mDijkstraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LocationListActivity", "Dijkstra onClick");
                isAntAlgo = false;
                prepareDistanceDurationList();
                getDijkstraRoute();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mLocationRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));
        locationListAdapter = new LocationListAdapter(this,mLocationDetails);
        locationListAdapter.onAdapterValueChanged = this;
        mLocationRecyclerView.setLayoutManager(llm);
        mLocationRecyclerView.setAdapter(locationListAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int pos_dragged = dragged.getAdapterPosition();
                int pos_target = target.getAdapterPosition();
                Collections.swap(mLocationDetails, pos_dragged, pos_target);
                locationListAdapter.notifyItemMoved(pos_dragged, pos_target);
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(mLocationRecyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

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
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            throw new AssertionError("Failed to get distance matrix");
        }

        {
            StringBuilder builderStr = new StringBuilder("");
            Log.d("Received origins", Arrays.toString(result.originAddresses));
            Log.d("Received destinations", Arrays.toString(result.destinationAddresses));
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


        Algorithm.newMatrix(convInt2DobuleMatrix(mInputMatrixForTspDistance));
        Algorithm.iterate();
        List<Integer> bestChain = (List<Integer>)(List<?>) Arrays.stream( Algorithm.getBestChain() ).boxed().collect( Collectors.toList() );
        bestChain = reArrangeRoute(bestChain);
        Log.d("best chain", Arrays.toString(bestChain.toArray()));

        for (int i = 0; i < bestChain.size()-1; i++) {
            totalDistance += mInputMatrixForTspDistance[bestChain.get(i)][bestChain.get(i+1)];
            totalDuration += mInputMatrixForTspDuration[bestChain.get(i)][bestChain.get(i+1)] / 60;
        }

        for(int i = 0; i < bestChain.size(); i++){
            optimizedLocationListDistance.add(mLocationDetails.get(bestChain.get(i)));
        }

        gotoResultLists();
    }

    public List<Integer> getDijkstrasBestPath(int start, int end) {
        DijkstrasV2 dijkstras = new DijkstrasV2(mInputMatrixForTspDistance.length);
        StringBuilder logBuilder = new StringBuilder();

        // build graph from distance matrix
        for (int y = 0; y < mInputMatrixForTspDistance.length; y++) {
            for (int x = 0; x < mInputMatrixForTspDistance[y].length; x++) {
                if (y == x) continue; // skip empty cells in the matrix
                if (y == start && x == end
                        || x == start && y == end) {
                    continue; // do not add Vertex which connects starts and end
                }

                logBuilder.append(String.format("Add Edge(%d, %d)=%d\n", y, x, mInputMatrixForTspDistance[y][x]));
                dijkstras.addEdge(y, x, mInputMatrixForTspDistance[y][x]);
            }
        }
        Log.d("DijkstrasV2", logBuilder.toString());
        return dijkstras.reconstructPath(start, end);
    }

    public void getDijkstraRoute() {
        // start point at 0 and end point at last index in array
        List<Integer> bestPath = getDijkstrasBestPath(0, mInputMatrixForTspDistance.length-1);

        Log.d("best path", Arrays.toString(bestPath.toArray()));

        for (int i = 0; i < bestPath.size()-1; i++) {
            totalDistance += mInputMatrixForTspDistance[bestPath.get(i)][bestPath.get(i+1)];
            totalDuration += mInputMatrixForTspDuration[bestPath.get(i)][bestPath.get(i+1)] / 60;
        }

        for(int i = 0; i < bestPath.size(); i++){
            optimizedLocationListDistance.add(mLocationDetails.get(bestPath.get(i)));
        }

        gotoResultLists();
    }

    private void gotoResultLists() {
        Log.d("LocationListActivity", "gotoResultsActivity");
        Intent intent = new Intent(this, ResultLocationListActivity.class);
        intent.putExtra("optimizedLocationListDistance", optimizedLocationListDistance);
        intent.putExtra("optimizedLocationListDuration", optimizedLocationListDuration);
        intent.putExtra("isAntAlgo", isAntAlgo);
        Log.d("gotoResultLists", String.format("totalDistance=%d", totalDistance));
        Log.d("gotoResultLists", String.format("totalDuration=%d", totalDuration));
        intent.putExtra("totalDistance", HandyFunctions.convertMeterToKiloMeter(totalDistance));
        intent.putExtra("totalDuration", totalDuration);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRemoveLocation(int position) {
        resetLists();
    }
}
