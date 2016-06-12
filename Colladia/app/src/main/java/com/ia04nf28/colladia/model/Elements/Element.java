package com.ia04nf28.colladia.model.Elements;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.Layout;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ia04nf28.colladia.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public static final String JSON_TYPE = "type";
    protected String id = UUID.randomUUID().toString();
    public static final String JSON_ID = "id";
    protected String text = "";
    public static final String JSON_TEXT = "text";
    protected float xMin;
    protected float yMin;
    protected float xMax;
    protected float yMax;

    public static final String JSON_X_MIN = "xMin";
    public static final String JSON_Y_MIN = "yMin";
    public static final String JSON_X_MAX = "xMax";
    public static final String JSON_Y_MAX = "yMax";

    // Element's lines size and color
    protected static final float THICKNESS = 12;
    protected static final float TEXT_SIZE = 50;
    protected static final int TEXT_COLOR = Color.BLACK;

    protected int notSelectedColor = Color.BLACK;
    protected int currentColor = notSelectedColor;
    protected boolean active = false;
    public static Paint textPaint;//not serialized

    public static final String JSON_NOT_SELECTED_COLOR = "notSelectedColor";
    public static final String JSON_CURRENT_COLOR = "currentColor";
    public static final String JSON_ACTIVE = "active";

    // Link points
    protected Anchor center = new Anchor(Anchor.CENTER, id);
    protected Anchor top = new Anchor(Anchor.TOP, id);
    protected Anchor bottom = new Anchor(Anchor.BOTTOM, id);
    protected Anchor left = new Anchor(Anchor.LEFT, id);
    protected Anchor right = new Anchor(Anchor.RIGHT, id);
    public static final String JSON_ANCHOR_CENTER = "center";
    public static final String JSON_ANCHOR_TOP = "top";
    public static final String JSON_ANCHOR_BOTTOM  = "bottom";
    public static final String JSON_ANCHOR_LEFT = "left";
    public static final String JSON_ANCHOR_RIGHT = "right";

    public Element() {
        // Instanciate TextPaint only once
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(TEXT_COLOR);
            textPaint.setTextSize(TEXT_SIZE);
        }
    }

    public Element(float xMin, float yMin, float xMax, float yMax)
    {
        this();
        this.set(xMin, yMin, xMax, yMax);
    }

    /**
     * Copy constructor : generate a new UUID
     * @param originalElement
     */
    public Element(Element originalElement) {
        this();
        // new UUID
        this.setId(UUID.randomUUID().toString());

        this.setText(originalElement.getText());
        this.setxMin(originalElement.getxMin());
        this.setyMin(originalElement.getyMin());
        this.setxMax(originalElement.getxMax());
        this.setyMax(originalElement.getyMax());
        this.setNotSelectedColor(originalElement.getNotSelectedColor());
        this.setCurrentColor(originalElement.getCurrentColor());

        // two elements can't be active at the same time
        this.setActive(false);

        // new Anchors
        this.setCenter(new Anchor(Anchor.CENTER, this.id));
        this.setTop(new Anchor(Anchor.TOP, this.id));
        this.setBottom(new Anchor(Anchor.BOTTOM, this.id));
        this.setLeft(new Anchor(Anchor.LEFT, this.id));
        this.setRight(new Anchor(Anchor.RIGHT, this.id));
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

    public void selectElement(int usersColor){
        currentColor = usersColor;
        setActive(true);
    }

    public void deselectElement(){
        currentColor = notSelectedColor;
        setActive(false);
    }

    public LinearLayout getTextEdit(Context ctx)
    {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);

        final EditText content = new EditText(ctx);
        content.setHint("Contenu");
        content.setId(R.id.element_edit_content);

        if(!this.text.isEmpty()) content.setText(text);

        ll.addView(content);

        return ll;
    }

    public void setTextFromLayout(LinearLayout layout)
    {
        if(layout.getChildCount() > 0)
        {
            EditText content = (EditText)layout.findViewById(R.id.element_edit_content);
            this.setText(content.getText().toString());
        }
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
        paint.setStrokeWidth(THICKNESS);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Anchor getAnchor(int positionAnchor){
        Anchor searchedAnchor = null;
        switch (positionAnchor){
            case Anchor.TOP :
                searchedAnchor =  this.getTop();
                break;
            case Anchor.CENTER :
                searchedAnchor =  this.getCenter();
                break;
            case Anchor.BOTTOM :
                searchedAnchor =  this.getBottom();
                break;
            case Anchor.LEFT :
                searchedAnchor =  this.getLeft();
                break;
            case Anchor.RIGHT :
                searchedAnchor =  this.getRight();
                break;
        }
        return searchedAnchor;
    }

    /**
     * Method to serialize the Element into a String
     * @return
     */
    public String serializeJSON () {
        JSONObject json = new JSONObject();
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
            json.put(JSON_ANCHOR_CENTER, getCenter().anchorToJsonString());
            json.put(JSON_ANCHOR_TOP,getTop().anchorToJsonString());
            json.put(JSON_ANCHOR_BOTTOM,getBottom().anchorToJsonString());
            json.put(JSON_ANCHOR_LEFT,getLeft().anchorToJsonString());
            json.put(JSON_ANCHOR_RIGHT,getRight().anchorToJsonString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }




    public void updateElement(JSONObject jsonUpdatedElement, final ObservableMap<String, Element> listElement){
            this.parseElement(jsonUpdatedElement, listElement);
            this.set(this.getxMin(),this.getyMin(),this.getxMax(),this.getyMax());
    }




    private void parseElement(JSONObject jsonElement, final ObservableMap<String, Element> listElement){
        try {
            Iterator<String> keys = jsonElement.keys();
            while(keys.hasNext()){
                String currKey = keys.next();
                String attribute = jsonElement.getString(currKey);
                switch(currKey){
                    case JSON_ID:
                        this.setId(attribute);
                        break;
                    case JSON_TEXT:
                        this.setText(attribute);
                        break;
                    case JSON_X_MIN:
                        this.setxMin(new Float(attribute));
                        break;
                    case JSON_Y_MIN:
                        this.setyMin(new Float(attribute));
                        break;
                    case JSON_X_MAX:
                        this.setxMax(new Float(attribute));
                        break;
                    case JSON_Y_MAX:
                        this.setyMax(new Float(attribute));
                        break;
                    case JSON_CURRENT_COLOR:
                        this.setCurrentColor(new Integer(attribute));
                        break;
                    case JSON_NOT_SELECTED_COLOR:
                        this.setNotSelectedColor(new Integer(attribute));
                        break;
                    case JSON_ACTIVE:
                        this.setActive(new Boolean(attribute));
                        break;
                    case JSON_ANCHOR_CENTER:
                        this.setCenter(new Anchor(attribute,listElement ));
                        break;
                    case JSON_ANCHOR_TOP:
                        this.setTop(new Anchor(attribute,listElement ));
                        break;
                    case JSON_ANCHOR_BOTTOM:
                        this.setBottom(new Anchor(attribute,listElement ));
                        break;
                    case JSON_ANCHOR_LEFT:
                        this.setLeft(new Anchor(attribute,listElement ));
                        break;
                    case JSON_ANCHOR_RIGHT:
                        this.setRight(new Anchor(attribute,listElement ));
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Anchor> removeAnchors() {
        List<Anchor> listAnchorAssociatedToReset = new ArrayList<>();
        listAnchorAssociatedToReset.add(top.reset());
        listAnchorAssociatedToReset.remove(null);
        listAnchorAssociatedToReset.add(center.reset());
        listAnchorAssociatedToReset.remove(null);
        listAnchorAssociatedToReset.add(right.reset());
        listAnchorAssociatedToReset.remove(null);
        listAnchorAssociatedToReset.add(bottom.reset());
        listAnchorAssociatedToReset.remove(null);
        listAnchorAssociatedToReset.add(left.reset());
        listAnchorAssociatedToReset.remove(null);
        return listAnchorAssociatedToReset;
    }
}
