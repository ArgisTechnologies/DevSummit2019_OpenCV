// TODO: Header goes here

package com.logicpd.papapill.device.tinyg;

import android.util.Log;

import com.logicpd.papapill.device.enums.MachineState;

/**
 * This class keeps track of the state of the motor control board and is used in conjunction
 * with CommandManager to coordinate motor moves.
 */
public final class MachineManager {

    private static MachineState machineState;

    private MachineManager() { }

    private static class LazyHolder {
        private static final MachineManager INSTANCE = new MachineManager();
    }

    public static MachineManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    // Machine state getters and setters
    public void setMachineState(int state) {
        machineState = MachineState.valueOf(state);
        Log.d("MachineState", "Machine state set to: " + machineState);
    }

    public MachineState getMachineState() {
        return this.machineState;
    }

    // Some quick helpers to quickly query if machine is a certain state.
    public boolean isMachineRunning() {
        return (machineState == MachineState.RUN);
    }

    public boolean isMachineHolding() {
        return (machineState == MachineState.HOLD);
    }
    public boolean isMachineHoming() {
        return (machineState == MachineState.HOMING);
    }
}
