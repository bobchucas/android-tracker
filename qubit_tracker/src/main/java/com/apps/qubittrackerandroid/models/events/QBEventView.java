package com.apps.qubittrackerandroid.models.events;

import android.content.Context;

import com.apps.qubittrackerandroid.models.QBConfiguration;
import com.apps.qubittrackerandroid.models.QBView;
import com.apps.qubittrackerandroid.utils.GPSTracker;
import com.orm.dsl.Ignore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class QBEventView extends QBEvent {

    String libName;
    String libVersion;
    int    viewCount;
    int    sessionViewCount;
    double latitude;
    double longitude;
    String deviceType;
    String osName;
    String osVersion;

    @Ignore
    QBEventId eventID;

    @Ignore
    Context context;

    public static String viewId;

    public QBEventView(){}

    public QBEventView(Context context) {
        this.libName = "Android";
        this.libVersion = "0.0.1_DEV";
        this.deviceType = "mobile";
        this.osName = "Android, " + System.getProperty("os.name").toLowerCase() + " v." + System.getProperty("os.version").toLowerCase() + " " + System.getProperty("os.arch").toLowerCase();
        this.osVersion = "api_lvl_" + android.os.Build.VERSION.SDK_INT;

        QBView counts = new QBView(QBEventView.viewId);
        counts.saveView();
        this.viewCount = QBView.getViewCount(false);
        this.sessionViewCount = QBView.getViewCount(true);

        this.context = context;
        detectMyLocation();
    }

    public void saveEvent(){
        save();
        this.eventID = new QBEventId(QBEventView.viewId, "view", getId());
        this.eventID.save();
    }

    public long getTimestamp(){
        List<QBEventId> ids = QBEventId.find(QBEventId.class, "_type = ? and event_id = ?", "view", getId().toString());
        if(ids.size() != 0){
            return ids.get(0).get_cts();
        }
        return 0;
    }

    public JSONObject getJson(){
        JSONObject jsonObject= new JSONObject();
        try {

            if(this.eventID != null && this.eventID.getId() > 0){
                jsonObject.put("_cid", this.eventID.get_cid());
                jsonObject.put("_cts", this.eventID.get_cts());
                jsonObject.put("sessionId", this.eventID.getSessionId());
                jsonObject.put("viewId", this.eventID.getViewId());
                jsonObject.put("_type", this.eventID.get_type());
            }
            else{
                List<QBEventId> ids = QBEventId.find(QBEventId.class, "_type = ? and event_id = ?", "view", getId().toString());
                if(ids.size() != 0){
                    jsonObject.put("_cid", ids.get(0).get_cid());
                    jsonObject.put("_cts", ids.get(0).get_cts());
                    jsonObject.put("sessionId", ids.get(0).getSessionId());
                    jsonObject.put("viewId", ids.get(0).getViewId());
                    jsonObject.put("_type", ids.get(0).get_type());
                }
            }

            jsonObject.put("libName", this.libName);
            jsonObject.put("libVersion", this.libVersion);
            jsonObject.put("viewCount", this.viewCount);
            jsonObject.put("sessionViewCount", this.sessionViewCount);
            jsonObject.put("deviceType", this.deviceType);
            jsonObject.put("osName", this.osName);
            jsonObject.put("osVersion", this.osVersion);

            if(QBConfiguration.getConfFromDB() != null && QBConfiguration.getConfFromDB().isSendGeoData()){
                JSONObject jsonLocation= new JSONObject();
                jsonLocation.put("latitude", this.latitude);
                jsonLocation.put("longitude", this.longitude);
                jsonObject.put("location", jsonLocation);
            }

            return jsonObject;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new JSONObject();
        }

    }

    public void detectMyLocation() {
        GPSTracker gpsTracker = new GPSTracker(this.context);
        if (gpsTracker.canGetLocation()) {
            this.latitude = gpsTracker.getLatitude();
            this.longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }
}
