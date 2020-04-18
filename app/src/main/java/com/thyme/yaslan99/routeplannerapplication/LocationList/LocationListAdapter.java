package com.thyme.yaslan99.routeplannerapplication.LocationList;

/**
 * Created by Yaroslava Landyga
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;
import com.thyme.yaslan99.routeplannerapplication.R;
import com.thyme.yaslan99.routeplannerapplication.Utils.LocationDetailSharedPrefUtils;

import java.util.ArrayList;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListViewHolder> {
    private Context mContext;
    private LocationDetailSharedPrefUtils mLocationDetailSharedPrefUtils;
    public ArrayList<LocationDetail> locationDetails;
    public OnAdapterValueChanged onAdapterValueChanged;

    public LocationListAdapter(Context context, ArrayList<LocationDetail> locationDetails) {
        this.mContext = context;
        mLocationDetailSharedPrefUtils = new LocationDetailSharedPrefUtils(context);
        this.locationDetails = locationDetails;
    }

    @Override
    public LocationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_cell, parent, false);

        return new LocationListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LocationListViewHolder holder, final int position) {
        LocationDetail locationDetail = locationDetails.get(position);

        holder.locationTitleTV.setText(locationDetail.getLocationTitle());
        holder.locationTitleTV.setTextColor(locationDetail.getIdentifierColor());
        holder.addressLineTV.setText(locationDetail.getAddressLine());
        holder.latlngTV.setText(locationDetail.getLat() + ", " + locationDetail.getLng());
        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDetails.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,locationDetails.size());
                mLocationDetailSharedPrefUtils.setLocationDataToSharedPref(locationDetails);
                onAdapterValueChanged.onRemoveLocation(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationDetails.size();
    }
}
