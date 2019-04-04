package com.logicpd.papapill.device.tinyg;

/**
 * This class contains helper functions intended to aid in constructing the actual string text of a
 * g-code command before sending it to the TinyG. This contains the entire list of possible commands
 * from TinyG wiki. Only a few commands are actually used in the system, however, so most of these
 * can be eventually trimmed.
 */
public class CommandBuilder {
    public static final String CMD_QUERY_COORDINATE_SYSTEM = "{\"coor\":\"\"}\n";
    public static final String CMD_QUERY_HARDWARE_BUILD_NUMBER = "{\"fb\":\"\"}\n";
    public static final String CMD_QUERY_HARDWARE_FIRMWARE_NUMBER = "{\"fv\":\"\"}\n";
    public static final String CMD_QUERY_HARDWARE_PLATFORM = "{\"hp\":null}\n";
    public static final String CMD_QUERY_OK_PROMPT = "{\"gc\":\"?\"}\n";
    //    public static final String CMD_APPLY_STATUS_REPORT_FORMAT = "{\"sr\":{\"line\":t,\"vel\":t,\"mpox\":t,\"mpoy\":t, \"mpoz\":t,\"mpoa\":t,\"coor\":t, \"ofsa\":t,\"ofsx\":t,\"ofsy\":t,\"ofsz\":t,\"unit\":t,\"stat\":t,\"homz\":t,\"homy\":t,\"homx\":t,\"momo\":t}}\n";
    public static final String CMD_APPLY_STATUS_REPORT_FORMAT = "{\"sr\":{\"line\":t,\"vel\":t,\"mpox\":t,\"mpoy\":t, \"mpoz\":t,\"mpoa\":t,\"coor\":t, \"ofsa\":t,\"ofsx\":t,\"ofsy\":t,\"ofsz\":t,\"dist\":t,\"unit\":t,\"stat\":t,\"homz\":t,\"homy\":t,\"homx\":t,\"momo\":t}}\n";
    public static final String CMD_QUERY_STATUS_REPORT = "{\"sr\":\"\"}\n";
    public static final String CMD_QUERY_HARDWARE_ID = "{\"id\":null}\n";
    public static final String CMD_QUERY_HARDWARE_VERSION = "{\"hv\":null}\n";
    public static final String CMD_QUERY_AXIS_X = "{\"x\":null}\n";
    public static final String CMD_QUERY_AXIS_Y = "{\"y\":null}\n";
    public static final String CMD_QUERY_AXIS_Z = "{\"z\":null}\n";
    public static final String CMD_QUERY_AXIS_A = "{\"a\":null}\n";
    public static final String CMD_QUERY_AXIS_B = "{\"b\":null}\n";
    public static final String CMD_QUERY_AXIS_C = "{\"c\":null}\n";
    public static final String CMD_QUERY_MOTOR_1_SETTINGS = "{\"1\":null}\n";
    public static final String CMD_QUERY_MOTOR_2_SETTINGS = "{\"2\":null}\n";
    public static final String CMD_QUERY_MOTOR_3_SETTINGS = "{\"3\":null}\n";
    public static final String CMD_QUERY_MOTOR_4_SETTINGS = "{\"4\":null}\n";
    public static final String CMD_QUERY_SYSTEM_SETTINGS = "{\"sys\":null}\n";
    public static final String CMD_APPLY_SYSTEM_ZERO_ALL_AXES = "{\"gc\":\"g92x0y0z0a0\"}\n";
    public static final String CMD_APPLY_SYSTEM_HOME_XYZ_AXES = "{\"gc\":\"g28.2x0y0z0\"}\n";
    public static final String CMD_APPLY_SYSTEM_GCODE_UNITS_INCHES = "{\"" + MnemonicManager.MNEMONIC_STATUS_REPORT_UNIT + "\":0}\n"; //0=inches
    public static final String CMD_APPLY_SYSTEM_GCODE_UNITS_MM = "{\"" + MnemonicManager.MNEMONIC_STATUS_REPORT_UNIT + "\":1}\n"; //1=mm
    public static final String CMD_APPLY_SYSTEM_DISABLE_LOCAL_ECHO = "{\"" + MnemonicManager.MNEMONIC_SYSTEM_ENABLE_ECHO + "\":0}\n";
    public static final String CMD_APPLY_SYSTEM_ENABLE_LOCAL_ECHO = "{\"" + MnemonicManager.MNEMONIC_SYSTEM_ENABLE_ECHO + "\":0}\n";
    public static final String CMD_APPLY_SYSTEM_MNEMONIC_SYSTEM_SWITCH_TYPE_NC = "{\"" + MnemonicManager.MNEMONIC_SYSTEM_SWITCH_TYPE + "\":1}\n";
    public static final String CMD_QUERY_SYSTEM_GCODE_UNIT_MODE = "{\"" + MnemonicManager.MNEMONIC_STATUS_REPORT_UNIT + "\":null}\n";
    public static final String CMD_QUERY_SYSTEM_GCODE_PLANE = "{\"" + MnemonicManager.MNEMONIC_SYSTEM_DEFAULT_GCODE_PLANE + "\":null}\n";
    public static final String CMD_APPLY_DISABLE_HASHCODE = "{\"eh\":0\"}\n";
    public static final String CMD_APPLY_DEFAULT_SETTINGS = "{\"defa\":1}\n";
    public static final String CMD_APPLY_STATUS_UPDATE_INTERVAL = "{\"si\":100}\n";
    public static final String CMD_APPLY_JSON_VOBERSITY = "{\"jv\":3}\n";
    public static final String CMD_APPLY_JSON_VERBOSE = "{\"jv\":5}\n";
    public static final String CMD_APPLY_ENABLE_JSON_MODE = "{\"ej\":1}\n";
    public static final String CMD_DEFAULT_ENABLE_JSON = "{\"ej\":1}\n";
    public static final String CMD_APPLY_TEXT_VOBERSITY = "{\"tv\":0}\n";
    public static final String CMD_APPLY_NOOP = "{}\n";
    public static final String CMD_QUERY_SWITCHMODE = "{\"st\":null}\n";
    public static final String CMD_APPLY_SWITCHMODE_NORMALLY_OPEN = "{\"st\":0}\n";
    public static final String CMD_APPLY_SWITCHMODE_NORMALLY_CLOSED = "{\"st\":1}\n";
    public static final String CMD_APPLY_UNITMODE_INCHES = "{\"gc\":\"g20\"}\n";
    public static final String CMD_APPLY_UNITMODE_MM = "{\"gc\":\"g21\"}\n";
    //public static final String CMD_APPLY_PAUSE = "!\n";
    //public static final String CMD_APPLY_RESUME = "~\n";
    //public static final String CMD_APPLY_QUEUE_FLUSH = "%\n";
    public static final String CMD_APPLY_CANCEL_MOVE = "!%\n";
    public static final Byte CMD_APPLY_FEEDHOLD = 0x21;
    public static final Byte CMD_APPLY_QUEUE_FLUSH = 0x25;
    //Homeing Commandings
    public static final String CMD_APPLY_HOME_X_AXIS = "{\"gc\":\"g28.2x0\"}\n";
    public static final String CMD_APPLY_HOME_Y_AXIS = "{\"gc\":\"g28.2y0\"}\n";
    public static final String CMD_APPLY_HOME_Z_AXIS = "{\"gc\":\"g28.2z0\"}\n";
    public static final String CMD_APPLY_HOME_A_AXIS = "{\"gc\":\"g28.2a0\"}\n";
    //ZERO Commands
    public static final String CMD_APPLY_ZERO_X_AXIS = "{\"gc\":\"g92x0\"}\n";
    public static final String CMD_APPLY_ZERO_Y_AXIS = "{\"gc\":\"g92y0\"}\n";
    public static final String CMD_APPLY_ZERO_Z_AXIS = "{\"gc\":\"g92z0\"}\n";
    public static final String CMD_APPLY_ZERO_A_AXIS = "{\"gc\":\"g92a0\"}\n";
    //    public static final String CMD_APPLY_INHIBIT_ALL_AXIS = "{\"xam\":2, \"yam\":2, \"zam\":2, \"aam\":2}\n";
    public static final String CMD_APPLY_INHIBIT_X_AXIS = "{\"xam\":2}\n";
    public static final String CMD_APPLY_INHIBIT_Y_AXIS = "{\"yam\":2}\n";
    public static final String CMD_APPLY_INHIBIT_Z_AXIS = "{\"zam\":2}\n";
    public static final String CMD_APPLY_INHIBIT_A_AXIS = "{\"aam\":2}\n";
    public static final String CMD_APPLY_ENABLE_X_AXIS = "{\"xam\":1}\n";
    public static final String CMD_APPLY_ENABLE_Y_AXIS = "{\"yam\":1}\n";
    public static final String CMD_APPLY_ENABLE_Z_AXIS = "{\"zam\":1}\n";
    public static final String CMD_APPLY_ENABLE_A_AXIS = "{\"aam\":1}\n";
    public static final String CMD_APPLY_INCREMENTAL_POSITION_MODE = "{\"gc\":\"g91\"}\n";
    public static final String CMD_APPLY_ABSOLUTE_POSITION_MODE = "{\"gc\":\"g90\"}\n";
    //    public static final String CMD_APPLY_ENABLE_ALL_AXIS = "{\"xam\":1, \"yam\":1, \"zam\":1, \"aam\":1}\n";
    public static final String CMD_QUERY_SYSTEM_SERIAL_BUFFER_LENGTH = "{\"rx\":null}\n";
    public static final Byte CMD_APPLY_RESET = 0x18;
    public static final Byte CMD_PRIME = 0x0;
    //    public static final String CMD_APPLY_RESET = "\x18\n";
    public static final String CMD_APPLY_FLOWCONTROL = "{\"ex\":2}\n";
    public static final String CMD_ZERO_ALL_AXIS = "{\"gc\":G920g0x0y0z0}\n";
    public static final String CMD_APPLY_BOOTLOADER_MODE = "{\"boot\":1}\n";

