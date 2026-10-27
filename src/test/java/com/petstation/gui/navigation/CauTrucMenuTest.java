package com.petstation.gui.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.model.MaManHinh;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Cung cấp giao diện và xử lý tương tác cho menu mô hình kiểm thử. */
class CauTrucMenuTest {
    @Test
    void coNamCacNhomVaoDonHang() {
        assertThat(CauTrucMenu.layCacNhom().stream().map(CauTrucMenu.Nhom::nhan).toList())
                .containsExactly("Hệ thống", "Danh mục", "Tìm kiếm", "Xử lý", "Thống kê");
    }

    @Test
    void layCacPhimTruyCapLaDuyNhat() {
        // Các khóa.
        List<Character> cacKhoa =
                CauTrucMenu.layCacNhom().stream().map(CauTrucMenu.Nhom::phimTruyCap).toList();
        assertThat(cacKhoa).doesNotHaveDuplicates();
    }

    /**
     * Lấy mỗi menu mục là đã đăng ký và không màn hình xuất hiện vào hai các nhóm trong
     * CauTrucMenuTest.
     */
    @Test
    void layMoiMenuMucLaDaDangKyVaKhongManHinhXuatHienVaoHaiCacNhom() {
        // Đã thấy.
        Set<MaManHinh> daThay = EnumSet.noneOf(MaManHinh.class);
        for (CauTrucMenu.Nhom doHoa : CauTrucMenu.layCacNhom()) {
            for (CauTrucMenu.Muc muc : doHoa.cacMuc()) {
                if (muc.kiemTraPhanCach()) continue;
                assertThat(daThay.add(muc.manHinh())).as("trùng %s", muc.manHinh()).isTrue();
            }
        }
        // Bộ đăng ký.
        DangKyManHinh boDangKy = new DangKyManHinh(null, boQua -> {});
        for (MaManHinh id : daThay) assertThat(boDangKy.layDinhNghia(id)).as(id.name()).isNotNull();
    }

    @Test
    void layMoiManHinhLaDaDangKy() {
        // Bộ đăng ký.
        DangKyManHinh boDangKy = new DangKyManHinh(null, boQua -> {});
        for (MaManHinh id : MaManHinh.values())
            assertThat(boDangKy.layDinhNghia(id)).as(id.name()).isNotNull();
    }

    @Test
    void timKiemNhomCoKhongDanhMucHoacXuLyCacManHinh() {
        // Tìm kiếm.
        Set<MaManHinh> timKiem =
                CauTrucMenu.layCacNhom().get(2).cacMuc().stream()
                        .filter(chiSo -> !chiSo.kiemTraPhanCach())
                        .map(CauTrucMenu.Muc::manHinh)
                        .collect(Collectors.toSet());
        assertThat(timKiem)
                .contains(
                        MaManHinh.ORDERS,
                        MaManHinh.BATCH_STOCK,
                        MaManHinh.SPA_DAILY,
                        MaManHinh.HOTEL_MAP)
                .doesNotContain(MaManHinh.POS, MaManHinh.CUSTOMERS, MaManHinh.SPA, MaManHinh.HOTEL);
    }

    @Test
    void laySanPhamDanhMucChuaLichSuGiaTab() {
        // Các nhãn.
        var cacNhan =
                CauTrucMenu.layCacNhom().get(1).cacMuc().stream()
                        .map(CauTrucMenu.Muc::nhan)
                        .toList();
        assertThat(cacNhan).anyMatch(dongDangXet -> dongDangXet.startsWith("Sản phẩm"));
        assertThat(cacNhan).noneMatch(dongDangXet -> dongDangXet.contains("▸ Lịch sử giá"));
    }
}
