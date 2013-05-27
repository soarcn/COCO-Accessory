package com.cocosw.accessory.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalLine extends View {

    public HorizontalLine(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public HorizontalLine(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        Resources resources = context.getResources();
        linePaint0 = createPaint(resources,0x08000000);
        linePaint1 = createPaint(resources, 0x15000000);
        linePaint2 = createPaint(resources, 0x15ffffff);
        linePaint3 = createPaint(resources, 0x08ffffff);
    }

    private static Paint createPaint(Resources resources, int i) {
        Paint paint = new Paint();
        paint.setStrokeWidth(1F);
        paint.setColor(i);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    protected void onDraw(Canvas canvas) {
        int i = getWidth();
        int j = -1 + 1;
        canvas.drawLine(0F, j, i, j, linePaint0);
        int k = j + 1;
        canvas.drawLine(0F, k, i, k, linePaint1);
        int l = k + 1;
        canvas.drawLine(0F, l, i, l, linePaint2);
        int i1 = l + 1;
        canvas.drawLine(0F, i1, i, i1, linePaint3);
    }

    protected void onMeasure(int i, int j) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), i), 4);
    }

    private final Paint linePaint0;
    private final Paint linePaint1;
    private final Paint linePaint2;
    private final Paint linePaint3;
}
