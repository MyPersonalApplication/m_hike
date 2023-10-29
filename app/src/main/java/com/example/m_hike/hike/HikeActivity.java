package com.example.m_hike.hike;

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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Hike;
import com.example.m_hike.observation.ObservationActivity;
import com.example.m_hike.utils.ConfirmationDialog;

import java.util.ArrayList;
import java.util.List;

public class HikeActivity extends AppCompatActivity implements HikeAdapter.CustomListeners {
    private String username;
    private HikeAdapter hikeAdapter;
    private TextView tvTotalHikes, tvTotalLength;
    private List<Hike> lstHike;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Spinner spSearchCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Mapping();
    }

    private void Mapping() {
        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);

        // Initialize the recyclerView
        recyclerView = findViewById(R.id.recycleViewHike);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the lstHike
        lstHike = new ArrayList<>();
        lstHike = databaseHelper.getAllHikes(username);

        // Set the last assigned id
        Hike.setLastAssignedId(lstHike.get(0).getId());

        // Initialize the spSearchCriteria
        spSearchCriteria = findViewById(R.id.spSearchCriteria);
        spSearchCriteria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCriteria = spSearchCriteria.getSelectedItem().toString();
                hikeAdapter.setSearchCriteria(selectedCriteria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case when nothing is selected if needed
            }
        });

        // Initialize the searchView
        SearchView searchView = findViewById(R.id.svSearchHike);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission here if needed
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hikeAdapter.getFilter().filter(newText);
                return true;
            }
        });

        LoadHike(lstHike);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadHike(List<Hike> lstHike) {
        if (hikeAdapter == null) {
            hikeAdapter = new HikeAdapter(HikeActivity.this, R.layout.list_hike_item, lstHike);
            hikeAdapter.setCustomListeners(HikeActivity.this);
            recyclerView.setAdapter(hikeAdapter);
        } else {
            hikeAdapter.notifyDataSetChanged();
        }

        tvTotalHikes = findViewById(R.id.tvTotalHikes);
        tvTotalLength = findViewById(R.id.tvTotalLength);
        Statistic();
    }

    @SuppressLint("SetTextI18n")
    private void Statistic() {
        tvTotalHikes.setText(String.valueOf(lstHike.size()));
        if (lstHike.size() == 0) {
            tvTotalLength.setText("0");
        } else {
            tvTotalLength.setText(CalculateTotalLength() + " km");
        }
    }

    private String CalculateTotalLength() {
        Float result = 0F;
        for (Hike hike : lstHike) {
            result += hike.getLength();
        }
        return result.toString();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hike_toolbar, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addHike) {
            AddHikeHandler();
        } else if (item.getItemId() == R.id.logout) {
            ConfirmationDialog confirmationDialog = new ConfirmationDialog("Logout", "Do you want to logout?");
            confirmationDialog.showConfirmationDialog(
                    this,
                    (dialog, which) -> HandleLogout()
            );
        }
        return super.onOptionsItemSelected(item);
    }

    private void HandleLogout() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private final ActivityResultLauncher<Intent> gotoObservationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Toast.makeText(HikeActivity.this, "Returned to the hike page", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void onItemClick(Hike hike) {
        Intent messageIntent = new Intent(HikeActivity.this, ObservationActivity.class);
        messageIntent.putExtra("hikeId", hike.getId());
        gotoObservationLauncher.launch(messageIntent);
    }

    private final ActivityResultLauncher<Intent> editHikeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Hike hike = (Hike) data.getSerializableExtra("hikeObj");
                        if (hike != null) {
                            databaseHelper.updateHike(hike);
                            for (Hike h : lstHike) {
                                if (h.getId() == hike.getId()) {
                                    h.setName(hike.getName());
                                    h.setLocation(hike.getLocation());
                                    h.setLatitude(hike.getLatitude());
                                    h.setLongitude(hike.getLongitude());
                                    h.setParkingAvailable(hike.getParkingAvailable());
                                    h.setLength(hike.getLength());
                                    h.setDifficultyLevel(hike.getDifficultyLevel());
                                    h.setDescription(hike.getDescription());
                                    hikeAdapter.updateItem(h);
                                    break;
                                }
                            }
                        }
                    }
                }
                LoadHike(lstHike);
            }
    );

    @Override
    public void editItem(Hike hike) {
        Intent messageIntent = new Intent(HikeActivity.this, ManageHikeActivity.class);
        messageIntent.putExtra("hikeObj", hike);
        editHikeLauncher.launch(messageIntent);
    }

    @Override
    public void deleteItem(Hike hike) {
        ConfirmationDialog deleteConfirmationDialog = new ConfirmationDialog("Delete hike!", "Do you want to delete this hike?");
        deleteConfirmationDialog.showConfirmationDialog(
                this,
                (dialog, which) -> {
                    databaseHelper.deleteHike(hike.getId());
                    lstHike.remove(hike);
                    LoadHike(lstHike);
                    hikeAdapter.deleteItem(hike);
                });
    }

    private final ActivityResultLauncher<Intent> addHikeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Hike reply = (Hike) data.getSerializableExtra("hikeObj");
                        if (reply != null) {
                            databaseHelper.saveHike(reply);
                            lstHike.add(0, reply);
                            LoadHike(lstHike);
                            hikeAdapter.addItem(reply);
                        }
                    }
                }
            }
    );

    private void AddHikeHandler() {
        Intent messageIntent = new Intent(HikeActivity.this, ManageHikeActivity.class);
        messageIntent.putExtra("username", username);
        addHikeLauncher.launch(messageIntent);
    }
}