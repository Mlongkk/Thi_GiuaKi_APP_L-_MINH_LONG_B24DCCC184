package com.example.thi_giuaki_app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText edtKeyword;
    Button btnSearch, btnBack;
    TextView tvEmptySearch;
    RecyclerView recyclerView;
    DatabaseHelper dbHelper;
    BookAdapter adapter;
    List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tìm Kiếm Sách");
        }

        edtKeyword = findViewById(R.id.edtSearchKeyword);
        btnSearch = findViewById(R.id.btnSearchAction);
        btnBack = findViewById(R.id.btnBackSearch);
        tvEmptySearch = findViewById(R.id.tvEmptySearch);
        recyclerView = findViewById(R.id.recyclerViewSearch);
        dbHelper = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchBooks("");

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String keyword = edtKeyword.getText().toString().trim();
            searchBooks(keyword);
        });

        edtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBooks(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchBooks(String keyword) {
        bookList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_BOOKS + " WHERE " +
                DatabaseHelper.COL_BOOK_TEN + " LIKE ? OR " + DatabaseHelper.COL_BOOK_TACGIA + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%"});
        
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_ID));
                String ma = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_MA));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_TEN));
                String tg = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_TACGIA));
                String tl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_THELOAI));
                int tt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOK_TRANGTHAI));
                bookList.add(new Book(id, ma, ten, tg, tl, tt));
            } while (cursor.moveToNext());
        }
        cursor.close();

        tvEmptySearch.setVisibility(bookList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter = new BookAdapter(this, bookList, false, null);
        recyclerView.setAdapter(adapter);
    }
}
