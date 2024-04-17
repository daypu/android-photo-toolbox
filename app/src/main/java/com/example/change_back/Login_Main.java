package com.example.change_back;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class Login_Main extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        // 在设置布局之前调用
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_login);

        // 初始化控件
        etUsername = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);

        // 找到注册账号按钮
        TextView tvRegister = findViewById(R.id.tv_register);
        // 找到更改密码按钮
        TextView tvForgetPassword = findViewById(R.id.tv_forget);
        // 找到登录按钮
        TextView login = findViewById(R.id.btn_login);

        // 为注册账号按钮设置点击监听器
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterPage();
            }
        });

        // 为更改密码按钮设置点击监听器
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgetPasswordPage();
            }
        });

        // 为登录按钮设置监听器
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Login_Main.this, Function_Selection.class);
                startActivity(intent);
            }
        });
    }

    // 处理注册账号按钮的点击事件
    public void openRegisterPage() {
        Intent intent = new Intent(this, Login_Register.class);
        startActivity(intent);
    }

    // 处理更改密码按钮的点击事件
    public void openForgetPasswordPage() {
        Intent intent = new Intent(this, Change_Password.class);
        startActivity(intent);
    }

}
