package com.example.thi_giuaki_app;

public class BorrowSlip {
    private int id;
    private String maSach;
    private String tenNguoiMuon;
    private String sdt;
    private String ngayMuon;
    private String ngayTra;
    private String trangThaiPhieu;

    public BorrowSlip(int id, String maSach, String tenNguoiMuon, String sdt, String ngayMuon, String ngayTra, String trangThaiPhieu) {
        this.id = id;
        this.maSach = maSach;
        this.tenNguoiMuon = tenNguoiMuon;
        this.sdt = sdt;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.trangThaiPhieu = trangThaiPhieu;
    }

    public int getId() { return id; }
    public String getMaSach() { return maSach; }
    public String getTenNguoiMuon() { return tenNguoiMuon; }
    public String getSdt() { return sdt; }
    public String getNgayMuon() { return ngayMuon; }
    public String getNgayTra() { return ngayTra; }
    public String getTrangThaiPhieu() { return trangThaiPhieu; }
}
