package com.thyme.yaslan99.routeplannerapplication.ResultLocationList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.Alerts;

import java.util.ArrayList;

/**
 * Created by Yaroslava Landyga
 */

public class ResultLocationListAdapter extends RecyclerView.Adapter<ResultLocationListViewHolder> {
    private AppCompatActivity mContext;
    private ArrayList<LocationDetail> locationDetails;

    public ResultLocationListAdapter(AppCompatActivity mContext, ArrayList<LocationDetail> locationDetails) {
        this.mContext = mContext;
        this.locationDetails = locationDetails;
    }

    @Override
    public ResultLocationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_location_cell, parent, false);

        return new ResultLocationListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResultLocationListViewHolder holder, int position) {
        final LocationDetail locationDetail = locationDetails.get(position);

        holder.sequenceNumber.setText(position + 1 + "");
        holder.sequenceNumber.setTextColor(locationDetail.getIdentifierColor());
        holder.locationName.setText(locationDetail.getLocationTitle());
        holder.locationName.setTextColor(locationDetail.getIdentifierColor());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alerts.simpleAlertWithMessage(mContext,locationDetail.getLocationTitle(),locationDetail.getAddressLine(),"Okay");
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationDetails.size();
    }
}
