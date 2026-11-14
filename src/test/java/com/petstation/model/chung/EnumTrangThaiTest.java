package com.petstation.model.chung;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnumTrangThaiTest {

    @Test
    @DisplayName("Kiểm tra 23 Enum trạng thái nghiệp vụ: maDB, tuMaDB, tenHienThi")
    void testTatCa23EnumTrangThai() {
        // 1. TrangThaiHoaDon
        assertEquals(TrangThaiHoaDon.DA_THANH_TOAN, TrangThaiHoaDon.tuMaDB("DA_THANH_TOAN"));
        assertEquals("Đã thanh toán", TrangThaiHoaDon.DA_THANH_TOAN.tenHienThi());
        assertNull(TrangThaiHoaDon.tuMaDB(null));

        // 2. TrangThaiPhienLamViec
        assertEquals(TrangThaiPhienLamViec.DANG_MO, TrangThaiPhienLamViec.tuMaDB("DANG_MO"));
        assertEquals("Đang mở", TrangThaiPhienLamViec.DANG_MO.tenHienThi());

        // 3. TrangThaiPhieuDoiTra
        assertEquals(TrangThaiPhieuDoiTra.HOAN_TAT, TrangThaiPhieuDoiTra.tuMaDB("HOAN_TAT"));

        // 5. LoaiPhieuDoiTra
        assertEquals(LoaiPhieuDoiTra.DOI_HANG, LoaiPhieuDoiTra.tuMaDB("DOI_HANG"));

        // 6. TrangThaiSanPham
        assertEquals(TrangThaiSanPham.DANG_BAN, TrangThaiSanPham.tuMaDB("DANG_BAN"));
        assertEquals(TrangThaiSanPham.NGUNG_BAN, TrangThaiSanPham.tuMaDB("NGUNG_BAN"));

        // 7. TrangThaiLoHang
        assertEquals(TrangThaiLoHang.SAN_SANG, TrangThaiLoHang.tuMaDB("SAN_SANG"));
        assertEquals(TrangThaiLoHang.HET_HAN, TrangThaiLoHang.tuMaDB("HET_HAN"));

        // 8. TrangThaiPhieuNhap
        assertEquals(TrangThaiPhieuNhap.DA_NHAP, TrangThaiPhieuNhap.tuMaDB("DA_NHAP"));

        // 9. TrangThaiPhieuXuat
        assertEquals(TrangThaiPhieuXuat.DA_XUAT, TrangThaiPhieuXuat.tuMaDB("DA_XUAT"));

        // 10. LoaiPhieuXuat
        assertEquals(LoaiPhieuXuat.XUAT_HUY, LoaiPhieuXuat.tuMaDB("XUAT_HUY"));

        // 11. TrangThaiBienBanKiemKe
        assertEquals(TrangThaiBienBanKiemKe.DA_DUYET, TrangThaiBienBanKiemKe.tuMaDB("DA_DUYET"));

        // 12. TrangThaiNhaCungCap
        assertEquals(TrangThaiNhaCungCap.DANG_HOP_TAC, TrangThaiNhaCungCap.tuMaDB("DANG_HOP_TAC"));

        // 13. TrangThaiHopDong
        assertEquals(TrangThaiHopDong.DANG_O, TrangThaiHopDong.tuMaDB("DANG_O"));
        assertEquals(TrangThaiHopDong.DA_DAT, TrangThaiHopDong.tuMaDB("DA_DAT"));

        // 14. TrangThaiPhongHotel
        assertEquals(TrangThaiPhongHotel.TRONG, TrangThaiPhongHotel.tuMaDB("TRONG"));
        assertEquals(TrangThaiPhongHotel.DANG_O, TrangThaiPhongHotel.tuMaDB("DANG_O"));

        // 15. TrangThaiPhieuSpa
        assertEquals(TrangThaiPhieuSpa.HOAN_TAT, TrangThaiPhieuSpa.tuMaDB("HOAN_TAT"));
        assertEquals(TrangThaiPhieuSpa.TIEP_NHAN, TrangThaiPhieuSpa.tuMaDB("TIEP_NHAN"));

        // 16. TrangThaiLichHen
        assertEquals(TrangThaiLichHen.DA_XAC_NHAN, TrangThaiLichHen.tuMaDB("DA_XAC_NHAN"));

        // 17. TrangThaiVoucher
        assertEquals(TrangThaiVoucher.CON_HAN, TrangThaiVoucher.tuMaDB("CON_HAN"));

        // 17. TrangThaiKhuyenMai
        assertEquals(TrangThaiKhuyenMai.HOAT_DONG, TrangThaiKhuyenMai.tuMaDB("HOAT_DONG"));

        // 18. TrangThaiLichLamViec
        assertEquals(TrangThaiLichLamViec.DA_KHOA, TrangThaiLichLamViec.tuMaDB("DA_KHOA"));

        // 19. LoaiLichLamViec
        assertEquals(LoaiLichLamViec.DI_LAM, LoaiLichLamViec.tuMaDB("DI_LAM"));

        // 20. TrangThaiPheDuyet
        assertEquals(TrangThaiPheDuyet.CHO_DUYET, TrangThaiPheDuyet.tuMaDB("CHO_DUYET"));
        assertEquals(TrangThaiPheDuyet.DA_DUYET, TrangThaiPheDuyet.tuMaDB("DA_DUYET"));
    }
}
