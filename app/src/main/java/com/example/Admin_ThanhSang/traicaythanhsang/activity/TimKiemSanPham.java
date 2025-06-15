package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Sanpham;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimKiemSanPham {

    public interface TimKiemCallback {
        void onSuccess(ArrayList<Sanpham> danhSach);
        void onError(String message);
    }

    public static void timKiemSanPham(Context context, String tuKhoa, final TimKiemCallback callback) {
        String url = Server.DuongdanTimKiemSanPham + tuKhoa;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<Sanpham> danhSach = new ArrayList<Sanpham>();
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id_sanpham");
                                String tensp = object.getString("tensanpham");
                                int gia = object.getInt("giasanpham");
                                String hinhanh = object.getString("hinhanhsanpham");
                                String mota = object.getString("motasanpham");
                                int idsanpham = object.getInt("id_loaisanpham");

                                danhSach.add(new Sanpham(id, tensp, gia, hinhanh, mota, idsanpham));
                            }

                            callback.onSuccess(danhSach);
                        } catch (Exception e) {
                            callback.onError("Lỗi phân tích dữ liệu: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Lỗi kết nối: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(stringRequest);
    }
}
