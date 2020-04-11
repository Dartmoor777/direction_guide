package com.thyme.yaslan99.routeplannerapplication.ResultLocationList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thyme.yaslan99.routeplannerapplication.R;

/**
 * Created by Yaroslava Landyga
 */

public class ResultLocationListViewHolder extends RecyclerView.ViewHolder {
    public TextView sequenceNumber, locationName;
    public CardView cardView;

    public ResultLocationListViewHolder(View itemView) {
        super(itemView);
        sequenceNumber = (TextView) itemView.findViewById(R.id.sequence_number_text_view);
        locationName = (TextView) itemView.findViewById(R.id.location_name_text_view);
        cardView = (CardView) itemView.findViewById(R.id.card_view_holder);
    }
}
