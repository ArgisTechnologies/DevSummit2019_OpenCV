package com.logicpd.papapill.device.models;

import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.device.tinyg.MnemonicManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This object contains motor position information from the TinyG.
 * (Both the reported motor position of each active axis, as well as the
 * position reported by the absolute position sensor).
 */
public class PositionData {

    public double x;
    public double z;
    public double a;
    public int abs;

    private static final int ABS_MAX = 4095;

    public PositionData() { }

    /**
     * Method to parse the JSON string containing the motor and absolute position
     * sensor information we receive from the TinyG.
     * @param js
     * @return
     * @throws JSONException
     */
    public boolean parseResponse(JSONObject js) throws JSONException {
        if (js.has(MnemonicManager.MNEMONIC_JSON_RESPONSE)) {
            JSONObject r = js.getJSONObject(MnemonicManager.MNEMONIC_JSON_RESPONSE);
            if (r.has(MnemonicManager.MNEMONIC_GROUP_POS)) {
                JSONObject pos = r.getJSONObject(MnemonicManager.MNEMONIC_GROUP_POS);
                if (pos.has("x")) { x = pos.getDouble("x"); }
                if (pos.has("z")) { z = pos.getDouble("z"); }
                if (pos.has("a")) { a = pos.getDouble("a"); }
                if (pos.has("abs")) { abs = pos.getInt("abs"); }
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the raw value of the absolute position sensor data into degrees
     * with or without an offset.
     * @param useOffset
     * @return
     */
    public double getAbsoluteDegrees(boolean useOffset) {
        double degrees = ((double)abs/ABS_MAX)*360;
        if (useOffset) {
            return (degrees - AppConfig.getInstance().getAbsPositionOffset());
        }
        return degrees;
    }

    /**
     * Method to check if the Z axis is homed. If the position of the Z-axis
     * is 0, then it is homed.
     * @return
     */
    public boolean isAxisHomedZ() {
        return (0 == (int)Math.floor(this.z));
    }
}
