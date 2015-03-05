/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cocosw.accessory.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import java.util.TimeZone;

/**
 * An assortment of UI helpers.
 */
public class UIUtils {
    /**
     * Time zone to use when formatting all session times. To always use the
     * phone local time, use {@link TimeZone#getDefault()}.
     */

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * UIUtils.SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * UIUtils.MINUTE_MILLIS;

    /**
     * Flags used with {@link DateUtils#formatDateRange}.
     */
    private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME
            | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY;

    /**
     * {@link StringBuilder} used for formatting time block.
     */
    private static StringBuilder sBuilder = new StringBuilder(50);


    private static StyleSpan sBoldSpan = new StyleSpan(Typeface.BOLD);

    public static String formatBlockTimeString(final long blockStart,
                                               final long blockEnd, final Context context) {
        // NOTE: There is an efficient version of formatDateRange in Eclair and
        // beyond that allows you to recycle a StringBuilder.
        return DateUtils.formatDateRange(context, blockStart, blockEnd,
                UIUtils.TIME_FLAGS);
    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    /**
     * Populate the given {@link TextView} with the requested text, formatting
     * through {@link Html#fromHtml(String)} when applicable. Also sets
     * {@link TextView#setMovementMethod} so inline links are handled.
     */
    public static void setTextMaybeHtml(final TextView view, final String text) {
        if (TextUtils.isEmpty(text)) {
            view.setText("");
            return;
        }
        if (text.contains("<") && text.contains(">")) {
            view.setText(Html.fromHtml(text));
            view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            view.setText(text);
        }
    }


    /**
     * Given a snippet string with matching segments surrounded by curly braces,
     * turn those areas into bold spans, removing the curly braces.
     */
    public static Spannable buildStyledSnippet(final String snippet) {
        final SpannableStringBuilder builder = new SpannableStringBuilder(
                snippet);

        // Walk through string, inserting bold snippet spans
        int startIndex = -1, endIndex = -1, delta = 0;
        while ((startIndex = snippet.indexOf('{', endIndex)) != -1) {
            endIndex = snippet.indexOf('}', startIndex);

            // Remove braces from both sides
            builder.delete(startIndex - delta, startIndex - delta + 1);
            builder.delete(endIndex - delta - 1, endIndex - delta);

            // Insert bold style
            builder.setSpan(UIUtils.sBoldSpan, startIndex - delta, endIndex
                    - delta - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            delta += 2;
        }

        return builder;
    }

    public static void preferPackageForIntent(final Context context,
                                              final Intent intent, final String packageName) {
        final PackageManager pm = context.getPackageManager();
        for (final ResolveInfo resolveInfo : pm
                .queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.packageName.equals(packageName)) {
                intent.setPackage(packageName);
                break;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setActivatedCompat(final View view,
                                          final boolean activated) {
        if (UIUtils.hasHoneycomb()) {
            view.setActivated(activated);
        }
    }

    public static boolean isGoogleTV(final Context context) {
        return context.getPackageManager().hasSystemFeature(
                "com.google.android.tv");
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasKitKat() {
        return isApiHighEnough(VERSION_CODES.KITKAT);
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed
        // behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasICSMR1() {
        return isApiHighEnough(VERSION_CODES.ICE_CREAM_SANDWICH_MR1);
    }

    public static boolean hasJBMR1() {
        return isApiHighEnough(Build.VERSION_CODES.JELLY_BEAN_MR1);
    }

    public static boolean hasJBMR2() {
        return isApiHighEnough(Build.VERSION_CODES.JELLY_BEAN_MR2);
    }

    public static boolean hasJellyBean() {
        return isApiHighEnough(Build.VERSION_CODES.JELLY_BEAN);
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isTablet(final Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isHoneycombTablet(final Context context) {
        return UIUtils.hasHoneycomb() && UIUtils.isTablet(context);
    }

    private static String guessAppropriateEncoding(final CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static int dip2px(final Context context, final float dipValue) {
        final float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    public static int px2dip(final Context context, final float pxValue) {
        final float m = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / m + 0.5f);
    }

    public static void disableHardwareAcceleration(final View view) {
        if (UIUtils.isApiHighEnough(VERSION_CODES.HONEYCOMB)) {
            SDK11.disableHardwareAcceleration(view);
        }
    }

    public static boolean isCanvasHardwareAccelerated(final Canvas canvas) {
        if (UIUtils.isApiHighEnough(VERSION_CODES.HONEYCOMB)) {
            return SDK11.isCanvasHardwareAccelerated(canvas);
        }
        return false;
    }

    public static boolean isApiHighEnough(final int requiredApiLevel) {
        return VERSION.SDK_INT >= requiredApiLevel;
    }

    @TargetApi(11)
    public static class SDK11 {

        public static void disableHardwareAcceleration(final View view) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        public static boolean isCanvasHardwareAccelerated(final Canvas canvas) {
            return canvas.isHardwareAccelerated();
        }

    }

    @SuppressLint("NewApi")
    public static int getScreenWidth(final Activity act) {
        if (isApiHighEnough(13)) {
            Point point = new Point();
            act.getWindowManager().getDefaultDisplay().getSize(point);
            return point.x;
        } else
            return act.getWindowManager().getDefaultDisplay().getWidth();
    }

    @SuppressLint("NewApi")
    public static int getScreenHight(final Activity act) {
        if (isApiHighEnough(13)) {
            Point point = new Point();
            act.getWindowManager().getDefaultDisplay().getSize(point);
            return point.y;
        } else
            return act.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * Returns the location of the view on the screen. The screen includes the 'notification area' (aka 'status bar').
     */
    public static Rect getLocationInScreen(View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];
        int width = v.getWidth();
        int height = v.getHeight();
        Rect rectPick = new Rect(x, y, x + width, y + height);
        return rectPick;
    }

    /**
     * Returns the location of the view on its window. The window does not include the 'notification area' (aka 'status bar').
     */
    public static Rect getLocationInWindow(View v) {
        // Height of status bar
        Rect rect = new Rect();
        ((Activity) v.getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        Rect res = getLocationInScreen(v);

        res.offset(0, -statusBarHeight);
        return res;
    }

    /**
     * Determines if given points are inside view
     *
     * @param x    - x coordinate of point
     * @param y    - y coordinate of point
     * @param view - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean inRegion(int rx, int ry, View view) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = view.getWidth();
        int h = view.getHeight();

        if (rx < x || rx > x + w || ry < y || ry > y + h) {
            return false;
        }
        return true;
    }

}
