package com.petstation.util;

import com.petstation.database.ThaoTacSQL;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class DuLieu {
    private static final class QuyTacMa {
        final String sequence;
        final String tienTo;
        final int soChuSo;

        private QuyTacMa(String sequence, String tienTo, int soChuSo) {
            this.sequence = sequence;
            this.tienTo = tienTo;
            this.soChuSo = soChuSo;
        }
    }

    private DuLieu() {}

    /**
     * Sinh mã nghiệp vụ an toàn giữa nhiều máy POS: dùng SQL Server SEQUENCE (khai báo trong
     * database/001_schema.sql) và bảng bộ đếm hóa đơn theo ngày. Lỗi CSDL được ném ra để giao dịch
     * hoàn tác, không bao giờ quay về bộ đếm trong RAM.
     */
    public static String taoMa(String tienTo) {
        String p = chuanHoaTienTo(tienTo);
        if ("HD".equals(p)) {
            return taoMaHoaDonTheoNgay(LocalDate.now().format(DinhDang.MA_NGAY));
        }
        QuyTacMa q = layQuyTac(p);
        Object giaTri = ThaoTacSQL.layGiaTriDon("SELECT NEXT VALUE FOR dbo.[" + q.sequence + "]");
        if (!(giaTri instanceof Number so)) {
            throw new IllegalStateException("Không lấy được số thứ tự từ " + q.sequence + ".");
        }
        return q.tienTo + String.format("%0" + q.soChuSo + "d", so.longValue());
    }

    private static String chuanHoaTienTo(String tienTo) {
        String p = tienTo == null ? "MA" : tienTo.trim().toUpperCase();
        return switch (p) {
            case "HT" -> "HDHT";
            case "VC" -> "VOUCHER";
            case "PH" -> "P";
            case "DH" -> "HD";
            case "PN" -> "PNK";
            case "PX" -> "PXK";
            case "SYS" -> "LOG";
            case "GIO" -> "TREO";
            default -> p;
        };
    }

    private static QuyTacMa layQuyTac(String p) {
        return switch (p) {
            case "CA" -> new QuyTacMa("seq_ca_lam_viec", "CA", 2);
            case "P" -> new QuyTacMa("seq_phong_hotel", "P", 3);
            case "NV" -> new QuyTacMa("seq_nhan_vien", "NV", 4);
            case "NCC" -> new QuyTacMa("seq_nha_cung_cap", "NCC", 4);
            case "TREOSPA" -> new QuyTacMa("seq_phieu_spa_treo", "TREOSPA", 4);
            case "TREODOI" -> new QuyTacMa("seq_hoa_don_treo_doi", "TREODOI", 4);
            case "KH" -> new QuyTacMa("seq_khach_hang", "KH", 6);
            case "TC" -> new QuyTacMa("seq_thu_cung", "TC", 6);
            case "SP" -> new QuyTacMa("seq_san_pham", "SP", 6);
            case "HDHT" -> new QuyTacMa("seq_hop_dong_luu_tru", "HDHT", 6);
            case "KM" -> new QuyTacMa("seq_chuong_trinh_khuyen_mai", "KM", 6);
            case "VOUCHER" -> new QuyTacMa("seq_voucher", "VOUCHER", 6);
            case "TREO" -> new QuyTacMa("seq_hoa_don_treo", "TREO", 6);
            case "SPA" -> new QuyTacMa("seq_phieu_tiep_nhan_spa", "SPA", 8);
            case "DV", "GOISPA" -> new QuyTacMa("seq_goi_dich_vu_spa", "SPA", 8);
            case "LH" -> new QuyTacMa("seq_lich_hen", "LH", 8);
            case "LO" -> new QuyTacMa("seq_lo_hang", "LO", 8);
            case "PNK" -> new QuyTacMa("seq_phieu_nhap_kho", "PNK", 8);
            case "PXK" -> new QuyTacMa("seq_phieu_xuat_kho", "PXK", 8);
            case "KK" -> new QuyTacMa("seq_bien_ban_kiem_ke", "KK", 8);
            case "DT" -> new QuyTacMa("seq_phieu_doi_tra", "DT", 8);
            case "NK" -> new QuyTacMa("seq_nhat_ky_cham_soc", "NK", 8);
            case "YC" -> new QuyTacMa("seq_yeu_cau_phe_duyet", "YC", 8);
            case "MPI" -> new QuyTacMa("seq_phien_lam_viec", "MPI", 8);
            case "LOG" -> new QuyTacMa("seq_nhat_ky_he_thong", "LOG", 8);
            default -> new QuyTacMa("seq_ma_chung", p, 8);
        };
    }

    private static String taoMaHoaDonTheoNgay(String ngay) {
        Object giaTri =
                ThaoTacSQL.layGiaTriDon(
                        "MERGE dbo.[bo_dem_hoa_don_ngay] WITH (HOLDLOCK) AS d USING (SELECT CAST(?"
                            + " AS CHAR(6)) AS ngay) AS s ON d.ngay = s.ngay WHEN MATCHED THEN"
                            + " UPDATE SET so_thu_tu = d.so_thu_tu + 1 WHEN NOT MATCHED THEN INSERT"
                            + " (ngay, so_thu_tu) VALUES (s.ngay, 1) OUTPUT inserted.so_thu_tu;",
                        ngay);
        if (!(giaTri instanceof Number so)) {
            throw new IllegalStateException("Không cấp được số hóa đơn cho ngày " + ngay + ".");
        }
        if (so.longValue() > 9999) {
            throw new IllegalArgumentException("Đã vượt 9999 hóa đơn trong ngày " + ngay + ".");
        }
        return "HD" + ngay + String.format("%04d", so.longValue());
    }

    public static LocalDate chuyenLocalDate(Object giaTri) {
        if (giaTri instanceof LocalDate d) return d;
        if (giaTri instanceof LocalDateTime dt) return dt.toLocalDate();
        if (giaTri instanceof Date d) {
            return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static LocalDateTime chuyenLocalDateTime(Object giaTri) {
        if (giaTri instanceof LocalDateTime dt) return dt;
        if (giaTri instanceof Date d) {
            return Instant.ofEpochMilli(d.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return null;
    }

    public static String chuyenThanhChuoi(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }

    public static long layGiaTri(Object giaTri) {
        if (giaTri instanceof Number n) return n.longValue();
        if (giaTri == null) return 0L;
        String s = String.valueOf(giaTri).trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    public static int tinhThangTuoi(LocalDate ngaySinh, LocalDate denNgay) {
        if (ngaySinh == null || denNgay == null || ngaySinh.isAfter(denNgay)) {
            return 0;
        }
        return (int) ChronoUnit.MONTHS.between(ngaySinh, denNgay);
    }

    public static void kiemTraDuong(long soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng/giá trị phải lớn hơn 0.");
        }
    }
}
