package com.gadgetsfury.rodomerchantsdk.listeners;

public interface RodoCallbackListener {

    void onConnect();

    void onAuthorized();

    void onUnAuthorized(Throwable t);

    void onError(Throwable t);

    void onDisconnect();

}
