package com.example.change_back;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

public class Change_Password extends AppCompatActivity {

    private static final String TAG = "Change_Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the window decor flags before setting the layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.change_password);

        // Find the change password button
        Button btnChangePassword = findViewById(R.id.btn_change_password);

        // Set click listener for the change password button
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginMainActivity();
            }
        });
    }

    // Handle the change password button click event, navigate to Login_MainActivity
    private void openLoginMainActivity() {
        String enteredUsername = getEnteredUsername(); // Get the entered username
        String enteredOldPassword = getEnteredOldPassword(); // Get the entered old password
        String newEnteredPassword = getEnteredNewPassword(); // Get the entered new password

        if (verifyOldPassword(enteredUsername, enteredOldPassword)) {
            updatePasswordInDatabase(enteredUsername, newEnteredPassword);
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login_Main.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "原密码不正确", Toast.LENGTH_SHORT).show();
        }
    }

    // Get the entered username from EditText
    private String getEnteredUsername() {
        EditText etUsername = findViewById(R.id.et_old_id);
        return etUsername.getText().toString().trim();
    }

    // Get the entered old password from EditText
    private String getEnteredOldPassword() {
        EditText etOldPassword = findViewById(R.id.et_old_password);
        return etOldPassword.getText().toString().trim();
    }

    // Get the entered new password from EditText
    private String getEnteredNewPassword() {
        EditText etNewPassword = findViewById(R.id.et_new_password);
        return etNewPassword.getText().toString().trim();
    }

    // Verify the old password against the database
    private boolean verifyOldPassword(String username, String oldPassword) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"password"};
        String selection = "username = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                DBHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String originalPassword = null;
        if (cursor != null && cursor.moveToFirst()) {
            originalPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            cursor.close();
        }

        db.close();
        return originalPassword != null && originalPassword.equals(oldPassword);
    }

    // Update the password in the database
    private void updatePasswordInDatabase(String username, String newPassword) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        String selection = "username = ?";
        String[] selectionArgs = {username};

        db.update(DBHelper.TABLE_USERS, values, selection, selectionArgs);

        db.close();
    }
}
