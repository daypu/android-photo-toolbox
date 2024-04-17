package com.example.change_back;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Function_Selection extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);

        //// 加载底部导航栏的 Fragment
        //getSupportFragmentManager().beginTransaction()
        //        .replace(R.id.fragment_container, new BottomNavigationFragment())
        //        .commit();

        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 MainActivity
                Intent intent = new Intent(Function_Selection.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 MainActivity
                Intent intent = new Intent(Function_Selection.this, change_clothes.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 MainActivity
                Intent intent = new Intent(Function_Selection.this, BW.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton4 = findViewById(R.id.imageButton4);
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 MainActivity
                Intent intent = new Intent(Function_Selection.this, Animation.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton5 = findViewById(R.id.imageButton5);
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 MainActivity
                Intent intent = new Intent(Function_Selection.this, trans_style.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton6 = findViewById(R.id.imageButton6);
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 MainActivity
                Intent intent = new Intent(Function_Selection.this, img_enhance.class);
                startActivity(intent);
            }
        });


    }

    public void onBackPressed()
    {
        // 阻止返回键操作
        // 这里可以添加提示信息或其他逻辑
    }
}
