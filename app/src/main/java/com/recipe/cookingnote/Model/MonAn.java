package com.recipe.cookingnote.Model;
public class MonAn {
    private int id;
    private String tenMon, moTa, anhMon, danhMuc;

    public MonAn(int id, String tenMon, String moTa, String anhMon, String danhMuc) {
        this.id = id;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.anhMon = anhMon;
        this.danhMuc = danhMuc;
    }

    public int getId() { return id; }
    public String getTenMon() { return tenMon; }
    public String getMoTa() { return moTa; }
    public String getAnhMon() { return anhMon; }
    public String getDanhMuc() { return danhMuc; }
}
