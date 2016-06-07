package com.ia04nf28.colladia.model;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Operate REST requests on the Colladia server.
 */
class Requestator {
    private static Requestator instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private String url;
    private static final String PROPERTIES ="properties";

    private static final String OPTIONS ="options";
    private static final String OPTIONS_AUTO ="[\"auto-positioning\"]";

    private StringRequest request;

    private Requestator(Context ctx) {
        Requestator.ctx = ctx;
        requestQueue = getRequestQueue();
    }

    static Requestator instance(Context ctx) {
        if (instance == null) {
            instance = new Requestator(ctx);
        }
        return instance;
    }

    void setUrl(String url) {
        this.url = url;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }


    void getDiagramsList(Response.Listener<String> responseListener){
        StringRequest request = new StringRequest(Request.Method.GET, this.url,
                responseListener, defaultErrorListener);

        getRequestQueue().add(request);
    }

    void getDiagram(String diaId, Response.Listener<String> responseListener) {
        StringRequest request = new StringRequest(Request.Method.GET, this.url + "/" + diaId,
                responseListener, defaultErrorListener);

        getRequestQueue().add(request);
    }

    void putDiagram(String diaId, Response.Listener<String> responseListener) {
        StringRequest request = new StringRequest(Request.Method.PUT, this.url + "/" + diaId,
                responseListener, defaultErrorListener);

        getRequestQueue().add(request);
    }

    void deleteDiagram(String diaId, Response.Listener<String> responseListener) {
        StringRequest request = new StringRequest(Request.Method.DELETE, this.url + "/" + diaId,
                responseListener, defaultErrorListener);

        getRequestQueue().add(request);
    }


    void getDiagram(String diaId,String lastClock, Response.Listener<String> responseListener) {
        final String lastClockRequest = lastClock;
        StringRequest request = new StringRequest(Request.Method.GET, this.url + "/" + diaId+"?"+Manager.LAST_CLOCK_INPUT_FIELD+"="+lastClockRequest,
                responseListener, defaultErrorListener);
        getRequestQueue().add(request);
    }




    void putElement(String diaId,String elementId,String lastClock, final String jsonElement, Response.Listener<String> responseListener) {
        final String lastClockRequest = lastClock;
        StringRequest request = new StringRequest(Request.Method.PUT, this.url + "/" + diaId+ "/" + elementId,
                responseListener, defaultErrorListener){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(PROPERTIES, jsonElement);
                params.put(Manager.LAST_CLOCK_INPUT_FIELD, lastClockRequest);
                return params;
            }
        };

        getRequestQueue().add(request);
    }


    void deleteElement(String diaId,String elementId,String lastClock, Response.Listener<String> responseListener) {
        final String lastClockRequest = lastClock;
        StringRequest request = new StringRequest(Request.Method.DELETE, this.url + "/" + diaId + "/" + elementId,
                responseListener, defaultErrorListener){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Manager.LAST_CLOCK_INPUT_FIELD, lastClockRequest);
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    void postElement(String diaId,String elementId,String lastClock, final String jsonElement, Response.Listener<String> responseListener) {
        final String lastClockRequest = lastClock;
        StringRequest request = new StringRequest(Request.Method.POST, this.url + "/" + diaId + "/" + elementId,
                responseListener, defaultErrorListener){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(PROPERTIES, jsonElement);
                params.put(Manager.LAST_CLOCK_INPUT_FIELD, lastClockRequest);
                return params;
            }
        };

        getRequestQueue().add(request);
    }


    void postAutoPositionElement(String diaId,String lastClock, Response.Listener<String> responseListener) {
        final String lastClockRequest = lastClock;
        StringRequest request = new StringRequest(Request.Method.POST, this.url + "/" + diaId ,
                responseListener, defaultErrorListener){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(OPTIONS, OPTIONS_AUTO);//TODO check if request working
                params.put(Manager.LAST_CLOCK_INPUT_FIELD, lastClockRequest);
                return params;
            }
        };

        getRequestQueue().add(request);
    }


    private Response.ErrorListener defaultErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            displayErrorInToast(error);
        }
    };

    private void displayErrorInToast(VolleyError e){
        Toast.makeText(Requestator.ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG);
    }
}
