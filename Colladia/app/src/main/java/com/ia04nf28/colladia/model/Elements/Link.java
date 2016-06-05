package com.ia04nf28.colladia.model.Elements;

import android.graphics.Canvas;

/**
 * Created by Charlie on 31/05/2016.
 */
public class Link {

    protected Anchor start = new Anchor();
    protected Anchor stop = new Anchor();

    public Link(Anchor start, Anchor stop)
    {
        this.start = start;
        this.stop = stop;
    }

    public void set(Anchor start, Anchor stop)
    {
        this.start = start;
        this.stop = stop;
    }

    public void draw(Canvas canvas)
    {
        if(start != null && stop != null)
            canvas.drawLine(start.x, start.y, stop.x, stop.y, Anchor.paint);
    }

}
