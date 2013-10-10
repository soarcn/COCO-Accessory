package com.cocosw.accessory.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Gallery;

public class GalleryView extends Gallery {

    private ViewGroup root;

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryView(Context context) {
        super(context);
    }

    public void setRootView(ViewGroup view) {
        this.root = view;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (root!=null)
        root.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (root!=null)
        root.requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (root!=null)
        root.requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}
