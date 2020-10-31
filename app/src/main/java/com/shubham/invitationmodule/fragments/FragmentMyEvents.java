package com.shubham.invitationmodule.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.adapters.AdapterMyEvents;
import com.shubham.invitationmodule.db.LoginPreference;
import com.shubham.invitationmodule.model.ModelMyEvents;
import com.shubham.invitationmodule.network.Api;
import com.shubham.invitationmodule.network.VolleySingleton;
import com.shubham.invitationmodule.utils.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMyEvents extends Fragment {

    private final static String TAG = "FragmentMyEvents";
    View view;
    private AdapterMyEvents adapterMyEvents;
    private List<ModelMyEvents> modelMyEventsList = new ArrayList<>();
    RecyclerView rvMyEvents;
    LoginPreference loginPreference;
    String loginId;
    SearchView searchView;
    ProgressBar progressMyEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_events, container, false);

        loginPreference = new LoginPreference(getActivity());
        HashMap<String, String> user = loginPreference.getUserDetails();
        loginId = user.get(LoginPreference.KEY_USER_ID);

        initData();
        return view;
    }

    private void initData() {
        rvMyEvents = view.findViewById(R.id.rvMyEvents);
        searchView = view.findViewById(R.id.searchView);
        progressMyEvents = view.findViewById(R.id.progressMyEvents);

        // searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // searchView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterMyEvents.getFilter().filter(newText);
                return false;
            }
        });

        getMyEventsData();
    }

    private void getMyEventsData() {
        progressMyEvents.setVisibility(View.VISIBLE);
        final String post_url = Api.serverAddress + "get_my_events.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, post_url,
                response -> {
                    progressMyEvents.setVisibility(View.GONE);
                    modelMyEventsList = new ArrayList();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        Log.d(TAG, response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String resp = jsonObject.getString("status");
                            if (resp.equals("Active")) {
                                ModelMyEvents modelMyEvents = new ModelMyEvents();

                                modelMyEvents.setEventTitle(jsonObject.getString("event_title"));
                                modelMyEvents.setEventDate(jsonObject.getString("event_date"));
                                modelMyEvents.setEventTime(jsonObject.getString("event_time"));
                                modelMyEvents.setEventAddress(jsonObject.getString("event_location"));
                                modelMyEvents.setEventPoster(jsonObject.getString("event_banner"));
                                modelMyEvents.setAttending(jsonObject.getString("attending"));
                                modelMyEvents.setNotAttending(jsonObject.getString("not_attending"));
                                modelMyEvents.setMaybe(jsonObject.getString("maybe"));
                                modelMyEventsList.add(modelMyEvents);
                            }
                        }
                        adapterMyEvents = new AdapterMyEvents(modelMyEventsList, getActivity());
                        rvMyEvents.setItemAnimator(new DefaultItemAnimator());
                        rvMyEvents.setAdapter(adapterMyEvents);
                        adapterMyEvents.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> VolleyErrors.volleyErrors(getActivity(), volleyError)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_login_id", loginId);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}