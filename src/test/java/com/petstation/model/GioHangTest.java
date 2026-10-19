package com.petstation.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

/** Biểu diễn dữ liệu bán hàng ngữ cảnh kiểm thử dùng trong nghiệp vụ cửa hàng. */
class GioHangTest {
    /** Lấy tạm tính sử dụng thực tế các dòng và số lượng trong GioHangTest. */
    @Test
    void layTamTinhSuDungThucTeCacDongVaSoLuong() {
        // Bán hàng.
        GioHang banHang = new GioHang();
        banHang.datCacDong(
                List.of(
                        new GioHang.DongHang("P1", "Product", 285000, 2),
                        new GioHang.DongHang("P2", "Product 2", 89000, 1)));
        assertThat(banHang.tinhTamTinh()).isEqualTo(659000);
        assertThatThrownBy(() -> banHang.layCacDong().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    /** Xóa đặt lại khách hàng và dịch vụ nhưng làm mới thanh toán định danh trong GioHangTest. */
    @Test
    void xoaDatLaiKhachHangVaDichVuNhungLamMoiThanhToanDinhDanh() {
        // Bán hàng.
        GioHang banHang = new GioHang();
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

    /** Lấy giảm giá phần trăm là có giới hạn trong GioHangTest. */
    @Test
    void layGiamGiaPhanTramLaCoGioiHan() {
        // Bán hàng.
        GioHang banHang = new GioHang();
        banHang.datGiamGiaPhanTram(200);
        assertThat(banHang.layGiamGiaPhanTram()).isEqualTo(100);
        banHang.datGiamGiaPhanTram(-20);
        assertThat(banHang.layGiamGiaPhanTram()).isZero();
    }

    /** Lấy các dòng từ chối âm các số tiền và các số lượng trong GioHangTest. */
    @Test
    void layCacDongTuChoiAmCacSoTienVaCacSoLuong() {
        assertThatThrownBy(() -> new GioHang.DongHang("P", "P", -1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GioHang.DongHang("P", "P", 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
