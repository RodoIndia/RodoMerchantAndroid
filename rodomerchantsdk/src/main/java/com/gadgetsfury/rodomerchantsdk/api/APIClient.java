package com.gadgetsfury.rodomerchantsdk.api;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static String TAG = APIClient.class.getSimpleName();

    private static final String BASE_URL = "http://merchant.rodoindia.com:4300/v0/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final String merchantKey, final String secretKey) {

        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            //Create a new Interceptor.
            Interceptor headerAuthorizationInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    okhttp3.Request request = chain.request();
                    Headers headers = request.headers().newBuilder()
                            .add("Content-Type", "application/json")
                            .add("merchant_id", merchantKey)
                            .add("Authorization", "Bearer " + secretKey).build();
                    request = request.newBuilder().headers(headers).build();
                    return chain.proceed(request);
                }
            };

            clientBuilder.addInterceptor(headerAuthorizationInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build();
        }

        return retrofit;
    }

}