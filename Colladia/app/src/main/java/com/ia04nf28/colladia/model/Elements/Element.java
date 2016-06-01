package com.ia04nf28.colladia.model.Elements;

import android.databinding.BaseObservable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Mar on 17/05/2016.
 */
public abstract class Element extends BaseObservable implements Cloneable {

    // Directions
    protected static final int TOP_LEFT = 1;
    protected static final int TOP_RIGHT = 2;
    protected static final int BOTTOM_LEFT = 3;
    protected static final int BOTTOM_RIGHT = 4;

    // Distance to link point tolerance
    // Touch tolerance (for lines)
    public static final float TOLERANCE = 20f;

    private static final String JSON_TYPE = "type";
    protected String id = UUID.randomUUID().toString();
    private static final String JSON_ID = "id";
    protected String text = "";
    private static final String JSON_TEXT = "text";
    protected float xMin;
    protected float yMin;
    protected float xMax;
    protected float yMax;
    private static final String JSON_X_MIN = "xMin";
    private static final String JSON_Y_MIN = "yMin";
    private static final String JSON_X_MAX = "xMax";
    private static final String JSON_Y_MAX = "yMax";

    // Element's lines size and color
    protected int notSelectedColor = Color.BLUE;
    protected int currentColor = notSelectedColor;
    protected static final float thickness = 20;
    protected boolean active = false;
    private static final String JSON_NOT_SELECTED_COLOR = "notSelectedColor";
    private static final String JSON_CURRENT_COLOR = "currentColor";
    private static final String JSON_ACTIVE = "active";

    // Link point
    protected PointF center = new PointF();
    protected PointF top = new PointF();
    protected PointF bottom = new PointF();
    protected PointF left = new PointF();
    protected PointF right = new PointF();
    private static final String JSON_CENTER = "center";
    private static final String JSON_TOP = "top";
    private static final String JSON_BOTTOM  = "bottom";
    private static final String JSON_LEFT = "left";
    private static final String JSON_RIGHT = "right";
    public abstract void drawElement(Canvas canvas);

    public Element()
    {
    }

    public Element(float xMin, float yMin, float xMax, float yMax)
    {
        this();
        this.set(xMin, yMin, xMax, yMax);
    }


    public Element(Element originalElement) {
        this();
        this.setId(originalElement.getId());
        this.setText(originalElement.getText());
        this.setxMin(originalElement.getxMin());
        this.setyMin(originalElement.getyMin());
        this.setxMax(originalElement.getxMax());
        this.setyMax(originalElement.getyMax());
        this.setNotSelectedColor(originalElement.getNotSelectedColor());
        this.setCurrentColor(originalElement.getCurrentColor());
        this.setActive(originalElement.isActive());
        this.setCenter(new PointF(originalElement.getCenter().x, originalElement.getCenter().y));
        this.setTop(new PointF(originalElement.getTop().x, originalElement.getTop().y));
        this.setBottom(new PointF(originalElement.getBottom().x, originalElement.getBottom().y));
        this.setLeft(new PointF(originalElement.getLeft().x, originalElement.getLeft().y));
        this.setRight(new PointF(originalElement.getRight().x, originalElement.getRight().y));
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

        this.top.set( (this.xMin + this.xMax) / 2, this.yMin - thickness );
        this.bottom.set((this.xMin + this.xMax) / 2, this.yMax + thickness);
        this.left.set(this.xMin - thickness, (this.yMin + this.yMax) / 2);
        this.right.set(this.xMax + thickness, (this.yMin + this.yMax) / 2);

        this.center.set((this.xMin + this.xMax) / 2, (this.yMin + this.yMax) / 2);
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
        return (((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax())));
    }

    public void selectElement(int usersColor){
        currentColor = usersColor;
        setActive(true);
    }

    public void deselectElement(){
        currentColor = notSelectedColor;
        setActive(false);
    }

