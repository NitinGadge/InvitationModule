package com.shubham.invitationmodule.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.LocationRequest;
import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.activities.ActivityInvitePeople;
import com.shubham.invitationmodule.activities.MapsActivity;
import com.shubham.invitationmodule.db.LoginPreference;
import com.shubham.invitationmodule.network.Api;
import com.shubham.invitationmodule.network.VolleySingleton;
import com.shubham.invitationmodule.utils.ShowSnackBar;
import com.shubham.invitationmodule.utils.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class FragmentCreateInvitation extends Fragment implements LocationListener, View.OnClickListener {

    View view;
    private final static String TAG = "FragmentInvitation";
    LocationManager locationManager;
    DatePickerDialog.OnDateSetListener setListener;
    private TextView tvCurrentLocation, tvSelectImage, tvSelectDate, tvSelectTime;
    private ImageView ivSelectImage, ivCurrentLocation, ivSelectedImage;
    private static final int SELECT_PHOTO = 100;
    private static final int TAKE_IMAGE = 101;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    int hours;
    int minutes;
    Bitmap bitmap;
    ProgressBar progressLocation;
    String eventImage, userId;
    private EditText etEventTitle, etEventDescription, etContactDetails;
    private Button btnSaveAndInvitePeople;
    LoginPreference loginPreference;
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_invitation, container, false);

        loginPreference = new LoginPreference(getActivity());
        HashMap<String, String> user = loginPreference.getUserDetails();
        userId = user.get(LoginPreference.KEY_USER_ID);

        initData();
        initOnClickListeners();

        return view;
    }

    private void initOnClickListeners() {
        ivCurrentLocation.setOnClickListener(this);
        tvSelectImage.setOnClickListener(this);
        ivSelectImage.setOnClickListener(this);
        tvSelectTime.setOnClickListener(this);
        tvSelectDate.setOnClickListener(this);
        btnSaveAndInvitePeople.setOnClickListener(this);
    }

    private void checkLocationServiceIsEnabledOrNot() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.need_permission)
                    .setCancelable(false)
                    .setPositiveButton(R.string.enable, (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
        if (gps_enabled && network_enabled) {
            getCurrentLocation();
        }
    }

    private void initData() {
        tvCurrentLocation = view.findViewById(R.id.tvCurrentLocation);
        ivCurrentLocation = view.findViewById(R.id.ivCurrentLocation);
        tvSelectImage = view.findViewById(R.id.tvSelectImage);
        ivSelectImage = view.findViewById(R.id.ivSelectImage);
        tvSelectDate = view.findViewById(R.id.tvSelectDate);
        tvSelectTime = view.findViewById(R.id.tvSelectTime);
        etEventTitle = view.findViewById(R.id.etEventTitle);
        etEventDescription = view.findViewById(R.id.etEventDescription);
        etContactDetails = view.findViewById(R.id.etContactDetails);
        btnSaveAndInvitePeople = view.findViewById(R.id.btnSaveAndInvitePeople);
        ivSelectedImage = view.findViewById(R.id.ivSelectedImage);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        progressLocation = view.findViewById(R.id.progressLocation);
    }

    private void initTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view, hourOfDay, minute) -> {
            hours = hourOfDay;
            minutes = minute;

            String string = hours + ":" + minutes;
            SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
            try {
                Date date = f24Hours.parse(string);
                SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                tvSelectTime.setText(f12Hours.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, 12, 0, false);

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(hours, minutes);
        timePickerDialog.show();
    }

    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                setListener, year, month, day);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

        setListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = day + "/" + month1 + "/" + year1;
            tvSelectDate.setText(date);
        };
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), REQUEST_CODE_LOCATION_PERMISSION);
            String address = addresses.get(0).getAddressLine(0);
            tvCurrentLocation.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivCurrentLocation:
                // checkLocationServiceIsEnabledOrNot();
         /*       if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    getLocation1();
                }*/
                startActivity(new Intent(getActivity(), MapsActivity.class));
                break;

            case R.id.tvSelectImage:
            case R.id.ivSelectImage:
                if (checkPermission()) {
                } else {
                    requestPermission();
                }
                selectImageFromGalleryOrTakePictureWithCamera();
                break;

            case R.id.tvSelectTime:
                initTimePicker();
                break;

            case R.id.tvSelectDate:
                initDatePicker();
                break;

            case R.id.btnSaveAndInvitePeople:
                checkEnteredData();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation1();
            } else {
                ShowSnackBar.showSnackBar(relativeLayout, getString(R.string.permission_denied));
            }
        }
    }

    private void getLocation1() {
        progressLocation.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void checkEnteredData() {
        if (!confirmInput()) {
        } else {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
            if (isConnected) {
                createEvent(userId, etEventTitle.getText().toString(),
                        etEventDescription.getText().toString(),
                        etContactDetails.getText().toString(),
                        tvSelectDate.getText().toString(),
                        tvSelectTime.getText().toString(),
                        tvCurrentLocation.getText().toString(), eventImage);
            } else {
                ShowSnackBar.showSnackBar(relativeLayout, getString(R.string.check_network_connection));
            }
        }
    }

    private void createEvent(String userId, String eventTitle,
                             String eventDescription,
                             String contactDetails,
                             String date,
                             String time,
                             String location, String eventImage) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Registration");
        progressDialog.setMessage("We are creating your account, Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String URL = Api.serverAddress + "create_event.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        Log.d(TAG, "createEvent" + response);
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String resp = jsonObject.getString("status");
                        if (resp.equals("Active")) {
                            Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ActivityInvitePeople.class);
                            // intent.putExtra();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    VolleyErrors.volleyErrors(getActivity(), error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_login_id", userId);
                params.put("event_title", eventTitle);
                params.put("event_description", eventDescription);
                params.put("event_contact_details", contactDetails);
                params.put("event_date", date);
                params.put("event_time", time);
                params.put("event_location", location);
                params.put("event_banner", eventImage);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public boolean confirmInput() {
        if (!validateEventTitle(etEventTitle) |
                !validateEventDescription(etEventDescription) |
                !validateContactDetails(etContactDetails) |
                !validateDate(tvSelectDate) |
                !validateTime(tvSelectTime) |
                !validateLocation(tvCurrentLocation)) {
            return false;
        }
        return true;
    }

    private boolean validateLocation(TextView tvCurrentLocation) {
        String location = tvCurrentLocation.getText().toString();
        if (location.isEmpty()) {
            tvCurrentLocation.setError("Field can't be empty");
            tvCurrentLocation.requestFocus();
            return false;
        } else {
            tvCurrentLocation.setError(null);
            return true;
        }
    }

    private boolean validateTime(TextView tvSelectTime) {
        String time = tvSelectTime.getText().toString();
        if (time.isEmpty()) {
            tvSelectTime.setError("Field can't be empty");
            tvSelectTime.requestFocus();
            return false;
        } else {
            tvSelectTime.setError(null);
            return true;
        }
    }

    private boolean validateDate(TextView tvSelectDate) {
        String date = tvSelectDate.getText().toString();
        if (date.isEmpty()) {
            tvSelectDate.setError("Field can't be empty");
            tvSelectDate.requestFocus();
            return false;
        } else {
            tvSelectDate.setError(null);
            return true;
        }
    }

    private boolean validateContactDetails(EditText etContactDetails) {
        String contactDetails = etContactDetails.getText().toString();
        if (contactDetails.isEmpty()) {
            etContactDetails.setError("Field can't be empty");
            etContactDetails.requestFocus();
            return false;
        } else {
            etContactDetails.setError(null);
            return true;
        }
    }

    private boolean validateEventDescription(EditText etEventDescription) {
        String eventDescription = etEventDescription.getText().toString();
        if (eventDescription.isEmpty()) {
            etEventDescription.setError("Field can't be empty");
            etEventDescription.requestFocus();
            return false;
        } else {
            etEventDescription.setError(null);
            return true;
        }
    }

    private boolean validateEventTitle(EditText etEventTitle) {
        String eventTitle = etEventTitle.getText().toString();
        if (eventTitle.isEmpty()) {
            etEventTitle.setError("Field can't be empty");
            etEventTitle.requestFocus();
            return false;
        } else {
            etEventTitle.setError(null);
            return true;
        }
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_IMAGE);
    }

    private void choosePhotoFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    private void selectImageFromGalleryOrTakePictureWithCamera() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Capture from Camera", "Select from Gallery"};
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            takePhotoFromCamera();
                            break;
                        case 1:
                            choosePhotoFromGallery();
                            break;
                    }
                });
        pictureDialog.show();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        try {
            locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    bitmap = BitmapFactory.decodeStream(imageStream);
                    ivSelectedImage.setImageURI(selectedImage);// To display selected image in image view
                    Bitmap bitmap = ((BitmapDrawable) ivSelectedImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    eventImage = Base64.encodeToString(b, Base64.DEFAULT);
                }
                break;

            case TAKE_IMAGE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ivSelectedImage.setImageBitmap(thumbnail);
                    Bitmap bitmap = ((BitmapDrawable) ivSelectedImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    eventImage = Base64.encodeToString(b, Base64.DEFAULT);
                }
                break;
        }
    }
}