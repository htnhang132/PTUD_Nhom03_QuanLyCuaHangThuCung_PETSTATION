package com.petstation.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.dto.MucLuaChon;
import com.petstation.model.banhang.ChiTietDoiTra;
import com.petstation.model.chung.MaDBEnum;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.kho.ChiTietPhieuNhap;
import com.petstation.model.kho.ChiTietPhieuXuat;
import com.petstation.model.kho.PhieuNhapKho;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.NhatKyChamSoc;
import com.petstation.model.spa.PhieuTiepNhanSpa;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

// Kiểm thử đơn vị offline cho các sửa lỗi Batch 4 (tiện ích số/tiền, DTO, và model)
class DuLieuDinhDangModelTest {

    @Test
    void dinhDangDocGiuDauAmVaDuLieuLayGiaTriThapPhan() {
        assertThat(DinhDang.doc("-50.000₫")).isEqualTo(-50_000L);
        assertThat(DinhDang.doc("−12.500")).isEqualTo(-12_500L);
        assertThat(DinhDang.doc("150.000₫")).isEqualTo(150_000L);

        assertThat(DuLieu.layGiaTri("100.0")).isEqualTo(100L);
        assertThat(DuLieu.layGiaTri("1234.6")).isEqualTo(1234L);
        assertThat(DuLieu.layGiaTri("-250")).isEqualTo(-250L);
        assertThat(DuLieu.layGiaTri("50000")).isEqualTo(50_000L);
        assertThat(DuLieu.layGiaTri(null)).isZero();
    }

    @Test
    void mucLuaChonHoTroEqualsHashCodeVaToString() {
        MucLuaChon a = new MucLuaChon("GSP01", "Tắm cắt tỉa");
        MucLuaChon b = new MucLuaChon("GSP01", "Tắm cắt tỉa");
        MucLuaChon khongNhan = new MucLuaChon("GSP02", null);

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isEqualTo("Tắm cắt tỉa");
        assertThat(khongNhan.toString()).isEmpty();
    }

    @Test
    void modelKiemTraSoAmVaBoSungTruongConThieu() {
        ChiTietDoiTra dt = new ChiTietDoiTra();
        dt.setTienHoan(25_000L);
        assertThat(dt.getTienHoan()).isEqualTo(25_000L);
        assertThatThrownBy(() -> dt.setTienHoan(-1L)).isInstanceOf(IllegalArgumentException.class);

        ChiTietPhieuXuat px = new ChiTietPhieuXuat();
        px.setGiaTri(80_000L);
        assertThat(px.getGiaTri()).isEqualTo(80_000L);
        assertThatThrownBy(() -> px.setGiaTri(-5L)).isInstanceOf(IllegalArgumentException.class);

        PhieuNhapKho pn = new PhieuNhapKho("PN0001");
        pn.setTongTien(500_000L);
        assertThat(pn.getTongTien()).isEqualTo(500_000L);
        assertThatThrownBy(() -> pn.setTongTien(-10L)).isInstanceOf(IllegalArgumentException.class);

        ChiTietPhieuNhap ctNhap = new ChiTietPhieuNhap();
        LocalDate hsd = LocalDate.of(2027, 6, 30);
        ctNhap.setHanSuDung(hsd);
        assertThat(ctNhap.getHanSuDung()).isEqualTo(hsd);

        PhieuTiepNhanSpa spa = new PhieuTiepNhanSpa();
        assertThatThrownBy(() -> spa.setDonGia(-1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> spa.setSoLuong(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> spa.setCanNangThucTe(-0.1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> spa.setTongTien(-1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void hopDongVaNhatKyEqualsTheoMaChinh() {
        HopDongLuuTru hd1 = new HopDongLuuTru();
        hd1.setMaHopDong("HDLT01");
        HopDongLuuTru hd2 = new HopDongLuuTru();
        hd2.setMaHopDong("HDLT01");
        assertThat(hd1).isEqualTo(hd2).hasSameHashCodeAs(hd2);
        assertThat(hd1.toString()).isEqualTo("HDLT01");

        NhatKyChamSoc nk1 = new NhatKyChamSoc();
        nk1.setMaNhatKy("NK01");
        NhatKyChamSoc nk2 = new NhatKyChamSoc();
        nk2.setMaNhatKy("NK01");
        assertThat(nk1).isEqualTo(nk2).hasSameHashCodeAs(nk2);
        assertThat(nk1.toString()).isEqualTo("NK01");
    }

    @Test
    void tienIchDinhDangHtmlTienBoDauVaDocNgayLinhHoat() {
        assertThat(DinhDang.thoatHtml("A & <B> \"C\" 'D'"))
                .isEqualTo("A &amp; &lt;B&gt; &quot;C&quot; &#39;D&#39;");
        assertThat(DinhDang.thoatHtml(null)).isEmpty();
        assertThat(DinhDang.thoatHtmlXuongDong("Dòng 1\n<2>")).isEqualTo("Dòng 1<br>&lt;2&gt;");

        assertThat(DinhDang.docTienKhongAm("150.000 ₫")).isEqualTo(150_000L);
        assertThat(DinhDang.docTienKhongAm("0đ")).isZero();
        assertThat(DinhDang.docTienKhongAm("-500")).isEqualTo(-1L);
        assertThat(DinhDang.docTienKhongAm("abc")).isEqualTo(-1L);

        assertThat(DinhDang.boDauTiengViet("Đặng Văn Đồng")).isEqualTo("Dang Van Dong");

        LocalDate expected = LocalDate.of(2026, 11, 18);
        assertThat(DuLieu.docNgayLinhHoat(expected)).isEqualTo(expected);
        assertThat(DuLieu.docNgayLinhHoat(LocalDateTime.of(2026, 11, 18, 14, 30)))
                .isEqualTo(expected);
        assertThat(DuLieu.docNgayLinhHoat(new Date())).isEqualTo(LocalDate.now());
        assertThat(DuLieu.docNgayLinhHoat("2026-11-18 14:30:00")).isEqualTo(expected);
        assertThat(DuLieu.docNgayLinhHoat("18/11/2026 09:45")).isEqualTo(expected);
        assertThat(DuLieu.docNgayLinhHoat("-")).isNull();
        assertThat(DuLieu.docNgayLinhHoat("khong_hop_le")).isNull();

        assertThat(MaDBEnum.tuMaDB(TrangThaiPhongHotel.class, " dang_o "))
                .isEqualTo(TrangThaiPhongHotel.DANG_O);
        assertThat(MaDBEnum.tuMaDB(TrangThaiPhongHotel.class, "  ")).isNull();
        assertThatThrownBy(() -> MaDBEnum.tuMaDB(TrangThaiPhongHotel.class, "KHONG_CO"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(TrangThaiTaiKhoan.HOAT_DONG.maDB()).isEqualTo("HOAT_DONG");
    }
}
