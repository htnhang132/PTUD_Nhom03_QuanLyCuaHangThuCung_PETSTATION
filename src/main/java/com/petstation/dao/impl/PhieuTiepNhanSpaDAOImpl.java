package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.PhieuTiepNhanSpaDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.util.DuLieu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class PhieuTiepNhanSpaDAOImpl implements PhieuTiepNhanSpaDAO {
    public PhieuTiepNhanSpaDAOImpl() {}

    public int capNhatTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='DANG_THUC_HIEN' WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int capNhatSucKhoeThuCung(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE thu_cung SET can_nang=?, tinh_trang_suc_khoe=? WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[1], cacThamSo[2], cacThamSo[2]);
    }

    public long demTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT CASE WHEN (ma_goi IS NOT NULL OR don_gia>0 OR tong_tien>0) THEN 1 ELSE 0"
                        + " END FROM phieu_tiep_nhan_spa WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demLichHen(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='HOAT_DONG'",
                cacThamSo[0], cacThamSo[0]);
    }

    public long khoaNhanVienDangLam(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT 1 FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " dang_lam_viec=1",
                cacThamSo[0], cacThamSo[0]);
    }

    public long layThoiLuongDichVu(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demLichTrung(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ((ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) OR (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))) AND trang_thai NOT IN"
                        + " ('DA_HUY','HOAN_TAT') AND bat_dau<? AND"
                        + " ket_thuc>?",
                cacThamSo[0], cacThamSo[0],
                cacThamSo[1], cacThamSo[1],
                cacThamSo[2],
                cacThamSo[3]);
    }

    public long themLichHen(Object... cacThamSo) {
        String maLichHen = String.valueOf(cacThamSo[0]);
        String maKH = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_khach_hang FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maKH = String.valueOf(r.get(0)[0]);
        }
        String maTC = String.valueOf(cacThamSo[2]);
        if (cacThamSo[2] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_thu_cung FROM thu_cung WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[2], cacThamSo[2]);
            if (!r.isEmpty()) maTC = String.valueOf(r.get(0)[0]);
        }
        String maNV = cacThamSo[5] != null ? String.valueOf(cacThamSo[5]) : null;
        if (cacThamSo[5] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[5], cacThamSo[5]);
            if (!r.isEmpty()) maNV = String.valueOf(r.get(0)[0]);
        }
        String maGoi = cacThamSo[6] != null ? String.valueOf(cacThamSo[6]) : null;
        if (cacThamSo[6] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_goi FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[6], cacThamSo[6]);
            if (!r.isEmpty()) maGoi = String.valueOf(r.get(0)[0]);
        }
        ThaoTacSQL.capNhat(
                "INSERT INTO lich_hen(ma_lich_hen, ma_khach_hang, ma_thu_cung, loai_dich_vu, bat_dau, ket_thuc, trang_thai, ma_nhan_vien, ma_goi) "
                        + "VALUES(?, ?, ?, 'SPA', ?, ?, 'DA_XAC_NHAN', ?, ?)",
                maLichHen, maKH, maTC, cacThamSo[3], cacThamSo[4], maNV, maGoi);
        return DuLieu.layIdCauNoi(null, maLichHen);
    }

    public List<Object[]> timTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lich_hen, ma_nhan_vien FROM lich_hen"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='DA_XAC_NHAN'",
                cacThamSo[0], cacThamSo[0],
                cacThamSo[1], cacThamSo[1]);
    }

    public long themTiepNhan(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        String maHen = cacThamSo[1] != null ? String.valueOf(cacThamSo[1]) : null;
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_lich_hen FROM lich_hen WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maHen = String.valueOf(r.get(0)[0]);
        }
        String maTC = String.valueOf(cacThamSo[2]);
        if (cacThamSo[2] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_thu_cung FROM thu_cung WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[2], cacThamSo[2]);
            if (!r.isEmpty()) maTC = String.valueOf(r.get(0)[0]);
        }
        String maNV = cacThamSo[4] != null ? String.valueOf(cacThamSo[4]) : null;
        if (cacThamSo[4] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[4], cacThamSo[4]);
            if (!r.isEmpty()) maNV = String.valueOf(r.get(0)[0]);
        }
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_tiep_nhan_spa(ma_phieu, ma_lich_hen, ma_thu_cung, can_nang_thuc_te, ma_nhan_vien, ghi_chu, trang_thai, tao_luc) "
                        + "VALUES(?, ?, ?, ?, ?, ?, 'TIEP_NHAN', SYSDATETIME())",
                maPhieu, maHen, maTC, cacThamSo[3], maNV, cacThamSo[5]);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    public Object layTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_goi FROM lich_hen WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> timThemDichVuSpa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT t.can_nang_thuc_te, p.loai, t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t WITH(UPDLOCK,HOLDLOCK) JOIN"
                        + " thu_cung p ON p.ma_thu_cung = t.ma_thu_cung WHERE (t.ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(t.ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> layGiaDichVuTheoCanNang(Object... cacThamSo) {
        String maGoi = String.valueOf(cacThamSo[0]);
        String loaiThuCung = String.valueOf(cacThamSo[1]);
        double canNang = ((Number) cacThamSo[2]).doubleValue();
        List<Object[]> goiList =
                ThaoTacSQL.truyVan(
                        "SELECT ma_goi, ten_goi, COALESCE(bang_gia, N'') FROM goi_dich_vu_spa WHERE"
                                + " ma_goi=? AND trang_thai='HOAT_DONG'",
                        maGoi);
        if (goiList.isEmpty()) return List.of();
        Object[] goi = goiList.get(0);
        long goiId = DuLieu.layIdCauNoi(null, String.valueOf(goi[0]));
        String tenGoi = String.valueOf(goi[1]);
        String bangGia = String.valueOf(goi[2]).trim();
        if (bangGia.isEmpty()) return List.of();

        long donGiaChon = timGiaTheoCanNang(bangGia, loaiThuCung, canNang);
        if (donGiaChon < 0) return List.of();
        return List.<Object[]>of(new Object[] {goiId, donGiaChon, tenGoi});
    }

    static long timGiaTheoCanNang(String bangGia, String loaiThuCung, double canNang) {
        boolean laCho = "Chó".equalsIgnoreCase(loaiThuCung) || "DOG".equalsIgnoreCase(loaiThuCung);
        long donGiaChon = -1;
        double maxTu = -1;
        for (String muc : bangGia.split(";")) {
            if (muc.isBlank()) continue;
            boolean namPhan = muc.contains("|");
            String[] p = muc.split(namPhan ? "\\|" : ":", -1);
            if (p.length != (namPhan ? 5 : 3)) continue;
            String loaiMuc = p[0].trim();
            boolean mucLaCho = "DOG".equalsIgnoreCase(loaiMuc) || "Chó".equalsIgnoreCase(loaiMuc);
            if (laCho != mucLaCho) continue;
            String[] khoang = namPhan ? new String[]{p[2], p[3]} : p[1].trim().split("-", -1);
            double tu = Double.parseDouble(khoang[0].trim());
            double den = khoang.length < 2 || khoang[1].isBlank()
                    || "NULL".equalsIgnoreCase(khoang[1].trim())
                    ? Double.POSITIVE_INFINITY : Double.parseDouble(khoang[1].trim());
            long gia = new java.math.BigDecimal(p[namPhan ? 4 : 2].trim()).longValueExact();
            String nhan = namPhan ? p[1].trim() : "";
            boolean datTu = nhan.startsWith(">") && !nhan.startsWith(">=")
                    ? canNang > tu : canNang >= tu;
            boolean datDen = nhan.startsWith("<") && !nhan.startsWith("<=")
                    ? canNang < den : canNang <= den;
            if (datTu && datDen && tu >= maxTu) {
                maxTu = tu;
                donGiaChon = gia;
            }
        }
        return donGiaChon;
    }

    public int capNhatThemDichVuSpa(Object... cacThamSo) {
        Object phieuSpaObj = cacThamSo[0];
        String maGoi = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_goi FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maGoi = String.valueOf(r.get(0)[0]);
        }
        String moTa = String.valueOf(cacThamSo[2]);
        long donGia = ((Number) cacThamSo[3]).longValue();
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET ma_goi=COALESCE(ma_goi, ?),"
                        + " mo_ta_dich_vu=CASE WHEN mo_ta_dich_vu IS NULL OR mo_ta_dich_vu=N''"
                        + " THEN ? ELSE mo_ta_dich_vu + N'; ' + ? END,"
                        + " don_gia=don_gia+?, so_luong=CASE WHEN so_luong<=0 THEN 1 ELSE so_luong"
                        + " END, tong_tien=tong_tien+? WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maGoi,
                moTa,
                moTa,
                donGia,
                donGia,
                phieuSpaObj,
                phieuSpaObj);
    }

    public int tinhLaiTongPhieuSpa(Object... cacThamSo) {
        Object phieuSpaObj = cacThamSo[0];
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=don_gia*COALESCE(NULLIF(so_luong,0),1)"
                        + " WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                phieuSpaObj, phieuSpaObj);
    }

    public int capNhatTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai=?",
                cacThamSo[0], cacThamSo[1], cacThamSo[1], cacThamSo[2]);
    }

    public int hoanTatLichHen(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='HOAN_TAT' WHERE ma_lich_hen = (SELECT"
                        + " ma_lich_hen FROM phieu_tiep_nhan_spa WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int huyLichHenBanGhi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='DA_HUY' WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='DA_XAC_NHAN'",
                cacThamSo[0], cacThamSo[0]);
    }

    public Object layMaLichHenThuCung(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_thu_cung FROM lich_hen WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> layPhieuSpaChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(mo_ta_dich_vu,N'Dịch vụ Spa'),"
                        + " COALESCE(NULLIF(don_gia,0),tong_tien),"
                        + " COALESCE(NULLIF(so_luong,0),1), tong_tien FROM phieu_tiep_nhan_spa"
                        + " WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND (ma_goi IS NOT NULL OR don_gia>0 OR tong_tien>0)",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> layChiTietLichHen(long id) {
        return truyVan(
                "SELECT l.ma_thu_cung, COALESCE(l.ma_goi, ''), COALESCE(l.ma_nhan_vien, ''), l.bat_dau FROM lich_hen l"
                        + " WHERE (l.ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    public List<Object[]> khoaLichHen(long id) {
        return truyVan(
                "SELECT trang_thai FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    public long demLichTrungKhiSua(
            long id, long nhanVien, Object thuCung, LocalDateTime ketThuc, LocalDateTime batDau) {
        return laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (ma_lich_hen <> CAST(? AS VARCHAR) AND TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) <> TRY_CAST(? AS BIGINT)) AND ((ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) OR (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))) AND"
                        + " trang_thai NOT IN ('DA_HUY','HOAN_TAT')"
                        + " AND bat_dau<? AND ket_thuc>?",
                id, id,
                nhanVien, nhanVien,
                thuCung, thuCung,
                ketThuc,
                batDau);
    }

    public int suaLichHen(
            Object khachHang,
            Object thuCung,
            long goi,
            long nhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long id) {
        String maKH = String.valueOf(khachHang);
        if (khachHang instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_khach_hang FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", khachHang, khachHang);
            if (!r.isEmpty()) maKH = String.valueOf(r.get(0)[0]);
        }
        String maTC = String.valueOf(thuCung);
        if (thuCung instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_thu_cung FROM thu_cung WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", thuCung, thuCung);
            if (!r.isEmpty()) maTC = String.valueOf(r.get(0)[0]);
        }
        String maGoi = String.valueOf(goi);
        List<Object[]> rGoi = ThaoTacSQL.truyVan("SELECT ma_goi FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", goi, goi);
        if (!rGoi.isEmpty()) maGoi = String.valueOf(rGoi.get(0)[0]);

        String maNV = String.valueOf(nhanVien);
        List<Object[]> rNv = ThaoTacSQL.truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", nhanVien, nhanVien);
        if (!rNv.isEmpty()) maNV = String.valueOf(rNv.get(0)[0]);

        return capNhat(
                "UPDATE lich_hen SET ma_khach_hang=?, ma_thu_cung=?, ma_goi=?, ma_nhan_vien=?, bat_dau=?, ket_thuc=?"
                        + " WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maKH,
                maTC,
                maGoi,
                maNV,
                batDau,
                ketThuc,
                id,
                id);
    }

    @Override
    public List<Object[]> timKiem(
        LocalDate tuNgay,
        LocalDate denNgay,
        String trangThai) {
        String sql = """
                SELECT t.ma_phieu AS ma_khoa, t.ma_phieu, k.ho_ten, p.ten,
                       t.can_nang_thuc_te, COALESCE(n.ho_ten, N''),
                       t.tong_tien, t.trang_thai, t.tao_luc
                FROM phieu_tiep_nhan_spa t
                JOIN thu_cung p ON p.ma_thu_cung = t.ma_thu_cung
                JOIN khach_hang k ON k.ma_khach_hang = p.ma_khach_hang
                LEFT JOIN nhan_vien n ON n.ma_nhan_vien = t.ma_nhan_vien
                WHERE 1 = 1
                """;

        List<Object> thamSo = new ArrayList<>();

        if (tuNgay != null) {
            sql += " AND t.tao_luc >= ?";
            thamSo.add(tuNgay.atStartOfDay());
        }

        if (denNgay != null) {
            sql += " AND t.tao_luc < ?";
            thamSo.add(denNgay.plusDays(1).atStartOfDay());
        }

        if (trangThai != null && !trangThai.isBlank()) {
            sql += " AND t.trang_thai = ?";
            thamSo.add(trangThai);
        }

        sql += " ORDER BY t.tao_luc DESC, t.ma_phieu DESC";

        List<Object[]> rows = truyVan(sql, thamSo.toArray());
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }
}
