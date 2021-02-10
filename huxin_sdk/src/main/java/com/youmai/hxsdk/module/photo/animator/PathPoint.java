package com.youmai.hxsdk.module.photo.animator;

/**
 * Created by yw on 2017/9/8.
 * 存储一个路径：绘制点的路线集合（参数坐标）
 * 路径：LINE...
 */

public class PathPoint {
    public static final int MOVE = 0;
    public static final int LINE = 1;
    public static final int CUBIC = 2;

    float mOperation;
    private float mX, mY, mAlpha; //起始点坐标
    float mControl0X, mControl1X;
    float mControl0Y, mControl1Y;


    private PathPoint(float operation, float x, float y) {
        mOperation = operation;
        mX = x;
        mY = y;
    }

    private PathPoint(float operation, float x, float y, float alpha) {
        mOperation = operation;
        mX = x;
        mY = y;
        mAlpha = alpha;
    }

    private PathPoint(int operation, int c0x, int c0y, int c1x, int c1y, int x, int y) {
        mOperation = operation;
        mControl0X = c0x;
        mControl0Y = c0y;
        mControl1X = c1x;
        mControl1Y = c1y;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public static PathPoint moveTo(float x, float y) {
        return new PathPoint(MOVE, x, y);
    }

    public static PathPoint moveToAndAlpha(float x, float y, float alpha) {
        return new PathPoint(MOVE, x, y, alpha);
    }

    public static PathPoint lineTo(float x, float y) {
        return new PathPoint(LINE, x, y);
    }

    public static PathPoint cubicTo(int c0x, int c0y, int c1x, int c1y, int x, int y) {
        return new PathPoint(CUBIC, c0x, c0y, c1x, c1y, x, y);
    }

}
