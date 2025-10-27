package com.recipe.cookingnote.Adapter; // <-- Hãy chắc chắn đây là package của bạn

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

import com.recipe.cookingnote.Model.MonAn; // <-- Class MonAn của bạn
import com.recipe.cookingnote.R;
import com.recipe.cookingnote.activity.ChiTietMonAnActivity;

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
        // Lấy đối tượng MonAn hiện tại từ danh sách
        MonAn currentMonAn = monAnList.get(position);

        // Gán tên món ăn
        holder.txtTenMon.setText(currentMonAn.getTenMon());

        // Logic hiển thị ảnh thông minh
        String anhPath = currentMonAn.getAnhMon();
        if (anhPath != null && !anhPath.isEmpty()) {
            try {
                int resourceId = Integer.parseInt(anhPath);
                holder.imgMonAn.setImageResource(resourceId);
            } catch (NumberFormatException e) {
                holder.imgMonAn.setImageURI(Uri.parse(anhPath));
            }
        } else {
            holder.imgMonAn.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Xử lý sự kiện khi người dùng click vào một món ăn
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChiTietMonAnActivity.class);

                // Dòng này hoạt động tốt vì class MonAn của bạn đã có phương thức getId()
                intent.putExtra(ChiTietMonAnActivity.EXTRA_MONAN_ID, currentMonAn.getId());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return monAnList.size();
    }

    public static class MonAnViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMonAn;
        TextView txtTenMon;

        public MonAnViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMonAn = itemView.findViewById(R.id.imgMonAnItem);
            txtTenMon = itemView.findViewById(R.id.txtTenMonItem);
        }
    }
}