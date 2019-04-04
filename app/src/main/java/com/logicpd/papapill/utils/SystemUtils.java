package com.logicpd.papapill.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Some useful system utilities
 *
 * @author alankilloren
 */
public class SystemUtils {

    /**
     * Returns true or false whether device is plugged in or running on battery
     *
     * @param context Context
     * @return True or False
     */
    public static boolean isPowerConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = -1;
        if (intent != null) {
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }
        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                /*|| plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS*/;
    }

    /**
     * Returns true or false whether the device is currently charging the battery
     *
     * @param context Context
     * @return true or false
     */
    public static boolean isCharging(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = -1;
        if (intent != null) {
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        }
        return status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    /**
     * Returns the battery percentage
     *
     * @param context Context
     * @return Battery percentage (float)
     */
    public static int getBatteryPercentage(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float batteryPct = 0;
        if (intent != null) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPct = level / (float) scale;
        }
        return (int) (batteryPct * 100);
    }
}
