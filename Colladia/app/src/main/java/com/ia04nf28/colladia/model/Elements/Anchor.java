package com.ia04nf28.colladia.model.Elements;

import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.ia04nf28.colladia.DrawColladiaView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Charlie on 31/05/2016.
 */
public class Anchor {

    public static final int CIRCLE  = 1;
    public static final int SQUARE  = 2;
    public static final int DIAMOND = 3;

    // Relative position to the element
    public static final int NONE     = 0;
    public static final int TOP     = 1;
    public static final int RIGHT   = 2;
    public static final int LEFT    = 3;
    public static final int BOTTOM  = 4;
    public static final int CENTER  = 5;

    public static final float TOLERANCE = 100;

    public static final String NO_PARENT = "no";

    //Anchor belongs to
    private String idParent = null;
    private static final String JSON_PARENT_ID = "idParent";

    // Anchor connected to
    private Anchor link = null;
    private int positionLink = NONE ;
    private String idParentLink = NO_PARENT;
    private static final String JSON_LINK = "link";
    private static final String JSON_LINK_POSITION = "positionLink";
    private static final String JSON_PARENT_LINK_ID = "idParentLink";

    // Position
    public float x;
    public float y;
    private static final String JSON_X = "x";
    private static final String JSON_Y = "y";

    private boolean active = false;
    private static final String JSON_ACTIVE = "active";

    // Shape
    int shape = CIRCLE;
    private static final String JSON_SHAPE= "shape";
    int position = NONE;
    private static final String JSON_POSITION = "position";
    public static final int SIZE = 30;

