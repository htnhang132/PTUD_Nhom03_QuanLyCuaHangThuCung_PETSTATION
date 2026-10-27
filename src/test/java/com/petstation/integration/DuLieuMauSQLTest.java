package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.ThaoTacSQL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/** Kiểm tra bộ mẫu bằng sổ chi tiết, không dựa vào bảng đối chiếu mã cũ. */
@EnabledIfEnvironmentVariable(named = "PETSTATION_SQL_TESTS", matches = "true")
class DuLieuMauSQLTest {
    @BeforeAll
    static void cauHinh() {
        var cauHinh = CauHinhCSDL.nap();
        assertThat(cauHinh.url()).contains("databaseName=PetStationIntegrationTest");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cauHinh));
    }

    @Test
    void duLieuCoDuDanhMucVaKhongCoKhoaNgoaiBiBoQua() {
        assertThat(laySo("SELECT COUNT(*) FROM san_pham WHERE nguon_url IS NOT NULL"))
                .isEqualTo(60);
        assertThat(laySo("SELECT COUNT(*) FROM khach_hang")).isGreaterThanOrEqualTo(20);
        assertThat(laySo("SELECT COUNT(*) FROM thu_cung")).isGreaterThanOrEqualTo(30);
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM sys.foreign_keys WHERE is_not_trusted=1 OR"
                                        + " is_disabled=1"))
                .isZero();
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM sys.check_constraints WHERE is_not_trusted=1"
                                        + " OR is_disabled=1"))
                .isZero();
    }

    @Test
    void tonKhoBangNhapTruBanTruXuatCongHangDoi() {
        assertThat(
                        laySo(
                                """
SELECT COUNT(*) FROM lo_hang l WHERE l.so_luong_ton <>
  COALESCE((SELECT SUM(n.so_luong) FROM chi_tiet_phieu_nhap n
    JOIN phieu_nhap_kho p ON p.id=n.phieu_nhap_id
    WHERE p.trang_thai='POSTED' AND n.san_pham_id=l.san_pham_id AND n.so_lo=l.so_lo),0)
  - COALESCE((SELECT SUM(b.so_luong) FROM phan_bo_lo_hang b
    JOIN chi_tiet_don_hang c ON c.id=b.chi_tiet_don_hang_id
    JOIN hoa_don h ON h.don_hang_id=c.don_hang_id
    WHERE b.lo_hang_id=l.id AND h.trang_thai='PAID'),0)
  - COALESCE((SELECT SUM(x.so_luong) FROM chi_tiet_phieu_xuat x
    JOIN phieu_xuat_kho p ON p.id=x.phieu_xuat_id
    WHERE x.lo_hang_id=l.id AND p.trang_thai='POSTED'),0)
  + COALESCE((SELECT SUM(t.so_luong) FROM chi_tiet_doi_tra t
    JOIN phieu_doi_tra p ON p.id=t.phieu_doi_tra_id
    WHERE t.lo_hang_id=l.id AND p.trang_thai='COMPLETED'),0)
"""))
                .isZero();
    }

    @Test
    void diemVaKhoanThuKhopChungTu() {
        assertThat(
                        laySo(
                                """
SELECT COUNT(*) FROM khach_hang k WHERE diem_tich_luy<>
COALESCE((SELECT SUM(thay_doi) FROM lich_su_diem l WHERE l.khach_hang_id=k.id),0)
"""))
                .isZero();
        assertThat(
                        laySo(
                                """
SELECT COUNT(*) FROM hoa_don h WHERE trang_thai='PAID' AND
(tong_tien<>tam_tinh-giam_gia OR tong_tien<>
 COALESCE((SELECT SUM(so_tien) FROM khoan_thu k WHERE k.hoa_don_id=h.id),0))
"""))
                .isZero();
        assertThat(
                        laySo(
                                """
SELECT COUNT(*) FROM hop_dong_luu_tru h WHERE
(CASE WHEN da_nhan_coc=1 THEN tien_coc ELSE 0 END)<>
COALESCE((SELECT SUM(so_tien) FROM khoan_thu k WHERE k.hop_dong_id=h.id),0)
"""))
                .isZero();
    }

    @Test
    void maVaAnhSanPhamDongNhat() {
        for (Object[] row :
                truyVan("SELECT ma_san_pham,anh FROM san_pham WHERE nguon_url IS NOT NULL")) {
            assertThat(row[1]).isNotNull();
            String duongDan = row[1].toString();
            assertThat(duongDan).startsWith("images/products/");
            assertThat(getClass().getResource("/" + duongDan)).isNotNull();
        }
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM san_pham WHERE ma_san_pham IS NULL OR"
                                        + " LTRIM(RTRIM(ma_san_pham))=''"))
                .isZero();
    }
}
