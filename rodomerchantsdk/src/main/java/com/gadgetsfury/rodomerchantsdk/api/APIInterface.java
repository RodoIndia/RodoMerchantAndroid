package com.gadgetsfury.rodomerchantsdk.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("lock/getlocks/{radius}/{origin}")
    Call<List<String>> getIds(@Path("radius") int radius, @Path("origin") String origin, @Query("status") String status);

}
