/*
 * Copyright (c) 2014. www.cocosw.com
 */

package com.cocosw.accessory.views.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Project: insight
 * Created by LiaoKai(soarcn) on 2014/7/3.
 */
public class NoClickThroughFrameLayout extends View {
    public NoClickThroughFrameLayout(Context context) {
        super(context);
    }

    public NoClickThroughFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoClickThroughFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
