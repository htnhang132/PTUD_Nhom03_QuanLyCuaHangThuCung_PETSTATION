package com.petstation.dao.kho.impl;

import com.petstation.dao.kho.NhapXuatKhoDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.chung.LoaiPhieuXuat;
import com.petstation.model.chung.TrangThaiPhieuNhap;
import com.petstation.model.chung.TrangThaiPhieuXuat;
import com.petstation.model.kho.ChiTietPhieuNhap;
import com.petstation.model.kho.ChiTietPhieuXuat;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.util.DuLieu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NhapXuatKhoDAOImpl implements NhapXuatKhoDAO {
    public NhapXuatKhoDAOImpl() {}

    @Override
    public long demNhapKho(String maSanPham) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham = ? AND"
                        + " theo_doi_hsd = 1",
                maSanPham);
    }

    @Override
    public long demNhaCungCapDangHopTac(String maNhaCungCap) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nha_cung_cap WHERE ma_nha_cung_cap = ? AND"
                        + " trang_thai = 'DANG_HOP_TAC'",
                maNhaCungCap);
    }

    @Override
    public String themNhapKho(
            String maPhieu, String maNhaCungCap, String maNhanVien, long tongTien, long daTra) {
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_nhap_kho(ma_phieu_nhap, ma_nha_cung_cap, ma_nhan_vien,"
                    + " ngay_nhap, tong_tien, trang_thai, da_tra) VALUES(?, ?, ?, CAST(GETDATE() AS"
                    + " DATE), ?, ?, ?)",
                maPhieu,
                maNhaCungCap,
                maNhanVien,
                tongTien,
                TrangThaiPhieuNhap.DA_NHAP.maDB(),
                daTra);
        return maPhieu;
    }

    @Override
    public int themChiTietNhapKho(
            String maPhieuNhap,
            String maSanPham,
            String maLo,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_phieu_nhap(ma_phieu_nhap, ma_san_pham, ma_lo, han_su_dung,"
                        + " so_luong, gia_nhap) VALUES(?, ?, ?, ?, ?, ?)",
                maPhieuNhap,
                maSanPham,
                maLo,
                hanSuDung,
                soLuong,
                giaNhap);
    }

    @Override
    public Optional<LoHang> timNhapKho(String maSanPham, String maHoacSoLo) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_lo, han_su_dung FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_san_pham = ? AND (so_lo = ? OR ma_lo = ?)",
                        maSanPham,
                        maHoacSoLo,
                        maHoacSoLo);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Object[] r = rows.get(0);
        LoHang lo = new LoHang();
        lo.setMaLo(r[0] != null ? r[0].toString() : null);
        lo.setHanSuDung(DuLieu.chuyenLocalDate(r[1]));
        return Optional.of(lo);
    }

    @Override
    public int themLoHang(String maSanPham, String soLo, LocalDate hanSuDung, int soLuong) {
        String maLo = soLo.matches("LO\\d{8}") ? soLo : DuLieu.taoMa("LO");
        return ThaoTacSQL.capNhat(
                "INSERT INTO lo_hang(ma_lo, ma_san_pham, so_lo, ngay_nhap, han_su_dung,"
                    + " so_luong_nhap, so_luong_ton) VALUES(?, ?, ?, CAST(GETDATE() AS DATE), ?, ?,"
                    + " ?)",
                maLo,
                maSanPham,
                soLo,
                hanSuDung,
                soLuong,
                soLuong);
    }

    @Override
    public int capNhatNhapKho(int soLuong, String maLo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton = so_luong_ton + ?,"
                        + " so_luong_nhap = COALESCE(so_luong_nhap, 0) + ?,"
                        + " trang_thai = CASE WHEN so_luong_ton + ? > 0 THEN 'SAN_SANG' ELSE"
                        + " trang_thai END WHERE ma_lo = ?",
                soLuong,
                soLuong,
                soLuong,
                maLo);
    }

    @Override
    public int capNhatGiaNhapGanNhat(long giaNhap, String maSanPham) {
        return ThaoTacSQL.capNhat(
                "UPDATE san_pham SET gia_nhap_gan_nhat = ? WHERE ma_san_pham = ?",
                giaNhap,
                maSanPham);
    }

    @Override
    public List<ChiTietPhieuNhap> layNhapKhoChiTiet(String maPhieuNhap) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT p.ten, c.ma_lo, l.so_lo, c.han_su_dung, c.so_luong, c.gia_nhap"
                                + " FROM chi_tiet_phieu_nhap c JOIN san_pham p ON p.ma_san_pham ="
                                + " c.ma_san_pham LEFT JOIN lo_hang l ON l.ma_lo = c.ma_lo WHERE"
                                + " c.ma_phieu_nhap = ? ORDER BY p.ma_san_pham",
                        maPhieuNhap);
        List<ChiTietPhieuNhap> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            SanPham sp = new SanPham();
            sp.setTenSanPham(r[0] != null ? r[0].toString() : null);
            LoHang lo = new LoHang();
            lo.setSanPham(sp);
            lo.setMaLo(r[1] != null ? r[1].toString() : null);
            lo.setSoLo(r[2] != null ? r[2].toString() : null);
            lo.setHanSuDung(DuLieu.chuyenLocalDate(r[3]));
            ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
            ct.setSanPham(sp);
            ct.setLoHang(lo);
            ct.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : 0);
            ct.setGiaNhap(r[5] != null ? ((Number) r[5]).longValue() : 0L);
            ketQua.add(ct);
        }
        return ketQua;
    }

    @Override
    public long layCongNoConLai(String maPhieuNhap) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(MAX(tong_tien - da_tra), 0) FROM phieu_nhap_kho WHERE"
                        + " ma_phieu_nhap = ?",
                maPhieuNhap);
    }

    @Override
    public int themThanhToanNhaCungCap(String maPhieuNhap, long daTra) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_nhap_kho SET da_tra = da_tra + ? WHERE ma_phieu_nhap = ? AND"
                        + " da_tra + ? <= tong_tien AND trang_thai = 'DA_NHAP'",
                daTra,
                maPhieuNhap,
                daTra);
    }

    @Override
    public Optional<LoHang> timXuatHuy(String maLo) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT l.so_luong_ton, COALESCE(p.gia_nhap_gan_nhat, p.gia_ban) FROM"
                            + " lo_hang l WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p ON p.ma_san_pham ="
                            + " l.ma_san_pham WHERE l.ma_lo = ?",
                        maLo);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Object[] r = rows.get(0);
        LoHang lo = new LoHang();
        lo.setSoLuongTon(r[0] != null ? ((Number) r[0]).intValue() : 0);
        lo.setGiaNhap(r[1] != null ? ((Number) r[1]).longValue() : 0L);
        return Optional.of(lo);
    }

    @Override
    public String themXuatHuy(String maPhieu, String maNhanVien, String lyDo, long tongGiaTri) {
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_xuat_kho(ma_phieu_xuat, ma_nhan_vien, loai_xuat, ly_do,"
                        + " tong_gia_tri, trang_thai) VALUES(?, ?, ?, ?, ?, ?)",
                maPhieu,
                maNhanVien,
                LoaiPhieuXuat.XUAT_HUY.maDB(),
                lyDo,
                tongGiaTri,
                TrangThaiPhieuXuat.CHO_XUAT.maDB());
        return maPhieu;
    }

    @Override
    public int capNhatXuatHuy(String maPhieuXuat, String maLo, int soLuong, long giaTri) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_phieu_xuat(ma_phieu_xuat, ma_lo, so_luong, gia_tri) VALUES(?,"
                        + " ?, ?, ?)",
                maPhieuXuat,
                maLo,
                soLuong,
                giaTri);
    }

    @Override
    public long demApDungXuatHuy(String maPhieuXuat) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_xuat_kho WITH(UPDLOCK,HOLDLOCK) WHERE ma_phieu_xuat = ?"
                        + " AND trang_thai = ?",
                maPhieuXuat,
                TrangThaiPhieuXuat.CHO_XUAT.maDB());
    }

    @Override
    public List<ChiTietPhieuXuat> timApDungXuatHuy(String maPhieuXuat) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_lo, so_luong FROM chi_tiet_phieu_xuat WHERE ma_phieu_xuat = ?",
                        maPhieuXuat);
        List<ChiTietPhieuXuat> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            ChiTietPhieuXuat ct = new ChiTietPhieuXuat();
            ct.setMaLo(r[0] != null ? r[0].toString() : null);
            ct.setSoLuong(r[1] != null ? ((Number) r[1]).intValue() : 0);
            ketQua.add(ct);
        }
        return ketQua;
    }

    @Override
    public int truTonXuatHuy(int soLuong, String maLo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton = so_luong_ton - ?,"
                        + " trang_thai = CASE WHEN so_luong_ton - ? <= 0 THEN 'HET_HANG' ELSE"
                        + " trang_thai END WHERE ma_lo = ? AND so_luong_ton >= ?",
                soLuong,
                soLuong,
                maLo,
                soLuong);
    }

    @Override
    public int capNhatApDungXuatHuy(String maPhieuXuat) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai = ? WHERE ma_phieu_xuat = ?",
                TrangThaiPhieuXuat.DA_XUAT.maDB(),
                maPhieuXuat);
    }
}
