package com.cocosw.accessory.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

import com.cocosw.accessory.views.foreground.ForegroundImageView;

/**
 * from https://github.com/chrisbanes/philm/blob/master/app/src/main/java/app/philm/in/view/BackdropImageView.java
 * <p/>
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/8/29.
 */
public class BackdropImageView extends ForegroundImageView {
    public BackdropImageView(Context context) {
        super(context);
    }

    public BackdropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int mOffset;

    public void offsetBackdrop(int offset) {
        if (offset != mOffset) {
            mOffset = offset;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mOffset != 0) {
            canvas.save();
            canvas.translate(0f, mOffset);
            canvas.clipRect(0f, 0f, canvas.getWidth(), canvas.getHeight() + mOffset);
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }
}
