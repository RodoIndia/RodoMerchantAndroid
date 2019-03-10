package com.gadgetsfury.rodomerchant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.gadgetsfury.rodomerchantsdk.Rodo;
import com.gadgetsfury.rodomerchantsdk.listeners.LocationCallbackListener;
import com.gadgetsfury.rodomerchantsdk.listeners.RodoCallbackListener;
import com.gadgetsfury.rodomerchantsdk.model.LatLng;
import com.gadgetsfury.rodomerchantsdk.model.LockStatus;
import com.gadgetsfury.rodomerchantsdk.model.RodoLock;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Rodo rodo;

    private String merchantKey = "2382hd23223";
    private String secretKey = "eyJhbGciOiJIUzI1NiJ9.MjM4MmhkMjMyMjM.v-D7_sYM_oSaYnL1QlbMU0vXexjf-7KK1VXmQVXiT6c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //build Rodo object with MerchantKey and SecretKey
        rodo = new Rodo.Builder()
                .merchantKey(merchantKey)
                .secretKey(secretKey)
                .build();

        //initialize and start rodo service
        rodo.initialize(new RodoCallbackListener() {
            @Override
            public void onConnect() {
                Log.e(TAG, "onConnect: ");
            }

            @Override
            public void onAuthorized() {
                Log.e(TAG, "onAuthorized: " );
            }

            @Override
            public void onUnAuthorized(Throwable t) {
                Log.e(TAG, "onUnAuthorized: " + t.getMessage() );
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onDisconnect() {
                Log.e(TAG, "onDisconnect: ");
            }
        });

        //get locations of all your locks
        rodo.getLocations(new LatLng(23.547368,87.289595),4000, LockStatus.ALL, new LocationCallbackListener() {
            @Override
            public void onLocationReceived(RodoLock list) {
                Log.e(TAG, "onLocationsReceived: " + list.toString());
            }
        });

        //get location of a single lock
        rodo.getLocation("12345", new LocationCallbackListener() {
            @Override
            public void onLocationReceived(RodoLock rodoLock) {
                Log.e(TAG, "onLocationReceivedSingle: " + rodoLock.toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        //destroy rodo service
        if (rodo != null) {
            rodo.destroy();
        }
        super.onDestroy();
    }
}
