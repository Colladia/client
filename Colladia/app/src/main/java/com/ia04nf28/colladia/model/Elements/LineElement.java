package com.ia04nf28.colladia.model.Elements;

import android.databinding.ObservableMap;
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

    private Anchor start = new Anchor(Anchor.TOP, this.getId());
    private Anchor stop = new Anchor(Anchor.BOTTOM, this.getId());
    private static final String JSON_ANCHOR_START = "start";
    private static final String JSON_ANCHOR_STOP = "stop";

    public LineElement()
    {
        //super();
    }

    public LineElement(Anchor start, Anchor stop)
    {
        this.start = start;
        this.stop = stop;
    }


    @Override
    public void drawElement(Canvas canvas)
    {
        /*// Top left to bottom right
        if(DIR == TOP_LEFT || DIR == BOTTOM_RIGHT)
        {
            canvas.drawLine(getxMin(), getyMin(), getxMax(), getyMax(), getElementPaint());
        }
        // Bottom left to top right
        else if(DIR == TOP_RIGHT || DIR == BOTTOM_LEFT)
        {
            canvas.drawLine(getxMin(), getyMax(), getxMax(), getyMin(), getPaint());
        }*/

        canvas.drawLine(start.x, start.y, stop.x, stop.y, Anchor.paint);

        if(isActive())
        {
            start.draw(canvas);
            stop.draw(canvas);
        }
        else
        {
            if(start.isActive()) start.draw(canvas);
            if(stop.isActive()) stop.draw(canvas);
        }
    }

    @Override
    public boolean isTouch(PointF finger)
    {
        float xMin = Math.min(start.x, stop.x) - TOLERANCE;
        float xMax = Math.max(start.x, stop.x) + TOLERANCE;
        float yMin = Math.min(start.y, stop.y) - TOLERANCE;
        float yMax = Math.max(start.y, stop.y) + TOLERANCE;

        return !( (finger.x < xMin) || (finger.x > xMax) || (finger.y < yMin) || (finger.y > yMax) );

    }

    @Override
    public Anchor isAnchorTouch(PointF finger)
    {
        if(start.isTouch(finger)) return start;
        if(stop.isTouch(finger)) return stop;

        return null;
    }


    @Override
    public void set(PointF first, PointF second)
    {
        this.start = new Anchor(first.x, first.y, this.getId());
        this.stop = new Anchor(second.x, second.y, this.getId());

        this.center.set( (start.x + stop.x) / 2, (start.y + stop.y) / 2 );
    }

    public void set(Anchor start, Anchor stop)
    {
        this.start = start;
        this.stop = stop;

        if(start != null && stop != null)
            this.center.set( (start.x + stop.x) / 2, (start.y + stop.y) / 2 );
    }

    @Override
    public void move(PointF newPosition)
    {
        // Anchor is active
        if(start.isActive())
        {
            if(start.isConnected())
            {
                // Old anchor becomes the element (which it is connected to) one
                start.setActive(false);

                // Line anchor becomes a new one
                start = new Anchor(newPosition.x, newPosition.y,this.getId());
                start.setActive(true);
            }
            else start.set(newPosition.x, newPosition.y);
        }

        // Anchor is active
        if(stop.isActive())
        {
            if(stop.isConnected())
            {
                // Old anchor becomes the element (which it is connected to) one
                stop.setActive(false);

                // Line anchor becomes a new one
                stop = new Anchor(newPosition.x - stop.x, newPosition.y - stop.y, this.getId());
                stop.setActive(true);
            }
            else stop.set(newPosition.x - stop.x, newPosition.y - stop.y);
        }
    }

    @Override
    public boolean isLine()
    {
        return true;
    }

    public Anchor getStart() {
        return start;
    }

    public Anchor getStop() {
        return stop;
    }

    public void setStart(Anchor start) {
        this.start = start;
    }

    public void setStop(Anchor stop) {
        this.stop = stop;
    }

    @Override
    public String serializeJSON() {
        String elementSerialized = super.serializeJSON();
        try {

            JSONObject json = new JSONObject(elementSerialized);
            json.put(JSON_ANCHOR_START,getStart().anchorToJsonString());
            json.put(JSON_ANCHOR_START,getStop().anchorToJsonString());
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
            if(jsonUpdatedElement.has(JSON_ANCHOR_START))
                this.setStart(new Anchor(jsonUpdatedElement.getString(JSON_ANCHOR_START), listElement));
            if(jsonUpdatedElement.has(JSON_ANCHOR_STOP))
                this.setStop(new Anchor(jsonUpdatedElement.getString(JSON_ANCHOR_STOP), listElement));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LineElement(Element originalElement) {
        super(originalElement);
        this.setStart(((LineElement) originalElement).getStart());
        this.setStop(((LineElement) originalElement).getStop());
    }
}
