package com.apps.qubittrackerandroid.classes.listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.apps.qubittrackerandroid.classes.QBHandler;

public class QBSimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 20;
    private static final int SWIPE_VELOCITY_THRESHOLD = 20;

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, View view) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        QBHandler.handleSwipe(view.getId(), "Swipe", "Right");
                    } else {
                        QBHandler.handleSwipe(view.getId(), "Swipe", "Left");
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    QBHandler.handleSwipe(view.getId(), "Swipe", "Bottom");
                } else {
                    QBHandler.handleSwipe(view.getId(), "Swipe", "Top");
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

}
