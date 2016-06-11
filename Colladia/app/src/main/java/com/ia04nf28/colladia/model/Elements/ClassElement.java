package com.ia04nf28.colladia.model.Elements;

import android.content.Context;
import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ia04nf28.colladia.R;
import org.json.JSONException;
import org.json.JSONObject;

public class ClassElement extends Element {

    // Header size in %
    private float header = 25;
    private String headerText = "";
    public static final String JSON_HEADER_TEXT = "headerText";
    private static final String JSON_HEADER = "header";

    public ClassElement()
    {
        super();
    }

    public ClassElement(float xMin, float yMin, float xMax, float yMax)
    {
        super(xMin, yMin, xMax, yMax);
    }

    /**
     * Copy constructor
     * @param originalElement the original element
     */
    public ClassElement(Element originalElement) {
        super(originalElement);
        this.setHeader(((ClassElement) originalElement).getHeader());
        this.setHeaderText(((ClassElement) originalElement).getHeaderText());
    }

    @Override
    public void drawElement(Canvas canvas)
    {
        // Header separator position
        float yPos = yMin + ((yMax - yMin)/100 * header);

        canvas.drawRect(xMin, yMin, xMax, yPos, getElementPaint());
        canvas.drawRect(xMin, yPos, xMax, yMax, getElementPaint());

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
        title.setId(R.id.class_edit_title);

        if(!this.headerText.isEmpty()) title.setText(headerText);

        ll.addView(title, 0);

        return ll;
    }

    @Override
    public void setTextFromLayout(LinearLayout layout)
    {
        // Set text content
        super.setTextFromLayout(layout);

        if(layout.getChildCount() > 1)
        {
            EditText title = (EditText)layout.findViewById(R.id.class_edit_title);
            this.headerText = title.getText().toString();
        }
    }


    public float getHeader() {
        return header;
    }
    public void setHeader(float header) {
        this.header = header;
    }
    public String getHeaderText() {
        return headerText;
    }
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    @Override
    public String serializeJSON() {
        String elementSerialized = super.serializeJSON();
        try {

            JSONObject json = new JSONObject(elementSerialized);
            json.put(JSON_HEADER,""+getHeader());
            json.put(JSON_HEADER_TEXT,getHeaderText());
            elementSerialized = json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return elementSerialized;
    }

    @Override
    public void updateElement(JSONObject jsonUpdatedElement, ObservableMap<String, Element> listElement) {
        super.updateElement(jsonUpdatedElement, listElement);
        try {
            if (jsonUpdatedElement.has(JSON_HEADER))
                setHeader(new Float(jsonUpdatedElement.getString(JSON_HEADER)));
            if (jsonUpdatedElement.has(JSON_HEADER_TEXT))
                setHeaderText(jsonUpdatedElement.getString(JSON_HEADER_TEXT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
