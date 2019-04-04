// TODO: Header goes here

package com.logicpd.papapill.device.tinyg.commands;

import android.util.Log;

import com.logicpd.papapill.data.AppConfig;
import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.gpio.LightringManager;
import com.logicpd.papapill.device.models.PickupData;
import com.logicpd.papapill.device.models.PositionData;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.CoordinateManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnBinReadyListener;
import com.logicpd.papapill.computervision.BinCamera;
import com.logicpd.papapill.computervision.detection.DetectionData;
import com.logicpd.papapill.interfaces.OnErrorListener;

public final class CommandFullDispense extends BaseCommand {

    // Identifier for this command.
    private static final DeviceCommand identifier = DeviceCommand.COMMAND_FULL_DISPENSE;

    // Internal parameters.
    private int binId;
    private double fullRadiusMove;

    // Data objects to pass to other sub commands.
    private PickupData pickupData = new PickupData();
    private PositionData positionData = new PositionData();

    // Holds coordinates generated from OpenCV Pill Detection algorithm.
    private DetectionData detectionData = new DetectionData();
    private byte[] binImage;

    // Listener registered to this command.
    protected OnBinReadyListener mListener = null;
    protected OnErrorListener mErrListener = null;

    public CommandFullDispense() {
        // Pass our identifier into the super's constructor. This will associate the
        // identifier with the command object. The identifier is a final property of
        // super so it can only be assigned during super's construction.
        super(identifier);
    }

    /**
     * Registers a listener to this command.
     * @param listener
     */
    public void setListeners(OnBinReadyListener listener,
                             OnErrorListener errListener) {
        mListener = listener;
        mErrListener = errListener;
    }

    public OnErrorListener getErrListener()
    {
        return mErrListener;
    }

    public void binImageDone(byte[] binImage) {
        // Save the image
        this.binImage = binImage;
        // Advance the state
        setState(CommandState.FULL_DISPENSE_CAMERA_PICTURE_DONE);
    }

