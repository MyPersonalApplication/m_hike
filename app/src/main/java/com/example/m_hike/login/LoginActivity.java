package com.example.m_hike.login;

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
import android.widget.Toast;

import com.example.m_hike.utils.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.hike.HikeActivity;
import com.example.m_hike.model.User;
import com.example.m_hike.utils.ConfirmationDialog;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoginAdapter.CustomListeners {
    private Dialog loginDialog;
    private EditText txtUsernameLogin, txtPasswordLogin;
    private DatabaseHelper databaseHelper;
    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;
    private List<User> lstUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        Mapping();
    }

    private void Mapping() {
        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);

        // Initialize the recyclerView
        recyclerView = findViewById(R.id.recyclerViewLogin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list
        lstUser = new ArrayList<>();
        lstUser = databaseHelper.getAllUsers();

        LoadUser(lstUser);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadUser(List<User> lstUser) {
        if (loginAdapter == null) {
            loginAdapter = new LoginAdapter(LoginActivity.this, R.layout.list_account_item, lstUser);
            loginAdapter.setCustomListeners(LoginActivity.this);
            recyclerView.setAdapter(loginAdapter);
        } else {
            loginAdapter.notifyDataSetChanged();
        }

        if (lstUser.size() == 0) {
            HandleAddUser();
        }
    }

    @Override
    public void onItemClick(User user) {
        // Initialize the dialog
        loginDialog = new Dialog(LoginActivity.this);
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setCanceledOnTouchOutside(false);

        HandleLogin(user);
    }

    @Override
    public void deleteItem(User user) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog("Delete user", "Are you sure you want to delete this user?");
        confirmationDialog.showConfirmationDialog(
                this,
                (dialog, which) -> {
                    databaseHelper.DeleteUser(user.getUsername());
                    lstUser.remove(user);
                    LoadUser(lstUser);
                }
        );
    }

    private void HandleLogin(User user) {
        // Initialize the dialog's components
        txtUsernameLogin = loginDialog.findViewById(R.id.txtUsernameLogin);
        txtPasswordLogin = loginDialog.findViewById(R.id.txtPasswordLogin);
        Button btnLogin = loginDialog.findViewById(R.id.btnLogin);
        Button btnCancelLogin = loginDialog.findViewById(R.id.btnCloseLogin);

        txtUsernameLogin.setText(user.getUsername());

        btnLogin.setOnClickListener(view -> {
            if (txtUsernameLogin.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "Please input your username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtPasswordLogin.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "Please input your password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!txtUsernameLogin.getText().toString().equals(user.getUsername()) || !txtPasswordLogin.getText().toString().equals(user.getPassword())) {
                Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginSuccess();
            loginDialog.dismiss();
        });

        btnCancelLogin.setOnClickListener(view -> loginDialog.dismiss());

        loginDialog.show();
    }

    private final ActivityResultLauncher<Intent> loginSuccessLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Toast.makeText(LoginActivity.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void LoginSuccess() {
        Intent messageIntent = new Intent(LoginActivity.this, HikeActivity.class);
        messageIntent.putExtra("username", txtUsernameLogin.getText().toString());
        loginSuccessLauncher.launch(messageIntent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_toolbar, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            HandleAddUser();
        } else if (item.getItemId() == R.id.exit) {
            ConfirmationDialog confirmationDialog = new ConfirmationDialog("Close the program!", "Do you want to exit?");
            confirmationDialog.showConfirmationDialog(
                    this,
                    (dialog, which) -> this.finishAffinity()
            );
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<Intent> editUserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        User user = (User) data.getSerializableExtra("userObj");
                        if (user != null) {
                            databaseHelper.updateUser(user);
                            for (User p : lstUser) {
                                if (p.getUsername().equals(user.getUsername())) {
                                    p.setFullName(user.getFullName());
                                    p.setPassword(user.getPassword());
                                    p.setAvatar(user.getAvatar());
                                    break;
                                }
                            }
                        }
                    }
                }
                LoadUser(lstUser);
            }
    );

    @Override
    public void editItem(User user) {
        Intent messageIntent = new Intent(LoginActivity.this, ManageUserActivity.class);
        messageIntent.putExtra("userObj", user);
        editUserLauncher.launch(messageIntent);
    }

    private final ActivityResultLauncher<Intent> addUserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        User reply = (User) data.getSerializableExtra("userObj");
                        if (reply != null) {
                            databaseHelper.addNewUser(reply);
                            lstUser.add(0, reply);
                            LoadUser(lstUser);
                        }
                    }
                }
            }
    );

    private void HandleAddUser() {
        Intent messageIntent = new Intent(LoginActivity.this, ManageUserActivity.class);
        addUserLauncher.launch(messageIntent);
    }
}