package com.petstation.model;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import java.util.List;

/** Biểu diễn dữ liệu bán hàng ngữ cảnh kiểm thử dùng trong nghiệp vụ cửa hàng. */
class GioHangTest {
    @Test
    void layTamTinhSuDungThucTeCacDongVaSoLuong() {
        // Bán hàng.
        GioHang banHang = new GioHang(mock(GioHang.ChinhSach.class));
        banHang.datCacDong(
                List.of(
                        new GioHang.DongHang("P1", "Product", 285000, 2),
                        new GioHang.DongHang("P2", "Product 2", 89000, 1)));
        assertThat(banHang.tinhTamTinh()).isEqualTo(659000);
        assertThatThrownBy(() -> banHang.layCacDong().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void xoaDatLaiKhachHangVaDichVuNhungLamMoiThanhToanDinhDanh() {
        // Bán hàng.
        GioHang banHang = new GioHang(mock(GioHang.ChinhSach.class));
        // Khóa.
        String khoa = banHang.layKhoaThanhToan();
        banHang.datKhachHang("KH1", "Customer", 12);
        banHang.datNguon("SPA", 20);
        banHang.datCacDong(List.of(new GioHang.DongHang("DV1", "Service", 150000, 1)));
        banHang.xoa();
        assertThat(banHang.kiemTraTrong()).isTrue();
        assertThat(banHang.layMaKhachHang()).isEmpty();
        assertThat(banHang.layKieuNguon()).isEmpty();
        assertThat(banHang.layKhoaThanhToan()).isNotEqualTo(khoa);
    }

    @Test
    void layGiamGiaPhanTramLaCoGioiHan() {
        // Bán hàng.
        GioHang banHang = new GioHang(mock(GioHang.ChinhSach.class));
        banHang.datGiamGiaPhanTram(200);
        assertThat(banHang.layGiamGiaPhanTram()).isEqualTo(100);
        banHang.datGiamGiaPhanTram(-20);
        assertThat(banHang.layGiamGiaPhanTram()).isZero();
    }

    @Test
    void layCacDongTuChoiAmCacSoTienVaCacSoLuong() {
        assertThatThrownBy(() -> new GioHang.DongHang("P", "P", -1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GioHang.DongHang("P", "P", 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void chonKhuyenMaiTotNhatDungPhamViVaKhongVuotTongTien() {
        var chinhSach = mock(GioHang.ChinhSach.class);
        when(chinhSach.layKhuyenMai())
                .thenReturn(
                        List.of(
                                new Object[] {
                                    "KM1",
                                    "Hàng hóa",
                                    "Giảm %",
                                    "Sản phẩm",
                                    "90",
                                    "0",
                                    "01/01/2000",
                                    "31/12/2099",
                                    "Đang áp dụng"
                                },
                                new Object[] {
                                    "KM2",
                                    "Dịch vụ",
                                    "Giảm tiền",
                                    "Dịch vụ",
                                    "200000",
                                    "0",
                                    "01/01/2000",
                                    "31/12/2099",
                                    "Đang áp dụng"
                                }));
        GioHang gio = new GioHang(chinhSach);
        gio.datNguon("SPA", 1);
        gio.datCacDong(List.of(new GioHang.DongHang("DV1", "Tắm", 100000, 1)));
        assertThat(gio.layKhuyenMaiTotNhat().ma()).isEqualTo("KM2");
        assertThat(gio.tinhTong()).isZero();
    }

    @Test
    void voucherSaiKhongGhiDeVoucherDaApDung() {
        var chinhSach = mock(GioHang.ChinhSach.class);
        when(chinhSach.kiemTraVoucher("TOT", "", "", 100000)).thenReturn(20000L);
        when(chinhSach.kiemTraVoucher("SAI", "", "", 100000))
                .thenThrow(new IllegalArgumentException("Voucher hết hạn."));
        GioHang gio = new GioHang(chinhSach);
        gio.datCacDong(List.of(new GioHang.DongHang("SP1", "Vòng cổ", 100000, 1)));
        assertThat(gio.apDungVoucher(" tot ")).isNull();
        assertThat(gio.apDungVoucher("SAI")).isEqualTo("Voucher hết hạn.");
        assertThat(gio.layMaVoucher()).isEqualTo("TOT");
        assertThat(gio.laySoTienVoucher()).isEqualTo(20000);
        assertThat(gio.apDungVoucher("")).isNull();
        assertThat(gio.laySoTienVoucher()).isZero();
    }

    @Test
    void thanhToanLoiGiuNguyenNguCanhVaKhoaChongLap() {
        var chinhSach = mock(GioHang.ChinhSach.class);
        GioHang gio = new GioHang(chinhSach);
        gio.datNguon("HOTEL", 10);
        gio.layQuyTrinh().chonHotel(10);
        String khoa = gio.layKhoaThanhToan();
        when(chinhSach.thanhToan(gio, PhuongThuc.CASH, 100000, "NV1"))
                .thenThrow(new IllegalStateException("Mất kết nối."));
        assertThatThrownBy(() -> gio.hoanTat(PhuongThuc.CASH, 100000, "NV1"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(gio.layQuyTrinh().layHopDongHotel()).isEqualTo(10);
        assertThat(gio.layKhoaThanhToan()).isEqualTo(khoa);
    }
}
