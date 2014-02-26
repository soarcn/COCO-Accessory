package com.cocosw.accessory.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {


    public static String getImagePathFromUri(final Context context,
                                             final Uri uri) {
        if (context == null || uri == null) {
            return null;
        }

        final String media_uri_start = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                .toString();

        if (uri.toString().startsWith(media_uri_start)) {

            final String[] proj = {MediaColumns.DATA};
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

    /**
     * Check if device has rooted or not
     *
     * @param mAppContext
     * @return
     */
    public static boolean isRooted(final Context mAppContext) {
        boolean mHasRootBeenChecked = false;
        boolean mIsDeviceRooted = false;
        Resources res = mAppContext.getResources();

        Log.v("isRooted", "isRooted called");

        // first try
        Log.v("isRooted", "Checking if device is rooted by checking if Superuser is available");

        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                Log.v("isRooted", "Device seems rooted");
                mHasRootBeenChecked = true;
                mIsDeviceRooted = true;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // second try
        Log.v("isRooted", "Checking if device is rooted by checking usual position of su");
        try {
            File file = new File("/system/xbin/su");
            if (file.exists()) {
                Log.v("isRooted", "Device seems rooted");
                mHasRootBeenChecked = true;
                mIsDeviceRooted = true;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // third try
        Log.v("isRooted", "Checking if device is rooted by checking if su is available");
        try {
            // get the existing environment
            ArrayList<String> envlist = new ArrayList<String>();
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                envlist.add(envName + "=" + env.get(envName));
            }
            String[] envp = (String[]) envlist.toArray(new String[0]);
            // execute which su
            Process proc = Runtime.getRuntime()
                    .exec(new String[]{"which", "su"}, envp);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            // if we receive location, we are on a rooted device
            // TODO: can break if the executable is on the device, but non working
            if (in.readLine() != null) {
                Log.v("isRooted", "Device seems rooted");
                mHasRootBeenChecked = true;
                mIsDeviceRooted = true;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHasRootBeenChecked = true;
        mIsDeviceRooted = false;
        return false;
    }

}