    public static final String CMD_QUERY_POSITION = "{\"" + MnemonicManager.MNEMONIC_GROUP_POS + "\":null}\n";

    public static final String CMD_PROGRAM_STOP = "{\"gc\":\"m0\"}\n";
    public static final String CMD_COOLANT_ON = "{\"gc\":\"m8\"}\n";
    public static final String CMD_COOLANT_OFF = "{\"gc\":\"m9\"}\n";

    /**
     * These functions take one or more paramaters and return a formatted JSON string containing the
     * corresponding Gcode / TinyG configuration command.
     */

    /**
     * Generic function to build the command to set any setting on the TinyG.
     * @param name
     * @param value
     * @return
     */
    public static String CMD_SET_CONFIG(String name, String value) {
        return String.format("{\"%s\":%s}\n", name, value);
    }

    /**
     * Generic function to build the command to get any setting on the TinyG.
     * @param name
     * @return
     */
    public static String CMD_GET_CONFIG(String name) {
        return String.format("{\"%s\":null}\n", name);
    }

    /**
     * MOTOR GROUPS:
     * Settings specific to a given motor. There are 4 motor groups, numbered 1,2,3,4 as labeled on
     * the TinyG board.
     */

    /**
     * Map a motor number to an axis number on the TinyG.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @param axis_number - The axis number from 1-5 (corresponds to XYZABC axes in that order).
     * @return
     */
    public static String CMD_SET_MAP_AXIS(int motor_number, int axis_number) {
        return String.format("{\"%dma\":%d}\n", motor_number, axis_number);
    }

