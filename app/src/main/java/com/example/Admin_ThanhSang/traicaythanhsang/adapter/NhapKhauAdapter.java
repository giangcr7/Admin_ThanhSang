package com.example.Admin_ThanhSang.traicaythanhsang.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Sanpham;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by LE HOANG GIANG on 6/2/2025.
 */

public class NhapKhauAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arraynhapkhau;
    public NhapKhauAdapter(Context context,ArrayList<Sanpham> arraynhapkhau){
        this.context = context;
        this.arraynhapkhau = arraynhapkhau;

    }
    @Override
    public int getCount() {
        return arraynhapkhau.size();
    }

    @Override
    public Object getItem(int i) {
        return arraynhapkhau.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder
    {
        public TextView txttennhapkhau,txtgianhapkhau,txtmotanhapkhau;
        public ImageView imgnhapkhau;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NhapKhauAdapter.ViewHolder viewHolder = null;
        if(view ==null)
        {
            viewHolder = new NhapKhauAdapter.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_nhapkhau,null);
            viewHolder.txttennhapkhau = (TextView) view.findViewById(R.id.textviewnhapkhau);
            viewHolder.txtgianhapkhau = (TextView) view.findViewById(R.id.textviewgianhapkhau);
            viewHolder.txtmotanhapkhau = (TextView) view.findViewById(R.id.textviewmotanhapkhau);
            viewHolder.imgnhapkhau = (ImageView) view.findViewById(R.id.imageviewnhapkhau);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder =(NhapKhauAdapter.ViewHolder) view.getTag();
        }
        Sanpham sanpham =(Sanpham) getItem(i);
        viewHolder.txttennhapkhau.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgianhapkhau.setText("Giá: "+decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotanhapkhau.setMaxLines(2);
        viewHolder.txtmotanhapkhau.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotanhapkhau.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imgnhapkhau);
        return view;
    }
}
