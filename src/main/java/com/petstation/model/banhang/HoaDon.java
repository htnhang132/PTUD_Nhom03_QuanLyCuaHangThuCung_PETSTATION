package com.petstation.model.banhang;

import com.petstation.model.chung.TrangThaiHoaDon;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.Voucher;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoaDon implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maHoaDon;
    private PhienLamViec phienLamViec;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private Voucher voucher;
    private List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
    private String hinhThucThanhToan;
    private long tienKhachDua;
    private long giamGia;
    private int diemDung;
    private String trangThai;
    private LocalDateTime taoLuc;
    private LocalDateTime thanhToanLuc;
    private String idempotencyKey;
    private String ghiChu;
    private long tamTinh;
    private long tongTien;
    private long tienThoi;
    private int diemCong;

    public HoaDon() {
        this.trangThai = TrangThaiHoaDon.DA_THANH_TOAN.maDB();
        this.taoLuc = LocalDateTime.now();
    }

    public HoaDon(String maHoaDon) {
        this();
        setMaHoaDon(maHoaDon);
    }

    public HoaDon(
            String maHoaDon,
            PhienLamViec phienLamViec,
            NhanVien nhanVien,
            KhachHang khachHang,
            Voucher voucher,
            String hinhThucThanhToan,
            long tienKhachDua,
            long giamGia,
            int diemDung,
            String trangThai,
            LocalDateTime taoLuc,
            LocalDateTime thanhToanLuc,
            String ghiChu) {
        this();
        setMaHoaDon(maHoaDon);
        setPhienLamViec(phienLamViec);
        setNhanVien(nhanVien);
        setKhachHang(khachHang);
        setVoucher(voucher);
        setHinhThucThanhToan(hinhThucThanhToan);
        setTienKhachDua(tienKhachDua);
        setGiamGia(giamGia);
        setDiemDung(diemDung);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setThanhToanLuc(thanhToanLuc);
        setGhiChu(ghiChu);
    }

    public String getMaHoaDon() {
        return this.maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon == null ? null : maHoaDon.trim();
    }

    public PhienLamViec getPhienLamViec() {
        return this.phienLamViec;
    }

    public void setPhienLamViec(PhienLamViec phienLamViec) {
        this.phienLamViec = phienLamViec;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public Voucher getVoucher() {
        return this.voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getMaVoucher() {
        return voucher == null ? null : voucher.getMaVoucher();
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

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public String getHinhThucThanhToan() {
        return this.hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public long getTienKhachDua() {
        return this.tienKhachDua;
    }

    public void setTienKhachDua(long tienKhachDua) {
        if (tienKhachDua < 0) throw new IllegalArgumentException("Tiền khách đưa không được âm.");
        this.tienKhachDua = tienKhachDua;
    }

    public long getGiamGia() {
        return this.giamGia;
    }

    public void setGiamGia(long giamGia) {
        if (giamGia < 0) throw new IllegalArgumentException("Giảm giá hóa đơn không được âm.");
        this.giamGia = giamGia;
    }

    public int getDiemDung() {
        return this.diemDung;
    }

    public void setDiemDung(int diemDung) {
        if (diemDung < 0) throw new IllegalArgumentException("Điểm dùng không được âm.");
        this.diemDung = diemDung;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public TrangThaiHoaDon getTrangThaiEnum() {
        return TrangThaiHoaDon.tuMaDB(trangThai);
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public LocalDateTime getThanhToanLuc() {
        return this.thanhToanLuc;
    }

    public void setThanhToanLuc(LocalDateTime thanhToanLuc) {
        this.thanhToanLuc = thanhToanLuc;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public long getTamTinh() {
        return tinhTamTinh();
    }

    public long getTongTien() {
        return tinhTongTien();
    }

    public long getTienThoi() {
        return this.tienThoi;
    }

    public long getTienThoiSnapshot() {
        return this.tienThoi;
    }

    public int getDiemCong() {
        return this.diemCong;
    }

    public int getDiemCongSnapshot() {
        return this.diemCong;
    }

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

    public long tinhTongTien() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            return Math.max(0L, tinhTamTinh() - giamGia);
        }
        if (this.tongTien > 0 || this.tamTinh == 0) {
            return Math.max(0L, this.tongTien);
        }
        return Math.max(0L, this.tamTinh - this.giamGia);
    }

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

    public void napSnapshot(long tamTinh, long tongTien, long tienThoi, int diemCong) {
        this.tamTinh = Math.max(0L, tamTinh);
        this.tongTien = Math.max(0L, tongTien);
        this.tienThoi = Math.max(0L, tienThoi);
        this.diemCong = Math.max(0, diemCong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon that = (HoaDon) o;
        return Objects.equals(this.getMaHoaDon(), that.getMaHoaDon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaHoaDon());
    }

    @Override
    public String toString() {
        return "HoaDon{"
                + "maHoaDon='"
                + getMaHoaDon()
                + '\''
                + ", khachHang="
                + (khachHang != null ? khachHang.getMaKhachHang() : null)
                + ", tamTinh="
                + tinhTamTinh()
                + ", giamGia="
                + giamGia
                + ", tongTien="
                + tinhTongTien()
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
