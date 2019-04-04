package com.logicpd.papapill.device.tinyg;

import com.logicpd.papapill.device.enums.DeviceCommand;

import java.util.HashMap;
import java.util.Map;

// TODO: I don't like this being here. Response Ids should be part of the command classes.
public class ResponseBuilder {

    public static final String RESPONSE_NONE = "none";
    public static final String RESPONSE_MOTOR_HOME = "{\"r\":{\"gc\":\"G28.2";
    public static final String RESPONSE_MOTOR_MOVE = "{\"r\":{\"gc\":\"G1";
    public static final String RESPONSE_READ_POSITION = "{\"r\":{\"pos\":";

    public static final Map<String, String> dictionary;

    // Any commands that have response Ids associated with it must be added to the dictionary
    // here. If a command does not have a response Id, the call to get the key from this
    // dictionary will return null.
    static {
        dictionary = new HashMap<String, String>();
        dictionary.put(DeviceCommand.COMMAND_FULL_DISPENSE.name(), RESPONSE_NONE);
        dictionary.put(DeviceCommand.COMMAND_GET_CONFIG.name(), RESPONSE_NONE);
        dictionary.put(DeviceCommand.COMMAND_MOTOR_HOME.name(), RESPONSE_MOTOR_HOME);
        dictionary.put(DeviceCommand.COMMAND_MOTOR_INIT.name(), RESPONSE_NONE);
        dictionary.put(DeviceCommand.COMMAND_MOTOR_MOVE.name(), RESPONSE_MOTOR_MOVE);
        dictionary.put(DeviceCommand.COMMAND_MOTOR_PICKUP.name(), RESPONSE_NONE);
        dictionary.put(DeviceCommand.COMMAND_READ_POSITION.name(), RESPONSE_READ_POSITION);
        dictionary.put(DeviceCommand.COMMAND_SET_CONFIG.name(), RESPONSE_NONE);
        dictionary.put(DeviceCommand.COMMAND_ROTATE_CAROUSEL.name(), RESPONSE_NONE);
    }
}
