package com.shubham.invitationmodule.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class ShowSnackBar {
    public static void showSnackBar(View rootView, String mMessage) {
        Snackbar.make(rootView, mMessage, Snackbar.LENGTH_LONG).show();
    }
}
