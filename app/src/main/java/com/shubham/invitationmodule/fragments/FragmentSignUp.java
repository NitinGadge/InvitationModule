package com.shubham.invitationmodule.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.activities.ActivityAuth;
import com.shubham.invitationmodule.activities.ActivityMainNavigation;
import com.shubham.invitationmodule.activities.MainActivity;
import com.shubham.invitationmodule.db.LoginPreference;
import com.shubham.invitationmodule.network.Api;
import com.shubham.invitationmodule.network.VolleySingleton;
import com.shubham.invitationmodule.utils.ShowProgressSnackBar;
import com.shubham.invitationmodule.utils.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentSignUp extends Fragment implements View.OnClickListener {

    private final static String TAG = "FragmentSignUp";
    View view;
    EditText etFullName, etEmail, etPassword;
    MaterialButton btnSignUp;
    TextView loginAccount;
    LoginPreference loginPreference;
    ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        loginPreference = new LoginPreference(getActivity());

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etFullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        btnSignUp = view.findViewById(R.id.btnSignUp);
        loginAccount = view.findViewById(R.id.loginAccount);
        constraintLayout = view.findViewById(R.id.constraintLayout);

        btnSignUp.setOnClickListener(this);
        loginAccount.setOnClickListener(this);

        return view;
    }

    @SuppressLint({"ObsoleteSdkInt", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                register();
                break;
            case R.id.loginAccount:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ((ActivityAuth) Objects.requireNonNull(getActivity())).fragmentTransition(new FragmentSignIn());
                }
                break;
        }
    }

    private void register() {
        if (!confirmInput()) {
        } else {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
            if (isConnected) {
                registerUserAccount(etFullName.getText().toString(), etEmail.getText().toString().toLowerCase(), etPassword.getText().toString());
            } else {
                Toast.makeText(getActivity(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean confirmInput() {
        if (!validateFullName(etFullName) | !validateEmail(etEmail) | !validatePassword(etPassword)) {
            return false;
        }
        return true;
    }

    public boolean validateFullName(EditText fullName) {
        String name = fullName.getText().toString();
        if (name.isEmpty()) {
            fullName.setError("Field can't be empty");
            fullName.requestFocus();
            return false;
        } else {
            fullName.setError(null);
            return true;
        }
    }

    public boolean validateEmail(EditText etemail) {
        String email = etemail.getText().toString().trim();
        if (email.isEmpty()) {
            etemail.setError("Field can't be empty");
            etemail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Please enter a valid email address");
            etemail.requestFocus();
            return false;
        } else {
            etemail.setError(null);
            return true;
        }
    }

    public boolean validatePassword(EditText et_pass) {
        String password = et_pass.getText().toString();
        if (password.isEmpty()) {
            et_pass.setError("Field can't be empty");
            return false;
        } else if (password.length() <= 5 || password.length() >= 12) {
            et_pass.setError("Password Length Should Between 6 and 8");
            return false;
        } else {
            et_pass.setError(null);
            return true;
        }
    }

    private void registerUserAccount(final String fullName, final String email, final String password) {
        ShowProgressSnackBar.progressDialogSnackBar(constraintLayout, getActivity(), "Creating your account, Please wait");
        final String URL = Api.serverAddress + "user_registration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        Log.d(TAG, "registerUserAccount" + response);
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String resp = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (resp.equals("Active")) {

                            String userId = jsonObject.getString("user_login_id");
                            String fullName1 = jsonObject.getString("full_name");
                            String username = jsonObject.getString("email");
                            String password1 = jsonObject.getString("password");

                            loginPreference.createLoginSession(userId, fullName1, username, password1);
                            Intent intent = new Intent(getActivity(), ActivityMainNavigation.class);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    VolleyErrors.volleyErrors(getActivity(), error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("full_name", fullName);
                params.put("email", email);
                params.put("password", password);
                //   params.put("login_type", "credentials");
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}