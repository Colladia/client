package com.ia04nf28.colladia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.ia04nf28.colladia.Elements.CircleElement;
import com.ia04nf28.colladia.Elements.Element;
import com.ia04nf28.colladia.Utils.ChangementBase;

import java.util.HashSet;

/**
 * Created by Mar on 17/05/2016.
 */
public class DrawColladiaView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "DrawColladiaView";

    private Paint paint;
    private Paint border;

    private static float TOLERANCE = 5;

    // We can be in one of these states
    static final int NONE   = 0;
    static final int SCROLL = 1;
    static final int ZOOM   = 2;
    static final int MOVE = 3;
    static final int INSERT = 4;
    static final int RESIZE = 5;

    int mode = INSERT;


    /** All available circles */
    private HashSet<Element> listElement = new HashSet<>();

    private static final float ZOOM_MIN = 0.1f;
    private static final float ZOOM_MAX = 10.f;
    private float scaleFactor = 1.f;

    // Screen width and height in pixels
    private int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    private int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;

    //mPointAbsolutePoint and iPointAbsolutePoint are relative to the absolute root
    private PointF mPointAbsolutePoint = new PointF(0f,0f);// corresponds to mX and mY, current x/y
    private PointF iPointAbsolutePoint = new PointF(0f,0f);// corresponds to iX and iY, initial x/y
    private PointF currAbsolutePoint = new PointF(0f,0f);
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
    private Element prevSelected;
    private Element drawElem;

    ScaleGestureDetector scaleDetector;

    private boolean scrolled = false;


    /*** Constructors **/
    public DrawColladiaView(Context c)
    {
        super(c);
        scaleDetector = new ScaleGestureDetector(getContext(), new SimpleScaleListener());
        init(c);
    }

    public DrawColladiaView(Context c, AttributeSet attrs)
    {
        super(c, attrs);
        scaleDetector = new ScaleGestureDetector(getContext(), new SimpleScaleListener());
        init(c);
    }

    public DrawColladiaView(Context c, AttributeSet attrs, int defStyle)
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

        for (Element elem : listElement) {
            elem.drawElement(canvas);
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
        selected = getTouchedElement(currAbsolutePoint);

        if(prevSelected!=null){
            prevSelected.deselectElement();
            prevSelected = null;
        }
        if(selected!=null){
            Log.d(TAG, "Element found x : "+selected.getCenter().x+" y : "+selected.getCenter().y);
            selected.selectElement();
            prevSelected = selected;
        }


        switch(mode)
        {
            case INSERT:
                // Get the position where we create the element
                iPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));
                mPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));

                // We add the selected element from the menu to the canvas
                drawElem = new CircleElement();
                drawElem.set(iPointAbsolutePoint, mPointAbsolutePoint);

                listElement.add(drawElem);

                mode = INSERT;
                break;

            case NONE:
                // We touched an object on the screen
                if(selected != null)
                    mode = MOVE;
                else //or we touch any object on the screen
                    mode = SCROLL;
                break;
        }
    }

    private void moveTouch(float x, float y)
    {

        switch(mode)
        {
            case INSERT:
                mPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));
                drawElem.set(iPointAbsolutePoint, mPointAbsolutePoint);//TODO issue if iPointAbsolutePoint > mPointAbsolutePoint and so on do a test
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

                }
                break;

            case MOVE:
                if(selected!=null){
                    selected.move(currAbsolutePoint);
                }
                break;
            case RESIZE:
                if(selected!=null){
                    selected.resize(newDistanceFingerSpace/oldDistanceFingerSpace);
                }
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
                drawElem.set(iPointAbsolutePoint, mPointAbsolutePoint);
                drawElem = null;
                break;

            case SCROLL:
                scrolled = false;
                break;
        }

        mode = NONE;

    }

    private void pointerDownTouch(float x, float y)
    {
        if(selected!= null)
            mode = RESIZE;

        switch(mode)
        {
            case INSERT:
                drawElem.set(iPointAbsolutePoint,mPointAbsolutePoint);
                drawElem = null;
                break;

            case SCROLL:
                scrolled = false;
                prevTranslateX = translateX;
                prevTranslateY = translateY;
                break;
            case RESIZE:
                selected.resize(newDistanceFingerSpace/oldDistanceFingerSpace);
                break;
        }
    }

    private void pointerUpTouch(float x, float y){
        switch(mode)
        {
            case RESIZE:
                if(selected!=null){
                    selected.resize(newDistanceFingerSpace/oldDistanceFingerSpace);
                }
                mode = NONE;
                break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        float x = evt.getX();
        float y = evt.getY();
        currAbsolutePoint = ChangementBase.WindowToAbsolute(x, y, root.x, root.y, scaleFactor);
        long time = System.currentTimeMillis();

        Log.d(TAG, "Point         x : "+x+" y : "+y);
        Log.d(TAG, "Point absolue x : "+currAbsolutePoint.x+" y : "+currAbsolutePoint.y);
        Log.d(TAG, "Point root    x : "+root.x+" y : "+root.y);
        switch(evt.getAction())
        {
            // First finger on screen
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                break;

            // Finger moved while pressed on screen
            case MotionEvent.ACTION_MOVE:
                if(evt.getPointerCount()>1)
                    newDistanceFingerSpace = spacing(evt);
                moveTouch(x, y);
                break;

            // Last finger removed from screen
            case MotionEvent.ACTION_UP:
                upTouch(x, y);
                break;

            // Second finger on screen
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistanceFingerSpace = spacing(evt);
                newDistanceFingerSpace = spacing(evt);
                pointerDownTouch(x, y);
                break;

            // Other finger removed from screen
            case MotionEvent.ACTION_POINTER_UP:
                pointerUpTouch(x,y);
                break;
        }

        scaleDetector.onTouchEvent(evt);

        invalidate();

        return true;
    }

    private Element getTouchedElement(final PointF pointTouch)
    {
        for (Element elem : listElement)
        {
            if (elem.isTouch(pointTouch))
            {
                return elem;
            }
        }
        return null;
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
