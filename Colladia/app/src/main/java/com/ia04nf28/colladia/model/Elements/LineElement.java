package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mar on 21/05/2016.
 */
public class LineElement extends Element {

    //private PointF start = new PointF();
    //private PointF stop = new PointF();

    private int DIR;
    private static final String JSON_DIR = "dir";
    public LineElement()
    {
        super();
    }

    public LineElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }


    @Override
    public void drawElement(Canvas canvas)
    {
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setStrokeWidth(8);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), p);

        // Top left to bottom right
        if(DIR == TOP_LEFT || DIR == BOTTOM_RIGHT)
        {
            canvas.drawLine(getxMin(), getyMin(), getxMax(), getyMax(), getElementPaint());
        }
        // Bottom left to top right
        else if(DIR == TOP_RIGHT || DIR == BOTTOM_LEFT)
        {
            canvas.drawLine(getxMin(), getyMax(), getxMax(), getyMin(), getElementPaint());
        }
    }

    @Override
    public boolean isTouch(PointF finger)
    {

        return ((( (getxMin() - TOLERANCE) < finger.x) && (finger.x < (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) < finger.y) && (finger.y < (getyMax() + TOLERANCE) ))
                || (( (getxMin() - TOLERANCE) > finger.x) && (finger.x > (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) > finger.y) && (finger.y > (getyMax() + TOLERANCE) ))
                || (( (getxMin() - TOLERANCE) > finger.x) && (finger.x > (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) < finger.y) && (finger.y < (getyMax() + TOLERANCE) ))
                || (( (getxMin() - TOLERANCE) < finger.x) && (finger.x < (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) > finger.y) && (finger.y > (getyMax() + TOLERANCE) )));
    }


    @Override
    public void set(PointF first, PointF second)
    {
        super.set(first, second);

        Log.d("Line element", "Set method");
        DIR = getDirection(first, second);
    }


    public int getDIR() {
        return DIR;
    }

    public void setDIR(int DIR) {
        this.DIR = DIR;
    }

    @Override
    public String serializeJSON() {
        String elementSerialized = super.serializeJSON();
        try {

            JSONObject json = new JSONObject(elementSerialized);
            json.put(JSON_DIR,""+getDIR());
            elementSerialized = json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return elementSerialized;
    }


    @Override
    public void jsonToSpecificElement(JSONObject jsonElement) {
        try {
            if(jsonElement.has(JSON_DIR))
                setDIR(new Integer(jsonElement.getString(JSON_DIR)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public LineElement(Element originalElement) {
        super(originalElement);
        this.setDIR(((LineElement) originalElement).getDIR());
    }
}
