package com.petstation.dao.impl;

import com.petstation.dao.KhoDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.service.PhienDangNhapService;
import com.petstation.util.DuLieu;

import java.util.List;

public final class KhoDAOImpl implements KhoDAO {
    public KhoDAOImpl() {}

    public int capNhatNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+?, so_luong_nhap=COALESCE(so_luong_nhap,0)+? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    public int capNhatGiaNhapGanNhat(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE san_pham SET gia_nhap_gan_nhat=? WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    public List<Object[]> timApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo,so_luong FROM chi_tiet_phieu_xuat WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public int capNhatApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='DA_XUAT' WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public int capNhatApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND so_luong_ton=?",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int danhDauKiemKeDaDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='DA_DUYET' WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public long demNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND theo_doi_hsd=1",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public long demNhaCungCapDangHopTac(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nha_cung_cap WHERE (ma_nha_cung_cap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nha_cung_cap, 4, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='DANG_HOP_TAC'",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public long themNhapKho(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        String maNcc = cacThamSo[1] != null ? String.valueOf(cacThamSo[1]) : "";
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> rows = ThaoTacSQL.truyVan(
                    "SELECT ma_nha_cung_cap FROM nha_cung_cap WHERE (ma_nha_cung_cap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nha_cung_cap, 4, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!rows.isEmpty()) {
                maNcc = String.valueOf(rows.get(0)[0]);
            }
        }
        String tenDangNhap = PhienDangNhapService.layTenDangNhap();
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_nhap_kho(ma_phieu_nhap, ma_nha_cung_cap, ten_dang_nhap, ngay_nhap, tong_tien, trang_thai, da_tra) "
                        + "VALUES(?, ?, ?, CAST(GETDATE() AS DATE), ?, 'DA_NHAP', ?)",
                maPhieu,
                maNcc,
                tenDangNhap,
                cacThamSo[3],
                cacThamSo[4]);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    public int themChiTietNhapKho(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_phieu_nhap FROM phieu_nhap_kho WHERE (ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_nhap, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maPhieu = String.valueOf(r.get(0)[0]);
        }
        String maSP = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_san_pham FROM san_pham WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maSP = String.valueOf(r.get(0)[0]);
        }
        String maLo = String.valueOf(cacThamSo[2]);
        if (cacThamSo[2] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[2], cacThamSo[2]);
            if (!r.isEmpty()) maLo = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_phieu_nhap(ma_phieu_nhap, ma_san_pham, ma_lo, han_su_dung, so_luong, gia_nhap) "
                        + "VALUES(?, ?, ?, ?, ?, ?)",
                maPhieu,
                maSP,
                maLo,
                cacThamSo[3],
                cacThamSo[4],
                cacThamSo[5]);
    }

    public List<Object[]> timNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo, han_su_dung FROM lo_hang WITH(UPDLOCK,HOLDLOCK) "
                        + "WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) "
                        + "AND (so_lo = ? OR ma_lo = ?)",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    public int themLoHang(Object... cacThamSo) {
        String maSP = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_san_pham FROM san_pham WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maSP = String.valueOf(r.get(0)[0]);
        }
        String soLo = String.valueOf(cacThamSo[1]);
        String maLo = soLo.startsWith("LO") ? soLo : DuLieu.taoMa("LO");
        return ThaoTacSQL.capNhat(
                "INSERT INTO lo_hang(ma_lo, ma_san_pham, so_lo, ngay_nhap, han_su_dung, so_luong_nhap, so_luong_ton) "
                        + "VALUES(?, ?, ?, CAST(GETDATE() AS DATE), ?, ?, ?)",
                maLo,
                maSP,
                soLo,
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[3]);
    }

    public List<Object[]> timXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.so_luong_ton, COALESCE(p.gia_nhap_gan_nhat, p.gia_ban) "
                        + "FROM lo_hang l WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham "
                        + "WHERE (l.ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public long themXuatHuy(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        String tenDangNhap = PhienDangNhapService.layTenDangNhap();
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_xuat_kho(ma_phieu_xuat, ten_dang_nhap, loai_xuat, ly_do, tong_gia_tri, trang_thai) "
                        + "VALUES(?, ?, 'XUAT_HUY', ?, ?, 'CHO_XUAT')",
                maPhieu,
                tenDangNhap,
                cacThamSo[2],
                cacThamSo[3]);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    public int capNhatXuatHuy(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_phieu_xuat FROM phieu_xuat_kho WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maPhieu = String.valueOf(r.get(0)[0]);
        }
        String maLo = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maLo = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_phieu_xuat(ma_phieu_xuat, ma_lo, so_luong, gia_tri) VALUES(?, ?, ?, ?)",
                maPhieu,
                maLo,
                cacThamSo[2],
                cacThamSo[3]);
    }

    public long demApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_xuat_kho WITH(UPDLOCK,HOLDLOCK) WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='CHO_XUAT'",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public int truTonXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND so_luong_ton>=?",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public List<Object[]> timKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public long themKiemKe(Object... cacThamSo) {
        String maBienBan = String.valueOf(cacThamSo[0]);
        String tenDangNhap = PhienDangNhapService.layTenDangNhap();
        ThaoTacSQL.capNhat(
                "INSERT INTO bien_ban_kiem_ke(ma_bien_ban, ten_dang_nhap, ky_kiem_ke, trang_thai) VALUES(?, ?, ?, ?)",
                maBienBan,
                tenDangNhap,
                cacThamSo[2],
                cacThamSo[3]);
        return DuLieu.layIdCauNoi(null, maBienBan);
    }

    public int capNhatKiemKe(Object... cacThamSo) {
        String maBienBan = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_bien_ban FROM bien_ban_kiem_ke WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maBienBan = String.valueOf(r.get(0)[0]);
        }
        String maLo = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maLo = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_kiem_ke(ma_bien_ban, ma_lo, ton_he_thong, ton_thuc_te, nguyen_nhan) VALUES(?, ?, ?, ?, ?)",
                maBienBan,
                maLo,
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[4]);
    }

    public long demApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM bien_ban_kiem_ke WITH(UPDLOCK,HOLDLOCK) WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='CHO_DUYET'",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> timApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo, ton_he_thong, ton_thuc_te FROM chi_tiet_kiem_ke WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public int themThanhToanNhaCungCap(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_nhap_kho SET da_tra=da_tra+? WHERE (ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_nhap, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND da_tra+?<=tong_tien AND trang_thai='DA_NHAP'",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public long layCongNoConLai(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(MAX(tong_tien - da_tra), 0) FROM phieu_nhap_kho WHERE (ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_nhap, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> layNhapKhoChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ten, c.ma_lo, c.han_su_dung, c.so_luong, c.gia_nhap FROM chi_tiet_phieu_nhap c JOIN san_pham p ON p.ma_san_pham = c.ma_san_pham WHERE (c.ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_phieu_nhap, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY p.ma_san_pham",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> layDanhSachLoHangKiemKe(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT l.ma_lo, p.ma_san_pham, p.ten, COALESCE(p.barcode, ''), l.so_lo, l.han_su_dung, l.so_luong_ton, p.nhom_hang FROM lo_hang l JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham WHERE l.trang_thai = 'SAN_SANG' AND p.trang_thai = 'DANG_BAN' ORDER BY p.ma_san_pham, l.han_su_dung",
                cacThamSo);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    public List<Object[]> layChiTietKiemKe(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT c.ma_lo, p.ma_san_pham, p.ten, l.so_lo, l.han_su_dung, c.ton_he_thong, c.ton_thuc_te, (c.ton_thuc_te - c.ton_he_thong) AS chenh_lech, COALESCE(c.nguyen_nhan, N'') FROM chi_tiet_kiem_ke c JOIN lo_hang l ON l.ma_lo = c.ma_lo JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham WHERE (c.ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_bien_ban, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY p.ma_san_pham",
                cacThamSo[0],
                cacThamSo[0]);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }
}
