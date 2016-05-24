package com.ia04nf28.colladia.model;

import android.content.Context;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ia04nf28.colladia.R;
import com.ia04nf28.colladia.model.Elements.CircleElement;
import com.ia04nf28.colladia.model.Elements.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mar on 21/05/2016.
 */
public class Diagram {
    private String name;

    /** All available elements */
    private ObservableMap<String,Element> listElement = new ObservableArrayMap();

    /**
     * Get the elements of the diagram
     * @return the observable map of elements
     */
    public ObservableMap<String, Element> getListElement() {
        return listElement;
    }

    public void setListElement(ObservableMap<String, Element> listElement) {
        this.listElement = listElement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String toJson(Diagram d){
        ObjectMapper mapper = new ObjectMapper();
        //Object to JSON in String
        try {
            return mapper.writeValueAsString(d);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Diagram toObject(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Diagram.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
