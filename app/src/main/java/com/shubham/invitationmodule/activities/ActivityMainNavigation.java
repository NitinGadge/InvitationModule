package com.shubham.invitationmodule.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.db.LoginPreference;
import com.shubham.invitationmodule.fragments.FragmentInvitations;
import com.shubham.invitationmodule.fragments.FragmentCreateInvitation;
import com.shubham.invitationmodule.fragments.FragmentProfile;
import com.shubham.invitationmodule.fragments.FragmentMyEvents;

public class ActivityMainNavigation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    LoginPreference loginPreference;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        loginPreference = new LoginPreference(this);

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.nav_view);
        toolbar.setTitle("Create New Invitation");
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        loadFragment(new FragmentCreateInvitation());

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Fragment fragment = null;
            switch (id) {
                case R.id.createInvitation:
                    toolbar.setTitle(R.string.create_invitation);
                    fragment = new FragmentCreateInvitation();
                    loadFragment(fragment);
                    break;
                case R.id.myEvents:
                    toolbar.setTitle(R.string.my_events);
                    fragment = new FragmentMyEvents();
                    loadFragment(fragment);
                    break;
                case R.id.invitations:
                    toolbar.setTitle(R.string.invitations);
                    fragment = new FragmentInvitations();
                    loadFragment(fragment);
                    break;
                case R.id.profile:
                    toolbar.setTitle(R.string.profile);
                    fragment = new FragmentProfile();
                    loadFragment(fragment);
                    break;
                case R.id.logout:
                    showCustomLogoutDialog();
                    break;
                default:
                    return true;
            }
            return true;
        });
    }

    private void showCustomLogoutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        (dialog.findViewById(R.id.btnCancel)).setOnClickListener(v -> dialog.dismiss());

        (dialog.findViewById(R.id.btnLogout)).setOnClickListener(v -> loginPreference.logoutUser());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }
}