// TODO: Header goes here

package com.logicpd.papapill.device.serial;

import android.util.Log;

import com.logicpd.papapill.device.enums.ResponseFooterIndex;
import com.logicpd.papapill.device.enums.TinyGStatusCode;
import com.logicpd.papapill.device.tinyg.CommandManager;
import com.logicpd.papapill.device.tinyg.MnemonicManager;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnErrorListener;
import com.logicpd.papapill.interfaces.OnLimitSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handles parsing of messages sent by the Tinyg in response to gcode / config commands.
 */
public class ResponseParser implements Runnable {

    private static final String TAG = "ResponseParser";
    private boolean run = true;

    // Footer holder class
    public ResponseFooter responseFooter = new ResponseFooter();

    private String line;
    private OnLimitSwitch mLimitListener;

    public ResponseParser(OnLimitSwitch listener)
    {
        mLimitListener = listener;
    }

    // Some getters and setters
    public boolean isRunning() { return run; }
    public void setRun(boolean run) { this.run = run; }

    /**
     * Parse the footer section of the Tinyg JSON response. Here is where the RX_RECV field in the
     * footer is used to return bytes to our running Rx buffer remaining variable.
     * @param footerValues
     * @param line
     */
    private void parseFooter(JSONArray footerValues, String line) {
        try {
            // Parse our footer values and populate our footer holder.
            responseFooter.setProtocolVersion(footerValues.getInt(ResponseFooterIndex.INDEX_PROTOCOL_VERSION.getValue()));
            responseFooter.setStatusCode(footerValues.getInt(ResponseFooterIndex.INDEX_STATUS_CODE.getValue()));
            responseFooter.setRxRecvd(footerValues.getInt(ResponseFooterIndex.INDEX_RX_RECVD.getValue()));
            responseFooter.setCheckSum(footerValues.getInt(ResponseFooterIndex.INDEX_CHECKSUM.getValue()));

            // This is the status code from the TinyG's response to our command. If we should
            // need to do any error handling in response to status codes, we are now able to do so.
            // TODO: Currently, all we're doing is printing out the status code to the log trace and
            // nothing else. If a command hangs, we'll at least know why it failed. But, there is a
            // general lack of error handling across the board providing path to recovery in the
            // case of errors like bad status codes or exceptions. What little of it exists now
            // is as-needed on a case-by-case basis.
            if (responseFooter.getStatusCode() != TinyGStatusCode.OK) {
                Log.e(TAG, "Error code in TinyG Response: " + responseFooter.getStatusCode().getValue()
                        + " - " + responseFooter.getStatusCode().toString());
            }

            // Save our current buffer state before attempting to return bytes.
            int beforeBytesReturned = TinyGDriver.getInstance().serialWriter.getBufferValue();

            // If for some reason the buffer is already at max, do not add any additional
            // characters to it.
            if (beforeBytesReturned < TinyGDriver.getInstance().serialWriter.getMaxBufferValue()) {
                // Add the number of returned characters parsed from the footer back to our
                // internal Tinyg buffer variable.
                TinyGDriver.getInstance().serialWriter
                        .addBytesReturnedToBuffer(responseFooter.getRxRecvd());

                // Now record our new buffer value. This should be the old value plus the number
                // of characters returned to it (parsed our from the most recent footer).
                int afterBytesReturned = TinyGDriver.getInstance().serialWriter.getBufferValue();

                // TODO: Delete this eventually and get rid of the line argument.
                Log.d(TAG, String.format("Line w/Footer Received: " + line + " Adding "
                        + responseFooter.getRxRecvd() + " to buffer... Buffer was " +
                        beforeBytesReturned + " is now " + afterBytesReturned));

                // Let our serialWriter thread know we have added some space to the buffer.
                TinyGDriver.getInstance().serialWriter.notifyAck();
            }
        } catch (Exception ex) {
            Log.i(TAG, "Error parsing JSON footer");
        }
    }

    /**
     * Parse the JSON response message from the Tinyg.
     * @param line
     * @throws JSONException
     */
    public synchronized void parseJSON(String line) throws JSONException {

        final JSONObject js = new JSONObject(line);

        // We got an incremental status report which was automatically sent by the Tinyg during
        // a motor move or machine state change. This type of JSON packet will only have the status
        // report key and won't have the response or footer keys.
        if (js.has(MnemonicManager.MNEMONIC_GROUP_STATUS_REPORT)) {
            // Get the JSON object paired with the status report key.
            JSONObject sr = js.getJSONObject(MnemonicManager.MNEMONIC_GROUP_STATUS_REPORT);

            // Check the status report JSON object for the status key.
            if (sr.has(MnemonicManager.MNEMONIC_STATUS_REPORT_STAT)) {
                // Status key found. Now get the value associated with it, which should
                // be in the form of an integer.
                int stat = sr.getInt(MnemonicManager.MNEMONIC_STATUS_REPORT_STAT);

                // This is the current machine status of our motor controller. Now update
                // our machine manager class with this information.
                TinyGDriver.getInstance().machineManager.setMachineState(stat);
            }
        }

        // Check if the JSON response contains the response key ("r").
        if (js.has(MnemonicManager.MNEMONIC_JSON_RESPONSE)) {
            // The Tinyg has sent us a JSON message in response to a command that we sent.
            // Here is where we will attempt to bind the response to the proper command. We have
            // to search and match the response identifier with that of each command since
            // we do not know from the context which command triggered which response.
            CommandManager.getInstance().bindResponse(line);

            try {
                // Now that we have a response, check if the footer portion exists.
                if (js.has(MnemonicManager.MNEMONIC_JSON_FOOTER)) {
                    // It exists. Parse it.
                    parseFooter(js.getJSONArray(MnemonicManager.MNEMONIC_JSON_FOOTER), line);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error Parsing Footer: " + ex);
            }
        }

        else if(js.has(MnemonicManager.MNEMONIC_GROUP_EMERGENCY_SHUTDOWN)) {
            // {"er":{"fb":440.20,"st":204,"msg":"Limit switch hit - Shutdown occurred"}}
            Log.e(TAG, "Limit Error detected:"+line);

            CommandManager.getInstance().limitSwitch(line);
            OnErrorListener context = CommandManager.getInstance().getContext();
            boolean hasContext = (null==context)?false:true;

            if(hasContext)
                context.onLimitError();

            mLimitListener.onMotorError(hasContext);
        }
    }

    /**
     * The method that gets called from the thread. This will sit in a loop and continuously
     * grabs and parses messages from the RX queue. The RX queue contains all Tinyg's responses to
     * our commands.
     */
    @Override
    public void run() {
        while (run) {
            try {
                // Take the next entry in our Rx queue. If empty, will block until an entry
                // becomes available.
                line = TinyGDriver.rxQueue.take();

                Log.d(TAG, "Line Received: " + line);

                // Do not attempt to parse an empty string.
                if (line.equals("")) {
                    continue;
                }

                // Only parse the line if it is a JSON response. A JSON response from the Tinyg
                // will always start with a left brace character (there are no pure array responses
                // from the Tinyg).
                if (line.startsWith("{")) {
                    // Call our parse method on the line.
                    parseJSON(line);
                } else {
                    // If the line is not empty and does not begin with a left brace, then either
                    // the JSON is malformed or the Tinyg was put in Text mode somehow.
                    Log.e(TAG, "Error: Received a non-JSON line: " + line);
                }
            } catch (InterruptedException | JSONException ex) {
                Log.e(TAG, "Exception in Run method: " + ex);
            }
        }
    }
}
