package com.recipe.cookingnote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.recipe.cookingnote.Model.MonAn;
import com.recipe.cookingnote.Adapter.MonAnAdapter;
import com.recipe.cookingnote.ThemMonAnActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.recipe.cookingnote.database.DatabaseHelper;

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

        recyclerView = findViewById(R.id.recyclerView);
        edtSearch = findViewById(R.id.edtSearch);
        btnClear = findViewById(R.id.btnClear);
        fabAdd = findViewById(R.id.fabAdd);
        btnBreakfast = findViewById(R.id.btnBreakfast);
        btnLunch = findViewById(R.id.btnLunch);
        btnDinner = findViewById(R.id.btnDinner);

        dbHelper = new DatabaseHelper(this);
        monAnList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MonAnAdapter(monAnList, this);
        recyclerView.setAdapter(adapter);

        loadMonAn(null);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMonAn(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnClear.setOnClickListener(v -> edtSearch.setText(""));

        btnBreakfast.setOnClickListener(v -> filterCategory("Ăn sáng"));
        btnLunch.setOnClickListener(v -> filterCategory("Món chính"));
        btnDinner.setOnClickListener(v -> filterCategory("Tráng miệng"));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThemMonAnActivity.class);
            startActivity(intent);
        });
    }

    private void loadMonAn(String keyword) {
        monAnList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon, DanhMuc.tenDanhMuc " +
                "FROM MonAn LEFT JOIN DanhMuc ON MonAn.idDanhMuc = DanhMuc.idDanhMuc";

        if (keyword != null && !keyword.isEmpty()) {
            query += " WHERE MonAn.tenMon LIKE '%" + keyword + "%'";
        }

        Cursor cursor = db.rawQuery(query, null);
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
        cursor.close();

        adapter.notifyDataSetChanged();
    }

    private void filterCategory(String category) {
        monAnList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon, DanhMuc.tenDanhMuc " +
                "FROM MonAn LEFT JOIN DanhMuc ON MonAn.idDanhMuc = DanhMuc.idDanhMuc " +
                "WHERE DanhMuc.tenDanhMuc = ?";

        Cursor cursor = db.rawQuery(query, new String[]{category});
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
        cursor.close();

        adapter.notifyDataSetChanged();
    }
}
