package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class HoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maHoaDon;

    private PhienLamViec phienLamViec;

    private TaiKhoan taiKhoan;

    private KhachHang khachHang;

    private Voucher voucher;

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

    private long tamTinh;
    private long tongTien;
    private long tienThoi;
    private int diemCong;

    public HoaDon() {
        this.trangThai = "DA_THANH_TOAN";
        this.taoLuc = LocalDateTime.now();
    }

    public HoaDon(String maHoaDon) {
        this();
        setMaHoaDon(maHoaDon);
    }

    public HoaDon(long id) {
        this();
        setId(id);
    }

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

    public long getTamTinh() {
        return tinhTamTinh();
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

    public long getTongTien() {
        return tinhTongTien();
    }

    public long tinhTienThoi() {
        long phaiThu = tinhTongTien();
        if (tienKhachDua >= phaiThu && phaiThu > 0) {
            return tienKhachDua - phaiThu;
        }
        return Math.max(0L, this.tienThoi);
    }

    public long getTienThoi() {
        return tinhTienThoi();
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

    public int getDiemCong() {
        return tinhDiemCong();
    }

    public void napSnapshot(long tamTinh, long tongTien, long tienThoi, int diemCong) {
        this.tamTinh = Math.max(0L, tamTinh);
        this.tongTien = Math.max(0L, tongTien);
        this.tienThoi = Math.max(0L, tienThoi);
        this.diemCong = Math.max(0, diemCong);
    }

    public void themChiTietHoaDon(ChiTietHoaDon chiTiet) {
        if (chiTiet == null)
            return;
        if (this.danhSachChiTiet == null) {
            this.danhSachChiTiet = new ArrayList<>();
        }
        chiTiet.setHoaDon(this);
        this.danhSachChiTiet.add(chiTiet);
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaHoaDon());
    }




    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maHoaDon == null || this.maHoaDon.isBlank()) && id > 0) {
            this.maHoaDon = DuLieu.taoMaTuId("HD", 10, id);
        }
    }


    public String getMaHoaDon() {
        return this.maHoaDon;
    }



    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon == null ? null : maHoaDon.trim();
    }


    public String getSoHoaDon() {
        return getMaHoaDon();
    }


    public String soHoaDon() {
        return getMaHoaDon();
    }

    public void setSoHoaDon(String soHoaDon) {
        setMaHoaDon(soHoaDon);
    }


    public PhienLamViec getPhienLamViec() {
        return this.phienLamViec;
    }


    public void setPhienLamViec(PhienLamViec phienLamViec) {
        this.phienLamViec = phienLamViec;
    }


    public Long getPhienLamViecId() {
        return phienLamViec == null ? null : phienLamViec.getId();
    }


    public Long phienLamViecId() {
        return getPhienLamViecId();
    }

    public void setPhienLamViecId(Long phienLamViecId) {
        this.phienLamViec = phienLamViecId == null ? null : new PhienLamViec(phienLamViecId);
    }


    public TaiKhoan getTaiKhoan() {
        return this.taiKhoan;
    }


    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }


    public Long getTaiKhoanId() {
        return taiKhoan == null ? null : taiKhoan.getId();
    }


    public Long taiKhoanId() {
        return getTaiKhoanId();
    }

    public void setTaiKhoanId(Long taiKhoanId) {
        this.taiKhoan = taiKhoanId == null ? null : new TaiKhoan(taiKhoanId);
    }


    public KhachHang getKhachHang() {
        return this.khachHang;
    }


    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }


    public Long getKhachHangId() {
        return khachHang == null ? null : khachHang.getId();
    }


    public Long khachHangId() {
        return getKhachHangId();
    }

    public void setKhachHangId(Long khachHangId) {
        this.khachHang = khachHangId == null ? null : new KhachHang(khachHangId);
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


    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }


    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
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
        if (tienKhachDua < 0)
            throw new IllegalArgumentException("Tiá»n khĂ¡ch Ä‘Æ°a khĂ´ng Ä‘Æ°á»£c Ă¢m.");
        this.tienKhachDua = tienKhachDua;
    }


    public String getTrangThai() {
        return this.trangThai;
    }



    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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


    public long getGiamGia() {
        return this.giamGia;
    }



    public void setGiamGia(long giamGia) {
        if (giamGia < 0)
            throw new IllegalArgumentException("Giáº£m giĂ¡ hĂ³a Ä‘Æ¡n khĂ´ng Ä‘Æ°á»£c Ă¢m.");
        this.giamGia = giamGia;
    }


    public int getDiemDung() {
        return this.diemDung;
    }



    public void setDiemDung(int diemDung) {
        if (diemDung < 0)
            throw new IllegalArgumentException("Äiá»ƒm dĂ¹ng khĂ´ng Ä‘Æ°á»£c Ă¢m.");
        this.diemDung = diemDung;
    }


    public String getGhiChu() {
        return this.ghiChu;
    }



    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }


    public String getChiTietThanhToan() {
        return this.chiTietThanhToan;
    }



    public void setChiTietThanhToan(String chiTietThanhToan) {
        this.chiTietThanhToan = chiTietThanhToan;
    }


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
