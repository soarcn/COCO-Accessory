package com.cocosw.accessory.views;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 2015/8/2.
 */
public class InputFilterMinMax implements InputFilter {

    private boolean integar = false;
    private float min, max;

    public InputFilterMinMax(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
        integar = true;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            float input = Float.parseFloat(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return integar ? ((int) min) + "" : min + "";
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}