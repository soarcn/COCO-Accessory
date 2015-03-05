/*
 * Copyright 2012 GitHub Inc.
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
package com.cocosw.accessory.views.complex;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;

import java.lang.reflect.Method;

/**
 * Web view extension with scrolling fixes
 */
public class WebView extends android.webkit.WebView {

    private OnScrollChangedListener mOnScrollChangedListener;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     * @param privateBrowsing
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public WebView(final Context context, final AttributeSet attrs,
                   final int defStyle, final boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
        enablePlugins(false);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WebView(final Context context, final AttributeSet attrs,
                   final int defStyle) {
        super(context, attrs, defStyle);
        enablePlugins(false);
    }

    /**
     * @param context
     * @param attrs
     */
    public WebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        enablePlugins(false);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    /**
     * Whether long enough to have vertical scrollbar
     * @return
     */
    public boolean existVerticalScrollbar() {
        return computeVerticalScrollRange() > computeVerticalScrollExtent();
    }

    /**
     * @param context
     */
    public WebView(final Context context) {
        super(context);
    }

    private boolean canScrollCodeHorizontally(final int direction) {
        final int range = computeHorizontalScrollRange()
                - computeHorizontalScrollExtent();
        if (range == 0) {
            return false;
        }

        if (direction < 0) {
            return computeHorizontalScrollOffset() > 0;
        } else {
            return computeHorizontalScrollOffset() < range - 1;
        }
    }

    @TargetApi(14)
    @Override
    public boolean canScrollHorizontally(final int direction) {
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            return super.canScrollHorizontally(direction);
        } else {
            return canScrollCodeHorizontally(direction);
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        // Workaround for problems in Android 4.2.x
        // You can't type properly in input texts and nor textareas until you
        // touch the webview
        // You can't change the font size until you touch the webview
        // http://stackoverflow.com/questions/15127762/webview-fails-to-render-until-touched-android-4-2-2
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            invalidate();
        }
        super.onDraw(canvas);
    }


    protected void enablePlugins(final boolean enabled) {
        // Android 4.3 and above has no concept of plugin states
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }

        if (VERSION.SDK_INT >= VERSION_CODES.FROYO) {
            // Note: this is needed to compile against api level 18.
            try {
                Class<Enum> pluginStateClass = (Class<Enum>) Class
                        .forName("android.webkit.WebSettings$PluginState");

                Class<?>[] parameters = {pluginStateClass};
                Method method = getSettings().getClass()
                        .getDeclaredMethod("setPluginState", parameters);

                Object pluginState = Enum.valueOf(pluginStateClass, enabled ? "ON" : "OFF");
                method.invoke(getSettings(), pluginState);
            } catch (Exception ignored) {

            }
        } else {
            try {
                Method method = Class.forName("android.webkit.WebSettings")
                        .getDeclaredMethod("setPluginsEnabled", boolean.class);
                method.invoke(getSettings(), enabled);
            } catch (Exception ignored) {
            }
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener mOnScrollChangedListener) {
        this.mOnScrollChangedListener = mOnScrollChangedListener;
    }


    public interface OnScrollChangedListener {
        void onScrollChanged(WebView webView, int l, int t, int oldl, int oldt);
    }
}
