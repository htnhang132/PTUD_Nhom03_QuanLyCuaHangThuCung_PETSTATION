package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Thực thể Sản phẩm ánh xạ bảng [san_pham] trong SQL Server.
 * Khóa chính: maSanPham.
 */
public class SanPham implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính (PK): Mã sản phẩm (ví dụ: SP000015). */
    private String maSanPham;
    private String barcode;
    private String tenSanPham;
    private String nhomHang;
    private String donVi;
    private BigDecimal giaBan;
    private BigDecimal giaVon;
    private Integer nguongTonThap;
    private Boolean theoDoiHsd;
    private String urlAnh;
    private String thuongHieu;
    private String quyCach;
    private String doiTuongSuDung;
    private String moTa;
    private Boolean canDonThuoc;
    private Boolean dangHoatDong;
    private LocalDateTime thoiDiemTao;
    private byte[] phienBanBanGhi;

    /** Quan hệ 1-nhiều: Một sản phẩm gồm nhiều lô hàng tồn kho. */
    private List<LoHang> danhSachLoHang = new ArrayList<>();

    /** Constructor mặc định. */
    public SanPham() {
        this.canDonThuoc = false;
        this.dangHoatDong = true;
        this.theoDoiHsd = false;
        this.nguongTonThap = 5;
        this.giaBan = BigDecimal.ZERO;
        this.giaVon = BigDecimal.ZERO;
    }

    /** Constructor chỉ có khóa chính (dùng khi tham chiếu đối tượng từ ChiTietHoaDon, LoHang...). */
    public SanPham(String maSanPham) {
        this();
        setMaSanPham(maSanPham);
    }

    /** Constructor đầy đủ các thuộc tính chính. */
    public SanPham(String maSanPham, String tenSanPham, String nhomHang, String donVi, BigDecimal giaBan) {
        this();
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setNhomHang(nhomHang);
        setDonVi(donVi);
        setGiaBan(giaBan);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public SanPham(Long id) {
        this();
        setId(id);
    }

    public SanPham(Long id, String maSanPham, String tenSanPham, BigDecimal giaBan) {
        this();
        setId(id);
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setGiaBan(giaBan);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính tổng tồn kho khả dụng (thuộc tính dẫn xuất từ danh sách các lô hàng còn hạn). */
    public int tinhTongTonKhoKhaDung() {
        if (danhSachLoHang == null || danhSachLoHang.isEmpty()) {
            return 0;
        }
        LocalDate homNay = LocalDate.now();
        int tong = 0;
        for (LoHang lo : danhSachLoHang) {
            if (lo != null
                    && "AVAILABLE".equalsIgnoreCase(lo.getTrangThai())
                    && lo.getSoLuongTon() > 0
                    && (lo.getHanSuDung() == null || !lo.getHanSuDung().isBefore(homNay))) {
                tong += lo.getSoLuongTon();
            }
        }
        return tong;
    }

    /** Kiểm tra sản phẩm có đang chạm ngưỡng cảnh báo tồn kho thấp hay không (dẫn xuất). */
    public boolean kiemTraTonKhoThap() {
        int nguong = nguongTonThap == null ? 5 : nguongTonThap;
        return tinhTongTonKhoKhaDung() <= nguong;
    }

    /** Tính lợi nhuận dự kiến trên 1 đơn vị sản phẩm (dẫn xuất: giaBan - giaVon). */
    public BigDecimal tinhLoiNhuanDonVi() {
        BigDecimal ban = giaBan == null ? BigDecimal.ZERO : giaBan;
        BigDecimal von = giaVon == null ? BigDecimal.ZERO : giaVon;
        return ban.subtract(von);
    }

    // ==================== GETTER & SETTER (CÓ KIỂM TRA RÀNG BUỘC) ====================

    public String getMaSanPham() {
        return maSanPham;
    }

    public String layMaSanPham() {
        return maSanPham;
    }

    public String maSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham == null ? null : maSanPham.trim();
    }

    public void datMaSanPham(String maSanPham) {
        setMaSanPham(maSanPham);
    }

    public String getBarcode() {
        return barcode;
    }

    public String layBarcode() {
        return barcode;
    }

    public String barcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public void datBarcode(String barcode) {
        setBarcode(barcode);
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public String layTenSanPham() {
        return tenSanPham;
    }

    public String tenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham == null ? null : tenSanPham.trim();
    }

    public void datTenSanPham(String tenSanPham) {
        setTenSanPham(tenSanPham);
    }

    public String getNhomHang() {
        return nhomHang;
    }

    public String layNhomHang() {
        return nhomHang;
    }

    public void setNhomHang(String nhomHang) {
        this.nhomHang = nhomHang;
    }

    public void datNhomHang(String nhomHang) {
        setNhomHang(nhomHang);
    }

    public Long layIdNhomHang() {
        if (nhomHang == null || nhomHang.isBlank()) return null;
        try {
            return Long.parseLong(nhomHang);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void datIdNhomHang(Long idNhomHang) {
        this.nhomHang = idNhomHang == null ? null : String.valueOf(idNhomHang);
    }

    public String getDonVi() {
        return donVi;
    }

    public String layDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public void datDonVi(String donVi) {
        setDonVi(donVi);
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public BigDecimal layGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        if (giaBan != null && giaBan.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá bán không được âm.");
        }
        this.giaBan = giaBan;
    }

    public void datGiaBan(BigDecimal giaBan) {
        setGiaBan(giaBan);
    }

    public BigDecimal getGiaVon() {
        return giaVon;
    }

    public BigDecimal layGiaVon() {
        return giaVon;
    }

    public void setGiaVon(BigDecimal giaVon) {
        if (giaVon != null && giaVon.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá vốn không được âm.");
        }
        this.giaVon = giaVon;
    }

    public void datGiaVon(BigDecimal giaVon) {
        setGiaVon(giaVon);
    }

    public Integer getNguongTonThap() {
        return nguongTonThap;
    }

    public Integer layNguongTonThap() {
        return nguongTonThap;
    }

    public void setNguongTonThap(Integer nguongTonThap) {
        if (nguongTonThap != null && nguongTonThap < 0) {
            throw new IllegalArgumentException("Ngưỡng tồn thấp không được âm.");
        }
        this.nguongTonThap = nguongTonThap;
    }

    public void datNguongTonThap(Integer nguongTonThap) {
        setNguongTonThap(nguongTonThap);
    }

    public Boolean getTheoDoiHsd() {
        return theoDoiHsd;
    }

    public Boolean layTheoDoiHsd() {
        return theoDoiHsd;
    }

    public void setTheoDoiHsd(Boolean theoDoiHsd) {
        this.theoDoiHsd = theoDoiHsd;
    }

    public void datTheoDoiHsd(Boolean theoDoiHsd) {
        setTheoDoiHsd(theoDoiHsd);
    }

    public String getUrlAnh() {
        return urlAnh;
    }

    public String layURLAnh() {
        return urlAnh;
    }

    public void setUrlAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    public void datURLAnh(String urlAnh) {
        setUrlAnh(urlAnh);
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public String layThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public void datThuongHieu(String thuongHieu) {
        setThuongHieu(thuongHieu);
    }

    public String getQuyCach() {
        return quyCach;
    }

    public String layQuyCach() {
        return quyCach;
    }

    public void setQuyCach(String quyCach) {
        this.quyCach = quyCach;
    }

    public void datQuyCach(String quyCach) {
        setQuyCach(quyCach);
    }

    public String getDoiTuongSuDung() {
        return doiTuongSuDung;
    }

    public String layDoiTuongSuDung() {
        return doiTuongSuDung;
    }

    public void setDoiTuongSuDung(String doiTuongSuDung) {
        this.doiTuongSuDung = doiTuongSuDung;
    }

    public void datDoiTuongSuDung(String doiTuongSuDung) {
        setDoiTuongSuDung(doiTuongSuDung);
    }

    public String getMoTa() {
        return moTa;
    }

    public String layMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void datMoTa(String moTa) {
        setMoTa(moTa);
    }

    public Boolean getCanDonThuoc() {
        return canDonThuoc;
    }

    public Boolean layCanDonThuoc() {
        return canDonThuoc;
    }

    public void setCanDonThuoc(Boolean canDonThuoc) {
        this.canDonThuoc = canDonThuoc;
    }

    public void datCanDonThuoc(Boolean canDonThuoc) {
        setCanDonThuoc(canDonThuoc);
    }

    public Boolean getDangHoatDong() {
        return dangHoatDong;
    }

    public Boolean layDangHoatDong() {
        return dangHoatDong;
    }

    public void setDangHoatDong(Boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    public void datDangHoatDong(Boolean dangHoatDong) {
        setDangHoatDong(dangHoatDong);
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        setThoiDiemTao(thoiDiemTao);
    }

    public byte[] getPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public void setPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        setPhienBanBanGhi(phienBanBanGhi);
    }

    public List<LoHang> getDanhSachLoHang() {
        return danhSachLoHang;
    }

    public void setDanhSachLoHang(List<LoHang> danhSachLoHang) {
        this.danhSachLoHang = danhSachLoHang == null ? new ArrayList<>() : new ArrayList<>(danhSachLoHang);
    }

    // ==================== CẦU NỐI TƯƠNG THÍCH TẦNG DAO ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, maSanPham);
    }

    public Long layId() {
        return getId();
    }

    public Long id() {
        return getId();
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("Mã định danh sản phẩm không hợp lệ.");
        }
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maSanPham == null || this.maSanPham.isBlank()) && id != null && id > 0) {
            this.maSanPham = DuLieu.taoMaTuId("SP", 6, id);
        }
    }

    public void datId(Long id) {
        setId(id);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH (maSanPham) ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham that = (SanPham) o;
        return Objects.equals(this.maSanPham, that.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }

    @Override
    public String toString() {
        return "SanPham{"
                + "maSanPham='" + maSanPham + '\''
                + ", tenSanPham='" + tenSanPham + '\''
                + ", giaBan=" + giaBan
                + ", dangHoatDong=" + dangHoatDong
                + '}';
    }
}
