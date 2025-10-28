package com.recipe.cookingnote.database;
import android.content.Context;
import android.database.Cursor;
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
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Ăn sáng')");
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Ăn trưa')");
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Ăn tối')");
        db.execSQL("INSERT INTO DanhMuc (tenDanhMuc) VALUES ('Tráng miệng')");

        // 🔹 Tạo bảng MonAn
        db.execSQL("CREATE TABLE MonAn (" +
                "idMonAn INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenMon TEXT NOT NULL, " +
                "moTa TEXT, " +
                "anhMon TEXT, " +        // dùng để lưu URI ảnh
                "idDanhMuc INTEGER, " +
                "yeuThich INTEGER DEFAULT 0, " +
                "FOREIGN KEY(idDanhMuc) REFERENCES DanhMuc(idDanhMuc))");

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
    // 🔹 Thêm món vào danh sách yêu thích
    public void themYeuThich(int idMonAn) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO YeuThich (idMonAn) VALUES (" + idMonAn + ")");
        db.close();
    }

    // 🔹 Xóa món khỏi danh sách yêu thích
    public void xoaYeuThich(int idMonAn) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM YeuThich WHERE idMonAn = " + idMonAn);
        db.close();
    }

    // 🔹 Kiểm tra xem món ăn có nằm trong danh sách yêu thích hay không
    public boolean laYeuThich(int idMonAn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM YeuThich WHERE idMonAn = " + idMonAn, null);
        boolean tonTai = cursor.moveToFirst();
        cursor.close();
        return tonTai;
    }

    // 🔹 Lấy danh sách món ăn yêu thích (JOIN MonAn + YeuThich)
    public Cursor layDanhSachYeuThich() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT MonAn.idMonAn, MonAn.tenMon, MonAn.moTa, MonAn.anhMon " +
                        "FROM MonAn " +
                        "INNER JOIN YeuThich ON MonAn.idMonAn = YeuThich.idMonAn " +
                        "ORDER BY YeuThich.ngayThem DESC", null
        );
    }
}