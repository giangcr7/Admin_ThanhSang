package com.example.lehoanggiang.traicaythanhsang.activity;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.lehoanggiang.traicaythanhsang.R;
import com.example.lehoanggiang.traicaythanhsang.adapter.LoaispAdapter;
import com.example.lehoanggiang.traicaythanhsang.adapter.SanphamAdapter;
import com.example.lehoanggiang.traicaythanhsang.model.Giohang;
import com.example.lehoanggiang.traicaythanhsang.model.Loaisp;
import com.example.lehoanggiang.traicaythanhsang.model.Sanpham;
import com.example.lehoanggiang.traicaythanhsang.ultil.CheckConnection;
import com.example.lehoanggiang.traicaythanhsang.ultil.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewmanhinhchinh;
    NavigationView navigationView;
    ListView listViewmanhinhchinh;
    DrawerLayout drawerLayout;
    ArrayList<Loaisp> mangloaisp;
    LoaispAdapter loaispAdapter;
    int id =0;
    String tenloaisp="";
    String hinhanhloaisp="";
    ArrayList<Sanpham> mangsanpham;
    SanphamAdapter sanphamAdapter;
    public static ArrayList<Giohang> manggiohang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        Anhxa();
        if (CheckConnection.isInternetAvailable(getApplicationContext())) {
            // Thiết lập Toolbar
            ActionBar();

            // Thiết lập Bảng quảng cáo
            ActionViewFlipper();
            GetDuLieuLoaisp();
            GetDuLieuSPMoiNhat();
            CatchOnItemListView();
        } else {
            CheckConnection.ShowToast_Short(getApplicationContext(), "Kiem tra lai ket noi");
            finish();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu (Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//
//    }


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
        }
        return super.onOptionsItemSelected(item);
    }

    private void CatchOnItemListView() {
        listViewmanhinhchinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                switch (i) {
                    case 0:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 1:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,SauRiengActivity.class);
                            intent.putExtra("idloaisanpham",mangloaisp.get(i).getId());
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 2:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,DuaHauActivity.class);
                            intent.putExtra("idloaisanpham",mangloaisp.get(i).getId());
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 3:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,NhapKhauActivity.class);
                            intent.putExtra("idloaisanpham",mangloaisp.get(i).getId());
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 4:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,HuuCoActivity.class);
                            intent.putExtra("idloaisanpham",mangloaisp.get(i).getId());
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 5:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,ComBoActivity.class);
                            intent.putExtra("idloaisanpham",mangloaisp.get(i).getId());
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 6:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,LienHeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 7:
                        if(CheckConnection.isInternetAvailable(getApplicationContext()))
                        {
                            Intent intent = new Intent(MainActivity.this,ThongTinActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;



                }
            }
        });
    }


    private void GetDuLieuSPMoiNhat() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.Duongdansanphammoinhat, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response != null){
                    int ID= 0;
                    String Tensanpham= "";
                    Integer Giasanpham= 0;
                    String Hinhanhsanpham= "";
                    String Motasanpham= "";
                    int IDsanpham= 0;
                    for(int i=0; i<response.length(); i++){
                        try{
                            JSONObject jsonObject = response.getJSONObject(i);
                            ID = jsonObject.getInt("id");
                            Tensanpham = jsonObject.getString("tensp");
                            Giasanpham = jsonObject.getInt("giasp");
                            Hinhanhsanpham = jsonObject.getString("hinhanhsp");
                            Motasanpham = jsonObject.getString("motasp");
                            IDsanpham = jsonObject.getInt("idsanpham");
                            mangsanpham.add(new Sanpham(ID, Tensanpham, Giasanpham, Hinhanhsanpham, Motasanpham, IDsanpham));
                            sanphamAdapter.notifyDataSetChanged();
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckConnection.ShowToast_Short(getApplicationContext(), error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void GetDuLieuLoaisp() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.DuongdanLoaisp, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response != null){
                   for(int i=0; i<response.length(); i++){
                       try{
                           JSONObject jsonObject = response.getJSONObject(i);
                           id = jsonObject.getInt("id");
                           tenloaisp = jsonObject.getString("tenloaisp");
                           hinhanhloaisp = jsonObject.getString("hinhanhloaisp");
                           mangloaisp.add(new Loaisp(id, tenloaisp, hinhanhloaisp));
                           loaispAdapter.notifyDataSetChanged();
                       }catch(JSONException e){
                           e.printStackTrace();
                       }
                   }
                    mangloaisp.add(6, new Loaisp(0,"Liên hệ","https://as2.ftcdn.net/jpg/05/00/52/07/1000_F_500520765_ZYO8tDmIOR1Ysu6hVthPEw9BwFBKx3qI.jpg"));
                    mangloaisp.add(7, new Loaisp(0,"Thông tin","https://static-00.iconduck.com/assets.00/info-icon-2048x2048-tcgtx810.png"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckConnection.ShowToast_Short(getApplicationContext(), error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void ActionViewFlipper() {
        ArrayList<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://newstore24h.com/wp-content/uploads/2024/08/thiet-ke-cua-hang-trai-cay-9.jpg");
        mangquangcao.add("https://thietkehoanggia.com/uploaded/NewFolder/H%E1%BA%B1ng/c%E1%BB%ADa%20h%C3%A0ng%20tr%C3%A1i%20c%C3%A2y%20nh%E1%BA%ADp%20kh%E1%BA%A9u/thiet-ke-cua-hang-trai-cay-nhap-khau%20(2).jpg");
        mangquangcao.add("https://noithattugia.com/wp-content/uploads/2021/06/thiet-ke-noi-that-shop-trai-cay_14.jpg");
        mangquangcao.add("https://thietkehoanggia.com/uploaded/NewFolder/H%E1%BA%B1ng/c%E1%BB%ADa%20h%C3%A0ng%20tr%C3%A1i%20c%C3%A2y%20nh%E1%BA%ADp%20kh%E1%BA%A9u/thiet-ke-cua-hang-trai-cay-nhap-khau%20(5).jpg");

        for (int i = 0; i < mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Picasso.with(getApplicationContext())
                    .load(mangquangcao.get(i))
                    .fit() // Tự động điều chỉnh kích thước cho vừa
                    .centerCrop() // Crop hình ảnh để vừa khít
                    .into(imageView);

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(5000); // 5 giây chuyển ảnh
        viewFlipper.setAutoStart(true);    // Tự động chạy
        Animation animation_slide_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation animation_slide_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
        viewFlipper.setInAnimation(animation_slide_in);
        viewFlipper.setOutAnimation(animation_slide_out);

    }


    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }
    private void Anhxa() {
        toolbar = (Toolbar)findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        recyclerViewmanhinhchinh =(RecyclerView) findViewById(R.id.recyclerview);
        navigationView =(NavigationView) findViewById(R.id.navigationview);
        listViewmanhinhchinh =(ListView) findViewById(R.id.listviewmanhinhchinh);
        drawerLayout =(DrawerLayout) findViewById(R.id.drawerlayout);
        mangloaisp= new ArrayList<>();
        mangloaisp.add(0, new Loaisp(0,"Trang chính","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFzVuVE5T79W84S5eYPDoqJAtFWUB9xYtoAw&s"));
        loaispAdapter= new LoaispAdapter(mangloaisp,getApplicationContext());
        listViewmanhinhchinh.setAdapter(loaispAdapter);
        mangsanpham= new ArrayList<>();
        sanphamAdapter= new SanphamAdapter(getApplicationContext(),mangsanpham);
        recyclerViewmanhinhchinh.setHasFixedSize(true);
        recyclerViewmanhinhchinh.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recyclerViewmanhinhchinh.setAdapter(sanphamAdapter);
        if(manggiohang !=null)
        {

        }else{
            manggiohang = new ArrayList<>();
        }
    }
}
