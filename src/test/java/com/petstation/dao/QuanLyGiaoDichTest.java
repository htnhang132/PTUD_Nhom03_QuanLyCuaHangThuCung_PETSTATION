package com.petstation.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

/** Khai báo thao tác truy cập dữ liệu của giao dịch quản lý kiểm thử. */
class QuanLyGiaoDichTest {
    /** CSDL. */
    private KetNoiCSDL csdl;

    /** Kết nối. */
    private Connection ketNoi;

    /** Các giao dịch. */
    private QuanLyGiaoDich cacGiaoDich;

    /**
     * Đặt lên trong QuanLyGiaoDichTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @BeforeEach
    void datLen() throws Exception {
        csdl = mock(KetNoiCSDL.class);
        ketNoi = mock(Connection.class);
        when(csdl.mo()).thenReturn(ketNoi);
        cacGiaoDich = new QuanLyGiaoDich(csdl);
    }

    /**
     * Lấy xác nhận giao dịch khi thành công trong QuanLyGiaoDichTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layXacNhanGiaoDichKhiThanhCong() throws Exception {
        // Kết quả.
        String ketQua = cacGiaoDich.thucThi(ketNoi2 -> "ok");

        assertThat(ketQua).isEqualTo("ok");
        verify(ketNoi).setAutoCommit(false);
        verify(ketNoi).commit();
        verify(ketNoi, never()).rollback();
    }

    /**
     * Lấy chuyển tiếp khi chạy ngoại lệ và hoàn tác quay lại trong QuanLyGiaoDichTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layChuyenTiepKhiChayNgoaiLeVaHoanTacQuayLai() throws Exception {
        // Lỗi.
        IllegalStateException loi = new IllegalStateException("boom");

        assertThatThrownBy(
                        () ->
                                cacGiaoDich.thucThi(
                                        ketNoi2 -> {
                                            throw loi;
                                        }))
                .isSameAs(loi);

        verify(ketNoi).rollback();
        verify(ketNoi, never()).commit();
    }

    /**
     * Lấy bọc đã kiểm tra ngoại lệ và hoàn tác quay lại trong QuanLyGiaoDichTest.
     *
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Test
    void layBocDaKiemTraNgoaiLeVaHoanTacQuayLai() throws Exception {
        assertThatThrownBy(
                        () ->
                                cacGiaoDich.thucThi(
                                        ketNoi2 -> {
                                            throw new SQLException("db error");
                                        }))
                .isInstanceOf(TruyCapDuLieuException.class)
                .hasCauseInstanceOf(SQLException.class);

        verify(ketNoi).rollback();
        verify(ketNoi, never()).commit();
    }
}
