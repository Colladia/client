package com.nf28_ia04.colladia.draw_test;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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

    private Paint paint;
    private Paint border;

    private static float TOLERANCE = 5;

    // We can be in one of these states
    static final int NONE   = 0;
    static final int SCROLL = 1;
    static final int ZOOM   = 2;
    static final int EDIT   = 3;
    static final int INSERT = 4;

    int mode = INSERT;


    /** All available circles */
    private HashSet<Element> listElement = new HashSet<>();
    private SparseArray<Element> listPointedElement = new SparseArray<>();


    private static final float ZOOM_MIN = 0.1f;
    private static final float ZOOM_MAX = 10.f;
    private float scaleFactor = 1.f;

    // Screen width and height in pixels
    private int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    private int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;

    private int mX, mY, iX, iY; // current x/y and initial x/y

    private float xPos = 0f;
    private float yPos = 0f;
    private float translateX = 0f;
    private float translateY = 0f;
    private float prevTranslateX = 0f;
    private float prevTranslateY = 0f;
    private float oldDistanceFingerSpace = 0f;
    private float newDistanceFingerSpace = 0f;


    private PointF root = new PointF(0f, 0f);

    private SurfaceHolder mHolder;
    private DrawThread mThread;
    private Context ctx;

    private RectF screen;
    private Element selected;
    private Element drawElem;

    ScaleGestureDetector scaleDetector;

    private boolean scrolled = false;


    /*** Constructors **/
    public DrawColladia(Context c)
    {
        super(c);
        scaleDetector = new ScaleGestureDetector(getContext(), new SimpleScaleListener());
        init(c);
    }

    public DrawColladia(Context c, AttributeSet attrs)
    {
        super(c, attrs);
        scaleDetector = new ScaleGestureDetector(getContext(), new SimpleScaleListener());
        init(c);
    }

    public DrawColladia(Context c, AttributeSet attrs, int defStyle)
    {
        super(c, attrs, defStyle);
        scaleDetector = new ScaleGestureDetector(getContext(), new SimpleScaleListener());
        init(c);
    }

    public void init(Context c)
    {
        mHolder = getHolder();
        mHolder.addCallback(this);

        ctx = c;

        mThread = new DrawThread();
        screen = new RectF();

        setFocusable(true);

        border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(1f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5f);


    }

    /** Surface methods **/
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        screen.set(0, 0, width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // Start our thread
        mThread.setRunning(true);
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;

        // Stop our thread
        mThread.setRunning(false);

        while (retry)
        {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screen.set(0, 0, w, h);
        //super.onSizeChanged(w, h, oldw, oldh);

        //bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        //canvas = new Canvas(bitmap);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();

        //Log.d(TAG, Integer.toString(mode));

       //canvas.scale(scaleFactor, scaleFactor, scaleDetector.getFocusX(), scaleDetector.getFocusY());
       canvas.scale(scaleFactor, scaleFactor, 0, 0);
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor);


        canvas.drawColor(Color.WHITE);

        canvas.drawRect(0, 0, 2000, 2000, border);

        //canvas.drawCircle(root.x, root.y, 10, paint); //represent the absoluteRoot of the view


        for (Element elem : listElement) {
            elem.drawElement(canvas,root);
        }

        canvas.restore();

    }

    // Possible modes
    // --> INSERT
    // --> NONE
    private void startTouch(float x, float y)
    {
        xPos = x - prevTranslateX;
        yPos = y - prevTranslateY;

        // Default mode is scroll
        //mode = SCROLL;

        // We check if an element was touched
        selected = getTouchedElement(x, y);

        // down event is the first pointer, we clear the array
        clearElementPointer();

        //PointF center = ChangementBase.WindowToAbsolute(xPos, yPos, root.x, root.y);

        switch(mode)
        {
            case INSERT:
                // Get the position where we create the element
                iX = (Math.round(x));
                iY = (Math.round(y));
                mX = (Math.round(x));
                mY = (Math.round(y));

                // We add the selected element from the menu to the canvas
                drawElem = new CircleElement();
                drawElem.set(iX, iY, mX, mY,root,scaleFactor);

                listElement.add(drawElem);

                mode = INSERT;
                break;

            case NONE:
                // We touched an object on the screen
                if(selected != null)
                    mode = EDIT;
                else //or we touch any object on the screen
                    mode = SCROLL;
                break;
        }

/*TODO resize
        else if(null != touchedElement && oldDistanceFingerSpace!=0f){
        newDistanceFingerSpace = spacing(evt);
        float scale = newDistanceFingerSpace / oldDistanceFingerSpace;
        oldDistanceFingerSpace = 0f;
        newDistanceFingerSpace = 0f;
        touchedElement.resize(scale);*/
    }

    private void moveTouch(float x, float y)
    {
        //Element touchedElement = listPointedElement.get(pointerId);

        // Update our root point
        //root.x += translateX;
        //root.y += translateY;

        switch(mode)
        {
            case INSERT:
                mX = Math.round(x);
                mY = Math.round(y);
                drawElem.set(iX, iY, mX, mY, root, scaleFactor);//TODO issue if ix > mx and so on do a test
                break;

            case SCROLL:
                // Get the difference
                translateX = x - xPos;
                translateY = y - yPos;

                if(Math.abs(translateX) >= TOLERANCE || Math.abs(translateY) >= TOLERANCE)
                {
                    scrolled = true;
                    // Update our root point
                    root.x = translateX;
                    root.y = translateY;

                    /*root.x += dx;
                    root.y += dy;
                    scrollBy(-(int)dx, -(int)dy);
                    xPos = x;
                    yPos = y;*/
                }
                break;

            case EDIT:
                /*PointF center = ChangementBase.WindowToAbsolute(x, y, root.x, root.y);
                touchedElement.setX(center.x);
                touchedElement.setY(center.y);*/
                break;
        }
    }

    private void upTouch(float x, float y)
    {
        prevTranslateX = translateX;
        prevTranslateY = translateY;

        switch(mode)
        {
            case INSERT:
                drawElem.set(iX, iY, mX, mY, root,scaleFactor);
                drawElem = null;
                break;

            case SCROLL:
                scrolled = false;
                break;
        }

        mode = NONE;

        clearElementPointer();
    }

    private void pointerDownTouch(float x, float y)
    {
        switch(mode)
        {
            case INSERT:
                drawElem.set(iX, iY, mX, mY, root,scaleFactor);
                drawElem = null;
                break;

            case SCROLL:
                scrolled = false;

                prevTranslateX = translateX;
                prevTranslateY = translateY;
                break;
        }
/*      TODO For resize see
  pointerId = evt.getPointerId(INDEX);

        x = evt.getX(INDEX);
        y = evt.getY(INDEX);

        touchedElement = listPointedElement.get(pointerId);

        if (null != touchedElement) {
            oldDistanceFingerSpace = spacing(evt);
            Log.d(TAG, "oldDist=" + oldDistanceFingerSpace);
        }
        invalidate();
        //time = System.currentTimeMillis();
        break;*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        float x = evt.getX();
        float y = evt.getY();
        long time = System.currentTimeMillis();
        Element touchedElement = null;
        int pointerId = 0;

        Log.d(TAG, "Point         x : "+x+" y : "+y);
        PointF temp = ChangementBase.WindowToAbsolute(x,y,root.x,root.y,scaleFactor);
        Log.d(TAG, "Point absolue x : "+temp.x+" y : "+temp.y);
        Log.d(TAG, "Point root    x : "+root.x+" y : "+root.y);
        switch(evt.getAction())
        {
            // First finger on screen
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                break;

            // Finger moved while pressed on screen
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                break;

            // Last finger removed from screen
            case MotionEvent.ACTION_UP:
                upTouch(x, y);
                break;

            // Second finger on screen
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerDownTouch(x, y);
                break;

            // Other finger removed from screen
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }

        scaleDetector.onTouchEvent(evt);

        invalidate();

        return true;
    }

    private Element getTouchedElement(final float xTouch, final float yTouch)
    {
        for (Element elem : listElement)
        {
            if (elem.isTouch(new PointF(xTouch,yTouch),root, scaleFactor))
            {
                return elem;
            }
        }
        return null;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearElementPointer() {
        Log.w(TAG, "clearCirclePointer");
        listPointedElement.clear();
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    public class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector sg)
        {
            scaleFactor *= sg.getScaleFactor();

            scaleFactor = Math.max(ZOOM_MIN, Math.min(scaleFactor, ZOOM_MAX));
            return true;
        }
    }

    public class DrawThread extends Thread {

        private boolean running = false;

        public void setRunning(boolean run) {
            running = run;
        }

        public boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            Canvas c;

            while (running) {
                c = null;
                try {

                    // Get the canvas we want to draw in
                    c = mHolder.lockCanvas();

                    // Make sure no other thread is accessing the surface
                    synchronized (mHolder) {
                        // We draw
                        onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mHolder.unlockCanvasAndPost(c);
                    }
                }

                // We slow the refresh rate
                // It's useless to draw more than 50-60 frames per second
                // and it's battery friendly :)
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }

            }
        }
    }
}
