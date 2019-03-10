package com.gadgetsfury.rodomerchantsdk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RodoLock implements Parcelable {

    private String imei;

    private LatLng location;

    public RodoLock(String imei, LatLng location) {
        this.imei = imei;
        this.location = location;
    }

    protected RodoLock(Parcel in) {
        imei = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<RodoLock> CREATOR = new Creator<RodoLock>() {
        @Override
        public RodoLock createFromParcel(Parcel in) {
            return new RodoLock(in);
        }

        @Override
        public RodoLock[] newArray(int size) {
            return new RodoLock[size];
        }
    };

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RodoLock{" +
                "imei='" + imei + '\'' +
                ", location=" + location +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imei);
        parcel.writeParcelable(location, i);
    }

}
