package com.petstation.dao.banhang.impl;

import com.petstation.dao.banhang.PhienLamViecDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.TrangThaiPhienLamViec;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhienLamViecDAOImpl implements PhienLamViecDAO {
    public PhienLamViecDAOImpl() {}

    @Override
    public long layCocHotel(String maPhien, String phuongThuc) {
        PhuongThuc pt = PhuongThuc.docTuCSDL(phuongThuc);
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(so_tien),0) FROM lich_su_thu_coc_hotel"
                        + " WHERE ma_phien=?"
                        + " AND UPPER(LTRIM(RTRIM(phuong_thuc))) = ?",
                maPhien,
                pt.name());
    }

    @Override
    public List<NhanVien> layNguoiNhan(String maNhanVienHienTai) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT DISTINCT n.ma_nhan_vien, n.ho_ten FROM tai_khoan t JOIN"
                                + " nhan_vien n ON n.ma_nhan_vien=t.ma_nhan_vien WHERE"
                                + " t.trang_thai='HOAT_DONG' AND n.dang_lam_viec=1 AND"
                                + " n.ma_nhan_vien<>? AND t.vai_tro IN"
                                + " ('QUAN_LY_CUA_HANG','NHAN_VIEN_BAN_HANG') ORDER BY n.ho_ten",
                        maNhanVienHienTai);
        List<NhanVien> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(r[0] != null ? r[0].toString() : null);
            nv.setHoTen(r[1] != null ? r[1].toString() : "");
            ketQua.add(nv);
        }
        return ketQua;
    }

    @Override
    public int dongCoBanGiao(
            long thucDem,
            long chenhLech,
            String lyDo,
            String hinhThuc,
            String maNhanVienNhan,
            String maPhien) {
        return ThaoTacSQL.capNhat(
                "UPDATE phien_lam_viec SET"
                    + " tien_thuc_te=?,chenh_lech=?,giai_trinh=?,hinh_thuc_ban_giao=?,nguoi_nhan_ban_giao=?,dong_luc=SYSDATETIME(),trang_thai=?"
                    + " WHERE ma_phien=? AND trang_thai=?",
                thucDem,
                chenhLech,
                lyDo,
                hinhThuc,
                maNhanVienNhan,
                TrangThaiPhienLamViec.DA_BAN_GIAO.maDB(),
                maPhien,
                TrangThaiPhienLamViec.DANG_MO.maDB());
    }

    @Override
    public long layTongCocDaNhan(String maPhien) {
        return layCocHotel(maPhien, PhuongThuc.TIEN_MAT.name());
    }

    @Override
    public long demDong(String maPhien) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE ma_phien=?",
                maPhien);
    }

    @Override
    public Optional<PhienLamViec> timHienTai(String maNhanVienHoacTenDangNhap) {
        if (maNhanVienHoacTenDangNhap == null || maNhanVienHoacTenDangNhap.isBlank())
            return Optional.empty();
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_phien,ten_ca,mo_luc,tien_dau_ca FROM phien_lam_viec WHERE"
                                + " (ma_nhan_vien=? OR ma_nhan_vien IN (SELECT ma_nhan_vien"
                                + " FROM tai_khoan WHERE ten_dang_nhap=?)) AND trang_thai=?"
                                + " ORDER BY mo_luc DESC",
                        maNhanVienHoacTenDangNhap,
                        maNhanVienHoacTenDangNhap,
                        TrangThaiPhienLamViec.DANG_MO.maDB());
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        PhienLamViec phien = new PhienLamViec();
        phien.setMaPhien(r[0] != null ? r[0].toString() : null);
        phien.setTenCa(r[1] != null ? r[1].toString() : null);
        phien.setMoLuc(DuLieu.chuyenLocalDateTime(r[2]));
        phien.setTienDauCa(r[3] != null ? ((Number) r[3]).longValue() : 0L);
        return Optional.of(phien);
    }

    @Override
    public long demBanHang(String maPhien, PhuongThuc mucTieu) {
        PhuongThuc phuongThucCanTim = mucTieu == null ? PhuongThuc.TIEN_MAT : mucTieu;
        List<Object[]> cacHoaDon =
                ThaoTacSQL.truyVan(
                        "SELECT hinh_thuc_thanh_toan, tong_tien FROM hoa_don WHERE"
                                + " ma_phien=? AND trang_thai='DA_THANH_TOAN'",
                        maPhien);
        long tong = 0;
        for (Object[] hd : cacHoaDon) {
            if (PhuongThuc.docTuCSDL(String.valueOf(hd[0])) == phuongThucCanTim) {
                tong += ((Number) hd[1]).longValue();
            }
        }
        return tong;
    }

    @Override
    public long demSoLuongHoaDon(String maPhien) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE ma_phien=? AND trang_thai='DA_THANH_TOAN'",
                maPhien);
    }

    @Override
    public long demDuKienTienMat(String maPhien) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(-p.chenh_lech),0) FROM phieu_doi_tra p JOIN hoa_don h ON"
                        + " h.ma_hoa_don=p.ma_hoa_don WHERE h.ma_phien=? AND"
                        + " p.trang_thai='HOAN_TAT' AND p.loai_phieu='TRA_HANG_CU' AND"
                        + " h.hinh_thuc_thanh_toan = 'TIEN_MAT'",
                maPhien);
    }

    @Override
    public int capNhatMo(String maNhanVien, String tenCa, long tienDauCa) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " phien_lam_viec(ma_phien,ma_nhan_vien,ma_ca,ten_ca,tien_dau_ca,mo_luc,trang_thai)"
                    + " VALUES(?,?,COALESCE((SELECT TOP 1 ma_ca FROM ca_lam_viec WHERE ten_ca=?),"
                    + " N'CA01'),?,?,SYSDATETIME(),N'DANG_MO')",
                DuLieu.taoMa("MPI"),
                maNhanVien,
                tenCa,
                tenCa,
                tienDauCa);
    }
}
