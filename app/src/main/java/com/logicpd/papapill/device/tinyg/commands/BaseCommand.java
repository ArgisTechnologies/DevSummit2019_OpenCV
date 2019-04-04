// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import com.logicpd.papapill.device.enums.CommandOperation;
import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.tinyg.ResponseBuilder;

/**
 * This class contains the properties and methods that are common to each individual command
 * (which must extend from BaseCommand).
 */
public abstract class BaseCommand implements Command {

    public final DeviceCommand commandId;
    public final String name;
    protected String responseId;
    public volatile String params = "";
    public volatile String response = "";
    public volatile Object data;

    public volatile CommandState state;
    public volatile CommandOperation operation;

    public BaseCommand(DeviceCommand command) {
        this.commandId = command;
        this.name = command.name();
        this.responseId = ResponseBuilder.dictionary.get(command.name());
    }

    // Some getters and setters
    public DeviceCommand getCommandId() { return this.commandId; }
    public String getName() { return this.name; }
    public String getResponseId() { return this.responseId; }
    public void setResponseId(String id)
    {
        responseId = id;
    }
    public String getParams() { return this.params; }
    public void setParams(String params) { this.params = params; }

    public String getResponse() { return this.response; }
    public void setResponse(String response) { this.response = response; }

    public Object getData() { return this.data; }
    public void setData(Object data) { this.data = data; }

    public CommandState getState() { return this.state; }
    public void setState(CommandState state) { this.state = state; }

    public CommandOperation getOperation() { return this.operation; }
    public void setOperation(CommandOperation operation) { this.operation = operation; }

    // Checks and returns true if waiting state is the current state.
    public boolean isWaiting() { return getState() == CommandState.WAITING_STATE; }
    public boolean isComplete() { return getState() == CommandState.COMPLETE_STATE; }

    // Splits the param string using whitespace delimiter into a param array.
    public String[] getParamArray() { return getParams().split("\\s+"); }

    // Abstract method of interface method execute(). It is up to the individual
    // commands to define the execute function.
    public abstract void execute();
}
