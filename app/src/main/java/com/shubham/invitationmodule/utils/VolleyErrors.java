package com.shubham.invitationmodule.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.shubham.invitationmodule.R;

public class VolleyErrors {
    public static void volleyErrors(Context context, VolleyError volleyError) {
        if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
            Toast.makeText(context, "No Connection/Communication Error!", Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof AuthFailureError) {
            Toast.makeText(context, "Authentication/ Auth Error!", Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof ServerError) {
            Toast.makeText(context, "Server Error!", Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof NetworkError) {
            Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof ParseError) {
            Toast.makeText(context, "Parse Error!", Toast.LENGTH_SHORT).show();
        }
    }
}
