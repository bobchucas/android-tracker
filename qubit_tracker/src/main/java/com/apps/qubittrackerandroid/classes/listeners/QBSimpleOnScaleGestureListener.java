package com.apps.qubittrackerandroid.classes.listeners;

import android.view.ScaleGestureDetector;

import com.apps.qubittrackerandroid.classes.QBHandler;

public class QBSimpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    int in;
    int out;

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        in = 0;
        out = 0;
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        if(detector.getScaleFactor() > 1){
            in++;
        }
        else{
            out++;
        }
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector, int viewID) {
        super.onScaleEnd(detector);

        if(in > out){
            QBHandler.handleZoomPinch(viewID, "ZOOM", "Zoom IN");
        }
        else{
            QBHandler.handleZoomPinch(viewID, "PINCH", "Zoom Out");
        }

        in = 0;
        out = 0;
    }

}
