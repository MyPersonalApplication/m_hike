package com.example.m_hike;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.m_hike.login.LoginActivity;
import com.example.m_hike.model.Hike;
import com.example.m_hike.model.Observation;
import com.example.m_hike.model.Photo;
import com.example.m_hike.model.User;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        databaseHelper.getWritableDatabase();

        String pattern = "yyyy-MM-dd hh:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        // Add 2 users to the database if it is empty
        if (databaseHelper.getAllUsers().size() == 0) {
            // Get default_avatar from drawable and assign to imageBytes
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_avatar);
            byte[] imageBytes = getImageBytes(bitmap);

            databaseHelper.addNewUser(new User("quangnd", "Quang Nguyen", "123", imageBytes, date));
            databaseHelper.addNewUser(new User("duongnt", "Duong Nguyen", "123", imageBytes, date));
        }

        // Add 4 hikes to the database if it is empty
        if (databaseHelper.getAllHikes("quangnd").size() == 0 && databaseHelper.getAllHikes("duongnt").size() == 0) {
            databaseHelper.saveHike(new Hike(
                    "quangnd",
                    "Hike 1",
                    "Hike 1 location",
                    10.0f,
                    10.0f,
                    date,
                    "Yes",
                    10.0f,
                    "Easy",
                    "Hike 1 description"));
            databaseHelper.saveHike(new Hike(
                    "quangnd",
                    "Hike 2",
                    "Hike 2 location",
                    20.0f,
                    20.0f,
                    date,
                    "Yes",
                    20.0f,
                    "Easy",
                    "Hike 2 description"));
            databaseHelper.saveHike(new Hike(
                    "duongnt",
                    "Hike 3",
                    "Hike 3 location",
                    30.0f,
                    30.0f,
                    date,
                    "Yes",
                    30.0f,
                    "Easy",
                    "Hike 3 description"));
            databaseHelper.saveHike(new Hike(
                    "duongnt",
                    "Hike 4",
                    "Hike 4 location",
                    40.0f,
                    40.0f,
                    date,
                    "Yes",
                    40.0f,
                    "Easy",
                    "Hike 4 description"));
        }

        // Add 8 observations to the database if it is empty
        if (
                databaseHelper.getAllObservations(1).size() == 0 &&
                databaseHelper.getAllObservations(2).size() == 0 &&
                databaseHelper.getAllObservations(3).size() == 0 &&
                databaseHelper.getAllObservations(4).size() == 0
        ) {
            databaseHelper.addObservation(new Observation(1, "Observation 1", date, "Observation 1 comment"));
            databaseHelper.addObservation(new Observation(1, "Observation 2", date, "Observation 2 comment"));
            databaseHelper.addObservation(new Observation(2, "Observation 3", date, "Observation 3 comment"));
            databaseHelper.addObservation(new Observation(2, "Observation 4", date, "Observation 4 comment"));
            databaseHelper.addObservation(new Observation(3, "Observation 5", date, "Observation 5 comment"));
            databaseHelper.addObservation(new Observation(3, "Observation 6", date, "Observation 6 comment"));
            databaseHelper.addObservation(new Observation(4, "Observation 7", date, "Observation 7 comment"));
            databaseHelper.addObservation(new Observation(4, "Observation 8", date, "Observation 8 comment"));
        }

        // Add 16 photos to the database if it is empty
        if (
                databaseHelper.getAllPhotos(1).size() == 0 &&
                databaseHelper.getAllPhotos(2).size() == 0 &&
                databaseHelper.getAllPhotos(3).size() == 0 &&
                databaseHelper.getAllPhotos(4).size() == 0 &&
                databaseHelper.getAllPhotos(5).size() == 0 &&
                databaseHelper.getAllPhotos(6).size() == 0 &&
                databaseHelper.getAllPhotos(7).size() == 0 &&
                databaseHelper.getAllPhotos(8).size() == 0
        ) {
            // Get default_avatar from drawable and assign to imageBytes
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_avatar);
            byte[] imageBytes = getImageBytes(bitmap);

            databaseHelper.addPhoto(new Photo(1, "Photo 1", "Description 1", imageBytes, date));
            databaseHelper.addPhoto(new Photo(1, "Photo 2", "Description 2", imageBytes, date));
            databaseHelper.addPhoto(new Photo(2, "Photo 3", "Description 3", imageBytes, date));
            databaseHelper.addPhoto(new Photo(2, "Photo 4", "Description 4", imageBytes, date));
            databaseHelper.addPhoto(new Photo(3, "Photo 5", "Description 5", imageBytes, date));
            databaseHelper.addPhoto(new Photo(3, "Photo 6", "Description 6", imageBytes, date));
            databaseHelper.addPhoto(new Photo(4, "Photo 7", "Description 7", imageBytes, date));
            databaseHelper.addPhoto(new Photo(4, "Photo 8", "Description 8", imageBytes, date));
            databaseHelper.addPhoto(new Photo(5, "Photo 9", "Description 9", imageBytes, date));
            databaseHelper.addPhoto(new Photo(5, "Photo 10", "Description 10", imageBytes, date));
            databaseHelper.addPhoto(new Photo(6, "Photo 11", "Description 11", imageBytes, date));
            databaseHelper.addPhoto(new Photo(6, "Photo 12", "Description 12", imageBytes, date));
            databaseHelper.addPhoto(new Photo(7, "Photo 13", "Description 13", imageBytes, date));
            databaseHelper.addPhoto(new Photo(7, "Photo 14", "Description 14", imageBytes, date));
            databaseHelper.addPhoto(new Photo(8, "Photo 15", "Description 15", imageBytes, date));
            databaseHelper.addPhoto(new Photo(8, "Photo 16", "Description 16", imageBytes, date));
        }

        Mapping();
    }

    private byte[] getImageBytes(Bitmap bitmap) {
        // Convert the Bitmap object to a byte array
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);

        return byteArray.toByteArray();
    }

    private void Mapping() {
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> handleStartButton());
    }

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void handleStartButton() {
        Intent messageIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginLauncher.launch(messageIntent);
    }
}