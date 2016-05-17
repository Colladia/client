package com.nf28_ia04.colladia.draw_test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Mar on 15/05/2016.
 */
public class CircleElement extends Element {
    private int radius;
    private static final int DEFAULT_RADIUS = 50;

    public CircleElement()
    {
        super();
        this.radius = DEFAULT_RADIUS;

        Paint defaultPaint = new Paint();
        defaultPaint.setColor(Color.BLUE);
        defaultPaint.setStrokeWidth(40);
        defaultPaint.setStyle(Paint.Style.FILL);
        this.setPaint(defaultPaint);
    }


    public CircleElement(float x, float y, int radius) {
        super(x, y);
        this.radius = radius;

        Paint defaultPaint = new Paint();
        defaultPaint.setColor(Color.BLUE);
        defaultPaint.setStrokeWidth(40);
        defaultPaint.setStyle(Paint.Style.STROKE);
        this.setPaint(defaultPaint);
    }

    public CircleElement(float x, float y) {
        super(x, y);
        this.radius = DEFAULT_RADIUS;

        Paint defaultPaint = new Paint();
        defaultPaint.setColor(Color.BLUE);
        defaultPaint.setStrokeWidth(40);
        defaultPaint.setStyle(Paint.Style.FILL);
        this.setPaint(defaultPaint);
    }

    @Override
    public void drawElement(Canvas canvas, PointF absoluteRoot) {
        PointF coord = ChangementBase.AbsoluteToWindow(this.center.x, this.center.y, absoluteRoot.x, absoluteRoot.y);
        canvas.drawCircle(coord.x, coord.y, this.getRadius(), this.getPaint());
    }

    @Override
    public boolean isTouch(PointF finger,PointF absoluteRoot ) {//TODO check for absolute root or window
        finger =  ChangementBase.WindowToAbsolute(finger.x, finger.y, absoluteRoot.x, absoluteRoot.y);
        return ((this.getX() - finger.x) * (this.getX() - finger.x) + (this.getY() - finger.y) * (this.getY() - finger.y) <= this.radius * this.radius);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void set(float x, float y, float width, float height)
    {
        super.set(x, y, width, height);
        this.radius = Math.min(Math.round(width/2), Math.round(height/2));
    }

}
