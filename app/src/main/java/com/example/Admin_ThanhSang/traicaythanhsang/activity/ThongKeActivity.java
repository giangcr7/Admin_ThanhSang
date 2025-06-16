package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.*;

import java.util.ArrayList;

public class ThongKeActivity extends AppCompatActivity {

    private TextView txtTongDoanhThu;
    private EditText editDate, editMonth, editYear;
    private Button btnThongKe;
    private Spinner spinnerType;
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private PieChart pieChart, pieChartSoLuong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        // Ánh xạ
        txtTongDoanhThu = (TextView) findViewById(R.id.txtTongDoanhThu);
        editDate = (EditText) findViewById(R.id.editDate);
        editMonth = (EditText) findViewById(R.id.editMonth);
        editYear = (EditText) findViewById(R.id.editYear);
        btnThongKe = (Button) findViewById(R.id.btnThongKe);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        toolbar = (Toolbar) findViewById(R.id.toolbarthongke);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChartSoLuong = (PieChart) findViewById(R.id.pieChartSoLuong);

        requestQueue = Volley.newRequestQueue(this);
        ActionToolbar();

        // Spinner loại thống kê
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.thongke_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateVisibility(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Bắt sự kiện nút thống kê
        btnThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = spinnerType.getSelectedItem().toString();
                String url = Server.DuongdanThongKeDoanhThu;

                String date = editDate.getText().toString().trim();
                String month = editMonth.getText().toString().trim();
                String year = editYear.getText().toString().trim();

                if (type.equals("Theo ngày")) {
                    if (date.isEmpty()) {
                        Toast.makeText(ThongKeActivity.this, "Vui lòng nhập ngày", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    url += "?type=day&date=" + date;
                } else if (type.equals("Theo tháng")) {
                    if (month.isEmpty() || year.isEmpty()) {
                        Toast.makeText(ThongKeActivity.this, "Vui lòng nhập tháng và năm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    url += "?type=month&month=" + month + "&year=" + year;
                } else if (type.equals("Theo năm")) {
                    if (year.isEmpty()) {
                        Toast.makeText(ThongKeActivity.this, "Vui lòng nhập năm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    url += "?type=year&year=" + year;
                } else {
                    url += "?type=total";
                }

                loadThongKe(url);

                editDate.setText("");
                editMonth.setText("");
                editYear.setText("");
            }
        });

        // Mặc định hiển thị tổng cộng
        loadThongKe(Server.DuongdanThongKeDoanhThu + "?type=total");
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

    private void loadThongKe(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = json.getBoolean("success");

                            if (success) {
                                JSONArray dataArray = json.getJSONArray("data");

                                ArrayList<Entry> doanhthuEntries = new ArrayList<>();
                                ArrayList<Entry> soluongEntries = new ArrayList<>();
                                ArrayList<String> labels = new ArrayList<>();

                                float tongDoanhThu = 0;

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject item = dataArray.getJSONObject(i);
                                    String loai = item.getString("loai");
                                    float doanhthu = (float) item.getDouble("doanhthu");
                                    int soluong = item.optInt("soluong");

                                    doanhthuEntries.add(new Entry(doanhthu, i));
                                    soluongEntries.add(new Entry(soluong, i));
                                    labels.add(loai);

                                    tongDoanhThu += doanhthu;
                                }

                                txtTongDoanhThu.setText(String.format("Tổng doanh thu: %,d VNĐ", (int) tongDoanhThu));
                                showPieChart(pieChart, doanhthuEntries, labels, "Doanh thu theo loại");
                                showPieChart(pieChartSoLuong, soluongEntries, labels, "Số lượng theo loại");

                            } else {
                                Toast.makeText(ThongKeActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ThongKeActivity.this, "Lỗi JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("ThongKe", "JSON error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ThongKeActivity.this, "Lỗi mạng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ThongKe", "Volley error: " + error.getMessage());
                    }
                });

        requestQueue.add(request);
    }

    private void showPieChart(PieChart chart, ArrayList<Entry> entries, ArrayList<String> labels, String centerText) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);

        // Sửa lại ValueFormatter cho đúng với phiên bản 2.2.5
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, com.github.mikephil.charting.utils.ViewPortHandler viewPortHandler) {
                return String.format("%d", (int) value); // Hiển thị số nguyên
            }
        });

        PieData data = new PieData(labels, dataSet);
        chart.setData(data);
        chart.setCenterText(centerText);
        chart.setUsePercentValues(false);
        chart.setDescription("");
        chart.animateY(1000);
        chart.invalidate();
    }


}
