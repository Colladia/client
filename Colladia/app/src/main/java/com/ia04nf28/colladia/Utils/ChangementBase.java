package com.ia04nf28.colladia.Utils;

import android.graphics.PointF;

/**
 * Created by Mar on 17/05/2016.
 */
public class ChangementBase {

    public static PointF AbsoluteToWindow(float pointX, float pointY, float absoluteRootX, float absoluteRootY, float zoom){
        return new PointF((pointX + absoluteRootX)*zoom, (pointY + absoluteRootY)*zoom);
    }

    public static PointF WindowToAbsolute(float pointX, float pointY, float absoluteRootX, float absoluteRootY, float zoom){
        return new PointF((pointX - absoluteRootX)/zoom, (pointY - absoluteRootY)/zoom);
    }
}
