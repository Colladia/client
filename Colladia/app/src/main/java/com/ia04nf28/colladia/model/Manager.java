package com.ia04nf28.colladia.model;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.graphics.PointF;
import android.util.Log;

import com.android.volley.Response;
import com.ia04nf28.colladia.Utils.Json;
import com.ia04nf28.colladia.model.Elements.Element;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by JeanV on 17/05/2016.
 */
public class Manager {
    private static final String TAG = "Manager";
    private static Manager instance;

    private final Context context;
    private final ObservableList<String> diagrams;
    private Diagram currentDiagram;
    private User user;
    private String lastClock;

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
    private Timer requestTimerElements = new Timer();
    private TimerTask getElementsTask = new TimerTask() {
        @Override
        public void run() {
            requestElements();
        }
    };



    private final static String STATUS_FIELD = "status";
    private final static String STATUS_OK = "200";
    private final static String STATUS_REDIRECTION = "304";
    private final static String STATUS_ERROR_CLIENT_BAD_REQUEST = "400";
    private final static String STATUS_ERROR_CLIENT_ALREADY_EXIST = "401";
    private final static String STATUS_ERROR_CLIENT_NOT_FOUND = "404";
    private final static String STATUS_ERROR_SERVER = "500";
    private final static String ERROR_MSG_FIELD = "error";
    private final static String TYPE_REQUEST_FIELD = "type";
    private final static String TYPE_REQUEST_GET = "GET";
    private final static String TYPE_REQUEST_PUT = "PUT";
    private final static String TYPE_REQUEST_POST = "POST";
    private final static String TYPE_REQUEST_DEL = "DELETE";
    private final static String CLOCK_FIELD = "clock";
    private final static String DESCRIPTION_FIELD = "description";
    private final static String MODIFICATION_LIST_FIELD = "modification-list";
    private final static String DIAGRAM_LIST_FIELD = "diagram-list";
    private final static String PATH_FIELD = "path";
    private final static String PROPERTIES_FIELD = "properties";


