package com.cocosw.accessory.utils;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {

    public AnimationHelper() {
    }

    public static Animation fadeIn(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final AlphaAnimation alphaanimation = new AlphaAnimation(0F, 1F);
        alphaanimation.setDuration(300L);
        alphaanimation.setInterpolator(new LinearInterpolator());
        alphaanimation.setAnimationListener(animationlistener);
        return alphaanimation;
    }

    public static Animation fadeInLong(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final AlphaAnimation alphaanimation = new AlphaAnimation(0F, 1F);
        alphaanimation.setDuration(600L);
        alphaanimation.setInterpolator(new LinearInterpolator());
        alphaanimation.setAnimationListener(animationlistener);
        return alphaanimation;
    }

    public static Animation fadeOut(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final AlphaAnimation alphaanimation = new AlphaAnimation(1F, 0F);
        alphaanimation.setDuration(300L);
        alphaanimation.setInterpolator(new LinearInterpolator());
        alphaanimation.setAnimationListener(animationlistener);
        return alphaanimation;
    }

    public static AnimationSet flash(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final AnimationSet animationset = new AnimationSet(true);
        final AlphaAnimation alphaanimation = new AlphaAnimation(0F, 1F);
        alphaanimation.setStartOffset(500L);
        alphaanimation.setDuration(100L);
        animationset.addAnimation(alphaanimation);
        final AlphaAnimation alphaanimation1 = new AlphaAnimation(1F, 0F);
        alphaanimation1.setDuration(100L);
        alphaanimation1.setStartOffset(1200L);
        animationset.addAnimation(alphaanimation1);
        animationset.setAnimationListener(animationlistener);
        return animationset;
    }

    public static Animation inFromBottomAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                0F, 2, 0F, 2, 1F, 2, 0F);
        translateanimation.setDuration(600L);
        translateanimation.setInterpolator(new DecelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation inFromBottomQuickAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                0F, 2, 0F, 2, 1F, 2, 0F);
        translateanimation.setDuration(300L);
        translateanimation.setInterpolator(new DecelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation inFromLeftAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                -1F, 2, 0F, 2, 0F, 2, 0F);
        translateanimation.setDuration(300L);
        translateanimation.setInterpolator(new AccelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation inFromRightAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                1F, 2, 0F, 2, 0F, 2, 0F);
        translateanimation.setDuration(300L);
        translateanimation.setInterpolator(new AccelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation outToBottomAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                0F, 2, 0F, 2, 0F, 2, 1F);
        translateanimation.setDuration(600L);
        translateanimation.setInterpolator(new AccelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation outToBottomQuickAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                0F, 2, 0F, 2, 0F, 2, 1F);
        translateanimation.setDuration(300L);
        translateanimation.setInterpolator(new AccelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation outToLeftAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                0F, 2, -1F, 2, 0F, 2, 0F);
        translateanimation.setDuration(300L);
        translateanimation.setInterpolator(new AccelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation outToRightAnimation(
            final android.view.animation.Animation.AnimationListener animationlistener) {
        final TranslateAnimation translateanimation = new TranslateAnimation(2,
                0F, 2, 1F, 2, 0F, 2, -1F);
        translateanimation.setDuration(300L);
        translateanimation.setInterpolator(new AccelerateInterpolator());
        translateanimation.setAnimationListener(animationlistener);
        return translateanimation;
    }

    public static Animation rotationAnimation() {
        final RotateAnimation rotate = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(600);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        return rotate;

    }
}