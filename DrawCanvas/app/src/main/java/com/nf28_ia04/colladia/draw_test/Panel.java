package com.nf28_ia04.colladia.draw_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Mar on 11/05/2016.
 */
public class Panel extends SurfaceView implements SurfaceHolder.Callback {
    private CanvasThread canvasthread;
    //Zoom & pan touch event
    int y_old=0,y_new=0;
    int zoomMode=0;
    float pinch_dist_old=0,pinch_dist_new=0;
    int zoomControllerScale=1;//new and old pinch distance to determine Zoom scale

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int PAN = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    private static final String TAG = "DebugTag";

    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        getHolder().addCallback(this);
        //canvasthread = new CanvasThread(getHolder(), this);
        setFocusable(true);
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
    public boolean onTouchEvent(MotionEvent event){
        PanZoomWithTouch(event);

        invalidate();//necessary to repaint the canvas
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {

        Paint paint = new Paint();


        Bitmap kangoo = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        canvas.drawColor(Color.GRAY);
        //canvas.drawBitmap(kangoo, 10, 10, null);
        canvas.drawBitmap(kangoo,matrix,paint);


    }









    /** PanZoomWithTouch function
     *  Listen to touch actions and perform Zoom or Pan respectively
     */
    void PanZoomWithTouch(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://when first finger down, get first point
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=PAN");
                mode = PAN;
                break;
            case MotionEvent.ACTION_POINTER_DOWN://when 2nd finger down, get second point
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); //then get the mide point as centre for zoom
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:       //when both fingers are released, do nothing
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:     //when fingers are dragged, transform matrix for panning
                if (mode == PAN) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x,
                            event.getY() - start.y);
                    Log.d(TAG,"Mapping rect");
                    //start.set(event.getX(), event.getY());
                }
                else if (mode == ZOOM) { //if pinch_zoom, calculate distance ratio for zoom
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
