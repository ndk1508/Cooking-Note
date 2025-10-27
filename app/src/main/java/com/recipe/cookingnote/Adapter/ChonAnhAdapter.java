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

public class ChonAnhAdapter extends RecyclerView.Adapter<ChonAnhAdapter.AnhViewHolder> {

    private Context context;
    private List<Integer> imageIds; // Danh sách ID của các ảnh trong drawable
    private OnImageClickListener listener;

    // Interface để gửi sự kiện click về Activity
    public interface OnImageClickListener {
        void onImageClick(int imageId);
    }

    public ChonAnhAdapter(Context context, List<Integer> imageIds, OnImageClickListener listener) {
        this.context = context;
        this.imageIds = imageIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnhViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_anh_chon, parent, false);
        return new AnhViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnhViewHolder holder, int position) {
        int imageId = imageIds.get(position);
        holder.imageView.setImageResource(imageId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(imageId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageIds.size();
    }

    public static class AnhViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public AnhViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgAnhLuaChon);
        }
    }
}