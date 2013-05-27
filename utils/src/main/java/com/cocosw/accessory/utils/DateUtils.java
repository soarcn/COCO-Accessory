package com.cocosw.accessory.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 和日期有关的工具类
 * 
 * @author Administrator
 */
public class DateUtils {

	public static String getDisplayDateStr(final String dateStr) {
		// dateStr = "Thu Apr 30 01:33:41 +0000 2009";
		final Date dd = new Date(dateStr);
		final SimpleDateFormat myFmt = new SimpleDateFormat(
				"yyyy年MM月dd日 HH时mm分ss秒");
		String str = "";
		try {
			str = myFmt.format(dd);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String getDateStr(final String dateStr) {
		final Date date = DateUtils.str2Date(dateStr);
		return DateUtils.getRelativeDate(date);
	}

	public static String getDateStr(final Long l) {
		final Date date = DateUtils.long2Date(l);
		return DateUtils.getRelativeDate(date);
	}

	public static Date long2Date(final Long l) {
		final Date d = new Date();
		d.setTime(l);
		return d;
	}

	// 输入"Thu Apr 30 01:33:41 +0000 2009" 返回Date
	public static Date str2Date(final String dateStr) {
		return new Date(dateStr);
	}

	public static Long dateStr2Long(final String dateStr) {
		if (TextUtils.isEmpty(dateStr)) {
			return new Date().getTime();
		}
		return new Date(dateStr).getTime();
	}

	public static String getRelativeDate(final Date date) {
		final long now = System.currentTimeMillis();
			return android.text.format.DateUtils.getRelativeTimeSpanString(
					date.getTime(), now,
					android.text.format.DateUtils.MINUTE_IN_MILLIS).toString();
	}

	/**
	 * 判断传入的时间是否已经大于后者的秒数
	 * 
	 * @param time
	 * @param duration
	 * @return
	 */
	public static boolean isOlderThan(final long time, final long duration) {
		return (new Date().getTime() - time > duration);
	}

	public static String currentTime() {
		return String.valueOf(new Date().getTime());
	}

	public static boolean isSameDay(final long time1, final long time2) {
		final Calendar cal1 = Calendar.getInstance();
		final Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
						.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 判断是否是今天内的时间戳
	 *
	 * @return
	 */
	public static boolean isToday(final String t) {
		if (t == null) {
			return true;
		}
		if (DateUtils.isSameDay(Long.valueOf(t), new Date().getTime())) {
			return false;
		} else {
			return true;
		}
	}

	public static String formatSameDayTime(final Context context,
			final long timestamp) {
		if (context == null) {
			return null;
		}
		if (android.text.format.DateUtils.isToday(timestamp)) {
			return android.text.format.DateUtils
					.formatDateTime(
							context,
							timestamp,
							android.text.format.DateFormat
									.is24HourFormat(context) ? android.text.format.DateUtils.FORMAT_SHOW_TIME
									| android.text.format.DateUtils.FORMAT_24HOUR
									: android.text.format.DateUtils.FORMAT_SHOW_TIME
											| android.text.format.DateUtils.FORMAT_12HOUR);
		}
		return android.text.format.DateUtils.formatDateTime(context, timestamp,
				android.text.format.DateUtils.FORMAT_SHOW_DATE);
	}

	public static String formatTimeStampString(final Context context,
			final long timestamp) {
		if (context == null) {
			return null;
		}
		final Time then = new Time();
		then.set(timestamp);
		final Time now = new Time();
		now.setToNow();

		int format_flags = android.text.format.DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| android.text.format.DateUtils.FORMAT_ABBREV_ALL
				| android.text.format.DateUtils.FORMAT_CAP_AMPM;

		if (then.year != now.year) {
			format_flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR
					| android.text.format.DateUtils.FORMAT_SHOW_DATE;
		} else if (then.yearDay != now.yearDay) {
			format_flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
		} else {
			format_flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
		}

		return android.text.format.DateUtils.formatDateTime(context, timestamp,
				format_flags);
	}

	@SuppressWarnings("deprecation")
	public static String formatTimeStampString(final Context context,
			final String date_time) {
		if (context == null) {
			return null;
		}
		return DateUtils.formatTimeStampString(context, Date.parse(date_time));
	}

	public static String formatToLongTimeString(final Context context,
			final long timestamp) {
		if (context == null) {
			return null;
		}
		final Time then = new Time();
		then.set(timestamp);
		final Time now = new Time();
		now.setToNow();

		int format_flags = android.text.format.DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| android.text.format.DateUtils.FORMAT_ABBREV_ALL
				| android.text.format.DateUtils.FORMAT_CAP_AMPM;

		format_flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE
				| android.text.format.DateUtils.FORMAT_SHOW_TIME;

		return android.text.format.DateUtils.formatDateTime(context, timestamp,
				format_flags);
	}
}
