package com.example.Admin_ThanhSang.traicaythanhsang.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
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

public class OrderListActivity extends AppCompatActivity {
    private ListView listViewOrders;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> orderList;
    private ArrayList<Integer> orderIdList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        listViewOrders = (ListView) findViewById(R.id.listViewOrders);
        orderList = new ArrayList<>();
        orderIdList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        listViewOrders.setAdapter(adapter);
        loadOrders();

        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                intent.putExtra("madonhang", orderIdList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
    private void loadOrders() {
        String url = Server.DuongdanLayDanhSachDonHang;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            orderList.clear();
                            orderIdList.clear();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                int madonhang = obj.getInt("madonhang");
                                String tenkhachhang = obj.getString("tenkhachhang");
                                String tongtien = obj.getString("tongtien");
                                String ngaydat = obj.getString("ngaydat");
                                String trangthai = obj.getString("trangthai");
                                String item = "Mã đơn: " + madonhang + "\nKhách hàng: " + tenkhachhang +
                                        "\nTổng tiền: " + tongtien + "\nNgày đặt: " + ngaydat +
                                        "\nTrạng thái: " + trangthai;
                                orderList.add(item);
                                orderIdList.add(madonhang);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(OrderListActivity.this, "Lỗi đọc JSON", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderListActivity.this, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }
}