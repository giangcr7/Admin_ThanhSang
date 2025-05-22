package com.example.lehoanggiang.traicaythanhsang.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lehoanggiang.traicaythanhsang.R;
import com.example.lehoanggiang.traicaythanhsang.adapter.SauRiengAdapter;
import com.example.lehoanggiang.traicaythanhsang.model.Sanpham;
import com.example.lehoanggiang.traicaythanhsang.ultil.CheckConnection;
import com.example.lehoanggiang.traicaythanhsang.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class SauRiengActivity extends AppCompatActivity {
    Toolbar toolbarsr;
    ListView lvsr;
    SauRiengAdapter sauriengadapter;
    ArrayList<Sanpham> mangsr;
    int idsr =0;
    int page = 1;
    View footerview;
    boolean isLoading = false;
    boolean limitadata = false;
    mHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sau_rieng);
        AnhXa();
        if (CheckConnection.isInternetAvailable(getApplicationContext()))
        {
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

    private void LoadMoreData() {
        lvsr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(),ChiTietSanPham.class);
                intent.putExtra("thongtinsanpham", mangsr.get(i));
                startActivity(intent);
            }
        });
        lvsr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int FirstItem, int VisibleItem, int TotalItem) {
                if (FirstItem + VisibleItem == TotalItem && TotalItem !=0 && isLoading == false && limitadata == false){
                    isLoading = true;
                    ThreadData threadData = new ThreadData();
                    threadData.start();
                }

            }
        });
    }

    private void GetData(int Page) {
        RequestQueue requestqueue = Volley.newRequestQueue(getApplicationContext());
        String duongdan = Server.Duongdansaurieng+String.valueOf(Page);
        StringRequest stringrequest = new StringRequest(Request.Method.POST, duongdan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int id = 0;
                String Tensr ="";
                int Giasr = 0;
                String Hinhanhsr = "";
                String Mota = "";
                int Idspsr = 0;
                if(response !=null && response.length() != 2)
                {
                    lvsr.removeFooterView(footerview);
                    try{
                        JSONArray jsonarray = new JSONArray(response);
                        for (int i = 0; i < jsonarray.length();i++)
                        {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            id = jsonobject.getInt("id");
                            Tensr = jsonobject.getString("tensp");
                            Giasr = jsonobject.getInt("giasp");
                            Hinhanhsr = jsonobject.getString("hinhanhsp");
                            Mota = jsonobject.getString("motasp");
                            Idspsr = jsonobject.getInt("idsanpham");
                            mangsr.add(new Sanpham(id,Tensr,Giasr,Hinhanhsr,Mota,Idspsr));
                            sauriengadapter.notifyDataSetChanged();
                        }



                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    limitadata=true;
                    lvsr.removeFooterView(footerview);
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
                param.put("idsanpham",String.valueOf(idsr));
                return param;
            }
        };
        requestqueue.add(stringrequest);
    }


    private void ActionToolbar() {
        setSupportActionBar(toolbarsr);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarsr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void GetIdloaisp() {
        idsr = getIntent().getIntExtra("idloaisanpham",-1);
        Log.d("giatriloaisanpham",idsr+"");
    }

    private void AnhXa() {
        toolbarsr  = (Toolbar) findViewById(R.id.toolbarsaurieng);
        lvsr  = (ListView) findViewById(R.id.listviewsaurieng);
        mangsr = new ArrayList<>();
        sauriengadapter = new SauRiengAdapter(getApplicationContext(),mangsr);
        lvsr.setAdapter(sauriengadapter);
        LayoutInflater inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerview= inflater.inflate(R.layout.progressbar,null);
        mHandler= new mHandler();
    }
    public class mHandler extends android.os.Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    lvsr.addFooterView(footerview);
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
