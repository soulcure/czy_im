package com.youmai.hxsdk.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by fylder on 2017/3/14.
 */

public class AnimatorUtils {

    /**
     * 显示
     *
     * @param view
     */
    public static void show(final View view) {
        show(view, 700);
    }

    /**
     * @param view
     * @param duration
     */
    public static void show(final View view, long duration) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f, 1));
            set.setDuration(duration);
            set.start();

        }
    }

    /**
     * 隐藏
     *
     * @param view
     */
    public static void hide(final View view) {
        hide(view, 700);
    }

    /**
     * 隐藏
     *
     * @param view
     */
    public static void hide(final View view, long duration) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 0));
            set.setDuration(duration);
            set.start();
        }
    }

    /**
     * 上移动
     *
     * @param view
     */
    public static void moveTop(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "translationY", 0, -view.getHeight() / 3));
            set.setDuration(700);
            set.start();
        }
    }

    /**
     * 上移动
     *
     * @param view
     */
    public static void moveFromDown(final View view) {
        moveFromDown(view, 400);
    }

    /**
     * 上移动
     *
     * @param view
     */
    public static void moveFromDown(final View view, final long duration) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0),
                        ObjectAnimator.ofFloat(view, "alpha", 0, 1));
                set.setDuration(duration);
                set.setInterpolator(new DecelerateInterpolator());
                set.start();
            }
        });
    }

    /**
     * 下移动
     *
     * @param view
     */
    public static void moveFromUp(final View view, final long duration) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight()),
                        ObjectAnimator.ofFloat(view, "alpha", 1, 0));
                set.setDuration(duration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
                set.start();
            }
        });
    }

    /**
     * 向下移动
     *
     * @param view
     */
    public static void moveBack(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "translationY", -view.getHeight() / 3, 0));
            set.setDuration(700);
            set.start();

        }
    }

    /**
     * 显示一会
     *
     * @param duration 总时间
     */
    public static void showMoment(View view, long duration) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f, 1));
        set.setDuration(duration / 2);
        set.start();

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 0));
        set2.setDuration(duration / 2);
        set.setStartDelay(duration / 2);
        set2.start();
    }


    /**
     * 旋转
     *
     * @param view
     */
    public static void rotationY(View view, long duration, Animator.AnimatorListener listener) {
        ObjectAnimator ra = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f);
        ra.setDuration(duration);
        ra.addListener(listener);
        ra.start();
    }

}
