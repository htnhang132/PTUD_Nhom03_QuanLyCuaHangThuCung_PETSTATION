package com.petstation.integration;

import static com.petstation.dao.ThaoTacSQL.*;

import static org.assertj.core.api.Assertions.*;

import com.petstation.dao.*;
import com.petstation.dao.impl.JdbcBanHangDAO;
import com.petstation.dao.impl.JdbcDanhMucDAO;
import com.petstation.model.LoaiDanhMuc;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/** Đối chiếu bộ dữ liệu giả lập bằng quan hệ và sổ chi tiết, chỉ dùng database kiểm thử. */
@EnabledIfEnvironmentVariable(named = "PETSTATION_SQL_TESTS", matches = "true")
class DuLieuThucTeSQLTest {
    @BeforeAll
    static void cauHinh() {
        var cfg = CauHinhCSDL.nap();
        assertThat(cfg.url()).contains("databaseName=PetStationIntegrationTest");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cfg));
    }

    @Test
    void duSoLuongVaAnhDongNhatGiuaDanhMucVaBanHang() {
        assertThat(laySo("SELECT COUNT(*) FROM san_pham WHERE nguon_url IS NOT NULL"))
                .isEqualTo(60);
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM hoa_don WHERE idempotency_key LIKE"
                                        + " 'REAL_DATA_V1_INVOICE_%'"))
                .isEqualTo(30);
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM khach_hang WHERE id IN(SELECT id_doi_tuong"
                                    + " FROM doi_chieu_ma_du_lieu WHERE bang='khach_hang' AND ma_cu"
                                    + " LIKE 'KH-REAL-DEMO-%')"))
                .isEqualTo(20);
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM thu_cung WHERE id IN(SELECT id_doi_tuong FROM"
                                    + " doi_chieu_ma_du_lieu WHERE bang='thu_cung' AND ma_cu LIKE"
                                    + " 'TC-REAL-DEMO-%')"))
                .isEqualTo(30);
        var dm = JdbcDanhMucDAO.nap(LoaiDanhMuc.PRODUCTS);
        for (var sp : JdbcBanHangDAO.layCoTheBanCacSanPham()) {
            if (!sp[0].toString().matches("SP-(TA|PK|GR|VS)-[0-9]{3}")) continue;
            var chiTiet = dm.stream().filter(d -> d[0].equals(sp[0])).findFirst().orElseThrow();
            assertThat(chiTiet).hasSize(17);
            assertThat(sp[5]).isEqualTo(chiTiet[10]);
            assertThat(chiTiet[11].toString()).isNotBlank();
            assertThat(chiTiet[12].toString()).doesNotContain("1 sản phẩm theo mẫu");
        }
    }

    @Test
    void tonLoBangNhapTruBanTruXuatCongDoiTra() {
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM lo_hang l WHERE l.id IN(SELECT id_doi_tuong"
                                    + " FROM doi_chieu_ma_du_lieu WHERE bang='lo_hang' AND ma_cu"
                                    + " LIKE 'REAL-DEMO-%') AND l.so_luong_ton <> COALESCE((SELECT"
                                    + " SUM(n.so_luong) FROM chi_tiet_phieu_nhap n JOIN"
                                    + " phieu_nhap_kho p ON p.id=n.phieu_nhap_id WHERE"
                                    + " p.trang_thai='POSTED' AND n.san_pham_id=l.san_pham_id AND"
                                    + " n.so_lo=l.so_lo),0)-COALESCE((SELECT SUM(b.so_luong) FROM"
                                    + " phan_bo_lo_hang b JOIN chi_tiet_don_hang c ON"
                                    + " c.id=b.chi_tiet_don_hang_id JOIN hoa_don h ON"
                                    + " h.don_hang_id=c.don_hang_id WHERE b.lo_hang_id=l.id AND"
                                    + " h.trang_thai='PAID'),0)-COALESCE((SELECT SUM(x.so_luong)"
                                    + " FROM chi_tiet_phieu_xuat x JOIN phieu_xuat_kho p ON"
                                    + " p.id=x.phieu_xuat_id WHERE x.lo_hang_id=l.id AND"
                                    + " p.trang_thai='POSTED'),0)+COALESCE((SELECT SUM(t.so_luong)"
                                    + " FROM chi_tiet_doi_tra t JOIN phieu_doi_tra p ON"
                                    + " p.id=t.phieu_doi_tra_id WHERE t.lo_hang_id=l.id AND"
                                    + " p.trang_thai='COMPLETED'),0)"))
                .isZero();
    }

    @Test
    void diemHoaDonVaTienCaKhopSoChiTiet() {
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM khach_hang k WHERE k.id IN(SELECT"
                                    + " id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE"
                                    + " bang='khach_hang' AND ma_cu LIKE 'KH-REAL-DEMO-%') AND"
                                    + " diem_tich_luy<>COALESCE((SELECT SUM(thay_doi) FROM"
                                    + " lich_su_diem WHERE khach_hang_id=k.id),0)"))
                .isZero();
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM hoa_don WHERE idempotency_key LIKE"
                                    + " 'REAL_DATA_V1_INVOICE_%' AND tong_tien<>tam_tinh-giam_gia"))
                .isZero();
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM phien_lam_viec c WHERE EXISTS(SELECT 1 FROM"
                                    + " hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                                    + " d.phien_lam_viec_id=c.id AND h.idempotency_key LIKE"
                                    + " 'REAL_DATA_V1_INVOICE_%') AND"
                                    + " tien_thuc_te<>tien_dau_ca+COALESCE((SELECT SUM(h.tong_tien)"
                                    + " FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                                    + " d.phien_lam_viec_id=c.id AND h.trang_thai='PAID' AND"
                                    + " h.hinh_thuc_thanh_toan='CASH'),0)+COALESCE((SELECT"
                                    + " SUM(tien_coc) FROM hop_dong_luu_tru WHERE"
                                    + " phien_nhan_coc_id=c.id AND da_nhan_coc=1),0)"))
                .isZero();
    }

    @Test
    void spaHotelVaDoiHangLienKetHopLe() {
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa p JOIN hoa_don h ON"
                                    + " h.id=p.hoa_don_id WHERE p.id IN(SELECT id_doi_tuong FROM"
                                    + " doi_chieu_ma_du_lieu WHERE bang='phieu_tiep_nhan_spa' AND"
                                    + " ma_cu LIKE 'SPA-REAL-DEMO-%') AND (p.trang_thai<>'PAID' OR"
                                    + " p.tong_tien<>h.tam_tinh OR p.tong_tien<>(SELECT"
                                    + " SUM(thanh_tien) FROM chi_tiet_phieu_spa WHERE"
                                    + " phieu_spa_id=p.id))"))
                .isZero();
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM hop_dong_luu_tru h JOIN hoa_don d ON"
                                    + " d.id=h.hoa_don_id WHERE h.id IN(SELECT id_doi_tuong FROM"
                                    + " doi_chieu_ma_du_lieu WHERE bang='hop_dong_luu_tru' AND"
                                    + " ma_cu LIKE 'HT-REAL-PAID-%') AND"
                                    + " (h.tien_coc+d.tong_tien<>h.gia_dem OR h.da_nhan_coc<>1 OR"
                                    + " h.phien_nhan_coc_id IS NULL)"))
                .isZero();
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM phieu_doi_tra p JOIN hoa_don h ON"
                                    + " h.id=p.hoa_don_thay_the_id WHERE p.id IN(SELECT"
                                    + " id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE"
                                    + " bang='phieu_doi_tra' AND ma_cu LIKE 'DOI-REAL-DEMO-%') AND"
                                    + " (p.chenh_lech<>h.tong_tien OR"
                                    + " p.gia_tri_duoc_doi<>h.giam_gia OR"
                                    + " p.loai_phieu<>'EXCHANGE')"))
                .isZero();
    }
}
