package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * Created by Mar on 21/05/2016.
 */
public class LineElement extends Element {
    public LineElement() {
        super();
    }

    public LineElement(float x, float y, int radius) {
        super(x, y);
    }

    public LineElement(float x, float y) {
        super(x, y);
    }

    @Override
    public void drawElement(Canvas canvas) {
        canvas.drawLine(getX(), getY(), getWidth(), getHeight(), getPaint());
    }

    @Override
    public boolean isTouch(PointF finger) {//TODO issue square and line in certain direction
        return (((getX() < finger.x) && (finger.x < getWidth()) && (getY() < finger.y) && (finger.y < getHeight()))
                || ((getX() > finger.x) && (finger.x > getWidth()) && (getY() > finger.y) && (finger.y > getHeight()))
                || ((getX() > finger.x) && (finger.x > getWidth()) && (getY() < finger.y) && (finger.y < getHeight()))
                || ((getX() < finger.x) && (finger.x < getWidth()) && (getY() > finger.y) && (finger.y > getHeight())));
    }

    @Override
    public void resize(float resizeFactor) {
        setWidth(getWidth()*resizeFactor);
        setHeight(getHeight() * resizeFactor);
        center.set((getX() + getWidth()) / 2, (getY() + getHeight()) / 2);
    }


    @Override
    public void set(PointF topLeftCorner, PointF bottomRightCorner)
    {
        super.set(topLeftCorner,bottomRightCorner);
    }
}
