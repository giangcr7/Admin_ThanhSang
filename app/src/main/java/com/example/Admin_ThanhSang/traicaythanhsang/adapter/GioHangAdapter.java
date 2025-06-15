package com.example.Admin_ThanhSang.traicaythanhsang.adapter;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.activity.GioHangActivity;
import com.example.Admin_ThanhSang.traicaythanhsang.activity.MainActivity;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Giohang;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by LE HOANG GIANG on 5/23/2025.
 */

public class GioHangAdapter extends BaseAdapter {
    Context context;
    ArrayList<Giohang> arraygiohang;

    public GioHangAdapter(Context context, ArrayList<Giohang> arraygiohang) {
        this.context = context;
        this.arraygiohang = arraygiohang;
    }

    @Override
    public int getCount() {
        return arraygiohang.size();
    }

    @Override
    public Object getItem(int i) {
        return arraygiohang.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder
    {
        public TextView txttengiohang,txtgiagiohan;
        public ImageView imggiohang;
        public Button btnminus,btbvalues,btnplus;

    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dong_giohang,null);
            viewHolder.txttengiohang = (TextView) view.findViewById(R.id.textviewtengiohang);
            viewHolder.txtgiagiohan = (TextView) view.findViewById(R.id.textviewgiagiohang);
            viewHolder.imggiohang  =(ImageView) view.findViewById(R.id.imageviewgiohang);
            viewHolder.btnminus  = (Button) view.findViewById(R.id.buttonins);
            viewHolder.btbvalues = (Button) view.findViewById(R.id.buttonvalues);
            viewHolder.btnplus = (Button) view.findViewById(R.id.buttonplus);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();

        }
        final Giohang giohang  = (Giohang) getItem(i);
        viewHolder.txttengiohang.setText(giohang.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.txtgiagiohan.setText(decimalFormat.format(giohang.getGiasp()) + "Đ");
        Picasso.with(context).load(giohang.getHinhsp())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imggiohang);

        viewHolder.btbvalues.setText(giohang.getSoluongsp() + "");
        int sl = Integer.parseInt(viewHolder.btbvalues.getText().toString());
        if (sl  >=10)
        {
            viewHolder.btnplus.setVisibility(View.INVISIBLE);
            viewHolder.btnminus.setVisibility(View.VISIBLE);
        }
        else if(sl<=1) {
            viewHolder.btnminus.setVisibility(View.INVISIBLE);
        }
        else if(sl>=1)
        {
            viewHolder.btnminus.setVisibility(View.VISIBLE);
            viewHolder.btnplus.setVisibility(View.VISIBLE);
        }
        //final ViewHolder finalViewHolder = viewHolder;
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int slmoinhat = Integer.parseInt(finalViewHolder.btbvalues.getText().toString()) +1;
                int slht = MainActivity.manggiohang.get(i).getSoluongsp();
                long giaht = MainActivity.manggiohang.get(i).getGiasp();
                MainActivity.manggiohang.get(i).setSoluongsp(slmoinhat);
                long giamoinhat = (giaht*slmoinhat)/slht;
                MainActivity.manggiohang.get(i).setGiasp(giamoinhat);
                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                finalViewHolder.txtgiagiohan.setText(decimalFormat.format(giamoinhat) + "Đ");
                GioHangActivity.EventUltil();
                if(slmoinhat > 9)
                {
                    finalViewHolder.btnplus.setVisibility(View.INVISIBLE);
                    finalViewHolder.btnminus.setVisibility(View.VISIBLE);
                    finalViewHolder.btbvalues.setText(String.valueOf(slmoinhat));


                }
                else
                {
                    finalViewHolder.btnminus.setVisibility(View.VISIBLE);
                    finalViewHolder.btnplus.setVisibility(View.VISIBLE);
                    finalViewHolder.btbvalues.setText(String.valueOf(slmoinhat));

                }


            }
        });
        final ViewHolder finalViewHolder1 = viewHolder;
        viewHolder.btnminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int slmoinhat = Integer.parseInt(finalViewHolder1.btbvalues.getText().toString()) -1;
                int slht = MainActivity.manggiohang.get(i).getSoluongsp();
                long giaht = MainActivity.manggiohang.get(i).getGiasp();
                MainActivity.manggiohang.get(i).setSoluongsp(slmoinhat);
                long giamoinhat = (giaht*slmoinhat)/slht;
                MainActivity.manggiohang.get(i).setGiasp(giamoinhat);
                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                finalViewHolder1.txtgiagiohan.setText(decimalFormat.format(giamoinhat) + "Đ");
                GioHangActivity.EventUltil();
                if(slmoinhat < 2)
                {
                    finalViewHolder1.btnminus.setVisibility(View.INVISIBLE);
                    finalViewHolder1.btnplus.setVisibility(View.VISIBLE);
                    finalViewHolder1.btbvalues.setText(String.valueOf(slmoinhat));


                }
                else
                {
                    finalViewHolder1.btnminus.setVisibility(View.VISIBLE);
                    finalViewHolder1.btnplus.setVisibility(View.VISIBLE);
                    finalViewHolder1.btbvalues.setText(String.valueOf(slmoinhat));

                }
            }
        });
        return view;
    }
}
