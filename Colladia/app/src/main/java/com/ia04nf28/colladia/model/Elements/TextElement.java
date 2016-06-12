package com.ia04nf28.colladia.model.Elements;

import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import org.json.JSONObject;

/**
 * Created by Mar on 09/06/2016.
 */
public class TextElement extends Element {
    public TextElement()
    {
        super();
    }

    public TextElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }

    /**
     * Copy constructor
     * @param originalElement the original element
     */
    public TextElement(Element originalElement) {
        super(originalElement);
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        if(!text.isEmpty())
        {
            float size = 0f, y = 0f;
            for(String sub : text.split("\n"))
            {
                size = (textPaint.measureText(sub) / 2);
                canvas.drawText(sub, center.x - size, center.y + y, Element.textPaint);
                y += textPaint.descent() - textPaint.ascent();
            }
        }

        Paint fgPaintSel = new Paint();
        fgPaintSel.setARGB(255, 0, 0,0);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), fgPaintSel);

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
