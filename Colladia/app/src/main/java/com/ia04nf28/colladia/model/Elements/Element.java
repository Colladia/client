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

    protected String id = UUID.randomUUID().toString();
    protected String text = "";
    protected float xMin;
    protected float yMin;
    protected float xMax;
    protected float yMax;
    protected Paint paint;

    // Element's lines size and color
    protected int color = Color.BLUE;
    protected int selectColor = Color.RED;
    protected float thickness = 20;
    protected boolean active = false;

    // Link point
    protected PointF center = new PointF();
    protected PointF top = new PointF();
    protected PointF bottom = new PointF();
    protected PointF left = new PointF();
    protected PointF right = new PointF();

    public abstract void drawElement(Canvas canvas);

    public Element()
    {
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);
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

        this.top.set( (this.xMin + this.xMax) / 2, this.yMin - thickness );
        this.bottom.set((this.xMin + this.xMax) / 2, this.yMax + thickness);
        this.left.set(this.xMin - thickness, (this.yMin + this.yMax) / 2);
        this.right.set(this.xMax + thickness, (this.yMin + this.yMax) / 2);

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
        return (((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax()))
                || ((getxMin() > finger.x) && (finger.x > getxMax()) && (getyMin() < finger.y) && (finger.y < getyMax()))
                || ((getxMin() < finger.x) && (finger.x < getxMax()) && (getyMin() > finger.y) && (finger.y > getyMax())));
    }

    public void selectElement(){
        paint.setColor(selectColor);
        setActive(true);
    }

    public void deselectElement(){
        paint.setColor(color);
        setActive(false);
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

    /**
     * Method to serialize the Element into a String
     * @return
     */
    public String serializeJSON () {
        JSONObject json = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();

        try {
            json.put("type",this.getClass().getSimpleName());
            json.put("id",getId());
            json.put("text",getText());
            json.put("xMin",""+getxMin());
            json.put("yMin",""+getyMin());
            json.put("xMax",""+getxMax());
            json.put("yMax",""+getyMax());
            json.put("color",""+getColor());//TODO not good need the real color
            json.put("active",""+isActive());
            json.put("center", "" + mapper.writeValueAsString(getCenter()));
            json.put("top","" + mapper.writeValueAsString(getTop()));
            json.put("bottom","" + mapper.writeValueAsString(getBottom()));
            json.put("left","" + mapper.writeValueAsString(getLeft()));
            json.put("right","" + mapper.writeValueAsString(getRight()));


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
            element = ElementFactory.createElementSerialized(json.getString("type"));
            element.setId(json.getString("id"));
            element.setText(json.getString("text"));
            element.setxMin(new Float(json.getString("xMin")));
            element.setyMin(new Float(json.getString("yMin")));
            element.setxMax(new Float(json.getString("xMax")));
            element.setyMax(new Float(json.getString("yMax")));
            element.setColor(new Integer(json.getString("color")));
            element.setActive(new Boolean(json.getString("active")));
            element.setCenter(mapper.readValue(json.getString("center"), PointF.class));
            element.setTop(mapper.readValue(json.getString("top"), PointF.class));
            element.setBottom(mapper.readValue(json.getString("bottom"), PointF.class));
            element.setLeft(mapper.readValue(json.getString("left"), PointF.class));
            element.setRight(mapper.readValue(json.getString("right"), PointF.class));
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
        if(this.getColor() != updatedElement.getColor()) this.setColor(updatedElement.getColor());
        if(this.isActive() != updatedElement.isActive()) this.setActive(updatedElement.isActive());
        if(this.getCenter().equals(updatedElement.getCenter())) this.setCenter(updatedElement.getCenter());
        if(this.getTop().equals(updatedElement.getTop())) this.setTop(updatedElement.getTop());
        if(this.getBottom().equals(updatedElement.getBottom())) this.setBottom(updatedElement.getBottom());
        if(this.getLeft().equals(updatedElement.getLeft())) this.setLeft(updatedElement.getLeft());
        if(this.getRight().equals(updatedElement.getRight())) this.setRight(updatedElement.getRight());
    }


    public Element clone() {
        Element clone = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la
            // méthode super.clone()
            clone = (Element) super.clone();
        } catch(CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implémentons
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        // on renvoie le clone
        return clone;
    }
}
