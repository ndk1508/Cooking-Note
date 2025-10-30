package com.recipe.cookingnote.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.recipe.cookingnote.Adapter.MonAnAdapter;
import com.recipe.cookingnote.Model.MonAn;
import com.recipe.cookingnote.R;
import com.recipe.cookingnote.database.DatabaseHelper;
import java.util.ArrayList;

/**
 * YeuThichActivity
 * Activity hiển thị danh sách món ăn mà người dùng đã đánh dấu "yêu thích".
 *
 * Giao diện: layout_mon_yeu_thich.xml (chứa RecyclerView có id recyclerYeuThich và ImageButton btnBack)
 *
 * Lưu ý chính:
 * - Dữ liệu lấy từ SQLite thông qua DatabaseHelper.
 * - Việc truy vấn DB hiện chạy trên main thread — nên cân nhắc chạy ở background (AsyncTask / Executor / ViewModel).
 */
public class YeuThichActivity extends AppCompatActivity {
    // RecyclerView dùng để hiển thị danh sách món ăn yêu thích
    RecyclerView recyclerYeuThich;

    // Adapter cho RecyclerView (tùy thuộc vào cách bạn implement MonAnAdapter)
    MonAnAdapter adapter;

    // Danh sách các MonAn (model) được hiển thị
    ArrayList<MonAn> listMonAn;

    // Helper quản lý kết nối đến SQLite (tạo/upgrade DB)
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập layout cho Activity này
        setContentView(R.layout.layout_mon_yeu_thich);

        // ------- Khởi tạo RecyclerView -------
        // Lấy reference tới RecyclerView trong layout
        recyclerYeuThich = findViewById(R.id.recyclerYeuThich);

        // Sử dụng LinearLayoutManager để hiển thị danh sách theo hàng dọc (vertical)
        // Bạn có thể đổi sang GridLayoutManager nếu muốn lưới
        recyclerYeuThich.setLayoutManager(new LinearLayoutManager(this));

        // ------- Khởi tạo DatabaseHelper -------
        // DatabaseHelper chịu trách nhiệm mở/create DB nếu chưa có
        dbHelper = new DatabaseHelper(this);

        // Lấy dữ liệu món ăn yêu thích từ DB (hàm private phía dưới)
        // Chú ý: hiện tại getMonAnYeuThich() chạy truy vấn trên main thread.
        listMonAn = getMonAnYeuThich();

        // ------- Khởi tạo Adapter và gắn vào RecyclerView -------
        // MonAnAdapter cần implement ViewHolder, binding dữ liệu MonAn vào item layout
        adapter = new MonAnAdapter(listMonAn, this);
        recyclerYeuThich.setAdapter(adapter);

        // ------- Nút quay lại (Back) -------
        // Tìm ImageButton có id btnBack trong layout và set sự kiện click để finish Activity
        // (giải phóng Activity hiện tại và quay về màn trước)
        ImageButton btnBackYeuThich = findViewById(R.id.btnBack);
        btnBackYeuThich.setOnClickListener(v -> finish());
    }

    /**
     * Lấy danh sách món ăn đã được đánh dấu yêu thích từ database.
     *
     * Cách hoạt động:
     * - Mở readable database từ dbHelper
     * - Thực hiện truy vấn JOIN giữa bảng MonAn và bảng YeuThich để lấy các món đã thích
     * - Duyệt Cursor, ánh xạ cột sang model MonAn, thêm vào ArrayList rồi trả về
     *
     * Lưu ý quan trọng:
     * - Hàm này đóng cursor và db sau khi dùng (điều tốt).
     * - Tuy nhiên việc truy vấn DB trên main thread có thể gây lag nếu dữ liệu lớn.
     * - Khuyến nghị: chạy truy vấn trong background và cập nhật adapter trên UI thread.
     */
    private ArrayList<MonAn> getMonAnYeuThich() {
        ArrayList<MonAn> ds = new ArrayList<>();

        // Mở DB ở chế độ chỉ đọc (đủ cho SELECT)
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // SQL: chọn các cột cần thiết từ MonAn bằng JOIN với YeuThich
        // Giả sử: YeuThich lưu idMonAn ứng với MonAn.idMonAn
        String sql = "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon " +
                "FROM MonAn INNER JOIN YeuThich ON MonAn.idMonAn = YeuThich.idMonAn";

        // rawQuery trả về một Cursor
        Cursor cursor = db.rawQuery(sql, null);

        // Kiểm tra cursor có dữ liệu không trước khi moveToFirst
        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu theo index cột:
                // 0 -> idMonAn (int)
                // 1 -> tenMon (String)
                // 2 -> moTa (String)
                // 3 -> anhMon (String) — có thể là đường dẫn, URI, hoặc tên resource
                int id = cursor.getInt(0);
                String ten = cursor.getString(1);
                String moTa = cursor.getString(2);
                String anh = cursor.getString(3);

                // Tạo đối tượng MonAn và thêm vào danh sách
                // Chú ý: constructor MonAn mà bạn dùng ở đây có 5 tham số; phần danhMuc tạm để rỗng
                ds.add(new MonAn(id, ten, moTa, anh, ""));
            } while (cursor.moveToNext());
        }

        // Đóng cursor để tránh leak
        cursor.close();

        // Đóng database (nếu bạn dùng dbHelper nhiều nơi, cân nhắc chỉ đóng khi không cần nữa)
        db.close();

        return ds;
    }
}
