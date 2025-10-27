package com.recipe.cookingnote.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton; // Thêm
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.recipe.cookingnote.R;
import com.recipe.cookingnote.database.DatabaseHelper;

public class ChiTietMonAnActivity extends AppCompatActivity {

    public static final String EXTRA_MONAN_ID = "EXTRA_MONAN_ID";

    // Các View trong layout mới
    private ImageView imgChiTiet;
    private TextView txtTenMonChiTiet, txtNguyenLieuChiTiet, txtBuocLamChiTiet;
    private Button btnSua, btnXoa;
    private ImageButton btnBack;

    private DatabaseHelper dbHelper;
    private int currentMonAnId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SỬA LỖI 1: Thiếu dấu ")" và tên layout chưa đúng
        setContentView(R.layout.layout_chi_tiet);

        // Ánh xạ View theo ID trong layout mới
        imgChiTiet = findViewById(R.id.imgChiTiet);
        txtTenMonChiTiet = findViewById(R.id.txtTenMonChiTiet);
        txtNguyenLieuChiTiet = findViewById(R.id.txtNguyenLieuChiTiet);
        txtBuocLamChiTiet = findViewById(R.id.txtBuocLamChiTiet);
        btnSua = findViewById(R.id.btnSua);       // SỬA LỖI 2: Ánh xạ đúng ID
        btnXoa = findViewById(R.id.btnXoa);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);

        currentMonAnId = getIntent().getIntExtra(EXTRA_MONAN_ID, -1);

        // Gán sự kiện click cho các nút
        btnBack.setOnClickListener(v -> finish()); // Nút quay lại

        btnSua.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietMonAnActivity.this, ThemMonAnActivity.class);
            intent.putExtra(ThemMonAnActivity.EXTRA_EDIT_MONAN_ID, currentMonAnId);
            startActivity(intent);
        });

        btnXoa.setOnClickListener(v -> {
            showDeleteConfirmDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentMonAnId != -1) {
            loadMonAnDetails(currentMonAnId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadMonAnDetails(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            // Cập nhật câu truy vấn cho đơn giản hơn (layout mới không có danh mục)
            String queryMonAn = "SELECT tenMon, anhMon FROM MonAn WHERE idMonAn = ?";
            try (Cursor cursorMonAn = db.rawQuery(queryMonAn, new String[]{String.valueOf(id)})) {
                if (cursorMonAn.moveToFirst()) {
                    txtTenMonChiTiet.setText(cursorMonAn.getString(0));
                    String anhPath = cursorMonAn.getString(1);
                    if (anhPath != null && !anhPath.isEmpty()) {
                        try {
                            imgChiTiet.setImageResource(Integer.parseInt(anhPath));
                        } catch (NumberFormatException e) {
                            imgChiTiet.setImageURI(Uri.parse(anhPath));
                        }
                    } else {
                        imgChiTiet.setImageResource(R.drawable.ic_image_placeholder);
                    }
                }
            }

            // Tải nguyên liệu (không đổi)
            StringBuilder nguyenLieuBuilder = new StringBuilder();
            try (Cursor cursorNguyenLieu = db.rawQuery("SELECT tenNguyenLieu FROM NguyenLieu WHERE idMonAn = ?", new String[]{String.valueOf(id)})) {
                while (cursorNguyenLieu.moveToNext()) {
                    nguyenLieuBuilder.append("• ").append(cursorNguyenLieu.getString(0)).append("\n");
                }
            }
            txtNguyenLieuChiTiet.setText(nguyenLieuBuilder.toString().trim());

            // Tải các bước làm (không đổi)
            StringBuilder buocLamBuilder = new StringBuilder();
            try (Cursor cursorBuocLam = db.rawQuery("SELECT moTaBuoc FROM BuocNau WHERE idMonAn = ? ORDER BY soThuTu ASC", new String[]{String.valueOf(id)})) {
                int step = 1;
                while (cursorBuocLam.moveToNext()) {
                    buocLamBuilder.append(step++).append(". ").append(cursorBuocLam.getString(0)).append("\n\n");
                }
            }
            txtBuocLamChiTiet.setText(buocLamBuilder.toString().trim());

        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn này không? Thao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteMonAn(currentMonAnId))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteMonAn(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("BuocNau", "idMonAn = ?", new String[]{String.valueOf(id)});
            db.delete("NguyenLieu", "idMonAn = ?", new String[]{String.valueOf(id)});
            db.delete("MonAn", "idMonAn = ?", new String[]{String.valueOf(id)});

            db.setTransactionSuccessful();
            Toast.makeText(this, "Đã xóa món ăn thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi xóa món ăn!", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            if (db.isOpen()) db.close();
        }
    }
}