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
 * Created by ADMIN on 5/23/2025.
 */

public class DuahauAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arrayduahau;

    public DuahauAdapter(Context context, ArrayList<Sanpham> arrayduahau) {
        this.context = context;
        this.arrayduahau = arrayduahau;
    }

    @Override
    public int getCount() {
        return arrayduahau.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayduahau.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder{
        public TextView txttenduahau,txtgiaduahau,txtmotaduahau;
        public ImageView imgduahau;




    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DuahauAdapter.ViewHolder viewHolder = null;
        if(view == null)
        {
            viewHolder = new DuahauAdapter.ViewHolder();
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_duahau,null);
            viewHolder.txttenduahau =(TextView) view.findViewById(R.id.textviewduahau);
            viewHolder.txtgiaduahau =(TextView) view.findViewById(R.id.textviewgiaduahau);
            viewHolder.txtmotaduahau =(TextView) view.findViewById(R.id.textviewmotaduahau);
            viewHolder.imgduahau = (ImageView) view.findViewById(R.id.imageviewduahau);
            view.setTag(viewHolder);

        }
        else
        {
            viewHolder =(DuahauAdapter.ViewHolder) view.getTag();

        }
        Sanpham sanpham =(Sanpham) getItem(i);
        viewHolder.txttenduahau.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiaduahau.setText("Giá: "+decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotaduahau.setMaxLines(2);
        viewHolder.txtmotaduahau.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotaduahau.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imgduahau);
        return view;
    }
}
