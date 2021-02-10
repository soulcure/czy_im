package com.youmai.hxsdk.module.photo.animator;

import java.util.ArrayList;

/**
 * Created by yw on 2017/9/8.
 * tools:
 */

public class AnimatorPath {

    //一系列路径
    ArrayList<PathPoint> mPoints = new ArrayList<>();

    public ArrayList<PathPoint> getPoints() {
        return mPoints;
    }

    public void moveTo(int x, int y) {
        mPoints.add(PathPoint.moveTo(x, y));
    }

    public void moveToAndAlpha(int x, int y, float alpha) {
        mPoints.add(PathPoint.moveToAndAlpha(x, y, alpha));
    }

    public void lineTo(int x, int y) {
        mPoints.add(PathPoint.lineTo(x, y));
    }

    public void cubicTo(int c0x, int c0y, int c1x, int c1y, int x, int y) {
        mPoints.add(PathPoint.cubicTo(c0x, c0y, c1x, c1y, x, y));
    }

}
