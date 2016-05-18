package com.nf28_ia04.colladia.draw_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    DrawColladia d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        d = (DrawColladia) findViewById(R.id.draw);

    }


    public void passToInsert(View view){
        if(d.mode!=DrawColladia.INSERT)
            d.mode = DrawColladia.INSERT;
        else
            d.mode= DrawColladia.NONE;
    }



}
