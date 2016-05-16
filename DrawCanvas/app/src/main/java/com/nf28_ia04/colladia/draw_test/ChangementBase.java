package com.nf28_ia04.colladia.draw_test;

import android.graphics.PointF;

/**
 * Created by Mar on 15/05/2016.
 */
public class ChangementBase {

    public static PointF AbsoluteToWindow(float pointX, float pointY, float absoluteRootX, float absoluteRootY){
        return new PointF(pointX + absoluteRootX, pointY + absoluteRootY);
    }

    public static PointF WindowToAbsolute(float pointX, float pointY, float absoluteRootX, float absoluteRootY){
        return new PointF(pointX - absoluteRootX, pointY - absoluteRootY);
    }
}
