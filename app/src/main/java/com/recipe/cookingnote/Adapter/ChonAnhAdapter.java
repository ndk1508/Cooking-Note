package com.recipe.cookingnote.Adapter; // Thay bằng tên package của bạn

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.recipe.cookingnote.R; // Thay bằng tên package của bạn
import java.util.List;

/**
 * Adapter hiển thị danh sách ảnh (từ drawable) để người dùng chọn làm ảnh món ăn.
 */
public class ChonAnhAdapter extends RecyclerView.Adapter<ChonAnhAdapter.AnhViewHolder> {

    private Context context;
    private List<Integer> imageIds; // Danh sách ID của các ảnh trong thư mục drawable
    private OnImageClickListener listener; // Lắng nghe sự kiện click ảnh

    /**
     * Interface để truyền sự kiện khi người dùng chọn ảnh về Activity cha.
     */
    public interface OnImageClickListener {
        void onImageClick(int imageId);
    }

    /**
     * Constructor - khởi tạo adapter với context, danh sách ảnh, và listener.
     */
    public ChonAnhAdapter(Context context, List<Integer> imageIds, OnImageClickListener listener) {
        this.context = context;
        this.imageIds = imageIds;
        this.listener = listener;
    }

    /**
     * Tạo ViewHolder mới khi RecyclerView cần hiển thị một item ảnh.
     */
    @NonNull
    @Override
    public AnhViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng item ảnh (item_anh_chon.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_anh_chon, parent, false);
        return new AnhViewHolder(view);
    }

    /**
     * Gán dữ liệu (ảnh) vào ViewHolder tại vị trí position.
     */
    @Override
    public void onBindViewHolder(@NonNull AnhViewHolder holder, int position) {
        int imageId = imageIds.get(position);
        holder.imageView.setImageResource(imageId); // Hiển thị ảnh

        // Bắt sự kiện khi người dùng bấm vào ảnh
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(imageId); // Gửi ID ảnh về Activity
            }
        });
    }

    /**
     * Trả về số lượng ảnh trong danh sách.
     */
    @Override
    public int getItemCount() {
        return imageIds.size();
    }

    /**
     * ViewHolder đại diện cho từng item ảnh trong RecyclerView.
     */
    public static class AnhViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public AnhViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgAnhLuaChon); // ánh xạ ImageView từ layout item
        }
    }
}
