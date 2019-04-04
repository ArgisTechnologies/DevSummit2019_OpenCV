package com.logicpd.papapill.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.logicpd.papapill.App;

public class ConnectionReceiver extends BroadcastReceiver {
    public static ConnectionReceiverListener connectionReceiverListener;

    public ConnectionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (connectionReceiverListener != null) {
                connectionReceiverListener.onNetworkConnectionChanged(isConnected());
            }
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectionReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
