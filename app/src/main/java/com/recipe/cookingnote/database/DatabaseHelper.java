package com.recipe.cookingnote.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên CSDL và version
    private static final String DATABASE_NAME = "CongThucNauAn.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 🔹 Tạo bảng DanhMuc
        db.execSQL("CREATE TABLE DanhMuc (" +
                "idDanhMuc INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenDanhMuc TEXT NOT NULL)");
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Món chính')");
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Ăn sáng')");
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Tráng miệng')");

        // 🔹 Tạo bảng MonAn
        db.execSQL("CREATE TABLE MonAn (\n" +
                "    idMonAn INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    tenMon TEXT NOT NULL,\n" +
                "    moTa TEXT,\n" +
                "    anhMon TEXT,              -- \uD83D\uDD39 Thêm cột này để lưu URI ảnh\n" +
                "    idDanhMuc INTEGER,\n" +
                "    FOREIGN KEY(idDanhMuc) REFERENCES DanhMuc(idDanhMuc)\n" +
                ");\n)");

        // 🔹 Tạo bảng NguyenLieu
        db.execSQL("CREATE TABLE NguyenLieu (" +
                "idNguyenLieu INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idMonAn INTEGER NOT NULL, " +
                "tenNguyenLieu TEXT NOT NULL, " +
                "soLuong TEXT, " +
                "FOREIGN KEY (idMonAn) REFERENCES MonAn(idMonAn))");

        // 🔹 Tạo bảng BuocNau
        db.execSQL("CREATE TABLE BuocNau (" +
                "idBuoc INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idMonAn INTEGER NOT NULL, " +
                "soThuTu INTEGER, " +
                "moTaBuoc TEXT NOT NULL, " +
                "FOREIGN KEY (idMonAn) REFERENCES MonAn(idMonAn))");

        // 🔹 Tạo bảng YeuThich
        db.execSQL("CREATE TABLE YeuThich (" +
                "idYeuThich INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idMonAn INTEGER NOT NULL, " +
                "ngayThem TEXT DEFAULT (datetime('now', 'localtime')), " +
                "FOREIGN KEY (idMonAn) REFERENCES MonAn(idMonAn))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Khi cập nhật cấu trúc DB
        db.execSQL("DROP TABLE IF EXISTS YeuThich");
        db.execSQL("DROP TABLE IF EXISTS BuocNau");
        db.execSQL("DROP TABLE IF EXISTS NguyenLieu");
        db.execSQL("DROP TABLE IF EXISTS MonAn");
        db.execSQL("DROP TABLE IF EXISTS DanhMuc");
        onCreate(db);
    }
    // 🔹 Cập nhật thông tin món ăn
    public boolean updateMonAn(int id, String tenMon, String danhMuc, String nguyenLieu, String buocLam, byte[] anh) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenMon", tenMon);
        values.put("danhMuc", danhMuc);
        values.put("nguyenLieu", nguyenLieu);
        values.put("buocLam", buocLam);
        values.put("anh", anh);

        int rows = db.update("MonAn", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0; // ✅ true nếu cập nhật thành công
    }
}