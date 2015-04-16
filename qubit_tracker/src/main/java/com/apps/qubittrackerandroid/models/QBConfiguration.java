package com.apps.qubittrackerandroid.models;

import android.util.Log;

import com.apps.qubittrackerandroid.utils.QBUtils;
import com.orm.SugarRecord;

import java.util.List;

public class QBConfiguration extends SugarRecord<QBConfiguration> {

    public static String trackingID = "test-config";
    String  endpoint;
    String  configurationReloadInterval;
    String  queueTimeout;
    String  visitorID;
    String  sessionID;
    boolean sendAutoViewEvents;
    boolean sendAutoInteractionEvents;
    boolean sendGeoData;
    boolean disabled;

    long    timestamp;
    long    sessionTimestamp;

    public static boolean isRunningTimer = false;

    public QBConfiguration(){}

    public QBConfiguration(String endpoint, boolean sendAutoViewEvents, boolean sendAutoInteractionEvents, boolean sendGeoData, boolean disabled, String configurationReloadInterval, String queueTimeout, String visitorID) {
        this.endpoint = endpoint;
        this.sendAutoViewEvents = sendAutoViewEvents;
        this.sendAutoInteractionEvents = sendAutoInteractionEvents;
        this.sendGeoData = sendGeoData;
        this.disabled = disabled;
        this.configurationReloadInterval = configurationReloadInterval;
        this.queueTimeout = queueTimeout;
        this.visitorID = visitorID;
        this.sessionID = QBUtils.MD5(String.valueOf(System.currentTimeMillis()));
        this.sessionTimestamp = System.currentTimeMillis();
        this.timestamp = System.currentTimeMillis();
    }


    public String getTrackingID() {
        return trackingID;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getConfigurationReloadInterval() {
        return configurationReloadInterval;
    }

    public String getQueueTimeout() {
        return queueTimeout;
    }

    public String getVisitorID() {
        return visitorID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public boolean isSendAutoViewEvents() {
        return sendAutoViewEvents;
    }

    public boolean isSendAutoInteractionEvents() {
        return sendAutoInteractionEvents;
    }

    public boolean isSendGeoData() {
        return sendGeoData;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getSessionTimestamp() {
        return sessionTimestamp;
    }

    public void setTrackingID(String trackingID) {
        QBConfiguration.trackingID = trackingID;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setConfigurationReloadInterval(String configurationReloadInterval) {
        this.configurationReloadInterval = configurationReloadInterval;
    }

    public void setQueueTimeout(String queueTimeout) {
        this.queueTimeout = queueTimeout;
    }

    public void setVisitorID(String visitorID) {
        this.visitorID = visitorID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setSendAutoViewEvents(boolean sendAutoViewEvents) {
        this.sendAutoViewEvents = sendAutoViewEvents;
    }

    public void setSendAutoInteractionEvents(boolean sendAutoInteractionEvents) {
        this.sendAutoInteractionEvents = sendAutoInteractionEvents;
    }

    public void setSendGeoData(boolean sendGeoData) {
        this.sendGeoData = sendGeoData;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSessionTimestamp(long sessionTimestamp) {
        this.sessionTimestamp = sessionTimestamp;
    }

    static public String retrieveSessionId(){
        long currentTimestamp = System.currentTimeMillis();
        QBConfiguration conf = getConfFromDB();
        if(conf != null){
            if(currentTimestamp >= conf.getSessionTimestamp() + 1800000){ // 1800000ms = 30m
                Log.i("----retrieveSessionId", "INVALID SESSION, CREATING NEW...");
                conf.setSessionID(QBUtils.MD5(String.valueOf(System.currentTimeMillis())));
                conf.save();
            }
            conf.setSessionTimestamp(System.currentTimeMillis());
            conf.save();
            return conf.getSessionID();
        }
        return QBUtils.MD5(String.valueOf(System.currentTimeMillis()));
    }

    static public QBConfiguration getConfFromDB(){
        List<QBConfiguration> configs = QBConfiguration.listAll(QBConfiguration.class);

        if(configs.size() != 0)
            return configs.get(0);

        return null;
    }
}
