package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.dto.ChiTietBaoCao.HangBaoCao;
import com.petstation.dto.ChiTietBaoCao.TongHopCaThuNgan;
import com.petstation.dto.KhoangNgay;
import com.petstation.service.thongke.DuLieuBaoCaoService;
import com.petstation.service.thongke.DuLieuBaoCaoService.TongHop;

import org.junit.jupiter.api.Test;

import java.util.List;

class BaoCaoTongHopTest {
    @Test
    void doiNhanVaDaoCotVanTongHopDung() {
        HangBaoCao goc = () -> new Object[] {"Ngày A", 2, 1000.0, 300.0};
        HangBaoCao dao = () -> new Object[] {300.0, "Ngày A", 1000.0, 2};
        var a =
                DuLieuBaoCaoService.taoKieuHienThi(
                                "Báo cáo",
                                new String[] {"Ngày", "Đơn", "Thu", "Lãi"},
                                0,
                                2,
                                true,
                                null,
                                new TongHop(1, 3),
                                k -> List.of(goc))
                        .tao()
                        .apply(KhoangNgay.ganDay(1));
        var b =
                DuLieuBaoCaoService.taoKieuHienThi(
                                "Tên mới",
                                new String[] {"Kết quả", "Kỳ", "Giá trị", "Số chứng từ"},
                                1,
                                2,
                                true,
                                null,
                                new TongHop(3, 0),
                                k -> List.of(dao))
                        .tao()
                        .apply(KhoangNgay.ganDay(1));
        assertThat(b.theCacGiaTri()).containsExactly(a.theCacGiaTri());
        assertThat(b.bieuDoCacGiaTri()).containsExactly(1000.0);
    }

    @Test
    void tongThuCaLuonCoCaHoaDonVaCoc() {
        var ca = new TongHopCaThuNgan("NV", "Sáng", null, null, 50, 100, 20, 170.0, 0.0);
        assertThat(ca.thanhHang()).hasSize(10);
        assertThat(ca.thanhHang()[7]).isEqualTo(120.0);
    }
}
