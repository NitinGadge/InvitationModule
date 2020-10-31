package com.shubham.invitationmodule.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shubham.invitationmodule.R;

public class ShowProgressSnackBar {

    public static ProgressDialog progressDialogSnackBar(View view, Context context, String s) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.CustomDialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                ProgressBar progress = findViewById(android.R.id.progress);
                LinearLayout bodyLayout = (LinearLayout) progress.getParent();
                TextView messageView = (TextView) bodyLayout.getChildAt(1);

                messageView.setPadding(20, 0, 0, 0);
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) messageView.getLayoutParams();
                llp.width = 0;
                llp.weight = 1;

                bodyLayout.removeAllViews();
                bodyLayout.addView(messageView, llp);
                bodyLayout.addView(progress);
            }
        };
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(s);
        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
