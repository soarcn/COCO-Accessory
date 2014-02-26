package com.cocosw.accessory.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.UUID;

// <uses-permission android:name="android.permission.READ_PHONE_STATE"/>

public class UID {


    // 不带设备序列号的UID
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        //Log.d("debug","----->UUID"+uuid);
        return uniqueId;
    }

    // 带设备序列号的UID
    public static String getUniqUUID(Context ctx) {
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    // 获得手机基本信息
    public static HashMap<String, String> getPhoneState(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        HashMap<String, String> out = new HashMap<String, String>();
        out.put("IMEI", tm.getDeviceId());
        out.put("DeviceSoftwareVersion", tm.getDeviceSoftwareVersion());
        out.put("Line1Number", tm.getLine1Number());
        out.put("NetworkCountryIso", tm.getNetworkCountryIso());
        out.put("NetworkOperator", tm.getNetworkOperator());
        out.put("NetworkOperatorName", tm.getNetworkOperatorName());
        out.put("NetworkType", String.valueOf(tm.getNetworkType()));
        out.put("PhoneType", String.valueOf(tm.getPhoneType()));
        out.put("SimCountryIso", tm.getSimCountryIso());
        out.put("SimOperator", tm.getSimOperator());
        out.put("SimOperatorName", tm.getSimOperatorName());
        out.put("SimSerialNumber", tm.getSimSerialNumber());
        out.put("SimState", String.valueOf(tm.getSimState()));
        out.put("IMSI", tm.getSubscriberId());
        return out;
    }

    // 获得imei标示
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    private static String sINSTID = null;
    private static final String INSTALLATION = "INSTALLATION";

    // 安装id,每次安装会有一个唯一标示
    public synchronized static String installID(Context context) {
        if (sINSTID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation, getUUID());
                sINSTID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sINSTID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation, String str) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);

        out.write(str.getBytes());
        out.close();
    }

    protected static String uuid;
    private static final String DEVICE = "DEVICE";

    //设备唯一ID,先取androidid,然后imei,然后序列号(>=2.3),最后取uuid
    public synchronized static String deviceID(Context context) {
        if (uuid == null) {
            File installation = new File(context.getFilesDir(), DEVICE);
            try {
                if (!installation.exists()) {
                    String deviceId;
                    String androidId = getAndroidID(context);
                    String imei = getIMEI(context);
                    String serial = android.os.Build.SERIAL;

                    deviceId = androidId;
                    if (deviceId == null || "9774d56d682e549c".equals(deviceId)) {
                        deviceId = imei;
                        if (deviceId == null || "".equals(deviceId)) {
                            deviceId = serial;
                            if (deviceId == null || deviceId.equals("") || deviceId.equals("unknown")) {
                                deviceId = getUUID();
                            }
                        }
                    }
                    writeInstallationFile(installation, deviceId);
                }
                uuid = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return uuid;
    }


    //无权限deviceid,不安全
    public synchronized static String getPseudoID() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    public synchronized static String getAndroidID(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //wifi mac地址,开启后才有
    public synchronized static String getWifiMac(Context ctx) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return m_szWLANMAC;
    }


}
