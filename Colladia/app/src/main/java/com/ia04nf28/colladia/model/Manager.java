package com.ia04nf28.colladia.model;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import com.android.volley.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;


/**
 * Created by JeanV on 17/05/2016.
 */
public class Manager {
    private static Manager instance;

    private final Context context;
    private final ObservableList<String> diagrams;
    private User user;
    private State state;
    private Timer requestTimer = new Timer();
    private TimerTask getDiagramsTask = new TimerTask() {
        @Override
        public void run() {
            requestDiagrams();
        }
    };

    public static Manager instance(Context ctx) {
        if (instance == null) {
            instance = new Manager(ctx);
        }
        return instance;
    }

    private Manager(Context ctx) {
        context = ctx;
        diagrams = new ObservableArrayList<>();
        state = State.START;
        user = null;
    }

    public void login(String user, String url) {
        Requestator.instance(context).setUrl(url);

        // TODO check url
        // TODO if url valid
        state = State.LOGGED;
        requestTimer.schedule(getDiagramsTask, 0, 5000);
        // end of if url valid
    }

    private void requestDiagrams() {
        // get diagrams list
        Requestator.instance(context).getDiagramsList(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject mainObject = new JSONObject(s);
                    String status = mainObject.getString("status");

                    if (status.equalsIgnoreCase("ok")) {
                        String dList = mainObject.getString("diagram-list");

                        JSONArray jArray = new JSONArray(dList);

                        List<String> res = new ArrayList<String>();

                        // add items not already in the stored list
                        for (int i = 0; i<jArray.length(); i++){
                            String item = jArray.getString(i);
                            res.add(item);
                            if (!diagrams.contains(item)) {
                                diagrams.add(item);
                            }
                        }

                        // remove the items that were stored but that were not in the response list
                        diagrams.retainAll(res);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get the names of available diagrams
     * @return an unmodifiable list of String
     */
    public List<String> getDiagrams() {
        return Collections.unmodifiableList(diagrams);
    }

    /**
     * Listen to the changes affecting the list of available diagrams names
     * @param callback the callback to execute when a change occurs
     */
    public void addOnDiagramsChangeCallback(ObservableList.OnListChangedCallback<ObservableList<String>> callback){
        diagrams.addOnListChangedCallback(callback);
    }

    /**
     * Add a diagram to the model
     * @param name
     */
    public void addDiagram(final String name){
        // trying to add a diagram already existing
        if (diagrams.contains(name))
            return;

        // put new diagram
        Requestator.instance(context).putDiagram(name, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject mainObject = new JSONObject(s);
                    String status = mainObject.getString("status");

                    if (status.equalsIgnoreCase("ok")) {
                        diagrams.add(name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Remove a diagram from the model
     * @param name
     */
    public void removeDiagram(final String name){
        // trying to remove a non-existing diagram
        if (!diagrams.contains(name))
            return;

        // delete requested diagram
        Requestator.instance(context).deleteDiagram(name, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject mainObject = new JSONObject(s);
                    String status = mainObject.getString("status");

                    if (status.equalsIgnoreCase("ok")) {
                        diagrams.remove(name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private enum State {
        START, // Just created
        LOGGED // User logged in with valid url
    }
}