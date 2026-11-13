package com.example.thi_giuaki_app;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String maSach;
    private String tenSach;
    private String tacGia;
    private String theLoai;
    private int trangThai; // 0: Có sẵn, 1: Đang được mượn

    public Book(int id, String maSach, String tenSach, String tacGia, String theLoai, int trangThai) {
        this.id = id;
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.theLoai = theLoai;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public String getMaSach() { return maSach; }
    public String getTenSach() { return tenSach; }
    public String getTacGia() { return tacGia; }
    public String getTheLoai() { return theLoai; }
    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}
