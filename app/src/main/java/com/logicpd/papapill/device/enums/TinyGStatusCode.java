package com.logicpd.papapill.device.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum contains all possible response status codes from the TinyG.
 * The full list of status codes is defined on the TinyG wiki page:
 * https://github.com/synthetos/TinyG/wiki/TinyG-Status-Codes
 */
public enum TinyGStatusCode {

    // Low Level Codes
    OK(0),                                      // universal OK code
    ERROR(1),                                   // generic error return code
    EAGAIN(2),                                  // function would block here
    NOOP(3),                                    // function had no-operation
    COMPLETE(4),                                // operation is complete
    TERMINATE(5),                               // operation terminated (gracefully)
    RESET(6),                                   // operation was hard reset (sig kill)
    EOL(7),                                     // returned end-of-line
    EOF(8),                                     // returned end-of-file
    FILE_NOT_OPEN(9),
    FILE_SIZE_EXCEEDED(10),
    NO_SUCH_DEVICE(11),
    BUFFER_EMPTY(12),
    BUFFER_FULL(13),
    BUFFER_FULL_FATAL(14),
    INITIALIZING(15),
    ENTERING_BOOT_LOADER(16),
    FUNCTION_IS_STUBBED(17),

    // Internal System Errors
    INTERNAL_ERROR(20),
    INTERNAL_RANGE_ERROR(21),
    FLOATING_POINT_ERROR(22),
    DIVIDE_BY_ZERO(23),
    INVALID_ADDRESS(24),
    READ_ONLY_ADDRESS(25),
    INIT_FAIL(26),
    ALARMED(27),
    FAILED_TO_GET_PLANNER_BUFFER(28),
    GENERIC_EXCEPTION_REPORT(29),
    PREP_LINE_MOVE_TIME_IS_INFINITE(30),
    PREP_LINE_MOVE_TIME_IS_NAN(31),
    FLOAT_IS_INFINITE(32),
    FLOAT_IS_NAN(33),
    PERSISTENCE_ERROR(34),
    BAD_STATUS_REPORT_SETTING(35),

    // Assertion Failures
    CONFIG_ASSERTION_FAILURE(90),
    XIO_ASSERTION_FAILURE(91),
    ENCODER_ASSERTION_FAILURE(92),
    STEPPER_ASSERTION_FAILURE(93),
    PLANNER_ASSERTION_FAILURE(94),
    CANONICAL_MACHINE_ASSERTION_FAILURE(95),
    CONTROLLER_ASSERTION_FAILURE(96),
    STACK_OVERFLOW(97),
    MEMORY_FAULT(98),
    GENERIC_ASSERTION_FAILURE(99),

    // Generic Data Input Errors
    UNRECOGNIZED_NAME(100),                     // parser didn't recognize the command
    INVALID_OR_MALFORMED_COMMAND(101),          // malformed line to parser
    BAD_NUMBER_FORMAT(102),                     // number format error
    BAD_UNSUPPORTED_TYPE(103),                  // number or JSON type is not supported
    PARAMETER_IS_READ_ONLY(104),                // this parameter is read-only - cannot be set
    PARAMETER_CANNOT_BE_READ(105),              // this parameter is not readable
    COMMAND_NOT_ACCEPTED(106),                  // command cannot be accepted at this time
    INPUT_EXCEEDS_MAX_LENGTH(107),              // input string too long
    INPUT_LESS_THAN_MIN_VALUE(108),             // value is under minimum
    INPUT_EXCEEDS_MAX_VALUE(109),               // value is over maximum
    INPUT_VALUE_RANGE_ERROR(110),               // value is out-of-range
    JSON_SYNTAX_ERROR(111),                     // JSON input string is not well formed
    JSON_TOO_MANY_PAIRS(112),                   // JSON input string has too many pairs
    JSON_TOO_LONG(113),                         // JSON exceeds buffer size

