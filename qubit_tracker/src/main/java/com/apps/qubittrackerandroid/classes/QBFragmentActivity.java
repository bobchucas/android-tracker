package com.apps.qubittrackerandroid.classes;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.apps.qubittrackerandroid.managers.QBTrackingManager;
import com.apps.qubittrackerandroid.utils.QBUtils;

public class QBFragmentActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
//        String vID = QBUtils.getViewIdFromResources(getResources().getResourceName(layoutResID) + ":" + layoutResID);
//        QBTrackingManager.sharedManager().registerViewEvent(vID, this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        Log.i("-----QBFragmentActivity : setContentView : view", view.getClass().toString() + "");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        super.setContentView(view, layoutParams);
        Log.i("-----QBFragmentActivity : setContentView : view, params", view.getClass().toString() + " / " + layoutParams.toString());
    }

    @Override
    public void onResume(){
        super.onResume();
        String vID = QBUtils.getViewIdFromPackage(getClass().getName());
        QBTrackingManager.sharedManager().registerViewEvent(vID);
    }

    @Override
    public void onClick(View v) {
        QBHandler.handleOnClick(v);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return QBHandler.handleOnTouch(v, event);
    }
}
