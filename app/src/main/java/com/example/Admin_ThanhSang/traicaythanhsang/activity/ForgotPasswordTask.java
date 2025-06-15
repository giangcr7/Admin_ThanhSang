package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ForgotPasswordTask extends AsyncTask<String, Void, String> {
    private Context context;

    public ForgotPasswordTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String email = params[0];
        String result = "";

        try {
            URL url = new URL(Server.DuongdanReset);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000); // 10s timeout
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "email=" + URLEncoder.encode(email, "UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();
            conn.disconnect();

            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = "{\"status\":\"error\",\"message\":\"Không thể kết nối đến máy chủ!\"}";
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String message = obj.optString("message", "Lỗi không xác định");

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Phản hồi không hợp lệ từ máy chủ!", Toast.LENGTH_SHORT).show();
        }
    }
}
