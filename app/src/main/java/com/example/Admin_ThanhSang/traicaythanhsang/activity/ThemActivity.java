package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ThemActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editProductName, editProductPrice, editProductDescription;
    private Spinner spinnerProductCategory;
    private Button buttonChooseImage, buttonSave;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private ArrayList<String> categoryNames = new ArrayList<String>();
    private ArrayList<Integer> categoryIds = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them);

        // Bỏ qua strict mode để cho phép mạng trên main thread (chỉ để debug)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        editProductName = (EditText) findViewById(R.id.edit_product_name);
        editProductPrice = (EditText) findViewById(R.id.edit_product_price);
        editProductDescription = (EditText) findViewById(R.id.edit_product_description);
        spinnerProductCategory = (Spinner) findViewById(R.id.spinner_product_category);
        buttonChooseImage = (Button) findViewById(R.id.button_choose_image);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        buttonSave = (Button) findViewById(R.id.button_save);

        categoryNames.add("Chọn loại sản phẩm");
        categoryIds.add(-1);

        loadCategories();

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editProductName.getText().toString().trim();
                String priceStr = editProductPrice.getText().toString().trim();
                String description = editProductDescription.getText().toString().trim();
                int categoryIndex = spinnerProductCategory.getSelectedItemPosition();

                if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || selectedImageUri == null) {
                    Toast.makeText(ThemActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (categoryIndex == 0 || categoryIds.get(categoryIndex) == -1) {
                    Toast.makeText(ThemActivity.this, "Vui lòng chọn loại sản phẩm hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                int price;
                try {
                    price = Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ThemActivity.this, "Giá phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }

                int categoryId = categoryIds.get(categoryIndex);
                Log.d("AddProduct", "Name: " + name + ", Price: " + price + ", Description: " + description + ", CategoryId: " + categoryId);
                addProduct(name, price, description, categoryId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                // Kiểm tra loại file
                String fileType = getContentResolver().getType(selectedImageUri);
                if (!fileType.startsWith("image/")) {
                    Toast.makeText(ThemActivity.this, "Vui lòng chọn file ảnh", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;
                    return;
                }
                imagePreview.setImageURI(selectedImageUri);
                imagePreview.setVisibility(View.VISIBLE);
                Log.d("AddProduct", "Selected image URI: " + selectedImageUri.toString() + ", Type: " + fileType);
            } catch (Exception e) {
                Log.e("AddProduct", "Lỗi hiển thị ảnh: " + e.getMessage());
                Toast.makeText(this, "Lỗi hiển thị ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadCategories() {
        try {
            URL url = new URL(Server.DuongdanLoaisp_admin);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Log.d("LoadCategories", "JSON Response: " + response.toString());
            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                categoryNames.add(jsonObject.getString("tenloaisanpham"));
                categoryIds.add(jsonObject.getInt("id_loaisanpham"));
            }

            Log.d("LoadCategories", "Names: " + categoryNames.toString() + ", IDs: " + categoryIds.toString());
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ThemActivity.this, android.R.layout.simple_spinner_item, categoryNames);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductCategory.setAdapter(categoryAdapter);

            conn.disconnect();
        } catch (Exception e) {
            Toast.makeText(ThemActivity.this, "Lỗi tải loại sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("LoadCategories", "Error: " + e.getMessage());
        }
    }

    private void addProduct(final String name, final int price, final String description, final int categoryId) {
        try {
            URL url = new URL(Server.DuongdanThemSanPham);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream os = conn.getOutputStream();
            StringBuilder body = new StringBuilder();

            // Thêm các trường text
            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"tensanpham\"\r\n\r\n");
            body.append(name).append("\r\n");

            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"giasanpham\"\r\n\r\n");
            body.append(price).append("\r\n");

            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"motasanpham\"\r\n\r\n");
            body.append(description).append("\r\n");

            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"id_loaisanpham\"\r\n\r\n");
            body.append(categoryId).append("\r\n");

            // Thêm file ảnh
            if (selectedImageUri != null) {
                body.append("--").append(boundary).append("\r\n");
                body.append("Content-Disposition: form-data; name=\"hinhanh\"; filename=\"image.jpg\"\r\n");
                body.append("Content-Type: image/jpeg\r\n\r\n");
                os.write(body.toString().getBytes());

                InputStream is = getContentResolver().openInputStream(selectedImageUri);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                Log.d("AddProduct", "Image sent successfully: " + selectedImageUri.toString() + ", Bytes read: " + bytesRead);
            } else {
                Log.e("AddProduct", "No image selected for upload");
                Toast.makeText(ThemActivity.this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            os.write(("\r\n--" + boundary + "--\r\n").getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Log.d("AddProduct", "JSON Response: " + response.toString());
            JSONObject jsonObject = new JSONObject(response.toString());
            boolean success = jsonObject.getBoolean("success");
            String message = jsonObject.getString("message");

            Toast.makeText(ThemActivity.this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                finish(); // Quay lại QuanLyActivity
            }

            conn.disconnect();
        } catch (Exception e) {
            Toast.makeText(ThemActivity.this, "Lỗi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("AddProduct", "Error: " + e.getMessage());
        }
    }
}