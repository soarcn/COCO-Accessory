package com.cocosw.accessory.views.complex;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class SafeViewFlipper extends ViewFlipper {
    public static interface Listener {
        public abstract void displayedChildChanged(int i, int j);
    }


    public SafeViewFlipper(Context context) {
        super(context);
    }

    public SafeViewFlipper(Context context, AttributeSet attributeset) {
        super(context, attributeset);
    }

    private int getChildIndex(int i) {

        for (int j = 0; j < getChildCount(); j++) {
            if (getChildAt(j).getId() == i)
                return j;
        }
        return -1;

    }

    public int getDisplayedChildId() {
        int i = getDisplayedChild();
        int j;
        if (i >= 0 && i < getChildCount())
            j = getChildAt(i).getId();
        else
            j = -1;
        return j;
    }

    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            // This happens when you're rotating and opening the keyboard that the same time
            // Possibly other rotation related scenarios as well
            stopFlipping();
        }
    }

    public void setDisplayedChild(int i) {
        int j = getDisplayedChild();
        super.setDisplayedChild(i);
        if (listener != null && j != i)
            listener.displayedChildChanged(j, i);
    }

    public void setDisplayedChildById(int i) {
        int j = getChildIndex(i);
        if (j != getDisplayedChild())
            setDisplayedChild(j);
    }

    public void setListener(Listener listener1) {
        listener = listener1;
    }

    private Listener listener;
}
