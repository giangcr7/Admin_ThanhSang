package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class ThongKeActivity extends AppCompatActivity {

    private TextView txtTongDoanhThu;
    private EditText editDate, editMonth, editYear;
    private Button btnThongKe;
    private Spinner spinnerType;
    private RequestQueue requestQueue;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        txtTongDoanhThu = (TextView) findViewById(R.id.txtTongDoanhThu);
        editDate = (EditText) findViewById(R.id.editDate);
        editMonth = (EditText) findViewById(R.id.editMonth);
        editYear = (EditText) findViewById(R.id.editYear);
        btnThongKe = (Button) findViewById(R.id.btnThongKe);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        toolbar = (Toolbar) findViewById(R.id.toolbarthongke);

        requestQueue = Volley.newRequestQueue(this);
        ActionToolbar(); // Gọi phương thức để thêm nút back

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.thongke_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateVisibility(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        loadThongKe("total", "", "", "");

        btnThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = spinnerType.getSelectedItem().toString();
                String date = editDate.getText().toString().trim();
                String month = editMonth.getText().toString().trim();
                String year = editYear.getText().toString().trim();

                // Xử lý type
                if ("Tổng cộng".equals(type)) {
                    loadThongKe("total", "", "", "");
                } else if ("Theo ngày".equals(type)) {
                    if (date.isEmpty()) {
                        Toast.makeText(ThongKeActivity.this, "Vui lòng nhập ngày", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadThongKe("day", date, "", "");
                } else if ("Theo tháng".equals(type)) {
                    if (month.isEmpty() || year.isEmpty()) {
                        Toast.makeText(ThongKeActivity.this, "Vui lòng nhập tháng và năm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadThongKe("month", "", month, year);
                } else if ("Theo năm".equals(type)) {
                    if (year.isEmpty()) {
                        Toast.makeText(ThongKeActivity.this, "Vui lòng nhập năm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadThongKe("year", "", "", year);
                }

                // Xóa nội dung các EditText sau khi thống kê
                editDate.setText("");
                editMonth.setText("");
                editYear.setText("");
            }
        });
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

    private void updateVisibility(int position) {
        editDate.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // Theo ngày
        editMonth.setVisibility(position == 2 ? View.VISIBLE : View.GONE); // Theo tháng
        editYear.setVisibility((position == 2 || position == 3) ? View.VISIBLE : View.GONE); // Theo tháng hoặc năm
    }

    private void loadThongKe(String type, String date, String month, String year) {
        String url = Server.DuongdanThongKeDoanhThu;
        if ("day".equals(type) && !date.isEmpty()) {
            url += "?type=day&date=" + date;
        } else if ("month".equals(type) && !month.isEmpty() && !year.isEmpty()) {
            url += "?type=month&month=" + month + "&year=" + year;
        } else if ("year".equals(type) && !year.isEmpty()) {
            url += "?type=year&year=" + year;
        } else {
            url += "?type=total";
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                double tongdoanhthu = response.getDouble("tongdoanhthu");
                                txtTongDoanhThu.setText(String.format("Tổng doanh thu: %,d VNĐ", (int) tongdoanhthu));
                                Log.d("ThongKeActivity", "Tổng doanh thu: " + tongdoanhthu);
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(ThongKeActivity.this, message, Toast.LENGTH_SHORT).show();
                                Log.e("ThongKeActivity", "Lỗi: " + message);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ThongKeActivity.this, "Lỗi phân tích JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("ThongKeActivity", "JSONException: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Lỗi tải thống kê: ";
                        if (error.networkResponse != null) {
                            errorMsg += "Status Code: " + error.networkResponse.statusCode;
                        } else {
                            errorMsg += error.getMessage();
                        }
                        Toast.makeText(ThongKeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("ThongKeActivity", errorMsg);
                    }
                });

        requestQueue.add(request);
    }
}