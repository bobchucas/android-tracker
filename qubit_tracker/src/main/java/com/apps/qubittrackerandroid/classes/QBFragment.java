package com.apps.qubittrackerandroid.classes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.apps.qubittrackerandroid.managers.QBTrackingManager;
import com.apps.qubittrackerandroid.utils.QBUtils;

public class QBFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getVisibleFragment() != null){
            String vID = QBUtils.getViewIdFromFragments(getVisibleFragment().toString(), getVisibleFragment().getId());
            QBTrackingManager.sharedManager().registerViewEvent(vID);
        }
    }

    //TODO : NEED TEST
    public Fragment getVisibleFragment(){
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
        return getFragmentManager().findFragmentByTag(tag);
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
