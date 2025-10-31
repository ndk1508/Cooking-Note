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

import com.recipe.cookingnote.Model.MonAn;
import com.recipe.cookingnote.R;
import com.recipe.cookingnote.activity.ChiTietMonAnActivity;

import java.util.ArrayList;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.MonAnViewHolder> {

    private ArrayList<MonAn> monAnList; // Danh sách các món ăn
    private Context context;            // Ngữ cảnh để inflate layout và start Activity

    // Constructor
    public MonAnAdapter(ArrayList<MonAn> monAnList, Context context) {
        this.monAnList = monAnList;
        this.context = context;
    }

    /**
     * Hàm tạo ViewHolder cho mỗi item trong RecyclerView
     */
    @NonNull
    @Override
    public MonAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_mon_an.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new MonAnViewHolder(view);
    }

    /**
     * Hàm gán dữ liệu cho từng item
     */
    @Override
    public void onBindViewHolder(@NonNull MonAnViewHolder holder, int position) {
        // Lấy món ăn hiện tại
        MonAn currentMonAn = monAnList.get(position);

        // Gán tên món ăn
        holder.txtTenMon.setText(currentMonAn.getTenMon());

        // Hiển thị ảnh món ăn
        String anhPath = currentMonAn.getAnhMon();

        if (anhPath != null && !anhPath.isEmpty()) {
            try {
                // Nếu ảnh được lưu dưới dạng resource ID (VD: R.drawable.bun_bo)
                int resourceId = Integer.parseInt(anhPath);
                holder.imgMonAn.setImageResource(resourceId);
            } catch (NumberFormatException e) {
                // Nếu ảnh là URI (chọn từ thư viện)
                holder.imgMonAn.setImageURI(Uri.parse(anhPath));
            }
        } else {
            // Ảnh mặc định nếu chưa có ảnh
            holder.imgMonAn.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Khi người dùng bấm vào món ăn → mở trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChiTietMonAnActivity.class);
            intent.putExtra(ChiTietMonAnActivity.EXTRA_MONAN_ID, currentMonAn.getId()); // Gửi ID món ăn sang Activity chi tiết
            context.startActivity(intent);
        });
    }
    /**
     * Trả về số lượng item trong danh sách
     */
    @Override
    public int getItemCount() {
        return monAnList.size();
    }
    /**
     * ViewHolder giữ tham chiếu đến các View trong layout item_mon_an.xml
     */
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