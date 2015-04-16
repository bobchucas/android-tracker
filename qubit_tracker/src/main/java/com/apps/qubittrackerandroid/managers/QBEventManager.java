package com.apps.qubittrackerandroid.managers;

import android.os.CountDownTimer;
import android.util.Log;

import com.android.volley.VolleyError;
import com.apps.qubittrackerandroid.QBLibrary;
import com.apps.qubittrackerandroid.api.APITask;
import com.apps.qubittrackerandroid.enums.APIJob;
import com.apps.qubittrackerandroid.interfaces.API;
import com.apps.qubittrackerandroid.models.QBConfiguration;
import com.apps.qubittrackerandroid.models.events.QBEventId;
import com.apps.qubittrackerandroid.models.events.QBEventInteraction;
import com.apps.qubittrackerandroid.models.events.QBEventUV;
import com.apps.qubittrackerandroid.models.events.QBEventView;
import com.apps.qubittrackerandroid.utils.QBUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QBEventManager implements API.OnAPIResponse {

    protected CountDownTimer sendEventsTimer;

    ArrayList<JSONObject> mEvents;
    ArrayList<Long> mEventsIds;

    APITask mApiTask;

    private static volatile QBEventManager instance;

    public static QBEventManager sharedInstance() {
        QBEventManager localInstance = instance;
        if (localInstance == null) {
            synchronized (QBEventManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new QBEventManager();
                }
            }
        }
        return localInstance;
    }

    public void handleEvents(){

        if(mApiTask == null) {
            mApiTask = new APITask(this);
            startTimerSendEvents();
        }

    }


    public void stopTimerSendEvents() {
        QBConfiguration.isRunningTimer = false;
        if(this.sendEventsTimer != null){
            this.sendEventsTimer.cancel();
            this.sendEventsTimer = null;
        }
    }

    public void startTimerSendEvents() {
        if(this.sendEventsTimer == null && !QBConfiguration.isRunningTimer) {
            QBConfiguration.isRunningTimer = true;
            this.sendEventsTimer = new CountDownTimer(Integer.MAX_VALUE, 1L * 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    groupAndSendEventsToAPI();
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }


    public void addEventAndId(JSONObject json, long id){
        mEvents.add(json);
        mEventsIds.add(id);
    }

    public void groupAndSendEventsToAPI(){

        mEvents = new ArrayList<>();
        mEventsIds = new ArrayList<>();

        long queueTimeout = 30;
        QBConfiguration conf = QBConfiguration.getConfFromDB();
        if(conf != null){
            queueTimeout = Long.parseLong(conf.getQueueTimeout());
        }
        final long finalQueueTimeout = queueTimeout;

        List<QBEventId> ids = QBEventId.listAll(QBEventId.class);
        for (int i = 0; i < ids.size(); i++){
            QBEventId id = ids.get(i);
            long eventId =  id.getEventId();

            //check if events is old
            if(id.get_cts() + finalQueueTimeout * 60 * 1000 < System.currentTimeMillis()){
                QBEventId eventID = QBEventId.findById(QBEventId.class, id.getId());
                if(eventID != null) {
                    eventID.delete();

                    if(id.get_type().equals("view")){
                        QBEventView eventV = QBEventView.findById(QBEventView.class, eventId);
                        if(eventV != null) eventV.delete();
                    }
                    if(id.get_type().equals("interaction")){
                        QBEventInteraction eventI = QBEventInteraction.findById(QBEventInteraction.class, eventId);
                        if(eventI != null) eventI.delete();
                    }
                    if(id.get_type().equals("uv")){
                        QBEventUV eventUV = QBEventUV.findById(QBEventUV.class, eventId);
                        if(eventUV != null) eventUV.delete();
                    }
                }

                continue;
            }

            if(id.get_type().equals("view")){
                QBEventView eventView = QBEventView.findById(QBEventView.class, eventId);
                if(eventView != null) addEventAndId(eventView.getJson(), id.getId());
            }
            if(id.get_type().equals("interaction")){
                QBEventInteraction eventInteraction = QBEventInteraction.findById(QBEventInteraction.class, eventId);
                if(eventInteraction != null) addEventAndId(eventInteraction.getJson(), id.getId());
            }
            if(id.get_type().equals("uv")){
                QBEventUV eventUV = QBEventUV.findById(QBEventUV.class, eventId);
                if(eventUV != null) addEventAndId(eventUV.getJson(), id.getId());
            }
        }

        if(mEvents.size() != 0){
            for (int e = 0; e < mEvents.size(); e++) {
                mApiTask.execAndExpectArray(getEventTrackerApiURL(), mEvents.get(e), APIJob.POST_EVENTS, mEventsIds.get(e), e);
                if(!QBUtils.checkConnection(QBLibrary.getGlobalQBContext(), false)){
                    mEvents.remove(e);
                    mEventsIds.remove(e);
                }
            }
        }
        else{
            if(QBUtils.checkConnection(QBLibrary.getGlobalQBContext(), false)) removeAllEvents();
        }
    }

    public String getEventTrackerApiURL () {
        String url = "";
        QBConfiguration config = QBConfiguration.getConfFromDB();
        if(config!=null){
            url = "https://" + config.getEndpoint() + "/" + config.getTrackingID() + "/" + config.getVisitorID();
        }

        //change API address, only for dev and tests
        return "http://192.168.21.117:5000/events/test_tracking_id/visitors/12f558a0-f9e9-68fe-7fec-09b22e1d";

        //only for live
        //return url;
    }

    public void removeSentEvents(long id){

        QBEventId eventID = QBEventId.findById(QBEventId.class, id);
        if(eventID != null){
            long eventId = eventID.getEventId();
            String eventType = eventID.get_type();

            eventID.delete();

            if(eventType.equals("view")){
                QBEventView eventV = QBEventView.findById(QBEventView.class, eventId);
                if(eventV != null) eventV.delete();
            }
            if(eventType.equals("interaction")){
                QBEventInteraction eventI = QBEventInteraction.findById(QBEventInteraction.class, eventId);
                if(eventI != null) eventI.delete();
            }
            if(eventType.equals("uv")){
                QBEventUV eventUV = QBEventUV.findById(QBEventUV.class, eventId);
                if(eventUV != null) eventUV.delete();
            }
        }
    }

    public void removeAllEvents(){
        QBEventId.deleteAll(QBEventId.class);
        QBEventView.deleteAll(QBEventView.class);
        QBEventInteraction.deleteAll(QBEventInteraction.class);
        QBEventUV.deleteAll(QBEventUV.class);
    }

    @Override
    public void onResponse(boolean isOK, JSONArray result, APIJob job, long id, int position) {
        if(isOK && job.equals(APIJob.POST_EVENTS)){
            removeSentEvents(id);
        }
    }

    @Override
    public void onResponse(boolean isOK, JSONObject result, APIJob job, long id, int position) {
        if(isOK && job.equals(APIJob.POST_EVENTS)){
            removeSentEvents(id);
            mEvents.remove(position);
            mEventsIds.remove(position);
        }
    }

    @Override
    public void onError(VolleyError error) {
        Log.i("----VolleyError", error.toString());
    }
}
