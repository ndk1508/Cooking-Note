package com.recipe.cookingnote.Model;

import java.io.Serializable;

/**
 * Lớp MonAn đại diện cho một món ăn trong ứng dụng.
 *
 * Đây là lớp model (dữ liệu) dùng để trao đổi giữa các Activity,
 * Adapter, và Database. Serializable giúp truyền đối tượng qua Intent.
 */
public class MonAn implements Serializable {

    // 🔹 Các thuộc tính của món ăn
    private int id;             // ID của món ăn trong cơ sở dữ liệu
    private String tenMon;      // Tên món ăn (VD: "Bún bò Huế")
    private String moTa;        // Mô tả ngắn gọn về món (tùy chọn)
    private String anhMon;      // Đường dẫn ảnh (có thể là resource ID hoặc URI)
    private String danhMuc;     // Tên danh mục (VD: "Ăn sáng", "Tráng miệng")
    private boolean yeuThich;   // Cờ đánh dấu món ăn yêu thích

    /**
     * Constructor đầy đủ dùng khi lấy dữ liệu từ SQLite.
     */
    public MonAn(int id, String tenMon, String moTa, String anhMon, String danhMuc) {
        this.id = id;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.anhMon = anhMon;
        this.danhMuc = danhMuc;
    }

    // 🔹 Getter - dùng để đọc dữ liệu
    public int getId() { return id; }
    public String getTenMon() { return tenMon; }
    public String getMoTa() { return moTa; }
    public String getAnhMon() { return anhMon; }
    public String getDanhMuc() { return danhMuc; }

    // 🔹 Getter/Setter cho trạng thái yêu thích
    public boolean isYeuThich() { return yeuThich; }
    public void setYeuThich(boolean yeuThich) { this.yeuThich = yeuThich; }

    /**
     * Ghi đè toString() giúp hiển thị đẹp khi debug hoặc trong Spinner/ListView
     */
    @Override
    public String toString() {
        return tenMon + " - " + danhMuc;
    }
}
