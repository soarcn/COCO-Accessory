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

	public static boolean debugFlag = BuildConfig.DEBUG;

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
	 * 打印log
	 * 
	 * @param obj
	 */
	public static void dout(final Object obj) {
		if (Utils.debugFlag) {
			// Views.showToast(obj.toString());
			if (obj != null) {
				Log.d("[dout]", "obj>>>>>>>>>>>>>" + obj.getClass().getName()
						+ ">>" + obj.toString());
			} else {
				Log.d("[dout]", "obj>>>>>>>>>>>>>NULL");
			}
		}
	}

	public static void d(final Object... obj) {
		for (final Object object : obj) {
			Utils.dout(object);
		}
	}

	public static void dout(final int obj) {
		if (Utils.debugFlag) {
			Log.d("[dout]", "int>>>>>>>>>>>>>" + obj);
		}
	}

	public static void dout(final String str) {
		if (Utils.debugFlag) {
			// Views.showToast(str);
			Log.d("[dout]", "str>>>>>>>>>>>>>" + str);
		}
	}

	public static void dout(final Cursor str) {
		if (Utils.debugFlag) {
			Log.d("[dout]", Utils.cur2Str(str));
		}
	}

	public static void doutCursor(final Cursor cursor) {
		final StringBuilder retval = new StringBuilder();

		retval.append("|");
		final int numcolumns = cursor.getColumnCount();
		for (int column = 0; column < numcolumns; column++) {
			final String columnName = cursor.getColumnName(column);
			retval.append(String.format("%-20s |",
					columnName.substring(0, Math.min(20, columnName.length()))));
		}
		retval.append("\n|");
		for (int column = 0; column < numcolumns; column++) {
			for (int i = 0; i < 21; i++) {
				retval.append("-");
			}
			retval.append("+");
		}
		retval.append("\n|");

		while (cursor.moveToNext()) {
			for (int column = 0; column < numcolumns; column++) {
				final String columnValue = cursor.getString(column);
				retval.append(columnValue);
			}
			retval.append("\n");

		}
		if (Utils.debugFlag) {
			Log.d("[dout]", retval.toString());
		}
	}

	public static void dout(final String str, final String str2) {
		if (Utils.debugFlag) {
			// Views.showToast(str + " " + str2);
			Log.d("[dout]", "str>>>>>>>>>>>>>" + str + " " + str2);
		}
	}

	/**
	 * 把游标内容显示出来
	 * 
	 * @param cur
	 * @return
	 */
	public static String cur2Str(final Cursor cur) {
		final StringBuffer buf = new StringBuffer();
		final String[] col = cur.getColumnNames();
		for (int i = 0; i < col.length; i++) {
			final String str = col[i];
			try {
				buf.append("; [" + str + "]:").append(cur.getString(i));
			} catch (final Exception e) {

			}

		}
		return buf.toString();
	}

	public static void dout(final String[] str) {
		if (Utils.debugFlag) {
			for (int i = 0; i < str.length; i++) {
				Log.d("[dout]", "str[" + i + "]>>>>>>>>>>>>>" + str[i]);
			}
		}
	}

	public static void dout(final Throwable t) {
		Utils.dout(Log.getStackTraceString(t));
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
	 * 从消息中获取全部提到的人，将它们按先后顺序放入一个列表
	 * 
	 * @param msg
	 *            消息文本
	 * @return 消息中@的人的列表，按顺序存放
	 */
	public static List<String> getMentions(final String msg) {
		final ArrayList<String> mentionList = new ArrayList<String>();

		final Pattern p = Pattern.compile("@(.*?)\\s");
		final int MAX_NAME_LENGTH = 12; // 简化判断，无论中英文最长12个字

		final Matcher m = p.matcher(msg);
		while (m.find()) {
			final String mention = m.group(1);

			// 过长的名字就忽略（不是合法名字） +1是为了补上“@”所占的长度
			if (mention.length() <= MAX_NAME_LENGTH + 1) {
				// 避免重复名字
				if (!mentionList.contains(mention)) {
					mentionList.add(m.group(1));
				}
			}
		}
		return mentionList;
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
	 * 判断是否联网,线程安全
	 * 
	 * @param context
	 * @return
	 */
	public static boolean haveInternet(final Context context) {
		try {
			final ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			final NetworkInfo info = manger.getActiveNetworkInfo();
			return info != null && info.isConnected();
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * 在桌面创建一个快捷方式 <uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
	 * 
	 * @param act
	 * @param iconResId
	 * @param appnameResId
	 */
	public static void createShortCut(final Activity act, final Class clz,
			final int iconResId, final int appnameResId) {

		// com.android.launcher.permission.INSTALL_SHORTCUT

		final Intent shortcutintent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				act.getString(appnameResId));
		// 快捷图片
		final Parcelable icon = Intent.ShortcutIconResource.fromContext(
				act.getApplicationContext(), iconResId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				new Intent(act.getApplicationContext(), clz));
		// 发送广播
		act.sendBroadcast(shortcutintent);
	}

	public static Intent createTextShareIntent(final String title,
			final String content) {
		final Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		shareIntent.putExtra(Intent.EXTRA_TEXT, content);// 文本内容
		return shareIntent;

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
