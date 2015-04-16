package com.apps.qubittrackerandroid.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.apps.qubittrackerandroid.init.QBTrackerInit;
import com.apps.qubittrackerandroid.models.QBConfiguration;

public class StateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //network
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected()) {

                //update configurations if needed
                QBConfiguration conf = QBConfiguration.getConfFromDB();
                if(conf != null){
                    long timestamp = System.currentTimeMillis();
                    long updateTimestamp = conf.getTimestamp();
                    long confInterval = Long.parseLong(conf.getConfigurationReloadInterval()) * 60 * 1000;
                    Log.i("----NetworkStateChangeReceiver", "connection is established");
                    if(timestamp > updateTimestamp + confInterval){
                        Log.i("----NetworkStateChangeReceiver", "reload configurations");
                        QBTrackerInit.sharedInstance().initConfig();
                    }
                }
            }
        }
    }
}