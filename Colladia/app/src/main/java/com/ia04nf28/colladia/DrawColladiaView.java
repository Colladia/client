package com.ia04nf28.colladia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableMap;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ia04nf28.colladia.Utils.ChangementBase;
import com.ia04nf28.colladia.model.Elements.Anchor;
import com.ia04nf28.colladia.model.Elements.Element;
import com.ia04nf28.colladia.model.Elements.ElementFactory;
import com.ia04nf28.colladia.model.Manager;
import com.szugyi.circlemenu.view.CircleImageView;
import com.szugyi.circlemenu.view.CircleLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mar on 17/05/2016.
 */
public class DrawColladiaView extends SurfaceView implements SurfaceHolder.Callback, CircleLayout.OnItemClickListener{
    private static final String TAG = "DrawColladiaView";

    private Paint paint;
    private Paint border;
    private Paint grid;

    // GLOBAL DISTANCE TOLERANCE
    private static final float TOLERANCE = 5;

    // ZOOM LIMITS
    private static final float ZOOM_MIN = 0.20f;
    private static final float ZOOM_MAX = 2.0f;

    // ALLOWED MODES
    private static final int NONE   = 0;    // No action
    private static final int SCROLL = 1;    // Scroll the view
    private static final int ZOOM   = 2;    // Zoom the view
    private static final int MOVE   = 3;    // Move an element
    private static final int INSERT = 4;    // Insert an element
    private static final int RESIZE = 5;    // Resize an element
    private static final int LINK   = 6;    // Link creation
    private static final int MAIN_CONTEXTUAL = 8;    // Interacting with main contextual menu
    private static final int SELECT_CONTEXTUAL = 9;    // Interacting with selected element contextual mennu

    // Current mode
    private int mode = NONE;

    // Current scale
    private float scaleFactor = 1.f;

    // Current positions and translations
    private float xPos = 0f;
    private float yPos = 0f;
    private float translateX = 0f;
    private float translateY = 0f;
    private float prevTranslateX = 0f;
    private float prevTranslateY = 0f;

    // Center point for our absolute frame
    private PointF root = new PointF(0f, 0f);

    private PointF touchFromCenter = null;


    // Screen width and height in pixels
    private int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    private int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;

    //mAbsolutePoint and iAbsolutePoint are relative to the absolute root
    private PointF mAbsolutePoint = new PointF(0f,0f);// corresponds to mX and mY, current x/y
    private PointF iAbsolutePoint = new PointF(0f,0f);// corresponds to iX and iY, initial x/y
    private PointF currAbsolutePoint = new PointF(0f,0f);

    private SurfaceHolder mHolder;
    private DrawThread mThread;
    private Context applicationCtx = null;//need to initialise it
    private Context ctx;//need to initialise it

    private RectF screen;
    private Element selected;
    private Element prevSelected;
    private Element drawElem;

    //private EditText userTextInput;

    ScaleGestureDetector scaleDetector;
    GestureDetector gestureDetector;

    private CircleLayout mainContextualMenu;
    private CircleLayout selectContextualMenu;
    private Anchor startAnchor;
    private Anchor stopAnchor;
    private Anchor linkedTo;

    public void setMainContextualMenu(CircleLayout cl) {
        this.mainContextualMenu = cl;
        mainContextualMenu.setOnItemClickListener(this);
    }

    public void setSelectContextualMenu(CircleLayout cl) {
        this.selectContextualMenu = cl;
        selectContextualMenu.setOnItemClickListener(this);
    }

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
        getManager().getCurrentDiagram().addOnElementsChangeCallback(diagramCallback);
        for (Element elem : getManager().getCurrentDiagram().getListElement().values()){//if there is already elements on the diagram
            elem.addOnPropertyChangedCallback(elementCallback);
        }

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

        grid = new Paint();
        grid.setAntiAlias(false);
        grid.setColor(Color.GRAY);
        grid.setStyle(Paint.Style.STROKE);
        grid.setStrokeWidth(2f);
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
        if (mThread.getState() == Thread.State.NEW)
        {
            mThread.start();
        }
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

            // Update scale
            canvas.scale(scaleFactor, scaleFactor, 0, 0);

            // Translate
            canvas.translate(translateX / scaleFactor, translateY / scaleFactor);

            // Draw grid
            canvas.drawColor(Color.WHITE);
            drawGrid(canvas);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(30);

