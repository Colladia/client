package com.ia04nf28.colladia.model.Elements;

import android.content.Context;
import android.databinding.BaseObservable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.Layout;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Mar on 17/05/2016.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CircleElement.class, name = "CircleElement"),
        @JsonSubTypes.Type(value = SquareElement.class, name = "SquareElement"),
        @JsonSubTypes.Type(value = SquareElement.class, name = "ClassElement") })
@JsonPropertyOrder(alphabetic=true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class Element extends BaseObservable {

    // Directions
    protected static final int TOP_LEFT = 1;
    protected static final int TOP_RIGHT = 2;
    protected static final int BOTTOM_LEFT = 3;
    protected static final int BOTTOM_RIGHT = 4;

    // Distance to link point tolerance
    // Touch tolerance (for lines)
    public static final float TOLERANCE = 20f;

    protected String id = UUID.randomUUID().toString();
    protected String text = "";
    protected float xMin;
    protected float yMin;
    protected float xMax;
    protected float yMax;
    @JsonIgnore
    protected Paint paint;
    @JsonIgnore
    public static Paint textPaint;

    // Element's lines size and color
    protected int color = Color.BLACK;
    protected int selectColor = Color.RED;
    protected float thickness = 12;
    protected boolean active = false;

    // Link points
    protected Anchor center = new Anchor(Anchor.CENTER);
    protected Anchor top = new Anchor(Anchor.TOP);
    protected Anchor bottom = new Anchor(Anchor.BOTTOM);
    protected Anchor left = new Anchor(Anchor.LEFT);
    protected Anchor right = new Anchor(Anchor.RIGHT);

    public Element()
    {
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);

        // Instanciate TextPaint only once
        if(textPaint == null)
        {
            textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(30);
        }
    }

    public Element(float xMin, float yMin, float xMax, float yMax)
    {
        this();
        this.set(xMin, yMin, xMax, yMax);
    }

    public Element(float xMin, float yMin, float xMax, float yMax, Paint paint)
    {
        this(xMin, yMin, xMax, yMax);
        this.paint = paint;
    }

    public static int getDirection(PointF first, PointF second)
    {
        // LEFT DIRECTION
        if(first.x > second.x)
        {
            // TOP DIRECTION
            if(first.y > second.y)
            {
                return TOP_LEFT;
            }
            else return BOTTOM_LEFT;
        }
        // RIGHT DIRECTION
        else
        {
            // TOP DIRECTION
            if(first.y > second.y)
            {
                return TOP_RIGHT;
            }
            else return BOTTOM_RIGHT;
        }
    }

    public void set(float xMin, float yMin, float xMax, float yMax)
    {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;

        this.top.set((this.xMin + this.xMax) / 2, this.yMin /*- thickness*/ );
        this.bottom.set((this.xMin + this.xMax) / 2, this.yMax /*+ thickness*/);
        this.left.set(this.xMin /*- thickness*/, (this.yMin + this.yMax) / 2);
        this.right.set(this.xMax /*+ thickness*/, (this.yMin + this.yMax) / 2);

        this.center.set( (this.xMin + this.xMax) / 2, (this.yMin + this.yMax) / 2);
    }

    public void set(PointF first, PointF second)
    {
        int DIR = getDirection(first, second);

        switch(DIR)
        {
            case TOP_LEFT:
                this.set(second.x, second.y, first.x, first.y);
                break;

            case TOP_RIGHT:
                this.set(first.x, second.y, second.x, first.y);
                break;

            case BOTTOM_LEFT:
                this.set(second.x, first.y, first.x, second.y);
                break;

            case BOTTOM_RIGHT:
                this.set(first.x, first.y, second.x, second.y);
                break;
        }
    }

    public void move(PointF newPosition)
    {
        float translateX = newPosition.x - center.x;
        float translateY = newPosition.y - center.y;

        this.set(xMin + translateX, yMin + translateY, xMax + translateX, yMax + translateY);
    }

    public boolean isTouch(PointF finger)
    {
        return !( (finger.x < getxMin()) || (finger.x > getxMax()) || (finger.y < getyMin()) || (finger.y > getyMax()) );
        /*return (((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax())));*/
    }

    public Anchor isAnchorTouch(PointF finger)
    {
        if(top.isTouch(finger)) return top;
        if(bottom.isTouch(finger)) return bottom;
        if(left.isTouch(finger)) return left;
        if(right.isTouch(finger)) return right;

        return null;
    }

    public void drawAnchor(Canvas canvas)
    {
        if(isActive())
        {

            // DEBUG
            //canvas.drawRect(getxMin(), getyMin(), getxMax(), getyMax(), Anchor.paint);
        }
        /*else
        {
            if(top.isActive()) top.draw(canvas);
            if(bottom.isActive()) bottom.draw(canvas);
            if(left.isActive()) left.draw(canvas);
            if(right.isActive()) right.draw(canvas);
        }*/

        top.draw(canvas);
        bottom.draw(canvas);
        left.draw(canvas);
        right.draw(canvas);
    }

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

        drawAnchor(canvas);
    }

    public void selectElement(){
        paint.setColor(selectColor);
        setActive(true);
    }

    public void deselectElement(){
        paint.setColor(color);
        setActive(false);
    }

    public LinearLayout getTextEdit(Context ctx)
    {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);

        final EditText content = new EditText(ctx);
        content.setHint("Contenu");

        if(!this.text.isEmpty()) content.setText(text);

        ll.addView(content);

        return ll;
    }

    public void setTextFromLayout(LinearLayout layout)
    {
        if(layout.getChildCount() > 0)
        {
            EditText content = (EditText)layout.getChildAt(0);
            this.setText(content.getText().toString());
        }
    }



    /**
     * Method to serialize the Element into a String
     * @return
     */
    public String serializeJSON () {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;

        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * Method to get a serialized Element from a String
     * @param serialized
     * @return
     */
    public static Element deserializeJSON (String serialized) {
        ObjectMapper mapper = new ObjectMapper();
        Element elemnt = null;

        try {
            elemnt = mapper.readValue(serialized, Element.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elemnt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getxMin() {
        return xMin;
    }

    public void setxMin(float xMin) {
        this.xMin = xMin;
    }

    public float getyMin() {
        return yMin;
    }

    public void setyMin(float yMin) {
        this.yMin = yMin;
    }

    public float getxMax() {
        return xMax;
    }

    public void setxMax(float xMax) {
        this.xMax = xMax;
    }

    public float getyMax() {
        return yMax;
    }

    public void setyMax(float yMax) {
        this.yMax = yMax;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSelectColor() {
        return selectColor;
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Anchor getCenter() {
        return center;
    }

    public void setCenter(Anchor center) {
        this.center = center;
    }

    public Anchor getTop() {
        return top;
    }

    public void setTop(Anchor top) {
        this.top = top;
    }

    public Anchor getBottom() {
        return bottom;
    }

    public void setBottom(Anchor bottom) {
        this.bottom = bottom;
    }

    public Anchor getLeft() {
        return left;
    }

    public void setLeft(Anchor left) {
        this.left = left;
    }

    public Anchor getRight() {
        return right;
    }

    public void setRight(Anchor right) {
        this.right = right;
    }
}
