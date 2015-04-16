package com.apps.qubittrackerandroid.models.events;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class QBEventUV extends QBEvent {

    String type;
    String data;

    QBEventId eventID;

    public QBEventUV(){}

    public QBEventUV(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public void saveEvent(){
        save();
        this.eventID = new QBEventId(QBEventView.viewId, "uv", getId());
        this.eventID.save();
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp(){
        List<QBEventId> ids = QBEventId.find(QBEventId.class, "_type = ? and event_id = ?", "uv", getId().toString());
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
                List<QBEventId> ids = QBEventId.find(QBEventId.class, "_type = ? and event_id = ?", "uv", getId().toString());
                if(ids.size() != 0){
                    jsonObject.put("_cid", ids.get(0).get_cid());
                    jsonObject.put("_cts", ids.get(0).get_cts());
                    jsonObject.put("sessionId", ids.get(0).getSessionId());
                    jsonObject.put("viewId", ids.get(0).getViewId());
                    jsonObject.put("_type", ids.get(0).get_type());
                }
            }

            jsonObject.put("type", this.type);
            jsonObject.put("data", this.data);

            return jsonObject;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new JSONObject();
        }

    }
}
