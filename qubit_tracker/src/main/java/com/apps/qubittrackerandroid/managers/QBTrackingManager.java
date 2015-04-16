package com.apps.qubittrackerandroid.managers;

import com.apps.qubittrackerandroid.QBLibrary;
import com.apps.qubittrackerandroid.init.QBTrackerInit;
import com.apps.qubittrackerandroid.models.QBConfiguration;
import com.apps.qubittrackerandroid.models.events.QBEventInteraction;
import com.apps.qubittrackerandroid.models.events.QBEventUV;
import com.apps.qubittrackerandroid.models.events.QBEventView;
import com.apps.qubittrackerandroid.utils.QBUtils;

public class QBTrackingManager {

    private static volatile QBTrackingManager instance;

    public static QBTrackingManager sharedManager() {
        QBTrackingManager localInstance = instance;
        if (localInstance == null) {
            synchronized (QBTrackingManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new QBTrackingManager();

                }
            }
        }
        return localInstance;
    }

    public void registerViewEvent(String viewID){
        QBConfiguration conf = QBConfiguration.getConfFromDB();
        QBEventView.viewId = viewID;

        if(conf != null && !conf.isDisabled() && conf.isSendAutoViewEvents()){
            QBEventView event = new QBEventView(QBLibrary.getGlobalQBContext());
            event.saveEvent();
            QBEventManager.sharedInstance().handleEvents();
        }
    }

    public void registerInteractionEvent(int viewID, String type, String name){
        if(viewID <= 0) return;

        QBConfiguration conf = QBConfiguration.getConfFromDB();
        QBEventView.viewId = QBUtils.getViewIdFromResources(QBLibrary.getGlobalQBContext().getResources().getResourceName(viewID));

        if(conf != null && !conf.isDisabled() && conf.isSendAutoInteractionEvents()){
            QBEventInteraction event = new QBEventInteraction(type, name);
            event.saveEvent();
            QBEventManager.sharedInstance().handleEvents();
        }
    }

    public void registerUvEvent(String viewID, String type, String data){
        QBConfiguration conf = QBConfiguration.getConfFromDB();
        QBEventView.viewId = viewID;

        if(conf != null && !conf.isDisabled()){
            QBEventUV event = new QBEventUV(type, data);
            event.saveEvent();
            QBEventManager.sharedInstance().handleEvents();
        }
    }

    public static void unsubscribeToTracking() {
        QBConfiguration conf = QBConfiguration.getConfFromDB();
        if(conf != null){
            conf.setDisabled(true);
            conf.save();
        }
        QBTrackerInit.sharedInstance().invalidateConfigTimer();
        QBEventManager.sharedInstance().stopTimerSendEvents();
    }

    public static void subscribeToTracking() {
        QBConfiguration conf = QBConfiguration.getConfFromDB();
        if(conf != null){
            conf.setDisabled(false);
            conf.save();
        }
        QBTrackerInit.sharedInstance().validateConfigTimer();
        QBEventManager.sharedInstance().startTimerSendEvents();
    }

}
