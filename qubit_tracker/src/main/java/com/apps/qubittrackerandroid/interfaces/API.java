package com.apps.qubittrackerandroid.interfaces;

import com.android.volley.VolleyError;
import com.apps.qubittrackerandroid.enums.APIJob;

import org.json.JSONArray;
import org.json.JSONObject;

public interface API {

	interface OnAPIResponse {
	    public void onResponse(boolean isOK, JSONArray result, APIJob job, long id, int position);
	    public void onResponse(boolean isOK, JSONObject result, APIJob job, long id, int position);
	    public void onError(VolleyError error);
	}
}
