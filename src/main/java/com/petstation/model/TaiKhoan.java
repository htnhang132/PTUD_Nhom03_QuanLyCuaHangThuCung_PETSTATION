package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể Tài khoản đăng nhập ánh xạ bảng [tai_khoan] trong SQL Server.
 * Khóa chính: tenDangNhap.
 * Quan hệ kết hợp 1-1 với đối tượng NhanVien.
 */
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính (PK): Tên đăng nhập. */
    private String tenDangNhap;
    /** Quan hệ kết hợp (Association): Tài khoản thuộc về một Nhân viên. */
    private NhanVien nhanVien;
    private String maBamMatKhau;
    private VaiTro vaiTro;
    private TrangThaiTaiKhoan trangThai;
    private int soLanDangNhapSai;
    private LocalDateTime khoaDenLuc;
    private boolean batBuocDoiMatKhau;

    /** Constructor mặc định. */
    public TaiKhoan() {
        this.trangThai = TrangThaiTaiKhoan.HOAT_DONG;
    }

    /** Constructor chỉ có khóa chính (dùng khi tham chiếu từ HoaDon, PhienLamViec...). */
    public TaiKhoan(String tenDangNhap) {
        this();
        setTenDangNhap(tenDangNhap);
    }

    /** Constructor chuẩn OOP nhận khóa chính tenDangNhap và đối tượng NhanVien. */
    public TaiKhoan(
            String tenDangNhap,
            NhanVien nhanVien,
            String maBamMatKhau,
            VaiTro vaiTro,
            TrangThaiTaiKhoan trangThai,
            int soLanDangNhapSai,
            LocalDateTime khoaDenLuc,
            boolean batBuocDoiMatKhau) {
        this();
        setTenDangNhap(tenDangNhap);
        setNhanVien(nhanVien);
        setMaBamMatKhau(maBamMatKhau);
        setVaiTro(vaiTro);
        setTrangThai(trangThai);
        setSoLanDangNhapSai(soLanDangNhapSai);
        setKhoaDenLuc(khoaDenLuc);
        setBatBuocDoiMatKhau(batBuocDoiMatKhau);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public TaiKhoan(long id) {
        this();
        setId(id);
    }

    public TaiKhoan(
            long id,
            NhanVien nhanVien,
            String tenDangNhap,
            String maBamMatKhau,
            VaiTro vaiTro,
            TrangThaiTaiKhoan trangThai,
            int soLanDangNhapSai,
            LocalDateTime khoaDenLuc,
            boolean batBuocDoiMatKhau) {
        this(tenDangNhap, nhanVien, maBamMatKhau, vaiTro, trangThai, soLanDangNhapSai, khoaDenLuc, batBuocDoiMatKhau);
        setId(id);
    }

    public TaiKhoan(
            long id,
            long idNhanVien,
            String tenDangNhap,
            String maBamMatKhau,
            VaiTro vaiTro,
            TrangThaiTaiKhoan trangThai,
            int soLanDangNhapSai,
            LocalDateTime khoaDenLuc,
            boolean batBuocDoiMatKhau) {
        this(
                id,
                new NhanVien(idNhanVien),
                tenDangNhap,
                maBamMatKhau,
                vaiTro,
                trangThai,
                soLanDangNhapSai,
                khoaDenLuc,
                batBuocDoiMatKhau);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Kiểm tra tài khoản có đang bị khóa tạm thời tại thời điểm xét hay không (dẫn xuất). */
    public boolean layTamThoiDaKhoa(LocalDateTime hienTai) {
        return khoaDenLuc != null && khoaDenLuc.isAfter(hienTai);
    }

    // ==================== GETTER & SETTER ====================

    public String getTenDangNhap() {
        return this.tenDangNhap;
    }

    public String layTenDangNhap() {
        return this.tenDangNhap;
    }

    public String tenDangNhap() {
        return this.tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap == null ? null : tenDangNhap.trim();
    }

    public void datTenDangNhap(String tenDangNhap) {
        setTenDangNhap(tenDangNhap);
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public NhanVien layNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public void datNhanVien(NhanVien nhanVien) {
        setNhanVien(nhanVien);
    }

    public String getMaNhanVien() {
        return nhanVien == null ? null : nhanVien.getMaNhanVien();
    }

    public void setMaNhanVien(String maNhanVien) {
        this.nhanVien = (maNhanVien == null || maNhanVien.isBlank()) ? null : new NhanVien(maNhanVien);
    }

    public String getMaBamMatKhau() {
        return this.maBamMatKhau;
    }

    public String layMaBamMatKhau() {
        return this.maBamMatKhau;
    }

    public String maBamMatKhau() {
        return this.maBamMatKhau;
    }

    public void setMaBamMatKhau(String maBamMatKhau) {
        this.maBamMatKhau = maBamMatKhau;
    }

    public void datMaBamMatKhau(String maBamMatKhau) {
        setMaBamMatKhau(maBamMatKhau);
    }

    public VaiTro getVaiTro() {
        return this.vaiTro;
    }

    public VaiTro layVaiTro() {
        return this.vaiTro;
    }

    public VaiTro vaiTro() {
        return this.vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public void datVaiTro(VaiTro vaiTro) {
        setVaiTro(vaiTro);
    }

    public TrangThaiTaiKhoan getTrangThai() {
        return this.trangThai;
    }

    public TrangThaiTaiKhoan layTrangThai() {
        return this.trangThai;
    }

    public TrangThaiTaiKhoan trangThai() {
        return this.trangThai;
    }

    public void setTrangThai(TrangThaiTaiKhoan trangThai) {
        this.trangThai = trangThai;
    }

    public void datTrangThai(TrangThaiTaiKhoan trangThai) {
        setTrangThai(trangThai);
    }

    public int getSoLanDangNhapSai() {
        return this.soLanDangNhapSai;
    }

    public int laySoLanDangNhapSai() {
        return this.soLanDangNhapSai;
    }

    public int soLanDangNhapSai() {
        return this.soLanDangNhapSai;
    }

    public void setSoLanDangNhapSai(int soLanDangNhapSai) {
        if (soLanDangNhapSai < 0)
            throw new IllegalArgumentException("Số lần đăng nhập sai không được âm.");
        this.soLanDangNhapSai = soLanDangNhapSai;
    }

    public void datSoLanDangNhapSai(int soLanDangNhapSai) {
        setSoLanDangNhapSai(soLanDangNhapSai);
    }

    public LocalDateTime getKhoaDenLuc() {
        return this.khoaDenLuc;
    }

    public LocalDateTime layKhoaDenLuc() {
        return this.khoaDenLuc;
    }

    public LocalDateTime khoaDenLuc() {
        return this.khoaDenLuc;
    }

    public void setKhoaDenLuc(LocalDateTime khoaDenLuc) {
        this.khoaDenLuc = khoaDenLuc;
    }

    public void datKhoaDenLuc(LocalDateTime khoaDenLuc) {
        setKhoaDenLuc(khoaDenLuc);
    }

    public boolean isBatBuocDoiMatKhau() {
        return this.batBuocDoiMatKhau;
    }

    public boolean layBatBuocDoiMatKhau() {
        return this.batBuocDoiMatKhau;
    }

    public boolean batBuocDoiMatKhau() {
        return this.batBuocDoiMatKhau;
    }

    public void setBatBuocDoiMatKhau(boolean batBuocDoiMatKhau) {
        this.batBuocDoiMatKhau = batBuocDoiMatKhau;
    }

    public void datBatBuocDoiMatKhau(boolean batBuocDoiMatKhau) {
        setBatBuocDoiMatKhau(batBuocDoiMatKhau);
    }

    // ==================== CẦU NỐI TƯƠNG THÍCH TẦNG DAO ====================

    public long getId() {
        return DuLieu.layIdCauNoi(this, tenDangNhap);
    }

    public long layId() {
        return getId();
    }

    public long id() {
        return getId();
    }

    public void setId(long id) {
        if (id < 0) throw new IllegalArgumentException("Mã định danh tài khoản không hợp lệ.");
        DuLieu.luuIdCauNoi(this, id);
        if ((this.tenDangNhap == null || this.tenDangNhap.isBlank()) && id > 0) {
            this.tenDangNhap = DuLieu.taoMaTuId("TK", 4, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    public long getIdNhanVien() {
        return nhanVien == null ? 0L : nhanVien.getId();
    }

    public long layIdNhanVien() {
        return getIdNhanVien();
    }

    public long idNhanVien() {
        return getIdNhanVien();
    }

    public void setIdNhanVien(long idNhanVien) {
        this.nhanVien = new NhanVien(idNhanVien);
    }

    public void datIdNhanVien(long idNhanVien) {
        setIdNhanVien(idNhanVien);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH (tenDangNhap) ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan that = (TaiKhoan) o;
        return Objects.equals(this.tenDangNhap, that.tenDangNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenDangNhap);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" + "tenDangNhap='" + tenDangNhap + '\''
                + ", nhanVien=" + (nhanVien == null ? null : nhanVien.getMaNhanVien())
                + ", vaiTro=" + vaiTro + ", trangThai=" + trangThai + '}';
    }
}
