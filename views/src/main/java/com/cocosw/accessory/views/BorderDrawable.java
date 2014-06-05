package com.cocosw.accessory.views;

/* The MIT License (MIT)

Copyright (c) 2014, Marty Glaubitz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE. */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;


/**
 * https://gist.github.com/martyglaubitz/37aa0268727835856061
 *
 * viewWhichNeedsBorders.setBackground(new BorderDrawable(null, Color.Black, null, Color.Black, 0, 3, 0, 3))
 */

public class BorderDrawable extends ColorDrawable {

    private Paint   _PaintLeft;
    private Paint   _PaintTop;
    private Paint   _PaintRight;
    private Paint   _PaintBottom;

    private int     _BorderLeft     = 1;
    private int     _BorderTop      = 1;
    private int     _BorderRight    = 1;
    private int     _BorderBottom   = 1;

    public BorderDrawable(Integer colorLeft, Integer colorTop, Integer colorRight, Integer colorBottom) {
        super();

        if (colorLeft != null) {
            _PaintLeft = new Paint();
            _PaintLeft.setStyle(Paint.Style.FILL);
            _PaintLeft.setColor(colorLeft);
        }

        if (colorTop != null) {
            _PaintTop = new Paint();
            _PaintTop.setStyle(Paint.Style.FILL);
            _PaintTop.setColor(colorTop);
        }

        if (colorRight != null) {
            _PaintRight = new Paint();
            _PaintRight.setStyle(Paint.Style.FILL);
            _PaintRight.setColor(colorRight);
        }

        if (colorBottom != null) {
            _PaintBottom = new Paint();
            _PaintBottom.setStyle(Paint.Style.FILL);
            _PaintBottom.setColor(colorBottom);
        }
    }

    public BorderDrawable(Integer colorLeft, Integer colorTop, Integer colorRight, Integer colorBottom, int borderLeft, int borderTop, int borderRight, int borderBottom) {
        this(colorLeft, colorTop, colorRight, colorBottom);

        _BorderLeft = borderLeft;
        _BorderTop = borderTop;
        _BorderRight = borderRight;
        _BorderBottom = borderBottom;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (_PaintLeft != null) {
            canvas.drawRect(0f, 0f, _BorderLeft, canvas.getHeight(), _PaintLeft);
        }

        if (_PaintTop != null) {
            canvas.drawRect(0f, 0f, canvas.getWidth(), _BorderTop, _PaintTop);
        }

        if (_PaintRight != null) {
            canvas.drawRect(canvas.getWidth() - _BorderRight, 0f, canvas.getWidth(), canvas.getHeight(), _PaintRight);
        }

        if (_PaintBottom != null) {
            canvas.drawRect(0, canvas.getHeight() - _BorderBottom, canvas.getWidth(), canvas.getHeight(), _PaintBottom);
        }

    }
}