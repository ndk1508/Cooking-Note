package com.recipe.cookingnote.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.recipe.cookingnote.Adapter.ChonAnhAdapter;
import com.recipe.cookingnote.R;
import com.recipe.cookingnote.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemMonAnActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_MONAN_ID = "EXTRA_EDIT_MONAN_ID";

    private EditText edtTenMon, edtNguyenLieu, edtBuocLam;
    private Spinner spinnerDanhMuc;
    private Button btnLuu, btnChonAnh;
    private ImageView imgMonAn;
    private TextView tvTieuDe;

    private Integer selectedImageResourceId = null;
    private Uri selectedImageUri = null;

    private DatabaseHelper dbHelper;
    private ArrayList<String> listDanhMuc = new ArrayList<>();
    private ArrayList<Integer> listIdDanhMuc = new ArrayList<>();

    private int editingMonAnId = -1;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {

                    Uri imageUri = result.getData().getData();

                    try {
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Không thể lưu quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
                    }

                    selectedImageUri = imageUri;
                    selectedImageResourceId = null;
                    imgMonAn.setImageURI(selectedImageUri);
                }
            });
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openGallery();
                else Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_them_mon);

        edtTenMon = findViewById(R.id.edtTenMon);
        edtNguyenLieu = findViewById(R.id.edtNguyenLieu);
        edtBuocLam = findViewById(R.id.edtBuocLam);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        btnLuu = findViewById(R.id.btnLuu);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        imgMonAn = findViewById(R.id.imgMonAn);
        tvTieuDe = findViewById(R.id.tvTieuDe);

        dbHelper = new DatabaseHelper(this);
        loadDanhMuc();

        editingMonAnId = getIntent().getIntExtra(EXTRA_EDIT_MONAN_ID, -1);
        if (editingMonAnId != -1) {
            setupEditMode();
            loadDataForEditing(editingMonAnId);
        }

        btnChonAnh.setOnClickListener(v -> showImageSourceDialog());
        btnLuu.setOnClickListener(v -> saveData());
    }

    private void setupEditMode() {
        tvTieuDe.setText("Chỉnh sửa món ăn");
        btnLuu.setText("Cập nhật");
    }

    private void loadDataForEditing(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            try (Cursor cursorMonAn = db.rawQuery("SELECT * FROM MonAn WHERE idMonAn = ?", new String[]{String.valueOf(id)})) {
                if (cursorMonAn.moveToFirst()) {
                    edtTenMon.setText(cursorMonAn.getString(cursorMonAn.getColumnIndexOrThrow("tenMon")));
                    String anhPath = cursorMonAn.getString(cursorMonAn.getColumnIndexOrThrow("anhMon"));
                    if (anhPath != null && !anhPath.isEmpty()) {
                        try {
                            selectedImageResourceId = Integer.parseInt(anhPath);
                            imgMonAn.setImageResource(selectedImageResourceId);
                        } catch (NumberFormatException e) {
                            selectedImageUri = Uri.parse(anhPath);
                            imgMonAn.setImageURI(selectedImageUri);
                        }
                    }
                    int idDanhMuc = cursorMonAn.getInt(cursorMonAn.getColumnIndexOrThrow("idDanhMuc"));
                    int spinnerPosition = listIdDanhMuc.indexOf(idDanhMuc);
                    if (spinnerPosition >= 0) spinnerDanhMuc.setSelection(spinnerPosition);
                }
            }
            try (Cursor cursorNL = db.rawQuery("SELECT tenNguyenLieu FROM NguyenLieu WHERE idMonAn = ?", new String[]{String.valueOf(id)})) {
                List<String> nguyenLieuList = new ArrayList<>();
                while (cursorNL.moveToNext()) nguyenLieuList.add(cursorNL.getString(0));
                edtNguyenLieu.setText(TextUtils.join("\n", nguyenLieuList));
            }
            try (Cursor cursorBL = db.rawQuery("SELECT moTaBuoc FROM BuocNau WHERE idMonAn = ? ORDER BY soThuTu ASC", new String[]{String.valueOf(id)})) {
                List<String> buocLamList = new ArrayList<>();
                while (cursorBL.moveToNext()) buocLamList.add(cursorBL.getString(0));
                edtBuocLam.setText(TextUtils.join("\n", buocLamList));
            }
        } finally {
            if (db.isOpen()) db.close();
        }
    }

    private void saveData() {
        String tenMon = edtTenMon.getText().toString().trim();
        String nguyenLieu = edtNguyenLieu.getText().toString().trim();
        String buocLam = edtBuocLam.getText().toString().trim();
        int indexDanhMuc = spinnerDanhMuc.getSelectedItemPosition();

        if (tenMon.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên món ăn!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues valuesMon = new ContentValues();
            valuesMon.put("tenMon", tenMon);
            valuesMon.put("idDanhMuc", listIdDanhMuc.get(indexDanhMuc));
            String anhPathToSave = "";
            if (selectedImageResourceId != null) anhPathToSave = String.valueOf(selectedImageResourceId);
            else if (selectedImageUri != null) anhPathToSave = selectedImageUri.toString();
            valuesMon.put("anhMon", anhPathToSave);
            long monAnIdForDetails;
            if (editingMonAnId != -1) {
                db.update("MonAn", valuesMon, "idMonAn = ?", new String[]{String.valueOf(editingMonAnId)});
                monAnIdForDetails = editingMonAnId;
                Toast.makeText(this, "Đã cập nhật món ăn!", Toast.LENGTH_SHORT).show();
            } else {
                monAnIdForDetails = db.insert("MonAn", null, valuesMon);
                if (monAnIdForDetails == -1) throw new Exception("Lỗi khi thêm món ăn!");
                Toast.makeText(this, "Đã lưu món ăn thành công!", Toast.LENGTH_SHORT).show();
            }
            db.delete("NguyenLieu", "idMonAn = ?", new String[]{String.valueOf(monAnIdForDetails)});
            db.delete("BuocNau", "idMonAn = ?", new String[]{String.valueOf(monAnIdForDetails)});
            insertDetails(db, monAnIdForDetails, nguyenLieu, buocLam);
            db.setTransactionSuccessful();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private void insertDetails(SQLiteDatabase db, long monAnId, String nguyenLieu, String buocLam) {
        String[] dsNguyenLieu = nguyenLieu.split("\\s*\\n+\\s*");
        for (String item : dsNguyenLieu) {
            if (!item.trim().isEmpty()) {
                ContentValues values = new ContentValues();
                values.put("idMonAn", monAnId);
                values.put("tenNguyenLieu", item.trim());
                db.insert("NguyenLieu", null, values);
            }
        }
        String[] dsBuoc = buocLam.split("\\s*\\n+\\s*");
        int stt = 1;
        for (String item : dsBuoc) {
            if (!item.trim().isEmpty()) {
                ContentValues values = new ContentValues();
                values.put("idMonAn", monAnId);
                values.put("soThuTu", stt++);
                values.put("moTaBuoc", item.trim());
                db.insert("BuocNau", null, values);
            }
        }
    }

    private void showImageSourceDialog() {
        final CharSequence[] options = { "Chọn từ ảnh gợi ý", "Chọn từ thư viện ảnh", "Hủy" };
        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh cho món ăn")
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Chọn từ ảnh gợi ý")) showChonAnhDialog();
                    else if (options[item].equals("Chọn từ thư viện ảnh")) handleChonAnhTuThuVien();
                    else if (options[item].equals("Hủy")) dialog.dismiss();
                }).show();
    }

    private void showChonAnhDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_chon_anh, null);
        builder.setView(dialogView);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewAnh);
        List<Integer> imageList = Arrays.asList(
                R.drawable.bun_bo, R.drawable.banh_mi, R.drawable.com_ga, R.drawable.pho, R.drawable.kem
        );
        final AlertDialog dialog = builder.create();
        ChonAnhAdapter adapter = new ChonAnhAdapter(this, imageList, imageId -> {
            selectedImageResourceId = imageId;
            selectedImageUri = null;
            imgMonAn.setImageResource(imageId);
            dialog.dismiss();
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    private void handleChonAnhTuThuVien() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) openGallery();
        else requestPermissionLauncher.launch(permission);
    }

    private void openGallery() {
        // ⭐ THAY ĐỔI NHỎ NHƯNG QUAN TRỌNG ⭐
        // Sử dụng ACTION_OPEN_DOCUMENT thay vì ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Chỉ hiển thị các loại ảnh
        imagePickerLauncher.launch(intent);
    }

    private void loadDanhMuc() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT idDanhMuc, tenDanhMuc FROM DanhMuc", null)) {
            listDanhMuc.clear();
            listIdDanhMuc.clear();
            while (cursor.moveToNext()) {
                listIdDanhMuc.add(cursor.getInt(0));
                listDanhMuc.add(cursor.getString(1));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, listDanhMuc);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDanhMuc.setAdapter(adapter);
        } finally {
            if(db.isOpen()) db.close();
        }
    }
}
