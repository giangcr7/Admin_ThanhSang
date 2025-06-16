package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.CheckConnection;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Thongtinkhachhang extends AppCompatActivity {
    EditText edttenkhachhang, edtemail, edtsdt,edtdiachi;
    Button btnxacnhan, btntrove;
    RadioButton rdbTienmat,rdbChuyenkhoan;

    int id_khachhang = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtinkhachhang);
        Anhxa();

        // Lấy id_khachhang từ SharedPreferences với đúng tên và kiểu dữ liệu
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String idStr = sharedPreferences.getString("id_khachhang", "-1");
        try {
            id_khachhang = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            id_khachhang = -1;
        }

        btntrove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (CheckConnection.isInternetAvailable(getApplicationContext())) {
            if (id_khachhang != -1) {
                LayThongTinKhachHang(id_khachhang);
            }
            EventButton();
        } else {
            CheckConnection.ShowToast_Short(getApplicationContext(), "Bạn hãy kiểm tra lại kết nối");
        }
    }

    private void LayThongTinKhachHang(final int id) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Server.Duongdanlaythongtinkhachhang,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            edttenkhachhang.setText(json.getString("tenkhachhang"));
                            edtemail.setText(json.getString("email"));
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id_khachhang", String.valueOf(id));
                return params;
            }
        };
        queue.add(request);
    }

    private void EventButton() {
        btnxacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ten = edttenkhachhang.getText().toString().trim();
                final String sdt = edtsdt.getText().toString().trim();
                final String email = edtemail.getText().toString().trim();
                final String diachi = edtdiachi.getText().toString().trim();

                if (ten.length() > 0 && sdt.length() > 0 && email.length() > 0) {
                    if (MainActivity.manggiohang == null || MainActivity.manggiohang.size() == 0) {
                        CheckConnection.ShowToast_Short(getApplicationContext(), "Giỏ hàng trống");
                        return;
                    }

                    double tongtien = 0;
                    for (int i = 0; i < MainActivity.manggiohang.size(); i++) {
                        int soluong = MainActivity.manggiohang.get(i).getSoluongsp();
                        double gia = MainActivity.manggiohang.get(i).getGiasp();
                        tongtien += soluong * gia;
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    final double finalTongtien = tongtien;
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Duongdandonhang,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(final String madonhang) {
                                    if (!madonhang.matches("\\d+")) {
                                        CheckConnection.ShowToast_Short(getApplicationContext(), "Lỗi trả về mã đơn hàng");
                                        return;
                                    }

                                    int madonhangInt = Integer.parseInt(madonhang);
                                    if (madonhangInt > 0) {
                                        ThemChiTietDonHangJson(madonhangInt);
                                        MainActivity.manggiohang.clear();
                                        CheckConnection.ShowToast_Short(getApplicationContext(), "Bạn đã thêm dữ liệu giỏ hàng thành công");


                                        if (rdbTienmat.isChecked()) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            CheckConnection.ShowToast_Short(getApplicationContext(), "Bạn đã chọn thanh toán tiền mặt");
                                        } else if (rdbChuyenkhoan.isChecked()) {
                                            CheckConnection.ShowToast_Short(getApplicationContext(), "Bạn đã chọn thanh toán chuyển khoản");
                                            Intent intent = new Intent(getApplicationContext(), ChuyenKhoanActivity.class);
                                            startActivity(intent);
                                        } else {
                                            CheckConnection.ShowToast_Short(getApplicationContext(), "Vui lòng chọn phương thức thanh toán");
                                        }
                                    } else {
                                        CheckConnection.ShowToast_Short(getApplicationContext(), "Đặt hàng không thành công");
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            CheckConnection.ShowToast_Short(getApplicationContext(), "Lỗi kết nối hoặc server");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("sodienthoai", sdt);
                            hashMap.put("diachi", diachi);
                            hashMap.put("id_khachhang", String.valueOf(id_khachhang));
                            hashMap.put("tongtien", String.valueOf(finalTongtien));
                            hashMap.put("phuong_thuc_thanh_toan", rdbTienmat.isChecked() ? "0" : "1");
                            return hashMap;
                        }
                    };
                    requestQueue.add(stringRequest);
                } else {
                    CheckConnection.ShowToast_Short(getApplicationContext(), "Hãy kiểm tra lại dữ liệu");
                }
            }
        });
    }

    private void ThemChiTietDonHangJson(final int madonhang) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < MainActivity.manggiohang.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("madonhang", madonhang);
                jsonObject.put("id_sanpham", MainActivity.manggiohang.get(i).getIdsp());
                jsonObject.put("tensanpham", MainActivity.manggiohang.get(i).getTensp());
                jsonObject.put("giasanpham", MainActivity.manggiohang.get(i).getGiasp());
                jsonObject.put("soluongsanpham", MainActivity.manggiohang.get(i).getSoluongsp());
                jsonArray.put(jsonObject);
            }

            final String jsonStr = jsonArray.toString();

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Duongdanchitietdonhang,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Không cần xử lý gì nếu không muốn log hoặc hiển thị
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Không xử lý lỗi nếu bạn không muốn hiển thị hoặc log
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("json", jsonStr);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            // Không cần log lỗi nếu không mong muốn
        }
    }


    private void Anhxa() {
        edtdiachi  = (EditText) findViewById(R.id.edittexdiachikhachhang);
        edttenkhachhang = (EditText) findViewById(R.id.edittexttenkhachhang);
        edtsdt = (EditText) findViewById(R.id.edittextsdtkhachhang);
        edtemail = (EditText) findViewById(R.id.edittextemailkhachhang);
        btnxacnhan = (Button) findViewById(R.id.buttonxacnhan);
        btntrove = (Button) findViewById(R.id.buttontrove);
        rdbTienmat = (RadioButton) findViewById(R.id.rdbTienmat);
        rdbChuyenkhoan = (RadioButton) findViewById(R.id.rdbChuyenkhoan);
    }
}
