package com.logicpd.papapill.fragments.user_settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;

import static com.logicpd.papapill.misc.AppConstants.TAG;

/**
 * Fragment for User Settings...Audio Volume
 *
 * @author alankilloren
 */
public class AudioSettingFragment extends Fragment implements View.OnClickListener {

    private static final String SCREEN_NAME = "Audio Settings";

    private LinearLayout contentLayout;
    private LinearLayout backButton, homeButton;
    private OnButtonClickListener mListener;
    private Button btnDone;
    private SeekBar seekBar;
    private User user;
    private DatabaseHelper db;
    private AudioManager audioManager;
    private ToneGenerator tone;

    public AudioSettingFragment() {
        // Required empty public constructor
    }

    public static AudioSettingFragment newInstance() {
        return new AudioSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings_audio_volume, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = DatabaseHelper.getInstance(getActivity());

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        setupViews(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            if (user.getAudioVolume() == 0) {
                seekBar.setProgress(50);//default
            } else {
                seekBar.setProgress(user.getAudioVolume());
            }
            //seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }


    private void setupViews(View view) {
        contentLayout = view.findViewById(R.id.layout_content);
        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(this);
        homeButton = view.findViewById(R.id.button_home);
        homeButton.setOnClickListener(this);
        btnDone = view.findViewById(R.id.button_done);
        btnDone.setOnClickListener(this);
        seekBar = view.findViewById(R.id.seekbar_volume);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "Audio volume: " + progress);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                // play beep
                try {
                    if (tone == null) {
                        tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, progress);
                    }
                    tone.startTone(ToneGenerator.TONE_PROP_BEEP, 200);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (tone != null) {
                                Log.d(TAG, "ToneGenerator released");
                                tone.release();
                                tone = null;
                            }
                        }

                    }, 200);
                } catch (Exception e) {
                    Log.d(TAG, "Exception while playing sound:" + e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = null;

        if (context instanceof Activity) {
            a = (Activity) context;
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnButtonClickListener) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (v == backButton) {
            bundle.putString("fragmentName", "Back");
            mListener.onButtonClicked(bundle);
        }
        if (v == homeButton) {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
        if (v == btnDone) {
            // save value to db
            user.setAudioVolume(seekBar.getProgress());
            int returnVal = db.updateUser(user);
            bundle.putSerializable("user", user);
            backButton.performClick();
        }
    }
}
