package com.cocosw.accessory.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;

import java.util.List;

public class IntentUtil {

    /**
     * 不选择，直接用系统默认浏览器打开url，在某些机型上有问题
     *
     * @param url
     * @param context
     */
    public static void openUrlByDefault(final String url, final Context context) {
        final Uri uri = Uri.parse(url);
        final Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        intent.setClassName("com.android.browser",
                "com.android.browser.BrowserActivity");
        try {
            // 如果没有的话，至少不崩溃
            context.startActivity(intent);
        } catch (final Exception e) {
            IntentUtil.openUrl(url, context);
        }

    }

    /**
     * 拨号
     *
     * @param num
     * @param context
     */
    public static void dail(final String num, final Context context) {
        final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + num));
        context.startActivity(intent);
    }

    /**
     * 打开网页
     *
     * @param url
     * @param context
     */
    public static void openUrl(final String url, final Context context) {
        final Uri uri = Uri.parse(url);
        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 打电话，需要 <uses-permission id="android.permission.CALL_PHONE" />
     *
     * @param num
     * @param context
     */
    public static void call(final String num, final Context context) {
        final Uri uri = Uri.parse("tel:" + num);
        final Intent it = new Intent(Intent.ACTION_CALL, uri);
        context.startActivity(it);
    }

    /**
     * 判断某个intent是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(final Context context,
                                            final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    /**
     * 短信
     *
     * @param num
     * @param context
     */
    public static void sms(final String num, final String text,
                           final Context context) {
        final Uri uri = Uri.parse("smsto:" + num);
        final Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", text);
        context.startActivity(it);
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
     * Launch a App by packageName
     *
     * @param context
     * @param packageName
     * @return
     */

    public static boolean launchApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

}
