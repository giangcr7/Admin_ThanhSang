package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Giohang;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Sanpham;
import com.squareup.picasso.Picasso;

public class ChiTietSanPham extends AppCompatActivity {
    Toolbar toolbarChitiet;
    ImageView imgChitiet;
    TextView txtten,txtgia,txtmota;
    Spinner spinner;
    Button btndatmua;
    int id=  0;
    String TenChiTiet = "";
    int GiaChiTiet = 0;
    String HinhAnhChiTiet = "";
    String MoTaChiTiet = "";
    int idsanpham = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_san_pham);
        Anhxa();
        ActionToolbar();
        GetInformation();
        CatchEventSpinner();
        EventButton();

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
        }
        return super.onOptionsItemSelected(item);
    }

    private void EventButton() {
        btndatmua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.manggiohang.size()>0)
                {
                    int sl = Integer.parseInt(spinner.getSelectedItem().toString());
                    boolean exists = false;
                    for(int i = 0;i<MainActivity.manggiohang.size();i++)
                    {
                        if(MainActivity.manggiohang.get(i).getIdsp() == id)
                        {
                            MainActivity.manggiohang.get(i).setSoluongsp(MainActivity.manggiohang.get(i).getSoluongsp()+sl);
                            if(MainActivity.manggiohang.get(i).getSoluongsp() >=10)
                            {
                                MainActivity.manggiohang.get(i).setSoluongsp(10);
                            }
                            MainActivity.manggiohang.get(i).setGiasp(GiaChiTiet *MainActivity.manggiohang.get(i).getSoluongsp());
                            exists = true;
                        }
                    }
                    if(exists == false)
                    {
                        int soluong =Integer.parseInt(spinner.getSelectedItem().toString());
                        long giamoi = soluong * GiaChiTiet;
                        MainActivity.manggiohang.add(new Giohang(id,TenChiTiet,giamoi,HinhAnhChiTiet,soluong));
                    }

                }
                else
                {
                    int soluong =Integer.parseInt(spinner.getSelectedItem().toString());
                    long giamoi = soluong * GiaChiTiet;
                    MainActivity.manggiohang.add(new Giohang(id,TenChiTiet,giamoi,HinhAnhChiTiet,soluong));

                }
                Intent intent = new Intent(getApplicationContext(),GioHangActivity.class);
                startActivity(intent);
            }
        });
    }


    private void CatchEventSpinner() {
        Integer[] soluong = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> arrayadater = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item,soluong);
        spinner.setAdapter(arrayadater);
    }

    private void GetInformation() {

        Sanpham sanpham = (Sanpham) getIntent().getSerializableExtra("thongtinsanpham");
        id = sanpham.getID();
        TenChiTiet = sanpham.getTensanpham();
        GiaChiTiet = sanpham.getGiasanpham();
        HinhAnhChiTiet = sanpham.getHinhanhsanpham();
        MoTaChiTiet = sanpham.getMotasanpham();
        idsanpham  =sanpham.getIDSanpham();
        txtten.setText(TenChiTiet);
        DecimalFormat decimalformat = new DecimalFormat("###,###,###");
        txtgia.setText("Giá : " + decimalformat.format(GiaChiTiet)  + "Đ");
        txtmota.setText(MoTaChiTiet);
        Picasso.with(getApplicationContext()).load(HinhAnhChiTiet)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(imgChitiet);
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbarChitiet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarChitiet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    private void Anhxa() {
        toolbarChitiet = (Toolbar) findViewById(R.id.toolbarchitietsanpham);
        imgChitiet = (ImageView) findViewById(R.id.imageviewchitietsanpham);
        txtten  = (TextView) findViewById(R.id.textviewtenchitietsanpham);
        txtgia = (TextView) findViewById(R.id.textviewgiachitietsanpham);
        txtmota = (TextView) findViewById(R.id.textviewmotachitietsanpham);
        spinner = (Spinner) findViewById(R.id.spinner);
        btndatmua = (Button) findViewById(R.id.buttondatmua);
    }
}

