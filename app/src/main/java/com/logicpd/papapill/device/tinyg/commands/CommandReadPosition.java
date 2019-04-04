// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.models.PositionData;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnPositionReadyListener;

import org.json.JSONException;
import org.json.JSONObject;

public final class CommandReadPosition extends BaseCommand {

    // Identifier for this command.
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_READ_POSITION;

    // Object to hold position data.
    public PositionData positionData;

    // Listener registered to this command.
    OnPositionReadyListener mListener = new OnPositionReadyListener() {
        @Override
        public void onPositionReady(String updateText) {
            // Initialize as an empty method.
        }
    };

    public CommandReadPosition() {
        super(identifier);
    }

    /**
     * Registers a listener to this command.
     * @param listener
     */
    public void setOnPositionReadyListener(OnPositionReadyListener listener) {
        mListener = listener;
    }

    /**
     * Read motor axes positions.
     */
    @Override
    public void execute() {

        switch (operation) {
            case OP_STATE_START:
                // Grab the data object that was passed into the command and make sure it is
                // not null. We can't complete execution if we don't have a place to put our
                // result data.
                if (data instanceof PositionData) {
                    positionData = (PositionData) data;
                } else {
                    Log.e(name, "Command failed. Data argument is wrong type or null.");
                    return;
                }

                // Now go ahead and query the position from the TinyG.
                TinyGDriver.getInstance().write(CommandBuilder.CMD_QUERY_POSITION);

                // Mark this command as no longer idle.
                setState(CommandState.WAITING_FOR_RESPONSE);
                break;

            case OP_STATE_RUN:
                switch (state) {
                    case WAITING_FOR_RESPONSE:
                        // Now we must wait for the response parser thread to receive a response
                        // from the TinyG. When it does, it will call command manager's bind
                        // response method which will match the response with the command. If the
                        // response was ours, we will see our response variable become populated.
                        if (!response.isEmpty()) {
                            try {
                                // Convert our response string to JSON.
                                JSONObject responseJson = new JSONObject(response);

                                // Parse the JSON response which populates the data object with the
                                // results of the command. If the parse was successful, we're done.
                                if(positionData.parseResponse(responseJson)) {
                                    Log.i(name, String.format("Parsed positions: x:%f z:%f a:%f abs(raw):%d abs(deg):%f ",
                                            positionData.x, positionData.z, positionData.a, positionData.abs, positionData.getAbsoluteDegrees(false)));

                                    mListener.onPositionReady(String.format("%d (%.2f deg)",
                                            positionData.abs, positionData.getAbsoluteDegrees(false)));

                                    setState(CommandState.COMPLETE_STATE);
                                }
                            } catch (JSONException ex) {
                                Log.e(name, "Exception while parsing response: " + ex);
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