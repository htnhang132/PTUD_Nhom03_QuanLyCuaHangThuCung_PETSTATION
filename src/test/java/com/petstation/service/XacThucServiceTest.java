package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.petstation.dao.KetNoiCSDL;
import com.petstation.dao.NhanVienDAO;
import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.dao.QuanLyGiaoDich;
import com.petstation.dao.TaiKhoanDAO;
import com.petstation.model.NhanVien;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;
import com.petstation.model.TrangThaiTaiKhoan;
import com.petstation.model.VaiTro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho xác thực dịch vụ kiểm thử. */
class XacThucServiceTest {
    /** Danh sách tài khoản. */
    private TaiKhoanDAO danhSachTaiKhoan;

    /** Các nhân viên. */
    private NhanVienDAO cacNhanVien;

    /** Nhật ký hệ thống các nhật ký. */
    private NhatKyHeThongDAO nhatKyHeThongCacNhatKy;

    /** Dịch vụ. */
    private XacThucService dichVu;

    /**
     * Lấy tài khoản đang hoạt động trong XacThucServiceTest.
     *
     * @param tenDangNhap tên đăng nhập
     * @param matKhau mật khẩu
     * @param soLuongThatBai số lượng thất bại
     * @param khoaDenLuc khóa đến lúc
     * @return kết quả lấy tài khoản đang hoạt động
     */
    private TaiKhoan layTaiKhoanDangHoatDong(
            String tenDangNhap, String matKhau, int soLuongThatBai, LocalDateTime khoaDenLuc) {
        return new TaiKhoan(
                1L,
                7L,
                tenDangNhap,
                BCrypt.hashpw(matKhau, BCrypt.gensalt(4)),
                VaiTro.SALES_STAFF,
                TrangThaiTaiKhoan.ACTIVE,
                soLuongThatBai,
                khoaDenLuc,
                false);
    }

    /**
     * Đặt lên trong XacThucServiceTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @BeforeEach
    void datLen() throws Exception {
        danhSachTaiKhoan = mock(TaiKhoanDAO.class);
        cacNhanVien = mock(NhanVienDAO.class);
        nhatKyHeThongCacNhatKy = mock(NhatKyHeThongDAO.class);

        // CSDL.
        KetNoiCSDL csdl = mock(KetNoiCSDL.class);
        when(csdl.mo()).thenReturn(mock(Connection.class));
        // Các giao dịch.
        QuanLyGiaoDich cacGiaoDich = new QuanLyGiaoDich(csdl);
        dichVu =
                new XacThucService(
                        cacGiaoDich, danhSachTaiKhoan, cacNhanVien, nhatKyHeThongCacNhatKy);
    }

    /** Lấy từ chối trống tên đăng nhập trong XacThucServiceTest. */
    @Test
    void layTuChoiTrongTenDangNhap() {
        assertThatThrownBy(() -> dichVu.dangNhap("  ", "secret".toCharArray()))
                .isInstanceOf(XacThucException.class);
    }

    /** Lấy từ chối trống mật khẩu trong XacThucServiceTest. */
    @Test
    void layTuChoiTrongMatKhau() {
        assertThatThrownBy(() -> dichVu.dangNhap("admin", new char[0]))
                .isInstanceOf(XacThucException.class);
    }

