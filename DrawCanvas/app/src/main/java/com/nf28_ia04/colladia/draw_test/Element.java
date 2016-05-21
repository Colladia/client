package com.nf28_ia04.colladia.draw_test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;

/**
 * Created by Mar on 15/05/2016.
 */
public abstract class Element{

    private float x;
    private float y;
    protected float width;
    protected float height;
    private Paint paint;
    protected PointF center;

    public Element()
    {
        center = new PointF();

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(40);
        paint.setStyle(Paint.Style.STROKE);
    }

    public Element(float x, float y, Paint paint) {
        this.x = x;
        this.y = y;
        this.paint = paint;
    }
    public Element(float x, float y) {
        this.x = x;
        this.y = y;

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(40);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void set(PointF topLeftCorner, PointF bottomRightCorner)
    {
        //TODO check if correct
        if(topLeftCorner.x > bottomRightCorner.x || topLeftCorner.y > bottomRightCorner.y){//case where the two corner are in the wrong order then inverse them
            PointF tempPoint = topLeftCorner;
            topLeftCorner = bottomRightCorner;
            bottomRightCorner = tempPoint;
        }

        this.x = topLeftCorner.x;
        this.y = topLeftCorner.y;
        this.width = bottomRightCorner.x;
        this.height = bottomRightCorner.y;
        center.set((this.x + this.width) / 2, (this.y + this.height) / 2);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public abstract void drawElement(Canvas canvas);
    public abstract boolean isTouch(PointF finger);
    public abstract void resize(float resizeFactor);

    public  void move(PointF newPosition){
        PointF translate =  new PointF(newPosition.x - center.x, newPosition.y - center.y);
        x += translate.x;
        y += translate.y;
        width += translate.x;
        height += translate.y;
        center.set(newPosition.x, newPosition.y);
    }


    public void selectElement(){
        paint.setColor(Color.RED);
    }
    public void deselectElement(){
        paint.setColor(Color.BLUE);
    }

}
