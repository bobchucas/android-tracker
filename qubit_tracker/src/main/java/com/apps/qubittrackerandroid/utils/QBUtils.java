package com.apps.qubittrackerandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QBUtils {


    public static boolean checkConnection(Context context, boolean onlyWiFi){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        if(!onlyWiFi){
            return isConnected;
        }
        else{
            return isConnected && isWiFi;
        }
    }

    public static String MD5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUUID(Context context){
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

    public static String getViewIdFromResources(String resourceAddr){
        String[] parts = resourceAddr.split("/");
        if(parts.length > 0){
            return parts[parts.length-1];
        }
        else{
            return resourceAddr;
        }
    }

    public static String getViewIdFromFragments(String fragmentInstance, int containerId){
        fragmentInstance = fragmentInstance.replace("{", "::");
        String[] parts = fragmentInstance.split("::");
        if(parts.length > 0){
            return parts[0] + ":" + containerId;
        }
        else{
            return fragmentInstance + ":" + containerId;
        }
    }

    public static String getViewIdFromPackage(String className){
        className = className.replace(".", "::");
        String[] parts = className.split("::");
        if(parts.length > 0){
            return parts[parts.length-1] + ":" + QBUtils.MD5(System.currentTimeMillis()+"");
        }
        else{
            return className + ":" + QBUtils.MD5(System.currentTimeMillis()+"");
        }
    }
}
