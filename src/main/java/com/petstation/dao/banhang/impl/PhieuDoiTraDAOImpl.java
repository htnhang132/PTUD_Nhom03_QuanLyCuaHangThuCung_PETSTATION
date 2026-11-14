package com.petstation.dao.banhang.impl;

import com.petstation.dao.banhang.PhieuDoiTraDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.banhang.ChiTietDoiTra;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.chung.TrangThaiLoHang;
import com.petstation.model.chung.TrangThaiPhieuDoiTra;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PhieuDoiTraDAOImpl implements PhieuDoiTraDAO {
    public PhieuDoiTraDAOImpl() {}

    @Override
    public int capNhatApDungDoiTra(int soLuong, String maLo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE ma_lo=?", soLuong, maLo);
    }

    @Override
    public long demDoiTraMuc(String maHoaDon, String maSanPham) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(c.so_luong),0) FROM chi_tiet_doi_tra c JOIN phieu_doi_tra"
                        + " p ON p.ma_phieu=c.ma_phieu WHERE c.ma_hoa_don=? AND c.ma_san_pham=?"
                        + " AND p.trang_thai IN ('CHO_XU_LY','HOAN_TAT')",
                maHoaDon,
                maSanPham);
    }

    @Override
    public List<LoHang> laySoLuongLoConDuocDoi(String maHoaDon, String maSanPham) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT COALESCE(b.ma_lo, (SELECT TOP 1 ma_lo FROM lo_hang WHERE"
                            + " ma_san_pham=b.ma_san_pham ORDER BY ngay_nhap ASC), (SELECT TOP 1"
                            + " ma_lo FROM lo_hang ORDER BY ngay_nhap ASC), 'LO-DEFAULT'),"
                            + " b.so_luong-COALESCE((SELECT SUM(c.so_luong) FROM chi_tiet_doi_tra c"
                            + " JOIN phieu_doi_tra p ON p.ma_phieu=c.ma_phieu WHERE"
                            + " c.ma_hoa_don=b.ma_hoa_don AND c.ma_san_pham=b.ma_san_pham AND"
                            + " (c.ma_lo=b.ma_lo OR b.ma_lo IS NULL) AND p.trang_thai IN"
                            + " ('CHO_XU_LY','HOAN_TAT')),0) FROM chi_tiet_hoa_don b WHERE"
                            + " b.ma_hoa_don=? AND b.ma_san_pham=? ORDER BY b.ma_san_pham",
                        maHoaDon,
                        maSanPham);
        List<LoHang> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            LoHang lo = new LoHang();
            lo.setMaLo(r[0] != null ? r[0].toString() : null);
            int conDuocDoi = r[1] != null ? ((Number) r[1]).intValue() : 0;
            lo.setSoLuongTon(Math.max(0, conDuocDoi));
            ketQua.add(lo);
        }
        return ketQua;
    }

    @Override
    public int capNhatDoiTraMuc(
            String maPhieu,
            String maHoaDon,
            String maSanPham,
            String maLo,
            int soLuong,
            long tienHoan) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_doi_tra(ma_phieu, ma_hoa_don, ma_san_pham, ma_lo, so_luong,"
                        + " tien_hoan) VALUES(?,?,?,?,?,?)",
                maPhieu,
                maHoaDon,
                maSanPham,
                maLo,
                soLuong,
                tienHoan);
    }

    @Override
    public List<HoaDon> timHoaDon(String tuKhoa) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT h.ma_hoa_don AS ma_khoa, h.ma_hoa_don,COALESCE(k.ho_ten, N'Khách"
                            + " lẻ'),h.tao_luc, h.tong_tien FROM hoa_don h LEFT JOIN khach_hang k"
                            + " ON k.ma_khach_hang=h.ma_khach_hang WHERE"
                            + " h.trang_thai='DA_THANH_TOAN' AND (h.ma_hoa_don=? OR"
                            + " k.so_dien_thoai=?) ORDER BY h.tao_luc DESC, h.ma_hoa_don DESC",
                        tuKhoa,
                        tuKhoa);
        List<HoaDon> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            KhachHang kh = new KhachHang();
            kh.setHoTen(r[2] != null ? r[2].toString() : "Khách lẻ");
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(r[1] != null ? r[1].toString() : null);
            hd.setKhachHang(kh);
            hd.setTaoLuc(DuLieu.chuyenLocalDateTime(r[3]));
            long tongTien = r[4] != null ? ((Number) r[4]).longValue() : 0L;
            hd.napSnapshot(0L, tongTien, 0L, 0);
            ketQua.add(hd);
        }
        return ketQua;
    }

    @Override
    public List<ChiTietDoiTra> layDongHoaDon(String maHoaDon) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT i.ma_san_pham, p.ten, p.nhom_hang, i.so_luong,COALESCE(d.da_doi,0),"
                            + " i.don_gia FROM chi_tiet_hoa_don i JOIN san_pham p ON"
                            + " p.ma_san_pham=i.ma_san_pham OUTER APPLY ( SELECT SUM(c.so_luong)"
                            + " da_doi FROM chi_tiet_doi_tra c JOIN phieu_doi_tra f ON"
                            + " f.ma_phieu=c.ma_phieu WHERE c.ma_hoa_don=i.ma_hoa_don AND"
                            + " c.ma_san_pham=i.ma_san_pham AND f.trang_thai IN"
                            + " ('CHO_XU_LY','HOAN_TAT') ) d WHERE i.ma_hoa_don=? ORDER BY p.ten",
                        maHoaDon);
        List<ChiTietDoiTra> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            SanPham sp = new SanPham();
            sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
            sp.setTenSanPham(r[1] != null ? r[1].toString() : null);
            sp.setNhomHang(r[2] != null ? r[2].toString() : null);
            ChiTietHoaDon ctHd = new ChiTietHoaDon();
            ctHd.setSanPham(sp);
            ctHd.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : 0);
            ctHd.setDonGia(r[5] != null ? ((Number) r[5]).longValue() : 0L);
            ChiTietDoiTra ctDt = new ChiTietDoiTra();
            ctDt.setChiTietHoaDon(ctHd);
            ctDt.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : 0);
            ketQua.add(ctDt);
        }
        return ketQua;
    }

    @Override
    public List<SanPham> timPhuKien(String tuKhoa) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT p.ma_san_pham, p.ten, p.gia_ban,COALESCE(SUM(l.so_luong_ton),0)"
                            + " FROM san_pham p LEFT JOIN lo_hang l ON (l.ma_san_pham=p.ma_san_pham"
                            + " AND l.trang_thai='SAN_SANG' AND (l.han_su_dung IS NULL OR"
                            + " l.han_su_dung>=CAST(GETDATE() AS date))) WHERE"
                            + " p.trang_thai='DANG_BAN' AND p.nhom_hang=N'Phụ kiện' AND"
                            + " (p.ma_san_pham=? OR p.ten LIKE ?) GROUP BY p.ma_san_pham, p.ten,"
                            + " p.gia_ban ORDER BY p.ten",
                        tuKhoa,
                        "%" + tuKhoa + "%");
        List<SanPham> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            SanPham sp = new SanPham();
            sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
            sp.setTenSanPham(r[1] != null ? r[1].toString() : null);
            sp.setGiaBan(r[2] != null ? ((Number) r[2]).longValue() : 0L);
            sp.setNhomHang("Phụ kiện");
            LoHang tonLo = new LoHang();
            tonLo.setTrangThaiEnum(TrangThaiLoHang.SAN_SANG);
            tonLo.setHanSuDung(null);
            tonLo.setSoLuongTon(r[3] != null ? ((Number) r[3]).intValue() : 0);
            sp.setDanhSachLoHang(List.of(tonLo));
            ketQua.add(sp);
        }
        return ketQua;
    }

    @Override
    public Optional<ChiTietHoaDon> timDongGoc(String maHoaDon, String maSanPham) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT"
                            + " h.ma_hoa_don,h.tong_tien,h.tam_tinh,h.thanh_toan_luc,i.don_gia,i.so_luong,h.trang_thai,p.nhom_hang,COALESCE(k.ma_khach_hang,''),COALESCE(k.ho_ten,''),COALESCE(k.diem_tich_luy,0)"
                            + " FROM chi_tiet_hoa_don i WITH(UPDLOCK,HOLDLOCK) JOIN hoa_don h"
                            + " WITH(UPDLOCK,HOLDLOCK) ON h.ma_hoa_don=i.ma_hoa_don JOIN san_pham p"
                            + " ON p.ma_san_pham=i.ma_san_pham LEFT JOIN khach_hang k ON"
                            + " k.ma_khach_hang=h.ma_khach_hang WHERE h.ma_hoa_don=? AND"
                            + " i.ma_san_pham=?",
                        maHoaDon,
                        maSanPham);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(r[8] != null ? r[8].toString() : "");
        kh.setHoTen(r[9] != null ? r[9].toString() : "");
        kh.setDiemTichLuy(r[10] != null ? ((Number) r[10]).intValue() : 0);
        long tongTien = r[1] != null ? ((Number) r[1]).longValue() : 0L;
        long tamTinh = r[2] != null ? ((Number) r[2]).longValue() : 0L;
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(r[0] != null ? r[0].toString() : null);
        hd.napSnapshot(tamTinh, tongTien, 0L, 0);
        hd.setGiamGia(Math.max(0L, tamTinh - tongTien));
        hd.setThanhToanLuc(DuLieu.chuyenLocalDateTime(r[3]));
        hd.setTrangThai(r[6] != null ? r[6].toString() : null);
        hd.setKhachHang(kh);
        SanPham sp = new SanPham();
        sp.setNhomHang(r[7] != null ? r[7].toString() : null);
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setHoaDon(hd);
        ct.setSanPham(sp);
        ct.setDonGia(r[4] != null ? ((Number) r[4]).longValue() : 0L);
        ct.setSoLuong(r[5] != null ? ((Number) r[5]).intValue() : 0);
        return Optional.of(ct);
    }

    @Override
    public Optional<SanPham> timThayThe(String ma) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_san_pham, ten, gia_ban, nhom_hang FROM san_pham"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE (ma_san_pham=? OR barcode=?) AND"
                                + " trang_thai='DANG_BAN'",
                        ma,
                        ma);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        SanPham sp = new SanPham();
        sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
        sp.setTenSanPham(r[1] != null ? r[1].toString() : null);
        sp.setGiaBan(r[2] != null ? ((Number) r[2]).longValue() : 0L);
        sp.setNhomHang(r[3] != null ? r[3].toString() : null);
        return Optional.of(sp);
    }

    @Override
    public String taoPhieu(
            String maHoaDon,
            String maNhanVien,
            long chenhLech,
            String lyDo,
            long giaTri,
            String khoa) {
        String maPhieu = DuLieu.taoMa("DT");
        if (maNhanVien == null || maNhanVien.isBlank())
            throw new IllegalArgumentException("Thiếu thông tin nhân viên lập phiếu đổi trả.");
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " phieu_doi_tra(ma_phieu,ma_hoa_don,ma_nhan_vien,chenh_lech,trang_thai,ly_do,loai_phieu,gia_tri_duoc_doi,khoa_thao_tac)"
                    + " VALUES(?,?,?,?, 'CHO_XU_LY',?,'DOI_HANG',?,?)",
                maPhieu,
                maHoaDon,
                maNhanVien,
                chenhLech,
                lyDo,
                giaTri,
                khoa);
        return maPhieu;
    }

    @Override
    public void hoanTat(String maPhieu, String maHoaDonThayThe) {
        if (ThaoTacSQL.capNhat(
                        "UPDATE phieu_doi_tra SET trang_thai=?, ma_hoa_don_thay_the=? WHERE"
                                + " ma_phieu=? AND trang_thai=? AND loai_phieu='DOI_HANG'",
                        TrangThaiPhieuDoiTra.HOAN_TAT.maDB(),
                        maHoaDonThayThe,
                        maPhieu,
                        TrangThaiPhieuDoiTra.CHO_XU_LY.maDB())
                != 1) throw new IllegalStateException("Phiếu đổi hàng đã xử lý.");
    }

    @Override
    public long layChenhLechTruoc(String maHoaDon) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(chenh_lech),0) FROM phieu_doi_tra WHERE ma_hoa_don=? AND"
                        + " loai_phieu='DOI_HANG' AND trang_thai=?",
                maHoaDon,
                TrangThaiPhieuDoiTra.HOAN_TAT.maDB());
    }

    @Override
    public boolean laHoaDonThayThe(String maHoaDon) {
        return ThaoTacSQL.laySo(
                        "SELECT COUNT(*) FROM phieu_doi_tra WHERE ma_hoa_don_thay_the=? AND"
                                + " trang_thai=?",
                        maHoaDon,
                        TrangThaiPhieuDoiTra.HOAN_TAT.maDB())
                > 0;
    }
}
