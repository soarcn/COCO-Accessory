package com.cocosw.accessory.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 14-2-26.
 */
public class PackageUtils {

    public static String getAppName(Context context) {
        return getAppName(context, null);
    }

    public static String getAppName(Context context, String packageName) {
        String applicationName;

        if (packageName == null) {
            packageName = context.getPackageName();
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            applicationName = context.getString(packageInfo.applicationInfo.labelRes);
        } catch (Exception e) {
            Log.w("Failed to get version number.", e);
            applicationName = "";
        }

        return applicationName;
    }

    public static String getAppVersionNumber(Context context) {
        return getAppVersionNumber(context, null);
    }

    public static String getAppVersionNumber(Context context, String packageName) {
        String versionName;

        if (packageName == null) {
            packageName = context.getPackageName();
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            Log.w("Failed to get version number.", e);
            versionName = "";
        }

        return versionName;
    }

    public static String getAppVersionCode(Context context) {
        return getAppVersionCode(context, null);
    }

    public static String getAppVersionCode(Context context, String packageName) {
        String versionCode;

        if (packageName == null) {
            packageName = context.getPackageName();
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = Integer.toString(packageInfo.versionCode);
        } catch (Exception e) {
            Log.w("Failed to get version code.", e);
            versionCode = "";
        }

        return versionCode;
    }

    public static int getSdkVersion() {
        try {
            return Build.VERSION.class.getField("SDK_INT").getInt(null);
        } catch (Exception e) {
            return 3;
        }
    }

    public static boolean isEmulator() {
        return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
    }

}
