package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.adapter.SanphamAdapter;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Sanpham;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuanLyActivity extends AppCompatActivity {

    private static final int ADD_PRODUCT_REQUEST = 1;
    private static final int EDIT_PRODUCT_REQUEST = 2;
    private RecyclerView recyclerView;
    private ArrayList<Sanpham> productList;
    private SanphamAdapter adapter;
    private RequestQueue requestQueue;
    private EditText searchView;
    private Button btnRefresh;
    private boolean isDataLoaded = false;
    private Toolbar toolbar; // Khai báo toolbar làm biến thành viên

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly);

        toolbar = (Toolbar) findViewById(R.id.toolbarquanly); // Gán giá trị cho toolbar
        setSupportActionBar(toolbar);
        ActionToolbar(); // Gọi phương thức để thêm nút back

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewquanly);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new SanphamAdapter(this, productList) {
            @Override
            public void onBindViewHolder(ItemHolder holder, final int position) {
                super.onBindViewHolder(holder, position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDataLoaded) {
                            showEditDeleteDialog(position);
                        } else {
                            Toast.makeText(QuanLyActivity.this, "Dữ liệu chưa tải, vui lòng chờ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        searchView = (EditText) findViewById(R.id.searchView);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);

        requestQueue = Volley.newRequestQueue(this);
        loadProducts();

        // Tích hợp tìm kiếm bằng API
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tuKhoa = s.toString().trim();
                if (!tuKhoa.isEmpty()) {
                    timKiemSanPham(tuKhoa);
                } else {
                    loadProducts(); // Khôi phục danh sách gốc khi text rỗng
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tích hợp làm mới
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProducts();
            }
        });
    }

    private void loadProducts() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Server.DuongdanXemSanPhamAdmin, (JSONObject) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        productList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject json = response.getJSONObject(i);
                                productList.add(new Sanpham(
                                        json.getInt("id_sanpham"),
                                        json.getString("tensanpham"),
                                        json.getInt("giasanpham"),
                                        json.getString("hinhanhsanpham"),
                                        json.getString("motasanpham"),
                                        json.getInt("id_loaisanpham")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                            isDataLoaded = true;
                            Log.d("QuanLyActivity", "Loaded " + productList.size() + " products");
                        } catch (Exception e) {
                            Toast.makeText(QuanLyActivity.this, "Lỗi phân tích JSON (danh sách sản phẩm): " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("QuanLyActivity", "LoadProducts error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Lỗi tải sản phẩm: ";
                        if (error.networkResponse != null) {
                            errorMsg += "Status Code: " + error.networkResponse.statusCode + ", ";
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                errorMsg += responseBody;
                            } catch (Exception e) {
                                errorMsg += "Không thể đọc phản hồi: " + e.getMessage();
                            }
                        } else if (error.getMessage() != null) {
                            errorMsg += error.getMessage();
                        } else {
                            errorMsg += "Không có phản hồi từ server.";
                        }
                        Toast.makeText(QuanLyActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("QuanLyActivity", "LoadProducts error: " + errorMsg);
                    }
                });
        requestQueue.add(request);
    }

    private void showEditDeleteDialog(final int position) {
        if (position < 0 || position >= productList.size()) {
            Log.e("QuanLyActivity", "Invalid position: " + position + ", productList size: " + productList.size());
            Toast.makeText(this, "Vị trí sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        final Sanpham selectedProduct = productList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tùy chọn sản phẩm");
        builder.setMessage("Chọn hành động cho sản phẩm: " + selectedProduct.getTensanpham());
        builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(QuanLyActivity.this, SuaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID", selectedProduct.getID());
                bundle.putString("Tensanpham", selectedProduct.getTensanpham());
                bundle.putInt("Giasanpham", selectedProduct.getGiasanpham());
                bundle.putString("Hinhanhsanpham", selectedProduct.getHinhanhsanpham());
                bundle.putString("Motasanpham", selectedProduct.getMotasanpham());
                bundle.putInt("IDSanpham", selectedProduct.getIDSanpham());
                intent.putExtras(bundle);
                Log.d("QuanLyActivity", "Sending product: " + selectedProduct.getTensanpham() + ", ID: " + selectedProduct.getID() + ", Position: " + position);
                startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
            }
        });
        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct(position);
            }
        });
        builder.setNeutralButton("Hủy", null);
        builder.create().show();
    }

    private void deleteProduct(final int position) {
        StringRequest request = new StringRequest(Request.Method.POST, Server.DuongdanXoaSanPham,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                productList.remove(position);
                                adapter.notifyItemRemoved(position);
                                loadProducts();
                                Toast.makeText(QuanLyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(QuanLyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(QuanLyActivity.this, "Lỗi phân tích JSON (xóa sản phẩm): " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QuanLyActivity.this, "Lỗi xóa sản phẩm: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_sanpham", String.valueOf(productList.get(position).getID()));
                return params;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quan_ly, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, ThemActivity.class);
            startActivityForResult(intent, ADD_PRODUCT_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            loadProducts();
        } else if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("PRODUCT_UPDATED", false)) {
                loadProducts();
            }
        }
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Thêm phương thức timKiemSanPham
    private void timKiemSanPham(final String tuKhoa) {
        TimKiemSanPham.timKiemSanPham(this, tuKhoa, new TimKiemSanPham.TimKiemCallback() {
            @Override
            public void onSuccess(ArrayList<Sanpham> danhSach) {
                productList.clear();
                productList.addAll(danhSach);
                adapter.notifyDataSetChanged();
                Log.d("TimKiemSanPham", "Found " + danhSach.size() + " products for keyword: " + tuKhoa);
                if (danhSach.isEmpty()) {
                    Toast.makeText(QuanLyActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(QuanLyActivity.this, message, Toast.LENGTH_LONG).show();
                Log.e("TimKiemSanPham", "Error: " + message);
            }
        });
    }
}