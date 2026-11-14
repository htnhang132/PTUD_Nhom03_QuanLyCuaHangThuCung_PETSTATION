package com.petstation.service.kho;

import com.petstation.dao.kho.SanPhamDAO;
import com.petstation.dao.kho.impl.SanPhamDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.kho.SanPham;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Tầng xử lý nghiệp vụ Quản lý Sản phẩm & Barcode
public final class SanPhamService {
    private final SanPhamDAO sanPhamDAO;

    public SanPhamService() {
        this(new SanPhamDAOImpl());
    }

    public SanPhamService(SanPhamDAO sanPhamDAO) {
        this.sanPhamDAO = Objects.requireNonNull(sanPhamDAO);
    }

    public List<SanPham> layTatCa() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        return sanPhamDAO.layTatCa();
    }

    public SanPham timTheoMa(String maSanPham) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        return sanPhamDAO.timTheoMa(maSanPham);
    }

    public List<SanPham> layDanhSachSanPham() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        return sanPhamDAO.layDanhSachSanPham();
    }

    public void capNhatBarcode(String maSanPham, String barcode) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException("Mã barcode không được để trống.");
        }
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (sanPhamDAO.capNhatBarcode(maSanPham, barcode.trim()) != 1) {
                                throw new IllegalArgumentException(
                                        "Không tìm thấy sản phẩm để cập nhật mã vạch.");
                            }
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Cập nhật Barcode", maSanPham + " → " + barcode);
                            return null;
                        });
    }

    public static boolean laBarcodeHopLe(String barcode) {
        return barcode != null && barcode.matches("\\d{13}");
    }

    public static String taoBarcodeEan13(String maSp) {
        String digits = maSp == null ? "" : maSp.replaceAll("\\D", "");
        long num = 1L;
        if (!digits.isEmpty()) {
            try {
                num = Long.parseLong(digits);
            } catch (NumberFormatException ignored) {
                num = Math.abs(maSp.hashCode() % 1000000000L);
            }
        }
        String base12 = "893" + String.format(Locale.ROOT, "%09d", num % 1000000000L);
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = base12.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return base12 + checkDigit;
    }

    public static String kiemTraThongTinSanPham(
            long giaBan, long giaNhap, String barcode, boolean laQuanLy) {
        if (laQuanLy && giaBan <= 0) {
            return "Giá bán phải lớn hơn 0.";
        }
        if (giaBan > 0 && giaNhap > 0 && giaBan < giaNhap) {
            return "Giá bán ("
                    + DinhDang.dinhDang(giaBan)
                    + ") không được thấp hơn giá nhập gần nhất ("
                    + DinhDang.dinhDang(giaNhap)
                    + ").";
        }
        String maVach = barcode == null ? "" : barcode.trim();
        if (!maVach.isBlank() && !laBarcodeHopLe(maVach)) {
            return "Mã vạch (Barcode) phải gồm đúng 13 chữ số chuẩn EAN-13.";
        }
        return null;
    }
}
