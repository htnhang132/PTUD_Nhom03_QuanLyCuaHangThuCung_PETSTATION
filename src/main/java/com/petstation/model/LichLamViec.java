package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Thực thể Phân ca / Lịch làm việc khớp bảng [lich_lam_viec].
 * Khóa chính: maLichLamViec.
 * Quan hệ kết hợp với đối tượng NhanVien và CaLamViec.
 */
public class LichLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính (PK): Mã lịch làm việc (ví dụ: LLV000001). */
    private String maLichLamViec;
    /** Quan hệ kết hợp: Lịch làm việc của một Nhân viên. */
    private NhanVien nhanVien;
    /** Quan hệ kết hợp: Lịch làm việc thuộc một Ca làm việc. */
    private CaLamViec caLamViec;
    private LocalDate ngayLam;
    private String trangThai;
    private String loai;

    /** Constructor mặc định. */
    public LichLamViec() {
        this.trangThai = "DANG_MO";
    }

    /** Constructor chỉ có khóa chính. */
    public LichLamViec(String maLichLamViec) {
        this();
        setMaLichLamViec(maLichLamViec);
    }

    /** Constructor chuẩn OOP nhận khóa chính maLichLamViec và các đối tượng NhanVien, CaLamViec. */
    public LichLamViec(
            String maLichLamViec,
            NhanVien nhanVien,
            CaLamViec caLamViec,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this();
        setMaLichLamViec(maLichLamViec);
        setNhanVien(nhanVien);
        setCaLamViec(caLamViec);
        setNgayLam(ngayLam);
        setTrangThai(trangThai);
        setLoai(loai);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public LichLamViec(long id) {
        this();
        setId(id);
    }

    public LichLamViec(
            long id,
            NhanVien nhanVien,
            CaLamViec caLamViec,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this(DuLieu.taoMaTuId("LLV", 6, id), nhanVien, caLamViec, ngayLam, trangThai, loai);
        setId(id);
    }

    public LichLamViec(
            long id,
            long nhanVienId,
            long caLamViecId,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this(id, new NhanVien(nhanVienId), new CaLamViec(caLamViecId), ngayLam, trangThai, loai);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Kiểm tra lịch làm việc có phải trong ngày hôm nay hay không (dẫn xuất). */
    public boolean kiemTraNgayLamHomNay() {
        return ngayLam != null && ngayLam.equals(LocalDate.now());
    }

    // ==================== GETTER & SETTER ====================

    public String getMaLichLamViec() {
        if (this.maLichLamViec != null && !this.maLichLamViec.isBlank()) {
            return this.maLichLamViec;
        }
        String nv = nhanVien != null ? nhanVien.getMaNhanVien() : "";
        String ca = caLamViec != null ? caLamViec.getMaCa() : "";
        String nl = ngayLam != null ? ngayLam.toString() : "";
        return (nv.isEmpty() && ca.isEmpty() && nl.isEmpty()) ? null : (nv + "_" + nl + "_" + ca);
    }
    public String layMaLichLamViec() { return this.maLichLamViec; }
    public String maLichLamViec() { return this.maLichLamViec; }
    public void setMaLichLamViec(String maLichLamViec) {
        this.maLichLamViec = maLichLamViec == null ? null : maLichLamViec.trim();
    }
    public void datMaLichLamViec(String maLichLamViec) { setMaLichLamViec(maLichLamViec); }

    public NhanVien getNhanVien() { return this.nhanVien; }
    public NhanVien layNhanVien() { return this.nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public void datNhanVien(NhanVien nhanVien) { setNhanVien(nhanVien); }

    public CaLamViec getCaLamViec() { return this.caLamViec; }
    public CaLamViec layCaLamViec() { return this.caLamViec; }
    public void setCaLamViec(CaLamViec caLamViec) { this.caLamViec = caLamViec; }
    public void datCaLamViec(CaLamViec caLamViec) { setCaLamViec(caLamViec); }

    public LocalDate getNgayLam() { return this.ngayLam; }
    public LocalDate layNgayLam() { return this.ngayLam; }
    public LocalDate ngayLam() { return this.ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }
    public void datNgayLam(LocalDate ngayLam) { setNgayLam(ngayLam); }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { setTrangThai(trangThai); }

    public String getLoai() { return this.loai; }
    public String layLoai() { return this.loai; }
    public String loai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public void datLoai(String loai) { setLoai(loai); }

    // ==================== CẦU NỐI TƯƠNG THÍCH TẦNG DAO ====================

    public long getId() { return DuLieu.layIdCauNoi(this, maLichLamViec); }
    public long layId() { return getId(); }
    public long id() { return getId(); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maLichLamViec == null || this.maLichLamViec.isBlank()) && id > 0) {
            this.maLichLamViec = DuLieu.taoMaTuId("LLV", 6, id);
        }
    }
    public void datId(long id) { setId(id); }

    public long getNhanVienId() { return nhanVien == null ? 0L : nhanVien.getId(); }
    public long layNhanVienId() { return getNhanVienId(); }
    public long nhanVienId() { return getNhanVienId(); }
    public void setNhanVienId(long nhanVienId) { this.nhanVien = new NhanVien(nhanVienId); }
    public void datNhanVienId(long nhanVienId) { setNhanVienId(nhanVienId); }

    public long getCaLamViecId() { return caLamViec == null ? 0L : caLamViec.getId(); }
    public long layCaLamViecId() { return getCaLamViecId(); }
    public long caLamViecId() { return getCaLamViecId(); }
    public void setCaLamViecId(long caLamViecId) { this.caLamViec = new CaLamViec(caLamViecId); }
    public void datCaLamViecId(long caLamViecId) { setCaLamViecId(caLamViecId); }

    // ==================== EQUALS & HASHCODE THEO KHÓA PHỨC HỢP (nhanVien + ngayLam + caLamViec) ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichLamViec that = (LichLamViec) o;
        if (this.nhanVien != null && that.nhanVien != null
                && this.caLamViec != null && that.caLamViec != null
                && this.ngayLam != null && that.ngayLam != null) {
            return Objects.equals(this.nhanVien, that.nhanVien)
                    && Objects.equals(this.caLamViec, that.caLamViec)
                    && Objects.equals(this.ngayLam, that.ngayLam);
        }
        return Objects.equals(this.getMaLichLamViec(), that.getMaLichLamViec());
    }

    @Override
    public int hashCode() {
        if (nhanVien != null && caLamViec != null && ngayLam != null) {
            return Objects.hash(nhanVien, caLamViec, ngayLam);
        }
        return Objects.hash(getMaLichLamViec());
    }

    @Override
    public String toString() {
        return "LichLamViec{" + "maLichLamViec='" + maLichLamViec + '\''
                + ", nhanVien=" + (nhanVien == null ? null : nhanVien.getMaNhanVien())
                + ", caLamViec=" + (caLamViec == null ? null : caLamViec.getMaCa())
                + ", ngayLam=" + ngayLam + ", trangThai='" + trangThai + '\'' + '}';
    }
}
