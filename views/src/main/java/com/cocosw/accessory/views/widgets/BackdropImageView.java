package com.cocosw.accessory.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

import com.cocosw.accessory.view.R;
import com.cocosw.accessory.views.Colour;
import com.cocosw.accessory.views.foreground.ForegroundImageView;

/**
 * from https://github.com/chrisbanes/philm/blob/master/app/src/main/java/app/philm/in/view/BackdropImageView.java
 * <p/>
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/8/29.
 */
public class BackdropImageView extends ForegroundImageView {

    private static int MIN_SCRIM_ALPHA;
    private static int MAX_SCRIM_ALPHA;
    private static int SCRIM_ALPHA_DIFF;

    private float mScrimDarkness;
    private int mScrimColor;
    private int mScrollOffset;
    private int mImageOffset;

    private final Paint mScrimPaint;

    private float friction = 0.5f;
    private int scrimOffset;

    public BackdropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrimPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BackdropImageView);
        mScrimColor = a.getColor(R.styleable.BackdropImageView_scrimColor, Color.BLACK);
        MIN_SCRIM_ALPHA = a.getInteger(R.styleable.BackdropImageView_minScrim, 20);
        MAX_SCRIM_ALPHA = a.getInteger(R.styleable.BackdropImageView_maxScrim, 255);
        friction = a.getFloat(R.styleable.BackdropImageView_friction, 0.5f);
        SCRIM_ALPHA_DIFF = MAX_SCRIM_ALPHA - MIN_SCRIM_ALPHA;
        a.recycle();
    }

    public void setScrollOffset(int offset) {
        if (offset != mScrollOffset) {
            mScrollOffset = offset;
            mImageOffset = (int) (-offset * friction);
            mScrimDarkness = Math.abs(offset / (float) (getHeight() - scrimOffset));
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

    public void setScrimOffset(int offset) {
        this.scrimOffset = offset;
    }

    public void setOffsetFriction(float friction) {
        if (friction <= 1 && friction > 0)
            this.friction = friction;
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
