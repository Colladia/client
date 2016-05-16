package com.nf28_ia04.colladia.draw_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by Mar on 14/05/2016.
 */
public class DrawColladia extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "DrawColladia";
    private CanvasThread canvasthread;
    private PointF root = new PointF(0f,0f);

    private int width, height;
    private float xPos, yPos;
    private Canvas canvas;
    private Paint paint;
    private Paint border;
    private Context ctx;
    private Bitmap bitmap;

    private static float TOLERANCE = 5;

    // We can be in one of these states
    static final int NONE = 0;
    static final int SCROLL = 1;
    static final int ZOOM = 2;
    static final int SELECT = 3;
    static final int INSERT = 4;
    int mode = NONE;

    private final static int INDEX = 0;

    /** All available circles */
    private HashSet<Element> listElement = new HashSet<>();
    private SparseArray<Element> listPointedElement = new SparseArray<>();
    private final Random mRadiusGenerator = new Random();
    private final static int RADIUS_LIMIT = 100;


    public DrawColladia(Context c, AttributeSet attrs)
    {
        super(c, attrs);
        getHolder().addCallback(this);
        canvasthread = new CanvasThread(getHolder(), this);
        setFocusable(true);
    }

    public void init(Context c)
    {
        xPos = yPos = -1;
        ctx = c;

        border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(1f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        canvasthread.setRunning(true);
        canvasthread.start();
        init(this.ctx);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        boolean retry = true;
        canvasthread.setRunning(false);
        while (retry) {
            try {
                canvasthread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;

        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(Color.GRAY);

        canvas.drawRect(0, 0, 2000, 2000, border);

        canvas.drawCircle(root.x, root.y, 10, paint); //represent the absoluteRoot of the view
        canvas.drawRect(root.x+100, root.y+100, root.x + 200, root.y+200, paint);

        for (Element elem : listElement) {
            elem.drawElement(canvas,root);
        }

    }

    private void startTouch(float x, float y)
    {
        xPos = x;
        yPos = y;
    }

    private void moveTouch(float x, float y)
    {
        if(xPos == -1 && yPos == -1)
        {
            xPos = x;
            yPos = y;
        }
        else
        {
            float dx = x - xPos;
            float dy = y - yPos;

            if(Math.abs(dx) >= TOLERANCE || Math.abs(dy) >= TOLERANCE)
            {
                root.x += dx;
                root.y += dy;
                scrollBy(-(int)dx, -(int)dy);
                xPos = x;
                yPos = y;
            }
        }

        //Log.d("ClientView", "New pos x: " + xPos + " y: " + yPos);
    }

    private void upTouch()
    {

    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        float x = evt.getX();
        float y = evt.getY();
        long time;
        Element touchedElement = null;

        switch(evt.getAction())
        {
            // Get the time pressed to do the right action
            case MotionEvent.ACTION_DOWN:

                // it's the first pointer, so clear all existing pointers data
                clearElementPointer();

                touchedElement = getTouchedElement(x, y);
                if(touchedElement!= null){
                    Log.d(TAG, "Find an element :  x: " + x + " y: " + y );
                    PointF center = ChangementBase.WindowToAbsolute(x,y,root.x,root.y);
                    touchedElement.setX(center.x);
                    touchedElement.setY(center.y);
                    listPointedElement.put(evt.getPointerId(INDEX), touchedElement);

                } else if (mode==INSERT){
                    mode = SCROLL;
                    PointF center = ChangementBase.WindowToAbsolute(x,y,root.x,root.y);
                    touchedElement = new CircleElement(center.x,center.y, mRadiusGenerator.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT);
                    Log.w(TAG, "Added element " + touchedElement);
                    listElement.add(touchedElement);
                }
                else {
                    mode = INSERT;
                    startTouch(x, y);
                }

                invalidate();
                //time = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerId = evt.getPointerId(INDEX);

                x = evt.getX(INDEX);
                y = evt.getY(INDEX);

                touchedElement = listPointedElement.get(pointerId);

                if (null != touchedElement) {
                    PointF center = ChangementBase.WindowToAbsolute(x,y,root.x,root.y);
                    touchedElement.setX(center.x);
                    touchedElement.setY(center.y);
                } else {
                    moveTouch(x, y);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                clearElementPointer();
                upTouch();
                invalidate();
                break;
        }

        return true;
    }



    public void clearCanvas()
    {
        invalidate();
    }

    /**
     * Determines touched element
     *
     * @param xTouch float x touch coordinate
     * @param yTouch float y touch coordinate
     *
     * @return {@link Element} touched element or null if no element has been touched
     */
    private Element getTouchedElement(final float xTouch, final float yTouch) {
        Element touched = null;

        for (Element elem : listElement) {
            if (elem.isTouch(new PointF(xTouch,yTouch),root)) {
                touched = elem;
                break;
            }
        }

        return touched;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearElementPointer() {
        Log.w(TAG, "clearCirclePointer");
        listPointedElement.clear();
    }
}
