package com.nf28_ia04.colladia.draw_test;

/**
 * Created by Mar on 14/05/2016.
 */
/** Stores data about single circle */
public class CircleArea {
    int radius;
    int centerX;
    int centerY;

    CircleArea(int centerX, int centerY, int radius) {
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public String toString() {
        return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
    }
}