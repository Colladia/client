package com.ia04nf28.colladia.model.Elements;

import android.util.Log;

/**
 * Created by Mar on 21/05/2016.
 */
public class ElementFactory {
    private static final String TAG = "ElementFactory";

    public static Element createElement(String typeElement){
        Element newElement = null;
        if(typeElement.equals(CircleElement.class.getSimpleName())){
            newElement = new CircleElement();
        } else{
            Log.d(TAG, "No such element");
        }
        return newElement;
    }
}
