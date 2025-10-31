package com.recipe.cookingnote.activity;

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


public class YeuThichActivity extends AppCompatActivity {

    // RecyclerView hiển thị danh sách món ăn yêu thích
    private RecyclerView recyclerYeuThich;

    // Adapter cho RecyclerView
    private MonAnAdapter adapter;

    // Danh sách các món ăn yêu thích
    private ArrayList<MonAn> listMonAn;

    // Đối tượng quản lý database
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mon_yeu_thich);

        // --- Ánh xạ view ---
        recyclerYeuThich = findViewById(R.id.recyclerYeuThich);
        ImageButton btnBackYeuThich = findViewById(R.id.btnBack);

        // --- Cấu hình RecyclerView ---
        recyclerYeuThich.setLayoutManager(new LinearLayoutManager(this));

        // --- Khởi tạo database helper ---
        dbHelper = new DatabaseHelper(this);

        // --- Lấy danh sách yêu thích lần đầu ---
        listMonAn = getMonAnYeuThich();

        // --- Gắn adapter ---
        adapter = new MonAnAdapter(listMonAn, this);
        recyclerYeuThich.setAdapter(adapter);

        // --- Sự kiện quay lại ---
        btnBackYeuThich.setOnClickListener(v -> finish());
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Làm mới danh sách
        listMonAn.clear();
        listMonAn.addAll(getMonAnYeuThich());

        // Cập nhật giao diện
        adapter.notifyDataSetChanged();
    }
    private ArrayList<MonAn> getMonAnYeuThich() {
        ArrayList<MonAn> ds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Truy vấn JOIN giữa MonAn và YeuThich
        String sql = "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon " +
                "FROM MonAn INNER JOIN YeuThich ON MonAn.idMonAn = YeuThich.idMonAn";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String ten = cursor.getString(1);
                String moTa = cursor.getString(2);
                String anh = cursor.getString(3);

                ds.add(new MonAn(id, ten, moTa, anh, ""));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ds;
    }
}
