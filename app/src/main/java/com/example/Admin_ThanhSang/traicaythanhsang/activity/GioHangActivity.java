package com.example.Admin_ThanhSang.traicaythanhsang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.adapter.GioHangAdapter;
import com.example.Admin_ThanhSang.traicaythanhsang.ultil.CheckConnection;

public class GioHangActivity extends AppCompatActivity {
    ListView lvgiohang;
    TextView txtthongbao;
    static TextView txttongtien;
    Button  btnthanhtoan,btntieptucmua;
    Toolbar toolbargiohang;
    GioHangAdapter gioHangAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang2);
        Anhxa();
        ActionToolBar();
        CheckData();
        EventUltil();
        CatchOnItemListView();
        EventButton();

        


    }

    private void EventButton() {
        btntieptucmua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        btnthanhtoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.manggiohang.size()>0)
                {
                    Intent intent = new Intent(getApplicationContext(),Thongtinkhachhang.class);
                    startActivity(intent);

                }
                else
                {
                    CheckConnection.ShowToast_Short(getApplicationContext(),"Giỏ hàng của bạn chưa có sản phẩm để thanh toán");
                }
            }
        });
    }

    private void CatchOnItemListView() {
        lvgiohang.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GioHangActivity.this);
                builder.setTitle("Xác nhận xoá sản phầm");
                builder.setMessage("Bạn có chắc chắn xoá!!!");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (MainActivity.manggiohang.size() <=0)
                        {
                            txtthongbao.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            MainActivity.manggiohang.remove(position);
                            gioHangAdapter.notifyDataSetChanged();
                            EventUltil();
                            if(MainActivity.manggiohang.size()<=0)
                            {
                                txtthongbao.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                txtthongbao.setVisibility(View.INVISIBLE);
                                gioHangAdapter.notifyDataSetChanged();
                                EventUltil();
                            }
                        }
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gioHangAdapter.notifyDataSetChanged();
                        EventUltil();

                    }
                });
                builder.show();



                return true;
            }
        });
    }

    public static void EventUltil() {
        long tongtien = 0;
        for(int i = 0; i<MainActivity.manggiohang.size();i++)
        {
            tongtien += MainActivity.manggiohang.get(i).getGiasp();

        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txttongtien.setText(decimalFormat.format(tongtien) + "Đ");

    }

    private void CheckData() {
        if(MainActivity.manggiohang.size()<=0)
        {
            gioHangAdapter.notifyDataSetChanged();
            txtthongbao.setVisibility(View.VISIBLE);
            lvgiohang.setVisibility(View.INVISIBLE);
        }
        else
        {
            gioHangAdapter.notifyDataSetChanged();
            txtthongbao.setVisibility(View.INVISIBLE);
            lvgiohang.setVisibility(View.VISIBLE);
        }
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbargiohang);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbargiohang.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    private void Anhxa() {
        lvgiohang = (ListView) findViewById(R.id.listviewgiohang);
        txtthongbao = (TextView) findViewById(R.id.textviewthongbao);
        txttongtien = (TextView) findViewById(R.id.txttongtien);
        btnthanhtoan = (Button) findViewById(R.id.buttonthanhtoangiohang);
        btntieptucmua = (Button) findViewById(R.id.buttontieptucmuahang);
        toolbargiohang = (Toolbar) findViewById(R.id.toolbargiohang);
        gioHangAdapter = new GioHangAdapter(GioHangActivity.this,MainActivity.manggiohang);
        lvgiohang.setAdapter(gioHangAdapter);
    }
}
