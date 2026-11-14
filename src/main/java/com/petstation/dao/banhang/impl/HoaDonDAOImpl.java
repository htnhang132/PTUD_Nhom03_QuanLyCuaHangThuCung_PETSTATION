package com.petstation.dao.banhang.impl;

import com.petstation.dao.banhang.HoaDonDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.DonDoiTreo;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.banhang.PhieuDoiTra;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.TrangThaiLoHang;
import com.petstation.model.chung.TrangThaiSanPham;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.PhieuTiepNhanSpa;
import com.petstation.util.DuLieu;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class HoaDonDAOImpl implements HoaDonDAO {
    public HoaDonDAOImpl() {}

    @Override
    public Optional<PhieuDoiTra> layNguonBienLai(String soHoaDon) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT CASE WHEN x.ma_phieu IS NOT NULL THEN 'DOI_HANG' WHEN"
                            + " ht.ma_hop_dong IS NOT NULL THEN 'LUU_TRU' WHEN spa.ma_phieu IS NOT"
                            + " NULL THEN 'SPA' ELSE '' END,"
                            + " COALESCE(x.ma_phieu,ht.ma_hop_dong,spa.ma_phieu,''),"
                            + " COALESCE(x.gia_tri_duoc_doi,CASE WHEN ht.da_nhan_coc=1 THEN"
                            + " ht.tien_coc ELSE 0 END,0), COALESCE(goc.ma_hoa_don,'') FROM hoa_don"
                            + " h LEFT JOIN phieu_doi_tra x ON x.ma_hoa_don_thay_the=h.ma_hoa_don"
                            + " AND x.loai_phieu='DOI_HANG' AND x.trang_thai='HOAN_TAT' LEFT JOIN"
                            + " hoa_don goc ON goc.ma_hoa_don=x.ma_hoa_don LEFT JOIN"
                            + " hop_dong_luu_tru ht ON ht.ma_hoa_don=h.ma_hoa_don LEFT JOIN"
                            + " phieu_tiep_nhan_spa spa ON spa.ma_hoa_don=h.ma_hoa_don WHERE"
                            + " h.ma_hoa_don=?",
                        soHoaDon);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        PhieuDoiTra phieu = new PhieuDoiTra();
        phieu.setLoaiPhieu(r[0] != null ? r[0].toString() : "");
        phieu.setMaPhieu(r[1] != null ? r[1].toString() : "");
        phieu.setGiaTriDuocDoi(r[2] != null ? ((Number) r[2]).longValue() : 0L);
        String maHoaDonGoc = r[3] != null ? r[3].toString() : "";
        if (!maHoaDonGoc.isBlank()) {
            HoaDon hdGoc = new HoaDon();
            hdGoc.setMaHoaDon(maHoaDonGoc);
            phieu.setHoaDonGoc(hdGoc);
        }
        return Optional.of(phieu);
    }

    @Override
    public Optional<String> timHoaDonTheoKhoaThu(String khoaThanhToan) {
        Object val =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT ma_hoa_don FROM hoa_don WHERE idempotency_key=?", khoaThanhToan);
        return Optional.ofNullable(val).map(Object::toString);
    }

    @Override
    public int capNhatThanhToan(int diemDung, long diemCong, String maKhachHang) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-?+? WHERE ma_khach_hang=?",
                diemDung,
                diemCong,
                maKhachHang);
    }

    @Override
    public int capNhatCapNhatHang(String hangThanhVien, String maKhachHang) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET hang_thanh_vien=? WHERE ma_khach_hang=?",
                hangThanhVien,
                maKhachHang);
    }

    @Override
    public long demMoLai(String maHoaDon) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM chi_tiet_hoa_don WHERE ma_hoa_don=?", maHoaDon);
    }

    @Override
    public int capNhatXoaTreo(String maHoaDon) {
        return ThaoTacSQL.capNhat("DELETE FROM chi_tiet_hoa_don WHERE ma_hoa_don=?", maHoaDon);
    }

    @Override
    public int xoaGioHangTreo(String maHoaDon, String maNhanVien) {
        String nv = maNhanVien == null ? "" : maNhanVien.trim();
        return ThaoTacSQL.capNhat(
                "DELETE FROM hoa_don WHERE ma_hoa_don=? AND trang_thai='GIO_TREO' AND (? = '' OR"
                        + " ma_nhan_vien=?)",
                maHoaDon,
                nv,
                nv);
    }

    @Override
    public Optional<String> layQuyetToan(String maHopDong) {
        Object val =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT ma_khach_hang FROM hop_dong_luu_tru WHERE ma_hop_dong=?",
                        maHopDong);
        return Optional.ofNullable(val).map(Object::toString);
    }

    @Override
    public Optional<KhachHang> timQuyetToan(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.isBlank()) return Optional.empty();
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_khach_hang,ho_ten,diem_tich_luy FROM khach_hang WHERE"
                                + " ma_khach_hang=?",
                        maKhachHang);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(r[0] != null ? r[0].toString() : "");
        kh.setHoTen(r[1] != null ? r[1].toString() : "");
        kh.setDiemTichLuy(r[2] != null ? ((Number) r[2]).intValue() : 0);
        return Optional.of(kh);
    }

    @Override
    public long demHuyHoaDon(String maHoaDon) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE ma_hoa_don=?", maHoaDon);
    }

    @Override
    public long demHopDongGanHoaDon(String maHoaDon) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WHERE ma_hoa_don=?", maHoaDon);
    }

    @Override
    public int capNhatHuyHoaDon(int soLuong, String maLo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE ma_lo=?", soLuong, maLo);
    }

    @Override
    public int hoanDiemKhachHang(long diemMoi, String maKhachHang) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET diem_tich_luy=? WHERE ma_khach_hang=?",
                diemMoi,
                maKhachHang);
    }

    @Override
    public int danhDauHuyHoaDon(String maHoaDon) {
        return ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET trang_thai='DA_HUY' WHERE ma_hoa_don=?", maHoaDon);
    }

    @Override
    public int danhDauHuyDonHang(String maHoaDon) {
        return 1;
    }

    @Override
    public Optional<String> layMaPhienDangMo(String tenHoacMaNhanVien) {
        if (tenHoacMaNhanVien == null || tenHoacMaNhanVien.isBlank()) return Optional.empty();
        Object giaTri =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT TOP 1 ma_phien FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " (ma_nhan_vien=? OR ma_nhan_vien IN (SELECT ma_nhan_vien"
                                + " FROM tai_khoan WHERE ten_dang_nhap=?)) AND"
                                + " trang_thai='DANG_MO' ORDER BY mo_luc DESC",
                        tenHoacMaNhanVien,
                        tenHoacMaNhanVien);
        return Optional.ofNullable(giaTri).map(Object::toString);
    }

    @Override
    public long demNgayDaChot() {
        return 0L;
    }

    @Override
    public Optional<KhachHang> timThanhToan(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.isBlank()) return Optional.empty();
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT diem_tich_luy, CAST(COALESCE(dang_hoat_dong, 1) AS BIT) FROM"
                                + " khach_hang WITH(UPDLOCK,HOLDLOCK) WHERE ma_khach_hang=?",
                        maKhachHang);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        KhachHang kh = new KhachHang();
        kh.setDiemTichLuy(r[0] != null ? ((Number) r[0]).intValue() : 0);
        boolean hoatDong =
                Boolean.TRUE.equals(r[1]) || (r[1] instanceof Number n && n.intValue() != 0);
        kh.setDangHoatDong(hoatDong);
        return Optional.of(kh);
    }

    @Override
    public int ghiNhanSuDungVoucher(
            String maVoucher, long tamTinh, String maKhachHang, String kieuNguon) {
        String maKh = maKhachHang == null || maKhachHang.isBlank() ? null : maKhachHang;
        return ThaoTacSQL.capNhat(
                "UPDATE voucher WITH(UPDLOCK,HOLDLOCK) SET"
                    + " da_dung=da_dung+1,su_dung_luc=SYSDATETIME() WHERE ma_voucher=? AND"
                    + " trang_thai='CON_HAN' AND da_dung<so_luong AND hieu_luc_den>SYSDATETIME()"
                    + " AND don_toi_thieu<=? AND (ma_khach_hang IS NULL OR ma_khach_hang=?) AND"
                    + " (chi_ap_dung_spa=0 OR ?='SPA')",
                maVoucher,
                tamTinh,
                maKh,
                kieuNguon);
    }

    @Override
    public Optional<SanPham> khoaSanPhamDeBan(String maSanPham) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_san_pham,gia_ban,trang_thai FROM san_pham"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?",
                        maSanPham);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        SanPham sp = new SanPham();
        sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
        sp.setGiaBan(r[1] != null ? ((Number) r[1]).longValue() : 0L);
        sp.setTrangThai(
                TrangThaiSanPham.DANG_BAN.maDB().equals(r[2] != null ? r[2].toString() : null)
                        ? TrangThaiSanPham.DANG_BAN.maDB()
                        : TrangThaiSanPham.NGUNG_BAN.maDB());
        return Optional.of(sp);
    }

    @Override
    public long themDongDonHang(
            String maHoaDon,
            String maSanPham,
            String maDichVu,
            String tenHang,
            int soLuong,
            long donGia,
            double thueGtgt) {
        String maSp = maSanPham != null && !maSanPham.isBlank() ? maSanPham : null;
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,ma_dich_vu,ten_hang,so_luong,don_gia,thue_gtgt)"
                    + " VALUES(?,?,?,?,?,?,?)",
                maHoaDon,
                maSp,
                maDichVu,
                tenHang != null ? tenHang : "",
                soLuong,
                donGia,
                thueGtgt);
        return 1L;
    }

    @Override
    public List<LoHang> layLoTheoFEFO(String maSanPham) {
        return ThaoTacSQL.truyVanDanhSach(
                "SELECT ma_lo,so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_san_pham=? AND trang_thai='SAN_SANG' AND so_luong_ton>0 AND"
                        + " (han_su_dung IS NULL OR han_su_dung>=CAST(GETDATE() AS DATE)) ORDER BY"
                        + " CASE WHEN han_su_dung IS NULL THEN 1 ELSE 0 END,han_su_dung,ma_lo",
                r -> taoLoTon(r, false),
                maSanPham);
    }

    @Override
    public int truTonLo(int soLuong, String maLo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-? WHERE ma_lo=? AND so_luong_ton>=?",
                soLuong,
                maLo,
                soLuong);
    }

    @Override
    public int ghiPhanBoLo(String maHoaDon, String maSanPham, String maLo, int soLuong) {
        int ketQua =
                ThaoTacSQL.capNhat(
                        "UPDATE TOP (1) chi_tiet_hoa_don SET ma_lo=? WHERE ma_hoa_don=? AND"
                                + " ma_san_pham=? AND (ma_lo IS NULL OR ma_lo = '')",
                        maLo,
                        maHoaDon,
                        maSanPham);
        if (ketQua > 0) return ketQua;
        List<Object[]> daCo =
                ThaoTacSQL.truyVan(
                        "SELECT ma_lo FROM chi_tiet_hoa_don WHERE ma_hoa_don=? AND ma_san_pham=?",
                        maHoaDon,
                        maSanPham);
        return (!daCo.isEmpty() && daCo.get(0)[0] != null) ? 1 : 0;
    }

    @Override
    public String themHoaDon(
            String maHoaDon,
            String maPhien,
            String maNhanVien,
            long tongTien,
            PhuongThuc phuongThuc,
            long daNhan,
            long tienThua,
            long diemCong,
            String khoaThanhToan,
            LocalDateTime thanhToanLuc,
            long tamTinh,
            long giamGia,
            int diemDung,
            String maVoucher,
            String maKhachHang) {
        String voucherHopLe = (maVoucher != null && maVoucher.trim().isEmpty()) ? null : maVoucher;
        String phien = (maPhien != null && !maPhien.isBlank()) ? maPhien : null;
        String nhanVien = (maNhanVien != null && !maNhanVien.isBlank()) ? maNhanVien : null;
        String khachHang = (maKhachHang != null && !maKhachHang.isBlank()) ? maKhachHang : null;
        ThaoTacSQL.capNhat(
                "INSERT INTO hoa_don(ma_hoa_don,ma_phien,ma_nhan_vien,ma_khach_hang,tong_tien,"
                    + "hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,idempotency_key,"
                    + "tao_luc,thanh_toan_luc,tam_tinh,giam_gia,diem_dung,ma_voucher,trang_thai)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'DA_THANH_TOAN')",
                maHoaDon,
                phien,
                nhanVien,
                khachHang,
                tongTien,
                phuongThuc.name(),
                daNhan,
                tienThua,
                diemCong,
                khoaThanhToan,
                thanhToanLuc,
                thanhToanLuc,
                tamTinh,
                giamGia,
                diemDung,
                voucherHopLe);
        return maHoaDon;
    }

    @Override
    public long layTienSpaChoThanhToan(String maPhieuSpa) {
        return ThaoTacSQL.laySo(
                "SELECT tong_tien FROM phieu_tiep_nhan_spa WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_phieu=? AND trang_thai='HOAN_TAT' AND ma_hoa_don IS NULL",
                maPhieuSpa);
    }

    @Override
    public int ganHoaDonSpa(String maHoaDon, String maPhieuSpa) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET ma_hoa_don=?,trang_thai='DA_THANH_TOAN' WHERE"
                        + " ma_phieu=? AND ma_hoa_don IS NULL",
                maHoaDon,
                maPhieuSpa);
    }

    @Override
    public int ganHoaDonHotel(String maHoaDon, String maHopDong) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET"
                        + " ma_hoa_don=?,trang_thai='DA_TRA_PHONG',ngay_ra_thuc_te=SYSDATETIME()"
                        + " WHERE ma_hop_dong=? AND trang_thai='DANG_O' AND ma_hoa_don IS NULL",
                maHoaDon,
                maHopDong);
    }

    @Override
    public int chuyenPhongChoDon(String maHopDong) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='DANG_DON' WHERE ma_phong=(SELECT ma_phong FROM"
                        + " hop_dong_luu_tru WHERE ma_hop_dong=?) AND NOT EXISTS (SELECT 1 FROM"
                        + " hop_dong_luu_tru h2 WHERE h2.ma_phong=phong_hotel.ma_phong AND"
                        + " h2.trang_thai='DANG_O')",
                maHopDong);
    }

    @Override
    public long demCapNhatHang(String maKhachHang) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h WHERE h.ma_khach_hang=? AND"
                        + " h.trang_thai='DA_THANH_TOAN'",
                maKhachHang);
    }

    @Override
    public Optional<HoaDon> timBienLai(String maHoaDon) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT"
                            + " h.thanh_toan_luc,h.ma_hoa_don,h.tam_tinh,h.giam_gia,h.tong_tien,h.hinh_thuc_thanh_toan,h.tien_khach_dua,h.tien_thoi,COALESCE(k.ho_ten,N'Khách"
                            + " lẻ'),COALESCE(h.ma_voucher,''),COALESCE(n.ho_ten,N'Thu ngân') FROM"
                            + " hoa_don h LEFT JOIN phien_lam_viec p ON p.ma_phien=h.ma_phien LEFT"
                            + " JOIN nhan_vien n ON n.ma_nhan_vien=h.ma_nhan_vien LEFT JOIN"
                            + " khach_hang k ON k.ma_khach_hang=h.ma_khach_hang WHERE"
                            + " h.ma_hoa_don=?",
                        maHoaDon);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        KhachHang kh = new KhachHang();
        kh.setHoTen(r[8] != null ? r[8].toString() : "Khách lẻ");
        NhanVien nv = new NhanVien();
        nv.setHoTen(r[10] != null ? r[10].toString() : "Thu ngân");
        long tamTinh = r[2] != null ? ((Number) r[2]).longValue() : 0L;
        long giamGia = r[3] != null ? ((Number) r[3]).longValue() : 0L;
        long tongTien = r[4] != null ? ((Number) r[4]).longValue() : 0L;
        long tienThoi = r[7] != null ? ((Number) r[7]).longValue() : 0L;
        HoaDon hd = new HoaDon();
        hd.setThanhToanLuc(DuLieu.chuyenLocalDateTime(r[0]));
        hd.setMaHoaDon(r[1] != null ? r[1].toString() : maHoaDon);
        hd.napSnapshot(tamTinh, tongTien, tienThoi, 0);
        hd.setGiamGia(giamGia);
        hd.setHinhThucThanhToan(r[5] != null ? r[5].toString() : null);
        hd.setTienKhachDua(r[6] != null ? ((Number) r[6]).longValue() : 0L);
        hd.setKhachHang(kh);
        hd.setMaVoucher(r[9] != null ? r[9].toString() : null);
        hd.setNhanVien(nv);
        return Optional.of(hd);
    }

    @Override
    public List<ChiTietHoaDon> layDongBienLai(String maHoaDon) {
        return ThaoTacSQL.truyVanDanhSach(
                "SELECT"
                    + " p.ma_san_pham,c.ma_dich_vu,COALESCE(c.ten_hang,p.ten),c.don_gia,SUM(c.so_luong)"
                    + " FROM chi_tiet_hoa_don c LEFT JOIN san_pham p ON p.ma_san_pham=c.ma_san_pham"
                    + " WHERE c.ma_hoa_don=? GROUP BY"
                    + " p.ma_san_pham,c.ma_dich_vu,COALESCE(c.ten_hang,p.ten),c.don_gia",
                r -> {
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    if (r[0] != null && !r[0].toString().isBlank()) {
                        SanPham sp = new SanPham();
                        sp.setMaSanPham(r[0].toString());
                        ct.setSanPham(sp);
                    } else if (r[1] != null && !r[1].toString().isBlank()) {
                        String maDv = r[1].toString();
                        if ("HOTEL-ROOM".equalsIgnoreCase(maDv)
                                || maDv.toUpperCase().startsWith("HT")) {
                            HopDongLuuTru hdLt = new HopDongLuuTru();
                            hdLt.setMaHopDong(maDv);
                            ct.setHopDongLuuTru(hdLt);
                        } else {
                            PhieuTiepNhanSpa phieuSpa = new PhieuTiepNhanSpa();
                            phieuSpa.setMaPhieu(maDv);
                            ct.setPhieuSpa(phieuSpa);
                        }
                    }
                    ct.setTenHang(r[2] != null ? r[2].toString() : null);
                    ct.setDonGia(r[3] != null ? ((Number) r[3]).longValue() : 0L);
                    ct.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : 0);
                    return ct;
                },
                maHoaDon);
    }

    @Override
    public String themTreo(
            String maHoaDonTreo, String maPhien, String maNhanVien, String maKhachHang) {
        String phien = (maPhien != null && !maPhien.isBlank()) ? maPhien : null;
        String nhanVien = (maNhanVien != null && !maNhanVien.isBlank()) ? maNhanVien : null;
        String khachHang = (maKhachHang != null && !maKhachHang.isBlank()) ? maKhachHang : null;
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " hoa_don(ma_hoa_don,ma_phien,ma_nhan_vien,ma_khach_hang,tam_tinh,giam_gia,"
                    + "tong_tien,hinh_thuc_thanh_toan,trang_thai,tao_luc,thanh_toan_luc)"
                    + " VALUES(?,?,?,?,0,0,0,'TIEN_MAT','GIO_TREO',SYSDATETIME(),SYSDATETIME())",
                maHoaDonTreo,
                phien,
                nhanVien,
                khachHang);
        return maHoaDonTreo;
    }

    @Override
    public int capNhatTreo(
            String maHoaDon, String maSanPham, String tenHang, long donGia, int soLuong) {
        int r =
                ThaoTacSQL.capNhat(
                        "INSERT INTO"
                            + " chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,ma_dich_vu,ten_hang,don_gia,so_luong)"
                            + " VALUES(?,?,?,?,?,?)",
                        maHoaDon,
                        maSanPham,
                        maSanPham,
                        tenHang != null ? tenHang : "",
                        donGia,
                        soLuong);
        ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET tam_tinh=COALESCE((SELECT SUM(so_luong*don_gia) FROM"
                        + " chi_tiet_hoa_don WHERE ma_hoa_don=hoa_don.ma_hoa_don),0),"
                        + " tong_tien=COALESCE((SELECT SUM(so_luong*don_gia) FROM chi_tiet_hoa_don"
                        + " WHERE ma_hoa_don=hoa_don.ma_hoa_don),0) WHERE ma_hoa_don=?",
                maHoaDon);
        return r;
    }

    @Override
    public long demGioTreoCuaTaiKhoan(String maHoaDon, String maNhanVien) {
        String nv = maNhanVien == null ? "" : maNhanVien.trim();
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WITH(UPDLOCK,HOLDLOCK) WHERE ma_hoa_don=? AND"
                        + " trang_thai='GIO_TREO' AND (? = '' OR ma_nhan_vien=?)",
                maHoaDon,
                nv,
                nv);
    }

    @Override
    public List<ChiTietHoaDon> timMoLai(String maHoaDon) {
        return ThaoTacSQL.truyVanDanhSach(
                "SELECT p.ma_san_pham,p.ten,p.gia_ban,c.so_luong FROM chi_tiet_hoa_don c"
                        + " JOIN san_pham p ON p.ma_san_pham=c.ma_san_pham WHERE"
                        + " c.ma_hoa_don=? AND p.trang_thai='DANG_BAN'",
                r -> {
                    SanPham sp = new SanPham();
                    sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
                    sp.setTenSanPham(r[1] != null ? r[1].toString() : null);
                    sp.setGiaBan(r[2] != null ? ((Number) r[2]).longValue() : 0L);
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setSanPham(sp);
                    ct.setDonGia(sp.getGia());
                    ct.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : 0);
                    return ct;
                },
                maHoaDon);
    }

    @Override
    public Optional<KhachHang> layKhachCuaGioTreo(String maHoaDon) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT k.ma_khach_hang,k.ho_ten,k.diem_tich_luy FROM hoa_don g JOIN"
                                + " khach_hang k ON k.ma_khach_hang=g.ma_khach_hang WHERE"
                                + " g.ma_hoa_don=? AND g.trang_thai='GIO_TREO'",
                        maHoaDon);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(r[0] != null ? r[0].toString() : "");
        kh.setHoTen(r[1] != null ? r[1].toString() : "");
        kh.setDiemTichLuy(r[2] != null ? ((Number) r[2]).intValue() : 0);
        return Optional.of(kh);
    }

    @Override
    public long demXoaTreo(String maHoaDon, String maNhanVien) {
        String nv = maNhanVien == null ? "" : maNhanVien.trim();
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WITH(UPDLOCK,HOLDLOCK) WHERE ma_hoa_don=? AND"
                        + " trang_thai='GIO_TREO' AND (? = '' OR ma_nhan_vien=?)",
                maHoaDon,
                nv,
                nv);
    }

    @Override
    public long demQuyetToan(String maPhieuSpa) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE ma_phieu=? AND"
                        + " trang_thai='HOAN_TAT' AND ma_hoa_don IS NULL",
                maPhieuSpa);
    }

    @Override
    public List<ChiTietHoaDon> layDichVuSpaQuyetToan(String maPhieuSpa) {
        return ThaoTacSQL.truyVanDanhSach(
                "SELECT COALESCE(g.ma_goi,'DV-SPA'),"
                        + " COALESCE(t.mo_ta_dich_vu,g.ten_goi,N'Dịch vụ Spa'),"
                        + " COALESCE(NULLIF(t.don_gia,0),t.tong_tien),"
                        + " COALESCE(NULLIF(t.so_luong,0),1) FROM phieu_tiep_nhan_spa t LEFT"
                        + " JOIN goi_dich_vu_spa g ON g.ma_goi=t.ma_goi WHERE t.ma_phieu=?",
                r -> taoChiTietSpa(maPhieuSpa, r),
                maPhieuSpa);
    }

    @Override
    public Optional<String> layKhachCuaPhieuSpa(String maPhieuSpa) {
        Object val =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT p.ma_khach_hang FROM phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                                + " p.ma_thu_cung=t.ma_thu_cung WHERE t.ma_phieu=?",
                        maPhieuSpa);
        return Optional.ofNullable(val).map(Object::toString);
    }

    @Override
    public Optional<HoaDon> timHuyHoaDon(String maHoaDon) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT h.ma_hoa_don,h.diem_cong,h.diem_dung,h.ma_voucher,h.ma_khach_hang"
                                + " FROM hoa_don h WITH(UPDLOCK,HOLDLOCK) WHERE h.ma_hoa_don=?"
                                + " AND h.trang_thai='DA_THANH_TOAN'",
                        maHoaDon);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(r[0] != null ? r[0].toString() : null);
        hd.napSnapshot(0L, 0L, 0L, r[1] != null ? ((Number) r[1]).intValue() : 0);
        hd.setDiemDung(r[2] != null ? ((Number) r[2]).intValue() : 0);
        hd.setMaVoucher(r[3] != null ? r[3].toString() : null);
        if (r[4] != null && !r[4].toString().isBlank()) {
            hd.setKhachHang(new KhachHang(r[4].toString()));
        }
        return Optional.of(hd);
    }

    @Override
    public long demPhieuDoiTraGanHoaDon(String maHoaDon) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_doi_tra WHERE ma_hoa_don=? AND trang_thai IN"
                        + " ('HOAN_TAT','CHO_XU_LY')",
                maHoaDon);
    }

    @Override
    public List<LoHang> layPhanBoCanHoanTon(String maHoaDon) {
        return ThaoTacSQL.truyVanDanhSach(
                "SELECT ma_lo,so_luong FROM chi_tiet_hoa_don WHERE ma_hoa_don=? AND ma_lo IS NOT"
                        + " NULL",
                r -> taoLoTon(r, true),
                maHoaDon);
    }

    @Override
    public long khoaDiemKhachHang(String maKhachHang) {
        return ThaoTacSQL.laySo(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_khach_hang=?",
                maKhachHang);
    }

    @Override
    public int hoanLuotVoucher(String maVoucher) {
        return ThaoTacSQL.capNhat(
                "UPDATE voucher SET da_dung=CASE WHEN da_dung>0 THEN da_dung-1 ELSE 0 END WHERE"
                        + " ma_voucher=?",
                maVoucher);
    }

    private static LoHang taoLoTon(Object[] r, boolean khongAm) {
        LoHang lo = new LoHang();
        lo.setMaLo(r[0] != null ? r[0].toString() : null);
        int sl = r[1] != null ? ((Number) r[1]).intValue() : 0;
        lo.setSoLuongTon(khongAm ? Math.max(0, sl) : sl);
        return lo;
    }

    private static ChiTietHoaDon taoChiTietSpa(String maPhieuSpa, Object[] r) {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        PhieuTiepNhanSpa phieu = new PhieuTiepNhanSpa();
        phieu.setMaPhieu(maPhieuSpa);
        phieu.setGoiDichVu(new GoiDichVuSpa(r[0] != null ? r[0].toString() : "DV-SPA"));
        ct.setPhieuSpa(phieu);
        ct.setTenHang(r[1] != null ? r[1].toString() : null);
        ct.setDonGia(r[2] != null ? ((Number) r[2]).longValue() : 0L);
        ct.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : 1);
        return ct;
    }

    @Override
    public List<SanPham> layCoTheBanCacSanPham() {
        return ThaoTacSQL.truyVanDanhSach(
                "SELECT p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,COALESCE(SUM(l.so_luong_ton),0),"
                    + " COALESCE(p.anh,''),COALESCE(p.barcode,'') FROM san_pham p LEFT JOIN lo_hang"
                    + " l ON l.ma_san_pham=p.ma_san_pham AND l.trang_thai='SAN_SANG' AND"
                    + " (l.han_su_dung IS NULL OR l.han_su_dung>=CAST(GETDATE() AS date)) WHERE"
                    + " p.trang_thai='DANG_BAN' GROUP BY"
                    + " p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,p.anh,p.barcode ORDER BY CASE"
                    + " WHEN NULLIF(p.anh,'') IS NULL THEN 1 ELSE 0 END,p.ma_san_pham",
                r -> {
                    SanPham sp = new SanPham();
                    sp.setMaSanPham(r[0] != null ? r[0].toString() : null);
                    sp.setTenSanPham(r[1] != null ? r[1].toString() : null);
                    sp.setNhomHang(r[2] != null ? r[2].toString() : null);
                    sp.setGiaBan(r[3] != null ? ((Number) r[3]).longValue() : 0L);
                    sp.setUrlAnh(r[5] != null ? r[5].toString() : "");
                    sp.setBarcode(r[6] != null ? r[6].toString() : "");
                    sp.setTrangThai(TrangThaiSanPham.DANG_BAN.maDB());
                    LoHang tonLo = new LoHang();
                    tonLo.setTrangThaiEnum(TrangThaiLoHang.SAN_SANG);
                    tonLo.setSoLuongTon(r[4] != null ? Math.max(0, ((Number) r[4]).intValue()) : 0);
                    sp.setDanhSachLoHang(List.of(tonLo));
                    return sp;
                });
    }

    @Override
    public Optional<HoaDon> layThongTinIn(String soHoaDon) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT COALESCE(k.so_dien_thoai,''),h.diem_cong,k.diem_tich_luy FROM"
                                + " hoa_don h LEFT JOIN khach_hang k ON"
                                + " k.ma_khach_hang=h.ma_khach_hang WHERE h.ma_hoa_don=?",
                        soHoaDon);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        HoaDon hd = new HoaDon();
        hd.napSnapshot(0L, 0L, 0L, r[1] != null ? ((Number) r[1]).intValue() : 0);
        if (r[2] != null) {
            KhachHang kh = new KhachHang();
            kh.setSoDienThoai(r[0] != null ? r[0].toString() : "");
            kh.setDiemTichLuy(((Number) r[2]).intValue());
            hd.setKhachHang(kh);
        }
        return Optional.of(hd);
    }

    @Override
    public DonDoiTreo layDonDoiTreo(String maHoaDon) {
        var ds =
                ThaoTacSQL.truyVan(
                        """
SELECT JSON_VALUE(j.noi_dung,'$.timHoaDon'),
       JSON_VALUE(j.noi_dung,'$.dongGoc'),
       JSON_VALUE(j.noi_dung,'$.maThayThe'), JSON_VALUE(j.noi_dung,'$.timHang'),
       CAST(JSON_VALUE(j.noi_dung,'$.soLuong') AS int),
       COALESCE(CAST(JSON_VALUE(j.noi_dung,'$.soLuongMoi') AS int),
                CAST(JSON_VALUE(j.noi_dung,'$.soLuong') AS int)),
       JSON_VALUE(j.noi_dung,'$.lyDo'),
       JSON_VALUE(j.noi_dung,'$.nguyenTem'), JSON_VALUE(j.noi_dung,'$.chuaDung')
FROM hoa_don h WITH(UPDLOCK,HOLDLOCK)
CROSS APPLY (SELECT CASE WHEN ISJSON(h.ghi_chu)=1 THEN h.ghi_chu ELSE N'{}' END noi_dung) j
WHERE h.ma_hoa_don = ? AND h.trang_thai='GIO_TREO'
  AND JSON_VALUE(j.noi_dung,'$.loai')='DOI_PHU_KIEN'
  AND JSON_VALUE(j.noi_dung,'$.v')='1'
""",
                        maHoaDon);
        if (ds.isEmpty()) return null;
        Object[] d = ds.get(0);
        if (d[1] == null || d[4] == null) return null;
        return new DonDoiTreo(
                (String) d[0],
                (String) d[1],
                (String) d[2],
                (String) d[3],
                ((Number) d[4]).intValue(),
                d[5] != null ? ((Number) d[5]).intValue() : ((Number) d[4]).intValue(),
                (String) d[6],
                "true".equals(d[7]),
                "true".equals(d[8]));
    }

    @Override
    public void luuDonDoiTreo(String maHoaDon, DonDoiTreo don) {
        String dong = don.dongGoc();
        int sep = dong.indexOf(':');
        String hdMa = sep > 0 ? dong.substring(0, sep) : dong;
        String spMa = sep > 0 ? dong.substring(sep + 1) : "";
        ThaoTacSQL.capNhat(
                """
DECLARE @nhap nvarchar(max) = (SELECT 'DOI_PHU_KIEN' loai, 1 v,
    CAST(? AS nvarchar(max)) timHoaDon, CAST(? AS nvarchar(max)) dongGoc,
    CAST(? AS nvarchar(max)) maThayThe, CAST(? AS nvarchar(max)) timHang,
    CAST(? AS int) soLuong, CAST(? AS int) soLuongMoi, CAST(? AS nvarchar(max)) lyDo,
    CAST(? AS bit) nguyenTem, CAST(? AS bit) chuaDung
    FOR JSON PATH, WITHOUT_ARRAY_WRAPPER);
IF DATALENGTH(@nhap)>2000
    THROW 50001, N'Nội dung nháp quá dài. Hãy rút gọn lý do hoặc từ khóa tìm kiếm.', 1;
UPDATE hoa_don SET ghi_chu=@nhap, ma_khach_hang=(
    SELECT TOP 1 h.ma_khach_hang FROM chi_tiet_hoa_don c
    JOIN hoa_don h ON h.ma_hoa_don=c.ma_hoa_don
    WHERE h.ma_hoa_don = ? OR (? = '' AND c.ma_san_pham = ?))
WHERE ma_hoa_don = ? AND trang_thai='GIO_TREO';
""",
                don.timHoaDon(),
                don.dongGoc(),
                don.maThayThe(),
                don.timHang(),
                don.soLuong(),
                don.soLuongMoi(),
                don.lyDo(),
                don.nguyenTem(),
                don.chuaDung(),
                hdMa,
                hdMa,
                spMa,
                maHoaDon);
    }

    @Override
    public String layMaHoaDonGoc(String maDong) {
        if (maDong != null && !maDong.isBlank()) {
            int sep = maDong.indexOf(':');
            if (sep > 0 && sep < maDong.length() - 1) {
                Object ma =
                        ThaoTacSQL.layGiaTriDon(
                                "SELECT TOP 1 h.ma_hoa_don FROM hoa_don h JOIN"
                                        + " chi_tiet_hoa_don c ON c.ma_hoa_don=h.ma_hoa_don"
                                        + " WHERE h.ma_hoa_don=? AND c.ma_san_pham=?",
                                maDong.substring(0, sep),
                                maDong.substring(sep + 1));
                if (ma != null) return ma.toString();
            }
        }
        throw new IllegalArgumentException("Không tìm thấy hóa đơn gốc.");
    }
}
