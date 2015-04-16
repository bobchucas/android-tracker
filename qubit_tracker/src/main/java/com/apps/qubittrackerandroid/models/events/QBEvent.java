package com.apps.qubittrackerandroid.models.events;

import com.orm.SugarRecord;

import org.json.JSONObject;

public class QBEvent extends SugarRecord {

    public QBEvent() {}

    //Override
    public JSONObject getJson(){
        return null;
    }

    //Override
    public void saveEvent(){

    }

    //Override
    public long getTimestamp(){
        return 0;
    }
}
