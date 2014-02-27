package com.cocosw.accessory.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
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

    public static void printCallStatck() {
        final Throwable ex = new Throwable();
        // StackTraceElement[] stackElements = ex.getStackTrace();
        ex.printStackTrace();
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
        }

        mHasRootBeenChecked = true;
        mIsDeviceRooted = false;
        return false;
    }

}
