package com.petstation.util;

import com.petstation.database.ThaoTacSQL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class DuLieu {
    private static final DateTimeFormatter DINH_DANG_NGAY_HD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private static final Map<String, AtomicLong> BO_DEM_DU_PHONG = new ConcurrentHashMap<>();

    private record QuyTacMa(String bang, String cot, String tienToChuan, int soChuSo) {}

    private DuLieu() {}

    public static long layIdCauNoi(Object thucThe, String maKhoaChinh) {
        if (maKhoaChinh != null && !maKhoaChinh.isBlank()) {
            String ma = maKhoaChinh.trim();
            int idxGach = ma.indexOf('-');
            if (idxGach > 0) {
                ma = ma.substring(0, idxGach);
            }
            StringBuilder chuSo = new StringBuilder();
            for (int i = ma.length() - 1; i >= 0; i--) {
                char c = ma.charAt(i);
                if (Character.isDigit(c)) {
                    chuSo.insert(0, c);
                } else if (chuSo.length() > 0) {
                    break;
                }
            }
            if (chuSo.length() > 0) {
                try {
                    return Long.parseLong(chuSo.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0L;
    }

    public static synchronized String taoMa(String tienTo) {
        String p = tienTo == null ? "MA" : tienTo.trim().toUpperCase();
        if ("HT".equals(p)) p = "HDHT";
        else if ("VC".equals(p)) p = "VOUCHER";
        else if ("P".equals(p) || "PH".equals(p)) p = "P";
        else if ("DH".equals(p)) p = "HD";
        else if ("PN".equals(p) || "NK".equals(p)) p = "PNK";
        else if ("PX".equals(p)) p = "PXK";
        else if ("SYS".equals(p)) p = "LOG";
        else if ("GIO".equals(p)) p = "TREO";

        if ("HD".equals(p)) {
            String tienToNgay = "HD" + LocalDate.now().format(DINH_DANG_NGAY_HD);
            return taoMaHoaDonTheoNgay(tienToNgay);
        }

        QuyTacMa quyTac =
                switch (p) {
                    case "CA" -> new QuyTacMa("ca_lam_viec", "ma_ca", "CA", 2);
                    case "P" -> new QuyTacMa("phong_hotel", "ma_phong", "P", 3);
                    case "NV" -> new QuyTacMa("nhan_vien", "ma_nhan_vien", "NV", 4);
                    case "NCC" -> new QuyTacMa("nha_cung_cap", "ma_nha_cung_cap", "NCC", 4);
                    case "TREOSPA" -> new QuyTacMa("phieu_spa_treo", "ma_phieu", "TREOSPA", 4);
                    case "TREODOI" -> new QuyTacMa("hoa_don", "ma_hoa_don", "TREODOI", 4);
                    case "KH" -> new QuyTacMa("khach_hang", "ma_khach_hang", "KH", 6);
                    case "TC" -> new QuyTacMa("thu_cung", "ma_thu_cung", "TC", 6);
                    case "SP" -> new QuyTacMa("san_pham", "ma_san_pham", "SP", 6);
                    case "HDHT" -> new QuyTacMa("hop_dong_luu_tru", "ma_hop_dong", "HDHT", 6);
                    case "KM" ->
                            new QuyTacMa("chuong_trinh_khuyen_mai", "ma_chuong_trinh", "KM", 6);
                    case "VOUCHER" -> new QuyTacMa("voucher", "ma_voucher", "VOUCHER", 6);
                    case "TREO" -> new QuyTacMa("hoa_don", "ma_hoa_don", "TREO", 6);
                    case "SPA" -> new QuyTacMa("phieu_tiep_nhan_spa", "ma_phieu", "SPA", 8);
                    case "DV", "GOISPA" -> new QuyTacMa("goi_dich_vu_spa", "ma_goi", "SPA", 8);
                    case "LH" -> new QuyTacMa("lich_hen", "ma_lich_hen", "LH", 8);
                    case "LO" -> new QuyTacMa("lo_hang", "ma_lo", "LO", 8);
                    case "PNK" -> new QuyTacMa("phieu_nhap_kho", "ma_phieu_nhap", "PNK", 8);
                    case "PXK" -> new QuyTacMa("phieu_xuat_kho", "ma_phieu_xuat", "PXK", 8);
                    case "KK" -> new QuyTacMa("bien_ban_kiem_ke", "ma_bien_ban", "KK", 8);
                    case "DT" -> new QuyTacMa("phieu_doi_tra", "ma_phieu", "DT", 8);
                    case "NK" -> new QuyTacMa("nhat_ky_cham_soc", "ma_nhat_ky", "NK", 8);
                    case "YC" -> new QuyTacMa("yeu_cau_phe_duyet", "ma_yeu_cau", "YC", 8);
                    case "MPI" -> new QuyTacMa("phien_lam_viec", "ma_phien", "MPI", 8);
                    case "LOG" -> new QuyTacMa("nhat_ky_he_thong", "ma_nhat_ky", "LOG", 8);
                    default -> new QuyTacMa(null, null, p, 8);
                };

        return taoMaTheoQuyTac(quyTac);
    }

    private static String taoMaHoaDonTheoNgay(String tienToNgay) {
        try {
            List<Object[]> ketQua =
                    ThaoTacSQL.truyVan(
                            "SELECT COALESCE(MAX(val), 0) FROM ("
                                    + " SELECT TRY_CAST(RIGHT(ma_hoa_don, 4) AS INT) AS val"
                                    + " FROM hoa_don"
                                    + " WHERE ma_hoa_don LIKE ? AND LEN(ma_hoa_don) = 12"
                                    + ") t WHERE val IS NOT NULL",
                            tienToNgay + "[0-9][0-9][0-9][0-9]");
            long tiepTheo =
                    (ketQua.isEmpty() || ketQua.get(0)[0] == null
                                    ? 0L
                                    : ((Number) ketQua.get(0)[0]).longValue())
                            + 1L;
            String ungVien = tienToNgay + String.format("%04d", tiepTheo);
            while (tonTaiTrongBang("hoa_don", "ma_hoa_don", ungVien)) {
                tiepTheo++;
                ungVien = tienToNgay + String.format("%04d", tiepTheo);
            }
            return ungVien;
        } catch (Throwable ignored) {
            long tiepTheo =
                    BO_DEM_DU_PHONG
                            .computeIfAbsent(tienToNgay, k -> new AtomicLong(0L))
                            .incrementAndGet();
            return tienToNgay + String.format("%04d", tiepTheo);
        }
    }

    private static String taoMaTheoQuyTac(QuyTacMa q) {
        String dinhDang = "%0" + q.soChuSo() + "d";
        if (q.bang() != null && q.cot() != null) {
            try {
                String sql =
                        "SELECT COALESCE(MAX(val), 0) FROM ("
                                + " SELECT TRY_CAST(SUBSTRING("
                                + " CASE WHEN CHARINDEX('-', "
                                + q.cot()
                                + ") > 0 THEN LEFT("
                                + q.cot()
                                + ", CHARINDEX('-', "
                                + q.cot()
                                + ") - 1) ELSE "
                                + q.cot()
                                + " END, ?, 20) AS BIGINT) AS val"
                                + " FROM "
                                + q.bang()
                                + " WHERE "
                                + q.cot()
                                + " LIKE ?"
                                + ") t WHERE val IS NOT NULL";
                List<Object[]> ketQua =
                        ThaoTacSQL.truyVan(
                                sql,
                                q.tienToChuan().length() + 1,
                                q.tienToChuan() + "[0-9]%");
                long tiepTheo =
                        (ketQua.isEmpty() || ketQua.get(0)[0] == null
                                        ? 0L
                                        : ((Number) ketQua.get(0)[0]).longValue())
                                + 1L;
                String ungVien = q.tienToChuan() + String.format(dinhDang, tiepTheo);
                while (tonTaiTrongBang(q.bang(), q.cot(), ungVien)) {
                    tiepTheo++;
                    ungVien = q.tienToChuan() + String.format(dinhDang, tiepTheo);
                }
                return ungVien;
            } catch (Throwable ignored) {
            }
        }
        long tiepTheo =
                BO_DEM_DU_PHONG
                        .computeIfAbsent(q.tienToChuan(), k -> new AtomicLong(0L))
                        .incrementAndGet();
        return q.tienToChuan() + String.format(dinhDang, tiepTheo);
    }

    private static boolean tonTaiTrongBang(String bang, String cot, String giaTri) {
        List<Object[]> r =
                ThaoTacSQL.truyVan(
                        "SELECT COUNT(1) FROM " + bang + " WHERE " + cot + " = ?", giaTri);
        return !r.isEmpty() && r.get(0)[0] != null && ((Number) r.get(0)[0]).longValue() > 0;
    }

    public static String chuyenThanhChuoi(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }
}
