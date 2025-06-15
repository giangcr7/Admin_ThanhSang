package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends Activity {

    EditText edtUsername, edtPassword;
    Button btnLogin, btnGotoRegister;
    CheckBox chkRemember;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean rememberMe = preferences.getBoolean("rememberMe", false);

        if (rememberMe) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGotoRegister = (Button) findViewById(R.id.btnGotoRegister);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);

        if (preferences.getBoolean("rememberMe", false)) {
            edtUsername.setText(preferences.getString("email", ""));
            edtPassword.setText(preferences.getString("matkhau", ""));
            chkRemember.setChecked(true);
        } else {
            edtUsername.setText(""); // Để trống nếu không nhớ
            edtPassword.setText("");
            chkRemember.setChecked(false);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginUser().execute(username, password);
                }
            }
        });

        btnGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Register.class);
                startActivity(intent);
            }
        });

        TextView txtForgot = (TextView) findViewById(R.id.txtForgot);
        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtUsername.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Nhập email trước khi quên mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                new ForgotPasswordTask(LoginActivity.this).execute(email);
            }
        });
    }

    private class LoginUser extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Đang đăng nhập...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(Server.DuongdanDangNhap);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "email=" + params[0] + "&matkhau=" + params[1];
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                writer.close();
                reader.close();
                return result.toString().trim();

            } catch (Exception e) {
                return "error: " + e.getMessage();
            }
        }

        private void saveLoginPreference(boolean remember, String tenkhachhang, String email, String matkhau, String id_khachhang) {
            SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("email", email);
            editor.putString("matkhau", matkhau);
            editor.putString("tenkhachhang", tenkhachhang);
            editor.putString("id_khachhang", id_khachhang);
            editor.putBoolean("rememberMe", remember);

            editor.apply();
        }


        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");

                if (status.equalsIgnoreCase("success")) {
                    String tenkhachhang = jsonObject.getString("tenkhachhang");
                    String email = jsonObject.getString("email");
                    String matkhau = jsonObject.getString("matkhau");
                    String id_khachhang = jsonObject.getString("id_khachhang");
                    saveLoginPreference(chkRemember.isChecked(), tenkhachhang, email, matkhau, id_khachhang);

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    String message = "Đăng nhập thất bại";
                    if (jsonObject.has("message")) {
                        message = jsonObject.getString("message");
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + result, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
