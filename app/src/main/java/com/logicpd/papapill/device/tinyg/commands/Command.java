// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import com.logicpd.papapill.device.enums.CommandOperation;
import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;

/**
 * This interface contains the abstract methods which all individual device commands
 * must implement.
 */
public interface Command {
    // Get the unique identifier of the command.
    public DeviceCommand getCommandId();

    // Get the front facing command name/string.
    public String getName();

    // Get the string used to identify the response to this command.
    public String getResponseId();

    // Get the parameters of the command.
    public String getParams();

    // Set the parameters of the command.
    public void setParams(String params);

    // Get the response string of the command.
    public String getResponse();

    // Set the response string of the command.
    public void setResponse(String response);

    // Get the data object of the command.
    public Object getData();

    // Set the data object of the command.
    public void setData(Object data);

    // Get the current execution state of the command.
    public CommandState getState();

    // Set the current execution state of the command.
    public void setState(CommandState state);

    // Determine whether command is in an internal waiting state.
    public boolean isWaiting();

    // Determine whether command is marked complete.
    public boolean isComplete();

    // Get the command's stage of operation.
    public CommandOperation getOperation();

    // Set the command's stage of operation.
    public void setOperation(CommandOperation operation);

    // Abstract method to actually run the command.
    public void execute();
}
