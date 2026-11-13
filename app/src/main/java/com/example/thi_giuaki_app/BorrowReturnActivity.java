package com.example.thi_giuaki_app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BorrowReturnActivity extends AppCompatActivity implements BorrowSlipAdapter.OnReturnListener {
    Spinner spinnerBooks;
    EditText edtTenNguoiMuon, edtSdt, edtNgayMuon, edtNgayTra;
    Button btnBorrow, btnBack;
    TextView tvEmptySlips;
    RecyclerView recyclerViewSlips;
    DatabaseHelper dbHelper;
    BorrowSlipAdapter adapter;
    List<BorrowSlip> slipList;
    List<String> availableBookDisplayList;
    List<String> availableBookCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_return);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Mượn & Trả Sách");
        }

        spinnerBooks = findViewById(R.id.spinnerBooks);
        edtTenNguoiMuon = findViewById(R.id.edtTenNguoiMuon);
        edtSdt = findViewById(R.id.edtSdt);
        edtNgayMuon = findViewById(R.id.edtNgayMuon);
        edtNgayTra = findViewById(R.id.edtNgayTra);
        btnBorrow = findViewById(R.id.btnBorrow);
        btnBack = findViewById(R.id.btnBackBorrow);
        tvEmptySlips = findViewById(R.id.tvEmptySlips);
        recyclerViewSlips = findViewById(R.id.recyclerViewSlips);
        dbHelper = new DatabaseHelper(this);

        recyclerViewSlips.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSlips.setNestedScrollingEnabled(false); // Mượt mà khi nằm trong ScrollView

        // Chọn ngày bằng DatePickerDialog theo đúng yêu cầu đề bài ("Chọn ngày mượn", "Chọn ngày dự kiến trả")
        setupDatePicker(edtNgayMuon);
        setupDatePicker(edtNgayTra);

        // Đặt ngày mượn mặc định là hôm nay
        Calendar cal = Calendar.getInstance();
        edtNgayMuon.setText(String.format(Locale.getDefault(), "%02d/%02d/%d",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));

        loadAvailableBooks();
        loadSlips();

        btnBack.setOnClickListener(v -> finish());

        btnBorrow.setOnClickListener(v -> {
            if (availableBookCodes.isEmpty() || spinnerBooks.getSelectedItemPosition() < 0) {
                Toast.makeText(this, "Không có sách nào có sẵn để mượn!", Toast.LENGTH_LONG).show();
                return;
            }
            String maSach = availableBookCodes.get(spinnerBooks.getSelectedItemPosition());
            String tenNguoi = edtTenNguoiMuon.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();
            String ngayMuon = edtNgayMuon.getText().toString().trim();
            String ngayTra = edtNgayTra.getText().toString().trim();

            if (TextUtils.isEmpty(tenNguoi)) {
                edtTenNguoiMuon.setError("Vui lòng nhập họ tên người mượn");
                edtTenNguoiMuon.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(sdt) || sdt.length() < 9) {
                edtSdt.setError("Vui lòng nhập số điện thoại hợp lệ");
                edtSdt.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(ngayMuon)) {
                Toast.makeText(this, "Vui lòng chọn ngày mượn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(ngayTra)) {
                Toast.makeText(this, "Vui lòng chọn ngày dự kiến trả", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Kiểm tra lại lần cuối xem sách có đang bị người khác mượn không
            Cursor checkCursor = db.rawQuery("SELECT " + DatabaseHelper.COL_BOOK_TRANGTHAI + " FROM " + DatabaseHelper.TABLE_BOOKS + " WHERE " + DatabaseHelper.COL_BOOK_MA + "=?", new String[]{maSach});
            if (checkCursor.moveToFirst() && checkCursor.getInt(0) == 1) {
                checkCursor.close();
                Toast.makeText(this, "Sách này đang được người khác mượn!", Toast.LENGTH_LONG).show();
                loadAvailableBooks();
                return;
            }
            checkCursor.close();

            // Thêm phiếu mượn
            ContentValues slipValues = new ContentValues();
            slipValues.put(DatabaseHelper.COL_BORROW_MASACH, maSach);
            slipValues.put(DatabaseHelper.COL_BORROW_TENNGUOIMUON, tenNguoi);
            slipValues.put(DatabaseHelper.COL_BORROW_SDT, sdt);
            slipValues.put(DatabaseHelper.COL_BORROW_NGAYMUON, ngayMuon);
            slipValues.put(DatabaseHelper.COL_BORROW_NGAYTRA, ngayTra);
            slipValues.put(DatabaseHelper.COL_BORROW_TRANGTHAI, "Đang mượn");
            db.insert(DatabaseHelper.TABLE_BORROW, null, slipValues);

            // Cập nhật trạng thái sách thành Đang được mượn (1)
            ContentValues bookValues = new ContentValues();
            bookValues.put(DatabaseHelper.COL_BOOK_TRANGTHAI, 1);
            db.update(DatabaseHelper.TABLE_BOOKS, bookValues, DatabaseHelper.COL_BOOK_MA + "=?", new String[]{maSach});

            Toast.makeText(this, "Lập phiếu mượn thành công!", Toast.LENGTH_SHORT).show();
            edtTenNguoiMuon.setText("");
            edtSdt.setText("");
            edtNgayTra.setText("");
            loadAvailableBooks();
            loadSlips();
        });
    }

    private void setupDatePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                editText.setText(dateStr);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
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

    private void loadAvailableBooks() {
        availableBookDisplayList = new ArrayList<>();
        availableBookCodes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COL_BOOK_MA + ", " + DatabaseHelper.COL_BOOK_TEN +
                " FROM " + DatabaseHelper.TABLE_BOOKS + " WHERE " + DatabaseHelper.COL_BOOK_TRANGTHAI + "=0", null);
        if (cursor.moveToFirst()) {
            do {
                String ma = cursor.getString(0);
                String ten = cursor.getString(1);
                availableBookCodes.add(ma);
                availableBookDisplayList.add(ma + " - " + ten);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (availableBookDisplayList.isEmpty()) {
            availableBookDisplayList.add("(Hết sách có sẵn để mượn)");
        }
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availableBookDisplayList);
        spinnerBooks.setAdapter(adapterSpinner);
    }

    private void loadSlips() {
        slipList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Hiển thị phiếu đang mượn lên trước
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BORROW +
                " ORDER BY CASE WHEN " + DatabaseHelper.COL_BORROW_TRANGTHAI + "='Đang mượn' THEN 0 ELSE 1 END, " +
                DatabaseHelper.COL_BORROW_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_ID));
                String ma = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_MASACH));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_TENNGUOIMUON));
                String sdt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_SDT));
                String nm = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_NGAYMUON));
                String nt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_NGAYTRA));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BORROW_TRANGTHAI));
                slipList.add(new BorrowSlip(id, ma, ten, sdt, nm, nt, tt));
            } while (cursor.moveToNext());
        }
        cursor.close();

        tvEmptySlips.setVisibility(slipList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter = new BorrowSlipAdapter(this, slipList, this);
        recyclerViewSlips.setAdapter(adapter);
    }

    @Override
    public void onReturnBook(BorrowSlip slip) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận trả sách")
                .setMessage("Xác nhận người mượn '" + slip.getTenNguoiMuon() + "' đã trả sách mã " + slip.getMaSach() + "?")
                .setPositiveButton("Trả sách", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // Cập nhật trạng thái phiếu thành Đã trả
                    ContentValues slipValues = new ContentValues();
                    slipValues.put(DatabaseHelper.COL_BORROW_TRANGTHAI, "Đã trả");
                    db.update(DatabaseHelper.TABLE_BORROW, slipValues, DatabaseHelper.COL_BORROW_ID + "=?", new String[]{String.valueOf(slip.getId())});

                    // Cập nhật trạng thái sách về Có sẵn (0)
                    ContentValues bookValues = new ContentValues();
                    bookValues.put(DatabaseHelper.COL_BOOK_TRANGTHAI, 0);
                    db.update(DatabaseHelper.TABLE_BOOKS, bookValues, DatabaseHelper.COL_BOOK_MA + "=?", new String[]{slip.getMaSach()});

                    Toast.makeText(this, "Đã trả sách! Trạng thái sách cập nhật về Có sẵn.", Toast.LENGTH_SHORT).show();
                    loadAvailableBooks();
                    loadSlips();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
