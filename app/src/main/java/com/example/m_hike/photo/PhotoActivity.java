package com.example.m_hike.photo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Photo;
import com.example.m_hike.utils.ConfirmationDialog;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PhotoActivity extends AppCompatActivity implements PhotoAdapter.CustomListeners {
    private EditText txtPhotoTitle, txtPhotoDescription;
    private String photoTitle, photoDescription;
    private ImageView imageViewTakePicture;
    private byte[] imageBytes;
    private Dialog photoDialog;
    private PhotoAdapter photoAdapter;
    private long observationId;
    private List<Photo> lstPhoto;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private final int PERMISSION_REQUEST_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        observationId = intent.getLongExtra("observationId", 0);

        Mapping();
    }

    private void Mapping() {
        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPhoto);
        setSupportActionBar(toolbar);

        // Initialize the recyclerView
        recyclerView = findViewById(R.id.recycleViewPhoto);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Load the photos
        lstPhoto = new ArrayList<>();
        lstPhoto = databaseHelper.getAllPhotos(observationId);

        // Set the last assigned id
        if (lstPhoto.size() > 0) {
            Photo.setLastAssignedId(lstPhoto.get(0).getId());
        }

        LoadPhoto(lstPhoto);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadPhoto(List<Photo> lstPhoto) {
        if (photoAdapter == null) {
            photoAdapter = new PhotoAdapter(PhotoActivity.this, R.layout.list_photo_item, lstPhoto);
            photoAdapter.setCustomListeners(PhotoActivity.this);
            recyclerView.setAdapter(photoAdapter);
        } else {
            photoAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_toolbar, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        MenuItem searchItem = menu.findItem(R.id.searchPhoto);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Handle search query submission here if needed
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    photoAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addPhoto) {
            HandleAddPhoto();
        } else if (item.getItemId() == R.id.returnObservation) {
            HandleReturn();
        }
        return super.onOptionsItemSelected(item);
    }

    private void HandleAddPhoto() {
        // Initialize the dialog
        photoDialog = new Dialog(PhotoActivity.this);
        photoDialog.setContentView(R.layout.manage_photo_dialog);
        photoDialog.setCanceledOnTouchOutside(false);

        // Initialize the dialog components
        txtPhotoTitle = photoDialog.findViewById(R.id.txtPhotoTitle);
        txtPhotoDescription = photoDialog.findViewById(R.id.txtPhotoDescription);
        imageViewTakePicture = photoDialog.findViewById(R.id.imageViewTakePicture);
        Button btnTakePicture = photoDialog.findViewById(R.id.btnTakePicture);
        Button btnSavePhoto = photoDialog.findViewById(R.id.btnSavePhoto);
        Button btnClosePhoto = photoDialog.findViewById(R.id.btnClosePhoto);

        // Handle the dialog components
        btnTakePicture.setOnClickListener(view -> HandleTakePicture());

        btnSavePhoto.setOnClickListener(view -> HandleSavePhoto());

        btnClosePhoto.setOnClickListener(view -> photoDialog.dismiss());

        photoDialog.show();
    }

    private void HandleSavePhoto() {
        photoTitle = txtPhotoTitle.getText().toString();
        photoDescription = txtPhotoDescription.getText().toString();

        if (isEmpty(photoTitle)) {
            showToast("Please input the title!");
            return;
        }
        if (imageBytes == null) {
            Toast.makeText(this, "Please take a picture!", Toast.LENGTH_SHORT).show();
            return;
        }

        String pattern = "yyyy-MM-dd hh:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        Photo photo = new Photo(observationId, photoTitle, photoDescription, imageBytes, date);

        String message = "Your inputted data:";
        message += "\nTitle: " + photo.getTitle();
        message += "\nDescription: " + photo.getDescription();
        message += "\nDo you want to save this photo?";

        ConfirmationDialog addConfirmationDialog = new ConfirmationDialog("Confirm your photo!", message);
        addConfirmationDialog.showConfirmationDialog(
                this,
                (dialog, which) -> {
                    databaseHelper.addPhoto(photo);
                    lstPhoto.add(0, photo);
                    LoadPhoto(lstPhoto);
                    photoAdapter.addItem(photo);

                    photoDialog.dismiss();
                });
    }

    private boolean isEmpty(String value) {
        return value.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(PhotoActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                HandleTakePicture();
            }
        }
    }

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        imageViewTakePicture.setImageBitmap(thumbnail);
                        imageBytes = getImageBytes(imageViewTakePicture);
                    }
                }
            }
    );

    private void HandleTakePicture() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intent);
    }

    private void HandleReturn() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onItemClick(Photo photo) {
        // Initialize the dialog
        photoDialog = new Dialog(PhotoActivity.this);
        photoDialog.setContentView(R.layout.manage_photo_dialog);
        photoDialog.setCanceledOnTouchOutside(false);

        // Initialize the dialog components
        txtPhotoTitle = photoDialog.findViewById(R.id.txtPhotoTitle);
        txtPhotoDescription = photoDialog.findViewById(R.id.txtPhotoDescription);
        imageViewTakePicture = photoDialog.findViewById(R.id.imageViewTakePicture);
        Button btnTakePicture = photoDialog.findViewById(R.id.btnTakePicture);
        Button btnSavePhoto = photoDialog.findViewById(R.id.btnSavePhoto);
        Button btnClosePhoto = photoDialog.findViewById(R.id.btnClosePhoto);

        txtPhotoTitle.setText(photo.getTitle());
        txtPhotoDescription.setText(photo.getDescription());
        byte[] imgPhoto = photo.getImageUrl();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgPhoto, 0, imgPhoto.length);
        imageViewTakePicture.setImageBitmap(bitmap);
        imageBytes = getImageBytes(imageViewTakePicture);

        // Handle the dialog components
        btnTakePicture.setOnClickListener(view -> HandleTakePicture());

        btnSavePhoto.setOnClickListener(view -> HandleUpdatePhoto(photo));

        btnClosePhoto.setOnClickListener(view -> photoDialog.dismiss());

        photoDialog.show();
    }

    private void HandleUpdatePhoto(Photo photo) {
        photoTitle = txtPhotoTitle.getText().toString();
        photoDescription = txtPhotoDescription.getText().toString();

        if (isEmpty(photoTitle)) {
            showToast("Please input the title!");
            return;
        }
        if (imageBytes == null) {
            Toast.makeText(this, "Please take a picture!", Toast.LENGTH_SHORT).show();
            return;
        }

        photo.setTitle(photoTitle);
        photo.setDescription(photoDescription);
        photo.setImageUrl(imageBytes);

        String message = "Your inputted data:";
        message += "\nTitle: " + photo.getTitle();
        message += "\nDescription: " + photo.getDescription();
        message += "\nDo you want to update this photo?";

        ConfirmationDialog updateConfirmationDialog = new ConfirmationDialog("Confirm your photo!", message);
        updateConfirmationDialog.showConfirmationDialog(
                this,
                (dialog, which) -> {
                    databaseHelper.updatePhoto(photo);
                    LoadPhoto(lstPhoto);
                    photoAdapter.updateItem(photo);

                    photoDialog.dismiss();
                });
    }

    @Override
    public void deleteItem(Photo photo) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog("Delete photo", "Are you sure you want to delete this photo?");
        confirmationDialog.showConfirmationDialog(this,
                (dialog, which) -> {
                    databaseHelper.deletePhoto(photo.getId());
                    lstPhoto.remove(photo);
                    LoadPhoto(lstPhoto);
                    photoAdapter.deleteItem(photo);
                });
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