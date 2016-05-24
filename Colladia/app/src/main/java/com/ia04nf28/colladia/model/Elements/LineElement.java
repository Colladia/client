package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Mar on 21/05/2016.
 */
public class LineElement extends Element {

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
        canvas.drawLine(getxMin(), getyMin(), getxMax(), getyMax(), getPaint());
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

    }
}
