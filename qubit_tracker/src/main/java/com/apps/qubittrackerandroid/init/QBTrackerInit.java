package com.apps.qubittrackerandroid.init;

import android.os.CountDownTimer;
import android.util.Log;

import com.android.volley.VolleyError;
import com.apps.qubittrackerandroid.QBLibrary;
import com.apps.qubittrackerandroid.api.APITask;
import com.apps.qubittrackerandroid.enums.APIJob;
import com.apps.qubittrackerandroid.interfaces.API;
import com.apps.qubittrackerandroid.models.QBConfiguration;
import com.apps.qubittrackerandroid.utils.QBUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class QBTrackerInit implements API.OnAPIResponse{

    private static volatile QBTrackerInit instance;

    APITask mApiTask;

    public boolean timerIsRuning;
    public boolean isUserSubscribed;

    protected CountDownTimer configTimer;

    public static QBTrackerInit sharedInstance() {
        QBTrackerInit localInstance = instance;
        if (localInstance == null) {
            synchronized (QBTrackerInit.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new QBTrackerInit();
                    instance.isUserSubscribed = true;
                    localInstance.isUserSubscribed = true;

                    instance.timerIsRuning = false;
                    localInstance.timerIsRuning = false;
                }
            }
        }
        return localInstance;
    }

    public void initConfig(){
        //check if data is old, get configurations
        if(QBUtils.checkConnection(QBLibrary.getGlobalQBContext(), false) && isUserSubscribed){
            mApiTask = new APITask(this);
            mApiTask.exec("http://192.168.21.117:3000/" + QBConfiguration.trackingID + ".json", null, APIJob.GET_CONFIGS, 0, 0);
        }
        else {
            if(QBConfiguration.getConfFromDB() == null) {
                storeConfigurationToDB(defaultConfigData());
            }
        }
    }

    private void storeConfigurationToDB(JSONObject configurations){
        try {

            if(QBConfiguration.getConfFromDB() == null) {

                new QBConfiguration(
                        configurations.isNull("endpoint") ? "pong.qubitproducts.com/events/" : configurations.getString("endpoint"),
                        !configurations.isNull("send_auto_view_events") && configurations.getString("send_auto_view_events").equals("true"),
                        !configurations.isNull("send_auto_interaction_events") && configurations.getString("send_auto_interaction_events").equals("true"),
                        !configurations.isNull("send_geo_data") && configurations.getString("send_geo_data").equals("true"),
                        !configurations.isNull("disabled") && configurations.getString("disabled").equals("true"),
                        configurations.isNull("configuration_reload_interval") ? "60" : configurations.getString("configuration_reload_interval"),
                        configurations.isNull("queue_timeout") ? "30" : configurations.getString("queue_timeout"),
                        QBUtils.getUUID(QBLibrary.getGlobalQBContext())
                ).save();
            }
            else{

                QBConfiguration config = QBConfiguration.getConfFromDB();

                if(configurations.isNull("tracking_id_disable_override") && !configurations.isNull("tracking_id")){
                    config.setTrackingID(configurations.getString("tracking_id"));
                }
                if(configurations.isNull("endpoint_disable_override") && !configurations.isNull("endpoint")){
                    config.setEndpoint(configurations.getString("endpoint"));
                }
                if(configurations.isNull("configuration_reload_interval_disable_override") && !configurations.isNull("configuration_reload_interval")){
                    config.setConfigurationReloadInterval(configurations.getString("configuration_reload_interval"));
                }
                if(configurations.isNull("queue_timeout_disable_override") && !configurations.isNull("queue_timeout")){
                    config.setQueueTimeout(configurations.getString("queue_timeout"));
                }
                if(configurations.isNull("send_auto_view_events_disable_override") && !configurations.isNull("send_auto_view_events")){
                    config.setSendAutoViewEvents(configurations.getString("send_auto_view_events").equals("true"));
                }
                if(configurations.isNull("send_auto_interaction_events_disable_override") && !configurations.isNull("send_auto_interaction_events")){
                    config.setSendAutoInteractionEvents(configurations.getString("send_auto_interaction_events").equals("true"));
                }
                if(configurations.isNull("send_geo_data_disable_override") && !configurations.isNull("send_geo_data")){
                    config.setSendGeoData(configurations.getString("send_geo_data").equals("true"));
                }
                if(configurations.isNull("disabled_disable_override") && !configurations.isNull("disabled")){
                    config.setDisabled(configurations.getString("disabled").equals("true"));
                }
                config.setTimestamp(System.currentTimeMillis());
                config.save();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        validateConfigTimer();
    }

    private JSONObject defaultConfigData(){
        JSONObject data = new JSONObject();

        try {
            data.put("endpoint", "pong.qubitproducts.com/events/");
            data.put("configuration_reload_interval", "60");
            data.put("queue_timeout", "30");
            data.put("send_auto_view_events", "true");
            data.put("send_auto_interaction_events", "true");
            data.put("send_geo_data", "true");
            data.put("disabled", "false");
            return data;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponse(boolean isOK, JSONArray result, APIJob job, long id, int position) {

    }

    @Override
    public void onResponse(boolean isOK, JSONObject result, APIJob job, long id, int position) {

        if(isOK && job == APIJob.GET_CONFIGS){
            storeConfigurationToDB(result);
        }
    }

    @Override
    public void onError(VolleyError error) {
        Log.i("----VolleyError conf, storeConfigurationToDB", error.toString());
        storeConfigurationToDB(defaultConfigData());
    }

    public int getConfigTimeInterval(){
        QBConfiguration config = QBConfiguration.getConfFromDB();
        int intervalMills = 3600000; //1h default

        if(config != null){
            int intMillsConf = Integer.parseInt(config.getConfigurationReloadInterval()) * 60 * 1000;
            if(intMillsConf > 0){
                intervalMills = intMillsConf;
            }
        }
        Log.i("----getConfigTimeInterval",String.valueOf(intervalMills));
        return intervalMills;
    }

    public void invalidateConfigTimer() {
        isUserSubscribed = false;
        this.timerIsRuning = false;
        if(this.configTimer != null){
            this.configTimer.cancel();
            this.configTimer = null;
        }

    }

    public void validateConfigTimer() {
        isUserSubscribed = true;
        if(this.configTimer == null && !this.timerIsRuning) {
            this.timerIsRuning = true;
            this.configTimer = new CountDownTimer(Integer.MAX_VALUE, getConfigTimeInterval()) {
                @Override
                public void onTick(long millisUntilFinished) {
                    initConfig();
                    Log.i("----validateConfigTimer", "onTick()");
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }

}
