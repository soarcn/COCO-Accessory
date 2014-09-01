package com.cocosw.accessory.views;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 2014/8/29.
 */
public abstract class ABHelper {

    private Drawable ab_bg;
    private View header;
    private float parallax_friction = 0.5f;
    private int height = 0;

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            setActionBarBackground(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    protected abstract void setActionBarBackground(Drawable who);

    public ABHelper(Drawable ab_bg, int actionBarHeight) {
        this.ab_bg = ab_bg;
        ab_bg.setAlpha(0);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            ab_bg.setCallback(mDrawableCallback);
        }
        height = actionBarHeight;
        setActionBarBackground(ab_bg);
    }

    public ABHelper(Drawable ab_bg, View header, int actionBarHeight) {
        this.ab_bg = ab_bg;
        this.header = header;
        ab_bg.setAlpha(0);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            ab_bg.setCallback(mDrawableCallback);
        }
        height = actionBarHeight;
        setActionBarBackground(ab_bg);
    }

    public void setParallaxFriction(float parallax_friction) {
        this.parallax_friction = parallax_friction;
    }

    public int onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount) {
        if (visibleItemCount > 0 && firstVisibleItem == 0) {
            final View firstView = absListView.getChildAt(0);
            final int y = absListView.getPaddingTop() - firstView.getTop();
            final float percent = y / (float) (firstView.getHeight() - height);

            ab_bg.setAlpha((int) (255 * percent));

            return (Math.round(-y * parallax_friction));
        }
        return 0;
    }

    public int onScroll(ScrollView scrollView, int topScroll) {
        final int headerHeight = header.getHeight() - height;
        final float ratio = (float) Math.min(Math.max(topScroll, 0), headerHeight) / headerHeight;
        ab_bg.setAlpha((int) (255 * ratio));
        return (Math.round(topScroll * parallax_friction));
    }

}
