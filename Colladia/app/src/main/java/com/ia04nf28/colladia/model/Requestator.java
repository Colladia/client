package com.ia04nf28.colladia.model;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Operate REST requests on the Colladia server.
 */
class Requestator {
    private static Requestator instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private String url;

    private StringRequest request;

    private Requestator(Context ctx) {
        Requestator.ctx = ctx;
        requestQueue = getRequestQueue();
    }

    public static Requestator instance(Context ctx) {
        if (instance == null) {
            instance = new Requestator(ctx);
        }
        return instance;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }


    public void getDiagramsList(Response.Listener<String> responseListener){
        StringRequest request = new StringRequest(Request.Method.GET, this.url,
            responseListener
            , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        getRequestQueue().add(request);
    }

    public void getDiagram(String diaId, Response.Listener<String> responseListener) {
        StringRequest request = new StringRequest(Request.Method.GET, this.url + "/" + diaId,
                responseListener
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        getRequestQueue().add(request);
    }
}
