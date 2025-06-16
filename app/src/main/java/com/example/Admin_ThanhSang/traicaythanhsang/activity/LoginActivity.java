package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

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
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

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
                URL url = new URL(Server.DuongdanDangNhap_Admin);
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

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                String role = jsonObject.optString("role", "");

                if (status.equalsIgnoreCase("success") && role.equalsIgnoreCase("admin")) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập admin thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản không có quyền admin!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + result, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
