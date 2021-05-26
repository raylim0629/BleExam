package com.exam.ble.central;

public interface CentralCallback {

    void requestEnableBLE();

    void requestLocationPermission();

    void onStatusMsg(final String message);

    void onTimerMsg(final String message);

    void onTouchMsg(final String message);

    void onIncreaseTouchCounter();

    int onGetTouchCounter();

    void onResetCounter();

    void onStartCounter();

    void onStopCounter();

    void onToast(final String message);
}
