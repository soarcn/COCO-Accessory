package com.cocosw.accessory.utils;

//https://github.com/hazemhagrass/android-profiler/blob/c42a30dfdfcc59670c5c4e1fca23ef4560d34e10/example-project/src/com/badrit/profiler/Profiler.java

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Hashtable;

/**
 * Profiler which get memory/cpu/battery runtime info
 */
public class Profiler {
    private Context context;

    public Profiler(Context ctx) {
        this.context = ctx;
    }

    public Hashtable<String, Integer> getCpuStatistics() {
        Hashtable<String, Integer> statistics = new Hashtable<String, Integer>();

        Cpu cpu = new Cpu();

        //[user, system, iqw, irq]
        int[] cpuUsageAsInt = cpu.getUsage();

        statistics.put("user", cpuUsageAsInt[0]);
        statistics.put("system", cpuUsageAsInt[1]);
        statistics.put("iqw", cpuUsageAsInt[2]);
        statistics.put("irq", cpuUsageAsInt[3]);

        return statistics;
    }

    public Hashtable<String, Long> getMemoryStatistics() {
        Hashtable<String, Long> statistics = new Hashtable<String, Long>();

        Memory mem = new Memory();

        //[usedSize, freeSize, totalSize]
        long[] memoryInfo = mem.getUsage();

        statistics.put("used", memoryInfo[0]);
        statistics.put("free", memoryInfo[1]);
        statistics.put("total", memoryInfo[2]);

        return statistics;
    }

    public Hashtable<String, String> getBatteryStatus() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.getApplicationContext().registerReceiver(null, ifilter);

        Hashtable<String, String> data = new Hashtable<>();

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
                -1);

        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

        data.put("is_charging", isCharging + "");

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(
                BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        data.put("usb_charge", usbCharge + "");
        data.put("ac_charge", acCharge + "");

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
                -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,
                -1);

        data.put("level", level + "");
        data.put("scale", scale + "");

        return data;
    }


    private class Cpu {


        protected Cpu() {
        }


        private boolean isGeneralStatisticsInfo(String info) {
            if ((info.indexOf("user") != -1 && info.indexOf("system") != -1)
                    || (info.indexOf("usr") != -1 && info.indexOf("sys") != -1))
                return true;
            return false;
        }

        //[user, system, iqw, irq]
        private int[] getGeneralStatisticsInfo(String info) {
            info = info.replaceAll(",", "");
            info = info.replaceAll("cpu:", "");
            info = info.replaceAll("user", "");
            info = info.replaceAll("usr", "");
            info = info.replaceAll("system", "");
            info = info.replaceAll("sys", "");
            info = info.replaceAll("iow", "");
            info = info.replaceAll("irq", "");
            info = info.replaceAll("nic", "");
            info = info.replaceAll("idle", "");
            info = info.replaceAll("io", "");
            info = info.replaceAll("s", "");

            info = info.replaceAll("%", "");
            for (int i = 0; i < 10; i++) {
                info = info.replaceAll("  ", " ");
            }
            info = info.trim();
            String[] myString = info.split(" ");
            int[] cpuUsageAsInt = new int[myString.length];
            for (int i = 0; i < myString.length; i++) {
                myString[i] = myString[i].trim();
                cpuUsageAsInt[i] = (int) Double.parseDouble(myString[i]);
            }
            return cpuUsageAsInt;
        }

        protected int[] getUsage() {
            int[] cpuUsageAsInt = null;
            try {
                // -m 10, how many entries you want, -d 1, delay by how much, -n 1,
                // number of iterations
                Process p = Runtime.getRuntime().exec("top -d 1 -n 1");


                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));

                String line = reader.readLine();
                while (line != null) {
                    line = line.toLowerCase();
                    if (isGeneralStatisticsInfo(line)) {
                        cpuUsageAsInt = getGeneralStatisticsInfo(line);
                        break;
                    }

                    line = reader.readLine();
                }


                p.waitFor();
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
            return cpuUsageAsInt;
        }
    }


    private class Memory {
        protected Memory() {

        }

        protected long[] getUsage() {
            //[usedSize, freeSize, totalSize]
            long[] memoryInfo = new long[3];
            try {
                //system total mempry
                RandomAccessFile reader = null;
                String line = null;
                reader = new RandomAccessFile("/proc/meminfo", "r");
                line = reader.readLine();
                memoryInfo[2] = Long.parseLong(line.replaceAll("MemTotal:", "").replaceAll("kB", "").trim()) / 1024;
                //memory free
                line = reader.readLine();
                memoryInfo[1] = Long.parseLong(line.replaceAll("MemFree:", "").replaceAll("kB", "").trim());
                //Buffers
                line = reader.readLine();
                memoryInfo[1] = memoryInfo[1] + Long.parseLong(line.replaceAll("Buffers:", "").replaceAll("kB", "").trim());
                //cached
                line = reader.readLine();
                memoryInfo[1] = memoryInfo[1] + Long.parseLong(line.replaceAll("Cached:", "").replaceAll("kB", "").trim());
                memoryInfo[1] /= 1024;

                memoryInfo[0] = memoryInfo[2] - memoryInfo[1];

            } catch (Exception e) {
                e.printStackTrace();
            }
            return memoryInfo;
        }
    }

}
