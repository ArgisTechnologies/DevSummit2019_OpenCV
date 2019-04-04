package com.logicpd.papapill.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.logicpd.papapill.R;
import com.logicpd.papapill.fragments.my_medication.CannotRetrieveMedFragment;
import com.logicpd.papapill.interfaces.OnButtonClickListener;


public class ErrorFragment extends Fragment implements View.OnClickListener{
    private OnButtonClickListener mListener;
    private Button btnHome;
    private Button btnRetry;

    public static ErrorFragment newInstance()
    {
        return new ErrorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        btnHome = view.findViewById(R.id.button_home);
        btnHome.setOnClickListener(this);
        btnRetry = view.findViewById(R.id.button_retry);
        btnRetry.setOnClickListener(this);
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
        Bundle bundle = this.getArguments();

        if (v == btnHome)
        {
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }

        if(v == btnRetry)
        {
            /*
             * where were we before ?
             * - is there UI state info I can retrieve from ?
             */
            bundle.putString("fragmentName", "Home");
            mListener.onButtonClicked(bundle);
        }
    }
}
