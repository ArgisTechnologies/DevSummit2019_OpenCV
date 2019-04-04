// TODO: Header goes here

package com.logicpd.papapill.device.serial;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.logicpd.papapill.device.tinyg.BoardDefaults;
import com.logicpd.papapill.device.tinyg.TinyGDriver;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

/**
 * This class is responsible for low(er) level serial communications, utilizing the android things'
 * peripheral IO's UART API. Basic reads and writes to the serial comm port is done here.
 */
public class SerialDriver {

    private static final String TAG = "SerialDriver";

    private HandlerThread mInputThread;
    private Handler mInputHandler;

    private UartDevice mUartDevice;

    private static byte[] lineBuffer = new byte[1024];
    private static int lineIdx = 0;

    private Runnable mTransferUartRunnable = new Runnable() {
        @Override
        public void run() {
            readUartLine();
        }
    };

    /**
     * Singleton pattern.
     */
    private SerialDriver() { }

    public static SerialDriver getInstance() {
        return SerialDriver.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final SerialDriver INSTANCE = new SerialDriver();
    }

    /**
     * The callback that runs when UART data is available. Uses android things PIO API.
     * Will simply call the helper function to read the incoming line received over UART.
     */
    private UartDeviceCallback mCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Queue up a data transfer.
            readUartLine();
            // Continue listening for more interrupts.
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };

    public boolean initialize() {
        Log.d(TAG, "UART Open");

        // Create a background looper thread for I/O.
        mInputThread = new HandlerThread("InputThread");
        mInputThread.start();
        mInputHandler = new Handler(mInputThread.getLooper());

        // Attempt to access the UART device.
        try {
            openUart(BoardDefaults.UART_DEVICE_NAME, BoardDefaults.UART_BAUD_RATE);
            // Read any initially buffered data.
            mInputHandler.post(mTransferUartRunnable);
        } catch (IOException e) {
            Log.e(TAG, "Unable to open UART device", e);
        }
        return true;
    }

    public synchronized void disconnect() {
        Log.d(TAG, "UART Close");

        // Terminate the worker thread
        if (mInputThread != null) {
            mInputThread.quitSafely();
        }

        // Attempt to close the UART device
        try {
            closeUart();
        } catch (IOException e) {
            Log.e(TAG, "Error closing UART device:", e);
        }
    }

    private void openUart(String name, int baudRate) throws IOException {

        // Call from Android Things API peripheral manager.
        mUartDevice = PeripheralManager.getInstance().openUartDevice(name);

        // Configure the UART.
        mUartDevice.setBaudrate(baudRate);
        mUartDevice.setDataSize(BoardDefaults.UART_DATA_BITS);
        mUartDevice.setParity(UartDevice.PARITY_NONE);
        mUartDevice.setStopBits(BoardDefaults.UART_STOP_BITS);

        // Register device callback.
        mUartDevice.registerUartDeviceCallback(mInputHandler, mCallback);
    }

    private void closeUart() throws IOException {
        if (mUartDevice != null) {
            mUartDevice.unregisterUartDeviceCallback(mCallback);
            try {
                mUartDevice.close();
            } finally {
                mUartDevice = null;
            }
        }
    }

    /**
     * Transmit a string over serial.
     * @param str
     */
    public void write(String str) {
        try {
            byte[] buffer = str.getBytes("UTF-8");
            int count = mUartDevice.write(buffer, buffer.length);
        } catch (Exception e) {
            Log.e(TAG, "Exception in write: " + e);
        }
    }

    /**
     * Transmit a single byte over serial.
     * @param b
     */
    public void writeByte(Byte b) {
        try {
            byte[] buffer = {b};
            int count = mUartDevice.write(buffer, buffer.length);
        } catch (Exception e) {
            Log.e(TAG, "Exception in write byte: " + e);
        }
    }

    /**
     * Read a line from the serial RX buffer.
     */
    private void readUartLine() {
        if (mUartDevice == null)
            return;

        byte[] chunkBuffer = new byte[255];
        int bytesRead;

        try {
            while ((bytesRead = mUartDevice.read(chunkBuffer, chunkBuffer.length)) > 0) {
                for (int i = 0; i < bytesRead; i++) {
                    // Look for the carriage return or new line characters.
                    if (chunkBuffer[i] == 0xA || chunkBuffer[i] == 0xD) {
                        // Don't read anymore bytes as we now have a line.
                        final String f = new String(lineBuffer, 0, lineIdx);
                        if (!f.equals("")) {
                            // Add the line to our internal Rx queue to process.
                            TinyGDriver.getInstance().appendRxQueue(f);
                        }
                        lineIdx = 0;
                    } else {
                        // Else keep adding characters to our line buffer.
                        lineBuffer[lineIdx++] = chunkBuffer[i];
                    }
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Unable to read line from UART", e);
        }
    }
}
