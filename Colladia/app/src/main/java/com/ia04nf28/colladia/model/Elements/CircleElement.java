package com.ia04nf28.colladia.model.Elements;

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

    public CircleElement(float xMin, float yMin, float xMax, float yMax, Paint paint)
    {
        super(xMin, yMin, xMax, yMax, paint);
        this.radius = DEFAULT_RADIUS;
    }

    public CircleElement(float xMin, float yMin, float xMax, float yMax, int radius) {
        super(xMin, yMin, xMax, yMax);
        this.radius = radius;
    }

    public CircleElement(float xMin, float yMin, float xMax, float yMax, int radius, Paint paint) {
        super(xMin, yMin, xMax, yMax, paint);
        this.radius = radius;
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        canvas.drawCircle(center.x, center.y, this.getRadius(), this.getPaint());

        if(active)
        {
            Paint p = new Paint();
            p.setColor(Color.GRAY);
            p.setStrokeWidth(5);
            p.setStyle(Paint.Style.STROKE);

            canvas.drawCircle(top.x, top.y, 4, p);
            canvas.drawCircle(bottom.x, bottom.y, 4, p);
            canvas.drawCircle(right.x, right.y, 4, p);
            canvas.drawCircle(left.x, left.y, 4, p);

            canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), p);
        }
    }

    @Override
    public boolean isTouch(PointF finger)
    {
        return ( ( (this.center.x - finger.x) * (this.center.x - finger.x) + (this.center.y - finger.y) * (this.center.y - finger.y) ) <= (this.radius * this.radius) );
    }

    @Override
    public void set(PointF first, PointF second)
    {
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
            json.put("radius",""+getRadius());
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
            setRadius(new Integer(json.getString("radius")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateElement(Element updatedElement) {
        super.updateElement(updatedElement);
        if(this.getRadius() != ((CircleElement)updatedElement).getRadius()) this.setRadius(((CircleElement)updatedElement).getRadius());

    }
}
