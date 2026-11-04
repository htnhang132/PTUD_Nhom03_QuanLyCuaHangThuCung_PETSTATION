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
 * - Khóa chính là số hóa đơn (soHoaDon). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp (Association) với đối tượng PhienLamViec, TaiKhoan, KhachHang, Voucher.
 * - Quan hệ hợp thành (Composition) 1-nhiều với danh sách ChiTietHoaDon.
 * - Phương thức tính các thuộc tính dẫn xuất: tinhTamTinh(), tinhTongTien(), tinhTienThoi(), tinhDiemCong().
 * - Kiểm tra ràng buộc dữ liệu trong setter và equals/hashCode chỉ theo khóa chính soHoaDon.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class HoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Hóa đơn */
    private String soHoaDon;
    /** Quan hệ kết hợp: Hóa đơn được lập trong một Phiên làm việc / Ca thu ngân. */
    private PhienLamViec phienLamViec;
    /** Quan hệ kết hợp: Tài khoản nhân viên thu ngân lập hóa đơn. */
    private TaiKhoan taiKhoan;
    /** Quan hệ kết hợp: Khách hàng mua hàng (null nếu khách vãng lai). */
    private KhachHang khachHang;
    /** Quan hệ kết hợp: Voucher áp dụng cho hóa đơn (null nếu không dùng). */
    private Voucher voucher;
    /** Quan hệ 1-nhiều: Danh sách các dòng chi tiết hóa đơn. */
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

    // Các giá trị snapshot từ CSDL khi chưa nạp danh sách ChiTietHoaDon
    private long tamTinh;
    private long tongTien;
    private long tienThoi;
    private int diemCong;

    /** Constructor mặc định. */
    public HoaDon() {
        this.trangThai = "PAID";
        this.taoLuc = LocalDateTime.now();
    }

    /** Constructor theo khóa chính nghiệp vụ soHoaDon. */
    public HoaDon(String soHoaDon) {
        this();
        setSoHoaDon(soHoaDon);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public HoaDon(long id) {
        this();
        setId(id);
    }

    /**
     * Constructor chuẩn OOP nhận các đối tượng thực thể liên kết và khóa chính soHoaDon.
     */
    public HoaDon(
            String soHoaDon,
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
        setSoHoaDon(soHoaDon);
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
            String soHoaDon,
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
        this(soHoaDon, phienLamViec, taiKhoan, khachHang, voucher, hinhThucThanhToan, tienKhachDua, trangThai, taoLuc, thanhToanLuc, giamGia, diemDung, ghiChu);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO và Service. */
    public HoaDon(
            long id,
            String soHoaDon,
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
        setSoHoaDon(soHoaDon);
        setId(id);
        setPhienLamViecId(phienLamViecId);
        setTaiKhoanId(taiKhoanId);
        setKhachHangId(khachHangId);
        setTongTien(tongTien);
        setHinhThucThanhToan(hinhThucThanhToan);
        setTienKhachDua(tienKhachDua);
        setTienThoi(tienThoi);
        setDiemCong(diemCong);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setThanhToanLuc(thanhToanLuc);
        setIdempotencyKey(idempotencyKey);
        setTamTinh(tamTinh);
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
     * Tính tổng tiền thanh toán cuối cùng của hóa đơn (thuộc tính dẫn xuất: tamTinh - giamGia).
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
        return DuLieu.layIdCauNoiObj(this, this.soHoaDon);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.soHoaDon);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.soHoaDon == null || this.soHoaDon.isBlank()) && id > 0) {
            this.soHoaDon = DuLieu.taoMaTuId("HD", 10, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getSoHoaDon() {
        return this.soHoaDon;
    }

    public String laySoHoaDon() {
        return this.soHoaDon;
    }

    public String soHoaDon() {
        return this.soHoaDon;
    }

    public void setSoHoaDon(String soHoaDon) {
        this.soHoaDon = soHoaDon == null ? null : soHoaDon.trim();
    }

    public void datSoHoaDon(String soHoaDon) {
        setSoHoaDon(soHoaDon);
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
            throw new IllegalArgumentException("Tổng tiền hóa đơn không được âm.");
        this.tongTien = tongTien;
    }

    public void datTongTien(long tongTien) {
        setTongTien(tongTien);
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

    public long getTienThoi() {
        return this.tienThoi;
    }

    public long layTienThoi() {
        return this.tienThoi;
    }

    public long tienThoi() {
        return this.tienThoi;
    }

    public void setTienThoi(long tienThoi) {
        if (tienThoi < 0)
            throw new IllegalArgumentException("Tiền thối không được âm.");
        this.tienThoi = tienThoi;
    }

    public void datTienThoi(long tienThoi) {
        setTienThoi(tienThoi);
    }

    public int getDiemCong() {
        return this.diemCong;
    }

    public int layDiemCong() {
        return this.diemCong;
    }

    public int diemCong() {
        return this.diemCong;
    }

    public void setDiemCong(int diemCong) {
        if (diemCong < 0)
            throw new IllegalArgumentException("Điểm cộng không được âm.");
        this.diemCong = diemCong;
    }

    public void datDiemCong(int diemCong) {
        setDiemCong(diemCong);
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

    public long getTamTinh() {
        return this.tamTinh;
    }

    public long layTamTinh() {
        return this.tamTinh;
    }

    public long tamTinh() {
        return this.tamTinh;
    }

    public void setTamTinh(long tamTinh) {
        if (tamTinh < 0)
            throw new IllegalArgumentException("Tạm tính không được âm.");
        this.tamTinh = tamTinh;
    }

    public void datTamTinh(long tamTinh) {
        setTamTinh(tamTinh);
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
        return Objects.equals(this.soHoaDon, that.soHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" + "soHoaDon='" + soHoaDon + '\'' + ", khachHangId=" + getKhachHangId()
                + ", tamTinh=" + tinhTamTinh() + ", giamGia=" + giamGia + ", tongTien=" + tinhTongTien()
                + ", trangThai='" + trangThai + '\'' + '}';
    }
}
