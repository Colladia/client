package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import org.json.JSONException;
import org.json.JSONObject;

public class ClassElement extends SquareElement {

    // Header size
    private float header = 100f;
    private static final String JSON_HEADER = "header";
    public ClassElement()
    {
        super();
    }

    public ClassElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }


    @Override
    public void drawElement(Canvas canvas)
    {
        // Draw the container
        super.drawElement(canvas);

        // Draw the header separator
        canvas.drawLine(xMin, yMin + header, xMax, yMin + header, this.getElementPaint());
    }


    public float getHeader() {
        return header;
    }

    public void setHeader(float header) {
        this.header = header;
    }

    @Override
    public String serializeJSON() {
        String elementSerialized = super.serializeJSON();
        try {

            JSONObject json = new JSONObject(elementSerialized);
            json.put(JSON_HEADER,""+getHeader());
            elementSerialized = json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return elementSerialized;
    }

    @Override
    public void jsonToElement(String serializedElement) {
        try {
            JSONObject json = new JSONObject(serializedElement);
            setHeader(new Float(json.getString(JSON_HEADER)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateElement(Element updatedElement) {
        super.updateElement(updatedElement);
        if(this.getHeader() != ((ClassElement)updatedElement).getHeader()) this.setHeader(((ClassElement)updatedElement).getHeader());

    }


    public ClassElement(Element originalElement) {
        super(originalElement);
        this.setHeader(((ClassElement) originalElement).getHeader());
    }
}
