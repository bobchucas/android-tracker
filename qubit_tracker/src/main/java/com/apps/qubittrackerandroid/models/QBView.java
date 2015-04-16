package com.apps.qubittrackerandroid.models;

import com.orm.SugarRecord;

import java.util.List;

public class QBView extends SugarRecord<QBView> {

    String viewName;
    String sessionId;

    public QBView(){}

    public QBView(String viewName) {
        this.viewName = viewName;
        this.sessionId = QBConfiguration.retrieveSessionId();
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public void setSessionID(String sessionID) {
        this.sessionId = sessionID;
    }

    public String getViewName() {
        return viewName;
    }

    public String getSessionID() {
        return sessionId;
    }

    public void saveView(){
        List<QBView> check = QBView.find(QBView.class, "view_name = ? and session_id = ?", this.viewName, this.sessionId);
        if(check.size() == 0){

            List<QBView> checkV = QBView.find(QBView.class, "view_name = ?", this.viewName);
            if(checkV.size() == 0){
                save();
            }
            else {
                for (int i=0; i < checkV.size(); i++){
                    QBView view = checkV.get(i);
                    view.setViewName(this.viewName);
                    view.setSessionID(this.sessionId);
                    view.save();
                }
            }
        }
        else{
            for (int i=0; i < check.size(); i++){
                QBView view = check.get(i);
                view.setViewName(this.viewName);
                view.setSessionID(this.sessionId);
                view.save();
            }
        }

    }

    static public int getViewCount(boolean bySession){
        List<QBView> array;
        if(bySession){
            array = QBView.find(QBView.class, "session_id = ?", QBConfiguration.retrieveSessionId());
        }
        else{
            array = QBView.listAll(QBView.class);
        }

        return array.size();
    }
}
