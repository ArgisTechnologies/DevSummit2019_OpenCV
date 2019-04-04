package com.logicpd.papapill.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.device.enums.DeviceCommand;
import com.logicpd.papapill.device.gpio.LightringManager;
import com.logicpd.papapill.device.models.PositionData;
import com.logicpd.papapill.device.tinyg.CommandBuilder;
import com.logicpd.papapill.device.tinyg.TinyGDriver;
import com.logicpd.papapill.interfaces.OnBinReadyListener;
import com.logicpd.papapill.interfaces.OnPositionReadyListener;
import com.logicpd.papapill.interfaces.OnErrorListener;

/**
 * NOTE: This activity is a single screen "dashboard" with button functionality
 * that is meant to assist in testing. This will not be in the final app.
 */
public class DeveloperActivity extends Activity {

    private static final String TAG = "DeveloperActivity";

    private TinyGDriver tg = TinyGDriver.getInstance();

    PositionData positionData = new PositionData();

    Handler handler = new Handler();

    // Some global variables (put into a settings file later):
    private int binNumber = 5;

    private boolean light = false;

    private int distanceCarousel = 120;
    private int distanceRadial = 1;
    private int distanceZ = 1;
    private int feedrateCarousel = 500;
    private int feedrateRadial = 500;
    private int feedrateZ = 500;

