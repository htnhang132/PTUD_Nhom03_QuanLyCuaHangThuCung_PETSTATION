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

    @Override
    public int capNhatTiepNhan(Object maLichHen) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='DANG_THUC_HIEN' WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maLichHen, maLichHen);
    }

    @Override
    public int capNhatSucKhoeThuCung(double canNang, String tinhTrang, Object maThuCung) {
        return ThaoTacSQL.capNhat(
                "UPDATE thu_cung SET can_nang=?, tinh_trang_suc_khoe=? WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                canNang, tinhTrang, maThuCung, maThuCung);
    }

    @Override
    public long demTrangThaiSpa(Object maPhieu) {
        return ThaoTacSQL.laySo(
                "SELECT CASE WHEN (ma_goi IS NOT NULL OR don_gia>0 OR tong_tien>0) THEN 1 ELSE 0"
                        + " END FROM phieu_tiep_nhan_spa WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maPhieu, maPhieu);
    }

    @Override
    public long demLichHen(Object maGoi) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, PATINDEX('%[0-9]%', ma_goi), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='HOAT_DONG'",
                maGoi, maGoi);
    }

    @Override
    public long khoaNhanVienDangLam(Object maNhanVien) {
        return ThaoTacSQL.laySo(
                "SELECT 1 FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " dang_lam_viec=1",
                maNhanVien, maNhanVien);
    }

    @Override
    public long layThoiLuongDichVu(Object maGoi) {
        return ThaoTacSQL.laySo(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, PATINDEX('%[0-9]%', ma_goi), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maGoi, maGoi);
    }

    @Override
    public long demLichTrung(Object maNhanVien, Object maThuCung, LocalDateTime ketThuc, LocalDateTime batDau) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ((ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) OR (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))) AND trang_thai NOT IN"
                        + " ('DA_HUY','HOAN_TAT') AND bat_dau<? AND"
                        + " ket_thuc>?",
                maNhanVien, maNhanVien,
                maThuCung, maThuCung,
                ketThuc,
                batDau);
    }

    @Override
    public long themLichHen(
            String maLichHen,
            Object maKhachHang,
            Object maThuCung,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            Object maNhanVien,
            Object maGoi) {
        String maKH = String.valueOf(maKhachHang);
        if (maKhachHang instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_khach_hang FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maKhachHang, maKhachHang);
            if (!r.isEmpty()) maKH = String.valueOf(r.get(0)[0]);
        }
        String maTC = String.valueOf(maThuCung);
        if (maThuCung instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_thu_cung FROM thu_cung WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maThuCung, maThuCung);
            if (!r.isEmpty()) maTC = String.valueOf(r.get(0)[0]);
        }
        String maNV = maNhanVien != null ? String.valueOf(maNhanVien) : null;
        if (maNhanVien instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maNhanVien, maNhanVien);
            if (!r.isEmpty()) maNV = String.valueOf(r.get(0)[0]);
        }
        String maG = maGoi != null ? String.valueOf(maGoi) : null;
        if (maGoi instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_goi FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, PATINDEX('%[0-9]%', ma_goi), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maGoi, maGoi);
            if (!r.isEmpty()) maG = String.valueOf(r.get(0)[0]);
        }
        ThaoTacSQL.capNhat(
                "INSERT INTO lich_hen(ma_lich_hen, ma_khach_hang, ma_thu_cung, loai_dich_vu, bat_dau, ket_thuc, trang_thai, ma_nhan_vien, ma_goi) "
                        + "VALUES(?, ?, ?, 'SPA', ?, ?, 'DA_XAC_NHAN', ?, ?)",
                maLichHen, maKH, maTC, batDau, ketThuc, maNV, maG);
        return DuLieu.layIdCauNoi(null, maLichHen);
    }

    @Override
    public List<Object[]> timTiepNhan(String maLichHen, Object maThuCung) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lich_hen, ma_nhan_vien FROM lich_hen"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='DA_XAC_NHAN'",
                maLichHen, maLichHen,
                maThuCung, maThuCung);
    }

    @Override
    public long themTiepNhan(
            String maPhieu,
            Object maLichHen,
            Object maThuCung,
            double canNang,
            Object maNhanVien,
            String ghiChu) {
        String maHen = maLichHen != null ? String.valueOf(maLichHen) : null;
        if (maLichHen instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_lich_hen FROM lich_hen WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maLichHen, maLichHen);
            if (!r.isEmpty()) maHen = String.valueOf(r.get(0)[0]);
        }
        String maTC = String.valueOf(maThuCung);
        if (maThuCung instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_thu_cung FROM thu_cung WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maThuCung, maThuCung);
            if (!r.isEmpty()) maTC = String.valueOf(r.get(0)[0]);
        }
        String maNV = maNhanVien != null ? String.valueOf(maNhanVien) : null;
        if (maNhanVien instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maNhanVien, maNhanVien);
            if (!r.isEmpty()) maNV = String.valueOf(r.get(0)[0]);
        }
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_tiep_nhan_spa(ma_phieu, ma_lich_hen, ma_thu_cung, can_nang_thuc_te, ma_nhan_vien, ghi_chu, trang_thai, tao_luc) "
                        + "VALUES(?, ?, ?, ?, ?, ?, 'TIEP_NHAN', SYSDATETIME())",
                maPhieu, maHen, maTC, canNang, maNV, ghiChu);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    @Override
    public Object layTiepNhan(Object maLichHen) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_goi FROM lich_hen WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maLichHen, maLichHen);
    }

    @Override
    public List<Object[]> timThemDichVuSpa(Object maPhieu) {
        return ThaoTacSQL.truyVan(
                "SELECT t.can_nang_thuc_te, p.loai, t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t WITH(UPDLOCK,HOLDLOCK) JOIN"
                        + " thu_cung p ON p.ma_thu_cung = t.ma_thu_cung WHERE (t.ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(t.ma_phieu, PATINDEX('%[0-9]%', t.ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maPhieu, maPhieu);
    }

    @Override
    public List<Object[]> layGiaDichVuTheoCanNang(String maGoi, Object loaiThuCung, double canNang) {
        String loai = String.valueOf(loaiThuCung);
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

        long donGiaChon = timGiaTheoCanNang(bangGia, loai, canNang);
        if (donGiaChon < 0) return List.of();
        return List.<Object[]>of(new Object[] {goiId, donGiaChon, tenGoi});
    }

    static long timGiaTheoCanNang(String bangGia, String loaiThuCung, double canNang) {
        boolean laCho = "Chó".equalsIgnoreCase(loaiThuCung) || "DOG".equalsIgnoreCase(loaiThuCung);
        boolean laMeo = "Mèo".equalsIgnoreCase(loaiThuCung) || "CAT".equalsIgnoreCase(loaiThuCung);
        if (!laCho && !laMeo) return -1;
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

    @Override
    public int capNhatThemDichVuSpa(Object maPhieu, Object maGoi, String moTa, double donGia) {
        String maG = String.valueOf(maGoi);
        if (maGoi instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_goi FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, PATINDEX('%[0-9]%', ma_goi), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", maGoi, maGoi);
            if (!r.isEmpty()) maG = String.valueOf(r.get(0)[0]);
        }
        long dg = (long) donGia;
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET ma_goi=COALESCE(ma_goi, ?),"
                        + " mo_ta_dich_vu=CASE WHEN mo_ta_dich_vu IS NULL OR mo_ta_dich_vu=N''"
                        + " THEN ? ELSE mo_ta_dich_vu + N'; ' + ? END,"
                        + " don_gia=don_gia+?, so_luong=CASE WHEN so_luong<=0 THEN 1 ELSE so_luong"
                        + " END, tong_tien=tong_tien+? WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maG,
                moTa,
                moTa,
                dg,
                dg,
                maPhieu,
                maPhieu);
    }

    @Override
    public int tinhLaiTongPhieuSpa(Object maPhieu) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=don_gia*COALESCE(NULLIF(so_luong,0),1)"
                        + " WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maPhieu, maPhieu);
    }

    @Override
    public int capNhatTrangThaiSpa(String trangThaiMoi, Object maPhieu, String trangThaiHienTai) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai=?",
                trangThaiMoi, maPhieu, maPhieu, trangThaiHienTai);
    }

    @Override
    public int hoanTatLichHen(Object maPhieu) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='HOAN_TAT' WHERE ma_lich_hen = (SELECT"
                        + " ma_lich_hen FROM phieu_tiep_nhan_spa WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)))",
                maPhieu, maPhieu);
    }

    @Override
    public int huyLichHenBanGhi(Object maLichHen) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='DA_HUY' WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='DA_XAC_NHAN'",
                maLichHen, maLichHen);
    }

    @Override
    public Object layMaLichHenThuCung(Object maLichHen) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_thu_cung FROM lich_hen WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maLichHen, maLichHen);
    }

    @Override
    public List<Object[]> layPhieuSpaChiTiet(Object maPhieu) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT COALESCE(p.mo_ta_dich_vu, N'Dịch vụ Spa'),"
                        + " COALESCE(NULLIF(p.don_gia, 0), p.tong_tien),"
                        + " COALESCE(NULLIF(p.so_luong, 0), 1), p.tong_tien,"
                        + " p.can_nang_thuc_te, COALESCE(tc.loai, N'Chó')"
                        + " FROM phieu_tiep_nhan_spa p"
                        + " LEFT JOIN thu_cung tc ON tc.ma_thu_cung = p.ma_thu_cung"
                        + " WHERE (p.ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(p.ma_phieu, PATINDEX('%[0-9]%', p.ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))"
                        + " AND (p.ma_goi IS NOT NULL OR p.don_gia > 0 OR p.tong_tien > 0)",
                maPhieu, maPhieu);
        if (rows.isEmpty()) return List.of();
        Object[] r0 = rows.get(0);
        String moTa = r0[0] != null ? r0[0].toString().trim() : "";
        if (!moTa.contains(";")) {
            return List.<Object[]>of(new Object[] {r0[0], r0[1], r0[2], r0[3]});
        }

        String[] dsDichVu = moTa.split(";\\s*");
        double canNang = r0[4] instanceof Number n ? n.doubleValue() : 5.0;
        String loai = r0[5] != null ? r0[5].toString().trim() : "Chó";
        long tongTien = r0[3] instanceof Number n ? n.longValue() : 0L;

        List<Object[]> ketQua = new java.util.ArrayList<>();
        long tongGiaTinh = 0;
        for (String dv : dsDichVu) {
            String ten = dv.trim();
            if (ten.isEmpty()) continue;
            List<Object[]> goiList = ThaoTacSQL.truyVan(
                    "SELECT COALESCE(bang_gia, N'') FROM goi_dich_vu_spa WHERE ten_goi = ? OR ma_goi = ?",
                    ten, ten);
            long gia = -1;
            if (!goiList.isEmpty()) {
                String bg = String.valueOf(goiList.get(0)[0]).trim();
                if (!bg.isEmpty()) {
                    gia = timGiaTheoCanNang(bg, loai, canNang);
                }
            }
            if (gia < 0) gia = 0;
            tongGiaTinh += gia;
            ketQua.add(new Object[] {ten, gia, 1, gia});
        }

        if (ketQua.isEmpty()) {
            return List.<Object[]>of(new Object[] {r0[0], r0[1], r0[2], r0[3]});
        }

        if (tongGiaTinh > 0 && tongTien > 0 && tongGiaTinh != tongTien) {
            long chenhLech = tongTien - tongGiaTinh;
            Object[] last = ketQua.get(ketQua.size() - 1);
            long lastGia = ((Number) last[1]).longValue() + chenhLech;
            ketQua.set(ketQua.size() - 1, new Object[] {last[0], lastGia, 1, lastGia});
        } else if (tongGiaTinh == 0 && tongTien > 0) {
            long chiaDeu = tongTien / ketQua.size();
            for (int i = 0; i < ketQua.size(); i++) {
                long g = (i == ketQua.size() - 1) ? (tongTien - chiaDeu * (ketQua.size() - 1)) : chiaDeu;
                ketQua.set(i, new Object[] {ketQua.get(i)[0], g, 1, g});
            }
        }
        return ketQua;
    }

    public List<Object[]> layChiTietLichHen(long id) {
        return truyVan(
                "SELECT l.ma_thu_cung, COALESCE(l.ma_goi, ''), COALESCE(l.ma_nhan_vien, ''), l.bat_dau FROM lich_hen l"
                        + " WHERE (l.ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_lich_hen, PATINDEX('%[0-9]%', l.ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    public List<Object[]> khoaLichHen(long id) {
        return truyVan(
                "SELECT trang_thai FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    public long demLichTrungKhiSua(
            long id, long nhanVien, Object thuCung, LocalDateTime ketThuc, LocalDateTime batDau) {
        return laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (ma_lich_hen <> CAST(? AS VARCHAR) AND TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) <> TRY_CAST(? AS BIGINT)) AND ((ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) OR (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))) AND"
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
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_khach_hang FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", khachHang, khachHang);
            if (!r.isEmpty()) maKH = String.valueOf(r.get(0)[0]);
        }
        String maTC = String.valueOf(thuCung);
        if (thuCung instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ma_thu_cung FROM thu_cung WHERE (ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, PATINDEX('%[0-9]%', ma_thu_cung), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", thuCung, thuCung);
            if (!r.isEmpty()) maTC = String.valueOf(r.get(0)[0]);
        }
        String maGoi = String.valueOf(goi);
        List<Object[]> rGoi = ThaoTacSQL.truyVan("SELECT ma_goi FROM goi_dich_vu_spa WHERE (ma_goi = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_goi, PATINDEX('%[0-9]%', ma_goi), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", goi, goi);
        if (!rGoi.isEmpty()) maGoi = String.valueOf(rGoi.get(0)[0]);

        String maNV = String.valueOf(nhanVien);
        List<Object[]> rNv = ThaoTacSQL.truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", nhanVien, nhanVien);
        if (!rNv.isEmpty()) maNV = String.valueOf(rNv.get(0)[0]);

        return capNhat(
                "UPDATE lich_hen SET ma_khach_hang=?, ma_thu_cung=?, ma_goi=?, ma_nhan_vien=?, bat_dau=?, ket_thuc=?"
                        + " WHERE (ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lich_hen, PATINDEX('%[0-9]%', ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
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
