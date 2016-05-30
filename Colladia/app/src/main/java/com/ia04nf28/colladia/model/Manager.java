package com.ia04nf28.colladia.model;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.graphics.PointF;

import com.android.volley.Response;
import com.ia04nf28.colladia.Utils.Json;
import com.ia04nf28.colladia.model.Elements.Element;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;
import java.util.regex.Pattern;


/**
 * Created by JeanV on 17/05/2016.
 */
public class Manager {
    private static Manager instance;

    private final Context context;
    private final ObservableList<String> diagrams;
    private Diagram currentDiagram;
    private User user;

    public ObservableBoolean getLogged() {
        return logged;
    }

    private final ObservableBoolean logged;
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
        currentDiagram = null;
        logged = new ObservableBoolean(false);
        user = null;
    }

    public void login(User user, String url) {
        if (!Pattern.matches("^http://", url))
        {
            url = "http://" + url;
        }

        Requestator.instance(context).setUrl(url);

        // TODO check url
        // TODO if url valid
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
                        // if first response
                        if (!logged.get())
                        {
                            logged.set(true);
                        }

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

    public final Diagram getCurrentDiagram() {
        // TODO wait for diagram selection if null
        return currentDiagram == null ? new Diagram() : currentDiagram;
    }

    public void setCurrentDiagram(String diaId) {
        // TODO get diagram from server and load it
        Requestator.instance(context).getDiagram(diaId, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

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


    public void addElement(final Element newElement) {
        // get diagrams list
        Requestator.instance(this.context).putElement("1", newElement.getId(), newElement.serializeJSON(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                //TODO temporary, wait for method jean
                Element receiveElement = Element.deserializeJSON(Json.deserializeStringMap(Json.deserializeStringMap(s).get("description")).get(newElement.getId()));
                System.out.println("Receive Element Add"+receiveElement.getxMin());

            }
        });
    }

    public void updatePositionElement(Element originalElement, PointF first, PointF second){
        final Element elementToServer = originalElement.clone();
        elementToServer.set(first,second);
        Requestator.instance(this.context).postElement("1", elementToServer.getId(), elementToServer.serializeJSON(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                Element receiveElement = Element.deserializeJSON(Json.deserializeStringMap(Json.deserializeStringMap(s).get("description")).get(elementToServer.getId()));
                System.out.println("Receive Element Update "+receiveElement.getxMin());
            }
        });
    }

    public void moveElement(Element originalElement, PointF newPosition){
        final Element elementToServer = originalElement.clone();
        elementToServer.move(newPosition);
        Requestator.instance(this.context).postElement("1", elementToServer.getId(), elementToServer.serializeJSON(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                Element receiveElement = Element.deserializeJSON(Json.deserializeStringMap(Json.deserializeStringMap(s).get("description")).get(elementToServer.getId()));
                System.out.println("Receive Element Update " + receiveElement.getxMin());
                //TODO  need a diag currentDiagram.getListElement().remove(receiveElement.getId());
                //currentDiagram.getListElement().put(receiveElement.getId(),receiveElement);
            }
        });
    }

    //TODO use color user
    public void selectElement(Element originalElement){
        Element elementToServer = originalElement.clone();
        elementToServer.selectElement();

    }
    public void deselectElement(Element originalElement){
        Element elementToServer = originalElement.clone();
        elementToServer.deselectElement();
    }

    public void changeText(Element originalElement, String textInput){
        Element elementToServer = originalElement.clone();
        elementToServer.setText(textInput);
    }
}