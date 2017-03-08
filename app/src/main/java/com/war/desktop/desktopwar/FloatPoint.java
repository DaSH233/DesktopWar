package com.war.desktop.desktopwar;

/**
 * Created by czy on 2016/12/23.
 */
public class FloatPoint {
    public float x;
    public float y;

    public FloatPoint(){
        this.x = 0;
        this.y = 0;
    }

    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(FloatPoint point) {
        this.x = point.x;
        this.y = point.y;
    }
}
