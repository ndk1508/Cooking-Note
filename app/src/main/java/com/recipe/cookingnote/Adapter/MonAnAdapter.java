package com.recipe.cookingnote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.recipe.cookingnote.R;
import com.recipe.cookingnote.activity.ThemMonAnActivity;
import com.recipe.cookingnote.Model.MonAn;

import java.util.ArrayList;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.MonAnViewHolder> {

    private ArrayList<MonAn> monAnList;
    private Context context;

    public MonAnAdapter(ArrayList<MonAn> monAnList, Context context) {
        this.monAnList = monAnList;
        this.context = context;
    }

    @NonNull
    @Override
    public MonAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new MonAnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonAnViewHolder holder, int position) {
        MonAn mon = monAnList.get(position);
        holder.tvTenMon.setText(mon.getTenMon());

        if (mon.getAnhMon() != null && !mon.getAnhMon().isEmpty()) {
            holder.imgMon.setImageURI(Uri.parse(mon.getAnhMon()));
        } else {
            holder.imgMon.setImageResource(R.drawable.ic_image_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ThemMonAnActivity.class);
            intent.putExtra("idMonAn", mon.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return monAnList.size();
    }

    static class MonAnViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon;
        ImageView imgMon;

        public MonAnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvTenMonItem);
            imgMon = itemView.findViewById(R.id.imgMonItem);
        }
    }
}
