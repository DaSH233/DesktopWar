package com.war.desktop.desktopwar;

import android.graphics.Path;
import android.graphics.Point;

/**
 * Created by czy on 2016/12/24.
 */
public class Hexagon {
    private Path path = new Path();
    private Point center = new Point();
    private int group;
    private int rank;
    private int type;

    public Hexagon(){
        path.reset();
        center.set(-10, -10);
        group = -1;
        rank = 0;
        type = 0;
    }

    public Hexagon(Path path, Point center, int group, int rank, int type) {
        this.path.set(path);
        this.center.set(center.x, center.y);
        this.group = group;
        this.rank = rank;
        this.type = type;
    }

    public void reset() {
        path.reset();
        center.set(-10, -10);
        group = -1;
        rank = 0;
        type = 0;
    }

    public void set(Path path, Point center, int group, int rank, int type) {
        this.path.set(path);
        this.center.set(center.x, center.y);
        this.group = group;
        this.rank = rank;
        this.type = type;
    }

    public void set(Hexagon hexagon) {
        this.path.set(hexagon.getPath());
        this.center.set(hexagon.getCenter().x, hexagon.getCenter().y);
        this.group = hexagon.getGroup();
        this.rank = hexagon.getRank();
        this.type = hexagon.getType();
    }

    public void setPath(Path path) {
        this.path.set(path);
    }

    public void setCenter(Point center) {
        this.center.set(center.x, center.y);
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    public Point getCenter() {
        return center;
    }

    public FloatPoint getXYCenter(int linelength) {
        float sqrt3 = (float) Math.sqrt(3);
        float x = (center.x + (float)center.y / 2) * linelength * sqrt3;
        float y = (center.y * sqrt3 / 2) * linelength * sqrt3;
        return new FloatPoint(x, y);
    }

    public int getGroup() {
        return group;
    }

    public int getRank() {
        return rank;
    }

    public int getType() {
        return type;
    }
}
