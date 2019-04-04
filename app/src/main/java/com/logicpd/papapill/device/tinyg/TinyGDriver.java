package com.logicpd.papapill.device.tinyg;

import android.util.Log;

import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.serial.ResponseParser;
import com.logicpd.papapill.device.serial.SerialDriver;
import com.logicpd.papapill.device.serial.SerialWriter;
import com.logicpd.papapill.device.tinyg.commands.CommandFullDispense;
import com.logicpd.papapill.device.tinyg.commands.CommandReadPosition;
import com.logicpd.papapill.device.tinyg.commands.CommandRotateCarousel;
import com.logicpd.papapill.interfaces.OnBinReadyListener;
import com.logicpd.papapill.interfaces.OnErrorListener;
import com.logicpd.papapill.interfaces.OnLimitSwitch;
import com.logicpd.papapill.interfaces.OnPositionReadyListener;
import com.logicpd.papapill.computervision.detection.Detector;
import com.logicpd.papapill.interfaces.OnRotateCompleteListener;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * This class handles interfacing with the TinyG motor control board.
 */
public class TinyGDriver implements OnLimitSwitch
{
    public String TAG;
    public static ArrayBlockingQueue<String> txQueue = new ArrayBlockingQueue<>(50000);
    public static ArrayBlockingQueue<String> rxQueue = new ArrayBlockingQueue<>(10000);

    private final SerialDriver serialDriver = SerialDriver.getInstance();
    public ResponseParser responseParser = new ResponseParser(this);
    public SerialWriter serialWriter = new SerialWriter(txQueue);
    public CommandManager commandManager = CommandManager.getInstance();
    public MachineManager machineManager = MachineManager.getInstance();
    private Detector mVisualizer = null;
    private OnErrorListener mErrListener;

    public Detector getVisualizer()
    {
        if(null==mVisualizer)
            mVisualizer = new Detector();

        return mVisualizer;
    }

    public void setErrListener(OnErrorListener listener)
    {
        mErrListener = listener;
    }

    /*
     * Emergency switch -- min/max limit has been hit
     */
    public void onMotorError(boolean hasContext)
    {
        // call command reset -- has has been reported
        Log.d("onMotorError", "invoke reset here");
        reset();

        if(!hasContext && null!=mErrListener)
        {
            // launch Error screen
            Log.e("onMotorError", "launch error screen");
            mErrListener.onLimitError();
        }
    }

    public void reset()
    {
        commandManager.callCommand(DeviceCommand.COMMAND_RESET, "", null);
    }


    /**
     * This class is a lazy-loaded singleton. It follows the intialization-on-demand holder idiom.
     * See: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private TinyGDriver()
    {
        TAG = this.getClass().getName();
    }

    private static class LazyHolder {
        private static final TinyGDriver INSTANCE = new TinyGDriver();
    }

    public static TinyGDriver getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Opens the serial driver connection. Start our running threads. Configure the TinyG.
     * @return
     */
    public boolean initialize() {

        // Create and start our threads.
        Thread commandThread = new Thread(commandManager);
        commandThread.setName("CommandManager");
        commandThread.setDaemon(true);
        commandThread.start();

        Thread serialWriterThread = new Thread(serialWriter);
        serialWriterThread.setName("SerialWriter");
        serialWriterThread.setDaemon(true);
        serialWriterThread.start();

        Thread responseParserThread = new Thread(responseParser);
        responseParserThread.setName("ResponseParser");
        responseParserThread.setDaemon(true);
        responseParserThread.start();

        // Connect to the serial port.
        if (this.serialDriver.initialize()) {
            // On BBv2, it seems the TinyG will not respond to commands after a power on reset
            // unless an initial carriage return is transmitted to "wake up" the UART comms. This
            // behavior was not seen on BBv1. Perhaps this is a side effect of switching from UART
            // over USB to a direct UART?
            priorityWriteByte((byte)0xD);
            // Send initial configuration settings to TinyG.
            this.doTinygInit();
            return true;
        }
        return false;
    }

    /**
     * Add an entry to the Rx queue. Response parser thread handles parsing JSON objects in queue.
     * @param line
     */
    public synchronized void appendRxQueue(String line) {
        TinyGDriver.rxQueue.add(line);
    }

    /**
     * Send a single fully formed message to the TinyG. This method will call the
     * SerialWriter method to add msg to queue.
     * @param msg
     * @throws java.lang.Exception
     */
    public synchronized void write(String msg) {
        TinyGDriver.getInstance().serialWriter.addCommandToBuffer(msg);
    }

    /**
     * Send a single character message to the TinyG. This method will call the
     * SerialDriver write method to bypass the message queue.
     * @param b
     * @throws Exception
     */
    public void priorityWriteByte(Byte b) {
        TinyGDriver.getInstance().serialDriver.writeByte(b);
    }

    /**
     * Sends the ^x character to the TinyG to trigger SW reset.
     * @throws Exception
     */
    public void softwareReset() {
        priorityWriteByte(CommandBuilder.CMD_APPLY_RESET);
    }

    /**
     * Registers a listener to the full dispense command. The command will execute the
     * listener's callback when it completes.
     * @param listener
     */

    public void setDispenseListeners(OnBinReadyListener listener,
                                     OnErrorListener visionErrListener)
    {
        CommandFullDispense commandFullDispense = (CommandFullDispense) commandManager.getCommand(
                DeviceCommand.COMMAND_FULL_DISPENSE);
        if (commandFullDispense != null) {
            commandFullDispense.setListeners(listener, visionErrListener);
        }
    }

    /*
     * Register a listener to the rotate command.
     * Callback to requestor when ready.
     * @param listener
     */
    public void setMoveBinListener(OnRotateCompleteListener listener)
    {
        CommandRotateCarousel command = (CommandRotateCarousel) commandManager.getCommand(
                DeviceCommand.COMMAND_ROTATE_CAROUSEL);
        if (command != null) {
            command.setListener(listener);
        }
    }
    /**
     * Registers a listener to the read position command. The command will execute the
     * listener's callback when it completes.
     * @param listener
     */
    public void setOnPositionReadyListener(OnPositionReadyListener listener) {
        CommandReadPosition commandReadPosition = (CommandReadPosition) commandManager.getCommand(
                DeviceCommand.COMMAND_READ_POSITION);
        if (commandReadPosition != null) {
            commandReadPosition.setOnPositionReadyListener(listener);
        }
    }

    public void binImageDone(byte[] binImage) {
        CommandFullDispense commandFullDispense = (CommandFullDispense) commandManager.getCommand(
                DeviceCommand.COMMAND_FULL_DISPENSE);
        if (commandFullDispense != null) {
            commandFullDispense.binImageDone(binImage);
        }
    }

    /**
     * Configures the TinyG with default settings.
     */
    public void doTinygInit() {
        commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_INIT, "", null);
    }

    /**
     * Fully dispense a pill from a bin number.
     * @param binId
     */
    public void doFullDispense(int binId) {
        String param = String.format("%s", binId);
        commandManager.callCommand(DeviceCommand.COMMAND_FULL_DISPENSE, param, null);
    }

    public void doMoveBin4User(int binId) {
        /*
         * Current bin index from extrusion (unused bin) is arbitrary.
         * Access door location is also not final.
         */
        double targetPosition = CoordinateManager.getDoorOffset(binId);
        String param = String.format("%f 0.3", targetPosition);
        Log.d(TAG, String.format("binId: %d targetPosition: %f param: %s", binId, targetPosition, param));
        commandManager.callCommand(DeviceCommand.COMMAND_ROTATE_CAROUSEL, param, null);
    }
}
