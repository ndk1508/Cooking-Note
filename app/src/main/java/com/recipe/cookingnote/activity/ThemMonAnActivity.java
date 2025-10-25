package com.recipe.cookingnote.activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.recipe.cookingnote.R;
import com.recipe.cookingnote.database.DatabaseHelper;
import java.util.ArrayList;

public class ThemMonAnActivity extends AppCompatActivity {

    private EditText edtTenMon, edtNguyenLieu, edtBuocLam;
    private Spinner spinnerDanhMuc;
    private Button btnLuu, btnChonAnh;
    private ImageView imgMonAn;
    private Uri selectedImageUri = null;

    private DatabaseHelper dbHelper;
    private ArrayList<String> listDanhMuc = new ArrayList<>();
    private ArrayList<Integer> listIdDanhMuc = new ArrayList<>();

    // 🔹 ActivityResultLauncher cho phép chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgMonAn.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_them_mon);

        // Ánh xạ view
        edtTenMon = findViewById(R.id.edtTenMon);
        edtNguyenLieu = findViewById(R.id.edtNguyenLieu);
        edtBuocLam = findViewById(R.id.edtBuocLam);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        btnLuu = findViewById(R.id.btnLuu);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        imgMonAn = findViewById(R.id.imgMonAn);

        dbHelper = new DatabaseHelper(this);

        // 🔹 Load danh mục vào Spinner
        loadDanhMuc();

        // 🔹 Chọn ảnh từ thư viện
        btnChonAnh.setOnClickListener(v -> chonAnh());

        // 🔹 Xử lý lưu món ăn
        btnLuu.setOnClickListener(v -> luuMonAn());
    }

    // ===================== HIỂN THỊ DANH MỤC =====================
    private void loadDanhMuc() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT idDanhMuc, tenDanhMuc FROM DanhMuc", null);

        listDanhMuc.clear();
        listIdDanhMuc.clear();

        while (cursor.moveToNext()) {
            listIdDanhMuc.add(cursor.getInt(0));
            listDanhMuc.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listDanhMuc);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDanhMuc.setAdapter(adapter);
    }

    // ===================== CHỌN ẢNH =====================
    private void chonAnh() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // ===================== LƯU MÓN ĂN =====================
    private void luuMonAn() {
        String tenMon = edtTenMon.getText().toString().trim();
        String nguyenLieu = edtNguyenLieu.getText().toString().trim();
        String buocLam = edtBuocLam.getText().toString().trim();
        int indexDanhMuc = spinnerDanhMuc.getSelectedItemPosition();

        if (tenMon.isEmpty() || nguyenLieu.isEmpty() || buocLam.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int idDanhMuc = listIdDanhMuc.get(indexDanhMuc);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 🔹 1. Thêm món ăn vào bảng MonAn
        ContentValues valuesMon = new ContentValues();
        valuesMon.put("tenMon", tenMon);
        valuesMon.put("moTa", "");
        valuesMon.put("idDanhMuc", idDanhMuc);

        if (selectedImageUri != null)
            valuesMon.put("anhMon", selectedImageUri.toString()); // Lưu URI ảnh vào SQLite
        else
            valuesMon.put("anhMon", "");

        long idMonAn = db.insert("MonAn", null, valuesMon);
        if (idMonAn == -1) {
            Toast.makeText(this, "Lỗi khi thêm món ăn!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 2. Thêm nguyên liệu (mỗi dòng là 1 nguyên liệu)
        String[] dsNguyenLieu = nguyenLieu.split("\n");
        for (String item : dsNguyenLieu) {
            if (!item.trim().isEmpty()) {
                ContentValues valuesNguyenLieu = new ContentValues();
                valuesNguyenLieu.put("idMonAn", idMonAn);
                valuesNguyenLieu.put("tenNguyenLieu", item.trim());
                db.insert("NguyenLieu", null, valuesNguyenLieu);
            }
        }

        // 🔹 3. Thêm các bước nấu
        String[] dsBuoc = buocLam.split("\n");
        int stt = 1;
        for (String item : dsBuoc) {
            if (!item.trim().isEmpty()) {
                ContentValues valuesBuoc = new ContentValues();
                valuesBuoc.put("idMonAn", idMonAn);
                valuesBuoc.put("soThuTu", stt++);
                valuesBuoc.put("moTaBuoc", item.trim());
                db.insert("BuocNau", null, valuesBuoc);
            }
        }

        Toast.makeText(this, "Đã lưu món ăn thành công!", Toast.LENGTH_SHORT).show();

        // 🔹 Reset form
        edtTenMon.setText("");
        edtNguyenLieu.setText("");
        edtBuocLam.setText("");
        imgMonAn.setImageResource(R.drawable.ic_image_placeholder);
        spinnerDanhMuc.setSelection(0);
        selectedImageUri = null;
    }
}