package com.example.m_hike.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.m_hike.R;

public class ConfirmationDialog {
    private final String title;
    private final String content;

    public ConfirmationDialog(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void showConfirmationDialog(Context context, DialogInterface.OnClickListener yesClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setTitle(this.title);
        builder.setMessage(this.content);

        builder.setPositiveButton("Yes", yesClickListener);

        builder.setNegativeButton("No", (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
