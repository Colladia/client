package com.ia04nf28.colladia.model;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by JeanV on 18/05/2016.
 */
public class User {
    private String login;
    private int color;

    public User(String l, int c) {
        login = l;
        color = c;
    }

    public User(String l) {
        login = l;
        Random rnd = new Random();
        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public String getLogin() {
        return login;
    }

    public int getColor() {
        return color;
    }
}
