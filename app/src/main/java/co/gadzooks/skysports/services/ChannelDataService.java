package co.gadzooks.skysports.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChannelDataService {
    Context ctx;

    public ChannelDataService(Context ctx) {
        this.ctx = ctx;
    }

    public interface OnDataResponse{
        void onResponse(JSONObject response) throws JSONException;
        void onError(String error);
    }


    public void getChannelData(String url, OnDataResponse onDataResponse) {

        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    onDataResponse.onResponse(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDataResponse.onError(error.getLocalizedMessage());
            }
        });
        queue.add(objectRequest);
    }
}
