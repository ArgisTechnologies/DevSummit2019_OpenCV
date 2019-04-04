package com.logicpd.papapill.device.gpio;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.logicpd.papapill.device.tinyg.BoardDefaults;

import java.io.IOException;

/**
 * This simple class utilizes the Android Things Peripheral Manager to control
 * the GPIO tied to the light ring on the system.
 */
public class LightringManager {

    private Gpio mGpio;

    private LightringManager() {
        this.initialize();
    }

    private static class LazyHolder {
        private static final LightringManager INSTANCE = new LightringManager();
    }

    public static LightringManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Open connection to GPIO peripheral manager.
     */
    private void initialize() {
        try {
            mGpio = PeripheralManager.getInstance().openGpio(BoardDefaults.GPIO_LIGHT_RING);
            Log.i("GPIO", "Opened GPIO");
        } catch (IOException | RuntimeException e) {
            Log.e("GPIO","Failed to Open GPIO");
        }
    }

    /**
     * Close connection to GPIO peripheral manager.
     */
    private void close() {
        if (mGpio != null) {
            try {
                mGpio.close();
                mGpio = null;
            } catch (IOException e) {
                Log.w("GPIO", "Unable to close GPIO", e);
            }
        }
    }

    /**
     * Configure gpio pin as input and read its value.
     * @return
     */
    public boolean configureInput() {
        boolean retval = false;
        try {
            // Initialize the pin as an input
            mGpio.setDirection(Gpio.DIRECTION_IN);
            // High voltage is considered active
            mGpio.setActiveType(Gpio.ACTIVE_HIGH);
            retval = mGpio.getValue();
        } catch (IOException e) {
            Log.w("GPIO", "Unable to configure input", e);
        }
        return retval;
    }

    /**
     * Configure gpio pin as output and drive a value.
     * @param val
     */
    public void configureOutput(boolean val) {
        try {
            mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mGpio.setActiveType(Gpio.ACTIVE_HIGH);
            mGpio.setValue(val);
        } catch (IOException e) {
            Log.w("GPIO", "Unable to configure output", e);
        }
    }
}
