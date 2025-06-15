package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class SuaActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editProductName, editProductPrice, editProductDescription;
    private Spinner spinnerProductCategory;
    private Button buttonChooseImage, buttonSave;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private int productId;
    private String productName, productDescription, productImageUrl;
    private int productPrice, productCategoryId;
    private ArrayList<String> categoryNames = new ArrayList<String>();
    private ArrayList<Integer> categoryIds = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        editProductName = (EditText) findViewById(R.id.edit_product_name);
        editProductPrice = (EditText) findViewById(R.id.edit_product_price);
        editProductDescription = (EditText) findViewById(R.id.edit_product_description);
        spinnerProductCategory = (Spinner) findViewById(R.id.spinner_product_category);
        buttonChooseImage = (Button) findViewById(R.id.button_choose_image);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        buttonSave = (Button) findViewById(R.id.button_save);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getInt("ID", -1);
            productName = bundle.getString("Tensanpham", "");
            productPrice = bundle.getInt("Giasanpham", 0);
            productImageUrl = bundle.getString("Hinhanhsanpham", "");
            productDescription = bundle.getString("Motasanpham", "");
            productCategoryId = bundle.getInt("IDSanpham", -1);

            if (productId == -1 || productName.isEmpty()) {
                Log.e("SuaActivity", "Dữ liệu không đầy đủ từ Bundle");
                Toast.makeText(this, "Không tìm thấy sản phẩm để sửa", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            editProductName.setText(productName);
            editProductPrice.setText(String.valueOf(productPrice));
            editProductDescription.setText(productDescription);

            if (productImageUrl != null && !productImageUrl.isEmpty() && !productImageUrl.equals("0")) {
                try {
                    // Cập nhật URL để khớp với localhost
                    if (!productImageUrl.startsWith("http://192.168.1.16/server/uploads/")) {
                        if (productImageUrl.startsWith("http://192.168.1.16/uploads/")) {
                            productImageUrl = productImageUrl.replace("http://192.168.1.16/uploads/", "http://192.168.1.16/server/uploads/");
                        }
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(productImageUrl).getContent());
                    if (bitmap != null) {
                        imagePreview.setImageBitmap(bitmap);
                        imagePreview.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("SuaActivity", "Không thể tải ảnh từ URL: " + productImageUrl);
                        Toast.makeText(this, "Không thể tải ảnh cũ", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("SuaActivity", "Lỗi tải ảnh cũ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải ảnh cũ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    imagePreview.setVisibility(View.GONE);
                }
            } else {
                imagePreview.setVisibility(View.GONE);
            }

            Log.d("SuaActivity", "Received product: " + productName + ", ID: " + productId);
        } else {
            Log.e("SuaActivity", "Bundle is null from Intent");
            Toast.makeText(this, "Không tìm thấy sản phẩm để sửa", Toast.LENGTH_SHORT).show();
            finish();
        }

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
                int categoryId = (categoryIndex > 0 && categoryIndex < categoryIds.size()) ? categoryIds.get(categoryIndex) : productCategoryId;

                if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                    Toast.makeText(SuaActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int price;
                try {
                    price = Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(SuaActivity.this, "Giá phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedImageUri != null) {
                    Log.d("SuaActivity", "Sending new image: " + selectedImageUri.toString());
                } else {
                    Log.d("SuaActivity", "Keeping old image: " + productImageUrl);
                }

                updateProduct(productId, name, price, description, categoryId, selectedImageUri);
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
                    Toast.makeText(SuaActivity.this, "Vui lòng chọn file ảnh", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;
                    return;
                }
                imagePreview.setImageURI(selectedImageUri);
                imagePreview.setVisibility(View.VISIBLE);
                Log.d("SuaActivity", "Selected new image URI: " + selectedImageUri.toString() + ", Type: " + fileType);
            } catch (Exception e) {
                Log.e("SuaActivity", "Lỗi hiển thị ảnh mới: " + e.getMessage());
                Toast.makeText(this, "Lỗi hiển thị ảnh mới", Toast.LENGTH_SHORT).show();
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

            categoryNames.clear();
            categoryIds.clear();
            categoryNames.add("Chọn loại sản phẩm");
            categoryIds.add(-1);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                categoryNames.add(jsonObject.getString("tenloaisanpham"));
                categoryIds.add(jsonObject.getInt("id_loaisanpham"));
            }

            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductCategory.setAdapter(categoryAdapter);

            for (int i = 0; i < categoryIds.size(); i++) {
                if (categoryIds.get(i) == productCategoryId) {
                    spinnerProductCategory.setSelection(i);
                    break;
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải loại sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("LoadCategories", "Error: " + e.getMessage());
        }
    }

    private void updateProduct(int id, String name, int price, String description, int categoryId, Uri imageUri) {
        try {
            URL url = new URL(Server.DuongdanSuaSanPham);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream os = conn.getOutputStream();
            StringBuilder body = new StringBuilder();

            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"id_sanpham\"\r\n\r\n");
            body.append(id).append("\r\n");

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

            if (imageUri != null) {
                body.append("--").append(boundary).append("\r\n");
                body.append("Content-Disposition: form-data; name=\"hinhanh\"; filename=\"image.jpg\"\r\n");
                body.append("Content-Type: image/jpeg\r\n\r\n");
                os.write(body.toString().getBytes());

                InputStream is = getContentResolver().openInputStream(imageUri);
                if (is != null) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    Log.d("UpdateProduct", "Image sent successfully: " + imageUri.toString() + ", Bytes read: " + bytesRead);
                } else {
                    Log.e("UpdateProduct", "Failed to open InputStream for image: " + imageUri.toString());
                    Toast.makeText(SuaActivity.this, "Không thể đọc ảnh mới", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("UpdateProduct", "No new image, keeping old URL: " + productImageUrl);
                // Không gửi trường hinhanh nếu không có ảnh mới
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

            Log.d("UpdateProduct", "JSON Response: " + response.toString());
            JSONObject jsonObject = new JSONObject(response.toString());
            boolean success = jsonObject.getBoolean("success");
            String message = jsonObject.getString("message");
            String newImageUrl = jsonObject.optString("hinhanh_url", productImageUrl); // Lấy URL mới nếu có

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                // Cập nhật productImageUrl nếu có ảnh mới
                if (!newImageUrl.equals(productImageUrl)) {
                    productImageUrl = newImageUrl;
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(productImageUrl).getContent());
                        if (bitmap != null) {
                            imagePreview.setImageBitmap(bitmap);
                            imagePreview.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e("SuaActivity", "Lỗi tải ảnh mới sau cập nhật: " + e.getMessage());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("PRODUCT_UPDATED", true);
                setResult(RESULT_OK, intent);
                finish();
            }

            conn.disconnect();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi cập nhật sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("UpdateProduct", "Error: " + e.getMessage());
        }
    }
}