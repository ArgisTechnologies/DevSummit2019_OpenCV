package com.logicpd.papapill.device.tinyg;

/**
 * This class contains various board level defines.
 */
public final class BoardDefaults {

    // UART board defaults
    public static final String UART_DEVICE_NAME = "UART0";
    public static final int UART_BAUD_RATE = 115200;
    public static final int UART_DATA_BITS = 8;
    public static final int UART_STOP_BITS = 1;

    // GPIO board defaults
    public static final String GPIO_LIGHT_RING = "BCM27";
    public static final String GPIO_SPEAKER_EN = "BCM20";

    // List of TinyG configuration settings.
    public static final Setting[] tinygSettings = {
            // Communication Settings
            new Setting("ee", "0"),         // enable echo [0=off,1=on]
            new Setting("ec", "0"),         // expand LF to CRLF on TX [0=off,1=on]
            new Setting("ex", "0"),         // enable flow control [0=off,1=XON/XOFF, 2=RTS/CTS]

            // System
            new Setting("st", "0"),         // switch type, [0=NO,1=NC]
            new Setting("mt", "2"),         // motor idle timeout, in seconds
            new Setting("jv", "5"),         // json verbosity, [0=silent,1=footer,2=messages,3=configs,4=linenum,5=verbose]
            new Setting("js", "1"),         // json serialize style [0=relaxed,1=strict]
            new Setting("tv", "0"),         // text verbosity [0=silent,1=verbose]
            new Setting("qv", "0"),         // queue report verbosity [0=off,1=single,2=triple]
            new Setting("sv", "1"),         // status report verbosity [0=off,1=filtered,2=verbose]
            new Setting("si", "200"),       // status interval, in ms

            new Setting("ja", "200000"),    // junction acceleration / global cornering acceleration, mm/min^2
            new Setting("ct", "0.0100"),    // Sets precision of arc drawing, in mm

            new Setting("gpl", "0"),        // default plane selection	[0=XY plane (G17), 1=XZ plane (G18), 2=YZ plane (G19)]
            new Setting("gun", "1"),        // default gcode units mode [0=G20,1=G21] (1=mm)]
            new Setting("gco", "1"),        // default coordinate system [1=G54, 2=G55, 3=G56, 4=G57, 5=G58, 6=G59]
            new Setting("gpa", "1"),        // default path control mode [0=Exact path mode (G61), 1=Exact stop mode (G61.1), 2=Continuous mode (G64)]
            new Setting("gdi", "1"),        // default distance mode [0=Absolute mode (G90), 1=Incremental mode (G91)]

            // Homing and limits
            new Setting("xsn", "3"),        // x switch min [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("xsx", "2"),        // x switch max [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("ysn", "3"),        // y switch min [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("ysx", "2"),        // y switch max [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("zsn", "3"),        // z switch min [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("zsx", "2"),        // z switch max [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("asn", "1"),        // a switch min [0=off,1=homing,2=limit,3=limit+homing];
            new Setting("asx", "0"),        // a switch max [0=off,1=homing,2=limit,3=limit+homing];

            // Motor 2 (Radial Motor) --> X Axis (Rho)
            new Setting("2ma", "0"),        // map to axis [0=X,1=Y,2=Z...]
            new Setting("2sa", "1.800"),    // step angle, deg
            new Setting("2tr", "23.562"),   // travel per revolution, mm
            new Setting("2mi", "8"),        // microsteps [1,2,4,8], qQuintic [1,2,4,8,16,32]
            new Setting("2po", "0"),        // motor polarity [0=normal,1=reverse]
            new Setting("2pm", "3"),        // power management [0=disabled,1=always on,2=in cycle,3=when moving]

            // Motor 3 (Pickup Motor) --> Z Axis (Z)
            new Setting("3ma", "2"),        // map to axis [0=X,1=Y,2=Z...]
            new Setting("3sa", "1.800"),    // step angle, deg
            new Setting("3tr", "23.562"),   // travel per revolution, mm
            new Setting("3mi", "8"),        // microsteps [1,2,4,8], qQuintic [1,2,4,8,16,32]
            new Setting("3po", "1"),        // motor polarity [0=normal,1=reverse]
            new Setting("3pm", "3"),        // power management [0=disabled,1=always on,2=in cycle,3=when moving]

            // Motor 1 (Carousel Motor) --> A Axis (Theta)
            new Setting("1ma", "3"),        // map to axis [0=X,1=Y,2=Z...]
            new Setting("1sa", "1.8"),      // step angle, deg
            new Setting("1tr", "360"),      // travel per revolution, deg
            new Setting("1mi", "8"),        // microsteps [1,2,4,8], qQuintic [1,2,4,8,16,32]
            new Setting("1po", "1"),        // motor polarity [0=normal,1=reverse]
            new Setting("1pm", "3"),        // power management [0=disabled,1=always on,2=in cycle,3=when moving]

            // Motor 4 (Not Used - Disabled)
            new Setting("4ma", "1"),        // map to axis [0=X,1=Y,2=Z...]
            new Setting("4sa", "1.800"),    // step angle, deg
            new Setting("4tr", "8"),        // travel per revolution, mm
            new Setting("4mi", "8"),        // microsteps [1,2,4,8], qQuintic [1,2,4,8,16,32]
            new Setting("4po", "1"),        // motor polarity [0=normal,1=reverse]
            new Setting("4pm", "0"),        // power management [0=disabled,1=always on,2=in cycle,3=when moving]

            // X axis (Mapped to Motor 2)
            new Setting("xam", "1"),        // x axis mode, 1=standard
            new Setting("xvm", "10000"),    // x velocity maximum, mm/min
            new Setting("xfr", "10000"),    // x feedrate maximum, mm/min
            new Setting("xtn", "0"),        // x travel minimum, mm
            new Setting("xtm", "200"),      // x travel maximum, mm
            new Setting("xjm", "200"),      // x jerk maximum, mm/min^3 * 1 million
            new Setting("xjh", "2000"),     // x jerk homing, mm/min^3 * 1 million
            new Setting("xsv", "1000"),     // x search velocity, mm/min
            new Setting("xlv", "100"),      // x latch velocity, mm/min
            new Setting("xlb", "15"),       // x latch backoff, mm
            new Setting("xzb", "2"),        // x zero backoff, mm

            // Y axis (Not Mapped)
            /*
            new Setting("yam", "0"),        // y axis mode, 1=standard
            new Setting("yvm", "10000"),    // y velocity maximum, mm/min
            new Setting("yfr", "10000"),    // y feedrate maximum, mm/min
            new Setting("ytn", "0"),        // y travel minimum, mm
            new Setting("ytm", "200"),      // y travel maximum, mm
            new Setting("yjm", "200"),      // y jerk maximum, mm/min^3 * 1 million
            new Setting("yjh", "1000"),     // y jerk homing, mm/min^3 * 1 million
            new Setting("ysv", "1500"),     // y search velocity, mm/min
            new Setting("ylv", "100"),      // y latch velocity, mm/min
            new Setting("ylb", "15"),       // y latch backoff, mm
            new Setting("yzb", "2"),        // y zero backoff, mm
            */

            // Z axis (Mapped to Motor 3)
            new Setting("zam", "1"),        // z axis mode, 1=standard
            new Setting("zvm", "5000"),     // z velocity maximum, mm/min
            new Setting("zfr", "2000"),     // z feedrate maximum, mm/min
            new Setting("ztn", "0"),        // z travel minimum, mm
            new Setting("ztm", "100"),      // z travel maximum, mm
            new Setting("zjm", "50"),       // z jerk maximum, mm/min^3 * 1 million
            new Setting("zjh", "2000"),     // z jerk homing, mm/min^3 * 1 million
            new Setting("zsv", "1000"),     // z search velocity, mm/min
            new Setting("zlv", "100"),      // z latch velocity, mm/min
            new Setting("zlb", "10"),       // z latch backoff, mm
            new Setting("zzb", "2"),        // z zero backoff, mm

            // A axis (Mapped to Motor 1)
            new Setting("aam", "1"),        // a axis mode, 1=standard
            new Setting("avm", "50000"),    // a velocity maximum, mm/min
            new Setting("afr", "50000"),    // a feedrate maximum, mm/min
            new Setting("atn", "0"),        // a travel minimum, mm
            new Setting("atm", "1080"),     // a travel maximum, mm
            new Setting("ajm", "20"),       // a jerk maximum, mm/min^3 * 1 million
            new Setting("ajh", "10000"),    // a jerk homing, mm/min^3 * 1 million
            new Setting("asv", "1000"),     // a search velocity, mm/min
            new Setting("alv", "200"),      // a latch velocity, mm/min
            new Setting("alb", "50"),       // a latch backoff, mm
            new Setting("azb", "23.5"),       // a zero backoff, mm

            // B axis (Not Mapped)
            /*
            new Setting("bam", "0"),        // b axis mode, 1=standard
            new Setting("bvm", "50000"),    // b velocity maximum, mm/min
            new Setting("bfr", "50000"),    // b feedrate maximum, mm/min
            new Setting("btn", "0"),        // b travel minimum, mm
            new Setting("btm", "1080"),     // b travel maximum, mm
            new Setting("bjm", "20"),       // b jerk maximum, mm/min^3 * 1 million
            new Setting("bjh", "10000"),    // b jerk homing, mm/min^3 * 1 million
            new Setting("bsv", "1000"),     // b search velocity, mm/min
            new Setting("blv", "200"),      // b latch velocity, mm/min
            new Setting("blb", "50"),       // b latch backoff, mm
            new Setting("bzb", "22"),       // b zero backoff, mm
            */

            // C axis (Not Mapped)
            /*
            new Setting("cam", "0"),        // c axis mode, 1=standard
            new Setting("cvm", "50000"),    // c velocity maximum, mm/min
            new Setting("cfr", "50000"),    // c feedrate maximum, mm/min
            new Setting("ctn", "0"),        // c travel minimum, mm
            new Setting("ctm", "1080"),     // c travel maximum, mm
            new Setting("cjm", "20"),       // c jerk maximum, mm/min^3 * 1 million
            new Setting("cjh", "10000"),    // c jerk homing, mm/min^3 * 1 million
            new Setting("csv", "1000"),     // c search velocity, mm/min
            new Setting("clv", "200"),      // c latch velocity, mm/min
            new Setting("clb", "50"),       // c latch backoff, mm
            new Setting("czb", "22"),       // c zero backoff, mm
            */
    };

    public static class Setting {
        private Setting(String name, String value) {
            this.name = name;
            this.value = value;
        }
        public String name;
        public String value;
    }
}
