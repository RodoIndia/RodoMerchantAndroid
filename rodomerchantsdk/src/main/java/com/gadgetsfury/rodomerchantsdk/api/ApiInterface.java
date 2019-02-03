package com.gadgetsfury.rodomerchantsdk.api;

import com.gadgetsfury.rodomerchantsdk.RodoLock;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("lock/getlocations")
    Call<List<RodoLock>> getLocations();

}

