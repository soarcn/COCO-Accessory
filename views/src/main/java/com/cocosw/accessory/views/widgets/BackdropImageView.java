package com.cocosw.accessory.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

import com.cocosw.accessory.views.Colour;
import com.cocosw.accessory.views.foreground.ForegroundImageView;

/**
 * from https://github.com/chrisbanes/philm/blob/master/app/src/main/java/app/philm/in/view/BackdropImageView.java
 * <p/>
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/8/29.
 */
public class BackdropImageView extends ForegroundImageView {

    private static final int MIN_SCRIM_ALPHA = 20;
    private static final int MAX_SCRIM_ALPHA = 180;
    private static final int SCRIM_ALPHA_DIFF = MAX_SCRIM_ALPHA - MIN_SCRIM_ALPHA;

    private float mScrimDarkness;
    private int mScrimColor = Color.TRANSPARENT;
    private int mScrollOffset;
    private int mImageOffset;

    private final Paint mScrimPaint;

    private boolean scrimEnable = false;

    public BackdropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrimPaint = new Paint();
    }

    public void setScrollOffset(int offset) {
        if (offset != mScrollOffset) {
            mScrollOffset = offset;
            mImageOffset = -offset / 2;
            mScrimDarkness = Math.abs(offset / (float) getHeight());
            offsetTopAndBottom(offset - getTop());

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mScrollOffset != 0) {
            offsetTopAndBottom(mScrollOffset - getTop());
        }
    }

    public void setScrimColor(int scrimColor) {
        if (mScrimColor != scrimColor) {
            mScrimColor = scrimColor;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Update the scrim paint
        mScrimPaint.setColor(Colour.setColorAlpha(mScrimColor,
                MIN_SCRIM_ALPHA + (int) (SCRIM_ALPHA_DIFF * mScrimDarkness)));

        if (mImageOffset != 0) {
            canvas.save();
            canvas.translate(0f, mImageOffset);
            canvas.clipRect(0f, 0f, canvas.getWidth(), canvas.getHeight() + mImageOffset + 1);
            super.onDraw(canvas);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mScrimPaint);
            canvas.restore();
        } else {
            super.onDraw(canvas);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mScrimPaint);
        }
    }
}
