// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.enums.ResponseFooterIndex;
import com.logicpd.papapill.device.enums.TinyGStatusCode;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.MnemonicManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This command is specifically used to set a configuration setting on the TinyG.
 * FUTURE: This command likely won't be required in the future as we will eventually
 * pre-program the TinyG EEPROM / settings with exactly the values that our system
 * wants, eliminating the need to do this from the main controller board. As such,
 * some of the logic in this command is intentionally left the way it is (such as
 * checking the response footer directly).
 */
public final class CommandSetConfig extends BaseCommand {

    private static final DeviceCommand identifier = DeviceCommand.COMMAND_SET_CONFIG;

    private String cmd;

    public CommandSetConfig() {
        super(identifier);
    }

    @Override
    public void execute() {
        switch(operation) {
            case OP_STATE_START:
                // Separate the parameters via whitespace regex.
                String[] paramArray = getParamArray();
                // This command is expected to have 2 parameters. Ensure we have
                // that amount before continuing (we'll ignore any additional parameters).
                if (paramArray.length < 2) {
                    Log.e(name, "Command failed due to too few or bad parameters.");
                    return;
                } else {
                    // Assign our parameters.
                    String settingName = paramArray[0];
                    String settingValue = paramArray[1];

                    // Locally build up the JSON command.
                    cmd = CommandBuilder.CMD_SET_CONFIG(settingName, settingValue);

                    // Change our response Id to match the response format we expect.
                    setResponseId ("{\"r\":{\"" + settingName);
                    setResponseId ( getResponseId().replaceAll("\\n", ""));

                    Log.d(name, "Setting response Id to: " + getResponseId());

                    // Actually send the command to the TinyG over serial.
                    TinyGDriver.getInstance().write(cmd);

                    // Set our state to wait for a response.
                    setState(CommandState.WAITING_FOR_RESPONSE);
                }
                break;

            case OP_STATE_RUN:
                switch(state) {
                    case WAITING_FOR_RESPONSE:
                        // We wait here until the response parser receives a response and notifies
                        // us here by updating the response variable.
                        if (!response.isEmpty()) {
                            try {
                                // Now do some parsing.
                                JSONObject js = new JSONObject(response);
                                if(js.has(MnemonicManager.MNEMONIC_JSON_FOOTER)) {

                                    // Grab the footer portion of the response.
                                    JSONArray footer = js.getJSONArray(MnemonicManager
                                            .MNEMONIC_JSON_FOOTER);

                                    // We care about the status code, which is the 2nd element in
                                    // the footer array (index 1).
                                    TinyGStatusCode statusCode = TinyGStatusCode.valueOf(footer
                                            .getInt(ResponseFooterIndex.INDEX_STATUS_CODE.getValue()));

                                    // Check to see if the status code is 0 (OK). If not, send the
                                    // command again.
                                    if (statusCode == TinyGStatusCode.OK) {
                                        setState(CommandState.COMPLETE_STATE);
                                    } else {
                                        Log.e(name, "Status Code not ok. Retrying command: " + cmd);
                                        TinyGDriver.getInstance().write(cmd);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(name, "Exception: " + e);
                            }
                        }
                        break;
                }
                break;

            default:
                break;
        }
    }
}
