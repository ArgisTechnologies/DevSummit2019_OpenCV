package com.logicpd.papapill.interfaces;

import android.os.Bundle;

/**
 * This interface is used for communication back to the MainActivity when buttons are clicked within a fragment
 *
 * @author alankilloren
 */
public interface OnButtonClickListener {
    void onButtonClicked(Bundle bundle);
}
