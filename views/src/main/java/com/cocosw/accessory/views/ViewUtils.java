/*
 * Copyright 2012 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cocosw.accessory.views;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Utilities for working with the {@link android.view.View} class
 */
public class ViewUtils {

    /**
     * Set visibility of given view to be gone or visible
     * <p/>
     * This method has no effect if the view visibility is currently invisible
     *
     * @param view
     * @param gone
     * @return view
     */
    public static <V extends View> V setGone(final V view, final boolean gone) {
        if (view != null)
            if (gone) {
                if (GONE != view.getVisibility())
                    view.setVisibility(GONE);
            } else {
                if (VISIBLE != view.getVisibility())
                    view.setVisibility(VISIBLE);
            }
        return view;
    }

    /**
     * Set background drawable for given view
     *
     * @param view
     * @param draw
     * @param <V>
     * @return
     */
    public static <V extends View> V setBackground(final V view, final Drawable draw) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(draw);
        } else {
            view.setBackground(draw);
        }
        return view;
    }


    /**
     * Set visibility of given view to be invisible or visible
     * <p/>
     * This method has no effect if the view visibility is currently gone
     *
     * @param view
     * @param invisible
     * @return view
     */
    public static <V extends View> V setInvisible(final V view,
                                                  final boolean invisible) {
        if (view != null)
            if (invisible) {
                if (INVISIBLE != view.getVisibility())
                    view.setVisibility(INVISIBLE);
            } else {
                if (VISIBLE != view.getVisibility())
                    view.setVisibility(VISIBLE);
            }
        return view;
    }

    /**
     * Increases the hit rect of a view. This should be used when an icon is small and cannot be easily tapped on.
     * Source: http://stackoverflow.com/a/1343796/5210
     *
     * @param amount   The amount of dp's to be added to all four sides of the view hit purposes.
     * @param delegate The view that needs to have its hit rect increased.
     */
    public static void increaseHitRectBy(final int amount, final View delegate) {
        increaseHitRectBy(amount, amount, amount, amount, delegate);
    }

    /**
     * Increases the hit rect of a view. This should be used when an icon is small and cannot be easily tapped on.
     * Source: http://stackoverflow.com/a/1343796/5210
     *
     * @param top      The amount of dp's to be added to the top for hit purposes.
     * @param left     The amount of dp's to be added to the left for hit purposes.
     * @param bottom   The amount of dp's to be added to the bottom for hit purposes.
     * @param right    The amount of dp's to be added to the right for hit purposes.
     * @param delegate The view that needs to have its hit rect increased.
     */
    public static void increaseHitRectBy(final int top, final int left, final int bottom, final int right, final View delegate) {
        final View parent = (View) delegate.getParent();
        if (parent != null && delegate.getContext() != null) {
            parent.post(new Runnable() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                public void run() {
                    final float densityDpi = delegate.getContext().getResources().getDisplayMetrics().densityDpi;
                    final Rect r = new Rect();
                    delegate.getHitRect(r);
                    r.top -= transformToDensityPixel(top, densityDpi);
                    r.left -= transformToDensityPixel(left, densityDpi);
                    r.bottom += transformToDensityPixel(bottom, densityDpi);
                    r.right += transformToDensityPixel(right, densityDpi);
                    parent.setTouchDelegate(new TouchDelegate(r, delegate));
                }
            });
        }
    }

    public static int transformToDensityPixel(int regularPixel, DisplayMetrics displayMetrics) {
        return transformToDensityPixel(regularPixel, displayMetrics.densityDpi);
    }

    public static int transformToDensityPixel(int regularPixel, float densityDpi) {
        return (int) (regularPixel * densityDpi);
    }

    private ViewUtils() {

    }

    /**
     * Hide soft input method manager
     *
     * @param view
     * @return view
     */
    public static View hideSoftInput(final View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return view;
    }

    /**
     * Show soft input method manager
     *
     * @param view
     * @return view
     */
    public static View showSoftInput(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.showSoftInput(view, 0);
        return view;
    }


    /**
     * Compat version of removeGlobalOnLayoutListener
     *
     * @param view
     * @param listener
     */
    public static void removeGlobalOnLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            // recommended removeOnGlobalLayoutListener method is available since API 16 only
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        else
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }


    /**
     * Do something when a view's layout is ready
     *
     * @param view
     * @param runnable
     */
    public static void runOnLayoutIsReady(@Nullable final View view, @NonNull final Runnable runnable) {
        if (view != null) {
            if (view.isShown())
                runnable.run();
            else
                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        runnable.run();
                        if (view != null)
                            removeGlobalOnLayoutListener(view, this);
                    }
                });
        }
    }

    /**
     * A compat method for Configuration.getLayoutDirection()
     *
     * @param config
     * @return
     */
    public static boolean isLayoutRTL(Configuration config) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static void scrollToTop(ListView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            view.smoothScrollToPosition(0);
        else view.setSelection(0);
    }

    public static void scrollToEnd(ListView view) {
        if (view == null || view.getAdapter() == null)
            return;
        int n = view.getAdapter().getCount();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            view.smoothScrollToPosition(n);
        else view.setSelection(n);
    }

    @SuppressLint("NewApi")
    public static boolean isLayoutRtl(View view) {
        return (Build.VERSION.SDK_INT >= 17) && (view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);
    }
}
