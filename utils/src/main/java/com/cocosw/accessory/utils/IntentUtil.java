package com.cocosw.accessory.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * part from https://github.com/d-tarasov/android-intents
 */
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

    public static boolean doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    }
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Restart the Activity
     *
     * @param activity
     */
    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.recreate();
        } else {
            Intent intent = activity.getIntent();
            activity.overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.finish();

            activity.overridePendingTransition(0, 0);
            activity.startActivity(intent);
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
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Acitivity Not Found
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Launch a App by packageName&className
     *
     * @param context
     * @param packageName
     * @return
     */

    public static boolean launchApp(Context context, String packageName, String className) {
        if (TextUtils.isEmpty(className))
            return launchApp(context, packageName);
        try {
            ComponentName cn = new ComponentName(packageName, className);
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(cn);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Open app page at Google Play. If Play Store application isn't available on the device
     * then web browser will be opened
     *
     * @param context Application context
     */
    public static Intent openPlayStore(Context context) {
        return openPlayStore(context, true);
    }

    /**
     * Open app page at Google Play
     *
     * @param context       Application context
     * @param openInBrowser Should we try to open application page in web browser
     *                      if Play Store app not found on device
     */
    public static Intent openPlayStore(Context context, boolean openInBrowser) {
        String appPackageName = context.getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        if (isIntentAvailable(context, marketIntent)) {
            return marketIntent;
        }
        if (openInBrowser) {
            return openLink("https://play.google.com/store/apps/details?id=" + appPackageName);
        }
        return marketIntent;
    }

    /**
     * Send email message
     *
     * @param to      Receiver email
     * @param subject Message subject
     * @param text    Message body
     * @see #sendEmail(String[], String, String)
     */
    public static Intent sendEmail(String to, String subject, String text) {
        return sendEmail(new String[]{to}, subject, text);
    }

    /**
     * @see #sendEmail(String, String, String)
     */
    public static Intent sendEmail(String[] to, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }

    /**
     * Share text via thirdparty app like twitter, facebook, email, sms etc.
     *
     * @param subject Optional subject of the message
     * @param text    Text to share
     */
    public static Intent shareText(String subject, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        return intent;
    }

    /**
     * Send SMS message using built-in app
     *
     * @param to      Receiver phone number
     * @param message Text to send
     */
    public static Intent sendSms(String to, String message) {
        Uri smsUri = Uri.parse("tel:" + to);
        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        intent.putExtra("address", to);
        intent.putExtra("sms_body", message);
        intent.setType("vnd.android-dir/mms-sms");
        return intent;
    }

    /**
     * Opens the Street View application to the given location.
     * The URI scheme is based on the syntax used for Street View panorama information in Google Maps URLs.
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     * @param yaw       Panorama center-of-view in degrees clockwise from North.
     *                  <p/>
     *                  Note: The two commas after the yaw parameter are required.
     *                  They are present for backwards-compatibility reasons.
     * @param pitch     Panorama center-of-view in degrees from -90 (look straight up) to 90 (look straight down.)
     * @param zoom      Panorama zoom. 1.0 = normal zoom, 2.0 = zoomed in 2x, 3.0 = zoomed in 4x, and so on.
     *                  A zoom of 1.0 is 90 degree horizontal FOV for a nominal landscape mode 4 x 3 aspect ratio display Android
     *                  phones in portrait mode will adjust the zoom so that the vertical FOV is approximately the same as the
     *                  landscape vertical FOV. This means that the horizontal FOV of an Android phone in portrait mode is much
     *                  narrower than in landscape mode. This is done to minimize the fisheye lens effect that would be present
     *                  if a 90 degree horizontal FOV was used in portrait mode.
     * @param mapZoom   The map zoom of the map location associated with this panorama.
     *                  This value is passed on to the Maps activity when the Street View "Go to Maps" menu item is chosen.
     *                  It corresponds to the zoomLevel parameter in {@link #showLocation(float, float, Integer)}
     */
    public static Intent showStreetView(float latitude,
                                        float longitude,
                                        Float yaw,
                                        Integer pitch,
                                        Float zoom,
                                        Integer mapZoom) {
        StringBuilder builder = new StringBuilder("google.streetview:cbll=").append(latitude).append(",").append(longitude);
        if (yaw != null || pitch != null || zoom != null) {
            String cbpParam = String.format("%s,,%s,%s", yaw == null ? "" : yaw, pitch == null ? "" : pitch, zoom == null ? "" : zoom);
            builder.append("&cbp=1,").append(cbpParam);
        }
        if (mapZoom != null) {
            builder.append("&mz=").append(mapZoom);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(builder.toString()));
        return intent;
    }

    /**
     * Opens the Maps application to the given location.
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     * @param zoomLevel A zoom level of 1 shows the whole Earth, centered at the given lat,lng.
     *                  A zoom level of 2 shows a quarter of the Earth, and so on. The highest zoom level is 23.
     *                  A larger zoom level will be clamped to 23.
     * @see #findLocation(String)
     */
    public static Intent showLocation(float latitude, float longitude, Integer zoomLevel) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:%s,%s", latitude, longitude);
        if (zoomLevel != null) {
            data = String.format("%s?z=%s", data, zoomLevel);
        }
        intent.setData(Uri.parse(data));
        return intent;
    }

    /**
     * Opens the Maps application to the given query.
     *
     * @param query Query string
     * @see #showLocation(float, float, Integer)
     */
    public static Intent findLocation(String query) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:0,0?q=%s", query);
        intent.setData(Uri.parse(data));
        return intent;
    }

    /**
     * Open system settings location services screen for turning on/off GPS
     */
    public static Intent showLocationServices() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }

    /**
     * Open a browser window to the URL specified.
     *
     * @param url Target url
     */
    public static Intent openLink(String url) {
        // if protocol isn't defined use http by default
        if (!TextUtils.isEmpty(url) && !url.contains("://")) {
            url = "http://" + url;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }

    /**
     * @see #openLink(String)
     */
    public static Intent openLink(URL url) {
        return openLink(url.toString());
    }

    /**
     * Open a video file in appropriate app
     *
     * @param file File to open
     */
    public static Intent openVideo(File file) {
        return openVideo(Uri.fromFile(file));
    }

    /**
     * @see #openVideo(java.io.File)
     */
    public static Intent openVideo(String file) {
        return openVideo(new File(file));
    }

    /**
     * @see #openVideo(java.io.File)
     */
    public static Intent openVideo(Uri uri) {
        return openMedia(uri, "video/*");
    }

    /**
     * Open an audio file in appropriate app
     *
     * @param file File to open
     */
    public static Intent openAudio(File file) {
        return openAudio(Uri.fromFile(file));
    }

    /**
     * @see #openAudio(java.io.File)
     */
    public static Intent openAudio(String file) {
        return openAudio(new File(file));
    }

    /**
     * @see #openAudio(java.io.File)
     */
    public static Intent openAudio(Uri uri) {
        return openMedia(uri, "audio/*");
    }

    /**
     * Open an image file in appropriate app
     *
     * @param file File to open
     */
    public static Intent openImage(String file) {
        return openImage(new File(file));
    }

    /**
     * @see #openImage(String)
     */
    public static Intent openImage(File file) {
        return openImage(Uri.fromFile(file));
    }

    /**
     * @see #openImage(String)
     */
    public static Intent openImage(Uri uri) {
        return openMedia(uri, "image/*");
    }

    /**
     * Open a text file in appropriate app
     *
     * @param file File to open
     */
    public static Intent openText(String file) {
        return openText(new File(file));
    }

    /**
     * @see #openText(String)
     */
    public static Intent openText(File file) {
        return openText(Uri.fromFile(file));
    }

    /**
     * @see #openText(String)
     */
    public static Intent openText(Uri uri) {
        return openMedia(uri, "text/plain");
    }

    /**
     * Pick file from sdcard with file manager. Chosen file can be obtained from Intent in onActivityResult.
     * See code below for example:
     * <p/>
     * <pre><code>
     *     @Override
     *     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *         Uri file = data.getData();
     *     }
     * </code></pre>
     */
    public static Intent pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        return intent;
    }

    /**
     * Calls the entered phone number. Valid telephone numbers as defined in the IETF RFC 3966 are accepted.
     * Valid examples include the following:
     * tel:2125551212
     * tel: (212) 555 1212
     * <p/>
     * Note: This requires your application to request the following permission in your manifest:
     * <code>&lt;uses-permission android:name="android.permission.CALL_PHONE"/&gt;</code>
     *
     * @param phoneNumber Phone number
     */
    public static Intent callPhone(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * Pick contact from phone book
     */
    public static Intent pickContact() {
        return pickContact(null);
    }

    /**
     * Pick contact from phone book
     *
     * @param scope You can restrict selection by passing required content type. Examples:
     *              <p/>
     *              <code><pre>
     *                                                                  // Select only from users with emails
     *                                                                  IntentUtils.pickContact(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
     *              <p/>
     *                                                                  // Select only from users with phone numbers on pre Eclair devices
     *                                                                  IntentUtils.pickContact(Contacts.Phones.CONTENT_TYPE);
     *              <p/>
     *                                                                  // Select only from users with phone numbers on devices with Eclair and higher
     *                                                                  IntentUtils.pickContact(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
     *                                                                  </pre></code>
     */
    public static Intent pickContact(String scope) {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR) {
            intent = new Intent(Intent.ACTION_PICK, Contacts.People.CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://com.android.contacts/contacts"));
        }

        if (!TextUtils.isEmpty(scope)) {
            intent.setType(scope);
        }
        return intent;
    }

    /**
     * Pick image from gallery
     */
    public static Intent pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }

    /**
     * Dials (but does not actually initiate the call) the number given.
     * Telephone number normalization described for {@link #callPhone(String)} applies to dial as well.
     *
     * @param phoneNumber Phone number
     */
    public static Intent dialPhone(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * Check that cropping application is available
     *
     * @param context Application context
     * @return true if cropping app is available
     * @see #cropImage(android.content.Context, java.io.File, int, int, int, int, boolean)
     */
    public static boolean isCropAvailable(Context context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        return IntentUtil.isIntentAvailable(context, intent);
    }

    /**
     * Crop image. Before using, cropImage requires especial check that differs from
     * {@link #isIntentAvailable(android.content.Context, android.content.Intent)}
     * see {@link #isCropAvailable(android.content.Context)} instead
     *
     * @param context Application context
     * @param image   Image that will be used for cropping. This image is not changed during the cropImage
     * @param outputX Output image width
     * @param outputY Output image height
     * @param aspectX Crop frame aspect X
     * @param aspectY Crop frame aspect Y
     * @param scale   Scale or not cropped image if output image and cropImage frame sizes differs
     * @return Intent with <code>data</code>-extra in <code>onActivityResult</code> which contains result as a
     * {@link android.graphics.Bitmap}. See demo app for details
     */
    public static Intent cropImage(Context context, File image, int outputX, int outputY, int aspectX, int aspectY, boolean scale) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        ResolveInfo res = list.get(0);

        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("scale", scale);
        intent.putExtra("return-data", true);
        intent.setData(Uri.fromFile(image));

        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        return intent;
    }

    /**
     * Call standard camera application for capturing an image
     *
     * @param file Full path to captured file
     */
    public static Intent photoCapture(String file) {
        Uri uri = Uri.fromFile(new File(file));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    /**
     * Check that in the system exists application which can handle this intent
     *
     * @param context Application context
     * @param intent  Checked intent
     * @return true if intent consumer exists, false otherwise
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    private static Intent openMedia(Uri uri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        return intent;
    }

}
