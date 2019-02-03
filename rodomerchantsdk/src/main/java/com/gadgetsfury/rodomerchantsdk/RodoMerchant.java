package com.gadgetsfury.rodomerchantsdk;

import android.util.Log;

import com.gadgetsfury.rodomerchantsdk.api.ApiClient;
import com.gadgetsfury.rodomerchantsdk.api.ApiInterface;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RodoMerchant {

    private final String TAG = this.getClass().getSimpleName();

    private int interval = 10000; //Default 10 sec
    private RodoCallbackListener rodoCallbackListener;
    ApiInterface apiInterface;

    private Timer timer;

    private RodoMerchant(String merchantKey, String secretKey, int interval) {
        this.interval = interval;
        apiInterface = ApiClient.getClient(merchantKey, secretKey).create(ApiInterface.class);
    }

    public void setOnRodoCallbackListener(RodoCallbackListener rodoCallbackListener) {
        this.rodoCallbackListener = rodoCallbackListener;
        initialize();
    }

    private void initialize() {

        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                apiInterface.getLocations().enqueue(new Callback<List<RodoLock>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<RodoLock>> call, @NonNull Response<List<RodoLock>> response) {
                        if (response.body() != null) {

                            List<RodoLock> rodoLocks = response.body();

                            if (rodoLocks.size() > 0) {

                                if (rodoCallbackListener != null) {
                                    for (RodoLock rl : rodoLocks) {
                                        rodoCallbackListener.onLocationReceived(rl);
                                    }
                                } else {
                                    Log.e(TAG, "listener not set yet: ");
                                }

                            } else {
                                Log.e(TAG, "list size 0: ");
                                if (rodoCallbackListener != null) {
                                    rodoCallbackListener.onError(new Throwable("Lock list size is 0."));
                                }
                            }

                        } else {
                            Log.e(TAG, "null response: ");
                            if (rodoCallbackListener != null) {
                                rodoCallbackListener.onError(new Throwable("Credentials are wrong or tampered."));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<RodoLock>> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        if (rodoCallbackListener != null) {
                            rodoCallbackListener.onError(t);
                        }
                    }
                });

            }
        }, 0, interval);

    }

    public void unsubscribe() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public static class Builder {
        private String merchantKey;
        private String secretKey;
        private int interval;

        public Builder merchantKey(String merchantKey) {
            this.merchantKey = merchantKey;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder interval(int interval){
            if (interval < 5000) {
                return this;
            }
            this.interval = interval;
            return this;
        }

        public RodoMerchant build() {
            return new RodoMerchant(merchantKey, secretKey, interval);
        }
    }

}
