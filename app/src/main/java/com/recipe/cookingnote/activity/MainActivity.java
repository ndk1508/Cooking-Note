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
import android.widget.LinearLayout;

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

    // Khai báo các view và biến cần thiết
    private RecyclerView recyclerView;
    private EditText edtSearch;
    private ImageButton btnClear;
    private FloatingActionButton fabAdd;
    private Button btnBreakfast, btnLunch, btnDinner, btnDessert, btnAll;

    private DatabaseHelper dbHelper;
    private ArrayList<MonAn> monAnList;
    private MonAnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Ánh xạ các thành phần giao diện (View) từ layout XML
        recyclerView = findViewById(R.id.recyclerView);
        edtSearch = findViewById(R.id.edtSearch);
        btnClear = findViewById(R.id.btnClear);
        fabAdd = findViewById(R.id.fabAdd);
        btnAll = findViewById(R.id.btnAll);
        btnBreakfast = findViewById(R.id.btnBreakfast);
        btnLunch = findViewById(R.id.btnLunch);
        btnDinner = findViewById(R.id.btnDinner);
        btnDessert = findViewById(R.id.btnDessert);
        LinearLayout btnFavorites = findViewById(R.id.btnFavorites);

        // 🔹 Khởi tạo database helper và danh sách món ăn
        dbHelper = new DatabaseHelper(this);
        monAnList = new ArrayList<>();

        // 🔹 Thiết lập RecyclerView hiển thị theo dạng danh sách dọc
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔹 Gắn adapter để điều khiển hiển thị dữ liệu lên RecyclerView
        adapter = new MonAnAdapter(monAnList, this);
        recyclerView.setAdapter(adapter);

        // ❌ Không cần tải dữ liệu ngay khi khởi tạo — vì sẽ tự động tải trong onResume()
        // loadDataFromDatabase(null, null);

        // 🔹 Lắng nghe sự thay đổi trong ô tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            // ✅ Khi người dùng nhập vào ô tìm kiếm → lọc dữ liệu theo từ khóa
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadDataFromDatabase(s.toString(), null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 🔹 Xử lý sự kiện nhấn nút “Tất cả”
        btnAll.setOnClickListener(v -> loadDataFromDatabase(null, null));

        // 🔹 Nút “X” để xóa nội dung tìm kiếm
        btnClear.setOnClickListener(v -> edtSearch.setText(""));

        // 🔹 Các nút lọc theo danh mục
        btnBreakfast.setOnClickListener(v -> loadDataFromDatabase(null, "Ăn sáng"));
        btnLunch.setOnClickListener(v -> loadDataFromDatabase(null, "Ăn trưa"));
        btnDinner.setOnClickListener(v -> loadDataFromDatabase(null, "Ăn tối"));
        btnDessert.setOnClickListener(v -> loadDataFromDatabase(null, "Tráng miệng"));

        // 🔹 Nút mở trang “Danh sách yêu thích”
        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, YeuThichActivity.class);
            startActivity(intent);
        });

        // 🔹 Nút “+” để thêm món ăn mới
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThemMonAnActivity.class);
            startActivity(intent);
        });
    }

    // ⭐ Phương thức onResume() — chạy mỗi khi màn hình chính hiển thị lại
    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Luôn tải lại dữ liệu mới nhất (sau khi thêm, sửa, xóa món ăn)
        loadDataFromDatabase(null, null);
    }

    /**
     * 📦 Phương thức tải dữ liệu từ SQLite và hiển thị lên RecyclerView.
     * @param keyword  Từ khóa tìm kiếm (có thể null)
     * @param category Tên danh mục cần lọc (có thể null)
     */
    private void loadDataFromDatabase(String keyword, String category) {
        monAnList.clear(); // Xóa dữ liệu cũ trước khi nạp mới
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // 🔹 Câu truy vấn cơ bản kết hợp bảng MonAn và DanhMuc
            String query = "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon, DanhMuc.tenDanhMuc " +
                    "FROM MonAn LEFT JOIN DanhMuc ON MonAn.idDanhMuc = DanhMuc.idDanhMuc";

            ArrayList<String> selectionArgs = new ArrayList<>();
            String whereClause = "";

            // 🔹 Nếu có từ khóa tìm kiếm → thêm điều kiện LIKE
            if (keyword != null && !keyword.isEmpty()) {
                whereClause += " MonAn.tenMon LIKE ?";
                selectionArgs.add("%" + keyword + "%");
            }

            // 🔹 Nếu có danh mục lọc → thêm điều kiện AND
            if (category != null && !category.isEmpty()) {
                if (!whereClause.isEmpty()) {
                    whereClause += " AND";
                }
                whereClause += " DanhMuc.tenDanhMuc = ?";
                selectionArgs.add(category);
            }

            // 🔹 Nếu có điều kiện → nối WHERE vào câu truy vấn
            if (!whereClause.isEmpty()) {
                query += " WHERE" + whereClause;
            }

            // 🔹 Thực thi truy vấn
            cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));

            // 🔹 Duyệt kết quả và thêm vào danh sách
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String ten = cursor.getString(1);
                    String moTa = cursor.getString(2);
                    String anh = cursor.getString(3);
                    String danhMuc = cursor.getString(4);

                    // ✅ Thêm món ăn vào danh sách
                    monAnList.add(new MonAn(id, ten, moTa, anh, danhMuc));
                } while (cursor.moveToNext());
            }
        } finally {
            // 🔹 Đảm bảo đóng Cursor & Database để tránh rò rỉ bộ nhớ
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        // 🔹 Cập nhật lại giao diện danh sách
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 🔹 Đóng kết nối DatabaseHelper khi Activity bị hủy
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
