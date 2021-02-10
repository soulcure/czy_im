package com.youmai.hxsdk.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.03.07 09:45
 * 描述：
 */
public class ViewAnimUtils {

    /**
     * 商家详情 动画
     *
     * @param view
     * @param delay
     */
    public static void scaleAnim(View view, int delay) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 1.2f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ);
        objectAnimator.setDuration(delay);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();
    }

    /**
     * 聊天界面  trans、alpha、scale
     *
     * @param context
     * @param view
     * @param delay
     */
    public static void multiAnim(Context context, View view, int delay) {
        AnimatorSet set = new AnimatorSet();

        /*ObjectAnimator transXAnim = ObjectAnimator.ofFloat(view, "translationX", 0f,
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_x1)),
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_x2)));
        ObjectAnimator transYAnim = ObjectAnimator.ofFloat(view, "translationY", 0f,
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_y1)),
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_y2)));

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.75f, 0.5f, 0.25f, 0.0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.25f, 0.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.25f, 0.0f);*/

        ObjectAnimator transXAnim = ObjectAnimator.ofFloat(view, "translationX", 0f,
                DisplayUtil.dip2px(context, 140.0f), DisplayUtil.dip2px(context, 160.0f));
        ObjectAnimator transYAnim = ObjectAnimator.ofFloat(view, "translationY", 0f,
                DisplayUtil.dip2px(context, -220.0f), DisplayUtil.dip2px(context, -240.0f));
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.75f, 0.5f, 0.25f, 0.0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.15f, 0.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.15f, 0.0f);

        set.playTogether(transXAnim, transYAnim, alphaAnim, scaleXAnim, scaleYAnim);
        set.setDuration(delay);
        set.start();
    }

    /**
     * 弹屏IM red point tip
     *
     * @param view
     * @param delay
     */
    public static void scaleRedTip(View view, int delay) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 1.5f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ);
        objectAnimator.setDuration(delay);
        objectAnimator.setRepeatCount(2);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();
    }

    /**
     * 弹屏IM red point tip
     *
     * @param view
     * @param delay
     */
    public static void alphaAndScaleAnim(View view, int delay) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.5f, 1.0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 0.5f, 1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 0.5f, 1.0f);
        set.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        set.setDuration(delay);
        set.start();
    }

    /**
     * 通话后红包雨翻转动画
     *
     * @param view
     * @param delay
     */
    public static void rotateAnim(View view, int delay) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f);
        set.playTogether(alphaAnim);
        set.setDuration(delay);
        set.start();
    }

    /**
     * 通话后红包雨翻转动画
     *
     * @param view
     * @param delay
     */
    public static void rotateAnim2(View view, int delay) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "rotationY", 270f, 360f);
        set.playTogether(alphaAnim);
        set.setDuration(delay);
        set.start();
    }


    /**
     * 扑克牌 旋转动画
     *
     * @param v1
     * @param v2
     */
    public static void flip(final View v1, final View v2) {
        final int duration = 500;
        final int degree = 90;
        final int degree2 = -degree;
        final ObjectAnimator a, b;
        a = ObjectAnimator.ofFloat(v1, "rotationY", 0, degree);
        b = ObjectAnimator.ofFloat(v2, "rotationY", degree2, 0);
        a.setDuration(duration);
        b.setDuration(duration);
        a.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);
                v2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        AnimatorSet set = new AnimatorSet();
        set.play(a).before(b);
        set.start();
    }

    /**
     * CallFullView 新手引导
     *
     * @param view
     * @param delay
     */
    public static void floatAnim(View view, int delay) {
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -4.0f, 4.0f, -4.0f);
        translationXAnim.setDuration(400);//1500
        translationXAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        translationXAnim.setRepeatMode(ValueAnimator.REVERSE);
        translationXAnim.start();
        animators.add(translationXAnim);
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -8.0f, 8.0f, -8.0f);
        translationYAnim.setDuration(400);//1000
        translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
        translationYAnim.setRepeatMode(ValueAnimator.REVERSE);
        translationYAnim.start();
        animators.add(translationYAnim);

        AnimatorSet btnSexAnimatorSet = new AnimatorSet();
        btnSexAnimatorSet.playTogether(animators);
        btnSexAnimatorSet.setStartDelay(delay);
        btnSexAnimatorSet.start();
    }

    /**
     * CallFullView 功能区背景透明度渐变
     *
     * @param view
     * @param delay
     */
    public static void alphaAnim(View view, int delay) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.5f, 0.0f, 0.5f, 1.0f);
        alphaAnim.setInterpolator(new LinearInterpolator());
        alphaAnim.setDuration(delay);
        alphaAnim.setRepeatCount(3);
        alphaAnim.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnim.start();
    }

}
