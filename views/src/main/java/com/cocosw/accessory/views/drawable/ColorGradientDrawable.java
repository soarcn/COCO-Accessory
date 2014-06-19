package com.cocosw.accessory.views.drawable;

import android.graphics.drawable.GradientDrawable;

public class ColorGradientDrawable extends GradientDrawable {

    public ColorGradientDrawable(final int baseColor) {
        super(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                ColorGradientDrawable.createColorGradient(baseColor));
    }

    private static int[] createColorGradient(final int baseColor) {
        final int i = baseColor;
        final int j = ColorGradientDrawable.fadeColor(i, -1, 0.1F);
        final int k = ColorGradientDrawable.fadeColor(i, 0xff000000, 0.1F);
        final int ai[] = new int[3];
        ai[0] = j;
        ai[1] = i;
        ai[2] = k;
        return ai;
    }

    public static int fadeColor(final int i, final int j, final float f) {
        final int k = i >> 24;
        final int l = 0xff & i >> 16;
        final int i1 = 0xff & i >> 8;
        final int j1 = i & 0xff;
        final int k1 = j >> 24;
        final int l1 = 0xff & j >> 16;
        final int i2 = 0xff & j >> 8;
        final int j2 = j & 0xff;
        final int k2 = Math.round(k + f * (k1 - k));
        final int l2 = Math.round(l + f * (l1 - l));
        final int i3 = Math.round(i1 + f * (i2 - i1));
        return Math.round(j1 + f * (j2 - j1)) | (k2 << 24 | l2 << 16 | i3 << 8);
    }
}