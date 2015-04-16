package com.apps.qubittrackerandroid.classes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;

import com.apps.qubittrackerandroid.managers.QBTrackingManager;
import com.apps.qubittrackerandroid.utils.QBUtils;

import java.util.List;

public class QBFragmentSupportV4 extends Fragment implements View.OnClickListener, View.OnTouchListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getVisibleFragment() != null){
            String vID = QBUtils.getViewIdFromFragments(getVisibleFragment().toString(), getVisibleFragment().getId());
            QBTrackingManager.sharedManager().registerViewEvent(vID);
        }
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
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
