package com.petstation.dao.impl;

import com.petstation.dao.KhoDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC kho DAO. */
public final class KhoDAOImpl implements KhoDAO {
    public KhoDAOImpl() {}

    public int capNhatNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", cacThamSo);
    }

    public int capNhatGiaNhapGanNhat(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE san_pham SET gia_nhap_gan_nhat=? WHERE id=?", cacThamSo);
    }

    public List<Object[]> timApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT lo_hang_id,so_luong FROM chi_tiet_phieu_xuat WHERE phieu_xuat_id=?",
                cacThamSo);
    }

    public int capNhatApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='POSTED' WHERE id=?", cacThamSo);
    }

    public int capNhatApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=? WHERE id=? AND so_luong_ton=?", cacThamSo);
    }

    public int danhDauKiemKeDaDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='APPROVED' WHERE id=?", cacThamSo);
    }

    public long demNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND theo_doi_hsd=1",
                cacThamSo);
    }

    public long demNhaCungCapDangHopTac(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nha_cung_cap WHERE id=? AND" + " trang_thai='COOPERATING'",
                cacThamSo);
    }

    public long themNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_nhap_kho(so_phieu,nha_cung_cap_id,nguoi_lap_id,ngay_nhap,tong_tien,trang_thai,da_tra)"
                    + " VALUES(?,?,?,CAST(GETDATE() AS DATE),?,'POSTED',?)",
                cacThamSo);
    }

    public int themChiTietNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_phieu_nhap(phieu_nhap_id,san_pham_id,so_lo,han_su_dung,so_luong,gia_nhap)"
                    + " VALUES(?,?,?,?,?,?)",
                cacThamSo);
    }

    public List<Object[]> timNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,han_su_dung FROM lo_hang WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE san_pham_id=? AND so_lo=?",
                cacThamSo);
    }

    public int themLoHang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                        + " VALUES(?,?,?,?)",
                cacThamSo);
    }

    public List<Object[]> timXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.so_luong_ton,COALESCE(p.gia_nhap_gan_nhat,p.gia_ban)"
                        + " FROM lo_hang l WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p"
                        + " ON p.id=l.san_pham_id WHERE l.id=?",
                cacThamSo);
    }

    public long themXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_xuat_kho(so_phieu,nguoi_lap_id,loai_xuat,ly_do,tong_gia_tri,trang_thai)"
                    + " VALUES(?,?,'WRITE_OFF',?,?,'DRAFT')",
                cacThamSo);
    }

    public int capNhatXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " chi_tiet_phieu_xuat(phieu_xuat_id,lo_hang_id,so_luong,gia_tri)"
                        + " VALUES(?,?,?,?)",
                cacThamSo);
    }

    public long demApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_xuat_kho WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='DRAFT'",
                cacThamSo);
    }

    public int truTonXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-? WHERE id=? AND"
                        + " so_luong_ton>=?",
                cacThamSo);
    }

    public List<Object[]> timKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE" + " id=?",
                cacThamSo);
    }

    public long themKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                        + " bien_ban_kiem_ke(so_bien_ban,nguoi_lap_id,ky_kiem_ke,trang_thai)"
                        + " VALUES(?,?,?,?)",
                cacThamSo);
    }

    public int capNhatKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_kiem_ke(bien_ban_id,lo_hang_id,ton_he_thong,ton_thuc_te,nguyen_nhan)"
                    + " VALUES(?,?,?,?,?)",
                cacThamSo);
    }

    public long demApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM bien_ban_kiem_ke WITH(UPDLOCK,HOLDLOCK) WHERE id=?"
                        + " AND trang_thai='PENDING'",
                cacThamSo);
    }

    public List<Object[]> timApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT lo_hang_id,ton_he_thong,ton_thuc_te FROM chi_tiet_kiem_ke WHERE"
                        + " bien_ban_id=?",
                cacThamSo);
    }

    public int themThanhToanNhaCungCap(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_nhap_kho SET da_tra=da_tra+? WHERE id=? AND"
                        + " da_tra+?<=tong_tien AND trang_thai='POSTED'",
                cacThamSo);
    }

    public List<Object[]> layNhapKhoChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ten,c.so_lo,c.han_su_dung,c.so_luong,c.gia_nhap FROM"
                        + " chi_tiet_phieu_nhap c JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                        + " c.phieu_nhap_id=? ORDER BY c.id",
                cacThamSo);
    }

    public List<Object[]> layDanhSachLoHangKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.id, p.ma_san_pham, p.ten, COALESCE(p.barcode, ''), l.so_lo,"
                    + " l.han_su_dung, l.so_luong_ton, p.loai_san_pham FROM lo_hang l JOIN san_pham"
                    + " p ON p.id = l.san_pham_id WHERE l.trang_thai = 'ACTIVE' AND p.trang_thai ="
                    + " 'ACTIVE' ORDER BY p.ma_san_pham, l.han_su_dung",
                cacThamSo);
    }

    public List<Object[]> layChiTietKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT c.id, p.ma_san_pham, p.ten, l.so_lo, l.han_su_dung, c.ton_he_thong,"
                    + " c.ton_thuc_te, (c.ton_thuc_te - c.ton_he_thong) AS chenh_lech,"
                    + " COALESCE(c.nguyen_nhan, N'') FROM chi_tiet_kiem_ke c JOIN lo_hang l ON l.id"
                    + " = c.lo_hang_id JOIN san_pham p ON p.id = l.san_pham_id WHERE c.bien_ban_id"
                    + " = ? ORDER BY c.id",
                cacThamSo);
    }
}
