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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.*;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.*;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;
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
	private static final int DAY_MILLIS = 24 * UIUtils.HOUR_MILLIS;

	/**
	 * Flags used with {@link DateUtils#formatDateRange}.
	 */
	private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME
			| DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY;

	/**
	 * {@link StringBuilder} used for formatting time block.
	 */
	private static StringBuilder sBuilder = new StringBuilder(50);
	/**
	 * {@link Formatter} used for formatting time block.
	 */
	private static Formatter sFormatter = new Formatter(UIUtils.sBuilder,
			Locale.getDefault());

	private static StyleSpan sBoldSpan = new StyleSpan(Typeface.BOLD);
	private static Bitmap mebg;


	public static String formatBlockTimeString(final long blockStart,
			final long blockEnd, final Context context) {
		// NOTE: There is an efficient version of formatDateRange in Eclair and
		// beyond that allows you to recycle a StringBuilder.
		return DateUtils.formatDateRange(context, blockStart, blockEnd,
				UIUtils.TIME_FLAGS);
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

	private static final int BRIGHTNESS_THRESHOLD = 130;

	/**
	 * Calculate whether a color is light or dark, based on a commonly known
	 * brightness formula.
	 * 
	 * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
	 */
	public static boolean isColorDark(final int color) {
		return (30 * Color.red(color) + 59 * Color.green(color) + 11 * Color
				.blue(color)) / 100 <= UIUtils.BRIGHTNESS_THRESHOLD;
	}

	private static final long sAppLoadTime = System.currentTimeMillis();


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

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
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

	static boolean isApiHighEnough(final int requiredApiLevel) {
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
}
