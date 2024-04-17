package com.example.change_back;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Login_Register extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 在初始化视图之前设置布局
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        client = new OkHttpClient();

        // 查找并设置注册按钮的点击监听器
        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                sendRegistrationData(username, password);
                openMainActivity();
            }
        });

        // 查找并设置“已有账号？”文本的点击监听器
        TextView tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

    }



    // 打开主活动的方法
    private void openMainActivity()
    {
        Intent intent = new Intent(this, Login_Main.class);
        startActivity(intent);
    }


    public void onBackPressed()
    {
        // 阻止返回键操作
        // 这里可以添加提示信息或其他逻辑
    }

    private void sendRegistrationData(String username, String password) {
        try {
            // 创建 JSON 对象并添加账号和密码字段
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);

            // 创建 JSON 请求体
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());

            // 创建请求对象
            Request request = new Request.Builder()
                    .url("http://172.164.35.255:9090")
                    .post(requestBody)
                    .build();

            // 发送请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login_Register.this, "注册失败，请检查你的网络连接。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login_Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                openMainActivity();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login_Register.this, "注册失败，请重试。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
