package com.shubham.invitationmodule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.db.LoginPreference;
import com.shubham.invitationmodule.fragments.FragmentSignIn;
import com.shubham.invitationmodule.fragments.FragmentSignUp;

public class ActivityAuth extends AppCompatActivity {

    Fragment currentFragment;
    int count = 0;
    LoginPreference loginPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginPreference = new LoginPreference(this);
        if (loginPreference.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), ActivityMainNavigation.class));
            finish();
        }

        fragmentTransition(new FragmentSignIn());
    }

    public void fragmentTransition(Fragment fragment) {
        this.currentFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof FragmentSignUp /*|| currentFragment instanceof ForgotPasswordFragment*/) {
            this.fragmentTransition(new FragmentSignIn());
        } else if (currentFragment instanceof FragmentSignIn) {
            count++;
            if (count == 1) {
                Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
            } else if (count == 2) {
                super.onBackPressed();
            }
        }
    }
}