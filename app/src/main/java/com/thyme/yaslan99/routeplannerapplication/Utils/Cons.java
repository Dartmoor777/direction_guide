package com.thyme.yaslan99.routeplannerapplication.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Yaroslava Landyga
 */

public class Cons {

    public static final String KEY_EXTRA_LOCATION_ARRAY_LIST = "locationArrayList";
    public static final LatLngBounds KYIV_BOUND = new LatLngBounds(
            new LatLng(50.4016991, 30.2525147), new LatLng(50.4500718, 30.5234528));
    public static final LatLng KYIV_LATLNG = new LatLng(50.431782, 30.5234528);
    public static final float MIN_ZOOM = 10.0f;
    public static final float MAX_ZOOM = 16.0f;
    public static final float CAMERA_ZOOM = 11.5f;
    public static final int MIN_LOCATION_COUNT = 3;
    public static final int MAX_LOCATION_COUNT = 10;
    // public static final LatLng BUET_LATLNG = new LatLng(23.7265631, 90.3886909);
}
