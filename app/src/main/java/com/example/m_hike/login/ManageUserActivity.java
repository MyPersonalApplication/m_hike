package com.example.m_hike.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.m_hike.R;
import com.example.m_hike.model.User;
import com.example.m_hike.utils.ConfirmationDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageUserActivity extends AppCompatActivity {
    private EditText txtUsername, txtFullName, txtPassword, txtConfirmPassword;
    private String username, fullName, password, confirmPassword;
    private ImageView chooseAvatar;
    private byte[] imageBytes;
    private User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        Mapping();
    }

    private void Mapping() {
        txtUsername = findViewById(R.id.txtUsername);
        txtFullName = findViewById(R.id.txtFullName);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        chooseAvatar = findViewById(R.id.uploadAvatar);
        Button btnConfirmCreateUser = findViewById(R.id.btnConfirmCreateUser);
        Button btnCancelCreateUser = findViewById(R.id.btnCancelCreateUser);

        // Get the intent to check if it is from the edit button
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("userObj");
        if (userObj != null) {
            txtUsername.setEnabled(false);
            txtUsername.setFocusable(false);

            txtUsername.setText(userObj.getUsername());
            txtFullName.setText(userObj.getFullName());

            byte[] avatarBytes = userObj.getAvatar();
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            chooseAvatar.setImageBitmap(bitmap);
            imageBytes = getImageBytes(chooseAvatar);
        }

        // Choose image
        chooseAvatar.setOnClickListener(this::chooseImage);
        // Handle confirm button
        btnConfirmCreateUser.setOnClickListener(view -> {
            username = txtUsername.getText().toString();
            fullName = txtFullName.getText().toString();
            password = txtPassword.getText().toString();
            confirmPassword = txtConfirmPassword.getText().toString();

            if (isEmpty(username)) {
                showToast("Please input your username!");
                return;
            }

            if (isEmpty(fullName)) {
                showToast("Please input your full name!");
                return;
            }

            if (userObj == null) {
                if (isEmpty(password)) {
                    showToast("Please input your password!");
                    return;
                }

                if (isEmpty(confirmPassword)) {
                    showToast("Please confirm your password!");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    showToast("Password and confirm password must be the same!");
                    return;
                }
            } else {
                if (!isEmpty(password)) {
                    if (isEmpty(confirmPassword)) {
                        showToast("Please confirm your password!");
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        showToast("Password and confirm password must be the same!");
                        return;
                    }
                }
            }

            if (imageBytes == null) {
                // Get default_avatar from drawable and assign to imageBytes
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_avatar);
                chooseAvatar.setImageBitmap(bitmap);
                imageBytes = getImageBytes(chooseAvatar);
            }

            HandleSaveUser();
        });

        // Handle cancel button
        btnCancelCreateUser.setOnClickListener(view -> HandleCancelUser());
    }

    private boolean isEmpty(String value) {
        return value.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(ManageUserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void HandleCancelUser() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void HandleSaveUser() {
        Intent returnIntent = new Intent();
        if (userObj != null) {  // Edit mode
            userObj.setFullName(fullName);
            if (!password.equals(""))
                userObj.setPassword(password);
            userObj.setAvatar(imageBytes);

            String message = "Your inputted data:";
            message += "\nUsername: " + userObj.getUsername();
            message += "\nFull name: " + userObj.getFullName();

            ConfirmationDialog updateConfirmationDialog = new ConfirmationDialog("Confirm your account!", message);
            updateConfirmationDialog.showConfirmationDialog(
                    this,
                    (dialog, which) -> {
                        returnIntent.putExtra("userObj", userObj);
                        setResult(Activity.RESULT_OK, returnIntent);

                        finish();
                    });
        } else {    // Add mode
            String pattern = "yyyy-MM-dd hh:mm:ss";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());

            User addUser = new User(
                    username,
                    fullName,
                    password,
                    imageBytes,
                    date
            );

            String message = "Your inputted data:";
            message += "\nUsername: " + addUser.getUsername();
            message += "\nFull name: " + addUser.getFullName();

            ConfirmationDialog addConfirmationDialog = new ConfirmationDialog("Confirm your account!", message);
            addConfirmationDialog.showConfirmationDialog(
                    this,
                    (dialog, which) -> {
                        returnIntent.putExtra("userObj", addUser);
                        setResult(Activity.RESULT_OK, returnIntent);

                        finish();
                    });
        }
    }

    private final ActivityResultLauncher<Intent> chooseImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageFileBath = data.getData();
                        Bitmap imageToStore;
                        try {
                            imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFileBath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        chooseAvatar.setImageBitmap(imageToStore);

                        imageBytes = getImageBytes(chooseAvatar);

                        FileOutputStream fileOutputStream;
                        try {
                            fileOutputStream = new FileOutputStream("image.png");
                            fileOutputStream.write(imageBytes);
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        chooseImageLauncher.launch(intent);
    }

    private byte[] getImageBytes(ImageView imgTaskImage) {
        // Get the BitmapDrawable object from the ImageView object
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imgTaskImage.getDrawable();
        // Get the Bitmap object from the BitmapDrawable object
        Bitmap bitmap = bitmapDrawable.getBitmap();
        // Convert the Bitmap object to a byte array
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);

        return byteArray.toByteArray();
    }
}