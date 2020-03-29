package com.thyme.yaslan99.routeplannerapplication.Map.InputMap;

import com.thyme.yaslan99.routeplannerapplication.Model.LocationDetail;

/**
 * Created by Yaroslava Landyga
 */

public interface OnMapInteractionCallBack {

    void onMapLongClick(LocationDetail locationDetail);
    void onMapClick();
}
