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

        if(typeElement.equals(ctx.getString(R.string.shape_circle)))
        {
            newElement = new CircleElement();
        }
        else if(typeElement.equals(ctx.getString(R.string.shape_square)))
        {
            newElement = new SquareElement();
        }
        else if(typeElement.equals(ctx.getString(R.string.shape_class)))
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


    public static Element createCopyElement( Element originalElement)
    {
        Element newElement = null;
        String className = originalElement.getClass().getSimpleName();

        if(className.equals(CircleElement.class.getSimpleName()))
        {
            newElement = new CircleElement(originalElement);
        }
        else if(className.equals(SquareElement.class.getSimpleName()))
        {
            newElement = new SquareElement(originalElement);
        }
        else if(className.equals(ClassElement.class.getSimpleName()))
        {
            newElement = new ClassElement(originalElement);
        }
        else
        {
            Log.d(TAG, "No such element");
        }
        return newElement;
    }
}
