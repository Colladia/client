package com.ia04nf28.colladia.model.Elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.design.widget.TextInputEditText;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ClassElement extends Element {

    // Header size in %
    private float header = 25;
    private String headerText = "";

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
        // Header separator position
        float yPos = yMin + ((yMax - yMin)/100 * header);

        canvas.drawRect(xMin, yMin, xMax, yPos, paint);
        canvas.drawRect(xMin, yPos, xMax, yMax, paint);

        float size = 0f, y = 0f;

        if(!text.isEmpty())
        {
            for(String sub : text.split("\n"))
            {
                size = (textPaint.measureText(sub) / 2);
                canvas.drawText(sub, center.x - size, (yMax + yPos) / 2 + y, Element.textPaint);
                y += textPaint.descent() - textPaint.ascent();
            }
        }

        if(!headerText.isEmpty())
        {
            y = 0;
            for(String sub : headerText.split("\n"))
            {
                size = (textPaint.measureText(sub) / 2);
                canvas.drawText(sub, center.x - size, (yMin + yPos) / 2 + y, Element.textPaint);
                y += textPaint.descent() - textPaint.ascent();
            }
        }

        super.drawAnchor(canvas);
    }

    @Override
    public LinearLayout getTextEdit(Context ctx)
    {
        LinearLayout ll = super.getTextEdit(ctx);

        final EditText title = new EditText(ctx);
        title.setHint("Titre");

        if(!this.headerText.isEmpty()) title.setText(headerText);

        ll.addView(title);

        return ll;
    }

    public void setTextFromLayout(LinearLayout layout)
    {
        // Set text content
        super.setTextFromLayout(layout);

        if(layout.getChildCount() > 1)
        {
            EditText title = (EditText)layout.getChildAt(1);
            this.headerText = title.getText().toString();
        }
    }

}
