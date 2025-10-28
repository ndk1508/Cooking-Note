package com.recipe.cookingnote.Model;

import java.io.Serializable;

public class MonAn implements Serializable {

    private int id;
    private String tenMon;
    private String moTa;
    private String anhMon;
    private String danhMuc;
    private boolean yeuThich;

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

    @Override
    public String toString() {
        return tenMon + " - " + danhMuc;
    }
    public boolean isYeuThich() {
        return yeuThich;
    }

    public void setYeuThich(boolean yeuThich) {
        this.yeuThich = yeuThich;
    }
}
