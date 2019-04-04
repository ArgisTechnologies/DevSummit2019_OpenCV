// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.models.PositionData;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.CoordinateManager;
import com.logicpd.papapill.interfaces.OnBinReadyListener;
import com.logicpd.papapill.interfaces.OnErrorListener;
import com.logicpd.papapill.interfaces.OnRotateCompleteListener;

import java.util.Locale;

public final class CommandRotateCarousel extends BaseCommand {

    // Identifier for this command.
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_ROTATE_CAROUSEL;

    // Internal parameters.
    private PositionData positionData = new PositionData();

    private int moveCount;
    private static final int MOVE_COUNT_MAX = 5;

    // Input parameters.
    private double targetDegrees;
    private double differenceThreshold;

    OnRotateCompleteListener mListener = null;

    public CommandRotateCarousel() {
        super(identifier);
    }

    /**
     * Registers a listener to this command.
     * @param listener
     */
    public void setListener(OnRotateCompleteListener listener) {
        mListener = listener;
    }

    /*
     * if there is a listener, callback to UI or business logic
     */
    protected void callback()
    {
        if(null!=mListener)
        {
            Log.d(name, "call back to UI");
            mListener.onCompleted();
            mListener = null;
        }
    }

    /**
     * This is the execute method for the rotate carousel command. The command takes an absolute
     * theta position (in degrees) and schedules a series of incremental motor moves. After each
     * incremental move it reads the absolute position sensor and compares the sensor reading from
     * the target position. The process is repeated until the difference is within the threshold.
     * @return
     */
    @Override
    public void execute() {

        switch (operation) {
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
                    targetDegrees = Double.parseDouble(paramArray[0]);
                    differenceThreshold = Double.parseDouble(paramArray[1]);
                    Log.d(name, String.format(Locale.US, " paramArray[0]: %s paramArray[1]: %s targetDegrees: %f differenceThreshold: %f", paramArray[0],paramArray[1],targetDegrees,differenceThreshold));

                    // Reset the number of times we've moved the carousel to 0.
                    moveCount = 0;

                    // Our first order of business is to read the absolute position sensor
                    // from the TinyG. We will use this value to calculate the step count
                    // and direction to move for the carousel motor. Doing so avoids having
                    // to home and then move the carousel motor every time we wish to move
                    // to a new bin location.

                    // Call the command to read the position data from the TinyG. We'll
                    // provide our internal position data holder object to store the
                    // returned data from the command.
                    CommandManager.getInstance().callCommand(
                            DeviceCommand.COMMAND_READ_POSITION, "", positionData);

                    // The command is now primed. Set the state to something other than the waiting
                    // state. This will cause command manager to switch the operation state of
                    // the command to run mode.
                    setState(CommandState.ROTATE_CAROUSEL_WAITING_FOR_READ_FINISH);
                }
                break;

            case OP_STATE_RUN:
                switch (state) {
                    case ROTATE_CAROUSEL_WAITING_FOR_READ_FINISH:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_READ_POSITION)) {

                            // When the command position query is finished, the result will be stored
                            // in our positionData object. Use the returned absolute position field
                            // to calculate our next carousel move.
                            double currentDegrees = positionData.getAbsoluteDegrees(true);

                            // If we are within the threshold, then we have sufficiently reached
                            // (or are already at) the destination. No need to continue moving.
                            if (Math.abs(targetDegrees - currentDegrees) < differenceThreshold) {

                                Log.d(name, "Destination theta reached in " + moveCount +
                                        " moves. Final diff: " + (targetDegrees - currentDegrees));

                                callback();
                                // Since there is no need to do anything else, mark the command as
                                // complete, then break immediately. Flow of execution will return
                                // to CommandManager which will in-turn recognize the command is
                                // complete and "reset" command state to the WAITING state.
                                setState(CommandState.COMPLETE_STATE);
                                break;
                            }

                            // If we've exceeded the move count limit, stop the command and mark it
                            // as "complete". This is to handle the theoretical case where the motor
                            // continues to overshoot the destination by a distance higher than the
                            // difference threshold, hence rotating back and forth indefinitely. This
                            // scenario can happen if the difference threshold is sufficiently small
                            // /smaller than the sensor's max linearization error.
                            if (moveCount > MOVE_COUNT_MAX) {

                                Log.d(name, "Max moves has been reached. Final diff: " +
                                        (targetDegrees - currentDegrees));

                                callback();

                                // We've exceeded the max amount of motor moves for this command but
                                // still have not reached our destination within the specified error
                                // threshold. Either the passed in threshold is too low, meaning we
                                // are getting as close as we can but are being hampered by noise or
                                // sensor's non-linearity error. Or something bad happened, causing
                                // us to repeatedly miss our target. In either case, if we continue,
                                // we will likely get stuck in an infinite loop. Thus, break here.
                                // Flow of execution will return to CommandManager which will in-turn
                                // recognize the command is complete and "reset" command state to the
                                // WAITING state. In the future, this "exception" case should be
                                // handled differently to allow for a retry or recovery path.
                                setState(CommandState.COMPLETE_STATE);
                                break;
                            }

                            // Calculate the shortest angle required to move from the current
                            // position to destination.
                            double degreesToMove = CoordinateManager
                                    .getShortestAngle(currentDegrees, targetDegrees);

                            Log.d(name, String.format("Current: %f, Target: %f, Move: %f",
                                    currentDegrees, targetDegrees, degreesToMove));

                            String params = String.format("a %f 1000", degreesToMove);

                            // Now send the command to move the carousel the calculated number of
                            // degrees to get to the requested bin.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_MOTOR_MOVE, params, null);

                            // Advance command state variable.
                            setState(CommandState.ROTATE_CAROUSEL_WAITING_FOR_MOVE_FINISH);
                        }
                        break;

                    case ROTATE_CAROUSEL_WAITING_FOR_MOVE_FINISH:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_MOVE)) {

                            // Increment move counter to keep track of how many times we've
                            // moved the carousel motor.
                            moveCount++;

                            // Read from the position sensor again to confirm we arrived at our
                            // destination, or if another move is required.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_READ_POSITION, "", positionData);

                            // Set command state variable.
                            setState(CommandState.ROTATE_CAROUSEL_WAITING_FOR_READ_FINISH);
                        }
                        break;
                }
                break;

            default:
                break;
        }
    }
}
