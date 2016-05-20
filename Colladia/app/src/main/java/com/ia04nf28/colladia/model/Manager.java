package com.ia04nf28.colladia.model;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import com.android.volley.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by JeanV on 17/05/2016.
 */
public class Manager {
    private static Manager instance;

    private final Context context;
    private final ObservableList<String> diagrams;
    private User user;
    private State state;

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

        // get diagrams list
        Requestator.instance(context).getDiagramsList(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject mainObject = new JSONObject(s);
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