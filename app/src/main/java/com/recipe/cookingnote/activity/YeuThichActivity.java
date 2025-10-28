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

public class YeuThichActivity extends AppCompatActivity {
    RecyclerView recyclerYeuThich;
    MonAnAdapter adapter;
    ArrayList<MonAn> listMonAn;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mon_yeu_thich);

        recyclerYeuThich = findViewById(R.id.recyclerYeuThich);
        recyclerYeuThich.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        listMonAn = getMonAnYeuThich();

        adapter = new MonAnAdapter(listMonAn, this);
        recyclerYeuThich.setAdapter(adapter);
        // 🔙 Nút quay lại
        ImageButton btnBackYeuThich = findViewById(R.id.btnBack);
        btnBackYeuThich.setOnClickListener(v -> finish());
    }

    private ArrayList<MonAn> getMonAnYeuThich() {
        ArrayList<MonAn> ds = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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
