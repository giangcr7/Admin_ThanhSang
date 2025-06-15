package com.example.Admin_ThanhSang.traicaythanhsang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Loaisp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ADMIN on 5/20/2025.
 */

public class LoaispAdapter extends BaseAdapter{
    ArrayList<Loaisp> arraylistloaisp;
    Context context;

    public LoaispAdapter(ArrayList<Loaisp> arraylistloaisp, Context context) {
        this.arraylistloaisp = arraylistloaisp;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arraylistloaisp.size();
    }

    @Override
    public Object getItem(int i) {
        return arraylistloaisp.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder{
        TextView txttenoaisp;
        ImageView imgloaisp;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder= null;
        if (view==null){
            viewHolder= new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.dong_listview_loaisp, null);
            viewHolder.txttenoaisp= (TextView) view.findViewById(R.id.textviewloaisp);
            viewHolder.imgloaisp= (ImageView) view.findViewById(R.id.imageviewloaisp);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        Loaisp loaisp= (Loaisp) getItem(i);
        viewHolder.txttenoaisp.setText(loaisp.getTenloaisp());
        Picasso.with(context).load(loaisp.getHinhanhloaisp())
                .placeholder(R.drawable.noimage)
                .placeholder(R.drawable.error)
                .into(viewHolder.imgloaisp);
        return view;
    }
}
