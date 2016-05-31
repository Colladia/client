package com.ia04nf28.colladia.model.Elements;

import android.content.Context;
import android.util.Log;

import com.ia04nf28.colladia.R;

/**
 * Created by Mar on 21/05/2016.
 */
public class ElementFactory {
    private static final String TAG = "ElementFactory";

    public static Element createElement(Context ctx, String typeElement)
    {
        Element newElement = null;

        if(typeElement.equals(ctx.getString(R.string.circle)))
        {
            newElement = new CircleElement();
        }
        else if(typeElement.equals(ctx.getString(R.string.square)))
        {
            newElement = new SquareElement();
        }
        else if(typeElement.equals(ctx.getString(R.string.line)))
        {
            newElement = new LineElement();
        }
        else if(typeElement.equals(ctx.getString(R.string.classe)))
        {
            newElement = new ClassElement();
        }
        else
        {
            Log.d(TAG, "No such element");
        }
        return newElement;
    }


    public static Element createElementSerialized( String typeElement)
    {
        Element newElement = null;

        if(typeElement.equals(CircleElement.class.getSimpleName()))
        {
            newElement = new CircleElement();
        }
        else if(typeElement.equals(SquareElement.class.getSimpleName()))
        {
            newElement = new SquareElement();
        }
        else if(typeElement.equals(LineElement.class.getSimpleName()))
        {
            newElement = new LineElement();
        }
        else if(typeElement.equals(ClassElement.class.getSimpleName()))
        {
            newElement = new ClassElement();
        }
        else
        {
            Log.d(TAG, "No such element");
        }
        return newElement;
    }
}
