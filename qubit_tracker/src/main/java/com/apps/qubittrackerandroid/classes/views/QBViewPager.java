package com.apps.qubittrackerandroid.classes.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.apps.qubittrackerandroid.managers.QBTrackingManager;

public class QBViewPager extends ViewPager {

    Context context;

    float downX; //initialized at the start of the swipe action
    float upX; //initialized at the end of the swipe action

    boolean isActive = false;
    int vID = 0;

    public QBViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public QBViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if(isActive)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    return true;

                case MotionEvent.ACTION_UP:
                    upX = event.getX();
                    final float deltaX = downX - upX;
                    //, (android.app.Activity) context
                    if (deltaX > 0 && deltaX > 20) {
                        QBTrackingManager.sharedManager().registerInteractionEvent(vID, "Swipe", "Left");
                    }
                    if (deltaX < 0 && -deltaX > 20) {
                        QBTrackingManager.sharedManager().registerInteractionEvent(vID, "Swipe", "Right");
                    }
                    return true;
            }

        return true;
    }

    public void registerViewPagerForTracking(int vID){
        this.isActive = true;
        this.vID = vID;
    }


}
