package colladia.com.colladia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Charlie on 06/05/2016.
 */
public class ClientView extends View {

    private int width, height;
    private float xPos, yPos;
    private Canvas canvas;
    private Paint paint;
    private Paint border;
    private Context ctx;
    private Bitmap bitmap;

    private static float TOLERANCE = 5;

    public ClientView(Context c, AttributeSet attrs)
    {
        super(c, attrs);
        init(c);
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

        canvas.drawRect(0, 0, 2000, 2000, border);

        canvas.drawCircle(900, 100, 100, paint);
        canvas.drawRect(0, 0, 20, 30, paint);

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
                scrollBy(-(int)dx, -(int)dy);
                xPos = x;
                yPos = y;
            }
        }

        Log.d("ClientView", "New pos x: " + xPos + " y: " + yPos);
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

        switch(evt.getAction())
        {
            // Get the time pressed to do the right action
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                //time = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
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
}
