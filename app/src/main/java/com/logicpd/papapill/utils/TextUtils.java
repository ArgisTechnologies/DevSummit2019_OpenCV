package com.logicpd.papapill.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.logicpd.papapill.R;

/**
 * Collection of useful utilities to use on views, etc.
 *
 * @author alankilloren
 */
public class TextUtils {

    public static void disableButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.rounded_rectangle_pressed);
    }

    public static void enableButton(Button button) {
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.button_selector);
    }

    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
