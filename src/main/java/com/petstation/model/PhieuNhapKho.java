package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Phiếu nhập kho khớp bảng [phieu_nhap_kho].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính là số phiếu (soPhieu). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với NhaCungCap, TaiKhoan (người lập phiếu) và danh sách ChiTietPhieuNhap.
 * - Phương thức tính thuộc tính dẫn xuất: tinhTongTien(), tinhCongNoConLai(), kiemTraDaTatToanCongNo().
 * - Kiểm tra ràng buộc trong setter và equals/hashCode chỉ theo khóa chính soPhieu.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class PhieuNhapKho implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Phiếu nhập kho */
    private String soPhieu;
    /** Quan hệ kết hợp: Nhà cung cấp giao hàng. */
    private NhaCungCap nhaCungCap;
    /** Quan hệ kết hợp: Tài khoản nhân viên lập phiếu nhập kho. */
    private TaiKhoan nguoiLap;
    /** Quan hệ hợp thành 1-nhiều: Danh sách dòng chi tiết phiếu nhập. */
    private List<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<>();

    private LocalDateTime ngayNhap;
    private long tongTien;
    private String trangThai;
    private String ghiChu;
    private long daTra;

    /** Constructor mặc định. */
    public PhieuNhapKho() {
        this.ngayNhap = LocalDateTime.now();
        this.trangThai = "COMPLETED";
    }

    /** Constructor theo khóa chính nghiệp vụ soPhieu. */
    public PhieuNhapKho(String soPhieu) {
        this();
        setSoPhieu(soPhieu);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public PhieuNhapKho(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng NhaCungCap, TaiKhoan và khóa chính soPhieu. */
    public PhieuNhapKho(
            String soPhieu,
            NhaCungCap nhaCungCap,
            TaiKhoan nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            String ghiChu,
            long daTra) {
        setSoPhieu(soPhieu);
        setNhaCungCap(nhaCungCap);
        setNguoiLap(nguoiLap);
        setNgayNhap(ngayNhap);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
        setDaTra(daTra);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public PhieuNhapKho(
            long id,
            String soPhieu,
            NhaCungCap nhaCungCap,
            TaiKhoan nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            String ghiChu,
            long daTra) {
        this(soPhieu, nhaCungCap, nguoiLap, ngayNhap, trangThai, ghiChu, daTra);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public PhieuNhapKho(
            long id,
            String soPhieu,
            long nhaCungCapId,
            long nguoiLapId,
            LocalDateTime ngayNhap,
            long tongTien,
            String trangThai,
            String ghiChu,
            long daTra) {
        this(
                id,
                soPhieu,
                new NhaCungCap(nhaCungCapId),
                new TaiKhoan(nguoiLapId),
                ngayNhap,
                trangThai,
                ghiChu,
                daTra);
        setTongTien(tongTien);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /**
     * Tính tổng tiền phiếu nhập kho (thuộc tính dẫn xuất từ danh sách ChiTietPhieuNhap).
     */
    public long tinhTongTien() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietPhieuNhap ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhThanhTien();
                }
            }
            return tong;
        }
        return Math.max(0L, this.tongTien);
    }

    /**
     * Tính công nợ còn lại phải trả cho nhà cung cấp (thuộc tính dẫn xuất: tongTien - daTra).
     */
    public long tinhCongNoConLai() {
        return Math.max(0L, tinhTongTien() - daTra);
    }

    /**
     * Kiểm tra phiếu nhập đã thanh toán hết công nợ cho nhà cung cấp hay chưa (dẫn xuất).
     */
    public boolean kiemTraDaTatToanCongNo() {
        return tinhCongNoConLai() == 0L;
    }

    /** Thêm dòng chi tiết vào phiếu nhập kho. */
    public void themChiTietPhieuNhap(ChiTietPhieuNhap chiTiet) {
        if (chiTiet == null)
            return;
        if (this.danhSachChiTiet == null) {
            this.danhSachChiTiet = new ArrayList<>();
        }
        chiTiet.setPhieuNhap(this);
        this.danhSachChiTiet.add(chiTiet);
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
            this.soPhieu = DuLieu.taoMaTuId("PN", 8, id);
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

    public NhaCungCap getNhaCungCap() {
        return this.nhaCungCap;
    }

    public NhaCungCap layNhaCungCap() {
        return this.nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public void datNhaCungCap(NhaCungCap nhaCungCap) {
        setNhaCungCap(nhaCungCap);
    }

    public long getNhaCungCapId() {
        return nhaCungCap == null ? 0L : nhaCungCap.getId();
    }

    public long layNhaCungCapId() {
        return getNhaCungCapId();
    }

    public long nhaCungCapId() {
        return getNhaCungCapId();
    }

    public void setNhaCungCapId(long nhaCungCapId) {
        this.nhaCungCap = new NhaCungCap(nhaCungCapId);
    }

    public void datNhaCungCapId(long nhaCungCapId) {
        setNhaCungCapId(nhaCungCapId);
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

    public List<ChiTietPhieuNhap> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuNhap> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public LocalDateTime getNgayNhap() {
        return this.ngayNhap;
    }

    public LocalDateTime layNgayNhap() {
        return this.ngayNhap;
    }

    public LocalDateTime ngayNhap() {
        return this.ngayNhap;
    }

    public void setNgayNhap(LocalDateTime ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public void datNgayNhap(LocalDateTime ngayNhap) {
        setNgayNhap(ngayNhap);
    }

    public long getTongTien() {
        return this.tongTien;
    }

    public long layTongTien() {
        return this.tongTien;
    }

    public long tongTien() {
        return this.tongTien;
    }

    public void setTongTien(long tongTien) {
        if (tongTien < 0)
            throw new IllegalArgumentException("Tổng tiền phiếu nhập không được âm.");
        this.tongTien = tongTien;
    }

    public void datTongTien(long tongTien) {
        setTongTien(tongTien);
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

    public String getGhiChu() {
        return this.ghiChu;
    }

    public String layGhiChu() {
        return this.ghiChu;
    }

    public String ghiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public void datGhiChu(String ghiChu) {
        setGhiChu(ghiChu);
    }

    public long getDaTra() {
        return this.daTra;
    }

    public long layDaTra() {
        return this.daTra;
    }

    public long daTra() {
        return this.daTra;
    }

    public void setDaTra(long daTra) {
        if (daTra < 0)
            throw new IllegalArgumentException("Số tiền đã trả không được âm.");
        this.daTra = daTra;
    }

    public void datDaTra(long daTra) {
        setDaTra(daTra);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuNhapKho that = (PhieuNhapKho) o;
        return Objects.equals(this.soPhieu, that.soPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieu);
    }

    @Override
    public String toString() {
        return "PhieuNhapKho{" + "soPhieu='" + soPhieu + '\'' + ", nhaCungCapId=" + getNhaCungCapId()
                + ", tongTien=" + tinhTongTien() + ", congNoConLai=" + tinhCongNoConLai() + ", trangThai='" + trangThai
                + '\'' + '}';
    }
}
