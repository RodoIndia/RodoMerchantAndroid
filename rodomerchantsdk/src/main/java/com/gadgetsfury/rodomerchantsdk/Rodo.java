package com.gadgetsfury.rodomerchantsdk;

import android.util.Log;

import com.gadgetsfury.rodomerchantsdk.api.APIClient;
import com.gadgetsfury.rodomerchantsdk.api.APIInterface;
import com.gadgetsfury.rodomerchantsdk.listeners.LocationCallbackListener;
import com.gadgetsfury.rodomerchantsdk.listeners.RodoCallbackListener;
import com.gadgetsfury.rodomerchantsdk.model.LatLng;
import com.gadgetsfury.rodomerchantsdk.model.LockStatus;
import com.gadgetsfury.rodomerchantsdk.model.RodoLock;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rodo {

    private final String TAG = this.getClass().getSimpleName();
    private final static String SERVER = "http://13.127.229.242:4200";

    private String merchantKey;
    private String secretKey;

    private LocationCallbackListener mLocationCallbackListener;
    private RodoCallbackListener mRodoCallbackListener;
    private Socket mSocket;

    private Emitter.Listener locationListener;

    private APIInterface mApiInterface;

    private Rodo(final String merchantKey, final String secretKey) {
        this.merchantKey = merchantKey;
        this.secretKey = secretKey;

        mApiInterface = APIClient.getClient(merchantKey, secretKey)
                .create(APIInterface.class);
    }

    public static class Builder {

        private String merchantKey;
        private String secretKey;

        public Rodo.Builder merchantKey(String merchantKey) {
            this.merchantKey = merchantKey;
            return this;
        }

        public Rodo.Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Rodo build() {
            if (merchantKey.isEmpty()) {
                throw new Error("Merchant Key cannot be null");
            }
            if (secretKey.isEmpty()) {
                throw new Error("Secret Key cannot be null");
            }

            return new Rodo(merchantKey, secretKey);
        }

    }

    public void getLocations(LatLng origin, Integer radius, LockStatus lockStatus, LocationCallbackListener locationCallbackListener) {
        mLocationCallbackListener = locationCallbackListener;

        if (mApiInterface == null) {
            throw new Error("Rodo object must be build first");
        }

        if ((radius == null && origin != null) || (origin == null && radius != null)) {
            throw new Error("Radius and Origin cannot be null at the same time. Either both has to be null or non null");
        }

        if (lockStatus == null) {
            throw new Error("Lock Status cannot be null");
        }

        if (radius != null) {

            if (radius <= 0) {
                throw new Error("Radius must be a positive number and cannot be null");
            }

            String o = origin.getLatitude() + "," + origin.getLongitude();
            String s;
            if (lockStatus == LockStatus.LOCKED) {
                s = "locked";
            } else if (lockStatus == LockStatus.UNLOCKED) {
                s = "unlocked";
            } else {
                s = null;
            }

            mApiInterface.getIds(radius, o, s)
                    .enqueue(new Callback<List<String>>() {
                        @Override
                        public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                            Log.e(TAG, "onResponse: " + response.body());
                            if (response.body() != null) {
                                for (String imei : response.body()) {
                                    Log.e(TAG, "locks : " + imei);
                                    mSocket.on("location/" + imei + "/" + merchantKey, locationListener);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<String>> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
        } else {
            mSocket.on("locations/" + merchantKey, locationListener);
        }

    }

    public void getLocation(String imei, LocationCallbackListener locationCallbackListener) {
        mLocationCallbackListener = locationCallbackListener;
        mSocket.on("location/" + imei + "/" + merchantKey, locationListener);
    }

    public void initialize(RodoCallbackListener rodoCallbackListener) {
        mRodoCallbackListener = rodoCallbackListener;
        try {
            mSocket = IO.socket(SERVER);
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    if (mRodoCallbackListener != null) {
                        mRodoCallbackListener.onConnect();
                    }
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("merchant_id", merchantKey);
                        obj.put("secret_key", secretKey);
                        Log.e(TAG, "call: " + obj.toString());
                        mSocket.emit("authentication", obj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onAuth: " + e.getMessage());
                    }
                }

            });
            mSocket.on("authenticated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "authenticated: ");
                    if (mRodoCallbackListener != null) {
                        mRodoCallbackListener.onAuthorized();
                    }
                }
            });
            mSocket.on("unauthorized", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "unauthorized: ");
                    if (mRodoCallbackListener != null) {
                        mRodoCallbackListener.onUnAuthorized(new Throwable("Credentials are tampered"));
                    }
                }
            });
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    if (mRodoCallbackListener != null) {
                        mRodoCallbackListener.onDisconnect();
                    }
                }

            });
            mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    if (mRodoCallbackListener != null) {
                        mRodoCallbackListener.onError(new Throwable("some error occurred"));
                    }
                }

            });
            mSocket.connect();

            locationListener = new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    if (args.length > 0 && mLocationCallbackListener != null) {
                        JSONObject obj = (JSONObject) args[0];
                        try {
                            double latitude = obj.getJSONObject("location").getDouble("lat");
                            double longitude = obj.getJSONObject("location").getDouble("lng");
                            String imei = obj.getString("imei");
                            mLocationCallbackListener.onLocationReceived(new RodoLock(imei, new LatLng(latitude, longitude)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onLocationError: " + e.getMessage());
                        }
                    }

                }
            };

        } catch (URISyntaxException e) {
            Log.e(TAG, "onListenRodoServer: " + e.getMessage());
            if (mRodoCallbackListener != null) {
                mRodoCallbackListener.onError(e);
            }
        }
    }

    public void destroy() {
        if (mSocket != null) {
            mSocket.disconnect();
            if (locationListener != null) {
                mSocket.off("locations/" + merchantKey, locationListener);
            }
        }
    }

}