    /**
     * This is the core command of the entire dispense system. It contains all the
     * logic required to ultimately dispense a pill from a bin. All other command
     * can be thought of as sub commands to the full dispense command, as somewhere
     * along the line, we'll make a call to all existing existing commands from full
     * dispense. It performs the initial carousel and r-stage movement to position
     * the camera to take a snapshot of the bin we want to dispense from. It will
     * then call into the pill recognition algorithm to determine where exactly to
     * position the pickup head. Once positioned over a suspected pill location, it
     * will lower the pickup head in an attempt to grab the pill. If the grab was
     * successful, we will dispense the pill into the dispense bin. If the grab was
     * not successful, there are a number of recovery sequences from most desired to
     * least desired to execute in an attempt to try again.
     */
    @Override
    public void execute() {

        switch (operation) {
            case OP_STATE_START:
                // Convert the parameter string that was passed into individual arguments
                // separate by spaces.
                String[] paramArray = getParamArray();
                // Full dispense is expected to have only 1 parameter (the bin Id). Ensure
                // we have that amount before continuing (ignore any additional parameters).
                if (paramArray.length < 1) {
                    String msg = "Command failed due to too few or bad parameters.";
                    Log.e(name, msg);

                    if(null!=mErrListener) {
                        mErrListener.onBadParams(msg);
                    }
                    return;
                } else {
                    // TODO: Must validate these parameters.
                    binId = Integer.parseInt(paramArray[0]);

                    // TODO: Perform some limit checking here.

                    // Turn on the light ring.
                    LightringManager.getInstance().configureOutput(true);

                    // Kickoff our sequence by calling our first sub command.
                    CommandManager.getInstance().callCommand(
                            DeviceCommand.COMMAND_MOTOR_HOME, "z", null);

                    // Then advance our command's state. This will cause the command manager
                    // thread to advance the operational state of the command to OP_STATE_RUN.
                    // It is important to note that the state of the command is advanced AFTER
                    // calling the sub command (in this case motor home). In general, modifying
                    // the command state should be done last. This ensure that there can be no
                    // race conditions between the main thread calling the dispense function and
                    // the command manager thread which advances the command's state machine.
                    // In this example, had we advanced the command state PRIOR to calling the
                    // sub command, we end up with a small window of time before the motor home
                    // command is finished executing where the full dispense command can be re-
                    // executed by the command manager's run() method. This can cause unwanted
                    // behavior (in this particular example, it causes us to think the homing
                    // command is done where in reality in hasn't even started yet). A better
                    // way to handle this would be to put a lock on each execute method so that
                    // only one thread can access it at a given time. We can also reorder the
                    // command priority when creating the command structure in command manager
                    // so that sub commands are always serviced before commands that call them.
                    // TODO: Investigate better ways to handle this.
                    setState(CommandState.FULL_DISPENSE_WAITING_FOR_Z_HOME);
                }
                break;

            case OP_STATE_RUN:
                switch (state) {
                    case FULL_DISPENSE_WAITING_FOR_Z_HOME:

                        // We have liftoff! Now the first thing we have to do is wait for the z
                        // axis to finish its homing move.
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_HOME)) {

                            // Now call the same homing command as before, except with an updated
                            // parameter this time around.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_MOTOR_HOME, "x", null);

                            // The z axis is homed and the pickup head is now raised up all the
                            // way so as to not interfere with the other axes. Our next action is
                            // to home the x axis (the radial-stage). Call the command and advance
                            // to our next state.
                            setState(CommandState.FULL_DISPENSE_WAITING_FOR_R_HOME);
                        }
                        break;

                    case FULL_DISPENSE_WAITING_FOR_R_HOME:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_HOME)) {

                            // Get the bin location from the passed in binId.
                            double targetPosition = CoordinateManager.getBinOffset(binId);
                            String params = String.format("%f 0.3", targetPosition);

                            // Once both Z and Rho are homed, its now safe to rotate the carousel
                            // to the proper bin location.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_ROTATE_CAROUSEL, params, null);

                            // Advance our state variable.
                            setState(CommandState.FULL_DISPENSE_WAITING_FOR_CAROUSEL_MOVE);
                        }
                        break;

                    case FULL_DISPENSE_WAITING_FOR_CAROUSEL_MOVE:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_ROTATE_CAROUSEL)) {

                            // Now center the camera in the middle of the bin.
                            // NOTE: This command is currently disabled and thus the state is
                            // effectively a NOP. In the current alpha breadboard, homing r and
                            // theta already puts us at the optimal camera position to take a
                            // picture of the bin. This may not be the case in the future.
                            //CommandManager.getInstance().callCommand(
                            //DeviceCommand.COMMAND_MOTOR_MOVE, "x 25 2000", null);

                            // Advance our state variable.
                            setState(CommandState.FULL_DISPENSE_WAITING_FOR_CAMERA_CENTERED);
                        }
                        break;

                    case FULL_DISPENSE_WAITING_FOR_CAMERA_CENTERED:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_MOVE)) {

                            // Register the callback to call when OpenCV pill detection algorithm
                            // if finished analyzing the image.
                            // OpenCvClient.setOnCompleteListener...
                            // In the callback:
                            // - Save returned coordinates
                            // - Advance state variable

                            // Now start the pill detection sequence by taking a picture.
                            BinCamera.getInstance().takePicture();

                            // Advance our state variable.
                            setState(CommandState.FULL_DISPENSE_WAITING_FOR_CAMERA_PICTURE);
                        }
                        break;

                    case FULL_DISPENSE_WAITING_FOR_CAMERA_PICTURE:
                        // Do nothing until the OpenCV pill detection algorithm is finished. This
                        // ensures we are not performing any actions which may interfere with the
                        // algorithm routine.
                        break;

                    case FULL_DISPENSE_CAMERA_PICTURE_DONE:

                        /*
                         * circular dependency here from TinyGDriver ?
                         */
                        detectionData = TinyGDriver.getInstance().getVisualizer().processImage(binImage);
                        if(null == detectionData ||
                                null == detectionData.getSelectedPills() ||
                                detectionData.getSelectedPills().isEmpty())
                        {
                            /*
                             * vision failed to detect.
                             * - motor home and report error.
                             */
                            setState(CommandState.FULL_DISPENSE_WAIT_FOR_VISION_ERROR_DISPLAY);
                            break;
                        }

                        Log.d(name, "Finished mVisualizer process image");
                        Log.d(name, String.format("r = %f, theta = %f, z = %f, mmZ = %f",
                                detectionData.getSelectedPills().get(0).getCoordinateData().radius,
                                detectionData.getSelectedPills().get(0).getCoordinateData().degrees,
                                detectionData.getSelectedPills().get(0).getCoordinateData().z,
                                detectionData.getSelectedPills().get(0).getCoordinateData().mmZ));

                        // Copy mmz to pickup data to be used later in fast z move.
                        pickupData.mmz = detectionData.getSelectedPills().get(0).getCoordinateData().mmZ;

                        // Use the coordinates returned from OpenCV pill detection algorithm to
                        // reposition the pickup head over the nearest/best pill. Reposition the
                        // r-axis first.
                        fullRadiusMove = detectionData.getSelectedPills().get(0).getCoordinateData().radius + AppConfig.getInstance().camera2TinyGRhoOffset;
                        String cmdReposR = String.format("x %f 2000", fullRadiusMove);

                        CommandManager.getInstance().callCommand(
                                DeviceCommand.COMMAND_MOTOR_MOVE, cmdReposR, null);

                        // Advance our state variable.
                        setState(CommandState.FULL_DISPENSE_WAITING_FOR_R_REPOSITION);

                        break;

                    case FULL_DISPENSE_WAITING_FOR_R_REPOSITION:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_MOVE)) {

                            // Read from the position sensor one more time to find our current
                            // absolute theta position.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_READ_POSITION, "", positionData);

                            // Advance our state variable.
                            setState(CommandState.FULL_DISPENSE_WAITING_FOR_READ_POSITION);
                        }
                        break;

                    case FULL_DISPENSE_WAITING_FOR_READ_POSITION:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_READ_POSITION)) {

                            // R-axis has been repositioned, now reposition theta-axis to the
                            // target (absolute) position, which is the current absolute position
                            // minus the pill's theta offset + a camera-pickup nozzle offset.
                            double targetPosition = positionData.getAbsoluteDegrees(true)
                                    - detectionData.getSelectedPills().get(0).getCoordinateData().degrees
                                    + AppConfig.getInstance().camera2TinyGThetaOffset;

                            String params = String.format("%f 0.3", targetPosition);

                            // Call the Rotate Carousel command to accurately position the pickup
                            // head to within 0.3 degrees of the pill's (absolute) theta location.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_ROTATE_CAROUSEL, params, null);

                            // Advance our state variable.
                            setState(CommandState.FULL_DISPENSE_WAITING_FOR_THETA_REPOSITION);
                        }
                        break;

                    case FULL_DISPENSE_WAITING_FOR_THETA_REPOSITION:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_ROTATE_CAROUSEL)) {

                            // Both R and Theta has been repositioned. The pickup head is now
                            // supposed to be directly over a pill. Time to pick it up.
                            CommandManager.getInstance().callCommand(
                                    DeviceCommand.COMMAND_MOTOR_PICKUP, "", pickupData);

                            // Advance our state variable.
                            setState(CommandState.FULL_DISPENSE_MOVING_TO_PICKUP_PILL);
                        }
                        break;

                    case FULL_DISPENSE_MOVING_TO_PICKUP_PILL:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_PICKUP)) {

                            // Check the result of the pickup command.
                            if (pickupData.success) {
                                // Calculate the remaining steps required to move X stage to the
                                // dispense bin to drop the pill.
                                double remainingMove = CoordinateManager.DISPENSE_BIN_OFFSET - fullRadiusMove;

                                String moveToDispenseParams = String.format("x %f 2000", remainingMove);

                                // Move it.
                                CommandManager.getInstance().callCommand(
                                        DeviceCommand.COMMAND_MOTOR_MOVE, moveToDispenseParams, null);

                                // Advance our state variable.
                                setState(CommandState.FULL_DISPENSE_WAIT_FOR_DISPENSE_CONFIRMATION);

                            } else {
                                // Otherwise, the pickup command indicates failed. Here depending
                                // on the pill type, we can try to perform a nudge or jog as a retry.
                                // This doesn't require a full homing of the pickup head as only a
                                // slight raise is required. If retries don't work after some time,
                                // we may need call the pickup command with a new location. Or
                                // possibly perform a "carousel jostle" and retake the picture.
                                // TODO: Find optimal retry strategy. For now, do the same as above.
                                // For now, simply do nothing and advance our state.

                                // we hit a limit at this time --> so just reset for now (maybe do above later)
                                setState(CommandState.FULL_DISPENSE_WAIT_FOR_PICK_ERROR_DISPLAY);
                            }
                        }
                        break;

                    case FULL_DISPENSE_WAIT_FOR_DISPENSE_CONFIRMATION:
                        if (CommandManager.getInstance().isCommandDone(
                                DeviceCommand.COMMAND_MOTOR_MOVE)) {

                            // Turn off vacuum pump.
                            TinyGDriver.getInstance().write(CommandBuilder.CMD_COOLANT_OFF);

                            // Turn off the light ring.
                            LightringManager.getInstance().configureOutput(false);

                            // TODO: This is where we could take a follow up image to confirm dispense.
                            Log.d("CommandFullDispense", "onBinReady()");
                            mListener.onBinReady();

                            // Mark our command as complete.
                            setState(CommandState.COMPLETE_STATE);
                        }
                        break;

                    case FULL_DISPENSE_WAIT_FOR_VISION_ERROR_DISPLAY:
                        // Turn off vacuum pump.
                        TinyGDriver.getInstance().write(CommandBuilder.CMD_COOLANT_OFF);

                        // Turn off the light ring.
                        LightringManager.getInstance().configureOutput(false);

                        Log.d("CommandFullDispense", "onVisionError()");
                        if(null!=mErrListener)
                            mErrListener.onVisionError();

                        setState(CommandState.COMPLETE_STATE);
                        break;

                    case FULL_DISPENSE_WAIT_FOR_PICK_ERROR_DISPLAY:
                        // Turn off vacuum pump.
                        TinyGDriver.getInstance().write(CommandBuilder.CMD_COOLANT_OFF);

                        // Turn off the light ring.
                        LightringManager.getInstance().configureOutput(false);

                        // it will hit a limit error if not picked
                        Log.d("CommandFullDispense", "onLimitError()");
                        if(null!=mErrListener)
                            mErrListener.onLimitError();

                        setState(CommandState.COMPLETE_STATE);
                        break;
                }
                break;

            default:
                mErrListener = null;
                mListener = null;
                break;
        }
    }
}
