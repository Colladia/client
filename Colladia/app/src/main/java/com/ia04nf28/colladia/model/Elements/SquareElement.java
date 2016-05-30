package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Mar on 21/05/2016.
 */
public class SquareElement extends Element{

    public SquareElement()
    {
        super();
    }

    public SquareElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }

    public SquareElement(float xMin, float yMin, float xMax, float yMax, Paint paint)
    {
        super(xMin, yMin, xMax, yMax, paint);
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), getPaint());

        if(active)
        {
            Paint p = new Paint();
            p.setColor(Color.GRAY);
            p.setStrokeWidth(5);
            p.setStyle(Paint.Style.STROKE);

            canvas.drawCircle(top.x, top.y, 4, p);
            canvas.drawCircle(bottom.x, bottom.y, 4, p);
            canvas.drawCircle(right.x, right.y, 4, p);
            canvas.drawCircle(left.x, left.y, 4, p);
        }

        if(!text.equals("")) canvas.drawText(text, center.x, center.y, getPaint());
    }
}
