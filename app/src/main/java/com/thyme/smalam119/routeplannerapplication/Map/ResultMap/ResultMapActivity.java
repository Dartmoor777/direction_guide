package com.thyme.smalam119.routeplannerapplication.Map.ResultMap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.android.gms.maps.SupportMapFragment;
import com.thyme.smalam119.routeplannerapplication.LocationList.OptimizationType;
import com.thyme.smalam119.routeplannerapplication.R;

public class ResultMapActivity extends AppCompatActivity {

    private RpaOnResultMapReadyCallBack mRpaOnResultMapReadyCallBack;
    private Button mOptimizeButton;
    public Button mNextButton;
    private RadioGroup mRadioGroup;
    private RadioButton mByDistanceRadioButton;
    private RadioButton mByDurationRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRpaOnResultMapReadyCallBack = new RpaOnResultMapReadyCallBack(this);
        setContentView(R.layout.activity_result_map);
        prepareView();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_result);
        mapFragment.getMapAsync(mRpaOnResultMapReadyCallBack);
    }

    private void prepareView() {
        mOptimizeButton = (Button) findViewById(R.id.opt_button);
        mOptimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                if(selectedId == mByDistanceRadioButton.getId()) {
                    mRpaOnResultMapReadyCallBack.drawRoute(OptimizationType.BY_DISTANCE);
                } else if(selectedId == mByDurationRadioButton.getId()) {
                    mRpaOnResultMapReadyCallBack.drawRoute(OptimizationType.BY_DURATION);
                }
            }
        });
        mRadioGroup = (RadioGroup) findViewById(R.id.type_of_opt_radio_group);
        mByDistanceRadioButton = (RadioButton) findViewById(R.id.by_distance);
        mByDistanceRadioButton.setChecked(true);
        mByDurationRadioButton = (RadioButton) findViewById(R.id.by_duration);
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setVisibility(View.GONE);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


}
