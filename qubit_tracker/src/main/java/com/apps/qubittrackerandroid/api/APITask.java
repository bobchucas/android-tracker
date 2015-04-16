package com.apps.qubittrackerandroid.api;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apps.qubittrackerandroid.QBLibrary;
import com.apps.qubittrackerandroid.enums.APIJob;
import com.apps.qubittrackerandroid.interfaces.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class APITask {

    private API.OnAPIResponse onAPIResponseListener;

    public APITask(API.OnAPIResponse listener) {
        this.onAPIResponseListener = listener;
    }

    public void exec(String requestURL, final JSONObject requestData, final APIJob tag, final long id, final int position) {
        JsonObjectRequest request = new JsonObjectRequest(requestURL, (requestData == null) ? null : requestData,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onAPIResponseListener.onResponse(true, response, tag, id, position);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onAPIResponseListener.onError(error);
                }
            }

        ){

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    if("{}".equals(new String(response.data, "UTF-8"))){
                        deliverResponse(new JSONObject("{\"success\":\"yes\"}"));
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);

            }
        };

        QBLibrary.getInstance().addToRequestQueue(request);
    }


    public void execAndExpectArray(String requestURL, final JSONObject requestData, final APIJob tag, final long id, final int position) {
        final ArrayPostRequest request = new ArrayPostRequest(requestURL, (requestData == null) ? new JSONObject() : requestData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        onAPIResponseListener.onResponse(true, response, tag, id, position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onAPIResponseListener.onError(error);
                    }
                }

        ) {

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    if ("[]".equals(new String(response.data, "UTF-8"))) {
                        deliverResponse(new JSONArray("[{\"success\":\"yes\"}]"));
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        QBLibrary.getInstance().addToRequestQueue(request);
    }



}