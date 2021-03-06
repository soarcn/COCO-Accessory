package com.cocosw.accessory.views.complex;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

/**
 * Add 2 addtional function to ViewPager
 * 1 enable/disable swipe
 * 2 set scrollview as root view, viewpager can place into a scroll view
 *
 * @author kaliao
 */
public class CocoPager extends ViewPager {

    private boolean enabled = true;

    private ViewGroup root;

    public CocoPager(Context context) {
        super(context);
    }

    public CocoPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (root != null)
                root.requestDisallowInterceptTouchEvent(true);
            if (enabled)
                return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            Log.e("CocoPager", "onInterceptTouchEvent in IllegalArgumentException");
            return false;
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            if (root != null)
                root.requestDisallowInterceptTouchEvent(true);
            if (enabled)
                return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            Log.e("CocoPager", "onInterceptTouchEvent in IllegalArgumentException");
            return false;
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getPagingEnabled() {
        return enabled;
    }

    @TargetApi(14)
    @Override
    protected boolean canScroll(final View v, final boolean checkV,
                                final int dx, final int x, final int y) {
        if (SDK_INT < ICE_CREAM_SANDWICH && v instanceof WebView)
            return ((WebView) v).canScrollHorizontally(-dx);
        else
            return super.canScroll(v, checkV, dx, x, y);
    }

    public void setRootView(ViewGroup view) {
        this.root = view;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (root != null)
                root.requestDisallowInterceptTouchEvent(true);
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e("CocoPager", "onInterceptTouchEvent in IllegalArgumentException");
            return false;
        }
    }
}
