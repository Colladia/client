package com.nf28_ia04.colladia.draw_test;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Mar on 15/05/2016.
 */
public abstract class Element{

    private float x;
    private float y;
    private float width;
    private float height;
    private Paint paint;
    protected PointF center;

    public Element()
    {
        center = new PointF();
    }

    public Element(float x, float y, Paint paint) {
        this.x = x;
        this.y = y;
        this.paint = paint;
    }
    public Element(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        center.set((x + width)/2, (y + height)/2);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public abstract void drawElement(Canvas canvas, PointF absoluteRoot);
    public abstract boolean isTouch(PointF finger, PointF absoluteRoot);

}
