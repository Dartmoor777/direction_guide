package com.thyme.yaslan99.routeplannerapplication.Map.NotificationMap;

import android.content.Context;
import android.os.Vibrator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationAlert;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.Cons;

import java.util.ArrayList;

/**
 * Created by Yaroslava Landyga
 */

public class RpaOnNotificationMapReadyCallBack implements OnMapReadyCallback {
    private AppCompatActivity mActivity;
    private Vibrator mVibrate;
    private GoogleMap mGoogleMap;

    public RpaOnNotificationMapReadyCallBack(AppCompatActivity activity) {
        this.mActivity = activity;
        mVibrate = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showInputDialog(latLng);
            }
        });
        prepareMap();
//        mFireBaseDBUtils.readData("rpa-alerts");
    }

    private void showInputDialog(final LatLng latLng) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mActivity);
        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);
        final AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mActivity);
        alertDialogBuilderUserInput.setView(mView);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        final EditText notificationTitle = (EditText) mView.findViewById(R.id.notification_title);
        final EditText notificationMessage = (EditText) mView.findViewById(R.id.notification_message);
        Button postButton = (Button) mView.findViewById(R.id.notification_post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogAndroid.dismiss();
            }
        });

    }

    private void prepareMap() {
        mGoogleMap.setMinZoomPreference(Cons.MIN_ZOOM);
        mGoogleMap.setMaxZoomPreference(Cons.MAX_ZOOM);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Cons.KYIV_LATLNG));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cons.KYIV_LATLNG, Cons.CAMERA_ZOOM));
    }
}
