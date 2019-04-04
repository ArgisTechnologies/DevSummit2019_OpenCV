package com.logicpd.papapill.device.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum contains the set of all possible states that the TinyG considers itself
 * to be in.
 */
public enum MachineState {
    INITIALIZING(0),
    READY(1),
    ALARM(2),
    STOP(3),
    END(4),
    RUN(5),
    HOLD(6),
    PROBE(7),
    CYCLE(8),
    HOMING(9),
    JOGGING(10),
    SHUTDOWN(11);

    private int value;
    private static Map map = new HashMap<>();

    private MachineState(int value) {
        this.value = value;
    }

    static {
        for (MachineState pageType : MachineState.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static MachineState valueOf(int machineState) {
        return (MachineState) map.get(machineState);
    }

    public int getValue() {
        return value;
    }
}
