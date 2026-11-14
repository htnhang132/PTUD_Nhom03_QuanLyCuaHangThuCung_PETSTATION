package com.petstation.dao.kho.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.kho.SanPhamDAO;
import com.petstation.model.kho.SanPham;

import java.util.List;

public final class SanPhamDAOImpl implements SanPhamDAO {
    public SanPhamDAOImpl() {}

    @Override
    public List<SanPham> layTatCa() {
        return truyVanDanhSach(
                "SELECT ma_san_pham, ten, nhom_hang, don_vi_tinh, gia_ban,"
                        + " gia_nhap_gan_nhat, vat_phan_tram, trang_thai, mo_ta, barcode FROM"
                        + " san_pham ORDER BY ma_san_pham ASC",
                r -> chuyenDong(r));
    }

    @Override
    public SanPham timTheoMa(String maSanPham) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_san_pham, ten, nhom_hang, don_vi_tinh, gia_ban,"
                            + " gia_nhap_gan_nhat, vat_phan_tram, trang_thai, mo_ta, barcode FROM"
                            + " san_pham WHERE (ma_san_pham = CAST(? AS VARCHAR) OR barcode ="
                            + " CAST(? AS VARCHAR))",
                        maSanPham,
                        maSanPham);
        if (rows.isEmpty()) return null;
        return chuyenDong(rows.get(0));
    }

    @Override
    public List<SanPham> layDanhSachSanPham() {
        return layTatCa();
    }

    @Override
    public int capNhatBarcode(String maSanPham, String barcode) {
        return capNhat("UPDATE san_pham SET barcode=? WHERE ma_san_pham = ?", barcode, maSanPham);
    }

    @Override
    public boolean kiemTraSanPhamCoGiaoDich(String maSanPham) {
        return laySo("SELECT COUNT(*) FROM chi_tiet_hoa_don WHERE ma_san_pham = ?", maSanPham) > 0;
    }

    @Override
    public int ngungKinhDoanhSanPham(String maSanPham) {
        return capNhat(
                "UPDATE san_pham SET trang_thai='NGUNG_BAN' WHERE ma_san_pham = ?", maSanPham);
    }

    private SanPham chuyenDong(Object[] r) {
        SanPham sp = new SanPham();
        sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
        sp.setTenSanPham(r[1] != null ? r[1].toString() : null);
        sp.setNhomHang(r[2] != null ? r[2].toString() : null);
        sp.setDonVi(r[3] != null ? r[3].toString() : null);
        sp.setGiaBan(r[4] != null ? ((Number) r[4]).longValue() : 0L);
        sp.setGiaNhapGanNhat(r[5] != null ? ((Number) r[5]).longValue() : 0L);
        sp.setMoTa(r[8] != null ? r[8].toString() : null);
        sp.setBarcode(r[9] != null ? r[9].toString() : null);
        return sp;
    }
}
