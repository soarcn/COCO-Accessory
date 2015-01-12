package com.cocosw.accessory.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 2015/1/12.
 */
public class PowerHelper {


    private GeneralPowerStatus generalinfo;

    public void register(@NonNull Context context, @NonNull PowerInfo info) {
        this.powerinfo = info;
        context.registerReceiver(batterReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void register(@NonNull Context context, @NonNull GeneralPowerStatus info) {
        this.generalinfo = info;
        context.registerReceiver(batterReceiver, getIntentFilter());
    }

    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        return intentFilter;
    }

    public void unregister(@NonNull Context context) {
        context.unregisterReceiver(batterReceiver);
    }

    public static interface PowerInfo {
        void powerLevel(float power);

        void status(int status);

        void plugged(int status);
    }

    public static interface GeneralPowerStatus {
        void batteryLow();

        void batteryOK();
    }

    private PowerInfo powerinfo;


    private BroadcastReceiver batterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (powerinfo != null) {
                powerinfo.plugged(getPlugged(intent));
                powerinfo.status(getStatus(intent));
                powerinfo.powerLevel(getLevel(intent));
            }
            if (generalinfo != null) {
                boolean batteryLow = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);
                boolean batteryOK = intent.getAction().equals(Intent.ACTION_BATTERY_OKAY);
                if (batteryLow) generalinfo.batteryLow();
                if (batteryOK) generalinfo.batteryOK();
            }
        }

        public int getPlugged(Intent intent) {
            return intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }

        public int getStatus(Intent intent) {
            return intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        }

        public float getLevel(Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level == -1 || scale == -1) {
                return 0f;
            }

            return ((float) level / (float) scale) * 100.0f;
        }
    };
}
