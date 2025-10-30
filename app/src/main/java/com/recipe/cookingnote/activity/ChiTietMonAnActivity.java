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

/**
 * ChiTietMonAnActivity
 * ----------------------
 * Màn hình hiển thị chi tiết một món ăn trong ứng dụng Cooking Note.
 * - Hiển thị ảnh, tên, nguyên liệu, và các bước nấu.
 * - Có thể sửa, xóa, hoặc thêm / gỡ khỏi danh sách yêu thích.
 *
 * Dữ liệu được truy xuất từ SQLite qua DatabaseHelper.
 */
public class ChiTietMonAnActivity extends AppCompatActivity {

    // Khóa Intent để truyền ID món ăn giữa Activity
    public static final String EXTRA_MONAN_ID = "EXTRA_MONAN_ID";

    // Các View trong layout layout_chi_tiet.xml
    private ImageView imgChiTiet;
    private TextView txtTenMonChiTiet, txtNguyenLieuChiTiet, txtBuocLamChiTiet;
    private Button btnSua, btnXoa;
    private ImageButton btnBack;

    // Hỗ trợ làm việc với cơ sở dữ liệu SQLite
    private DatabaseHelper dbHelper;

    // Biến lưu ID món ăn hiện tại (được truyền từ màn hình trước)
    private int currentMonAnId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chi_tiet);

        // ===================== ÁNH XẠ VIEW TỪ LAYOUT =====================
        imgChiTiet = findViewById(R.id.imgChiTiet);
        txtTenMonChiTiet = findViewById(R.id.txtTenMonChiTiet);
        txtNguyenLieuChiTiet = findViewById(R.id.txtNguyenLieuChiTiet);
        txtBuocLamChiTiet = findViewById(R.id.txtBuocLamChiTiet);
        btnSua = findViewById(R.id.btnSua);
        btnXoa = findViewById(R.id.btnXoa);
        btnBack = findViewById(R.id.btnBack);
        ImageButton btnFavorite = findViewById(R.id.btnFavorite);

        // ===================== KHỞI TẠO DATABASE HELPER =====================
        dbHelper = new DatabaseHelper(this);

        // ===================== LẤY ID MÓN ĂN TRUYỀN QUA INTENT =====================
        currentMonAnId = getIntent().getIntExtra(EXTRA_MONAN_ID, -1);

        // Nếu không có ID → Thông báo lỗi và thoát Activity
        if (currentMonAnId == -1) {
            Toast.makeText(this, "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===================== KIỂM TRA TRẠNG THÁI YÊU THÍCH =====================
        // Mở DB ở chế độ đọc để kiểm tra xem món này có trong bảng YeuThich hay không
        SQLiteDatabase dbCheck = dbHelper.getReadableDatabase();
        Cursor cursorCheck = dbCheck.rawQuery(
                "SELECT idMonAn FROM YeuThich WHERE idMonAn = ?", // Câu SQL
                new String[]{String.valueOf(currentMonAnId)}
        );
        if (cursorCheck.moveToFirst()) {
            // Nếu có trong bảng YeuThich → hiện tim đầy
            btnFavorite.setImageResource(R.drawable.ic_heart);
        } else {
            // Nếu không có → hiện tim rỗng
            btnFavorite.setImageResource(R.drawable.ic_heart_hollow);
        }
        cursorCheck.close();
        dbCheck.close();

        // ===================== CÁC NÚT CHỨC NĂNG =====================

        // 🔙 Nút quay lại: đóng Activity hiện tại, trở về màn hình trước
        btnBack.setOnClickListener(v -> finish());

        // ✏️ Nút sửa: mở Activity ThemMonAnActivity với chế độ “sửa món ăn”
        btnSua.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietMonAnActivity.this, ThemMonAnActivity.class);
            intent.putExtra(ThemMonAnActivity.EXTRA_EDIT_MONAN_ID, currentMonAnId);
            startActivity(intent);
        });

        // 🗑️ Nút xóa: hiển thị hộp thoại xác nhận xóa
        btnXoa.setOnClickListener(v -> showDeleteConfirmDialog());

        // ❤️ Nút yêu thích: thêm / xóa món khỏi bảng YeuThich
        btnFavorite.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                // Kiểm tra xem món đã tồn tại trong bảng YeuThich chưa
                Cursor cursor = db.rawQuery(
                        "SELECT idMonAn FROM YeuThich WHERE idMonAn = ?",
                        new String[]{String.valueOf(currentMonAnId)}
                );

                if (cursor.moveToFirst()) {
                    // Nếu đã yêu thích → Xóa khỏi YeuThich
                    db.delete("YeuThich", "idMonAn = ?", new String[]{String.valueOf(currentMonAnId)});
                    btnFavorite.setImageResource(R.drawable.ic_heart_hollow);
                    Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu chưa → Thêm vào YeuThich
                    db.execSQL("INSERT INTO YeuThich (idMonAn) VALUES (?)",
                            new Object[]{currentMonAnId});
                    btnFavorite.setImageResource(R.drawable.ic_heart);
                    Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
            } finally {
                db.close(); // Đảm bảo đóng DB dù có lỗi hay không
            }
        });
    }

    // ===================== KHI MÀN HÌNH HIỂN THỊ TRỞ LẠI =====================
    @Override
    protected void onResume() {
        super.onResume();
        // Nếu có ID hợp lệ → load lại chi tiết món ăn
        if (currentMonAnId != -1) {
            loadMonAnDetails(currentMonAnId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Hàm tải chi tiết món ăn (tên, ảnh, nguyên liệu, các bước)
     * Dữ liệu lấy từ nhiều bảng: MonAn, NguyenLieu, BuocNau
     */
    private void loadMonAnDetails(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            // ================== LẤY THÔNG TIN CƠ BẢN CỦA MÓN ==================
            String queryMonAn = "SELECT tenMon, anhMon FROM MonAn WHERE idMonAn = ?";
            try (Cursor cursorMonAn = db.rawQuery(queryMonAn, new String[]{String.valueOf(id)})) {
                if (cursorMonAn.moveToFirst()) {
                    // Cập nhật tên món
                    txtTenMonChiTiet.setText(cursorMonAn.getString(0));

                    // Lấy đường dẫn hoặc ID ảnh
                    String anhPath = cursorMonAn.getString(1);
                    if (anhPath != null && !anhPath.isEmpty()) {
                        try {
                            // Nếu ảnh lưu dưới dạng ID (int trong resource)
                            imgChiTiet.setImageResource(Integer.parseInt(anhPath));
                        } catch (NumberFormatException e) {
                            // Nếu là đường dẫn (URI)
                            imgChiTiet.setImageURI(Uri.parse(anhPath));
                        }
                    } else {
                        // Nếu không có ảnh → dùng ảnh mặc định
                        imgChiTiet.setImageResource(R.drawable.ic_image_placeholder);
                    }
                }
            }

            // ================== LẤY DANH SÁCH NGUYÊN LIỆU ==================
            StringBuilder nguyenLieuBuilder = new StringBuilder();
            try (Cursor cursorNguyenLieu = db.rawQuery(
                    "SELECT tenNguyenLieu FROM NguyenLieu WHERE idMonAn = ?",
                    new String[]{String.valueOf(id)})) {
                while (cursorNguyenLieu.moveToNext()) {
                    nguyenLieuBuilder.append("• ")
                            .append(cursorNguyenLieu.getString(0))
                            .append("\n");
                }
            }
            txtNguyenLieuChiTiet.setText(nguyenLieuBuilder.toString().trim());

            // ================== LẤY CÁC BƯỚC NẤU ==================
            StringBuilder buocLamBuilder = new StringBuilder();
            try (Cursor cursorBuocLam = db.rawQuery(
                    "SELECT moTaBuoc FROM BuocNau WHERE idMonAn = ? ORDER BY soThuTu ASC",
                    new String[]{String.valueOf(id)})) {
                int step = 1;
                while (cursorBuocLam.moveToNext()) {
                    buocLamBuilder.append(step++)
                            .append(". ")
                            .append(cursorBuocLam.getString(0))
                            .append("\n\n");
                }
            }
            txtBuocLamChiTiet.setText(buocLamBuilder.toString().trim());

        } finally {
            // Đảm bảo đóng DB khi xong
            if (db != null && db.isOpen()) db.close();
        }
    }

    // ================== HỘP THOẠI XÁC NHẬN XÓA ==================
    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn này không? Thao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteMonAn(currentMonAnId))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // ================== HÀM XÓA MÓN ĂN VÀ CÁC LIÊN KẾT ==================
    private void deleteMonAn(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu transaction để đảm bảo tính toàn vẹn dữ liệu
        try {
            // Xóa các bước nấu và nguyên liệu liên quan trước
            db.delete("BuocNau", "idMonAn = ?", new String[]{String.valueOf(id)});
            db.delete("NguyenLieu", "idMonAn = ?", new String[]{String.valueOf(id)});
            db.delete("MonAn", "idMonAn = ?", new String[]{String.valueOf(id)});

            // ✅ Đánh dấu transaction thành công
            db.setTransactionSuccessful();

            Toast.makeText(this, "Đã xóa món ăn thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi xóa món ăn!", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction(); // Kết thúc transaction
            if (db.isOpen()) db.close();
        }
    }
}