            // Draw the center
            canvas.drawCircle(0f, 0f, 15, paint);

            // Debug
            //canvas.drawText("x:" + root.x + " y:" + root.y, 10f, 10f, textPaint );

            // Draw elements
            for (Element elem : getManager().getCurrentDiagram().getListElement().values())
            {
                elem.drawElement(canvas);
            }

            canvas.restore();
        }
    }


    private void drawGrid(Canvas canvas)
    {
        // Size for one square of the grid, depending on the scale
        float gridWidth = 150 * scaleFactor;
        float gridHeight = 100 * scaleFactor;

        // Distance from absolute center and top-left corner of the screen
        PointF min = ChangementBase.WindowToAbsolute(0f, 0f, root.x, root.y, scaleFactor);
        // Distance from absolute center and bottom-right corner of the screen
        PointF max = ChangementBase.WindowToAbsolute(getWidth(), getHeight(), root.x, root.y, scaleFactor);

        float limitX = getWidth() / gridWidth;
        float limitY = getHeight() / gridHeight;

        float offsetX = (root.x) % (gridWidth);
        float offsetY = (root.y) % (gridHeight);

        List<Float> array = new ArrayList<Float>();

        for(int i = 0; i < limitX; i++)
        {
            array.add( (((i * gridWidth) - root.x + offsetX) / scaleFactor));
            array.add(min.y);

            array.add( (((i * gridWidth) - root.x + offsetX) / scaleFactor));
            array.add(max.y);
        }

        for(int j = 0; j < limitY; j++)
        {
            array.add(min.x);
            array.add( (((j * gridHeight) - root.y + offsetY) / scaleFactor));

            array.add(max.x);
            array.add( (((j * gridHeight) - root.y + offsetY) / scaleFactor));
        }

        float points[] = new float[array.size()];

        int i = 0;

        for(Float val : array)
        {
            points[i++] = (val != null ? val : Float.NaN);
        }

        canvas.drawLines(points, grid);
    }


    public void recenter()
    {
        Log.d(TAG, "recenter");
        xPos = this.getWidth() / 2;
        yPos = this.getHeight() / 2;
        translateX = xPos;
        translateY = yPos;
        prevTranslateX = 0f;
        prevTranslateY = 0f;
        root.set(xPos, yPos);
        invalidate();
    }


    private void startTouch(float x, float y)
    {
        xPos = x - prevTranslateX;
        yPos = y - prevTranslateY;

        prevSelected = selected;

        // We check if an element was touched
        selected = getTouchedElement(currAbsolutePoint);

        // We check if an anchor was touched
        startAnchor = getTouchedAnchor(currAbsolutePoint);

        // If an anchor was touched, we save it
        if(startAnchor != null) startAnchor.setActive(true);

        // Deselect previous selected element
        if(prevSelected != null)
        {
            Manager.instance(applicationCtx).deselectElement(prevSelected);
            prevSelected = null;
        }

        // No element touched
        if(selected == null)
        {

        }
        // Element touched
        else
        {
            Manager.instance(applicationCtx).selectElement(selected, Manager.instance(applicationCtx).getUser().getColor());
            prevSelected = selected;
        }


        switch(mode)
        {
            case INSERT:
                // Get the start position where we create the element
                iAbsolutePoint = new PointF(currAbsolutePoint.x, currAbsolutePoint.y);
                mAbsolutePoint = new PointF(currAbsolutePoint.x, currAbsolutePoint.y);

                // We add the selected element from the menu to the canvas
                drawElem.set(iAbsolutePoint, mAbsolutePoint);
                Manager.instance(applicationCtx).getCurrentDiagram().getListElement().put(drawElem.getId(), drawElem);
                break;

            case NONE:
                // Anchors have top priority on touch
                if(startAnchor != null)
                {
                    mode = LINK;

                    // Save the previous anchor it was linked to
                    if(startAnchor.isConnected()) linkedTo = startAnchor.getLink();
                    else linkedTo = null;

                    // Create new link
                    stopAnchor =  new Anchor(currAbsolutePoint.x, currAbsolutePoint.y,null);
                    stopAnchor.setActive(true);

                    // Link the anchor
                    startAnchor.linkTo(stopAnchor);
                }
                // We touched an object on the screen
                else if(selected != null)
                {
                    mode = MOVE;

                    // Distance between touched point and center of object
                    touchFromCenter = new PointF(selected.getCenter().x - currAbsolutePoint.x, selected.getCenter().y - currAbsolutePoint.y);

                    // Start position of the element
                    iAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                    mAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                }
                // we did not touch any object on the screen
                else mode = SCROLL;
                break;

            case MAIN_CONTEXTUAL:
                // main contextual menu was visible
                mainContextualMenu.setVisibility(GONE);
                mode = NONE;
                break;
            case SELECT_CONTEXTUAL:
                // select contextual menu was visible
                selectContextualMenu.setVisibility(GONE);
                mode = NONE;
                break;
        }
    }

    private void moveTouch(float x, float y)
    {
        switch(mode)
        {
            case INSERT:
                // Update last point and element
                mAbsolutePoint = new PointF(Math.round(currAbsolutePoint.x),Math.round(currAbsolutePoint.y));
                drawElem.set(iAbsolutePoint, mAbsolutePoint);
                break;

            case SCROLL:
                // Get the difference
                translateX = x - xPos;
                translateY = y - yPos;

                if(Math.abs(translateX) >= TOLERANCE || Math.abs(translateY) >= TOLERANCE)
                {
                    // Update our root point
                    root.x = translateX;
                    root.y = translateY;

                }
                break;

            case MOVE:
                if(selected != null)
                {
                    // Translate object
                    selected.move(new PointF(currAbsolutePoint.x + touchFromCenter.x, currAbsolutePoint.y + touchFromCenter.y));
                    // Save initial element's top-left position
                    iAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                    //Manager.instance(applicationCtx).moveElement(selected, p);
                }
                break;

            case RESIZE:
                if(selected != null)
                {
                    mAbsolutePoint = new PointF(currAbsolutePoint.x, currAbsolutePoint.y);
                    selected.set(iAbsolutePoint, mAbsolutePoint);
                    //Manager.instance(applicationCtx).updatePositionElement(selected, iPointAbsolutePoint, mPointAbsolutePoint);
                }
                break;

            case LINK:
                // Check if we found a stop anchor
                Anchor elemAnchor = getTouchedAnchor(currAbsolutePoint);

                // Found a stop anchor
                if(elemAnchor != null && elemAnchor != startAnchor) stopAnchor.set(elemAnchor.x, elemAnchor.y);
                else stopAnchor.set(currAbsolutePoint.x, currAbsolutePoint.y);
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
                //Manager.instance(applicationCtx).updatePositionElement(drawElem, iAbsolutePoint, mAbsolutePoint);
                drawElem.set(iAbsolutePoint,mAbsolutePoint);
                Manager.instance(applicationCtx).addElement(drawElem);
                drawElem = null;
                break;

            case MOVE:
                if(selected != null)
                {
                    // Translate object
                    PointF p = new PointF(currAbsolutePoint.x + touchFromCenter.x, currAbsolutePoint.y + touchFromCenter.y);
                    Manager.instance(applicationCtx).moveElement(selected, p);
                    // Save initial element's top-left position
                    iAbsolutePoint = new PointF(selected.getxMin(), selected.getyMin());
                }
                // Deselect anchors
                if(startAnchor != null) startAnchor.setActive(false);
                if(stopAnchor != null) stopAnchor.setActive(false);
                break;

            case LINK:
                Anchor elemAnchor = getTouchedAnchor(currAbsolutePoint);

                // Valid anchor found
                if(elemAnchor != null && startAnchor != elemAnchor)
                {
                    // Remove previous link
                    if(linkedTo != null) //linkedTo.linkTo(null);
                    {
                        Manager.instance(applicationCtx).connectElement(linkedTo, null);
                    }

                    if(elemAnchor.isConnected()) //elemAnchor.getLink().linkTo(null);
                    {
                        Manager.instance(applicationCtx).connectElement(elemAnchor.getLink(), null);
                    }

                    // Connect in both sides
                    //startAnchor.linkTo(elemAnchor);
                    //elemAnchor.linkTo(startAnchor);
                    Manager.instance(applicationCtx).connectElement(startAnchor, elemAnchor);

                }
                else
                {
                    if(linkedTo != null)
                    {
                        // Reset the previous link
                        //startAnchor.linkTo(linkedTo);
                        //linkedTo.linkTo(startAnchor);
                        Manager.instance(applicationCtx).connectElement(startAnchor, linkedTo);
                    }
                    else startAnchor.linkTo(null);
                }

                if(startAnchor != null) startAnchor.setActive(false);
                if(stopAnchor != null) stopAnchor.setActive(false);

                break;

            case RESIZE:
                if(selected != null)
                {
                    Manager.instance(applicationCtx).updatePositionElement(selected, iAbsolutePoint, mAbsolutePoint);
                }
                break;

            case MAIN_CONTEXTUAL:
                break;

            case SELECT_CONTEXTUAL:
                break;
        }

        if (mode == MAIN_CONTEXTUAL || mode == SELECT_CONTEXTUAL) {

        }
        else {
            mode = NONE;
        }

    }

    private void pointerDownTouch(float x, float y)
    {

        if(mode != INSERT)
        {
            if(selected != null) mode = RESIZE;
            else mode = ZOOM;
        }

        //Log.d(TAG, "Pointer down : " + String.valueOf(mode));

        switch(mode)
        {
            case INSERT:
                drawElem.set(iAbsolutePoint, mAbsolutePoint);
                drawElem = null;
                break;

//            case SCROLL:
//                prevTranslateX = translateX;
//                prevTranslateY = translateY;
//                break;

        }

    }

    private void pointerUpTouch(float x, float y){
        switch(mode)
        {
            case RESIZE:
                if(selected != null)
                {
                    Manager.instance(applicationCtx).updatePositionElement(selected, iAbsolutePoint, mAbsolutePoint);
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
        for (Element elem : getManager().getCurrentDiagram().getListElement().values())
        {
            if (elem.isTouch(pointTouch))
            {
                return elem;
            }
        }
        return null;
    }

    private Anchor getTouchedAnchor(final PointF touched)
    {
        Anchor anch;

        for(Element elem : getManager().getCurrentDiagram().getListElement().values())
        {
            anch = elem.isAnchorTouch(touched);
            if(anch != null && anch != stopAnchor) return anch;
        }

        return null;
    }

    public void insertNewElement(Element newElement){
        drawElem = newElement;
        mode = DrawColladiaView.INSERT;
    }

    @Override
    public void onItemClick(View view) {
        Element newElement;

        switch (view.getId()) {
            case R.id.add_square:
                // Handle add_square click
                newElement = ElementFactory.createElement(applicationCtx, applicationCtx.getString(R.string.shape_square));
                if (newElement != null) insertNewElement(newElement);
                break;
            case R.id.add_circle:
                // Handle circle click
                newElement = ElementFactory.createElement(applicationCtx, applicationCtx.getString(R.string.shape_circle));
                if (newElement != null) insertNewElement(newElement);
                break;
            case R.id.add_umlclass:
                // Handle class click
                newElement = ElementFactory.createElement(applicationCtx, applicationCtx.getString(R.string.shape_class));
                if (newElement != null) insertNewElement(newElement);
                break;
            case R.id.add_text:
                // Handle class click
                newElement = ElementFactory.createElement(applicationCtx, applicationCtx.getString(R.string.shape_text));
                if (newElement != null) insertNewElement(newElement);
                break;
            case R.id.auto_layout_elements:
                Manager.instance(applicationCtx).autoPositioning();
                mode = NONE;
                break;
            case R.id.center_view:
                this.recenter();
                mode = NONE;
                break;
            case R.id.delete_element:
                Manager.instance(applicationCtx).removeElement(selected);
                mode = NONE;
                break;
        }

        // exit contextual menus
        mainContextualMenu.setVisibility(GONE);
        selectContextualMenu.setVisibility(GONE);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
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

                final LinearLayout ll = selected.getTextEdit(getContext());

                builder.setTitle("Edition du contenu").setView(ll);
                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i)
                    {
                        Manager.instance(applicationCtx).changeText(selected, ll);
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
            if (selected != null && selectContextualMenu.getVisibility() == GONE && mode == MOVE) {
                selectContextualMenu.setVisibility(View.VISIBLE);
                mode = SELECT_CONTEXTUAL;
            } else if (mainContextualMenu.getVisibility() == GONE && mode == SCROLL){
                mainContextualMenu.setVisibility(View.VISIBLE);
                mode = MAIN_CONTEXTUAL;
            }
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
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                }

            }
        }
    }


}
