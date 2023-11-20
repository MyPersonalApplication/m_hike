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