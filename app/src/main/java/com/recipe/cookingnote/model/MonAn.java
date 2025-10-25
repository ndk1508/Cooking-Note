package com.recipe.cookingnote.model;

public class MonAn {
    private int id;
    private String tenMon;
    private String danhMuc;
    private String nguyenLieu;
    private String buocLam;
    private byte[] anh;

    public MonAn(int id, String tenMon, String danhMuc, String nguyenLieu, String buocLam, byte[] anh) {
        this.id = id;
        this.tenMon = tenMon;
        this.danhMuc = danhMuc;
        this.nguyenLieu = nguyenLieu;
        this.buocLam = buocLam;
        this.anh = anh;
    }
    // 🔹 Getter
    public int getId() { return id; }
    public String getTenMon() { return tenMon; }
    public String getDanhMuc() { return danhMuc; }
    public String getNguyenLieu() { return nguyenLieu; }
    public String getBuocLam() { return buocLam; }
    public byte[] getAnh() { return anh; }

    // 🔹 Setter (nếu cần)
    public void setId(int id) { this.id = id; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }
    public void setNguyenLieu(String nguyenLieu) { this.nguyenLieu = nguyenLieu; }
    public void setBuocLam(String buocLam) { this.buocLam = buocLam; }
    public void setAnh(byte[] anh) { this.anh = anh; }
}
