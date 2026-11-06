package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Hóa đơn bán hàng khớp bảng [hoa_don].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính chuẩn hóa là mã hóa đơn (maHoaDon / soHoaDon).
 * - Quan hệ kết hợp (Association) với đối tượng PhienLamViec, TaiKhoan, KhachHang, Voucher.
 * - Quan hệ hợp thành (Composition) 1-nhiều với danh sách ChiTietHoaDon.
 * - Các thuộc tính dẫn xuất: tamTinh, tongTien, tienThoi, diemCong được tính toán động
 *   thông qua các phương thức getter tương ứng và tuyệt đối KHÔNG có setter công khai.
 * - Equals/hashCode chỉ theo khóa chính maHoaDon.
 */
public class HoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Hóa đơn (ma_hoa_don) */
    private String maHoaDon;
    /** Quan hệ kết hợp: Hóa đơn được lập trong một Phiên làm việc / Ca thu ngân. */
    private PhienLamViec phienLamViec;
    /** Quan hệ kết hợp: Tài khoản nhân viên thu ngân lập hóa đơn. */
    private TaiKhoan taiKhoan;
    /** Quan hệ kết hợp: Khách hàng mua hàng (null nếu khách vãng lai). */
    private KhachHang khachHang;
    /** Quan hệ kết hợp: Voucher áp dụng cho hóa đơn (null nếu không dùng). */
    private Voucher voucher;
    /** Quan hệ hợp thành 1-nhiều: Danh sách các dòng chi tiết hóa đơn. */
    private List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

    private String hinhThucThanhToan;
    private long tienKhachDua;
    private String trangThai;
    private LocalDateTime taoLuc;
    private LocalDateTime thanhToanLuc;
    private String idempotencyKey;
    private long giamGia;
    private int diemDung;
    private String ghiChu;
    private String chiTietThanhToan;

    // Các giá trị snapshot lưu từ CSDL khi truy vấn mà chưa nạp danh sách ChiTietHoaDon
    private long tamTinh;
    private long tongTien;
    private long tienThoi;
    private int diemCong;

    /** Constructor mặc định. */
    public HoaDon() {
        this.trangThai = "DA_THANH_TOAN";
        this.taoLuc = LocalDateTime.now();
    }

    /** Constructor theo khóa chính nghiệp vụ maHoaDon. */
    public HoaDon(String maHoaDon) {
        this();
        setMaHoaDon(maHoaDon);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public HoaDon(long id) {
        this();
        setId(id);
    }

    /**
     * Constructor chuẩn OOP nhận các đối tượng thực thể liên kết và khóa chính maHoaDon.
     */
    public HoaDon(
            String maHoaDon,
            PhienLamViec phienLamViec,
            TaiKhoan taiKhoan,
            KhachHang khachHang,
            Voucher voucher,
            String hinhThucThanhToan,
            long tienKhachDua,
            String trangThai,
            LocalDateTime taoLuc,
            LocalDateTime thanhToanLuc,
            long giamGia,
            int diemDung,
            String ghiChu) {
        this();
        setMaHoaDon(maHoaDon);
        setPhienLamViec(phienLamViec);
        setTaiKhoan(taiKhoan);
        setKhachHang(khachHang);
        setVoucher(voucher);
        setHinhThucThanhToan(hinhThucThanhToan);
        setTienKhachDua(tienKhachDua);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setThanhToanLuc(thanhToanLuc);
        setGiamGia(giamGia);
        setDiemDung(diemDung);
        setGhiChu(ghiChu);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public HoaDon(
            long id,
            String maHoaDon,
            PhienLamViec phienLamViec,
            TaiKhoan taiKhoan,
            KhachHang khachHang,
            Voucher voucher,
            String hinhThucThanhToan,
            long tienKhachDua,
            String trangThai,
            LocalDateTime taoLuc,
            LocalDateTime thanhToanLuc,
            long giamGia,
            int diemDung,
            String ghiChu) {
        this(maHoaDon, phienLamViec, taiKhoan, khachHang, voucher, hinhThucThanhToan, tienKhachDua, trangThai, taoLuc, thanhToanLuc, giamGia, diemDung, ghiChu);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO và Service khi đọc từ CSDL. */
    public HoaDon(
            long id,
            String maHoaDon,
            Long phienLamViecId,
            Long taiKhoanId,
            Long khachHangId,
            long tongTien,
            String hinhThucThanhToan,
            long tienKhachDua,
            long tienThoi,
            int diemCong,
            String trangThai,
            LocalDateTime taoLuc,
            LocalDateTime thanhToanLuc,
            String idempotencyKey,
            long tamTinh,
            long giamGia,
            int diemDung,
            String maVoucher,
            String ghiChu,
            String chiTietThanhToan) {
        setMaHoaDon(maHoaDon);
        setId(id);
        setPhienLamViecId(phienLamViecId);
        setTaiKhoanId(taiKhoanId);
        setKhachHangId(khachHangId);
        this.tongTien = Math.max(0L, tongTien);
        setHinhThucThanhToan(hinhThucThanhToan);
        setTienKhachDua(tienKhachDua);
        this.tienThoi = Math.max(0L, tienThoi);
        this.diemCong = Math.max(0, diemCong);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setThanhToanLuc(thanhToanLuc);
        setIdempotencyKey(idempotencyKey);
        this.tamTinh = Math.max(0L, tamTinh);
        setGiamGia(giamGia);
        setDiemDung(diemDung);
        setMaVoucher(maVoucher);
        setGhiChu(ghiChu);
        setChiTietThanhToan(chiTietThanhToan);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /**
     * Tính tạm tính của hóa đơn (thuộc tính dẫn xuất bằng tổng thành tiền của danh sách ChiTietHoaDon).
     */
    public long tinhTamTinh() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietHoaDon ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhThanhTien();
                }
            }
            return tong;
        }
        return Math.max(0L, this.tamTinh);
    }

    /**
     * Thuộc tính dẫn xuất tamTinh: Không có setter công khai để đảm bảo an toàn bảo mật.
     */
    public long getTamTinh() {
        return tinhTamTinh();
    }

    public long layTamTinh() {
        return tinhTamTinh();
    }

    public long tamTinh() {
        return tinhTamTinh();
    }

    /**
     * Tính tổng tiền thanh toán cuối cùng của hóa đơn (thuộc tính dẫn xuất: max(0, tamTinh - giamGia)).
     */
    public long tinhTongTien() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            return Math.max(0L, tinhTamTinh() - giamGia);
        }
        if (this.tongTien > 0 || this.tamTinh == 0) {
            return Math.max(0L, this.tongTien);
        }
        return Math.max(0L, this.tamTinh - this.giamGia);
    }

    /**
     * Thuộc tính dẫn xuất tongTien: Không có setter công khai để đảm bảo tính an toàn dữ liệu.
     */
    public long getTongTien() {
        return tinhTongTien();
    }

    public long layTongTien() {
        return tinhTongTien();
    }

    public long tongTien() {
        return tinhTongTien();
    }

    /**
     * Tính tiền thối lại cho khách (thuộc tính dẫn xuất: max(0, tienKhachDua - tongTien)).
     */
    public long tinhTienThoi() {
        long phaiThu = tinhTongTien();
        if (tienKhachDua >= phaiThu && phaiThu > 0) {
            return tienKhachDua - phaiThu;
        }
        return Math.max(0L, this.tienThoi);
    }

    /**
     * Thuộc tính dẫn xuất tienThoi: Không có setter công khai.
     */
    public long getTienThoi() {
        return tinhTienThoi();
    }

    public long layTienThoi() {
        return tinhTienThoi();
    }

    public long tienThoi() {
        return tinhTienThoi();
    }

    /**
     * Tính điểm tích lũy cộng thêm cho khách hàng từ hóa đơn (thuộc tính dẫn xuất: 10.000đ = 1 điểm).
     */
    public int tinhDiemCong() {
        if (khachHang == null) {
            return 0;
        }
        long thanhToan = tinhTongTien();
        if (thanhToan > 0) {
            return (int) (thanhToan / 10_000L);
        }
        return Math.max(0, this.diemCong);
    }

    /**
     * Thuộc tính dẫn xuất diemCong: Không có setter công khai.
     */
    public int getDiemCong() {
        return tinhDiemCong();
    }

    public int layDiemCong() {
        return tinhDiemCong();
    }

    public int diemCong() {
        return tinhDiemCong();
    }

    /** Phương thức nội bộ ghi nhận snapshot dẫn xuất khi đọc từ CSDL */
    public void napSnapshot(long tamTinh, long tongTien, long tienThoi, int diemCong) {
        this.tamTinh = Math.max(0L, tamTinh);
        this.tongTien = Math.max(0L, tongTien);
        this.tienThoi = Math.max(0L, tienThoi);
        this.diemCong = Math.max(0, diemCong);
    }

    /** Thêm một dòng chi tiết vào hóa đơn và gắn kết hai chiều. */
    public void themChiTietHoaDon(ChiTietHoaDon chiTiet) {
        if (chiTiet == null)
            return;
        if (this.danhSachChiTiet == null) {
            this.danhSachChiTiet = new ArrayList<>();
        }
        chiTiet.setHoaDon(this);
        this.danhSachChiTiet.add(chiTiet);
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaHoaDon());
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, getMaHoaDon());
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maHoaDon == null || this.maHoaDon.isBlank()) && id > 0) {
            this.maHoaDon = DuLieu.taoMaTuId("HD", 10, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getMaHoaDon() {
        return this.maHoaDon;
    }

    public String layMaHoaDon() {
        return this.maHoaDon;
    }

    public String maHoaDon() {
        return this.maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon == null ? null : maHoaDon.trim();
    }

    public void datMaHoaDon(String maHoaDon) {
        setMaHoaDon(maHoaDon);
    }

    // Alias soHoaDon tương thích ngược
    public String getSoHoaDon() {
        return getMaHoaDon();
    }

    public String laySoHoaDon() {
        return getMaHoaDon();
    }

    public String soHoaDon() {
        return getMaHoaDon();
    }

    public void setSoHoaDon(String soHoaDon) {
        setMaHoaDon(soHoaDon);
    }

    public void datSoHoaDon(String soHoaDon) {
        setMaHoaDon(soHoaDon);
    }

    public PhienLamViec getPhienLamViec() {
        return this.phienLamViec;
    }

    public PhienLamViec layPhienLamViec() {
        return this.phienLamViec;
    }

    public void setPhienLamViec(PhienLamViec phienLamViec) {
        this.phienLamViec = phienLamViec;
    }

    public void datPhienLamViec(PhienLamViec phienLamViec) {
        setPhienLamViec(phienLamViec);
    }

    public Long getPhienLamViecId() {
        return phienLamViec == null ? null : phienLamViec.getId();
    }

    public Long layPhienLamViecId() {
        return getPhienLamViecId();
    }

    public Long phienLamViecId() {
        return getPhienLamViecId();
    }

    public void setPhienLamViecId(Long phienLamViecId) {
        this.phienLamViec = phienLamViecId == null ? null : new PhienLamViec(phienLamViecId);
    }

    public void datPhienLamViecId(Long phienLamViecId) {
        setPhienLamViecId(phienLamViecId);
    }

    public TaiKhoan getTaiKhoan() {
        return this.taiKhoan;
    }

    public TaiKhoan layTaiKhoan() {
        return this.taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public void datTaiKhoan(TaiKhoan taiKhoan) {
        setTaiKhoan(taiKhoan);
    }

    public Long getTaiKhoanId() {
        return taiKhoan == null ? null : taiKhoan.getId();
    }

    public Long layTaiKhoanId() {
        return getTaiKhoanId();
    }

    public Long taiKhoanId() {
        return getTaiKhoanId();
    }

    public void setTaiKhoanId(Long taiKhoanId) {
        this.taiKhoan = taiKhoanId == null ? null : new TaiKhoan(taiKhoanId);
    }

    public void datTaiKhoanId(Long taiKhoanId) {
        setTaiKhoanId(taiKhoanId);
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

    public Long getKhachHangId() {
        return khachHang == null ? null : khachHang.getId();
    }

    public Long layKhachHangId() {
        return getKhachHangId();
    }

    public Long khachHangId() {
        return getKhachHangId();
    }

    public void setKhachHangId(Long khachHangId) {
        this.khachHang = khachHangId == null ? null : new KhachHang(khachHangId);
    }

    public void datKhachHangId(Long khachHangId) {
        setKhachHangId(khachHangId);
    }

    public Voucher getVoucher() {
        return this.voucher;
    }

    public Voucher layVoucher() {
        return this.voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public void datVoucher(Voucher voucher) {
        setVoucher(voucher);
    }

    public String getMaVoucher() {
        return voucher == null ? null : voucher.getMaVoucher();
    }

    public String layMaVoucher() {
        return getMaVoucher();
    }

    public String maVoucher() {
        return getMaVoucher();
    }

    public void setMaVoucher(String maVoucher) {
        if (maVoucher == null || maVoucher.isBlank()) {
            this.voucher = null;
        } else if (this.voucher != null) {
            this.voucher.setMaVoucher(maVoucher);
        } else {
            this.voucher = new Voucher(maVoucher);
        }
    }

    public void datMaVoucher(String maVoucher) {
        setMaVoucher(maVoucher);
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public List<ChiTietHoaDon> layDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public String getHinhThucThanhToan() {
        return this.hinhThucThanhToan;
    }

    public String layHinhThucThanhToan() {
        return this.hinhThucThanhToan;
    }

    public String hinhThucThanhToan() {
        return this.hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public void datHinhThucThanhToan(String hinhThucThanhToan) {
        setHinhThucThanhToan(hinhThucThanhToan);
    }

    public long getTienKhachDua() {
        return this.tienKhachDua;
    }

    public long layTienKhachDua() {
        return this.tienKhachDua;
    }

    public long tienKhachDua() {
        return this.tienKhachDua;
    }

    public void setTienKhachDua(long tienKhachDua) {
        if (tienKhachDua < 0)
            throw new IllegalArgumentException("Tiền khách đưa không được âm.");
        this.tienKhachDua = tienKhachDua;
    }

    public void datTienKhachDua(long tienKhachDua) {
        setTienKhachDua(tienKhachDua);
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

    public LocalDateTime getThanhToanLuc() {
        return this.thanhToanLuc;
    }

    public LocalDateTime layThanhToanLuc() {
        return this.thanhToanLuc;
    }

    public LocalDateTime thanhToanLuc() {
        return this.thanhToanLuc;
    }

    public void setThanhToanLuc(LocalDateTime thanhToanLuc) {
        this.thanhToanLuc = thanhToanLuc;
    }

    public void datThanhToanLuc(LocalDateTime thanhToanLuc) {
        setThanhToanLuc(thanhToanLuc);
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public String layIdempotencyKey() {
        return this.idempotencyKey;
    }

    public String idempotencyKey() {
        return this.idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void datIdempotencyKey(String idempotencyKey) {
        setIdempotencyKey(idempotencyKey);
    }

    public long getGiamGia() {
        return this.giamGia;
    }

    public long layGiamGia() {
        return this.giamGia;
    }

    public long giamGia() {
        return this.giamGia;
    }

    public void setGiamGia(long giamGia) {
        if (giamGia < 0)
            throw new IllegalArgumentException("Giảm giá hóa đơn không được âm.");
        this.giamGia = giamGia;
    }

    public void datGiamGia(long giamGia) {
        setGiamGia(giamGia);
    }

    public int getDiemDung() {
        return this.diemDung;
    }

    public int layDiemDung() {
        return this.diemDung;
    }

    public int diemDung() {
        return this.diemDung;
    }

    public void setDiemDung(int diemDung) {
        if (diemDung < 0)
            throw new IllegalArgumentException("Điểm dùng không được âm.");
        this.diemDung = diemDung;
    }

    public void datDiemDung(int diemDung) {
        setDiemDung(diemDung);
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

    public String getChiTietThanhToan() {
        return this.chiTietThanhToan;
    }

    public String layChiTietThanhToan() {
        return this.chiTietThanhToan;
    }

    public String chiTietThanhToan() {
        return this.chiTietThanhToan;
    }

    public void setChiTietThanhToan(String chiTietThanhToan) {
        this.chiTietThanhToan = chiTietThanhToan;
    }

    public void datChiTietThanhToan(String chiTietThanhToan) {
        setChiTietThanhToan(chiTietThanhToan);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HoaDon that = (HoaDon) o;
        return Objects.equals(this.getMaHoaDon(), that.getMaHoaDon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaHoaDon());
    }

    @Override
    public String toString() {
        return "HoaDon{" + "maHoaDon='" + getMaHoaDon() + '\'' + ", khachHangId=" + getKhachHangId()
                + ", tamTinh=" + tinhTamTinh() + ", giamGia=" + giamGia + ", tongTien=" + tinhTongTien()
                + ", trangThai='" + trangThai + '\'' + '}';
    }
}
