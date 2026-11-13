package com.example.thi_giuaki_app;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditBookActivity extends AppCompatActivity {
    TextView tvTitle;
    EditText edtMaSach, edtTenSach, edtTacGia, edtTheLoai;
    Button btnSave, btnCancel;
    DatabaseHelper dbHelper;
    Book editBook = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTitle = findViewById(R.id.tvAddEditTitle);
        edtMaSach = findViewById(R.id.edtMaSach);
        edtTenSach = findViewById(R.id.edtTenSach);
        edtTacGia = findViewById(R.id.edtTacGia);
        edtTheLoai = findViewById(R.id.edtTheLoai);
        btnSave = findViewById(R.id.btnSaveBook);
        btnCancel = findViewById(R.id.btnCancelBook);
        dbHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("book")) {
            editBook = (Book) getIntent().getSerializableExtra("book");
            tvTitle.setText("CẬP NHẬT THÔNG TIN SÁCH");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sửa Thông Tin Sách");
            edtMaSach.setText(editBook.getMaSach());
            edtMaSach.setEnabled(false); // Không cho sửa mã sách khi update
            edtTenSach.setText(editBook.getTenSach());
            edtTacGia.setText(editBook.getTacGia());
            edtTheLoai.setText(editBook.getTheLoai());
        } else {
            tvTitle.setText("THÊM SÁCH MỚI");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Thêm Sách Mới");
        }

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String ma = edtMaSach.getText().toString().trim();
            String ten = edtTenSach.getText().toString().trim();
            String tg = edtTacGia.getText().toString().trim();
            String tl = edtTheLoai.getText().toString().trim();

            if (TextUtils.isEmpty(ma)) {
                edtMaSach.setError("Vui lòng nhập mã sách");
                edtMaSach.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(ten)) {
                edtTenSach.setError("Vui lòng nhập tên sách");
                edtTenSach.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(tg)) {
                edtTacGia.setError("Vui lòng nhập tác giả");
                edtTacGia.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(tl)) {
                edtTheLoai.setError("Vui lòng nhập thể loại");
                edtTheLoai.requestFocus();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_BOOK_TEN, ten);
            values.put(DatabaseHelper.COL_BOOK_TACGIA, tg);
            values.put(DatabaseHelper.COL_BOOK_THELOAI, tl);

            if (editBook == null) {
                if (dbHelper.isBookCodeExists(ma)) {
                    edtMaSach.setError("Mã sách đã tồn tại!");
                    edtMaSach.requestFocus();
                    Toast.makeText(this, "Lỗi: Mã sách '" + ma + "' đã tồn tại!", Toast.LENGTH_LONG).show();
                    return;
                }
                values.put(DatabaseHelper.COL_BOOK_MA, ma);
                values.put(DatabaseHelper.COL_BOOK_TRANGTHAI, 0);
                long result = db.insert(DatabaseHelper.TABLE_BOOKS, null, values);
                if (result != -1) {
                    Toast.makeText(this, "Thêm sách thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Thêm sách thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                long result = db.update(DatabaseHelper.TABLE_BOOKS, values, DatabaseHelper.COL_BOOK_ID + "=?", new String[]{String.valueOf(editBook.getId())});
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật sách thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
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
}
