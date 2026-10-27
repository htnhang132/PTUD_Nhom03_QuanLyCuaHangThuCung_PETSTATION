package com.petstation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.petstation.dao.ChiTietThongKeDAO;
import com.petstation.model.ChiTietBaoCao;
import com.petstation.model.KhoangNgay;
import com.petstation.model.NhanVien;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

class ChiTietThongKeServiceTest {
    private final ChiTietThongKeDAO dao = mock(ChiTietThongKeDAO.class);

    @AfterEach
    void ketThucPhien() {
        PhienDangNhapService.datPhienDangNhap(null);
    }

    private void dangNhap(boolean tatCa, boolean caNhan) {
        var phien = mock(PhienDangNhap.class);
        var taiKhoan = mock(TaiKhoan.class);
        var nhanVien = mock(NhanVien.class);
        when(taiKhoan.id()).thenReturn(17L);
        when(nhanVien.id()).thenReturn(23L);
        when(phien.taiKhoan()).thenReturn(taiKhoan);
        when(phien.nhanVien()).thenReturn(nhanVien);
        when(phien.kiemTraCo(Quyen.REPORT_VIEW_ALL)).thenReturn(tatCa);
        when(phien.kiemTraCo(Quyen.REPORT_VIEW_OWN_SHIFT)).thenReturn(caNhan);
        PhienDangNhapService.datPhienDangNhap(phien);
    }

    @Test
    void gioiHanDuLieuTheoTaiKhoanVaNhanVien() {
        dangNhap(false, true);
        var ky = KhoangNgay.ganDay(7);
        var duLieu = new ChiTietBaoCao(new String[] {"Hóa đơn"}, List.of(), 0);
        {
            when(dao.lay("Thống kê doanh thu", "Theo ngày", ky, 17L, 23L)).thenReturn(duLieu);
            assertThat(ChiTietThongKeService.lay(dao, "Thống kê doanh thu", "Theo ngày", ky))
                    .isSameAs(duLieu);
            verify(dao).lay("Thống kê doanh thu", "Theo ngày", ky, 17L, 23L);
        }
    }

    @Test
    void quanLyDuocXemToanCuaHang() {
        dangNhap(true, false);
        var ky = KhoangNgay.ganDay(30);
        {
            ChiTietThongKeService.lay(dao, "Thống kê khách hàng", "Chi tiêu theo kỳ", ky);
            verify(dao).lay("Thống kê khách hàng", "Chi tiêu theo kỳ", ky, null, null);
        }
    }

    @Test
    void thieuQuyenKhongDuocGoiDao() {
        dangNhap(false, false);
        {
            assertThatThrownBy(
                            () ->
                                    ChiTietThongKeService.lay(
                                            dao,
                                            "Thống kê doanh thu",
                                            "Theo ngày",
                                            KhoangNgay.ganDay(7)))
                    .isInstanceOf(PhanQuyenException.class);
            verifyNoInteractions(dao);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "Công nợ nhà cung cấp,Tất cả",
        "Thống kê khách hàng,Chi tiêu theo kỳ",
        "Thống kê nhân viên,Lịch làm việc",
        "Thống kê hàng hóa,Nhập xuất tồn"
    })
    void baoCaoToanCuaHangKhongChapNhanQuyenCaNhan(String baoCao, String goc) {
        dangNhap(false, true);
        {
            assertThatThrownBy(
                            () -> ChiTietThongKeService.lay(dao, baoCao, goc, KhoangNgay.ganDay(7)))
                    .isInstanceOf(PhanQuyenException.class);
            verifyNoInteractions(dao);
        }
    }
}
