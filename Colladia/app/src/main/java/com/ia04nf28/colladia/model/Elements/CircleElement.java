package com.ia04nf28.colladia.model.Elements;

import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mar on 17/05/2016.
 */
public class CircleElement extends Element {

    private int radius;
    private static final String JSON_RADIUS = "radius";
    private static final int DEFAULT_RADIUS = 15;

    public CircleElement()
    {
        super();
        this.radius = DEFAULT_RADIUS;
    }

    public CircleElement(float xMin, float yMin, float xMax, float yMax) {
        super(xMin, yMin, xMax, yMax);
        this.radius = DEFAULT_RADIUS;
    }

    public CircleElement(float xMin, float yMin, float xMax, float yMax, int radius) {
        super(xMin, yMin, xMax, yMax);
        this.radius = radius;
    }


    @Override
    public void drawElement(Canvas canvas)
    {
        canvas.drawCircle(center.x, center.y, this.getRadius(), this.getElementPaint());

        super.drawElement(canvas);
    }

    @Override
    public boolean isTouch(PointF finger)
    {
        return ( ( (this.center.x - finger.x) * (this.center.x - finger.x) + (this.center.y - finger.y) * (this.center.y - finger.y) ) <= (this.radius * this.radius) );
    }

    @Override
    public void set(float xMin, float yMin, float xMax, float yMax)
    {
        float max = Math.max(xMax, yMax);

        // We want a perfect square
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = max;
        this.yMax = max;

        this.top.set((this.xMin + this.xMax) / 2, this.yMin /*- thickness*/ );
        this.bottom.set((this.xMin + this.xMax) / 2, this.yMax /*+ thickness*/);
        this.left.set(this.xMin /*- thickness*/, (this.yMin + this.yMax) / 2);
        this.right.set(this.xMax /*+ thickness*/, (this.yMin + this.yMax) / 2);

        this.center.set((this.xMin + this.xMax) / 2, (this.yMin + this.yMax) / 2);
    }


    @Override
    public void set(PointF first, PointF second) {

        super.set(first, second);

        this.radius = Math.max(Math.round((getxMax() - getxMin()) / 2), Math.round((getyMax() - getyMin()) / 2));

        // Resize the square to match circle size
        this.xMax = this.xMin + 2 * this.radius;
        this.yMax = this.yMin + 2 * this.radius;

    }



    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public String serializeJSON() {
        String elementSerialized = super.serializeJSON();
        try {

            JSONObject json = new JSONObject(elementSerialized);
            json.put(JSON_RADIUS,""+getRadius());
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
            if(jsonUpdatedElement.has(JSON_RADIUS))
                setRadius(new Integer(jsonUpdatedElement.getString(JSON_RADIUS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public CircleElement(Element originalElement) {
        super(originalElement);
        this.setRadius(((CircleElement)originalElement).getRadius());
    }
}
