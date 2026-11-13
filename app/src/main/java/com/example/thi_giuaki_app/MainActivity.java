package com.example.thi_giuaki_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnBookManager, btnBorrowReturn, btnSearch;
    TextView tvStats;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        tvStats = findViewById(R.id.tvStats);
        btnBookManager = findViewById(R.id.btnBookManager);
        btnBorrowReturn = findViewById(R.id.btnBorrowReturn);
        btnSearch = findViewById(R.id.btnSearch);

        btnBookManager.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, BookManagementActivity.class)));

        btnBorrowReturn.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, BorrowReturnActivity.class)));

        btnSearch.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cTotal = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKS, null);
        int totalBooks = cTotal.moveToFirst() ? cTotal.getInt(0) : 0;
        cTotal.close();

        Cursor cBorrowed = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKS + " WHERE " + DatabaseHelper.COL_BOOK_TRANGTHAI + "=1", null);
        int borrowedBooks = cBorrowed.moveToFirst() ? cBorrowed.getInt(0) : 0;
        cBorrowed.close();

        int availableBooks = totalBooks - borrowedBooks;
        tvStats.setText("Tổng số sách: " + totalBooks + " | Có sẵn: " + availableBooks + " | Đang mượn: " + borrowedBooks);
    }
}
