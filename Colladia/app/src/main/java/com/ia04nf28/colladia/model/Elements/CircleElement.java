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
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setStrokeWidth(8);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), p);
        canvas.drawCircle(center.x, center.y, this.getRadius(), this.getPaint());
    }

    @Override
    public boolean isTouch(PointF finger)
    {
        return ( ( (this.center.x - finger.x) * (this.center.x - finger.x) + (this.center.y - finger.y) * (this.center.y - finger.y) ) <= (this.radius * this.radius) );
    }

    @Override
    public void set(PointF first, PointF second) {
        super.set(first, second);

        // Min or max, we have to choose
        //this.radius = Math.min(Math.round( (getxMax() - getxMin()) / 2 ), Math.round( (getyMax() - getyMin()) / 2 ));
        this.radius = Math.max(Math.round((getxMax() - getxMin()) / 2), Math.round((getyMax() - getyMin()) / 2));

        Log.d("radius", String.valueOf(radius));
    }

    public int getRadius() {
        return radius;
    }
}
