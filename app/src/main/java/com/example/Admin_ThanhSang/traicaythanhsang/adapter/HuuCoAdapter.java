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

public class HuuCoAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arrayhuuco;

    public HuuCoAdapter(Context context, ArrayList<Sanpham> arrayhuuco) {
        this.context = context;
        this.arrayhuuco = arrayhuuco;
    }

    @Override
    public int getCount() {
        return arrayhuuco.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayhuuco.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder{
        public TextView txttenhuuco,txtgiahuuco,txtmotahuuco;
        public ImageView imghuuco;


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HuuCoAdapter.ViewHolder viewHolder = null;
        if(view == null)
        {
            viewHolder = new HuuCoAdapter.ViewHolder();
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_huuco,null);
            viewHolder.txttenhuuco =(TextView) view.findViewById(R.id.textviewhuuco);
            viewHolder.txtgiahuuco =(TextView) view.findViewById(R.id.textviewgiahuuco);
            viewHolder.txtmotahuuco =(TextView) view.findViewById(R.id.textviewmotahuuco);
            viewHolder.imghuuco = (ImageView) view.findViewById(R.id.imageviewhuuco);
            view.setTag(viewHolder);

        }
        else
        {
            viewHolder =(HuuCoAdapter.ViewHolder) view.getTag();

        }
        Sanpham sanpham =(Sanpham) getItem(i);
        viewHolder.txttenhuuco.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiahuuco.setText("Giá: "+decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotahuuco.setMaxLines(2);
        viewHolder.txtmotahuuco.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotahuuco.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imghuuco);
        return view;
    }
}
