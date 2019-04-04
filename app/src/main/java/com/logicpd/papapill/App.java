package com.logicpd.papapill;

import android.app.Application;
import android.content.Context;

import com.logicpd.papapill.receivers.ConnectionReceiver;

public class App extends Application {
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }

    public static Context getContext() {
        return mInstance;
    }
}
