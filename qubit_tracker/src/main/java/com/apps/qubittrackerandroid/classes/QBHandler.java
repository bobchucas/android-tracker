package com.apps.qubittrackerandroid.classes;

import android.view.MotionEvent;
import android.view.View;

import com.apps.qubittrackerandroid.managers.QBTrackingManager;

public class QBHandler {

    static public int[] idsForTouchCapture = new int[]{};
    static public int[] idsForClickCapture = new int[]{};

    public static boolean handleOnTouch(View v, MotionEvent event){

        int vId = v.getId();
        int eventAction = event.getAction();

        for (int anIdsForTouchCapture : idsForTouchCapture) {

            if(vId == anIdsForTouchCapture){
                if(eventAction == MotionEvent.ACTION_DOWN) {
                    QBTrackingManager.sharedManager().registerInteractionEvent(anIdsForTouchCapture, "onTouch", "DOWN");
                }
                else if (eventAction == MotionEvent.ACTION_UP) {
                    QBTrackingManager.sharedManager().registerInteractionEvent(anIdsForTouchCapture, "onTouch", "UP");
                }
                break;
            }
        }

        return true;
    }

    public static void handleOnClick(View v){

        for (int anIdsForClickCapture : idsForClickCapture) {
            if (v.getId() == anIdsForClickCapture) {
                QBTrackingManager.sharedManager().registerInteractionEvent(anIdsForClickCapture, "onClick", "Tap");
                break;
            }
        }
    }

    public static void handleSwipe(int viewID, String type, String name){
        QBTrackingManager.sharedManager().registerInteractionEvent(viewID, type, name);
    }

    public static void handleZoomPinch(int viewID, String type, String name){
        QBTrackingManager.sharedManager().registerInteractionEvent(viewID, type, name);
    }

}