    /**
     * Set the step angle for the motor.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @param step_angle - Degrees the motor travels in 1 step.
     * @return
     */
    public static String CMD_SET_STEP_ANGLE(int motor_number, int step_angle) {
        return String.format("{\"%dsa\":%d}\n", motor_number, step_angle);
    }

    /**
     * Set the distance the axis will move per one revolution of the motor. For linear axes, this
     * usually depends on the TPI (threads per inch) of the leadscrew. For more detailed information, see:
     * https://github.com/synthetos/TinyG/wiki/TinyG-Configuration-for-Firmware-Version-0.97#1tr---travel-per-revolution
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @param d - distance axis travels per revolution
     * @return
     */
    public static String CMD_SET_TRAVEL_PER_REV(int motor_number, int d) {
        return String.format("{\"%dtr\":%d}\n", motor_number, d);
    }

    /**
     * Set the microsteps of the motor.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @param n - The microstep value. Supported values:
     *          1 = no microsteps (whole steps)
     *          2 = half stepping
     *          4 = quarter stepping
     *          8 = eight stepping
     * @return
     */
    public static String CMD_SET_MICROSTEPS(int motor_number, int n) {
        return String.format("{\"%dmi\":%d}\n", motor_number, n);
    }

    /**
     * Set the polarity/direction motor will turn when presented with positive and negative Gcode
     * coordinates.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @param polarity - Polarity setting:
     *                 0 = Normal motor polarity
     *                 1 = Invert motor polarity
     * @return
     */
    public static String CMD_SET_POLARITY(int motor_number, int polarity) {
        return String.format("{\"%dpo\":%d}\n", motor_number, polarity);
    }

