package com.shubham.invitationmodule.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.shubham.invitationmodule.R;

public class FragmentInvitations extends Fragment {

    View view;
    MaterialButton btnGoing, btnWont, btnMaybe;
    RecyclerView rvGoing, rvWontGoing, rvMaybe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invitations, container, false);

        initData();

        return view;
    }

    private void initData() {
        btnGoing = view.findViewById(R.id.btnGoing);
        btnWont = view.findViewById(R.id.btnWont);
        btnMaybe = view.findViewById(R.id.btnMaybe);

        rvGoing = view.findViewById(R.id.rvGoing);
        rvWontGoing = view.findViewById(R.id.rvWontGoing);
        rvMaybe = view.findViewById(R.id.rvMaybe);

        btnGoing.setChecked(true);

        btnGoing.setOnClickListener(v -> {
            rvGoing.setVisibility(View.VISIBLE);
            rvWontGoing.setVisibility(View.INVISIBLE);
            rvMaybe.setVisibility(View.INVISIBLE);
        });

        btnWont.setOnClickListener(v -> {
            rvGoing.setVisibility(View.INVISIBLE);
            rvWontGoing.setVisibility(View.VISIBLE);
            rvMaybe.setVisibility(View.INVISIBLE);
        });

        btnMaybe.setOnClickListener(v -> {
            rvGoing.setVisibility(View.INVISIBLE);
            rvWontGoing.setVisibility(View.INVISIBLE);
            rvMaybe.setVisibility(View.VISIBLE);
        });
    }
}