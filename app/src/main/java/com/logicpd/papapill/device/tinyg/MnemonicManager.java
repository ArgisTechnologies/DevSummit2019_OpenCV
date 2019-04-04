package com.logicpd.papapill.device.tinyg;

/**
 * The purpose of this class is to keep track of the various configuration and command mnemonics
 * that the TinyG uses in its communication protocol. It is used primarily by CommandBuilder class
 * to help construct the g-code command for the TinyG. Like the CommandBuilder, this class contains
 * a full list of mnemonics available on the TinyG whereas we only use a select few. Eventually, it
 * can be trimmed down.
 */
public class MnemonicManager {
    //General JSON Mnemonics (Keys)
    public static final String MNEMONIC_JSON_RESPONSE = "r";
    public static final String MNEMONIC_JSON_FOOTER = "f";
    //Group Mnemonics
    public static final String MNEMONIC_GROUP_SYSTEM = "sys";
    public static final String MNEMONIC_GROUP_EMERGENCY_SHUTDOWN = "er";
    public static final String MNEMONIC_GROUP_STATUS_REPORT = "sr";
    public static final String MNEMONIC_GROUP_HOME = "hom";
    public static final String MNEMONIC_GROUP_POS = "pos";
    public static final String MNEMONIC_GROUP_MOTOR_1 = "1";
    public static final String MNEMONIC_GROUP_MOTOR_2 = "2";
    public static final String MNEMONIC_GROUP_MOTOR_3 = "3";
    public static final String MNEMONIC_GROUP_MOTOR_4 = "4";
    public static final String MNEMONIC_GROUP_AXIS_X = "x";
    public static final String MNEMONIC_GROUP_AXIS_Y = "y";
    public static final String MNEMONIC_GROUP_AXIS_Z = "z";
    public static final String MNEMONIC_GROUP_AXIS_A = "a";
    public static final String MNEMONIC_GROUP_AXIS_B = "b";
    public static final String MNEMONIC_GROUP_AXIS_C = "c";
    //AXIS Mnemonics
    public static final String MNEMONIC_AXIS_AXIS_MODE = "am";
    public static final String MNEMONIC_AXIS_VELOCITY_MAXIMUM = "vm";
    public static final String MNEMONIC_AXIS_FEEDRATE_MAXIMUM = "fr";
    public static final String MNEMONIC_AXIS_TRAVEL_MAXIMUM = "tm";
    public static final String MNEMONIC_AXIS_JERK_MAXIMUM = "jm";
    public static final String MNEMONIC_AXIS_JERK_HOMING = "jh";
    public static final String MNEMONIC_AXIS_JUNCTION_DEVIATION = "jd";
    public static final String MNEMONIC_AXIS_MAX_SWITCH_MODE = "sx";
    public static final String MNEMONIC_AXIS_MIN_SWITCH_MODE = "sn";
    public static final String MNEMONIC_AXIS_SEARCH_VELOCITY = "sv";
    public static final String MNEMONIC_AXIS_LATCH_VELOCITY = "lv";
    public static final String MNEMONIC_AXIS_LATCH_BACKOFF = "lb";
    public static final String MNEMONIC_AXIS_ZERO_BACKOFF = "zb";
    public static final String MNEMONIC_AXIS_RADIUS = "ra";
    //MOTOR Mnemonics
    public static final String MNEMONIC_MOTOR_MAP_AXIS = "ma";
    public static final String MNEMONIC_MOTOR_STEP_ANGLE = "sa";
    public static final String MNEMONIC_MOTOR_TRAVEL_PER_REVOLUTION = "tr";
    public static final String MNEMONIC_MOTOR_MICROSTEPS = "mi";
    public static final String MNEMONIC_MOTOR_POLARITY = "po";
    public static final String MNEMONIC_MOTOR_POWER_MANAGEMENT = "pm";
    //Status Report
    public static final String MNEMONIC_STATUS_REPORT_POSX = "posx";
    public static final String MNEMONIC_STATUS_REPORT_POSY = "posy";
    public static final String MNEMONIC_STATUS_REPORT_POSZ = "posz";
    public static final String MNEMONIC_STATUS_REPORT_POSA = "posa";
    //Homed Status
    public static final String MNEMONIC_STATUS_REPORT_HOMEDX = "homx";
    public static final String MNEMONIC_STATUS_REPORT_HOMEDY = "homy";
    public static final String MNEMONIC_STATUS_REPORT_HOMEDZ = "homz";
    public static final String MNEMONIC_STATUS_REPORT_HOMEDA = "homa";
    //Machine Positions
    public static final String MNEMONIC_STATUS_REPORT_MACHINEPOSX = "mpox"; //Machine Position
    public static final String MNEMONIC_STATUS_REPORT_MACHINEPOSY = "mpoy"; //Machine Position
    public static final String MNEMONIC_STATUS_REPORT_MACHINEPOSZ = "mpoz"; //Machine Position
    public static final String MNEMONIC_STATUS_REPORT_MACHINEPOSA = "mpoa"; //Machine Position
    //Offsets
    public static final String MNEMONIC_STATUS_REPORT_WORKOFFSETA = "ofsa";
    public static final String MNEMONIC_STATUS_REPORT_WORKOFFSETX = "ofsx";
    public static final String MNEMONIC_STATUS_REPORT_WORKOFFSETY = "ofsy";
    public static final String MNEMONIC_STATUS_REPORT_WORKOFFSETZ = "ofsz";
    //
    public static final String MNEMONIC_STATUS_REPORT_LINE = "line";
    public static final String MNEMONIC_STATUS_REPORT_VELOCITY = "vel";
    public static final String MNEMONIC_STATUS_REPORT_MOTION_MODE = "momo";
    public static final String MNEMONIC_STATUS_REPORT_STAT = "stat";
    public static final String MNEMONIC_STATUS_REPORT_UNIT = "unit";
    public static final String MNEMONIC_STATUS_REPORT_COORDNIATE_MODE = "coor";
    //System MNEMONICS
    public static final String MNEMONIC_SYSTEM_DEFAULT_GCODE_UNIT_MODE = "gun";
    public static final String MNEMONIC_SYSTEM_DEFAULT_GCODE_PLANE = "gpl";
    public static final String MNEMONIC_SYSTEM_DEFAULT_GCODE_COORDINATE_SYSTEM = "gco";
    public static final String MNEMONIC_SYSTEM_DEFAULT_GCODE_PATH_CONTROL = "gpa";
    public static final String MNEMONIC_SYSTEM_DEFAULT_GCODE_DISTANCE_MODE = "gdi";
    public static final String MNEMONIC_SYSTEM_FIRMWARE_BUILD = "fb";
    public static final String MNEMONIC_SYSTEM_SWITCH_TYPE = "st";
    public static final String MNEMONIC_SYSTEM_FIRMWARE_VERSION = "fv";
    public static final String MNEMONIC_SYSTEM_HARDWARD_PLATFORM = "hp";
    public static final String MNEMONIC_SYSTEM_HARDWARE_VERSION = "hv";
    public static final String MNEMONIC_SYSTEM_JUNCTION_ACCELERATION = "ja";
    public static final String MNEMONIC_SYSTEM_MIN_LINE_SEGMENT = "ml";
    public static final String MNEMONIC_SYSTEM_MIN_ARC_SEGMENT = "ma";
    public static final String MNEMONIC_SYSTEM_MIN_TIME_SEGMENT = "mt";
    public static final String MNEMONIC_SYSTEM_IGNORE_CR = "ic";
    public static final String MNEMONIC_SYSTEM_ENABLE_ECHO = "ee";
    public static final String MNEMONIC_SYSTEM_ENABLE_XON = "ex";
    public static final String MNEMONIC_SYSTEM_QUEUE_REPORTS = "eq";
    public static final String MNEMONIC_SYSTEM_ENABLE_JSON_MODE = "ej";
    public static final String MNEMONIC_SYSTEM_JSON_VOBERSITY = "jv";
    public static final String MNEMONIC_SYSTEM_TEXT_VOBERSITY = "tv";
    public static final String MNEMONIC_SYSTEM_STATUS_REPORT_INTERVAL = "si";
    public static final String MNEMONIC_SYSTEM_BAUDRATE = "baud";
    //    public static final String MNEMONIC_SYSTEM_LAST_MESSAGE = "msg";
    public static final String MNEMONIC_SYSTEM_EXPAND_LF_TO_CRLF_ON_TX = "ec";
    public static final String MNEMONIC_SYSTEM_CHORDAL_TOLERANCE = "ct";
    public static final String MNEMONIC_SYSTEM_TINYG_ID_VERSION = "id";
    public static final String MNEMONIC_STATUS_REPORT_TINYG_DISTANCE_MODE = "dist";
}
