package com.thyme.yaslan99.routeplannerapplication.ResultLocationList;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.thyme.yaslan99.routeplannerapplication.LocationList.LocationListActivity;
import com.thyme.yaslan99.routeplannerapplication.LocationList.VerticalSpaceItemDecoration;
import com.thyme.yaslan99.routeplannerapplication.Map.ResultMap.ResultMapActivity;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.Model.User.SinglePath;
import com.thyme.yaslan99.routeplannerapplication.Model.User.SingleRoute;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.Alerts;

import java.util.ArrayList;

public class ResultLocationListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TextView mTotalDistanceTV, mTotalDurationTV;
    private Button /* mSaveResultButton,*/ mContinueButton;
    private ResultLocationListAdapter mResultLocationListAdapter;
    private ArrayList<LocationDetail> optimizedLocationListDistance;
    private ArrayList<LocationDetail> optimizedLocationListDuration;
    private boolean isAntAlgo;
    private double mTotalDistance;
    private int mTotalDuration;
    private ArrayList<SingleRoute> mPreviousRoutes;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_location_list);
        prepareUtils();
        prepareView();
    }

    private void prepareUtils() {
        prepareLists();
        mResultLocationListAdapter = new ResultLocationListAdapter(this,optimizedLocationListDistance);
    }

    private void prepareLists() {
        isAntAlgo = (boolean) getIntent().getSerializableExtra("isAntAlgo");
        optimizedLocationListDistance = (ArrayList<LocationDetail>) getIntent().getSerializableExtra("optimizedLocationListDistance");
        optimizedLocationListDuration = (ArrayList<LocationDetail>) getIntent().getSerializableExtra("optimizedLocationListDuration");
        mTotalDistance = getIntent().getDoubleExtra("totalDistance",0.0);
        mTotalDuration = getIntent().getIntExtra("totalDuration",0);
        mPreviousRoutes = new ArrayList<>();
    }

    private void prepareView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.result_location_recycler_view);
        mTotalDistanceTV = (TextView) findViewById(R.id.total_distance);
        mTotalDurationTV = (TextView) findViewById(R.id.total_duration);
//        mSaveResultButton = (Button) findViewById(R.id.save_result_button);
//        mSaveResultButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isSaved) {
//                    Alerts.showSimpleWarning(ResultLocationListActivity.this,"Saved",
//                            "Already Location Saved");
//                } else {
//                    sendSingleRouteData();
//                }
//            }
//        });

        mContinueButton = (Button) findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoResultMap();
            }
        });

        mTotalDistanceTV.setText(getString(R.string.total_distance, mTotalDistance));
        mTotalDurationTV.setText(getString(R.string.total_duration, mTotalDuration / 60, mTotalDuration % 60));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mResultLocationListAdapter);
    }

    private void sendSingleRouteData() {
        ArrayList<SinglePath> singlePaths = new ArrayList<>();
        ArrayList<SingleRoute> singleRoutes = new ArrayList<>();
        for(LocationDetail locationDetail: optimizedLocationListDistance) {
            SinglePath singlePath = new SinglePath(locationDetail.getLocationTitle(),Double.valueOf(locationDetail.getLat()),
                    Double.valueOf(locationDetail.getLng()),1);
            singlePaths.add(singlePath);
        }
        SingleRoute singleRoute = new SingleRoute(mTotalDistance,mTotalDuration,singlePaths);
        singleRoutes.add(singleRoute);
        for(SingleRoute previousSingleRoute: mPreviousRoutes) {
            singleRoutes.add(previousSingleRoute);
        }
        isSaved = true;
        Alerts.showSimpleWarning(ResultLocationListActivity.this,"Saved",
                "Location Saved");
    }

    private void gotoResultMap() {
        Intent intent = new Intent(ResultLocationListActivity.this, ResultMapActivity.class);
        intent.putExtra("optimizedLocationListDistance", optimizedLocationListDistance);
        intent.putExtra("optimizedLocationListDuration", optimizedLocationListDuration);
        intent.putExtra("isAntAlgo", isAntAlgo);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LocationListActivity.class));
    }

}