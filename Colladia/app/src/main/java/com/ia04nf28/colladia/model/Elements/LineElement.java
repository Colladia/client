package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by Mar on 21/05/2016.
 */
public class LineElement extends Element {

    //private PointF start = new PointF();
    //private PointF stop = new PointF();

    private int DIR;

    public LineElement()
    {
        super();
    }

    public LineElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }

    public LineElement(float xMin, float yMin, float xMax, float yMax, Paint paint)
    {
        super(xMin, yMin, xMax, yMax, paint);
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setStrokeWidth(8);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), p);

        // Top left to bottom right
        if(DIR == TOP_LEFT || DIR == BOTTOM_RIGHT)
        {
            canvas.drawLine(getxMin(), getyMin(), getxMax(), getyMax(), getPaint());
        }
        // Bottom left to top right
        else if(DIR == TOP_RIGHT || DIR == BOTTOM_LEFT)
        {
            canvas.drawLine(getxMin(), getyMax(), getxMax(), getyMin(), getPaint());
        }
    }

    @Override
    public boolean isTouch(PointF finger)
    {

        return ((( (getxMin() - TOLERANCE) < finger.x) && (finger.x < (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) < finger.y) && (finger.y < (getyMax() + TOLERANCE) ))
                || (( (getxMin() - TOLERANCE) > finger.x) && (finger.x > (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) > finger.y) && (finger.y > (getyMax() + TOLERANCE) ))
                || (( (getxMin() - TOLERANCE) > finger.x) && (finger.x > (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) < finger.y) && (finger.y < (getyMax() + TOLERANCE) ))
                || (( (getxMin() - TOLERANCE) < finger.x) && (finger.x < (getxMax() + TOLERANCE) ) && ( (getyMin() - TOLERANCE) > finger.y) && (finger.y > (getyMax() + TOLERANCE) )));
    }

    @Override
    public void set(PointF first, PointF second)
    {
        super.set(first, second);

        Log.d("Line element", "Set method");
        DIR = getDirection(first, second);
    }
}
