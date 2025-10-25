package com.recipe.cookingnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.recipe.cookingnote.R;
import com.recipe.cookingnote.activity.SuaMonAnActivity;
import com.recipe.cookingnote.model.MonAn;

import java.util.List;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.ViewHolder> {

    private Context context;
    private List<MonAn> list;

    public MonAnAdapter(Context context, List<MonAn> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_mon_an, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn mon = list.get(position);
        holder.tvTenMon.setText(mon.getTenMon());
        holder.tvDanhMuc.setText(mon.getDanhMuc());

        // Ảnh
        if (mon.getAnh() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mon.getAnh(), 0, mon.getAnh().length);
            holder.imgMonAn.setImageBitmap(bitmap);
        } else {
            holder.imgMonAn.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Khi bấm nút sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaMonAnActivity.class);
            intent.putExtra("MON_ID", mon.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMonAn;
        TextView tvTenMon, tvDanhMuc;
        ImageButton btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMonAn = itemView.findViewById(R.id.imgMonAn);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvDanhMuc = itemView.findViewById(R.id.tvDanhMuc);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}