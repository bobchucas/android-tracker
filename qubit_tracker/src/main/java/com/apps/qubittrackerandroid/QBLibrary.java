package com.apps.qubittrackerandroid;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orm.SugarApp;

public class QBLibrary extends SugarApp {

    private static Context globalQBContext;

    public static String APP_TAG = "QBTrackerLibrary";

    private static QBLibrary appInstance;
    private RequestQueue appRequestQueue;

    public static synchronized QBLibrary getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        QBLibrary.globalQBContext = getApplicationContext();
        appInstance = this;
    }

    public static Context getGlobalQBContext() {
        return QBLibrary.globalQBContext;
    }

    public RequestQueue getRequestQueue() {
        if (appRequestQueue == null) {
            appRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return appRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req).setTag(APP_TAG);
    }

    public void cancelPendingRequests(Object tag) {
        if (appRequestQueue != null) {
            appRequestQueue.cancelAll(tag);
        }
    }


}
