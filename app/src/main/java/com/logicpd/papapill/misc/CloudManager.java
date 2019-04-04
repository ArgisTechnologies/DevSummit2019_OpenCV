package com.logicpd.papapill.misc;

import android.content.Context;
import android.util.Log;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageCallback;

/**
 * Class for managing communications between client and IoT Hub
 *
 * @author alankilloren
 */
public class CloudManager implements MessageCallback, IotHubEventCallback {

    private Context context;
    private static CloudManager mInstance;
    private DeviceClient client;
    private String connString = "HostName=papapill-dev-iothub.azure-devices.net;DeviceId=MyDotnetDevice;SharedAccessKey=MNktCwuBcYZKYBx2m57XSbpusl55tzrWUA0mefnP9eA=";
    private IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private CloudCallbackListener cloudCallbackListener;

    // Define method response codes
    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_NOT_DEFINED = 404;
    private static final int INVALID_PARAMETER = 400;

    private boolean isConnected;

    public CloudManager(Context context) {
        this.context = context;
        init();
    }

    /**
     * Initializes the MQTT client and sets up callback listeners
     */
    private void init() {
        try {
            client = new DeviceClient(connString, protocol);
            client.setMessageCallback(this, null);
            //TODO client.subscribeToDeviceMethod(new DeveloperTestFragment.DirectMethodCallback(), null, new DeveloperTestFragment.DirectMethodStatusCallback(), null);
        } catch (Exception e) {//TODO not sure what all we need to be catching here, WIP
            e.printStackTrace();
        }
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }

    public void setProtocol(IotHubClientProtocol protocol) {
        this.protocol = protocol;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * This establishes communications between this class and another class implementing the callback
     *
     * @param listener CloudCallbackListener object
     */
    public void setOnCloudCallbackListener(CloudCallbackListener listener) {
        cloudCallbackListener = listener;
    }

    /**
     * Opens a client connection to the hub if closed
     */
    public void connect() {
        if (client != null && !isConnected) {
            try {
                client.open();
                isConnected = true;
                Log.d(AppConstants.TAG, "Successful connection to IoT Hub");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(AppConstants.TAG, "ERROR connecting to IoT Hub");
                isConnected = false;
            }
        }
    }

    /**
     * Closes client connection if connected
     */
    public void close() {
        if (client != null && isConnected) {
            try {
                client.closeNow();
                isConnected = false;
                Log.d(AppConstants.TAG, "Connection to IoT Hub closed");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(AppConstants.TAG, "ERROR while trying to close connection to IoT Hub");
            }
        }
    }

    /**
     * Send a message from the client to the cloud (hub)
     *
     * @param action String action (lower case)
     * @param msgStr String message (typically JSON) to send
     * @param object Object to pass?
     */
    public void sendMessage(String action, String msgStr, Object object) {
        Message msg = new Message(msgStr);
        msg.setProperty("Action", action.toLowerCase());
        msg.setMessageId(java.util.UUID.randomUUID().toString());
        if (client != null && isConnected) {
            client.sendEventAsync(msg, this, object);
        }
    }

    public static CloudManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CloudManager(context);
        }
        return mInstance;
    }

    @Override
    public IotHubMessageResult execute(Message message, Object callbackContext) {
        Log.d(AppConstants.TAG, "Received message from IoT Hub: " + new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

        //send back to UI
        cloudCallbackListener.onCloudCallback("Received message from IoT Hub: "
                + new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET), callbackContext);

        return IotHubMessageResult.COMPLETE;
    }

    @Override
    public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
        Log.d(AppConstants.TAG, ("IoT Hub responded to message with status " + responseStatus.name()));

        //send back to  UI
        cloudCallbackListener.onCloudCallback(" IoT Hub responsed to message with " + responseStatus.name(), callbackContext);
    }

    public interface CloudCallbackListener {
        void onCloudCallback(String msg, Object object);
    }

     /*class DirectMethodStatusCallback implements IotHubEventCallback {
        public void execute(final IotHubStatusCode status, Object context) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    tvResult.append("Direct method # IoT Hub responded to device method acknowledgement with status: " + status.name() + "\n");
                }
            });
            Log.d(AppConstants.TAG, "Direct method # IoT Hub responded to device method acknowledgement with status: " + status.name());

        }
    }
*/
    /*class DirectMethodCallback implements DeviceMethodCallback {
        private void setTelemetryInterval(final int interval) {
            Log.d(AppConstants.TAG, "Direct method # Setting telemetry interval (seconds): " + interval);
            h.post(new Runnable() {
                @Override
                public void run() {
                    tvResult.append("Direct method # Setting telemetry interval (seconds): " + interval + "\n");
                }
            });
        }

        @Override
        public DeviceMethodData call(String methodName, Object methodData, Object context) {
            DeviceMethodData deviceMethodData = null;
            String payload = new String((byte[]) methodData);
            switch (methodName) {
                case "SetTelemetryInterval": {
                    int interval;
                    try {
                        int status = METHOD_SUCCESS;
                        interval = Integer.parseInt(payload);
                        System.out.println(payload);
                        setTelemetryInterval(interval);
                        deviceMethodData = new DeviceMethodData(status, "Executed direct method " + methodName);
                    } catch (NumberFormatException e) {
                        int status = INVALID_PARAMETER;
                        deviceMethodData = new DeviceMethodData(status, "Invalid parameter " + payload);
                    }
                    break;
                }
                default: {
                    int status = METHOD_NOT_DEFINED;
                    deviceMethodData = new DeviceMethodData(status, "Not defined direct method " + methodName);
                }
            }
            return deviceMethodData;
        }
    }
*/
}