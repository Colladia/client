package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

public class ClassElement extends SquareElement {

    // Header size in %
    private float header = 25;

    public ClassElement()
    {
        super();
    }

    public ClassElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }

    public ClassElement(float xMin, float yMin, float xMax, float yMax, Paint paint)
    {
        super(xMin, yMin, xMax, yMax, paint);
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        // Draw the container
        super.drawElement(canvas);

        // Draw the header separator
        float yPos = yMin + ((yMax - yMin)/100 * header);
        canvas.drawLine(xMin, yPos, xMax, yPos, paint);
    }

}