    // Gcode Errors and Warnings
    GCODE_GENERIC_INPUT_ERROR(130),
    GCODE_COMMAND_UNSUPPORTED(131),
    MCODE_COMMAND_UNSUPPORTED(132),
    GCODE_MODAL_GROUP_VIOLATION(133),
    GCODE_AXIS_IS_MISSING(134),
    GCODE_AXIS_CANNOT_BE_PRESENT(135),
    GCODE_AXIS_IS_INVALID(136),
    GCODE_AXIS_IS_NOT_CONFIGURED(137),
    GCODE_AXIS_NUMBER_IS_MISSING(138),
    GCODE_AXIS_NUMBER_IS_INVALID(139),
    GCODE_ACTIVE_PLANE_IS_MISSING(140),
    GCODE_ACTIVE_PLANE_IS_INVALID(141),
    GCODE_FEEDRATE_NOT_SPECIFIED(142),
    GCODE_INVERSE_TIME_MODE_CANNOT_BE_USED(143),
    GCODE_ROTARY_AXIS_CANNOT_BE_USED(144),
    GCODE_G53_WITHOUT_G0_OR_G1(145),
    REQUESTED_VELOCITY_EXCEEDS_LIMITS(146),
    CUTTER_COMPENSATION_CANNOT_BE_ENABLED(147),
    PROGRAMMED_POINT_SAME_AS_CURRENT_POINT(148),
    SPINDLE_SPEED_BELOW_MINIMUM(149),
    SPINDLE_SPEED_MAX_EXCEEDED(150),
    S_WORD_IS_MISSING(151),
    S_WORD_IS_INVALID(152),
    SPINDLE_MUST_BE_OFF(153),
    SPINDLE_MUST_BE_TURNING(154),
    ARC_SPECIFICATION_ERROR(155),
    ARC_AXIS_MISSING_FOR_SELECTED_PLANE(156),
    ARC_OFFSETS_MISSING_FOR_SELECTED_PLANE(157),
    ARC_RADIUS_OUT_OF_TOLERANCE(158),
    ARC_ENDPOINT_IS_STARTING_POINT(159),
    P_WORD_IS_MISSING(160),
    P_WORD_IS_INVALID(161),
    P_WORD_IS_ZERO(162),
    P_WORD_IS_NEGATIVE(163),
    P_WORD_IS_NOT_AN_INTEGER(164),
    P_WORD_IS_NOT_VALID_TOOL_NUMBER(165),
    D_WORD_IS_MISSING(166),
    D_WORD_IS_INVALID(167),
    E_WORD_IS_MISSING(168),
    E_WORD_IS_INVALID(169),
    H_WORD_IS_MISSING(170),
    H_WORD_IS_INVALID(171),
    L_WORD_IS_MISSING(172),
    L_WORD_IS_INVALID(173),
    Q_WORD_IS_MISSING(174),
    Q_WORD_IS_INVALID(175),
    R_WORD_IS_MISSING(176),
    R_WORD_IS_INVALID(177),
    T_WORD_IS_MISSING(178),
    T_WORD_IS_INVALID(179),

    // TinyG Errors and Warnings
    GENERIC_ERROR(200),
    MINIMUM_LENGTH_MOVE(201),
    MINIMUM_TIME_MOVE(202),
    MACHINE_ALARMED(203),
    LIMIT_SWITCH_HIT(204),
    PLANNER_FAILED_TO_CONVERGE(205),
    SOFT_LIMIT_EXCEEDED(220),
    SOFT_LIMIT_EXCEEDED_XMIN(221),
    SOFT_LIMIT_EXCEEDED_XMAX(222),
    SOFT_LIMIT_EXCEEDED_YMIN(223),
    SOFT_LIMIT_EXCEEDED_YMAX(224),
    SOFT_LIMIT_EXCEEDED_ZMIN(225),
    SOFT_LIMIT_EXCEEDED_ZMAX(226),
    SOFT_LIMIT_EXCEEDED_AMIN(227),
    SOFT_LIMIT_EXCEEDED_AMAX(228),
    SOFT_LIMIT_EXCEEDED_BMIN(229),
    SOFT_LIMIT_EXCEEDED_BMAX(230),
    SOFT_LIMIT_EXCEEDED_CMIN(231),
    SOFT_LIMIT_EXCEEDED_CMAX(231),
    HOMING_CYCLE_FAILED (240),
    HOMING_ERROR_BAD_OR_NO_AXIS(241),
    HOMING_ERROR_SWITCH_MISCONFIGURATION(242),
    HOMING_ERROR_ZERO_SEARCH_VELOCITY(243),
    HOMING_ERROR_ZERO_LATCH_VELOCITY(244),
    HOMING_ERROR_TRAVEL_MIN_MAX_IDENTICAL(245),
    HOMING_ERROR_NEGATIVE_LATCH_BACKOFF(246),
    HOMING_ERROR_SEARCH_FAILED(247),
    PROBE_CYCLE_FAILED(250),
    PROBE_ENDPOINT_IS_STARTING_POINT(251),
    JOGGING_CYCLE_FAILED(252);

    private int value;
    private static Map map = new HashMap<>();

    private TinyGStatusCode(int value) {
        this.value = value;
    }

    static {
        for (TinyGStatusCode pageType : TinyGStatusCode.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static TinyGStatusCode valueOf(int statusCode) {
        return (TinyGStatusCode) map.get(statusCode);
    }

    public int getValue() {
        return value;
    }
}
