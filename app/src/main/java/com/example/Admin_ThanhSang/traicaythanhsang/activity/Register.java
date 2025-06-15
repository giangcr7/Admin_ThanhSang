package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText edtTenKhachHang, edtEmail, edtMatKhau;
    Button btnDangKy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        edtTenKhachHang = (EditText) findViewById(R.id.edtUsername);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtMatKhau = (EditText) findViewById(R.id.edtPassword);
        btnDangKy = (Button) findViewById(R.id.btnRegister);

        // Xử lý sự kiện nhấn nút đăng ký
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String tenKhachHang = edtTenKhachHang.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String matKhau = edtMatKhau.getText().toString().trim();

        if (tenKhachHang.isEmpty() || email.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(Register.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String duongdan = Server.DuongdanDangKy;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, duongdan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equalsIgnoreCase("Đăng ký thành công!")) {
                            Toast.makeText(Register.this, "Đăng ký thành công! Mời bạn đăng nhập.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Đóng màn hình đăng ký
                        } else {
                            Toast.makeText(Register.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this, "Đã xảy ra lỗi! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tenkhachhang", tenKhachHang);
                params.put("email", email);
                params.put("matkhau", matKhau);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
