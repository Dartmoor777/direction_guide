package com.thyme.yaslan99.routeplannerapplication.Map.ResultMap;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.android.gms.maps.SupportMapFragment;
import com.thyme.yaslan99.routeplannerapplication.LocationList.OptimizationType;
import com.thyme.yaslan99.routeplannerapplication.Map.InputMap.MainActivity;
import com.thyme.yaslan99.routeplannerapplication.R;

public class ResultMapActivity extends AppCompatActivity {
    private RpaOnResultMapReadyCallBack mRpaOnResultMapReadyCallBack;
    SupportMapFragment mMapFragment;
    private Button mOptimizeButton;
    public Button mNextButton;
    private RadioGroup mRadioGroup;
    private RadioButton mByDistanceRadioButton;
    private RadioButton mByDurationRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_map);
        prepareUtils();
        prepareView();
    }

    private void prepareUtils() {
        mRpaOnResultMapReadyCallBack = new RpaOnResultMapReadyCallBack(this);
    }

    private void prepareView() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_result);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(mRpaOnResultMapReadyCallBack);

        mOptimizeButton = findViewById(R.id.opt_button);
        mOptimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                if(selectedId == mByDistanceRadioButton.getId()) {
                    mRpaOnResultMapReadyCallBack.drawRoute(OptimizationType.BY_DISTANCE);
                } else if(selectedId == mByDurationRadioButton.getId()) {
                    mRpaOnResultMapReadyCallBack.drawRoute(OptimizationType.BY_DURATION);
                }
                Log.d("resultMapActivity", "optimize button");
            }
        });

        mRadioGroup = findViewById(R.id.type_of_opt_radio_group);
        mByDistanceRadioButton = findViewById(R.id.by_distance);
        mByDistanceRadioButton.setChecked(true);
        mByDurationRadioButton = findViewById(R.id.by_duration);
        mNextButton = findViewById(R.id.go_main_button);
        mNextButton.setVisibility(View.GONE);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), MainActivity.class));
                finish();
            }
        });
    }

}