    /**
     * Set the power management mode for the motor.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @param mode - Power management mode:
     *             0 = Always disabled. Motor will NOT be enabled by $me
     *             1 = Always enabled. Motor will NOT be disabled by $md
     *             2 = Enabled in cycle. Motor is energized when ANY axis is moving and for $mt
     *                 seconds afterwards.
     *             3 = Enabled while moving. Motor is energized when only it is moving and for $mt
     *                 seconds afterwards.
     * @return
     */
    public static String CMD_SET_POWER_MANAGEMENT_MODE(int motor_number, int mode) {
        return String.format("{\"%dpm\":%d}\n", motor_number, mode);
    }


    /**
     * AXIS GROUPS:
     * These commands are specific to a given axis. TinyG supports 6 axis groups: XYZ linear axes
     * and ABC rotary axes (Only 4 axes can be mapped to physical motors per board).
     */

    /**
     * Sets the function of the axis.
     * @param axis - XYZ or A axis
     * @param mode - axis mode:
     *             0 = Disable. All input to that axis will be ignored and the axis will not move.
     *             1 = Standard. Linear axes move in length units. Rotary axes move in degrees.
     *             2 = Inhibited. Axis values are taken into account when planning moves, but the
     *                 axis will not move.
     *             3 = Radius mode (Rotary axes only). In radius mode gcode values are interpreted
     *                 in linear units (mm or inch) instead of degrees. The conversion from linear
     *                 to degrees is accomplished using the radius setting for that axis ($aRA).
     * @return
     */
    public static String CMD_SET_AXISMODE(String axis, int mode) {
        return String.format("{\"%sam\":%d}\n", axis, mode);
    }

    /**
     * Sets the maximum velocity the axis will move during a G0 move (straight traverse). This is
     * set in length units per minute for linear axes, degrees per minute for rotary axes.
     * @param axis - XYZ or A axis
     * @param v - max velocity in units of mm/min (G21 mode) or in/min (G20 mode) for XYZ axes, and
     *          units of deg/min for A axes.
     * @return
     */
    public static String CMD_SET_VELOCITY_MAX(String axis, int v) {
        return String.format("{\"%svm\":%d}\n", axis, v);
    }

