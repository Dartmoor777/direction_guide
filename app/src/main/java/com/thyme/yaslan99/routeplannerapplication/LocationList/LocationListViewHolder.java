package com.thyme.yaslan99.routeplannerapplication.LocationList;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.thyme.yaslan99.routeplannerapplication.R;

/**
 * Created by Yaroslava Landyga
 */

public class LocationListViewHolder extends RecyclerView.ViewHolder {

    public TextView locationTitleTV, addressLineTV, latlngTV;
    public ImageButton crossButton;

    public LocationListViewHolder(View itemView) {
        super(itemView);
        locationTitleTV = itemView.findViewById(R.id.location_title_list_view);
        addressLineTV = itemView.findViewById(R.id.location_info_list_view);
        latlngTV = itemView.findViewById(R.id.latlng_list_view);
        crossButton = itemView.findViewById(R.id.cross_button);
    }
}
