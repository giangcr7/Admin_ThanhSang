package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {
    private ListView listViewDetails;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> detailList;
    private Button btnApprove;
    private int madonhang;
    private String trangthai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        listViewDetails = (ListView) findViewById(R.id.listViewDetails);
        btnApprove = (Button) findViewById(R.id.btnApprove);
        detailList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailList);
        listViewDetails.setAdapter(adapter);

        madonhang = getIntent().getIntExtra("madonhang", -1);
        if (madonhang == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderDetails();

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus("Đã duyệt");
            }
        });
    }

    private void loadOrderDetails() {
        String url = Server.DuongdanLayChiTietDonHang + "?madonhang=" + madonhang;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject orderInfo = new JSONObject(response);
                            trangthai = orderInfo.getString("trangthai");
                            if (trangthai.equals("Đã duyệt")) {
                                btnApprove.setVisibility(View.GONE);
                            } else {
                                btnApprove.setVisibility(View.VISIBLE);
                            }
                            JSONArray array = orderInfo.getJSONArray("details");
                            detailList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String tensanpham = obj.getString("tensanpham");
                                int giasanpham = obj.getInt("giasanpham");
                                int soluongsanpham = obj.getInt("soluongsanpham");
                                String item = "Sản phẩm: " + tensanpham + "\nGiá: " + giasanpham +
                                        "\nSố lượng: " + soluongsanpham;
                                detailList.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(OrderDetailActivity.this, "Lỗi phân tích JSON chi tiết: " + response, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Lỗi tải chi tiết: ";
                        if (error.getMessage() != null) {
                            errorMessage += error.getMessage();
                        } else if (error.networkResponse != null) {
                            errorMessage += "Mã lỗi HTTP: " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += ", Phản hồi: " + responseBody;
                            } catch (Exception e) {
                                errorMessage += ", Không đọc được phản hồi";
                            }
                        } else {
                            errorMessage += "Không kết nối được server";
                        }
                        Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void updateOrderStatus(final String status) {
        Log.d("OrderDetail", "madonhang: " + madonhang + ", trangthai: " + status);
        String url = Server.DuongdanCapNhatTrangThaiDonHang;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("OrderDetail", "Phản hồi: " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Toast.makeText(OrderDetailActivity.this, "Duyệt đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                trangthai = "Đã duyệt";
                                loadOrderDetails(); // Tải lại chi tiết đơn hàng từ server
                                if (json.has("warning")) {
                                    Toast.makeText(OrderDetailActivity.this, json.getString("warning"), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(OrderDetailActivity.this, json.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(OrderDetailActivity.this, "Lỗi phân tích JSON: " + response, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Lỗi cập nhật: ";
                        if (error.getMessage() != null) {
                            errorMessage += error.getMessage();
                        } else if (error.networkResponse != null) {
                            errorMessage += "Mã lỗi HTTP: " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += ", Phản hồi: " + responseBody;
                            } catch (Exception e) {
                                errorMessage += ", Không đọc được phản hồi";
                            }
                        } else {
                            errorMessage += "Không kết nối được server";
                        }
                        Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("madonhang", String.valueOf(madonhang));
                params.put("trangthai", status);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                return headers;
            }
        };

        // Đặt thời gian chờ tùy chỉnh bằng RetryPolicy
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // Thời gian chờ: 30 giây
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Số lần thử lại mặc định (thường là 1)
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Hệ số nhân backoff mặc định (thường là 1.0)
        ));

        Volley.newRequestQueue(this).add(request);
    }
}