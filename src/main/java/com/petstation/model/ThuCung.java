package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Hồ sơ thú cưng khớp bảng [thu_cung].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính là mã thú cưng (maThuCung). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với đối tượng KhachHang (chủ nuôi).
 * - Có các phương thức tính tuổi dẫn xuất (theo năm / theo tháng).
 * - Kiểm tra ràng buộc trong setter và equals/hashCode theo khóa chính maThuCung.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class ThuCung implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Thú cưng */
    private String maThuCung;
    /** Quan hệ kết hợp (Association): Thú cưng thuộc sở hữu của một Khách hàng. */
    private KhachHang khachHang;
    private String ten;
    private String loai;
    private String giong;
    private String gioiTinh;
    private String mauLong;
    private double canNang;
    private String tinhTrangSucKhoe;
    private String diUngBenhLy;
    private String anh;
    private boolean dangTheoDoi;
    private LocalDate ngaySinh;
    private String tinhCach;
    private String tiemChung;
    private int soMuiTiem;
    private LocalDate ngayTiemGanNhat;

    /** Constructor mặc định. */
    public ThuCung() {
        this.dangTheoDoi = true;
    }

    /** Constructor theo khóa chính nghiệp vụ maThuCung. */
    public ThuCung(String maThuCung) {
        this();
        setMaThuCung(maThuCung);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public ThuCung(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng KhachHang và khóa chính maThuCung. */
    public ThuCung(
            String maThuCung,
            KhachHang khachHang,
            String ten,
            String loai,
            String giong,
            String gioiTinh,
            String mauLong,
            double canNang,
            String tinhTrangSucKhoe,
            String diUngBenhLy,
            String anh,
            boolean dangTheoDoi,
            LocalDate ngaySinh,
            String tinhCach,
            String tiemChung,
            int soMuiTiem,
            LocalDate ngayTiemGanNhat) {
        setMaThuCung(maThuCung);
        setKhachHang(khachHang);
        setTen(ten);
        setLoai(loai);
        setGiong(giong);
        setGioiTinh(gioiTinh);
        setMauLong(mauLong);
        setCanNang(canNang);
        setTinhTrangSucKhoe(tinhTrangSucKhoe);
        setDiUngBenhLy(diUngBenhLy);
        setAnh(anh);
        setDangTheoDoi(dangTheoDoi);
        setNgaySinh(ngaySinh);
        setTinhCach(tinhCach);
        setTiemChung(tiemChung);
        setSoMuiTiem(soMuiTiem);
        setNgayTiemGanNhat(ngayTiemGanNhat);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public ThuCung(
            long id,
            String maThuCung,
            KhachHang khachHang,
            String ten,
            String loai,
            String giong,
            String gioiTinh,
            String mauLong,
            double canNang,
            String tinhTrangSucKhoe,
            String diUngBenhLy,
            String anh,
            boolean dangTheoDoi,
            LocalDate ngaySinh,
            String tinhCach,
            String tiemChung,
            int soMuiTiem,
            LocalDate ngayTiemGanNhat) {
        this(maThuCung, khachHang, ten, loai, giong, gioiTinh, mauLong, canNang, tinhTrangSucKhoe, diUngBenhLy, anh, dangTheoDoi, ngaySinh, tinhCach, tiemChung, soMuiTiem, ngayTiemGanNhat);
        setId(id);
    }

    /** Constructor cầu nối tương thích DAO cũ với khachHangId. */
    public ThuCung(
            long id,
            String maThuCung,
            long khachHangId,
            String ten,
            String loai,
            String giong,
            String gioiTinh,
            String mauLong,
            double canNang,
            String tinhTrangSucKhoe,
            String diUngBenhLy,
            String anh,
            boolean dangTheoDoi,
            LocalDate ngaySinh,
            String tinhCach,
            String tiemChung,
            int soMuiTiem,
            LocalDate ngayTiemGanNhat) {
        this(
                id,
                maThuCung,
                new KhachHang(khachHangId),
                ten,
                loai,
                giong,
                gioiTinh,
                mauLong,
                canNang,
                tinhTrangSucKhoe,
                diUngBenhLy,
                anh,
                dangTheoDoi,
                ngaySinh,
                tinhCach,
                tiemChung,
                soMuiTiem,
                ngayTiemGanNhat);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính tuổi thú cưng theo năm (thuộc tính dẫn xuất từ ngaySinh). */
    public int tinhTuoiTheoNam() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    /** Tính tổng số tháng tuổi của thú cưng (thuộc tính dẫn xuất từ ngaySinh). */
    public int tinhTuoiTheoThang() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        Period p = Period.between(ngaySinh, LocalDate.now());
        return p.getYears() * 12 + p.getMonths();
    }

    /** Kiểm tra thú cưng đã có thông tin tiêm phòng hay chưa (dẫn xuất). */
    public boolean kiemTraDaTiemPhong() {
        return soMuiTiem > 0 || (tiemChung != null && !tiemChung.isBlank());
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maThuCung);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.maThuCung);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maThuCung == null || this.maThuCung.isBlank()) && id > 0) {
            this.maThuCung = DuLieu.taoMaTuId("TC", 6, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getMaThuCung() {
        return this.maThuCung;
    }

    public String layMaThuCung() {
        return this.maThuCung;
    }

    public String maThuCung() {
        return this.maThuCung;
    }

    public void setMaThuCung(String maThuCung) {
        this.maThuCung = maThuCung == null ? null : maThuCung.trim();
    }

    public void datMaThuCung(String maThuCung) {
        setMaThuCung(maThuCung);
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public KhachHang layKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public void datKhachHang(KhachHang khachHang) {
        setKhachHang(khachHang);
    }

    public long getKhachHangId() {
        return (khachHang == null || khachHang.getId() == null) ? 0L : khachHang.getId();
    }

    public long layKhachHangId() {
        return getKhachHangId();
    }

    public long khachHangId() {
        return getKhachHangId();
    }

    public void setKhachHangId(long khachHangId) {
        this.khachHang = new KhachHang(khachHangId);
    }

    public void datKhachHangId(long khachHangId) {
        setKhachHangId(khachHangId);
    }

    public String getTen() {
        return this.ten;
    }

    public String layTen() {
        return this.ten;
    }

    public String ten() {
        return this.ten;
    }

    public void setTen(String ten) {
        this.ten = ten == null ? null : ten.trim();
    }

    public void datTen(String ten) {
        setTen(ten);
    }

    public String getLoai() {
        return this.loai;
    }

    public String layLoai() {
        return this.loai;
    }

    public String loai() {
        return this.loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public void datLoai(String loai) {
        setLoai(loai);
    }

    public String getGiong() {
        return this.giong;
    }

    public String layGiong() {
        return this.giong;
    }

    public String giong() {
        return this.giong;
    }

    public void setGiong(String giong) {
        this.giong = giong;
    }

    public void datGiong(String giong) {
        setGiong(giong);
    }

    public String getGioiTinh() {
        return this.gioiTinh;
    }

    public String layGioiTinh() {
        return this.gioiTinh;
    }

    public String gioiTinh() {
        return this.gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void datGioiTinh(String gioiTinh) {
        setGioiTinh(gioiTinh);
    }

    public String getMauLong() {
        return this.mauLong;
    }

    public String layMauLong() {
        return this.mauLong;
    }

    public String mauLong() {
        return this.mauLong;
    }

    public void setMauLong(String mauLong) {
        this.mauLong = mauLong;
    }

    public void datMauLong(String mauLong) {
        setMauLong(mauLong);
    }

    public double getCanNang() {
        return this.canNang;
    }

    public double layCanNang() {
        return this.canNang;
    }

    public double canNang() {
        return this.canNang;
    }

    public void setCanNang(double canNang) {
        if (canNang < 0)
            throw new IllegalArgumentException("Cân nặng thú cưng không được âm.");
        this.canNang = canNang;
    }

    public void datCanNang(double canNang) {
        setCanNang(canNang);
    }

    public String getTinhTrangSucKhoe() {
        return this.tinhTrangSucKhoe;
    }

    public String layTinhTrangSucKhoe() {
        return this.tinhTrangSucKhoe;
    }

    public String tinhTrangSucKhoe() {
        return this.tinhTrangSucKhoe;
    }

    public void setTinhTrangSucKhoe(String tinhTrangSucKhoe) {
        this.tinhTrangSucKhoe = tinhTrangSucKhoe;
    }

    public void datTinhTrangSucKhoe(String tinhTrangSucKhoe) {
        setTinhTrangSucKhoe(tinhTrangSucKhoe);
    }

    public String getDiUngBenhLy() {
        return this.diUngBenhLy;
    }

    public String layDiUngBenhLy() {
        return this.diUngBenhLy;
    }

    public String diUngBenhLy() {
        return this.diUngBenhLy;
    }

    public void setDiUngBenhLy(String diUngBenhLy) {
        this.diUngBenhLy = diUngBenhLy;
    }

    public void datDiUngBenhLy(String diUngBenhLy) {
        setDiUngBenhLy(diUngBenhLy);
    }

    public String getAnh() {
        return this.anh;
    }

    public String layAnh() {
        return this.anh;
    }

    public String anh() {
        return this.anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public void datAnh(String anh) {
        setAnh(anh);
    }

    public boolean isDangTheoDoi() {
        return this.dangTheoDoi;
    }

    public boolean layDangTheoDoi() {
        return this.dangTheoDoi;
    }

    public boolean dangTheoDoi() {
        return this.dangTheoDoi;
    }

    public void setDangTheoDoi(boolean dangTheoDoi) {
        this.dangTheoDoi = dangTheoDoi;
    }

    public void datDangTheoDoi(boolean dangTheoDoi) {
        setDangTheoDoi(dangTheoDoi);
    }

    public LocalDate getNgaySinh() {
        return this.ngaySinh;
    }

    public LocalDate layNgaySinh() {
        return this.ngaySinh;
    }

    public LocalDate ngaySinh() {
        return this.ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void datNgaySinh(LocalDate ngaySinh) {
        setNgaySinh(ngaySinh);
    }

    public String getTinhCach() {
        return this.tinhCach;
    }

    public String layTinhCach() {
        return this.tinhCach;
    }

    public String tinhCach() {
        return this.tinhCach;
    }

    public void setTinhCach(String tinhCach) {
        this.tinhCach = tinhCach;
    }

    public void datTinhCach(String tinhCach) {
        setTinhCach(tinhCach);
    }

    public String getTiemChung() {
        return this.tiemChung;
    }

    public String layTiemChung() {
        return this.tiemChung;
    }

    public String tiemChung() {
        return this.tiemChung;
    }

    public void setTiemChung(String tiemChung) {
        this.tiemChung = tiemChung;
    }

    public void datTiemChung(String tiemChung) {
        setTiemChung(tiemChung);
    }

    public int getSoMuiTiem() {
        return this.soMuiTiem;
    }

    public int laySoMuiTiem() {
        return this.soMuiTiem;
    }

    public int soMuiTiem() {
        return this.soMuiTiem;
    }

    public void setSoMuiTiem(int soMuiTiem) {
        if (soMuiTiem < 0)
            throw new IllegalArgumentException("Số mũi tiêm không được âm.");
        this.soMuiTiem = soMuiTiem;
    }

    public void datSoMuiTiem(int soMuiTiem) {
        setSoMuiTiem(soMuiTiem);
    }

    public LocalDate getNgayTiemGanNhat() {
        return this.ngayTiemGanNhat;
    }

    public LocalDate layNgayTiemGanNhat() {
        return this.ngayTiemGanNhat;
    }

    public LocalDate ngayTiemGanNhat() {
        return this.ngayTiemGanNhat;
    }

    public void setNgayTiemGanNhat(LocalDate ngayTiemGanNhat) {
        this.ngayTiemGanNhat = ngayTiemGanNhat;
    }

    public void datNgayTiemGanNhat(LocalDate ngayTiemGanNhat) {
        setNgayTiemGanNhat(ngayTiemGanNhat);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ThuCung that = (ThuCung) o;
        return Objects.equals(this.maThuCung, that.maThuCung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThuCung);
    }

    @Override
    public String toString() {
        return "ThuCung{" + "maThuCung='" + maThuCung + '\'' + ", khachHangId=" + getKhachHangId()
                + ", ten='" + ten + '\'' + ", loai='" + loai + '\'' + ", canNang=" + canNang + '}';
    }
}
