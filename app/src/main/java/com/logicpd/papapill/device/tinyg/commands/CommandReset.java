package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.gpio.LightringManager;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.CoordinateManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnBinReadyListener;
import com.logicpd.papapill.interfaces.OnErrorListener;

public final class CommandReset extends BaseCommand {
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_RESET;

    public CommandReset() {
        super(identifier);
    }

    @Override
    public void execute() {

        switch (operation) {
            case OP_STATE_START:

                /*
                 * Send Ctrl+x 0x18 to reset tinyG
                 */
                Log.d("CommandReset", "execute OP_STATE_START");
                this.setResponseId("{\"r\":{\"fv\":");

                /*
                 * Circular dependency can kill !!!!
                 */
                TinyGDriver.getInstance().softwareReset();
                setState(CommandState.RESET_WAITING_FOR_IDLE);
                break;

            case OP_STATE_RUN:
                switch (state) {
                    case RESET_WAITING_FOR_IDLE:
                        /*
                         * add time out condition here -> or check time transpired since start
                         * put it in database
                         */
                        String response = this.getResponse();
                        //{"r":{"fv":0.970,"fb":440.20,"hp":1,"hv":8,"id":"7W0243-VWX","msg":"SYSTEM READY"},"f":[1,0,0,9991]}

                        Log.d("RESET_WAITING_FOR_IDLE", "response:"+response);
                        if(response.contains("SYSTEM READY"))
                        {
                            // Turn off the light ring.
                            LightringManager.getInstance().configureOutput(false);

                            Log.d("RESET_WAITING_FOR_IDLE", "Calling COMMAND_HOME_ALL");
                            // tinyG reset done, home to verify
                            CommandManager.getInstance().callCommand(DeviceCommand.COMMAND_HOME_ALL, null, null);
                            setState(CommandState.RESET_WAITING_FOR_HOME);
                        }
                        break;

                    case RESET_WAITING_FOR_HOME:
                        if (CommandManager.getInstance().isCommandDone(DeviceCommand.COMMAND_HOME_ALL))
                        {
                            Log.d("CommandReset", "COMPLETE_STATE");

                            // Advance our state variable.
                            setState(CommandState.COMPLETE_STATE);
                        }
                        break;

                    default:
                        break;
                }
        }
    }
}