    /**
     * Lấy thất bại cho không xác định tên đăng nhập trong XacThucServiceTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layThatBaiChoKhongXacDinhTenDangNhap() throws Exception {
        when(danhSachTaiKhoan.timTheoTenDangNhap(any(), eq("ghost"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dichVu.dangNhap("ghost", "secret123".toCharArray()))
                .isInstanceOf(XacThucException.class);
    }

    /**
     * Lấy thất bại cho không đúng mật khẩu và các bản ghi lỗi trong XacThucServiceTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layThatBaiChoKhongDungMatKhauVaCacBanGhiLoi() throws Exception {
        // Tài khoản.
        TaiKhoan taiKhoan = layTaiKhoanDangHoatDong("admin", "secret123", 0, null);
        when(danhSachTaiKhoan.timTheoTenDangNhap(any(), eq("admin")))
                .thenReturn(Optional.of(taiKhoan));

        assertThatThrownBy(() -> dichVu.dangNhap("admin", "wrong-pass".toCharArray()))
                .isInstanceOf(XacThucException.class);

        verify(danhSachTaiKhoan).ghiNhanDangNhapThatBai(any(), eq(1L), eq(1), isNull());
        verify(nhatKyHeThongCacNhatKy)
                .them(any(), argThat(suKien -> "LOGIN_FAILED".equals(suKien.thaoTac())));
    }

    /**
     * Lấy khóa tài khoản sau năm các lần thất bại trong XacThucServiceTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layKhoaTaiKhoanSauNamCacLanThatBai() throws Exception {
        // Tài khoản.
        TaiKhoan taiKhoan = layTaiKhoanDangHoatDong("admin", "secret123", 4, null);
        when(danhSachTaiKhoan.timTheoTenDangNhap(any(), eq("admin")))
                .thenReturn(Optional.of(taiKhoan));

        assertThatThrownBy(() -> dichVu.dangNhap("admin", "wrong-pass".toCharArray()))
                .isInstanceOf(XacThucException.class);

        verify(danhSachTaiKhoan).ghiNhanDangNhapThatBai(any(), eq(1L), eq(5), notNull());
    }

    /**
     * Lấy từ chối tạm thời đã khóa tài khoản trong XacThucServiceTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layTuChoiTamThoiDaKhoaTaiKhoan() throws Exception {
        // Tài khoản.
        TaiKhoan taiKhoan =
                layTaiKhoanDangHoatDong(
                        "admin", "secret123", 5, LocalDateTime.now().plusMinutes(10));
        when(danhSachTaiKhoan.timTheoTenDangNhap(any(), eq("admin")))
                .thenReturn(Optional.of(taiKhoan));

        assertThatThrownBy(() -> dichVu.dangNhap("admin", "secret123".toCharArray()))
                .isInstanceOf(XacThucException.class);

        verify(danhSachTaiKhoan, never()).ghiNhanDangNhapThanhCong(any(), anyLong());
        verify(nhatKyHeThongCacNhatKy)
                .them(any(), argThat(suKien -> "LOGIN_REJECTED".equals(suKien.thaoTac())));
    }

    /**
     * Hiển thị kết quả thành công với hợp lệ thông tin đăng nhập trong XacThucServiceTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layThanhCongVoiHopLeThongTinDangNhap() throws Exception {
        // Tài khoản.
        TaiKhoan taiKhoan = layTaiKhoanDangHoatDong("admin", "secret123", 0, null);
        // Nhân viên.
        NhanVien nhanVien =
                new NhanVien(
                        7L,
                        "NV007",
                        "Nguyễn Văn Quản",
                        null,
                        "0900000000",
                        null,
                        "Cửa hàng trưởng",
                        LocalDate.now(),
                        true);
        when(danhSachTaiKhoan.timTheoTenDangNhap(any(), eq("admin")))
                .thenReturn(Optional.of(taiKhoan));
        when(cacNhanVien.timTheoId(any(), eq(7L))).thenReturn(Optional.of(nhanVien));
        when(danhSachTaiKhoan.timCacQuyen(any(), eq(1L))).thenReturn(Set.of(Quyen.POS_SELL));

        // Phiên đăng nhập.
        PhienDangNhap phienDangNhap = dichVu.dangNhap("admin", "secret123".toCharArray());

        assertThat(phienDangNhap.nhanVien().id()).isEqualTo(7L);
        assertThat(phienDangNhap.kiemTraCo(Quyen.POS_SELL)).isTrue();
        verify(danhSachTaiKhoan).ghiNhanDangNhapThanhCong(any(), eq(1L));
        verify(nhatKyHeThongCacNhatKy)
                .them(any(), argThat(suKien -> "LOGIN_SUCCESS".equals(suKien.thaoTac())));
    }
}
