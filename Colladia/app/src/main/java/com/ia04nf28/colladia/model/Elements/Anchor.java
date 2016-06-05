package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.ia04nf28.colladia.DrawColladiaView;

/**
 * Created by Charlie on 31/05/2016.
 */
public class Anchor {

    public static final int CIRCLE  = 1;
    public static final int SQUARE  = 2;
    public static final int DIAMOND = 3;

    // Relative position to the element
    public static final int TOP     = 1;
    public static final int RIGHT   = 2;
    public static final int LEFT    = 3;
    public static final int BOTTOM  = 4;
    public static final int CENTER  = 5;

    public static final float TOLERANCE = 100;

    // Position
    public float x;
    public float y;

    private boolean active = false;
    // Anchor connected to
    private Anchor link = null;

    // Shape
    int shape = CIRCLE;
    int position = TOP;
    int size = 30;

    // Paint for Anchors
    public static Paint paint;
    static
    {
        if(paint == null)
        {
            paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    public Anchor(){}

    public Anchor(int pos)
    {
        this.position = pos;
    }

    public Anchor(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Anchor(float x, float y, int pos)
    {
        this.x = x;
        this.y = y;
        this.position = pos;
    }

    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas)
    {
        if(isConnected()) canvas.drawLine(this.x, this.y, link.x, link.y, paint);

        switch (shape)
        {
            case CIRCLE:
                canvas.drawCircle(this.x, this.y, this.size, paint);
                break;

            case SQUARE:
                canvas.drawRect( this.x - (this.size / 2), this.y - (this.size / 2), this.x + (this.size / 2), this.y + (this.size / 2), paint );
                break;
        }
    }

    public boolean isTouch(PointF finger)
    {
        switch (shape)
        {
            case CIRCLE:
                return ( ( Math.pow((finger.x - this.x), 2) + Math.pow(( finger.y - this.y), 2 ) ) <=  Math.pow(this.size / 2.0f, 2) + TOLERANCE );

            case SQUARE:

                float xMin = x - (size / 2) - TOLERANCE;
                float xMax = x + (size / 2) + TOLERANCE;
                float yMin = y - (size / 2) - TOLERANCE;
                float yMax = y + (size / 2) + TOLERANCE;

                return !( (finger.x < xMin) || (finger.x > xMax) || (finger.y < yMin) || (finger.y > yMax) );

            default:
                return false;
        }
    }

    public void linkTo(Anchor anch){
        this.link = anch;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isConnected() {
        return (link != null);
    }

    public Anchor getLink() {
        return link;
    }
}
