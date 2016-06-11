package com.ia04nf28.colladia.model.Elements;

import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import org.json.JSONObject;

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

    /**
     * Copy constructor the original element
     * @param originalElement
     */
    public SquareElement(Element originalElement) {
        super(originalElement);
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), this.getElementPaint());
        super.drawElement(canvas);

    }

    @Override
    public String serializeJSON() {
        return super.serializeJSON();
    }

    @Override
    public void updateElement(JSONObject jsonUpdatedElement, ObservableMap<String, Element> listElement) {
        super.updateElement(jsonUpdatedElement, listElement);
    }
}
