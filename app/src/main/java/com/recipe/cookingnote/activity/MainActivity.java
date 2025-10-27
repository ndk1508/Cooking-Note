package com.recipe.cookingnote.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.recipe.cookingnote.R;
import com.recipe.cookingnote.Adapter.MonAnAdapter;
import com.recipe.cookingnote.database.DatabaseHelper;
import com.recipe.cookingnote.Model.MonAn;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText edtSearch;
    private ImageButton btnClear;
    private FloatingActionButton fabAdd;
    private Button btnBreakfast, btnLunch, btnDinner;

    private DatabaseHelper dbHelper;
    private ArrayList<MonAn> monAnList;
    private MonAnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view
        recyclerView = findViewById(R.id.recyclerView);
        edtSearch = findViewById(R.id.edtSearch);
        btnClear = findViewById(R.id.btnClear);
        fabAdd = findViewById(R.id.fabAdd);
        btnBreakfast = findViewById(R.id.btnBreakfast);
        btnLunch = findViewById(R.id.btnLunch);
        btnDinner = findViewById(R.id.btnDinner);

        // Khởi tạo database helper và list
        dbHelper = new DatabaseHelper(this);
        monAnList = new ArrayList<>();

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MonAnAdapter(monAnList, this);
        recyclerView.setAdapter(adapter);

        // <-- THAY ĐỔI 1: Không cần gọi hàm tải dữ liệu ở đây nữa
        // loadDataFromDatabase(null, null);

        // Thêm listener cho ô tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadDataFromDatabase(s.toString(), null);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Thêm listener cho các nút
        btnClear.setOnClickListener(v -> edtSearch.setText(""));
        btnBreakfast.setOnClickListener(v -> loadDataFromDatabase(null, "Ăn sáng"));
        btnLunch.setOnClickListener(v -> loadDataFromDatabase(null, "Ăn trưa"));
        btnDinner.setOnClickListener(v -> loadDataFromDatabase(null, "Ăn tối"));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThemMonAnActivity.class);
            startActivity(intent);
        });
    }

    // ⭐ THAY ĐỔI 2: THÊM PHƯƠNG THỨC onResume() ⭐
    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi màn hình này quay lại và hiển thị cho người dùng.
        // Điều này đảm bảo danh sách luôn được cập nhật sau khi thêm, sửa, xóa.
        loadDataFromDatabase(null, null);
    }

    /**
     * Phương thức chung để tải dữ liệu từ cơ sở dữ liệu dựa trên từ khóa hoặc danh mục.
     * @param keyword Từ khóa tìm kiếm (có thể là null).
     * @param category Tên danh mục để lọc (có thể là null).
     */
    private void loadDataFromDatabase(String keyword, String category) {
        monAnList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Xây dựng câu truy vấn cơ bản
            String query = "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon, DanhMuc.tenDanhMuc " +
                    "FROM MonAn LEFT JOIN DanhMuc ON MonAn.idDanhMuc = DanhMuc.idDanhMuc";

            ArrayList<String> selectionArgs = new ArrayList<>();
            String whereClause = ""; // Sử dụng tên biến rõ ràng hơn

            // Thêm điều kiện tìm kiếm theo từ khóa
            if (keyword != null && !keyword.isEmpty()) {
                whereClause += " MonAn.tenMon LIKE ?";
                selectionArgs.add("%" + keyword + "%");
            }

            // Thêm điều kiện lọc theo danh mục
            if (category != null && !category.isEmpty()) {
                if (!whereClause.isEmpty()) {
                    whereClause += " AND";
                }
                whereClause += " DanhMuc.tenDanhMuc = ?";
                selectionArgs.add(category);
            }

            if (!whereClause.isEmpty()) {
                query += " WHERE" + whereClause;
            }

            // Thực thi truy vấn
            cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String ten = cursor.getString(1);
                    String moTa = cursor.getString(2);
                    String anh = cursor.getString(3);
                    String danhMuc = cursor.getString(4);
                    monAnList.add(new MonAn(id, ten, moTa, anh, danhMuc));
                } while (cursor.moveToNext());
            }
        } finally {
            // Luôn đóng cursor và database để tránh rò rỉ tài nguyên
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        // Cập nhật lại adapter
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng database helper khi activity bị hủy
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}