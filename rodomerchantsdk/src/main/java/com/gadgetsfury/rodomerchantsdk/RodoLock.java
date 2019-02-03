package com.gadgetsfury.rodomerchantsdk;

import com.google.gson.annotations.SerializedName;

public class RodoLock {

    @SerializedName("imei")
    private String imei;

    @SerializedName("t")
    private long t;

    @SerializedName("x")
    private double x;

    @SerializedName("y")
    private double y;

    public RodoLock() {
    }

    public RodoLock(String imei, long timestamp, double latitude, double longitude) {
        this.imei = imei;
        this.t = timestamp;
        this.x = latitude;
        this.y = longitude;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getTimestamp() {
        return t;
    }

    public void setTimestamp(long timestamp) {
        this.t = timestamp;
    }

    public double getLatitude() {
        return x;
    }

    public void setLatitude(double latitude) {
        this.x = latitude;
    }

    public double getLongitude() {
        return y;
    }

    public void setLongitude(double longitude) {
        this.y = longitude;
    }

    @Override
    public String toString() {
        return "RodoLock{" +
                "imei='" + imei + '\'' +
                ", t=" + t +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

}
