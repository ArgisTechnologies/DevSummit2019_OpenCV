package com.logicpd.papapill.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.logicpd.papapill.misc.AppConstants;

/**
 * Utilities for handling preferences
 *
 * @author alankilloren
 */
public class PreferenceUtils {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PreferenceUtils(Context context) {
        prefs = context.getSharedPreferences(AppConstants.TAG, Context.MODE_PRIVATE);
    }

    public boolean isFirstTimeRun() {
        return prefs.getBoolean("isFirstTimeRun", true);
    }

    public void setFirstTimeRun(boolean b) {
        editor = prefs.edit();
        editor.putBoolean("isFirstTimeRun", b);
        editor.apply();
    }

    public boolean getFirstTimeRun() {
        return prefs.getBoolean("isFirstTimeRun", true);
    }

    public void setSSID(String ssid) {
        editor = prefs.edit();
        editor.putString("SSID", ssid);
        editor.apply();
    }

    public String getSSID() {
        return prefs.getString("SSID", null);
    }
}