    public final static String LAST_CLOCK_INPUT_FIELD = "last-clock";//not for first GET





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
        lastClock = "0";
    }

    public void login(User user, String url) {
        Pattern p = Pattern.compile("^http://");
        Matcher m = p.matcher(url) ;
        if (!m.lookingAt())
        {
            url = "http://" + url;
        }

        Requestator.instance(context).setUrl(url);

        // TODO check url
        // TODO if url valid
        requestTimer.schedule(getDiagramsTask, 0, 5000);
        // end of if url valid
    }

    public void joinWorkspace(){
        requestTimerElements.schedule(getElementsTask, 0, 1000);
    }

    private void responseRequestHandler(String responseRequest){
        try {
            JSONObject mainObject = new JSONObject(responseRequest);
            if (mainObject.getString(STATUS_FIELD).equalsIgnoreCase(STATUS_OK)){
                Log.d(TAG, "Request success " );
                // if first response
                if (!logged.get())
                {
                    logged.set(true);
                }
                if(mainObject.has(CLOCK_FIELD))
                    lastClock = mainObject.getString(CLOCK_FIELD);

                if(mainObject.has(MODIFICATION_LIST_FIELD)){//for any other kind of request with a correct clock in input, for a specific diagram

                    JSONArray descArray = new JSONArray(mainObject.getString(MODIFICATION_LIST_FIELD));
                    for (int indexModif = 0;  indexModif < descArray.length() ; indexModif++){
                        typeModificationTraitement(new JSONObject(descArray.getString(indexModif)));
                    }

                }else if(mainObject.has(DESCRIPTION_FIELD)){//case for the first get or if the clock is not valid then update everything
                    getCurrentDiagram().getListElement().clear();

                    JSONObject descArray = new JSONObject(mainObject.getString(DESCRIPTION_FIELD));
                    Iterator<?> keys = descArray.keys();
                    Log.d(TAG, "desc size "+ descArray.length());
                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        //TODO may be test if not element expected, or keep it for the exceptions
                        getCurrentDiagram().getListElement().put(key, Element.deserializeJSON(descArray.getString(key)));
                    }


                }else if(mainObject.has(DIAGRAM_LIST_FIELD)){//for a get without anything list of all diagram
                    JSONArray jArray = new JSONArray(mainObject.getString(DIAGRAM_LIST_FIELD));
                    List<String> res = new ArrayList<>();

                    // add items not already in the stored list
                    for (int i = 0; i<jArray.length(); i++){
                        String item = jArray.getString(i);
                        res.add(item);
                        if (!diagrams.contains(item)) {
                            diagrams.add(item);
                        }
                    }

                    // remove the items that were stored but that were not in the response list
                    List<String> tempListDia = new ArrayList<>();
                    for(String dia : diagrams){
                        if(!res.contains(dia))
                            tempListDia.add(dia);
                    }
                    for(String dia : tempListDia){
                        diagrams.remove(dia);
                    }


                }else if(mainObject.has(TYPE_REQUEST_FIELD)){//case where the user has changed the list of diagram
                    String typeRequest = mainObject.getString(TYPE_REQUEST_FIELD);
                    String idDiagram = mainObject.getJSONArray(PATH_FIELD).getString(0);

                    if(typeRequest.equals(TYPE_REQUEST_PUT)){//the user create a new diagram
                        diagrams.add(idDiagram);
                    } else if(typeRequest.equals(TYPE_REQUEST_DEL)){//the user delete a diagram
                        diagrams.remove(idDiagram);
                    }
                }

            }else{
                Log.d(TAG, "Request Error "+mainObject.getString(STATUS_FIELD));
                //TODO handle error here
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void typeModificationTraitement(JSONObject modification){
        try {
            JSONArray pathJsonArray = new JSONArray(modification.getString(PATH_FIELD));
            if(pathJsonArray.length() == 2){
                String idElement = pathJsonArray.getString(pathJsonArray.length() - 1);
                String typeRequest = modification.getString(TYPE_REQUEST_FIELD);
                switch (typeRequest){
                    case TYPE_REQUEST_GET ://nothing
                        break;
                    case TYPE_REQUEST_PUT :
                        getCurrentDiagram().getListElement().put(idElement, Element.deserializeJSON(modification.getString(PROPERTIES_FIELD)));
                        break;
                    case TYPE_REQUEST_POST :
                        Element elementUpdated = getCurrentDiagram().getListElement().get(idElement);
                        elementUpdated.updateElement(Element.deserializeJSON(modification.getString(PROPERTIES_FIELD)));
                        break;
                    case TYPE_REQUEST_DEL :
                        getCurrentDiagram().getListElement().remove(idElement);
                        break;
                    default:
                        break;
                }
            } else
                Log.d(TAG, "SubElement or diagrams, not managed yet");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void requestDiagrams() {
        // get diagrams list
        Requestator.instance(context).getDiagramsList(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                responseRequestHandler(s);
                for(String dia : diagrams)
                    Log.d(TAG, dia);
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
                        Collections.sort(diagrams);
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

    public void setCurrentDiagram(final String diaId) {
        // TODO get diagram from server and load it
        Requestator.instance(context).getDiagram(diaId, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //TODO urgent not gonna work if request error
                currentDiagram = new Diagram();
                currentDiagram.setName(diaId);
                responseRequestHandler(s);
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
                responseRequestHandler(s);
            }
        });
    }


    public void addElement(final Element newElement) {
        // get diagrams list
        Requestator.instance(this.context).putElement(getCurrentDiagram().getName(), newElement.getId(), lastClock, newElement.serializeJSON(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                responseRequestHandler(s);

            }
        });
    }
    public void requestElements(){
        Requestator.instance(this.context).getDiagram(getCurrentDiagram().getName(), lastClock,new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                responseRequestHandler(s);
            }
        });
    }
    public void updatePositionElement(Element originalElement, PointF first, PointF second){
        final Element elementToServer = originalElement.clone();
        elementToServer.set(first,second);
        Requestator.instance(this.context).postElement(getCurrentDiagram().getName(), elementToServer.getId(), lastClock, elementToServer.serializeJSON(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                responseRequestHandler(s);
            }
        });
    }

    public void moveElement(Element originalElement, PointF newPosition){
        final Element elementToServer = originalElement.clone();
        elementToServer.move(newPosition);
        Requestator.instance(this.context).postElement(getCurrentDiagram().getName(), elementToServer.getId(), lastClock, elementToServer.serializeJSON(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Response Server : " + s);
                responseRequestHandler(s);
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