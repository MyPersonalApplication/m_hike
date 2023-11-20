package com.example.m_hike.hike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.model.Hike;
import com.example.m_hike.utils.ConfirmationDialog;
import com.example.m_hike.utils.LocationHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ManageHikeActivity extends AppCompatActivity implements LocationHelper.LocationListener {
    private EditText txtHikeName, txtHikeLocation, txtHikeLatitude, txtHikeLongitude, getTxtHikeLength, txtHikeDescription;
    private String hikeName, hikeLocation, hikeLatitude, hikeLongitude, hikeParkingAvailable, hikeLength, hikeDifficulty, hikeDescription;
    private Spinner spHikeDifficulty;
    private RadioGroup rgParkingAvailable;
    private String username;
    private Hike hikeObj;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_hike);

        // Get the username from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Mapping();
    }

    private void Mapping() {
        txtHikeName = findViewById(R.id.txtHikeName);
        txtHikeLocation = findViewById(R.id.txtHikeLocation);
        txtHikeLatitude = findViewById(R.id.txtHikeLatitude);
        txtHikeLongitude = findViewById(R.id.txtHikeLongitude);
        rgParkingAvailable = findViewById(R.id.rgParkingAvailable);
        getTxtHikeLength = findViewById(R.id.txtHikeLength);
        spHikeDifficulty = findViewById(R.id.spDifficultyLevel);
        txtHikeDescription = findViewById(R.id.txtHikeDescription);
        Button btnSave = findViewById(R.id.btnSaveHike);
        Button btnCancel = findViewById(R.id.btnCancelHike);

        // Get the intent to check if it is from the edit button
        Intent intent = getIntent();
        hikeObj = (Hike) intent.getSerializableExtra("hikeObj");
        if (hikeObj != null) {
            txtHikeName.setText(hikeObj.getName());
            txtHikeLocation.setText(hikeObj.getLocation());
            txtHikeLatitude.setText(String.valueOf(hikeObj.getLatitude()));
            txtHikeLongitude.setText(String.valueOf(hikeObj.getLongitude()));

            RadioButton rbParkingAvailable = findViewById(
                    hikeObj.getParkingAvailable().equals("Yes") ? R.id.rbParkingYes : R.id.rbParkingNo
            );
            rbParkingAvailable.setChecked(true);

            getTxtHikeLength.setText(String.valueOf(hikeObj.getLength()));

            int difficultySelection = 2; // Default to "Hard"
            if (hikeObj.getDifficultyLevel().equals("Easy")) {
                difficultySelection = 0;
            } else if (hikeObj.getDifficultyLevel().equals("Medium")) {
                difficultySelection = 1;
            }
            spHikeDifficulty.setSelection(difficultySelection);
            txtHikeDescription.setText(hikeObj.getDescription());
        } else {
            // Get the location
            locationHelper = new LocationHelper(this);
            locationHelper.setLocationListener(this);
            locationHelper.requestLocation();
        }

        btnSave.setOnClickListener(view -> {
            RadioButton rbParkingAvailable = findViewById(rgParkingAvailable.getCheckedRadioButtonId());

            hikeName = txtHikeName.getText().toString();
            hikeLocation = txtHikeLocation.getText().toString();
            hikeLatitude = txtHikeLatitude.getText().toString();
            hikeLongitude = txtHikeLongitude.getText().toString();
            hikeLength = getTxtHikeLength.getText().toString();
            hikeDifficulty = spHikeDifficulty.getSelectedItem().toString();
            hikeDescription = txtHikeDescription.getText().toString();

            if (isEmpty(hikeName)) {
                showToast("Hike name is required");
                return;
            }
            if (isEmpty(hikeLocation)) {
                showToast("Hike location is required");
                return;
            }
            if (isEmpty(hikeLatitude)) {
                showToast("Hike latitude is required");
                return;
            }
            if (isNumber(hikeLatitude)) {
                showToast("Hike latitude must be a number");
                return;
            }
            if (isEmpty(hikeLongitude)) {
                showToast("Hike longitude is required");
                return;
            }
            if (isNumber(hikeLongitude)) {
                showToast("Hike longitude must be a number");
                return;
            }
            if (rbParkingAvailable == null) {
                showToast("Hike parking availability is required");
                return;
            }
            hikeParkingAvailable = rbParkingAvailable.getText().toString();
            if (isEmpty(hikeLength)) {
                showToast("Hike length is required");
                return;
            }
            if (isNumber(hikeLength)) {
                showToast("Hike length must be a number");
                return;
            }
            if (isEmpty(hikeDifficulty)) {
                showToast("Hike difficulty is required");
                return;
            }

            HandleSaveHike();
        });

        btnCancel.setOnClickListener(view -> HandleCancelHike());
    }

    private boolean isEmpty(String value) {
        return value.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(ManageHikeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void HandleSaveHike() {
        Intent returnIntent = new Intent();
        if (hikeObj != null) {  // Edit mode
            hikeObj.setName(hikeName);
            hikeObj.setLocation(hikeLocation);
            hikeObj.setLatitude(Float.parseFloat(hikeLatitude));
            hikeObj.setLongitude(Float.parseFloat(hikeLongitude));
            hikeObj.setParkingAvailable(hikeParkingAvailable);
            hikeObj.setLength(Float.parseFloat(hikeLength));
            hikeObj.setDifficultyLevel(hikeDifficulty);
            hikeObj.setDescription(hikeDescription);

            String message = "Your inputted data:";
            message += "\nName: " + hikeObj.getName();
            message += "\nLocation: " + hikeObj.getLocation();
            message += "\nDate created: " + hikeObj.getDate();
            message += "\nParking available: " + hikeObj.getParkingAvailable();
            message += "\nLength: " + hikeObj.getLength();
            message += "\nDifficulty level: " + hikeObj.getDifficultyLevel();
            message += "\nDo you want to save this hike?";

            ConfirmationDialog updateConfirmationDialog = new ConfirmationDialog("Confirm your hike!", message);
            updateConfirmationDialog.showConfirmationDialog(
                    this,
                    (dialog, which) -> {
                        returnIntent.putExtra("hikeObj", hikeObj);
                        setResult(Activity.RESULT_OK, returnIntent);

                        finish();
                    });
        } else {    // Add mode
            String pattern = "yyyy-MM-dd hh:mm:ss";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());

            Hike hike = new Hike(
                    username,
                    hikeName,
                    hikeLocation,
                    Float.parseFloat(hikeLatitude),
                    Float.parseFloat(hikeLongitude),
                    date,
                    hikeParkingAvailable,
                    Float.parseFloat(hikeLength),
                    hikeDifficulty,
                    hikeDescription
            );

            String message = "Your inputted data:";
            message += "\nName: " + hike.getName();
            message += "\nLocation: " + hike.getLocation();
            message += "\nDate created: " + date;
            message += "\nParking available: " + hike.getParkingAvailable();
            message += "\nLength: " + hike.getLength();
            message += "\nDifficulty level: " + hike.getDifficultyLevel();
            message += "\nDo you want to save this hike?";

            ConfirmationDialog addConfirmationDialog = new ConfirmationDialog("Confirm your hike!", message);
            addConfirmationDialog.showConfirmationDialog(
                    this,
                    (dialog, which) -> {
                        returnIntent.putExtra("hikeObj", hike);
                        setResult(Activity.RESULT_OK, returnIntent);

                        finish();
                    });
        }
    }

    private void HandleCancelHike() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(ManageHikeActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            return obj.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public void onLocationReceived(double latitude, double longitude) {
        if (locationHelper != null) {
            txtHikeLatitude.setText(String.valueOf(latitude));
            txtHikeLongitude.setText(String.valueOf(longitude));
            String currentLocation = getAddress(latitude, longitude);
            if (currentLocation != null) {
                txtHikeLocation.setText(currentLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        return !pattern.matcher(str).matches();
    }
}