    /**
     * Sets the maximum velocity the axis will move during a feed in a G1, G2, or G3 move (prevents
     * the Gcode F words from exceeding this value). Axis feed rates should be equal to or less than
     * the maximum velocity.
     * @param axis - XYZ or A axis
     * @param f - max feedrate in units of mm/min (G21 mode) or in/min (G20 mode)
     * @return
     */
    public static String CMD_SET_FEEDRATE_MAX(String axis, int f) {
        return String.format("{\"%sfr\":%d}\n", axis, f);
    }

    /**
     * Defines the minimum extent of travel in that axis during homing sequence (G28.2).
     * If minimum and maximum are equal, the axis is treated as an infinite axis. This is useful
     * for rotary axes.
     * @param axis - XYZ or A axis
     * @param d - minimum travel distance
     * @return
     */
    public static String CMD_SET_TRAVEL_MIN(String axis, int d) {
        return String.format("{\"%stn\":%d}\n", axis, d);
    }

    /**
     * Defines the maximum extent of travel in that axis during homing sequence (G28.2).
     * If minimum and maximum are equal, the axis is treated as an infinite axis. This is useful
     * for rotary axes.
     * @param axis - XYZ or A axis
     * @param d - maximum travel distance
     * @return
     */
    public static String CMD_SET_TRAVEL_MAX(String axis, int d) {
        return String.format("{\"%stm\":%d}\n", axis, d);
    }

    /**
     * Sets the maximum acceleration or "jerk" of the axis. Jerk values that are less than 1,000,000
     * will be multiplied by 1 million by the TinyG internally.
     * @param axis - XYZ or A axis
     * @param a - acceleration or "jerk" measured in mm/min^3 (G21 mode) or in/min^3 (G20 mode)
     * @return
     */
    public static String CMD_SET_JERK_MAX(String axis, int a) {
        return String.format("{\"%sjm\":%d}\n", axis, a);
    }

    /**
     * Sets the jerk value used for homing to stop movement when switches are hit or released.
     * Generally recommended to be larger than the JERK_MAX value as this determines how fast the
     * axis will stop once it hits the switch.
     * @param axis - XYZ or A axis
     * @param a - acceleration in units of mm/min^3 (G21 mode) or in/min^3 (G20 mode).
     * @return
     */
    public static String CMD_SET_HOMING_JERK_MAX(String axis, int a) {
        return String.format("{\"%sjh\":%d}\n", axis, a);
    }

    public static String CMD_SET_JUNCTION_DEVIATION(String axis, int d) {
        return "todo";
    }

    public static String CMD_SET_RADIUS_VALUE(String axis, int r) {
        return "todo";
    }

    /**
     * Applies the switch configuration for the minimum limit switch on the specified axis.
     * @param axis  - XYZ or A axis
     * @param mode - the following modes are supported:
     *             0 = Disabled. Switch closures will have no effect. All unused switch pins must be
     *                 set to Disabled.
     *             1 = Homing-only. Switch is active during homing but has no effect otherwise.
     *             2 = Limits-only. Switch is not active in homing but will act as a kill switch
     *                 during normal operation.
     *             3 = Homing-and-limits. Switch is active during homing and acts as kill switch
     *                 during normal operation.
     * @return
     */
    public static String CMD_SET_SWITCHMODE_MIN(String axis, int mode) {
        return String.format("{\"%ssn\":%d}\n", axis, mode);
    }

    /**
     * Applies the switch configuration for the maximum limit switch on the specified axis.
     * @param axis  - XYZ or A axis
     * @param mode - the following modes are supported:
     *             0 = Disabled. Switch closures will have no effect. All unused switch pins must be
     *                 set to Disabled.
     *             1 = Homing-only. Switch is active during homing but has no effect otherwise.
     *             2 = Limits-only. Switch is not active in homing but will act as a kill switch
     *                 during normal operation.
     *             3 = Homing-and-limits. Switch is active during homing and acts as kill switch
     *                 during normal operation.
     * @return
     */
    public static String CMD_SET_SWITCHMODE_MAX(String axis, int mode) {
        return String.format("{\"%ssx\":%d}\n", axis, mode);
    }

