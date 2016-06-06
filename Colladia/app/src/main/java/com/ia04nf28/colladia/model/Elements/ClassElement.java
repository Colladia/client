package com.ia04nf28.colladia.model.Elements;

import android.databinding.ObservableMap;
import android.graphics.Canvas;

import org.json.JSONException;
import org.json.JSONObject;

public class ClassElement extends SquareElement {

    // Header size in %
    private float header = 25;
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
        float yPos = yMin + ((yMax - yMin)/100 * header);
        canvas.drawLine(xMin, yPos, xMax, yPos, this.getElementPaint());
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
    public void updateElement(JSONObject jsonUpdatedElement, ObservableMap<String, Element> listElement) {
        super.updateElement(jsonUpdatedElement, listElement);
        try {
            if (jsonUpdatedElement.has(JSON_HEADER))
                setHeader(new Float(jsonUpdatedElement.getString(JSON_HEADER)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ClassElement(Element originalElement) {
        super(originalElement);
        this.setHeader(((ClassElement) originalElement).getHeader());
    }
}
