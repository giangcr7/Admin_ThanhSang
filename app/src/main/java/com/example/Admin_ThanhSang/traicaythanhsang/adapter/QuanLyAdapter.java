package com.example.Admin_ThanhSang.traicaythanhsang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextUtils;

import com.example.Admin_ThanhSang.traicaythanhsang.R;
import com.example.Admin_ThanhSang.traicaythanhsang.model.Sanpham;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class QuanLyAdapter extends RecyclerView.Adapter<QuanLyAdapter.ItemHolder> {

    private Context context;
    private ArrayList<Sanpham> arrayquanly;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public QuanLyAdapter(Context context, ArrayList<Sanpham> arrayquanly) {
        this.context = context;
        this.arrayquanly = arrayquanly;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dong_quanly, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {
        Sanpham sanpham = arrayquanly.get(position);
        holder.txttenquanly.setText(sanpham.getTensanpham());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgiaquanly.setText("Giá: " + decimalFormat.format(sanpham.getGiasanpham()) + "Đ");

        holder.txtmotaquanly.setMaxLines(2);
        holder.txtmotaquanly.setEllipsize(TextUtils.TruncateAt.END);
        holder.txtmotaquanly.setText(sanpham.getMotasanpham());

        Picasso.with(context).load(sanpham.getHinhanhsanpham())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.imgquanly);
    }

    @Override
    public int getItemCount() {
        return arrayquanly.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView txttenquanly, txtgiaquanly, txtmotaquanly;
        ImageView imgquanly;

        public ItemHolder(View itemView) {
            super(itemView);
            txttenquanly = (TextView) itemView.findViewById(R.id.textviewquanly);
            txtgiaquanly = (TextView) itemView.findViewById(R.id.textviewgiaquanly);
            txtmotaquanly = (TextView) itemView.findViewById(R.id.textviewmotaquanly);
            imgquanly = (ImageView) itemView.findViewById(R.id.imageviewquanly);
        }
    }
}