    public void changeColor(int newColor){
        notSelectedColor = newColor;
        currentColor = notSelectedColor;
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

    public Paint getElementPaint() {
        Paint paint = new Paint();
        paint.setColor(currentColor);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public PointF getCenter() {
        return center;
    }

    public void setCenter(PointF center) {
        this.center = center;
    }

    public PointF getTop() {
        return top;
    }

    public void setTop(PointF top) {
        this.top = top;
    }

    public PointF getBottom() {
        return bottom;
    }

    public void setBottom(PointF bottom) {
        this.bottom = bottom;
    }

    public PointF getLeft() {
        return left;
    }

    public void setLeft(PointF left) {
        this.left = left;
    }

    public PointF getRight() {
        return right;
    }

    public void setRight(PointF right) {
        this.right = right;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNotSelectedColor() {
        return notSelectedColor;
    }

    public void setNotSelectedColor(int notSelectedColor) {
        this.notSelectedColor = notSelectedColor;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Method to serialize the Element into a String
     * @return
     */
    public String serializeJSON () {
        JSONObject json = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();

        try {
            json.put(JSON_TYPE, this.getClass().getSimpleName());
            json.put(JSON_ID, getId());
            json.put(JSON_TEXT, getText());
            json.put(JSON_X_MIN, "" + getxMin());
            json.put(JSON_Y_MIN, "" + getyMin());
            json.put(JSON_X_MAX, "" + getxMax());
            json.put(JSON_Y_MAX, "" + getyMax());
            json.put(JSON_CURRENT_COLOR, "" + getCurrentColor());
            json.put(JSON_NOT_SELECTED_COLOR, "" + getNotSelectedColor());
            json.put(JSON_ACTIVE, "" + isActive());
            json.put(JSON_CENTER, "" + mapper.writeValueAsString(getCenter()));
            System.out.println("Center serialisation x " + getCenter().x + " y " + getCenter().y);
            json.put(JSON_TOP, "" + mapper.writeValueAsString(getTop()));
            json.put(JSON_BOTTOM, "" + mapper.writeValueAsString(getBottom()));
            json.put(JSON_LEFT, "" + mapper.writeValueAsString(getLeft()));
            json.put(JSON_RIGHT, "" + mapper.writeValueAsString(getRight()));


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        return jsonString;
    }

    /**
     * Method to get a serialized Element from a String
     * @param serialized
     * @return
     */
    public static Element deserializeJSON (String serialized) {
        ObjectMapper mapper = new ObjectMapper();
        Element element = null;

        try {
            JSONObject json = new JSONObject(serialized);
            element = ElementFactory.createElementSerialized(json.getString(JSON_TYPE));
            element.setId(json.getString(JSON_ID));
            element.setText(json.getString(JSON_TEXT));
            element.setxMin(new Float(json.getString(JSON_X_MIN)));
            element.setyMin(new Float(json.getString(JSON_Y_MIN)));
            element.setxMax(new Float(json.getString(JSON_X_MAX)));
            element.setyMax(new Float(json.getString(JSON_Y_MAX)));
            element.setCurrentColor(new Integer(json.getString(JSON_CURRENT_COLOR)));
            element.setNotSelectedColor(new Integer(json.getString(JSON_NOT_SELECTED_COLOR)));
            element.setActive(new Boolean(json.getString(JSON_ACTIVE)));
            element.setCenter(mapper.readValue(json.getString(JSON_CENTER), PointF.class));
            System.out.println("Center deserialisation x " + element.getCenter().x + " y " + element.getCenter().y);
            element.setTop(mapper.readValue(json.getString(JSON_TOP), PointF.class));
            element.setBottom(mapper.readValue(json.getString(JSON_BOTTOM), PointF.class));
            element.setLeft(mapper.readValue(json.getString(JSON_LEFT), PointF.class));
            element.setRight(mapper.readValue(json.getString(JSON_RIGHT), PointF.class));
            element.jsonToElement(serialized);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return element;
    }

    public abstract void jsonToElement(String serializedElement);




    public void updateElement(Element updatedElement){
        if(this.getText().equals(updatedElement.getText())) this.setText(updatedElement.getText());
        if(this.getxMin() != updatedElement.getxMin()) this.setxMin(updatedElement.getxMin());
        if(this.getyMin() != updatedElement.getyMin()) this.setyMin(updatedElement.getyMin());
        if(this.getxMax() != updatedElement.getxMax()) this.setxMax(updatedElement.getxMax());
        if(this.getyMax() != updatedElement.getyMax()) this.setyMax(updatedElement.getyMax());
        if(this.getCurrentColor() != updatedElement.getCurrentColor()) this.setCurrentColor(updatedElement.getCurrentColor());
        if(this.getNotSelectedColor() != updatedElement.getNotSelectedColor()) this.setNotSelectedColor(updatedElement.getNotSelectedColor());
        if(this.isActive() != updatedElement.isActive()) this.setActive(updatedElement.isActive());
        if(this.getCenter().equals(updatedElement.getCenter())) this.setCenter(updatedElement.getCenter());
        if(this.getTop().equals(updatedElement.getTop())) this.setTop(updatedElement.getTop());
        if(this.getBottom().equals(updatedElement.getBottom())) this.setBottom(updatedElement.getBottom());
        if(this.getLeft().equals(updatedElement.getLeft())) this.setLeft(updatedElement.getLeft());
        if(this.getRight().equals(updatedElement.getRight())) this.setRight(updatedElement.getRight());
    }
}
