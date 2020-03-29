package com.thyme.yaslan99.routeplannerapplication.NetworkCalls;

import com.thyme.yaslan99.routeplannerapplication.Model.Direction.Example;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yaroslava Landyga
 */

public interface ApiInterface {
    @GET("api/directions/json")
    Call<Example> getDistanceDuration(
            @Query("units") String units,
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode,
            @Query("key") String key
    );
}
