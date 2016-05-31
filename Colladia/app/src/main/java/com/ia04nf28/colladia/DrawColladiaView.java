package com.ia04nf28.colladia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableMap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.EditText;

import android.widget.RelativeLayout;
import com.ia04nf28.colladia.model.Elements.Element;
import com.ia04nf28.colladia.Utils.ChangementBase;
import com.ia04nf28.colladia.model.Manager;
import com.ia04nf28.colladia.rotatemenu.view.CoverRingImageView;
import com.ia04nf28.colladia.rotatemenu.view.RingOperationLayout;

import java.util.HashSet;

/**
 * Created by Mar on 17/05/2016.
 */
public class DrawColladiaView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "DrawColladiaView";

    private Paint paint;
    private Paint border;

    private static float TOLERANCE = 5;

    // We can be in one of these states
    static final int NONE   = 0;    // No action
    static final int SCROLL = 1;    // Scroll the view
    static final int ZOOM   = 2;    // Zoom the view
    static final int MOVE   = 3;    // Move an element
    static final int INSERT = 4;    // Insert an element
    static final int RESIZE = 5;    // Resize an element

    int mode = NONE;

    private PointF touchFromCenter = null;

    /** All available elements */
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



    private PointF root = new PointF(0f, 0f);

    private SurfaceHolder mHolder;
    private DrawThread mThread;
    private Context applicationCtx = null;//need to initialise it
    private Context ctx;//need to initialise it

    private RectF screen;
    private Element selected;
    private Element prevSelected;
    public Element drawElem;


    private EditText userTextInput;

    ScaleGestureDetector scaleDetector;
    GestureDetector gestureDetector;

    private boolean scrolled = false;


    private android.databinding.Observable.OnPropertyChangedCallback elementCallback = new android.databinding.Observable.OnPropertyChangedCallback(){
        @Override
        public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
            invalidate();
        }
    };
    private ObservableMap.OnMapChangedCallback diagramCallback = new ObservableMap.OnMapChangedCallback<ObservableMap<String,Element>,String, Element>(){
        @Override
        public void onMapChanged(ObservableMap<String, Element> sender, String key){
            Element changedElement = sender.get(key);
            if(changedElement != null){
                changedElement.addOnPropertyChangedCallback(elementCallback);
            }
            invalidate();
        }

    };


    /*** Constructors **/
    public DrawColladiaView(Context c)
    {
        super(c);
        init(c);
    }

    public DrawColladiaView(Context c, AttributeSet attrs)
    {
        super(c, attrs);
        init(c);
    }

    public DrawColladiaView(Context c, AttributeSet attrs, int defStyle)
    {
        super(c, attrs, defStyle);
        init(c);
    }


    private Manager getManager(){
        return Manager.instance(getApplicationCtx());
    }

    public Context getApplicationCtx() {
        return applicationCtx;
    }

    public void setApplicationCtx(Context applicationCtx) {
        this.applicationCtx = applicationCtx;
    }

    public void init(Context c)
    {
        //add a callback to the list of elements of the diagram
        /*TODO tp put back for server request getManager().getCurrentDiagram().addOnElementsChangeCallback(diagramCallback);
        for (Element elem : getManager().getCurrentDiagram().getListElement().values()){//if there is already elements on the diagram
            elem.addOnPropertyChangedCallback(elementCallback);
        }*/

        scaleDetector = new ScaleGestureDetector(getContext(), new SimpleScaleListener());
        gestureDetector = new GestureDetector(getContext(), new SimpleGestureListener());

        ctx = c;

        mHolder = getHolder();
        mHolder.addCallback(this);

        mThread = new DrawThread();
        screen = new RectF();
        setFocusable(true);

        border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(1f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(20f);


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

        if(canvas != null)
        {
            canvas.save();

            //Log.d(TAG, Integer.toString(mode));

            //canvas.scale(scaleFactor, scaleFactor, scaleDetector.getFocusX(), scaleDetector.getFocusY());
            canvas.scale(scaleFactor, scaleFactor, 0, 0);
            canvas.translate(translateX / scaleFactor, translateY / scaleFactor);


            canvas.drawColor(Color.WHITE);
            canvas.drawLine(-10, 0, 10, 0, paint);
            canvas.drawLine(0, -10, 0, 10, paint);

            for (Element elem : listElement)
            {
                elem.drawElement(canvas);
            }

            canvas.restore();
        }
    }



    private void startTouch(float x, float y)
    {
        Log.d(TAG, "Start touch : " + String.valueOf(mode));

        xPos = x - prevTranslateX;
        yPos = y - prevTranslateY;

        prevSelected = selected;

        // We check if an element was touched
        selected = getTouchedElement(currAbsolutePoint);

        if(prevSelected != null)
        {
            prevSelected.deselectElement();
            prevSelected = null;
        }

        // No element touched
        if(selected == null)
        {

        }
        // Element touched
        else
        {
            //Log.d(TAG, "Element found x : "+selected.getCenter().x+" y : "+selected.getCenter().y);
            selected.selectElement();
            prevSelected = selected;
        }


        switch(mode)
        {
            case INSERT:
                // Get the start position where we create the element
                iPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));
                mPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));

                // We add the selected element from the menu to the canvas
                drawElem.set(iPointAbsolutePoint, mPointAbsolutePoint);
                listElement.add(drawElem);

                mode = INSERT;
                break;

            case NONE:
                // We touched an object on the screen
                if(selected != null)
                {
                    mode = MOVE;

                    touchFromCenter = new PointF(selected.getCenter().x - currAbsolutePoint.x, selected.getCenter().y - currAbsolutePoint.y);

                    iPointAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                    mPointAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                }
                // we did not touch any object on the screen
                else mode = SCROLL;
                break;
        }
    }

    private void moveTouch(float x, float y)
    {
        Log.d(TAG, "Move touch : " + String.valueOf(mode));

        switch(mode)
        {
            case INSERT:
                mPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));
                drawElem.set(iPointAbsolutePoint, mPointAbsolutePoint);
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
                if(selected != null)
                {
                    PointF p = new PointF(currAbsolutePoint.x + touchFromCenter.x, currAbsolutePoint.y + touchFromCenter.y);

                    selected.move(p);
                    iPointAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                }
                break;

            case RESIZE:
                if(selected != null)
                {
                    mPointAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));
                    selected.set(iPointAbsolutePoint, mPointAbsolutePoint);
                }
                break;
        }
    }

    private void upTouch(float x, float y)
    {
        Log.d(TAG, "Up touch : " + String.valueOf(mode));

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

        if(mode != INSERT)
        {
            if(selected != null) mode = RESIZE;
            else mode = ZOOM;
        }

        Log.d(TAG, "Pointer down : " + String.valueOf(mode));

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
        }


    }

    private void pointerUpTouch(float x, float y){
        switch(mode)
        {
            case RESIZE:
                if(selected != null)
                {
                    selected.set(iPointAbsolutePoint, mPointAbsolutePoint);
                }
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

        //Log.d(TAG, "Point         x : "+x+" y : "+y);
        //Log.d(TAG, "Point absolue x : "+currAbsolutePoint.x+" y : "+currAbsolutePoint.y);
        //Log.d(TAG, "Point root    x : "+root.x+" y : "+root.y);
        switch(evt.getAction() & MotionEvent.ACTION_MASK)
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
                pointerUpTouch(x,y);
                break;
        }

        scaleDetector.onTouchEvent(evt);
        gestureDetector.onTouchEvent(evt);

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


    public void insertNewElement(Element newElement){
        drawElem = newElement;
        mode = DrawColladiaView.INSERT;
    }

    public class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector sg)
        {
            Log.d(TAG, "Scale : " + String.valueOf(mode));
            if(mode == ZOOM)
            {
                scaleFactor *= sg.getScaleFactor();

                scaleFactor = Math.max(ZOOM_MIN, Math.min(scaleFactor, ZOOM_MAX));
            }
            return true;
        }
    }

    public class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if(selected != null)
            {
                String value;
                //((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                userTextInput = new EditText(getContext());

                if(!selected.getText().isEmpty()) userTextInput.setText(selected.getText());

                builder.setTitle("Entrez votre texte").setView(userTextInput);
                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        selected.setText(userTextInput.getText().toString());
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {

                    }
                });

                builder.create().show();
            }
            //Log.d(TAG, "Double tap");
            //Toast.makeText(context, "onDoubleTap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // pop contextual menu
            RelativeLayout rel = (RelativeLayout) ((Activity) ctx).findViewById(R.id.rel);
            RingOperationLayout rol = new RingOperationLayout(ctx);
            //rol.setId(5);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rol.setLayoutParams(p);
            rel.addView(rol);
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
