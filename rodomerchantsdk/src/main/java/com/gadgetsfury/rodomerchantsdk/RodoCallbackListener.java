package com.gadgetsfury.rodomerchantsdk;

public interface RodoCallbackListener {

    void onConnect();

    void onError(Throwable t);

    void onLocationReceived(RodoLock rodoLock);

}
