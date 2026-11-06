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
 * - Khóa chính chuẩn hóa là mã phiếu nhập (maPhieuNhap / soPhieu).
 * - Quan hệ kết hợp với NhaCungCap, TaiKhoan (người lập phiếu) và danh sách ChiTietPhieuNhap.
 * - Thuộc tính dẫn xuất: tongTien, congNoConLai, kiemTraDaTatToanCongNo() được tính toán động
 *   tuyệt đối KHÔNG có setter công khai.
 * - Equals/hashCode chỉ theo khóa chính.
 */
public class PhieuNhapKho implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Phiếu nhập kho (ma_phieu_nhap) */
    private String maPhieuNhap;
    /** Quan hệ kết hợp: Nhà cung cấp giao hàng. */
    private NhaCungCap nhaCungCap;
    /** Quan hệ kết hợp: Tài khoản nhân viên lập phiếu nhập kho. */
    private TaiKhoan nguoiLap;
    /** Quan hệ hợp thành 1-nhiều: Danh sách dòng chi tiết phiếu nhập. */
    private List<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<>();

    private LocalDateTime ngayNhap;
    private long tongTien; // snapshot từ CSDL khi chưa load danh sách chi tiết
    private String trangThai;
    private String ghiChu;
    private long daTra;

    /** Constructor mặc định. */
    public PhieuNhapKho() {
        this.ngayNhap = LocalDateTime.now();
        this.trangThai = "DA_NHAP";
    }

    /** Constructor theo khóa chính nghiệp vụ maPhieuNhap. */
    public PhieuNhapKho(String maPhieuNhap) {
        this();
        setMaPhieuNhap(maPhieuNhap);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public PhieuNhapKho(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng NhaCungCap, TaiKhoan và khóa chính maPhieuNhap. */
    public PhieuNhapKho(
            String maPhieuNhap,
            NhaCungCap nhaCungCap,
            TaiKhoan nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            String ghiChu,
            long daTra) {
        setMaPhieuNhap(maPhieuNhap);
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
            String maPhieuNhap,
            NhaCungCap nhaCungCap,
            TaiKhoan nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            String ghiChu,
            long daTra) {
        this(maPhieuNhap, nhaCungCap, nguoiLap, ngayNhap, trangThai, ghiChu, daTra);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO (có tham số tongTien snapshot). */
    public PhieuNhapKho(
            long id,
            String maPhieuNhap,
            long nhaCungCapId,
            long nguoiLapId,
            LocalDateTime ngayNhap,
            long tongTien,
            String trangThai,
            String ghiChu,
            long daTra) {
        this(
                id,
                maPhieuNhap,
                new NhaCungCap(nhaCungCapId),
                new TaiKhoan(nguoiLapId),
                ngayNhap,
                trangThai,
                ghiChu,
                daTra);
        this.tongTien = Math.max(0L, tongTien);
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

    /** Thuộc tính dẫn xuất: Không có setter công khai để đảm bảo tính an toàn dữ liệu. */
    public long getTongTien() {
        return tinhTongTien();
    }

    public long layTongTien() {
        return tinhTongTien();
    }

    public long tongTien() {
        return tinhTongTien();
    }

    /** Phương thức nội bộ gán snapshot tổng tiền từ CSDL. */
    public void napTongTienSnapshot(long tongTien) {
        this.tongTien = Math.max(0L, tongTien);
    }

    /**
     * Tính công nợ còn lại phải trả cho nhà cung cấp (thuộc tính dẫn xuất: tongTien - daTra).
     */
    public long tinhCongNoConLai() {
        return Math.max(0L, tinhTongTien() - daTra);
    }

    public long getCongNoConLai() {
        return tinhCongNoConLai();
    }

    public long layCongNoConLai() {
        return tinhCongNoConLai();
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
        return DuLieu.layIdCauNoiObj(this, getMaPhieuNhap());
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, getMaPhieuNhap());
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieuNhap == null || this.maPhieuNhap.isBlank()) && id > 0) {
            this.maPhieuNhap = DuLieu.taoMaTuId("PN", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getMaPhieuNhap() {
        return this.maPhieuNhap;
    }

    public String layMaPhieuNhap() {
        return this.maPhieuNhap;
    }

    public String maPhieuNhap() {
        return this.maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap == null ? null : maPhieuNhap.trim();
    }

    public void datMaPhieuNhap(String maPhieuNhap) {
        setMaPhieuNhap(maPhieuNhap);
    }

    // Alias soPhieu tương thích ngược
    public String getSoPhieu() {
        return getMaPhieuNhap();
    }

    public String laySoPhieu() {
        return getMaPhieuNhap();
    }

    public String soPhieu() {
        return getMaPhieuNhap();
    }

    public void setSoPhieu(String soPhieu) {
        setMaPhieuNhap(soPhieu);
    }

    public void datSoPhieu(String soPhieu) {
        setMaPhieuNhap(soPhieu);
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

    public String getMaNhaCungCap() {
        return nhaCungCap == null ? null : nhaCungCap.getMaNhaCungCap();
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.nhaCungCap = (maNhaCungCap == null || maNhaCungCap.isBlank()) ? null : new NhaCungCap(maNhaCungCap);
    }

    public String getMaNcc() {
        return getMaNhaCungCap();
    }

    public void setMaNcc(String maNcc) {
        setMaNhaCungCap(maNcc);
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
        return Objects.equals(this.getMaPhieuNhap(), that.getMaPhieuNhap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaPhieuNhap());
    }

    @Override
    public String toString() {
        return "PhieuNhapKho{" + "maPhieuNhap='" + getMaPhieuNhap() + '\'' + ", nhaCungCap=" + getMaNhaCungCap()
                + ", tongTien=" + tinhTongTien() + ", congNoConLai=" + tinhCongNoConLai() + ", trangThai='" + trangThai
                + '\'' + '}';
    }
}
