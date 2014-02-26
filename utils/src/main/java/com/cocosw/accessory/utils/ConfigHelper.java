package com.cocosw.accessory.utils;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 14-2-26.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author by
 * @description 用于获取各种开关的状态，以及设置开关状态
 * @time 2013.07.31
 */
public class ConfigHelper {
    /**
     * 获取WIFI状态
     *
     * @param context
     * @return
     */
    public static boolean getWifiStatu(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 设置WIFI状态
     *
     * @param context
     * @param isopen
     */
    public static void setWifiStatu(Context context, boolean isopen) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isopen);
    }

    /**
     * 获取GPRS状态
     *
     * @param context
     * @return
     */
    public static boolean getGprsStatu(Context context) {
        boolean isopen = false;
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        Field iConMgrField = null;
        Object iConMgr = null;
        Class<?> iConMgrClass = null;
        Method getMobileDataEnabledMethod = null;
        try {
            conMgrClass = Class.forName(conMgr.getClass().getName());
            iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            iConMgr = iConMgrField.get(conMgr);
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            getMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("getMobileDataEnabled");
            getMobileDataEnabledMethod.setAccessible(true);
            isopen = (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isopen;
    }

    /**
     * 设置GPRS状态
     *
     * @param context
     * @param isopen
     */
    public static void setGprsStatu(Context context, boolean isopen) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        Field iConMgrField = null;
        Object iConMgr = null;
        Class<?> iConMgrClass = null;
        Method setMobileDataEnabledMethod = null;
        try {
            conMgrClass = Class.forName(conMgr.getClass().getName());
            iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            iConMgr = iConMgrField.get(conMgr);
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConMgr, isopen);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取GPS状态
     *
     * @param context
     * @return
     */
    public static boolean getGpsStatu(Context context) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    /**
     * 设置GPS状态
     *
     * @param caller
     */
    public static void setGpsStatu(Activity caller) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        caller.startActivityForResult(intent, 0);
    }

    /**
     * 获取蓝牙状态
     *
     * @return
     */
    public static boolean getBluetoothStatu() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 设置蓝牙状态
     *
     * @param isOpen
     */
    public static void setBluetoothStatu(boolean isOpen) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isOpen) {
            bluetoothAdapter.enable();
        } else {
            bluetoothAdapter.disable();
        }
    }

    /**
     * 获取飞行模式状态
     *
     * @return
     */
    public static boolean getFlightMode(Context context) {
        boolean isOpen = false;
        ContentResolver resolver = context.getContentResolver();
        if (Settings.System.getString(resolver, Settings.System.AIRPLANE_MODE_ON).equals("0")) {
            isOpen = false;
        } else {
            isOpen = true;
        }
        return isOpen;
    }

    /**
     * 设置飞行模式状态
     *
     * @param context
     * @param isOpen
     */
    public static void setFiightMode(Context context, boolean isOpen) {
        ContentResolver resolver = context.getContentResolver();
        if (isOpen) {
            Settings.System.putString(resolver, Settings.System.AIRPLANE_MODE_ON, "1");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            context.sendBroadcast(intent);
        } else {
            Settings.System.putString(resolver, Settings.System.AIRPLANE_MODE_ON, "0");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 获取屏幕旋转状态
     *
     * @return
     */
    public static boolean getScreenRotation(Context context) {
        boolean isOpen = false;
        try {
            int status = Settings.System.getInt(context.getContentResolver(), "accelerometer_rotation");
            if (status == 1) {
                isOpen = true;
            } else {
                isOpen = false;
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    /**
     * 设置屏幕旋转状态
     *
     * @param context
     * @param isOpen
     */
    public static void setScreenRotation(Context context, boolean isOpen) {
        if (isOpen) {
            Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", 1);
        } else {
            Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", 0);
        }
    }

    /**
     * 关机
     *
     * @param context
     * @param time
     */
    public static void Shutdown(Context context, long time) {

    }

    /**
     * 获得当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static int getScreenMode(Context context) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    public static int getScreenBrightness(Context context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void setScreenMode(int paramInt, Context context) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    public static void saveScreenBrightness(int paramInt, Context context) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值
     */
    public static void setScreenBrightness(int paramInt, Activity caller) {
        Window window = caller.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float f = paramInt / 255.0F;
        params.screenBrightness = f;
        window.setAttributes(params);
    }

    /**
     * 获取当前通话音量
     */
    public static int getCallSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        } catch (Exception e) {
        }
        return current;
    }

    /**
     * 获取通话最大音量
     */
    public static int getCallMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        } catch (Exception e) {
        }
        return max;
    }

    /**
     * 获取当前系统音量
     */
    public static int getSystemSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        } catch (Exception e) {
        }
        return current;
    }

    /**
     * 获取系统最大音量
     */
    public static int getSystemMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        } catch (Exception e) {
        }
        return max;
    }

    /**
     * 获取当前铃声音量
     */
    public static int getRingtoneSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_RING);
        } catch (Exception e) {
        }
        return current;
    }

    /**
     * 获取铃声最大音量
     */
    public static int getRingtoneMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_RING);
        } catch (Exception e) {
        }
        return max;
    }

    /**
     * 获取当前音乐音量
     */
    public static int getMusicSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
        }
        return current;
    }

    /**
     * 获取音乐最大音量
     */
    public static int getMusicMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
        }
        return max;
    }

    /**
     * 获取当前提示音音量
     */
    public static int getAlarmSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_ALARM);
        } catch (Exception e) {
        }
        return current;
    }

    /**
     * 获取提示音最大音量
     */
    public static int getAlarmMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        } catch (Exception e) {
        }
        return max;
    }

    /**
     * 设置音量
     *
     * @param systemsound   系统音量
     * @param callsound     通话音量
     * @param ringtonesound 铃声音量
     * @param musicsound    音乐音量
     * @param alarmsound    提示音量
     */
    public static void setAllSound(Context context, int systemsound, int callsound, int ringtonesound, int musicsound, int alarmsound) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        try {
            manager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemsound, 0);
            manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, callsound, 0);
            manager.setStreamVolume(AudioManager.STREAM_RING, ringtonesound, 0);
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, musicsound, 0);
            manager.setStreamVolume(AudioManager.STREAM_ALARM, alarmsound, 0);
        } catch (Exception e) {
        }
    }

    /**
     * 获取情景模式
     *
     * @return 0 静音模式 1 振动模式  2 正常模式
     */
    public static int getSceneMode(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getRingerMode();
    }

    /**
     * 设置情景模式
     *
     * @param mode 0 静音模式 1 振动模式  2 正常模式
     */
    public static void setSceneMode(Context context, int mode) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (mode) {
            case 0:
                manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case 1:
                manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 2:
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                break;
        }
    }
}
