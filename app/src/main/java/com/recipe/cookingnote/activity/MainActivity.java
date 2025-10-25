package com.recipe.cookingnote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.recipe.cookingnote.R;
import com.recipe.cookingnote.adapter.MonAnAdapter;
import com.recipe.cookingnote.database.DatabaseHelper;
import com.recipe.cookingnote.model.MonAn;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MonAnAdapter adapter;
    private DatabaseHelper dbHelper;
    private EditText edtSearch;
    private List<MonAn> monAnList, filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        edtSearch = findViewById(R.id.edtSearch);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        Button btnBreakfast = findViewById(R.id.btnBreakfast);
        Button btnLunch = findViewById(R.id.btnLunch);
        Button btnDinner = findViewById(R.id.btnDinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        monAnList = dbHelper.getAllMonAn();
        filteredList = new ArrayList<>(monAnList);

        adapter = new MonAnAdapter(this, filteredList, monAn -> {
            Intent intent = new Intent(MainActivity.this, SuaMonAnActivity.class);
            intent.putExtra("MON_ID", monAn.getId());

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 🟢 Nút thêm món
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThemMonAnActivity.class);
            startActivity(intent);
        });

        // 🟢 Tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByName(s.toString());
            }
        });

        // 🟢 Lọc theo danh mục
        btnBreakfast.setOnClickListener(v -> filterByCategory("Ăn sáng"));
        btnLunch.setOnClickListener(v -> filterByCategory("Ăn trưa"));
        btnDinner.setOnClickListener(v -> filterByCategory("Ăn tối"));
    }

    private void filterByName(String keyword) {
        filteredList.clear();
        for (MonAn item : monAnList) {
            if (item.getTenMon().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterByCategory(String category) {
        filteredList.clear();
        for (MonAn item : monAnList) {
            if (item.getDanhMuc().equalsIgnoreCase(category)) {
                filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        monAnList = dbHelper.getAllMonAn();
        filteredList.clear();
        filteredList.addAll(monAnList);
        adapter.notifyDataSetChanged();
    }
}