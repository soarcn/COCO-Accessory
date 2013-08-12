package com.cocosw.accessory.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


	public static String getImagePathFromUri(final Context context,
			final Uri uri) {
		if (context == null || uri == null) {
			return null;
		}

		final String media_uri_start = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.toString();

		if (uri.toString().startsWith(media_uri_start)) {

			final String[] proj = { MediaColumns.DATA };
			final Cursor cursor = context.getContentResolver().query(uri, proj,
					null, null, null);

			if (cursor == null || cursor.getCount() <= 0) {
				return null;
			}

			final int column_index = cursor
					.getColumnIndexOrThrow(MediaColumns.DATA);

			cursor.moveToFirst();

			final String path = cursor.getString(column_index);
			cursor.close();
			return path;
		} else {
			final String path = uri.getPath();
			if (path != null) {
				if (new File(path).exists()) {
					return path;
				}
			}
		}
		return null;
	}

	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(final String input) {
		final char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 去除特殊字符或将所有中文标号替换为英文标号
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		final String regEx = "[『』]"; // 清除掉特殊字符
		final Pattern p = Pattern.compile(regEx);
		final Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static boolean isEnglish(final CharSequence prefix) {
		for (int i = 0; i < prefix.length(); i++) {
			if (!(prefix.charAt(i) >= 'A' && prefix.charAt(i) <= 'Z')
					&& !(prefix.charAt(i) >= 'a' && prefix.charAt(i) <= 'z')) {
				return false;
			}
		}
		return true;
	}

	public static void printCallStatck() {
		final Throwable ex = new Throwable();
		// StackTraceElement[] stackElements = ex.getStackTrace();
		ex.printStackTrace();
	}

	public static Uri getContactUri(final long contactId) {
		return ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
	}

	/**
	 * Join array to a string.
	 * <p/>
	 * Example:<br>
	 * <p/>
	 * 
	 * <pre>
	 * join(",", new int [] {1, 2, 3)) = "1,2,3"
	 * </pre>
	 * <p/>
	 * You can pass anything as {@code array} as long its {@code toString()}
	 * method makes sense.
	 * 
	 * @param separator
	 *            separator to put between joined string elements
	 * @param array
	 *            object which is an array with elements on which
	 *            {@code toString()} will be called to join them to a string
	 * @return joined string
	 * @throws IllegalArgumentException
	 *             {@code array} is not an array
	 */
	public static String join(final String separator, final Object array) {
		if (!array.getClass().isArray()) {
			throw new IllegalArgumentException("Given object is not an array!");
		}

		final StringBuilder s = new StringBuilder();
		final int length = Array.getLength(array) - 1;

		for (int i = 0; i <= length; i++) {
			s.append(String.valueOf(Array.get(array, i)));

			if (i != length) {
				s.append(separator);
			}
		}

		return s.toString();
	}

	/**
	 * 将Bitmap转成文件放在程序文件目录下
	 */
	public static void Bitmap2File(final Bitmap bitmap, final String fileName,
			final Context ctx) {

		try {
			final FileOutputStream fos = ctx.openFileOutput(fileName,
					android.content.Context.MODE_WORLD_READABLE);
			bitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (final FileNotFoundException e) {
		} catch (final IOException e) {
		}
	}

	public static String appendSql(final String[] columns, String sql) {

		final StringBuffer sb = new StringBuffer();
		final int l = columns.length;
		for (int i = 0; i < l; i++) {
			final String s = columns[i];
			if (i == l - 1) {
				sb.append(s);
			} else {
				sb.append(s + ",");
			}
		}
		sql = sql.replace("*", sb.toString());
		return sql;
	}

	static public String formatSize(long size) {
		String suffix = null;

		if (size >= 1024) {
			suffix = "KiB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MiB";
				size /= 1024;
			}
		}

		final StringBuilder resultBuffer = new StringBuilder(
				Long.toString(size));
		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null) {
			resultBuffer.append(suffix);
		}
		return resultBuffer.toString();
	}

	/**
	 * 判断字符串是否是空字符串
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmptyString(final String s) {
		return TextUtils.isEmpty(s) || s.trim().length() == 0;
	}

	public static String replaceNull(final String in) {
		return in == null ? "" : in;
	}

	public static int getPackageVersion(final Context context) {
		try {
			final PackageInfo pinfo = context
					.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			return pinfo.versionCode;
		} catch (final NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}

	}


	/**
	 * 合并两个List，不包含重复项
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static List mergeList(final List a, final List b) {
		for (final Object o : a) {
			if (!b.contains(o)) {
				b.add(o);
			}
		}
		return b;
	}

	/**
	 * 获得程序版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(final Context context) {
		final PackageInfo pinfo;
		try {
			pinfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			return pinfo.versionName;
		} catch (final NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Prior to Android 2.2 (Froyo), {@link HttpURLConnection} had some
	 * frustrating bugs. In particular, calling close() on a readable
	 * InputStream could poison the connection pool. Work around this by
	 * disabling connection pooling.
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (!UIUtils.hasFroyo()) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/** */
	/**
	 * 从字节数组获取对象
	 * 
	 * @Author Sean.guo
	 * @EditTime 2007-8-13 上午11:46:34
	 */
	public static Object getObjectFromBytes(final byte[] objBytes) {
		try {
			if (objBytes == null || objBytes.length == 0) {
				return null;
			}
			final ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
			ObjectInputStream oi;

			oi = new ObjectInputStream(bi);

			return oi.readObject();
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * /** 从对象获取一个字节数组
	 * 
	 * @Author Sean.guo
	 * @EditTime 2007-8-13 上午11:46:56
	 */
	public static byte[] getBytesFromObject(final Serializable obj) {
		try {
			if (obj == null) {
				return null;
			}
			final ByteArrayOutputStream bo = new ByteArrayOutputStream();
			final ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			return bo.toByteArray();
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackground(final Context context) {

		final ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (final RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

}