    /**
     * Velocity to move while initially finding the homing switch (search phase). Recommended by
     * Synthetos to set a moderate pace, e.g. 1/4 to 1/2 motor's max velocity.
     * @param axis - XYZ or A axis
     * @param v - velocity in units of mm/min (G21 mode) or in/min (G20 mode)
     * @return
     */
    public static String CMD_SET_HOMING_SEARCH_VELOCITY(String axis, int v) {
        return String.format("{\"%ssv\":%d}\n", axis, v);
    }

    /**
     * Velocity to move during the latch phase of homing sequence. Recommended by Synthetos to set
     * very low for best positional accuracy, e.g. 10 mm/min.
     * @param axis - XYZ or A axis
     * @param v - velocity in units of mm/min (G21 mode) or in/min (G20 mode)
     * @return
     */
    public static String CMD_SET_HOMING_LATCH_VELOCITY(String axis, int v) {
        return String.format("{\"%slv\":%d}\n", axis, v);
    }

    /**
     * Maximum distance to back off switch during latch phase. The distance must be large enough to
     * clear the home switch.
     * @param axis - XYZ or A axis
     * @param d - distance in units of mm (G21 mode) or in (G20 mode)
     * @return
     */
    public static String CMD_SET_HOMING_LATCH_BACKOFF(String axis, int d) {
        return String.format("{\"%slb\":%d}\n", axis, d);
    }

    /**
     * Additional distance to back off switch (after clearing the home switch during the latch phase)
     * before setting machine coordinate system zero. Usually no more than a few millimeters.
     * @param axis - XYZ or A axis
     * @param d - distance in units of mm (G21 mode) or in (G20 mode)
     * @return
     */
    public static String CMD_SET_HOMING_ZERO_BACKOFF(String axis, int d) {
        return String.format("{\"%szb\":%d}\n", axis, d);
    }

    public static String CMD_APPLY_SWITCHMODE_XMIN_LIMIT_AND_HOMING = "{\"xsn\":3}\n";
    public static String CMD_APPLY_SWITCHMODE_XMAX_LIMIT_ONLY = "{\"xsx\":2}\n";
    public static String CMD_APPLY_SWITCHMODE_ZMIN_LIMIT_AND_HOMING = "{\"zsn\":3}\n";
    public static String CMD_APPLY_SWITCHMODE_ZMAX_LIMIT_ONLY = "{\"zsx\":2}\n";
    public static String CMD_APPLY_SWITCHMODE_AMIN_HOMING_ONLY = "{\"asn\":1}\n";
    public static String CMD_APPLY_SWITCHMODE_AMAX_DISABLED = "{\"asx\":0}\n";


    /**
     * SYSTEM GROUP:
     * The system group contains global TinyG settings.
     */

    /**
     * Disable (de-energize) motor.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @return
     */
    public static String CMD_SET_MOTOR_DISABLE(int motor_number) {
        return String.format("{\"md\":%d}\n", motor_number);
    }

    /**
     * Enable (energize) motor.
     * @param motor_number - The physical motor "slot" which the motor is wired to on the TinyG.
     * @return
     */
    public static String CMD_SET_MOTOR_ENABLE(int motor_number) {
        return String.format("{\"me\":%d}\n", motor_number);
    }

    /**
     * Set motor enable timeout.
     * @param seconds - timeout in seconds (up to 1 million seconds).
     * @return
     */
    public static String CMD_SET_MOTOR_ENABLE_TIMEOUT(int seconds) {
        return String.format("{\"mt\":%d}\n", seconds);
    }


    public static String CMD_STRAIGHT_TRAVERSE(String axis, int d) {
        return String.format("{\"gc\":\"g0%s%d\"}\n", axis, d);
    }
    public static String CMD_STRAIGHT_FEED(String axis, double d, int f) {
        return String.format("{\"gc\":\"g1%s%fF%d\"}\n", axis, d, f);
    }
    public static String CMD_DWELL(int sec) {
        return String.format("{\"gc\":\"g4p%d\"}\n", sec);
    }
}