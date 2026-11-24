package com.petstation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.petstation.dao.chung.LuaChonNghiepVuDAO;
import com.petstation.dao.chung.NghiepVuDAO;
import com.petstation.dto.PhienDangNhap;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.chung.PhatSuKien;
import com.petstation.service.hethong.PhienDangNhapService;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

class SuKienVaPhanQuyenTest {
    @Test
    void listenerLoiKhongChanListenerConLai() {
        PhatSuKien<String> phat = new PhatSuKien<>();
        AtomicInteger dem = new AtomicInteger();
        phat.them(
                e -> {
                    throw new IllegalStateException("test listener");
                });
        phat.them(e -> dem.incrementAndGet());
        assertThatCode(() -> phat.phat("COMMITTED")).doesNotThrowAnyException();
        assertThat(dem.get()).isEqualTo(1);
    }

    @Test
    void tuChoiDocKhoTruocKhiGoiDao() {
        NghiepVuDAO dao = mock(NghiepVuDAO.class);
        PhienDangNhapService phien = new PhienDangNhapService();
        phien.datPhienDangNhap(
                new PhienDangNhap(
                        new TaiKhoan("spa"), new NhanVien("NV1"), EnumSet.of(Quyen.QUAN_LY_SPA)));
        NghiepVuService service = new NghiepVuService(dao, mock(LuaChonNghiepVuDAO.class), phien);
        assertThatThrownBy(() -> service.layCacDong(LoaiNghiepVu.PHIEU_NHAP_KHO))
                .isInstanceOf(SecurityException.class);
        verifyNoInteractions(dao);
    }

    @Test
    void duQuyenChiDocDungLoaiDaYeuCau() {
        NghiepVuDAO dao = mock(NghiepVuDAO.class);
        PhienDangNhapService phien = new PhienDangNhapService();
        phien.datPhienDangNhap(
                new PhienDangNhap(
                        new TaiKhoan("stock"), new NhanVien("NV1"), EnumSet.of(Quyen.NHAP_KHO)));
        new NghiepVuService(dao, mock(LuaChonNghiepVuDAO.class), phien)
                .layCacDong(LoaiNghiepVu.PHIEU_NHAP_KHO);
        verify(dao).layCacDong(LoaiNghiepVu.PHIEU_NHAP_KHO, "");
    }
}
