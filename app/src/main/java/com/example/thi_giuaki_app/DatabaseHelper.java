package com.example.thi_giuaki_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "library_v2.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_BOOKS = "books";
    public static final String COL_BOOK_ID = "id";
    public static final String COL_BOOK_MA = "ma_sach";
    public static final String COL_BOOK_TEN = "ten_sach";
    public static final String COL_BOOK_TACGIA = "tac_gia";
    public static final String COL_BOOK_THELOAI = "the_loai";
    public static final String COL_BOOK_TRANGTHAI = "trang_thai"; // 0: Có sẵn, 1: Đang được mượn

    public static final String TABLE_BORROW = "borrow_slips";
    public static final String COL_BORROW_ID = "id";
    public static final String COL_BORROW_MASACH = "ma_sach";
    public static final String COL_BORROW_TENNGUOIMUON = "ten_nguoi_muon";
    public static final String COL_BORROW_SDT = "sdt";
    public static final String COL_BORROW_NGAYMUON = "ngay_muon";
    public static final String COL_BORROW_NGAYTRA = "ngay_tra";
    public static final String COL_BORROW_TRANGTHAI = "trang_thai_phieu"; // Đang mượn / Đã trả

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBooksTable = "CREATE TABLE " + TABLE_BOOKS + " (" +
                COL_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOK_MA + " TEXT UNIQUE NOT NULL, " +
                COL_BOOK_TEN + " TEXT NOT NULL, " +
                COL_BOOK_TACGIA + " TEXT NOT NULL, " +
                COL_BOOK_THELOAI + " TEXT NOT NULL, " +
                COL_BOOK_TRANGTHAI + " INTEGER DEFAULT 0)";
        db.execSQL(createBooksTable);

        String createBorrowTable = "CREATE TABLE " + TABLE_BORROW + " (" +
                COL_BORROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BORROW_MASACH + " TEXT NOT NULL, " +
                COL_BORROW_TENNGUOIMUON + " TEXT NOT NULL, " +
                COL_BORROW_SDT + " TEXT NOT NULL, " +
                COL_BORROW_NGAYMUON + " TEXT NOT NULL, " +
                COL_BORROW_NGAYTRA + " TEXT NOT NULL, " +
                COL_BORROW_TRANGTHAI + " TEXT NOT NULL)";
        db.execSQL(createBorrowTable);

        // Thêm sẵn dữ liệu mẫu để giáo viên chấm bài dễ test ngay
        insertSampleBooks(db);
    }

    private void insertSampleBooks(SQLiteDatabase db) {
        insertBookDirect(db, "S001", "Lập trình Android cơ bản", "Nguyễn Văn An", "Công nghệ thông tin", 0);
        insertBookDirect(db, "S002", "Cấu trúc dữ liệu và giải thuật", "Trần Minh Tuấn", "Khoa học máy tính", 0);
        insertBookDirect(db, "S003", "Đắc Nhân Tâm", "Dale Carnegie", "Kỹ năng sống", 1);

        ContentValues cv = new ContentValues();
        cv.put(COL_BORROW_MASACH, "S003");
        cv.put(COL_BORROW_TENNGUOIMUON, "Lê Hoàng Nam");
        cv.put(COL_BORROW_SDT, "0912345678");
        cv.put(COL_BORROW_NGAYMUON, "10/11/2026");
        cv.put(COL_BORROW_NGAYTRA, "24/11/2026");
        cv.put(COL_BORROW_TRANGTHAI, "Đang mượn");
        db.insert(TABLE_BORROW, null, cv);
    }

    private void insertBookDirect(SQLiteDatabase db, String ma, String ten, String tg, String tl, int tt) {
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOK_MA, ma);
        cv.put(COL_BOOK_TEN, ten);
        cv.put(COL_BOOK_TACGIA, tg);
        cv.put(COL_BOOK_THELOAI, tl);
        cv.put(COL_BOOK_TRANGTHAI, tt);
        db.insert(TABLE_BOOKS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BORROW);
        onCreate(db);
    }

    public boolean isBookCodeExists(String maSach) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_BOOKS + " WHERE LOWER(" + COL_BOOK_MA + ") = LOWER(?)", new String[]{maSach});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
