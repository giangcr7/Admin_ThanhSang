package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.adapter.ComBoAdapter;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Sanpham;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.CheckConnection;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComBoActivity extends AppCompatActivity {

    Toolbar toolbarcb;
    ListView lvcb;
    ComBoAdapter comboadapter;
    ArrayList<Sanpham> mangcb;
    int idcb =0;
    int page = 1;
    View footerview;
    boolean isLoading = false;
    boolean limitadata = false;
    mHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_bo);
        Anhxa();
        if (CheckConnection.isInternetAvailable(getApplicationContext())){
            GetIdloaisp();
            ActionToolbar();
            GetData(page);
            LoadMoreData();
        }else
        {
            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại Internet");
            finish();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menugiohang:
                Intent intent =  new Intent(getApplicationContext(),GioHangActivity.class);
                startActivity(intent);
            case R.id.dangxuat:
                SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(ComBoActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                // Quay về màn hình đăng nhập
                Intent intent1 = new Intent(ComBoActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetData(int Page) {
        RequestQueue requestqueue = Volley.newRequestQueue(getApplicationContext());
        String duongdan = Server.Duongdansaurieng+String.valueOf(Page);
        StringRequest stringrequest = new StringRequest(Request.Method.POST, duongdan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int id = 0;
                String Tencb ="";
                int Giacb = 0;
                String Hinhanhcb = "";
                String Mota = "";
                int Idspcb = 0;
                if(response !=null && response.length() != 2)
                {
                    lvcb.removeFooterView(footerview);
                    try{
                        JSONArray jsonarray = new JSONArray(response);
                        for (int i = 0; i < jsonarray.length();i++)
                        {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            id = jsonobject.getInt("id");
                            Tencb = jsonobject.getString("tensp");
                            Giacb = jsonobject.getInt("giasp");
                            Hinhanhcb = jsonobject.getString("hinhanhsp");
                            Mota = jsonobject.getString("motasp");
                            Idspcb = jsonobject.getInt("idsanpham");
                            mangcb.add(new Sanpham(id,Tencb,Giacb,Hinhanhcb,Mota,Idspcb));
                            comboadapter.notifyDataSetChanged();
                        }



                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    limitadata=true;
                    lvcb.removeFooterView(footerview);
                    CheckConnection.ShowToast_Short(getApplicationContext(),"Đã hết dữ liệu");
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String, String>();
                param.put("id_loaisanpham",String.valueOf(idcb));
                return param;
            }
        };
        requestqueue.add(stringrequest);
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbarcb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarcb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void GetIdloaisp() {
        idcb = getIntent().getIntExtra("idloaisanpham",-1);
        Log.d("giatriloaisanpham",idcb+"");
    }

    private void LoadMoreData() {
        lvcb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(),ChiTietSanPham.class);
                intent.putExtra("thongtinsanpham", mangcb.get(i));
                startActivity(intent);
            }
        });
        lvcb.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int FirstItem, int VisibleItem, int TotalItem) {
                if (FirstItem + VisibleItem == TotalItem && TotalItem !=0 && isLoading == false && limitadata == false){
                    isLoading = true;
                    ComBoActivity.ThreadData threadData = new ThreadData();
                    threadData.start();
                }

            }
        });
    }

    private void Anhxa() {
        toolbarcb  = (Toolbar) findViewById(R.id.toolbarcombo);
        lvcb  = (ListView) findViewById(R.id.listviewcombo);
        mangcb = new ArrayList<>();
        comboadapter = new ComBoAdapter(getApplicationContext(), mangcb);
        lvcb.setAdapter(comboadapter);
        LayoutInflater inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerview= inflater.inflate(R.layout.progressbar,null);
        mHandler= new mHandler();
    }

    public class mHandler extends android.os.Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    lvcb.addFooterView(footerview);
                    break;
                case 1:
                    GetData(++page);
                    isLoading = false;
                    break;
            }
            super.handleMessage(msg);
        }
    }
    public class ThreadData extends Thread{
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message= mHandler.obtainMessage(1);
            mHandler.sendMessage(message);
            super.run();
        }
    }
}
