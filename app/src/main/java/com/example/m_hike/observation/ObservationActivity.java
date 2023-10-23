package com.example.m_hike.observation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Observation;
import com.example.m_hike.photo.PhotoActivity;
import com.example.m_hike.utils.ConfirmationDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObservationActivity extends AppCompatActivity implements ObservationAdapter.CustomListeners {
    private Dialog observationDialog;
    private EditText txtObservationName, txtObservationComment;
    private String observationName, observationComment;
    private long hikeId;
    private ObservationAdapter observationAdapter;
    private List<Observation> lstObservation;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        hikeId = intent.getLongExtra("hikeId", 0);

        Mapping();
    }

    private void Mapping() {
        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarObservation);
        setSupportActionBar(toolbar);

        // Initialize the recyclerView
        recyclerView = findViewById(R.id.recycleViewObservation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the observations
        lstObservation = new ArrayList<>();
        lstObservation = databaseHelper.getAllObservations(hikeId);

        LoadObservation(lstObservation);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadObservation(List<Observation> lstObservation) {
        if (observationAdapter == null) {
            observationAdapter = new ObservationAdapter(ObservationActivity.this, R.layout.list_observation_item, lstObservation);
            observationAdapter.setCustomListeners(ObservationActivity.this);
            recyclerView.setAdapter(observationAdapter);
        } else {
            observationAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.observation_toolbar, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        MenuItem searchItem = menu.findItem(R.id.searchObservation);
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
                    observationAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addObservation) {
            HandleAddObservation();
        } else if (item.getItemId() == R.id.returnHike) {
            HandleReturn();
        }
        return super.onOptionsItemSelected(item);
    }

    private void HandleReturn() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void HandleAddObservation() {
        // Initialize the dialog
        observationDialog = new Dialog(ObservationActivity.this);
        observationDialog.setContentView(R.layout.manage_observation_dialog);
        observationDialog.setCanceledOnTouchOutside(false);

        // Initialize the dialog components
        txtObservationName = observationDialog.findViewById(R.id.txtObservationName);
        txtObservationComment = observationDialog.findViewById(R.id.txtObservationComment);
        Button btnSaveObservation = observationDialog.findViewById(R.id.btnSaveObservation);
        Button btnCloseObservation = observationDialog.findViewById(R.id.btnCloseObservation);

        btnSaveObservation.setOnClickListener(view -> {
            observationName = txtObservationName.getText().toString();
            observationComment = txtObservationComment.getText().toString();

            if (isEmpty(observationName)) {
                showToast("Please enter the observation name!");
                return;
            }
            if (isEmpty(observationComment)) {
                showToast("Please enter the observation comment!");
                return;
            }

            String pattern = "yyyy-MM-dd hh:mm:ss";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());

            Observation observation = new Observation(hikeId, observationName, date, observationComment);
            databaseHelper.addObservation(observation);
            lstObservation.add(0, observation);
            LoadObservation(lstObservation);
            observationAdapter.addItem(observation);

            observationDialog.dismiss();
        });

        btnCloseObservation.setOnClickListener(view -> observationDialog.dismiss());

        observationDialog.show();
    }

    private boolean isEmpty(String value) {
        return value.trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(ObservationActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> gotoPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Toast.makeText(ObservationActivity.this, "Return successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void onItemClick(Observation observation) {
        Intent messageIntent = new Intent(ObservationActivity.this, PhotoActivity.class);
        messageIntent.putExtra("observationId", observation.getId());
        gotoPhotoLauncher.launch(messageIntent);
    }

    @Override
    public void editItem(Observation observation) {
        // Initialize the dialog
        observationDialog = new Dialog(ObservationActivity.this);
        observationDialog.setContentView(R.layout.manage_observation_dialog);
        observationDialog.setCanceledOnTouchOutside(false);

        // Initialize the dialog components'
        txtObservationName = observationDialog.findViewById(R.id.txtObservationName);
        txtObservationComment = observationDialog.findViewById(R.id.txtObservationComment);
        Button btnSaveObservation = observationDialog.findViewById(R.id.btnSaveObservation);
        Button btnCloseObservation = observationDialog.findViewById(R.id.btnCloseObservation);

        txtObservationName.setText(observation.getName());
        txtObservationComment.setText(observation.getAdditionalComment());

        btnSaveObservation.setOnClickListener(view -> {
            observationName = txtObservationName.getText().toString();
            observationComment = txtObservationComment.getText().toString();

            if (isEmpty(observationName)) {
                showToast("Please enter the observation name!");
                return;
            }
            if (isEmpty(observationComment)) {
                showToast("Please enter the observation comment!");
                return;
            }

            observation.setName(observationName);
            observation.setAdditionalComment(observationComment);
            databaseHelper.UpdateObservation(observation);
            LoadObservation(lstObservation);
            observationAdapter.updateItem(observation);
            observationDialog.dismiss();
        });

        btnCloseObservation.setOnClickListener(view -> observationDialog.dismiss());

        observationDialog.show();
    }

    @Override
    public void deleteItem(Observation observation) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog("Delete observation", "Are you sure you want to delete this observation?");
        confirmationDialog.showConfirmationDialog(
                this,
                (dialog, which) -> {
                    databaseHelper.DeleteObservation(observation.getId());
                    lstObservation.remove(observation);
                    LoadObservation(lstObservation);
                    observationAdapter.deleteItem(observation);
                }
        );
    }
}