    // Paint for Anchors
    public static Paint paint;
    static
    {
        if(paint == null)
        {
            paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    public Anchor(){}

    public Anchor(int pos, String idParent)
    {
        this.position = pos;
        this.idParent = idParent;
    }

    public Anchor(float x, float y, String idParent)
    {
        this.x = x;
        this.y = y;
        this.idParent = idParent;
    }

    public Anchor(float x, float y, int pos, String idParent)
    {
        this.x = x;
        this.y = y;
        this.position = pos;
        this.idParent = idParent;

    }

    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Anchor(String serializedAnchor, final ObservableMap<String, Element> listElement){
        try {
            JSONObject jsonAnchor = new JSONObject(serializedAnchor);
            Iterator<String> keys = jsonAnchor.keys();
            while(keys.hasNext()) {
                String currKey = keys.next();
                String attribute = jsonAnchor.getString(currKey);
                switch (currKey) {
                    case JSON_PARENT_ID:
                        this.setIdParent(attribute);
                        break;
                    case JSON_X:
                        this.setX(new Float(attribute));
                        break;
                    case JSON_Y:
                        this.setY(new Float(attribute));
                        break;
                    case JSON_POSITION:
                        this.setPosition(new Integer(attribute));
                        break;
                    case JSON_SHAPE:
                        this.setShape(new Integer(attribute));
                        break;
                    case JSON_ACTIVE:
                        this.setActive(new Boolean(attribute));
                        break;
                    case JSON_PARENT_LINK_ID:
                        this.setIdParentLink(attribute);
                        break;
                    case JSON_LINK_POSITION:
                        this.setPositionLink(new Integer(attribute));
                        break;
                }
            }

            if(!this.getIdParentLink().equals(NO_PARENT) && this.getPositionLink() != NONE && listElement.containsKey(this.getIdParentLink())){//it is an anchor fixed to an element that is referenced

                // Anchor was already connected
                if(this.getLink() != null && !this.getIdParentLink().equals(this.getLink().getIdParent())){
                    // Remove link to current anchor from the other one
                    this.getLink().setIdParentLink(NO_PARENT);
                    this.getLink().setPositionLink(NONE);
                    this.getLink().setLink(null);
                }

                // Connect to new anchor
                this.setLink(listElement.get(this.getIdParentLink()).getAnchor(this.getPositionLink()));

                if(this.getLink().getLink() == null || this.getLink().getLink() != this.getLink()){//condition fulfilled if the anchor referenced has to get a reference of this one
                    this.getLink().setLink(this.getLink());
                }


            }
            // No link
            else if(this.getIdParentLink().equals(NO_PARENT) && this.getPositionLink() == NONE){

                if(this.getLink() != null)
                {
                    this.getLink().setPositionLink(NONE);
                    this.getLink().setIdParentLink(NO_PARENT);
                    this.getLink().setLink(null);
                }

                this.setLink(null);
               /* if(jsonAnchor.has(JSON_LINK) && !jsonAnchor.getString(JSON_LINK).equals("")){//it is a mobile anchor that is referenced
                    this.setLink(new Anchor(jsonAnchor.getString(JSON_LINK), listElement));
                }*/
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String anchorToJsonString(){
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put(JSON_PARENT_ID,this.getIdParent());
            json.put(JSON_X,this.getX());
            json.put(JSON_Y,this.getY());
            json.put(JSON_POSITION,this.getPosition());
            json.put(JSON_SHAPE,this.getShape());
            json.put(JSON_ACTIVE,this.isActive());
            json.put(JSON_PARENT_LINK_ID,this.getIdParentLink());
            json.put(JSON_LINK_POSITION,this.getPositionLink());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json!=null ? json.toString() : "";
    }

    public Anchor(Anchor originalAnchor){
        this.setIdParent(originalAnchor.getIdParent());
        this.setX(originalAnchor.getX());
        this.setY(originalAnchor.getY());
        this.setPosition(originalAnchor.getPosition());
        this.setShape(originalAnchor.getShape());
        this.setActive(originalAnchor.isActive());
        this.setIdParentLink(originalAnchor.getIdParentLink());
        this.setPositionLink(originalAnchor.getPositionLink());
        this.setLink(originalAnchor.getLink());
    }

    public void draw(Canvas canvas)
    {
        if(isConnected()) canvas.drawLine(this.x, this.y, link.x, link.y, paint);

        switch (shape)
        {
            case CIRCLE:
                canvas.drawCircle(this.x, this.y, this.SIZE, paint);
                break;

            case SQUARE:
                canvas.drawRect( this.x - (this.SIZE / 2), this.y - (this.SIZE / 2), this.x + (this.SIZE / 2), this.y + (this.SIZE / 2), paint );
                break;
        }
    }

    public boolean isTouch(PointF finger)
    {
        switch (shape)
        {
            case CIRCLE:
                return ( ( Math.pow((finger.x - this.x), 2) + Math.pow(( finger.y - this.y), 2 ) ) <=  Math.pow(this.SIZE / 2.0f, 2) + TOLERANCE );

            case SQUARE:

                float xMin = x - (SIZE / 2) - TOLERANCE;
                float xMax = x + (SIZE / 2) + TOLERANCE;
                float yMin = y - (SIZE / 2) - TOLERANCE;
                float yMax = y + (SIZE / 2) + TOLERANCE;

                return !( (finger.x < xMin) || (finger.x > xMax) || (finger.y < yMin) || (finger.y > yMax) );

            default:
                return false;
        }
    }

    public void linkTo(Anchor anch){
        this.setLink(anch);

        if(anch != null && anch.getIdParent() != null){
            this.setIdParentLink(anch.getIdParent());
            this.setPositionLink(anch.getPosition());
        }
        else
        {
            this.setIdParentLink(NO_PARENT);
            this.setPositionLink(NONE);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isConnected() {
        return (link != null);
    }

    public Anchor getLink() {
        return link;
    }

    private void setLink(Anchor link) {
        this.link = link;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public String getIdParentLink() {
        return idParentLink;
    }

    public void setIdParentLink(String idParentLink) {
        this.idParentLink = idParentLink;
    }

    public int getPositionLink() {
        return positionLink;
    }

    public void setPositionLink(int positionLink) {
        this.positionLink = positionLink;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }
}
