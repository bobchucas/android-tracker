package com.apps.qubittrackerandroid.models.events;


import com.apps.qubittrackerandroid.models.QBConfiguration;
import com.apps.qubittrackerandroid.utils.QBUtils;
import com.orm.SugarRecord;

public class QBEventId extends SugarRecord<QBEventId> {

    String _cid;
    long   _cts;
    String sessionId;
    String viewId;
    String _type;
    long   eventId;

    public QBEventId(){}

    public QBEventId(String viewId, String _type, long eventId) {
        this.viewId = viewId;
        this._type = _type;
        this.eventId = eventId;

        this._cid = QBUtils.MD5(String.valueOf(System.currentTimeMillis()));
        this._cts = System.currentTimeMillis();
        this.sessionId = QBConfiguration.retrieveSessionId();
    }

    //for save use ORM Sugar method, ex: new QBEventId(...).save();


    public String get_cid() {
        return _cid;
    }

    public long get_cts() {
        return _cts;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getViewId() {
        return viewId;
    }

    public String get_type() {
        return _type;
    }

    public long getEventId() {
        return eventId;
    }
}
