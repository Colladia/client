package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Mar on 17/05/2016.
 */
public abstract class Element {

    // Directions
    protected static final int TOP_LEFT = 1;
    protected static final int TOP_RIGHT = 2;
    protected static final int BOTTOM_LEFT = 3;
    protected static final int BOTTOM_RIGHT = 4;

    // Distance to link point tolerance
    // Touch tolerance (for lines)
    public static final float TOLERANCE = 20f;

    protected String id = UUID.randomUUID().toString();
    protected String text = "";
    protected float xMin;
    protected float yMin;
    protected float xMax;
    protected float yMax;
    protected Paint paint;

    // Element's lines size
    protected float thickness = 20;
    protected boolean active = false;

    // Link point
    protected PointF center = new PointF();
    protected PointF top = new PointF();
    protected PointF bottom = new PointF();
    protected PointF left = new PointF();
    protected PointF right = new PointF();

    public abstract void drawElement(Canvas canvas);

    public Element()
    {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);
    }

    public Element(float xMin, float yMin, float xMax, float yMax)
    {
        this();

        this.set(xMin, yMin, xMax, yMax);
    }

    public Element(float xMin, float yMin, float xMax, float yMax, Paint paint)
    {
        this(xMin, yMin, xMax, yMax);

        this.paint = paint;
    }

    public static int getDirection(PointF first, PointF second)
    {
        // LEFT DIRECTION
        if(first.x > second.x)
        {
            // TOP DIRECTION
            if(first.y > second.y)
            {
                return TOP_LEFT;
            }
            else return BOTTOM_LEFT;
        }
        // RIGHT DIRECTION
        else
        {
            // TOP DIRECTION
            if(first.y > second.y)
            {
                return TOP_RIGHT;
            }
            else return BOTTOM_RIGHT;
        }
    }

    public void set(float xMin, float yMin, float xMax, float yMax)
    {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;

        this.top.set( (this.xMin + this.xMax) / 2, this.yMin - thickness );
        this.bottom.set((this.xMin + this.xMax) / 2, this.yMax + thickness);
        this.left.set(this.xMin - thickness, (this.yMin + this.yMax) / 2);
        this.right.set(this.xMax + thickness, (this.yMin + this.yMax) / 2);

        this.center.set( (this.xMin + this.xMax) / 2, (this.yMin + this.yMax) / 2);
    }

    public void set(PointF first, PointF second)
    {
        int DIR = getDirection(first, second);

        switch(DIR)
        {
            case TOP_LEFT:
                this.set(second.x, second.y, first.x, first.y);
                break;

            case TOP_RIGHT:
                this.set(first.x, second.y, second.x, first.y);
                break;

            case BOTTOM_LEFT:
                this.set(second.x, first.y, first.x, second.y);
                break;

            case BOTTOM_RIGHT:
                this.set(first.x, first.y, second.x, second.y);
                break;
        }
    }

    public void move(PointF newPosition)
    {
        float translateX = newPosition.x - center.x;
        float translateY = newPosition.y - center.y;

        this.set(xMin + translateX, yMin + translateY, xMax + translateX, yMax + translateY);
    }

    public boolean isTouch(PointF finger)
    {
        return (((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax())));
    }

    public void selectElement(){
        paint.setColor(Color.RED);
        setActive(true);
    }

    public void deselectElement(){
        paint.setColor(Color.BLUE);
        setActive(false);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getxMin() {
        return xMin;
    }

    public void setxMin(float xMin) {
        this.xMin = xMin;
    }

    public float getyMin() {
        return yMin;
    }

    public void setyMin(float yMin) {
        this.yMin = yMin;
    }

    public float getxMax() {
        return xMax;
    }

    public void setxMax(float xMax) {
        this.xMax = xMax;
    }

    public float getyMax() {
        return yMax;
    }

    public void setyMax(float yMax) {
        this.yMax = yMax;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public PointF getCenter() {
        return center;
    }

    public void setCenter(PointF center) {
        this.center = center;
    }

    public PointF getTop() {
        return top;
    }

    public void setTop(PointF top) {
        this.top = top;
    }

    public PointF getBottom() {
        return bottom;
    }

    public void setBottom(PointF bottom) {
        this.bottom = bottom;
    }

    public PointF getLeft() {
        return left;
    }

    public void setLeft(PointF left) {
        this.left = left;
    }

    public PointF getRight() {
        return right;
    }

    public void setRight(PointF right) {
        this.right = right;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
