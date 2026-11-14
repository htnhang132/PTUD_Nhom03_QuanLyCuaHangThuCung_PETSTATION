package com.petstation.dao.kho.impl;

import com.petstation.dao.kho.KiemKeDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.kho.ChiTietKiemKe;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class KiemKeDAOImpl implements KiemKeDAO {
    public KiemKeDAOImpl() {}

    @Override
    public List<LoHang> layDanhSachLoHangKiemKe() {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT l.ma_lo, p.ma_san_pham, p.ten, COALESCE(p.barcode, ''), l.so_lo,"
                            + " l.han_su_dung, l.so_luong_ton, p.nhom_hang FROM lo_hang l JOIN"
                            + " san_pham p ON p.ma_san_pham = l.ma_san_pham WHERE l.trang_thai ="
                            + " 'SAN_SANG' AND p.trang_thai = 'DANG_BAN' ORDER BY p.ma_san_pham,"
                            + " l.han_su_dung");
        List<LoHang> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            SanPham sp = new SanPham();
            sp.setMaSanPham(r[1] != null ? r[1].toString() : null);
            sp.setTenSanPham(r[2] != null ? r[2].toString() : null);
            sp.setBarcode(r[3] != null ? r[3].toString() : "");
            sp.setNhomHang(r[7] != null ? r[7].toString() : null);
            LoHang lo = new LoHang();
            lo.setMaLo(r[0] != null ? r[0].toString() : null);
            lo.setSanPham(sp);
            lo.setSoLo(r[4] != null ? r[4].toString() : null);
            lo.setHanSuDung(DuLieu.chuyenLocalDate(r[5]));
            lo.setSoLuongTon(r[6] != null ? ((Number) r[6]).intValue() : 0);
            ketQua.add(lo);
        }
        return ketQua;
    }

    @Override
    public Optional<Integer> timKiemKe(String maLo) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE ma_lo = ?",
                        maLo);
        if (rows.isEmpty() || rows.get(0)[0] == null) {
            return Optional.empty();
        }
        return Optional.of(((Number) rows.get(0)[0]).intValue());
    }

    @Override
    public String themKiemKe(
            String maBienBan, String maNhanVien, String kyKiemKe, String trangThai) {
        ThaoTacSQL.capNhat(
                "INSERT INTO bien_ban_kiem_ke(ma_bien_ban, ma_nhan_vien, ky_kiem_ke, trang_thai)"
                        + " VALUES(?, ?, ?, ?)",
                maBienBan,
                maNhanVien,
                kyKiemKe,
                trangThai);
        return maBienBan;
    }

    @Override
    public int capNhatKiemKe(
            String maBienBan, String maLo, int tonHeThong, int tonThucTe, String nguyenNhan) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_kiem_ke(ma_bien_ban, ma_lo, ton_he_thong, ton_thuc_te,"
                        + " nguyen_nhan) VALUES(?, ?, ?, ?, ?)",
                maBienBan,
                maLo,
                tonHeThong,
                tonThucTe,
                nguyenNhan);
    }

    @Override
    public List<ChiTietKiemKe> layChiTietKiemKe(String maBienBan) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT c.ma_lo, p.ma_san_pham, p.ten, l.so_lo, l.han_su_dung,"
                            + " c.ton_he_thong, c.ton_thuc_te, (c.ton_thuc_te - c.ton_he_thong) AS"
                            + " chenh_lech, COALESCE(c.nguyen_nhan, N'') FROM chi_tiet_kiem_ke c"
                            + " JOIN lo_hang l ON l.ma_lo = c.ma_lo JOIN san_pham p ON"
                            + " p.ma_san_pham = l.ma_san_pham WHERE c.ma_bien_ban = ? ORDER BY"
                            + " p.ma_san_pham",
                        maBienBan);
        List<ChiTietKiemKe> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            SanPham sp = new SanPham();
            sp.setMaSanPham(r[1] != null ? r[1].toString() : null);
            sp.setTenSanPham(r[2] != null ? r[2].toString() : null);
            LoHang lo = new LoHang();
            lo.setMaLo(r[0] != null ? r[0].toString() : null);
            lo.setSanPham(sp);
            lo.setSoLo(r[3] != null ? r[3].toString() : null);
            lo.setHanSuDung(DuLieu.chuyenLocalDate(r[4]));
            ChiTietKiemKe ct = new ChiTietKiemKe();
            ct.setLoHang(lo);
            ct.setTonHeThong(r[5] != null ? ((Number) r[5]).intValue() : 0);
            ct.setTonThucTe(r[6] != null ? ((Number) r[6]).intValue() : 0);
            ct.setNguyenNhan(r[8] != null ? r[8].toString() : "");
            ketQua.add(ct);
        }
        return ketQua;
    }

    @Override
    public long demApDungKiemKe(String maBienBan) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM bien_ban_kiem_ke WITH(UPDLOCK,HOLDLOCK) WHERE ma_bien_ban ="
                        + " ? AND trang_thai='CHO_DUYET'",
                maBienBan);
    }

    @Override
    public List<ChiTietKiemKe> timApDungKiemKe(String maBienBan) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_lo, ton_he_thong, ton_thuc_te FROM chi_tiet_kiem_ke WHERE"
                                + " ma_bien_ban = ?",
                        maBienBan);
        List<ChiTietKiemKe> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            ChiTietKiemKe ct = new ChiTietKiemKe();
            ct.setMaLo(r[0] != null ? r[0].toString() : null);
            ct.setTonHeThong(r[1] != null ? ((Number) r[1]).intValue() : 0);
            ct.setTonThucTe(r[2] != null ? ((Number) r[2]).intValue() : 0);
            ketQua.add(ct);
        }
        return ketQua;
    }

    @Override
    public int capNhatApDungKiemKe(int tonThucTe, String maLo, int tonHeThong) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=? WHERE ma_lo = ? AND so_luong_ton=?",
                tonThucTe,
                maLo,
                tonHeThong);
    }

    @Override
    public int danhDauKiemKeDaDuyet(String maBienBan) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='DA_DUYET' WHERE ma_bien_ban = ?",
                maBienBan);
    }
}
