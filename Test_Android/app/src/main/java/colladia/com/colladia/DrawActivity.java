package colladia.com.colladia;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class DrawActivity extends AppCompatActivity {

    public static final String TAG = "DrawActvity";
    private ClientView canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        canvas = (ClientView) findViewById(R.id.canvas);

    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Log.d(TAG, "OnTouch start");
        canvas.onTouchEvent(event);
        return true;
    }*/

    public void clearCanvas(View v)
    {
        canvas.clearCanvas();
    }
}
