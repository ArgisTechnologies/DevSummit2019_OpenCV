package com.logicpd.papapill.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

/**
 * Utilities for handling fragments
 *
 * @author alankilloren
 */
public class FragmentUtils {

    /**
     * Displays the fragment in the specified layout
     *
     * @param activity     Parent activity
     * @param fragment     Fragment to be displayed
     * @param layout       Layout container for fragment
     * @param bundle       Any passed in data from another fragment
     * @param fragmentName String name of fragment
     */
    public static void showFragment(Activity activity, Fragment fragment, View layout, Bundle bundle, String fragmentName) {

        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        fragmentTransaction.replace(layout.getId(), fragment, fragmentName);
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.commit();
    }

    /**
     * Displays the fragment in the specified layout
     *
     * @param activity     Parent activity
     * @param layout       layout container for fragment
     * @param bundle       Any passed in data from another fragment
     * @param fragmentName String name of fragment
     */
    public static void showFragment(Activity activity, View layout, Bundle bundle, String fragmentName) {

        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        try {
            Class<?> fragmentClass = Class.forName(fragmentName);
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bundle != null) {
            if (fragment != null) {
                fragment.setArguments(bundle);
            }
        }

        fragmentTransaction.replace(layout.getId(), fragment, fragmentName);
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.commit();
    }

    /**
     * Removes all fragments from the backstack
     *
     * @param activity Parent activity
     */
    public static void removeAllFragments(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Removes all fragments up to the specified fragment
     *
     * @param activity     Parent activity
     * @param fragmentName String name of fragment
     */
    public static void removeAllFragmentsUpToCurrent(Activity activity, String fragmentName) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        //count backwards and remove all fragments up to fragmentName
        int backstacksize = fragmentManager.getBackStackEntryCount() - 1;
        for (int i = backstacksize; i >= 0; i--) {
            if (!fragmentManager.getBackStackEntryAt(i).getName().equals(fragmentName)) {
                fragmentManager.popBackStack();
            } else {
                break;
            }
        }
    }

    /**
     * Removes the current fragment from the backstack
     *
     * @param activity Parent activity
     */
    public static void removeFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.popBackStack();
    }
}
