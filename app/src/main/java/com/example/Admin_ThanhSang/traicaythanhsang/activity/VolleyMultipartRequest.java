package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<JSONObject> {
    private final Response.Listener<JSONObject> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mParams;
    private final Map<String, DataPart> mByteData;
    private final String mBoundary = "----" + System.currentTimeMillis();
    private final String mMimeType = "multipart/form-data; boundary=" + mBoundary;

    public VolleyMultipartRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mParams = new HashMap<>();
        this.mByteData = new HashMap<>();
    }

    protected Map<String, String> getParams() {
        return mParams;
    }

    protected Map<String, DataPart> getByteData() {
        return mByteData;
    }

    @Override
    public String getBodyContentType() {
        return mMimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Thêm các tham số văn bản
            for (Map.Entry<String, String> entry : getParams().entrySet()) {
                bos.write(("--" + mBoundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                bos.write((entry.getValue() + "\r\n").getBytes());
            }

            // Thêm dữ liệu file
            for (Map.Entry<String, DataPart> entry : getByteData().entrySet()) {
                DataPart dataPart = entry.getValue();
                bos.write(("--" + mBoundary + "\r\n").getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + dataPart.getFileName() + "\"\r\n").getBytes());
                bos.write(("Content-Type: " + dataPart.getMimeType() + "\r\n\r\n").getBytes());
                bos.write(dataPart.getContent());
                bos.write("\r\n".getBytes());
            }

            bos.write(("--" + mBoundary + "--\r\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(new JSONObject(jsonString), null);
        } catch (Exception e) {
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String mimeType;

        public DataPart(String fileName, byte[] content, String mimeType) {
            this.fileName = fileName;
            this.content = content;
            this.mimeType = mimeType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}