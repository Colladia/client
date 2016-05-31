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

    /**
     * Method PUT to ask the server to create an Element
     * @param diaId
     * @param elementId
     * @param responseListener
     * @param jsonElement parsed Element in a JSON String
     */
    void putElement(String diaId,String elementId, final String jsonElement, Response.Listener<String> responseListener) {
        StringRequest request = new StringRequest(Request.Method.PUT, this.url + "/" + diaId+ "/" + elementId,
                responseListener, defaultErrorListener){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(PROPERTIES, jsonElement);
                return params;
            }
        };

        getRequestQueue().add(request);
    }

    void deleteElement(String diaId,String elementId, Response.Listener<String> responseListener) {
        StringRequest request = new StringRequest(Request.Method.DELETE, this.url + "/" + diaId + "/" + elementId,
                responseListener, defaultErrorListener);
        getRequestQueue().add(request);
    }

    void postElement(String diaId,String elementId, final String jsonElement, Response.Listener<String> responseListener) {

        StringRequest request = new StringRequest(Request.Method.POST, this.url + "/" + diaId + "/" + elementId,
                responseListener, defaultErrorListener){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(PROPERTIES, jsonElement);
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
