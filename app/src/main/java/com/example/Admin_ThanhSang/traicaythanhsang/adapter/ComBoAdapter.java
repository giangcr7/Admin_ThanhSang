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

public class ComBoAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arraycombo;

    public ComBoAdapter(Context context, ArrayList<Sanpham> arraycombo) {
        this.context = context;
        this.arraycombo = arraycombo;
    }

    @Override
    public int getCount() {
        return arraycombo.size();
    }

    @Override
    public Object getItem(int i) {
        return arraycombo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder{
        public TextView txttencombo,txtgiacombo,txtmotacombo;
        public ImageView imgcombo;




    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ComBoAdapter.ViewHolder viewHolder = null;
        if(view == null)
        {
            viewHolder = new ComBoAdapter.ViewHolder();
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_combo,null);
            viewHolder.txttencombo =(TextView) view.findViewById(R.id.textviewcombo);
            viewHolder.txtgiacombo =(TextView) view.findViewById(R.id.textviewgiacombo);
            viewHolder.txtmotacombo =(TextView) view.findViewById(R.id.textviewmotacombo);
            viewHolder.imgcombo = (ImageView) view.findViewById(R.id.imageviewcombo);
            view.setTag(viewHolder);

        }
        else
        {
            viewHolder =(ComBoAdapter.ViewHolder) view.getTag();

        }
        Sanpham sanpham =(Sanpham) getItem(i);
        viewHolder.txttencombo.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiacombo.setText("Giá: "+decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotacombo.setMaxLines(2);
        viewHolder.txtmotacombo.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotacombo.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imgcombo);
        return view;
    }
}
