package com.shubham.invitationmodule.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.adapters.AdapterInvitePeople;
import com.shubham.invitationmodule.model.ModelInvitePeople;
import com.shubham.invitationmodule.network.Api;
import com.shubham.invitationmodule.network.VolleySingleton;
import com.shubham.invitationmodule.utils.ShowSnackBar;
import com.shubham.invitationmodule.utils.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityInvitePeople extends AppCompatActivity{

    RecyclerView recyclerViewInviteList;
    ArrayList<ModelInvitePeople> invitePeopleList = new ArrayList<>();
    AdapterInvitePeople adapterInvitePeople;
    Button btnSendInvitation;
    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_people);

        recyclerViewInviteList = findViewById(R.id.recyclerViewInviteList);
        btnSendInvitation = findViewById(R.id.btnSendInvitation);
        mainLayout = findViewById(R.id.mainLayout);

        getUserListToInvite();

        btnSendInvitation.setOnClickListener(view -> {
            if (adapterInvitePeople.getSelected().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < adapterInvitePeople.getSelected().size(); i++) {
                    stringBuilder.append(adapterInvitePeople.getSelected().get(i).getName());
                    stringBuilder.append("\n");
                }
                ShowSnackBar.showSnackBar(mainLayout, stringBuilder.toString().trim());
            } else {
                ShowSnackBar.showSnackBar(mainLayout, getString(R.string.select_people));
            }
        });
    }

    private void getUserListToInvite() {
        final String post_url = Api.serverAddress + getString(R.string.get_user_list);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, post_url,
                response -> {
                    invitePeopleList = new ArrayList();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        Log.d("checkDataSearch ===", "getParams:use " + response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String resp = jsonObject.getString("status");
                            if (resp.equals("Active")) {
                                ModelInvitePeople modelCurrentAffairs = new ModelInvitePeople();

                                modelCurrentAffairs.setName(jsonObject.getString("full_name"));
                                modelCurrentAffairs.setEmail(jsonObject.getString("email"));
                                invitePeopleList.add(modelCurrentAffairs);
                            }
                        }
                        adapterInvitePeople = new AdapterInvitePeople(invitePeopleList, ActivityInvitePeople.this);
                        recyclerViewInviteList.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerViewInviteList.setLayoutManager(linearLayoutManager);
                        recyclerViewInviteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerViewInviteList.setAdapter(adapterInvitePeople);
                        adapterInvitePeople.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> VolleyErrors.volleyErrors(ActivityInvitePeople.this, volleyError)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

/*    @Override
    public void onInvitePeople(Boolean isSelected) {
        if (isSelected) {
            btnSendInvitation.setVisibility(View.VISIBLE);
        } else {
            btnSendInvitation.setVisibility(View.GONE);
        }
    }*/
}