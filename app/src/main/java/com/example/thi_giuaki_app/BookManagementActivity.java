package com.example.thi_giuaki_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BookManagementActivity extends AppCompatActivity implements BookAdapter.OnBookListener {
    RecyclerView recyclerView;
    Button btnAddBook, btnBack;
    TextView tvEmpty;
    DatabaseHelper dbHelper;
    BookAdapter adapter;
    List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_management);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Danh Sách Sách");
        }

        recyclerView = findViewById(R.id.recyclerBooks);
        btnAddBook = findViewById(R.id.btnAddBook);
        btnBack = findViewById(R.id.btnBackBookManager);
        tvEmpty = findViewById(R.id.tvEmptyBooks);
        dbHelper = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadBooks();

        btnAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(BookManagementActivity.this, AddEditBookActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks() {
        bookList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BOOKS + " ORDER BY " + DatabaseHelper.COL_BOOK_ID + " DESC", null);
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

        tvEmpty.setVisibility(bookList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter = new BookAdapter(this, bookList, true, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEdit(Book book) {
        Intent intent = new Intent(BookManagementActivity.this, AddEditBookActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    @Override
    public void onDelete(Book book) {
        if (book.getTrangThai() == 1) {
            Toast.makeText(this, "Không thể xóa sách đang được mượn!", Toast.LENGTH_LONG).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa cuốn sách '" + book.getTenSach() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    long result = db.delete(DatabaseHelper.TABLE_BOOKS, DatabaseHelper.COL_BOOK_ID + "=?", new String[]{String.valueOf(book.getId())});
                    if (result > 0) {
                        Toast.makeText(this, "Xóa sách thành công", Toast.LENGTH_SHORT).show();
                        loadBooks();
                    } else {
                        Toast.makeText(this, "Xóa sách thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
