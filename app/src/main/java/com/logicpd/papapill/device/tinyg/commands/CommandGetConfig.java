// TODO: Currently a stub. This was meant to be a command to query a config setting
// from the TinyG. However, there seems to be no immediate need for this command
// at this time. May be able to be removed it is not needed.

package com.logicpd.papapill.device.tinyg.commands;

import com.logicpd.papapill.device.enums.DeviceCommand;

public final class CommandGetConfig extends BaseCommand {

    private static final DeviceCommand identifier = DeviceCommand.COMMAND_GET_CONFIG;

    public CommandGetConfig() {
        super(identifier);
    }

    @Override
    public void execute() {
        switch(operation) {
            case OP_STATE_START:

                break;

            case OP_STATE_RUN:
                switch(state) {

                }
                break;

            default:
                break;
        }
    }
}
