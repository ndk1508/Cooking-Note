package com.recipe.cookingnote.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import com.recipe.cookingnote.R;
import com.recipe.cookingnote.database.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SuaMonAnActivity extends AppCompatActivity {

    private EditText edtTenMon, edtNguyenLieu, edtBuocLam;
    private Spinner spinnerDanhMuc;
    private ImageView imgMonAn;
    private Button btnChonAnh, btnCapNhat;
    private DatabaseHelper dbHelper;

    private int monAnId; // ID món ăn cần sửa
    private byte[] anhByte; // Lưu ảnh mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sua_mon);

        // Ánh xạ
        edtTenMon = findViewById(R.id.edtTenMon);
        edtNguyenLieu = findViewById(R.id.edtNguyenLieu);
        edtBuocLam = findViewById(R.id.edtBuocLam);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        imgMonAn = findViewById(R.id.imgMonAn);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnCapNhat = findViewById(R.id.btnCapNhat);

        dbHelper = new DatabaseHelper(this);

        // 🔹 Lấy ID món ăn từ Intent
        monAnId = getIntent().getIntExtra("MON_ID", -1);
        if (monAnId != -1) {
            //loadThongTinMonAn(monAnId);
        }

        // 🔹 Chọn ảnh
        btnChonAnh.setOnClickListener(v -> chonAnhTuThuVien());

        // 🔹 Cập nhật món ăn
        btnCapNhat.setOnClickListener(v -> capNhatMonAn());
    }

    // -----------------------------
    //  📸 Hàm chọn ảnh
    // -----------------------------
    private void chonAnhTuThuVien() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imgMonAn.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                anhByte = stream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // -----------------------------
    //  📦 Load dữ liệu món ăn cũ
    // -----------------------------
//    private void loadThongTinMonAn(int id) {
//            MonAn monAn = dbHelper.getMonAnById(id); // bạn cần có hàm này trong DatabaseHelper
//        if (monAn != null) {
//            edtTenMon.setText(monAn.getTenMon());
//            edtNguyenLieu.setText(monAn.getNguyenLieu());
//            edtBuocLam.setText(monAn.getBuocLam());
//            // chọn danh mục trong Spinner nếu có
//            if (monAn.getAnh() != null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(monAn.getAnh(), 0, monAn.getAnh().length);
//                imgMonAn.setImageBitmap(bitmap);
//                anhByte = monAn.getAnh();
//            }
//        }
//    }

    // -----------------------------
    //  💾 Cập nhật món ăn
    // -----------------------------
    private void capNhatMonAn() {
        String ten = edtTenMon.getText().toString().trim();
        String nguyenLieu = edtNguyenLieu.getText().toString().trim();
        String buocLam = edtBuocLam.getText().toString().trim();
        String danhMuc = spinnerDanhMuc.getSelectedItem().toString();

        boolean success = dbHelper.updateMonAn(monAnId, ten, danhMuc, nguyenLieu, buocLam, anhByte);

        if (success) {
            Toast.makeText(this, "✅ Cập nhật món ăn thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "❌ Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}