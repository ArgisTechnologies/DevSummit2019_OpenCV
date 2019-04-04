// TODO: Header goes here

package com.logicpd.papapill.device.serial;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class coordinates writing out messages to the motor control board from
 * the tx queue.
 */
public class SerialWriter implements Runnable {

    private static final String TAG = "SerialWriter";

    private BlockingQueue<String> queue;
    private boolean run = true;
    private boolean cleared  = false;
    private String tmpCmd;
    private static final int TINYG_RXBUF_MAX = 254;
    public AtomicInteger buffer_available = new AtomicInteger(TINYG_RXBUF_MAX);
    private SerialDriver ser = SerialDriver.getInstance();
    private static final Object mutex = new Object();
    private static boolean throttled = false;

    public SerialWriter(BlockingQueue q) {
        this.queue = q;
    }

    /**
     * Reset our internal buffer available counter variable and notify our response parser class.
     * This may happen in cases where we need to SW reset the Tinyg.
     */
    public void resetBuffer() {
        buffer_available.set(TINYG_RXBUF_MAX);
        notifyAck();
    }

    /**
     * Clear our Tx queue buffer of all its contents. This needs to be called whenever
     * we want to cancel any planned moves on the Tinyg.
     */
    public void clearQueueBuffer() {
        // Clear the queue and remove all contents.
        queue.clear();
        // We set this to tell the mutex with waiting for an ack to send a line that it should not
        // send a line. We were asked to be cleared.
        this.cleared = true;
        try {
            // Since we cleared the queue, let us also set the buffer available back to max
            // as no more commands are queued to be sent.
            buffer_available.set(TINYG_RXBUF_MAX);
            // Clear are throttled state as there is no reason to be throttle anymore
            // with our queue cleared.
            this.setThrottled(false);
            this.notifyAck();
        } catch (Exception ex) {
            Log.e(TAG, "Exception when clearing queue buffer: " + ex);
        }
    }

    // Some getters and setters
    public boolean isRunning() { return run; }
    public void setRun(boolean run) { this.run = run; }

    // Methods to manage buffer available
    public int getMaxBufferValue() { return TINYG_RXBUF_MAX; }

    public synchronized int getBufferValue() { return buffer_available.get(); }
    public synchronized void setBuffer(int val) { buffer_available.set(val); }

    public synchronized void addBytesReturnedToBuffer(int lenBytesReturned) {
        buffer_available.set(getBufferValue() + lenBytesReturned);
    }

    /**
     * Add a command to our tx queue.
     * @param cmd
     */
    public void addCommandToBuffer(String cmd) {
        this.queue.add(cmd);
    }

    /**
     * A helper method that sets the throttled state.
     * @param t
     * @return
     */
    public boolean setThrottled(boolean t) {
        synchronized (mutex) {
            if (t == throttled) {
                Log.i(TAG, "Throttled is already set " + t);
                return false;
            }
            Log.i(TAG, "Setting Throttled: " + t);
            throttled = t;
        }
        return true;
    }

    /**
     * This is called by the response parser when a response packet is received. This
     * will wake up the mutex that is sleeping in the write method of the serial writer
     * (this) class.
     */
    public void notifyAck() {
        synchronized (mutex) {
            mutex.notify();
        }
    }

    /**
     * The write method calls the serial driver method that actually writes our message
     * to the motor control board. It also monitors the available buffer remaining on the
     * motor control board and throttles sending if we have reached its capacity.
     * @param str
     */
    public void write(String str) {
        try {
            synchronized (mutex) {

                if (str.length() > getBufferValue()) {
                    setThrottled(true);
                } else {
                    int oldbuffer = getBufferValue();
                    this.setBuffer(getBufferValue() - str.length());

                    String strippedStr = str.replaceAll("\\n", "");
                    Log.d("SerialWriter", String.format("Line to Write: " + strippedStr +
                            " Subtracting " + str.length() + " from buffer... Buffer was " +
                            oldbuffer + " is now " + getBufferValue()));
                }

                // If we detect we are throttled, see if we need to remain throttled (and wait)
                // or is it ok to break out of the throttled state to write the next message.
                // NOTE: This is a holdover from the very first demo where Motor control from
                // Raspberry Pi to TinyG was largely open loop. Commands were sent from Rpi to
                // TinyG as fast as possible to be queued in the TinyG's internal scheduler.
                // In such a system, there needed to be some way to prevent a barrage of messages
                // being sent at once such that TinyG's buffer was overrun. Now, in the current
                // system, we are no longer sending the "next" command until the previous command
                // has finished, thereby avoiding the need to throttle on the Rpi side. Thus, this
                // feature can probably be safely removed.
                while (throttled) {
                    if (str.length() > getBufferValue()) {
                        setThrottled(true);
                    } else {
                        setThrottled(false);
                        int oldbufferVal = getBufferValue();
                        buffer_available.set(getBufferValue() - str.length());

                        String strippedStr = str.replaceAll("\\n", "");
                        Log.d("SerialWriter", String.format("Line to Write: " + strippedStr +
                                " Subtracting " + str.length() + " from buffer... Buffer was " +
                                oldbufferVal + " is now " + getBufferValue()));
                        break;
                    }
                    // Wait here until an ack comes in to the response parser frees up some buffer
                    // space. Then unlock the mutex and write the next line.
                    mutex.wait();
                    if(cleared){
                        // Clear out the line we were waiting to send.
                        cleared = false;
                        return;
                    }
                }
            }
            // Finally call the serial driver method to write the message over uart.
            ser.write(str);
        } catch (InterruptedException ex) {
            Log.e("SerialWriter", "Error in SerialWriter write()");
        }
    }

    /**
     * The run method sits in a loop and continuously takes messages from our Tx queue
     * and sends them out to be written.
     */
    @Override
    public void run() {
        while (run) {
            try {
                // Retrieves and removes the head of the queue. If no element exists, the method
                // will block until an element becomes available.
                tmpCmd = queue.take();
                // Now call the write method on our retreived string.
                this.write(tmpCmd);
            } catch (Exception ex) {
                Log.i(TAG, "Exception in Run method: " + ex);
            }
        }
    }
}
