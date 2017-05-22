package com.example.tsha.myapplication.network;


import com.example.tsha.myapplication.modle.Feature;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by ts.ha on 2017-04-24.
 */
interface RetrofitApi {
//    @Headers("appKey:d96f311a-665f-3819-9873-979415f08633")
    @GET("routes?callback=json&version=1&resCoordType=WGS84GEO&reqCoordType=WGS84GEO")
    Call<Feature> login(@Query("endX") String endX, @Query("endY") String endY,
                        @Query("startX") String startX, @Query("startY") String startY);
}
