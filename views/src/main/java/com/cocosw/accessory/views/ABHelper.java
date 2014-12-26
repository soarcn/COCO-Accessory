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
        height = actionBarHeight;
        if (ab_bg != null) {
            ab_bg.setAlpha(0);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                ab_bg.setCallback(mDrawableCallback);
            }
            setActionBarBackground(ab_bg);
        }
    }

    public ABHelper(Drawable ab_bg, View header, int actionBarHeight) {
        this.ab_bg = ab_bg;
        this.header = header;
        height = actionBarHeight;
        if (ab_bg != null) {
            ab_bg.setAlpha(0);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                ab_bg.setCallback(mDrawableCallback);
            }
            setActionBarBackground(ab_bg);
        }
    }

    public float onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount) {
        if (visibleItemCount > 0 && firstVisibleItem == 0) {
            final View firstView = absListView.getChildAt(0);
            final int y = absListView.getPaddingTop() - firstView.getTop();
            final float percent = y / (float) (firstView.getHeight() - height);
            if (ab_bg != null)
                ab_bg.setAlpha((int) (255 * percent));
            return percent;
        }
        return 0;
    }

    public float onScroll(ScrollView scrollView, int topScroll) {
        final int headerHeight = header.getHeight() - height;
        final float ratio = (float) Math.min(Math.max(topScroll, 0), headerHeight) / headerHeight;
        if (ab_bg != null)
            ab_bg.setAlpha((int) (255 * ratio));
        return ratio;
    }
}
