package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Phiếu xuất kho khớp bảng [phieu_xuat_kho].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính là số phiếu (soPhieu). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với TaiKhoan (người lập phiếu) và danh sách ChiTietPhieuXuat.
 * - Có phương thức tính tổng giá trị xuất dẫn xuất và equals/hashCode theo khóa chính soPhieu.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class PhieuXuatKho implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Phiếu xuất kho */
    private String soPhieu;
    /** Quan hệ kết hợp: Tài khoản nhân viên lập phiếu xuất kho. */
    private TaiKhoan nguoiLap;
    /** Quan hệ 1-nhiều: Danh sách dòng chi tiết xuất kho. */
    private List<ChiTietPhieuXuat> danhSachChiTiet = new ArrayList<>();

    private String loaiXuat;
    private String lyDo;
    private long tongGiaTri;
    private String trangThai;
    private LocalDateTime taoLuc;

    /** Constructor mặc định. */
    public PhieuXuatKho() {
        this.trangThai = "COMPLETED";
        this.taoLuc = LocalDateTime.now();
    }

    /** Constructor theo khóa chính nghiệp vụ soPhieu. */
    public PhieuXuatKho(String soPhieu) {
        this();
        setSoPhieu(soPhieu);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public PhieuXuatKho(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng TaiKhoan và khóa chính soPhieu. */
    public PhieuXuatKho(
            String soPhieu,
            TaiKhoan nguoiLap,
            String loaiXuat,
            String lyDo,
            String trangThai,
            LocalDateTime taoLuc) {
        setSoPhieu(soPhieu);
        setNguoiLap(nguoiLap);
        setLoaiXuat(loaiXuat);
        setLyDo(lyDo);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public PhieuXuatKho(
            long id,
            String soPhieu,
            TaiKhoan nguoiLap,
            String loaiXuat,
            String lyDo,
            String trangThai,
            LocalDateTime taoLuc) {
        this(soPhieu, nguoiLap, loaiXuat, lyDo, trangThai, taoLuc);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public PhieuXuatKho(
            long id,
            String soPhieu,
            long nguoiLapId,
            String loaiXuat,
            String lyDo,
            long tongGiaTri,
            String trangThai,
            LocalDateTime taoLuc) {
        this(id, soPhieu, new TaiKhoan(nguoiLapId), loaiXuat, lyDo, trangThai, taoLuc);
        setTongGiaTri(tongGiaTri);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính tổng giá trị xuất kho (thuộc tính dẫn xuất từ danh sách ChiTietPhieuXuat). */
    public long tinhTongGiaTri() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietPhieuXuat ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhGiaTriXuat();
                }
            }
            return tong;
        }
        return Math.max(0L, this.tongGiaTri);
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.soPhieu);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.soPhieu);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.soPhieu == null || this.soPhieu.isBlank()) && id > 0) {
            this.soPhieu = DuLieu.taoMaTuId("PX", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getSoPhieu() {
        return this.soPhieu;
    }

    public String laySoPhieu() {
        return this.soPhieu;
    }

    public String soPhieu() {
        return this.soPhieu;
    }

    public void setSoPhieu(String soPhieu) {
        this.soPhieu = soPhieu == null ? null : soPhieu.trim();
    }

    public void datSoPhieu(String soPhieu) {
        setSoPhieu(soPhieu);
    }

    public TaiKhoan getNguoiLap() {
        return this.nguoiLap;
    }

    public TaiKhoan layNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(TaiKhoan nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public void datNguoiLap(TaiKhoan nguoiLap) {
        setNguoiLap(nguoiLap);
    }

    public long getNguoiLapId() {
        return nguoiLap == null ? 0L : nguoiLap.getId();
    }

    public long layNguoiLapId() {
        return getNguoiLapId();
    }

    public long nguoiLapId() {
        return getNguoiLapId();
    }

    public void setNguoiLapId(long nguoiLapId) {
        this.nguoiLap = new TaiKhoan(nguoiLapId);
    }

    public void datNguoiLapId(long nguoiLapId) {
        setNguoiLapId(nguoiLapId);
    }

    public List<ChiTietPhieuXuat> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuXuat> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public String getLoaiXuat() {
        return this.loaiXuat;
    }

    public String layLoaiXuat() {
        return this.loaiXuat;
    }

    public String loaiXuat() {
        return this.loaiXuat;
    }

    public void setLoaiXuat(String loaiXuat) {
        this.loaiXuat = loaiXuat;
    }

    public void datLoaiXuat(String loaiXuat) {
        setLoaiXuat(loaiXuat);
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public String layLyDo() {
        return this.lyDo;
    }

    public String lyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public void datLyDo(String lyDo) {
        setLyDo(lyDo);
    }

    public long getTongGiaTri() {
        return this.tongGiaTri;
    }

    public long layTongGiaTri() {
        return this.tongGiaTri;
    }

    public long tongGiaTri() {
        return this.tongGiaTri;
    }

    public void setTongGiaTri(long tongGiaTri) {
        if (tongGiaTri < 0)
            throw new IllegalArgumentException("Tổng giá trị xuất kho không được âm.");
        this.tongGiaTri = tongGiaTri;
    }

    public void datTongGiaTri(long tongGiaTri) {
        setTongGiaTri(tongGiaTri);
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public String layTrangThai() {
        return this.trangThai;
    }

    public String trangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void datTrangThai(String trangThai) {
        setTrangThai(trangThai);
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime layTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime taoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public void datTaoLuc(LocalDateTime taoLuc) {
        setTaoLuc(taoLuc);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuXuatKho that = (PhieuXuatKho) o;
        return Objects.equals(this.soPhieu, that.soPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieu);
    }

    @Override
    public String toString() {
        return "PhieuXuatKho{" + "soPhieu='" + soPhieu + '\'' + ", nguoiLapId=" + getNguoiLapId() + ", loaiXuat='" + loaiXuat + '\'' + ", tongGiaTri=" + tinhTongGiaTri() + '}';
    }
}
