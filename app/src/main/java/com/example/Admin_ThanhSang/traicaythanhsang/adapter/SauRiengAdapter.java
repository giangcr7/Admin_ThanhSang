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
 * Created by LE HOANG GIANG on 5/22/2025.
 */


public class SauRiengAdapter extends BaseAdapter {
    Context context;
    ArrayList<Sanpham> arraysaurieng;

    public SauRiengAdapter(Context context, ArrayList<Sanpham> arraysaurieng) {
        this.context = context;
        this.arraysaurieng = arraysaurieng;
    }

    @Override
    public int getCount() {
        return arraysaurieng.size();
    }

    @Override
    public Object getItem(int i) {
        return arraysaurieng.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder{
        public TextView txttensaurieng,txtgiasaurieng,txtmotasaurieng;
        public ImageView imgsaurieng;


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_saurieng,null);
            viewHolder.txttensaurieng =(TextView) view.findViewById(R.id.textviewsaurieng);
            viewHolder.txtgiasaurieng =(TextView) view.findViewById(R.id.textviewgiasaurieng);
            viewHolder.txtmotasaurieng =(TextView) view.findViewById(R.id.textviewmotasaurieng);
            viewHolder.imgsaurieng = (ImageView) view.findViewById(R.id.imageviewsaurieng);
            view.setTag(viewHolder);

        }
        else
        {
            viewHolder =(ViewHolder) view.getTag();

        }
        Sanpham sanpham =(Sanpham) getItem(i);
        viewHolder.txttensaurieng.setText(sanpham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiasaurieng.setText("Giá: "+decimalFormat.format(sanpham.getGiasanpham())+ "Đ");
        viewHolder.txtmotasaurieng.setMaxLines(2);
        viewHolder.txtmotasaurieng.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtmotasaurieng.setText(sanpham.getMotasanpham());
        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imgsaurieng);
        return view;
    }
}
