package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * Created by Mar on 17/05/2016.
 */
public class CircleElement extends Element {
    private int radius;
    private static final int DEFAULT_RADIUS = 50;

    public CircleElement()
    {
        super();
        this.radius = DEFAULT_RADIUS;
    }


    public CircleElement(float x, float y, int radius) {
        super(x, y);
        this.radius = radius;
    }

    public CircleElement(float x, float y) {
        super(x, y);
        this.radius = DEFAULT_RADIUS;
    }

    @Override
    public void drawElement(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, this.getRadius(), this.getPaint());
    }

    @Override
    public boolean isTouch(PointF finger) {
        return ((this.center.x - finger.x) * (this.center.x - finger.x) + (this.center.y - finger.y) * (this.center.y - finger.y) <= this.radius * this.radius);
    }

    @Override
    public void resize(float resizeFactor) {
        this.radius *= resizeFactor;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public void set(PointF topLeftCorner, PointF bottomRightCorner)
    {
        super.set(topLeftCorner,bottomRightCorner);
        this.radius = Math.min(Math.round(getWidth()/2), Math.round(getHeight()/2));
    }
}
