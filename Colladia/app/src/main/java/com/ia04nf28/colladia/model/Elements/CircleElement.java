package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

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

        super.drawElement(canvas);
    }

    @Override
    public boolean isTouch(PointF finger)
    {
        return ( ( (this.center.x - finger.x) * (this.center.x - finger.x) + (this.center.y - finger.y) * (this.center.y - finger.y) ) <= (this.radius * this.radius) );
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
}
