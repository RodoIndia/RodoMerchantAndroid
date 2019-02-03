package com.gadgetsfury.rodomerchant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.gadgetsfury.rodomerchantsdk.RodoCallbackListener;
import com.gadgetsfury.rodomerchantsdk.RodoLock;
import com.gadgetsfury.rodomerchantsdk.RodoMerchant;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private RodoMerchant rodoMerchant;
    private String merchantKey = "2382hd23223";
    private String secretKey = "eyJhbGciOiJIUzI1NiJ9.MjM4MmhkMjMyMjM.NTrIb5ujzoKfNaU4URbH77fA7SykyODsEJlGGbqjyQA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rodoMerchant = new RodoMerchant.Builder()
                .merchantKey(merchantKey)
                .secretKey(secretKey)
                .interval(4000)
                .build();

        rodoMerchant.setOnRodoCallbackListener(new RodoCallbackListener() {
            @Override
            public void onConnect() {
                Log.e(TAG, "onConnect: " );
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: " + t.getMessage() );
            }

            @Override
            public void onLocationReceived(RodoLock rodoLock) {
                Log.e(TAG, "onLocationReceived:" + rodoLock.toString() );
            }
        });

    }

}