    private void initializeActivity() {
        TextView numboxBinTextView = findViewById(R.id.numbox_bin);
        numboxBinTextView.setText(Integer.toString(binNumber));

        TextView cdTextView = findViewById(R.id.carousel_distance);
        cdTextView.setText(Integer.toString(distanceCarousel));
        TextView cfTextView = findViewById(R.id.carousel_feedrate);
        cfTextView.setText(Integer.toString(feedrateCarousel));

        TextView rdTextView = findViewById(R.id.radial_distance);
        rdTextView.setText(Integer.toString(distanceRadial));
        TextView rfTextView = findViewById(R.id.radial_feedrate);
        rfTextView.setText(Integer.toString(feedrateRadial));

        TextView zdTextView = findViewById(R.id.z_distance);
        zdTextView.setText(Integer.toString(distanceZ));
        TextView zfTextView = findViewById(R.id.z_feedrate);
        zfTextView.setText(Integer.toString(feedrateZ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        // Initialize activity.
        initializeActivity();
        Log.d(TAG, "Activity Initialized");

        // Initialize the Tinyg driver.
        if(!tg.initialize()) {
            Log.e(TAG, "Failed to initialize Tinyg driver.");
        }

        // Event Listeners
        tg.setDispenseListeners(new OnBinReadyListener() {
                            @Override
                            public void onBinReady() {
                                Log.d(TAG, "Full Dispense is finished!");
                            }
                        },

                new OnErrorListener() {
                    @Override
                    public void onVisionError() {
                        Log.d(TAG, "Vision failed to detect!");
                    }
                    @Override
                    public void onLimitError() {
                        Log.d(TAG, "Limit reached !");
                    }
                    @Override
                    public void onBadParams(String msg) {
                        Log.d(TAG, "bad params:"+msg);
                    }
                }
        );

        tg.setOnPositionReadyListener(new OnPositionReadyListener() {
            @Override
            public void onPositionReady(final String updateText) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView editText = findViewById(R.id.result_text_pos);
                        editText.setText(updateText);
                    }
                });
            }
        });

        // Button Listeners
        final Button buttonStart = findViewById(R.id.button_start_dispense);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.doFullDispense(binNumber);
                } catch (Exception e) {}
            }
        });

        final Button buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.write(CommandBuilder.CMD_APPLY_CANCEL_MOVE);
                } catch (Exception e) {}
            }
        });

        final Button buttonHomeCarousel = findViewById(R.id.button_home_carousel);
        buttonHomeCarousel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_HOME, "a", null);
                } catch (Exception e) {}
            }
        });

        final Button buttonHomeRadial = findViewById(R.id.button_home_radial);
        buttonHomeRadial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_HOME, "x", null);
                } catch (Exception e) {}
            }
        });

        final Button buttonHomeZ = findViewById(R.id.button_home_z);
        buttonHomeZ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_HOME, "z", null);
                } catch (Exception e) {}
            }
        });

        final Button buttonVacuumOn = findViewById(R.id.button_vacuum_on);
        buttonVacuumOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.write(CommandBuilder.CMD_COOLANT_ON);
                } catch (Exception e) {}
            }
        });

        final Button buttonVacuumOff = findViewById(R.id.button_vacuum_off);
        buttonVacuumOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.write(CommandBuilder.CMD_COOLANT_OFF);
                } catch (Exception e) {}
            }
        });

        final Button buttonLightToggle = findViewById(R.id.button_light);
        buttonLightToggle.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               try {
                   light = !light;
                   LightringManager.getInstance().configureOutput(light);
               } catch (Exception e) {}
           }
        });

        final Button buttonReadPressure = findViewById(R.id.button_read_pressure);
        buttonReadPressure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //new readPressureSensorTask().execute();
                } catch (Exception e) {
                    Log.e("I2C", "Exception when reading I2C.");
                }
            }
        });

        final Button buttonReadPos = findViewById(R.id.button_read_position);
        buttonReadPos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tg.commandManager.callCommand(DeviceCommand
                        .COMMAND_READ_POSITION, "", positionData);
            }
        });

        final Button buttonConfigure = findViewById(R.id.button_config);
        buttonConfigure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    tg.softwareReset();
                }
                catch (Exception e) {}
            }
        });

        final Button buttonDemo = findViewById(R.id.button_demo);
        buttonDemo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RunDemoLoop();
            }
        });

        final Button buttonCarouselUp = findViewById(R.id.button_carouselup);
        buttonCarouselUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String cmdParams = String.format("a %s %s", distanceCarousel, feedrateCarousel);
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_MOVE, cmdParams, null);
                }
                catch (Exception e) {}
            }
        });

        final Button buttonCarouselDown = findViewById(R.id.button_carouseldown);
        buttonCarouselDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String cmdParams = String.format("a %s %s", -distanceCarousel, feedrateCarousel);
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_MOVE, cmdParams, null);
                }
                catch (Exception e) {}
            }
        });

        final Button buttonRadialUp = findViewById(R.id.button_radialup);
        buttonRadialUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String cmdParams = String.format("x %s %s", distanceRadial, feedrateRadial);
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_MOVE, cmdParams, null);
                }
                catch (Exception e) {}
            }
        });

        final Button buttonRadialDown = findViewById(R.id.button_radialdown);
        buttonRadialDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String cmdParams = String.format("x %s %s", -distanceRadial, feedrateRadial);
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_MOVE, cmdParams, null);
                }
                catch (Exception e) {}
            }
        });

        final Button buttonZUp = findViewById(R.id.button_zup);
        buttonZUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String cmdParams = String.format("z %s %s", distanceZ, feedrateZ);
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_MOVE, cmdParams, null);
                }
                catch (Exception e) {}
            }
        });

        final Button buttonZDown = findViewById(R.id.button_zdown);
        buttonZDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String cmdParams = String.format("z %s %s", -distanceZ, feedrateZ);
                    tg.commandManager.callCommand(DeviceCommand.COMMAND_MOTOR_MOVE, cmdParams, null);
                }
                catch (Exception e) {}
            }
        });

        // Text button listeners
        final Button buttonBinUp = findViewById(R.id.button_bin_up);
        buttonBinUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (binNumber >= 15) {
                    return;
                }
                TextView editText = findViewById(R.id.numbox_bin);
                binNumber = Integer.parseInt(editText.getText().toString()) + 1;
                editText.setText(Integer.toString(binNumber));
            }
        });

        final Button buttonBinDown = findViewById(R.id.button_bin_down);
        buttonBinDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (binNumber <= 1) {
                    return;
                }
                TextView editText = findViewById(R.id.numbox_bin);
                binNumber = Integer.parseInt(editText.getText().toString()) - 1;
                editText.setText(Integer.toString(binNumber));
            }
        });

        final Button buttonCarouselDistanceUp = findViewById(R.id.button_carousel_distance_up);
        buttonCarouselDistanceUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.carousel_distance);
                distanceCarousel = Integer.parseInt(editText.getText().toString()) + 1;
                editText.setText(Integer.toString(distanceCarousel));
            }
        });
        final Button buttonCarouselDistanceDown = findViewById(R.id.button_carousel_distance_down);
        buttonCarouselDistanceDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.carousel_distance);
                distanceCarousel = Integer.parseInt(editText.getText().toString()) - 1;
                editText.setText(Integer.toString(distanceCarousel));
            }
        });
        final Button buttonCarouselFeedRateUp = findViewById(R.id.button_carousel_feedrate_up);
        buttonCarouselFeedRateUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.carousel_feedrate);
                feedrateCarousel = Integer.parseInt(editText.getText().toString()) + 100;
                editText.setText(Integer.toString(feedrateCarousel));
            }
        });
        final Button buttonCarouselFeedRateDown = findViewById(R.id.button_carousel_feedrate_down);
        buttonCarouselFeedRateDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.carousel_feedrate);
                feedrateCarousel = Integer.parseInt(editText.getText().toString()) - 100;
                editText.setText(Integer.toString(feedrateCarousel));
            }
        });

        final Button buttonRadialDistanceUp = findViewById(R.id.button_radial_distance_up);
        buttonRadialDistanceUp.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               TextView editText = findViewById(R.id.radial_distance);
               distanceRadial = Integer.parseInt(editText.getText().toString()) + 1;
               editText.setText(Integer.toString(distanceRadial));
           }
        });
        final Button buttonRadialDistanceDown = findViewById(R.id.button_radial_distance_down);
        buttonRadialDistanceDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.radial_distance);
                distanceRadial = Integer.parseInt(editText.getText().toString()) - 1;
                editText.setText(Integer.toString(distanceRadial));
            }
        });
        final Button buttonRadialFeedRateUp = findViewById(R.id.button_radial_feedrate_up);
        buttonRadialFeedRateUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.radial_feedrate);
                feedrateRadial = Integer.parseInt(editText.getText().toString()) + 100;
                editText.setText(Integer.toString(feedrateRadial));
            }
        });
        final Button buttonRadialFeedRateDown = findViewById(R.id.button_radial_feedrate_down);
        buttonRadialFeedRateDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.radial_feedrate);
                feedrateRadial = Integer.parseInt(editText.getText().toString()) - 100;
                editText.setText(Integer.toString(feedrateRadial));
            }
        });

        final Button buttonZDistanceUp = findViewById(R.id.button_z_distance_up);
        buttonZDistanceUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.z_distance);
                distanceZ = Integer.parseInt(editText.getText().toString()) + 1;
                editText.setText(Integer.toString(distanceZ));
            }
        });
        final Button buttonZDistanceDown = findViewById(R.id.button_z_distance_down);
        buttonZDistanceDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.z_distance);
                distanceZ = Integer.parseInt(editText.getText().toString()) - 1;
                editText.setText(Integer.toString(distanceZ));
            }
        });
        final Button buttonZFeedRateUp = findViewById(R.id.button_z_feedrate_up);
        buttonZFeedRateUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.z_feedrate);
                feedrateZ = Integer.parseInt(editText.getText().toString()) + 100;
                editText.setText(Integer.toString(feedrateZ));
            }
        });
        final Button buttonZFeedRateDown = findViewById(R.id.button_z_feedrate_down);
        buttonZFeedRateDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView editText = findViewById(R.id.z_feedrate);
                feedrateZ = Integer.parseInt(editText.getText().toString()) - 100;
                editText.setText(Integer.toString(feedrateZ));
            }
        });
    }

    // Sandbox testing grounds.
    private void RunDemoLoop() {
        try {
            tg.doFullDispense(binNumber);
        } catch (Exception e) {
            Log.d(TAG, "Demo loop failed");
        }
    }


    /*class readPressureSensorTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "Some string result to pass to onPostExecute";

            int pressure = tg.pressureSensor.getPressure();
            result = Integer.toString(pressure);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView editText = findViewById(R.id.result_text_pressure);
            editText.setText(result);
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Activity Destroyed");
    }
}
