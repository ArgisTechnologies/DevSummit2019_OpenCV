// TODO: Header goes here

package com.logicpd.papapill.device.tinyg;

import android.util.Log;

import com.logicpd.papapill.device.enums.CommandOperation;
import com.logicpd.papapill.device.enums.CommandState;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.tinyg.commands.Command;
import com.logicpd.papapill.device.tinyg.commands.CommandFullDispense;
import com.logicpd.papapill.device.tinyg.commands.CommandHomeAll;
import com.logicpd.papapill.device.tinyg.commands.CommandMotorHome;
import com.logicpd.papapill.device.tinyg.commands.CommandMotorInit;
import com.logicpd.papapill.device.tinyg.commands.CommandMotorMove;
import com.logicpd.papapill.device.tinyg.commands.CommandMotorPickup;
import com.logicpd.papapill.device.tinyg.commands.CommandReadPosition;
import com.logicpd.papapill.device.tinyg.commands.CommandReset;
import com.logicpd.papapill.device.tinyg.commands.CommandRotateCarousel;
import com.logicpd.papapill.device.tinyg.commands.CommandSetConfig;
import com.logicpd.papapill.interfaces.OnErrorListener;

/**
 * The CommandManager class implements the logic that runs during the main command thread.
 * It handles monitoring global command state, advancing command state, and execution
 * of commands themselves.
 */
public class CommandManager implements Runnable {

    private static final String TAG = "CommandManager";
    private boolean run = true;

    // The main command data structure.
    public static final Command[] commands = {
            new CommandFullDispense(),
            new CommandRotateCarousel(),
            new CommandMotorHome(),
            new CommandMotorInit(),
            new CommandMotorMove(),
            new CommandMotorPickup(),
            new CommandReadPosition(),
            new CommandSetConfig(),
            new CommandReset(),
            new CommandHomeAll()
    };

    private CommandManager() { }

    private static class LazyHolder {
        private static final CommandManager INSTANCE = new CommandManager();
    }

    public static CommandManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    // Some getters and setters
    public boolean isRunning() { return run; }
    public void setRun(boolean run) { this.run = run; }

    /**
     * Clear any responses or params from the command and also reset its
     * state to default.
     */
    private void clearCommand(Command command) {
        command.setParams("");
        command.setResponse("");
        command.setOperation(CommandOperation.OP_STATE_NONE);
        command.setState(CommandState.WAITING_STATE);
    }

    /**
     * Initalize all commands by resetting their state.
     */
    public void clearAllCommands() {
        for (Command command : commands) {
            clearCommand(command);
        }
    }

    /**
     * Look up the command in our command structure by identifier and return the command
     * if found.
     * @param commandId
     * @return
     */
    public Command getCommand(DeviceCommand commandId) {
        for (Command command : commands) {
            if (command.getCommandId() == commandId) {
                return command;
            }
        }
        return null;
    }

    /**
     * Matches the commandId supplied with a command from the internal command data structure.
     * If the command is matched, it will start the state machine and call the command's execute
     * function to run the command.
     * @param commandId - Command identifier to look up.
     * @param params - A string containing any additional parameters separated by space.
     * @param data - The object that holds the returned data from the command.
     * @return
     */
    public boolean callCommand(DeviceCommand commandId, String params, Object data) {
        Log.d(TAG, "Attempting to call command: " + commandId + " ( " + params + " ) ");
        // Loop through each command in our main command data structure. If we match the supplied
        // command Id against our array of commands, call that command's execute function.
        for (Command command : commands) {
            if (command.getCommandId() == commandId) {
                // Double check that the command's internal state is ready and waiting.
                if (command.getState() != CommandState.WAITING_STATE) {
                    Log.e(TAG, "Error! Command is already running and cannot be restarted: "
                            + command.getName());
                }
                else {
                    // Set the commands data field which holds the commands return data if needed.
                    command.setData(data);

                    // Copy the param string passed in to the command.
                    command.setParams(params);

                    // Prime the command by setting its operation state to start. The command
                    // manager's run loop will take care of setting operation state to run after
                    // the specific command has finished its start state logic.
                    command.setOperation(CommandOperation.OP_STATE_START);

                    // Execute the command already!
                    command.execute();
                }
                // Indicate that we've successfully located and handled the command call by
                // returning true.
                return true;
            }
        }
        // If we're here, the command Id was not found. Return false.
        return false;
    }

    /**
     * Checks to see if the command specified is currently in the waiting state (meaning idle) or
     * if it is still working.
     * @param commandId
     * @return
     */
    public boolean isCommandDone(DeviceCommand commandId) {
        for (Command command : commands) {
            if (command.getCommandId() == commandId) {
                return command.isWaiting();
            }
        }
        return true;
    }

    /**
     * This method binds a response to the command which triggered it. This method is called
     * upon receiving a response in the response parser thread. We will go through all our commands
     * that we suspect may be waiting for a response and bind the response string to the command
     * instance. Note that not all commands are capable of generating a response or even need to
     * know about a response.
     * @param response
     * @return
     */
    public synchronized boolean bindResponse(String response) {
        //Log.d("bindResponse", "response:"+response);

        for (Command command : commands) {
            // If a command is complete or waiting state, then surely it cannot be expecting
            // a response as it has finished (or not started) its execution stage. Therefore, we
            // skip them when searching for a command to bind a response to.
            // TODO: Ideally, there should be an isWaitingForResponse() method.
            if(!command.isWaiting() && !command.isComplete()) {
                // From the remaining commands that are currently doing something, check to see
                // if the command response matches a certain format. The TinyG does not have an
                // explicit "command id" field that uniquely identifies the command. We instead
                // look at the first part of the JSON response (preceding the values) and use
                // that to identify which command the response is for. For the purposes of our
                // application, this is effectively a unique identifier as no two commands will
                // have the same "leading JSON."
                String responseId = command.getResponseId();
                if(null!=responseId &&
                        response.startsWith(responseId)) {
                    command.setResponse(response);

                    Log.d(TAG, "Response bound to " + command.getName() + ": " + response);
                }
            }
        }
        return false;
    }

    public synchronized boolean limitSwitch(String response) {
        clearAllCommands();
        Log.d("limitSwitch", "response:"+response);
        return false;
    }

    public OnErrorListener getContext()
    {
        for (Command command : commands) {
            if (!command.isComplete()) {
                Log.d("getContext", "name:"+command.getClass().getName());
                return ((CommandFullDispense)command).getErrListener();
            }
        }
        return null;
    }

    @Override
    public void run() {

        // Initialize all of the command state machines to the waiting state.
        clearAllCommands();

        while (run) {
            // Loop through each command in our command list. If its state is something other than
            // the default waiting state, call the command.
            for (Command command : commands) {
                if (command.isComplete()) {
                    // This command has been marked complete. Reset its state / variables in
                    // preparation for the next time its called.
                    clearCommand(command);

                } else if (!command.isWaiting()) {
                    // If the command is not complete or in waiting state (idling), then the
                    // command is currently in the middle of doing something. Advance the command
                    // state machine.
                    command.setOperation(CommandOperation.OP_STATE_RUN);

                    // Call the command (again).
                    command.execute();
                }
            }
        }
    }
}
