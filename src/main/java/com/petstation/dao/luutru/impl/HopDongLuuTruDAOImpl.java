package com.petstation.dao.luutru.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.layGiaTriDon;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.luutru.HopDongLuuTruDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.HotelDTO;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.LichSuThuCocHotel;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DuLieu;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class HopDongLuuTruDAOImpl implements HopDongLuuTruDAO {
    public HopDongLuuTruDAOImpl() {}

    @Override
    public List<PhongHotel> layPhongDangDon() {
        return truyVanDanhSach(
                "SELECT ma_phong FROM phong_hotel WHERE trang_thai='DANG_DON' ORDER BY ma_phong",
                r -> new PhongHotel(DuLieu.chuyenThanhChuoi(r[0])));
    }

    @Override
    public int capNhatNhanPhong(String maHopDong) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET trang_thai='DANG_O' WHERE ma_hop_dong=?", maHopDong);
    }

    @Override
    public List<ThongTinPhongDat> timDatPhong(String maPhong) {
        return truyVanDanhSach(
                "SELECT ma_phong,gia_dem,trang_thai,loai_phong,suc_chua FROM phong_hotel"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?",
                r ->
                        new ThongTinPhongDat(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.layGiaTri(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2]),
                                DuLieu.chuyenThanhChuoi(r[3]),
                                (int) DuLieu.layGiaTri(r[4])),
                maPhong);
    }

    @Override
    public long demPhongDatTrung(String maPhong, LocalDateTime ketThuc, LocalDateTime batDau) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?"
                        + " AND trang_thai IN ('DA_DAT','DANG_O') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>?",
                maPhong,
                ketThuc,
                batDau);
    }

    @Override
    public long demThuCungDatTrung(String maThuCung, LocalDateTime ketThuc, LocalDateTime batDau) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE ma_thu_cung=?"
                        + " AND trang_thai IN ('DA_DAT','DANG_O') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>?",
                maThuCung,
                ketThuc,
                batDau);
    }

    @Override
    public long demPhongDatTrungBoQua(
            String maPhong, LocalDateTime ketThuc, LocalDateTime batDau, String boQuaMa) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?"
                        + " AND trang_thai IN ('DA_DAT','DANG_O') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>? AND ma_hop_dong<>?",
                maPhong,
                ketThuc,
                batDau,
                boQuaMa);
    }

    @Override
    public long demThuCungDatTrungBoQua(
            String maThuCung, LocalDateTime ketThuc, LocalDateTime batDau, String boQuaMa) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE ma_thu_cung=?"
                        + " AND trang_thai IN ('DA_DAT','DANG_O') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>? AND ma_hop_dong<>?",
                maThuCung,
                ketThuc,
                batDau,
                boQuaMa);
    }

    @Override
    public List<HotelDTO.ThongTinDatPhong> layDatPhongSua(String maHopDong) {
        return truyVanDanhSach(
                "SELECT h.trang_thai,p.ma_thu_cung,r.ma_phong,h.ngay_vao,"
                        + " h.ngay_ra_du_kien,h.ghi_chu,h.tien_coc,h.da_nhan_coc,"
                        + " COALESCE(h.coc_yeu_cau,h.tien_coc),COALESCE(h.gia_dem,r.gia_dem)"
                        + " FROM hop_dong_luu_tru h WITH(UPDLOCK,HOLDLOCK) JOIN thu_cung p ON"
                        + " p.ma_thu_cung=h.ma_thu_cung JOIN phong_hotel r ON"
                        + " r.ma_phong=h.ma_phong WHERE h.ma_hop_dong=?",
                r -> {
                    ThuCung thu = new ThuCung(DuLieu.chuyenThanhChuoi(r[1]));
                    PhongHotel phong = new PhongHotel();
                    phong.setMaPhong(DuLieu.chuyenThanhChuoi(r[2]));
                    return new HotelDTO.ThongTinDatPhong(
                            thu,
                            phong,
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.chuyenLocalDateTime(r[3]),
                            DuLieu.chuyenLocalDateTime(r[4]),
                            r[5] == null ? null : r[5].toString(),
                            DuLieu.layGiaTri(r[6]),
                            r[7] instanceof Boolean b ? b : DuLieu.layGiaTri(r[7]) != 0,
                            DuLieu.layGiaTri(r[8]),
                            DuLieu.layGiaTri(r[9]));
                },
                maHopDong);
    }

    @Override
    public int capNhatDatPhong(
            String maKhachHang,
            String maThuCung,
            String maPhong,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            long giaDem,
            long cocYeuCau,
            String ghiChu,
            String maHopDong) {
        String gc = ghiChu == null ? "" : ghiChu;
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET ma_khach_hang=?, ma_thu_cung=?, ma_phong=?,"
                        + " ngay_vao=?,ngay_ra_du_kien=?,gia_dem=?,coc_yeu_cau=?,ghi_chu=? WHERE"
                        + " ma_hop_dong=? AND trang_thai='DA_DAT' AND ma_hoa_don IS NULL",
                maKhachHang,
                maThuCung,
                maPhong,
                ngayVao,
                ngayRaDuKien,
                giaDem,
                cocYeuCau,
                gc,
                maHopDong);
    }

    @Override
    public int huyDatPhong(String lyDo, String maHopDong) {
        String lyDoChuan = lyDo == null ? "" : lyDo;
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET trang_thai='DA_HUY',ghi_chu=COALESCE(ghi_chu + N' | ',"
                        + " N'') + ? WHERE ma_hop_dong=? AND trang_thai='DA_DAT' AND ma_hoa_don IS"
                        + " NULL AND (da_nhan_coc=0 OR da_nhan_coc IS NULL)",
                lyDoChuan,
                maHopDong);
    }

    @Override
    public String themDatPhong(
            String maHopDong,
            String maKhachHang,
            String maThuCung,
            String maPhong,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            long tienCoc,
            long giaDem,
            String ghiChu) {
        String gc = ghiChu == null ? "" : ghiChu;
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " hop_dong_luu_tru(ma_hop_dong,ma_khach_hang,ma_thu_cung,ma_phong,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu)"
                    + " VALUES(?,?,?,?,?,?,?,'DA_DAT',?,?)",
                maHopDong,
                maKhachHang,
                maThuCung,
                maPhong,
                ngayVao,
                ngayRaDuKien,
                tienCoc,
                giaDem,
                gc);
        return maHopDong;
    }

    @Override
    public String layNhanPhong(String maHopDong) {
        Object giaTri =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT ma_phong FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_hop_dong=? AND trang_thai='DA_DAT' AND (CASE WHEN"
                                + " da_nhan_coc=1 THEN tien_coc ELSE 0 END)>=COALESCE(coc_yeu_cau,"
                                + "tien_coc)",
                        maHopDong);
        return giaTri == null ? null : giaTri.toString();
    }

    @Override
    public int danhDauPhongDangO(String maPhong) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel WITH(UPDLOCK,HOLDLOCK) SET trang_thai='DANG_O' WHERE"
                        + " ma_phong=? AND trang_thai IN ('TRONG', 'DANG_O')",
                maPhong);
    }

    @Override
    public long demNhatKy(String maHopDong) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE ma_hop_dong=?"
                        + " AND trang_thai='DANG_O'",
                maHopDong);
    }

    @Override
    public int capNhatNhatKy(
            String maHopDong,
            String maNhanVien,
            String anUong,
            String daThucHien,
            String tinhTrang) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " nhat_ky_cham_soc(ma_nhat_ky,ma_hop_dong,ma_nhan_vien,an_uong,da_thuc_hien,tinh_trang,thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,SYSDATETIME())",
                DuLieu.taoMa("NK"),
                maHopDong,
                maNhanVien,
                anUong,
                daThucHien,
                tinhTrang);
    }

    @Override
    public List<ThongTinThanhToanHotel> timHotelConPhaiThu(String maHopDong) {
        return truyVanDanhSach(
                "SELECT h.ngay_vao,h.ngay_ra_du_kien,COALESCE(h.gia_dem,p.gia_dem),CASE"
                        + " WHEN h.da_nhan_coc=1 THEN h.tien_coc ELSE 0 END FROM"
                        + " hop_dong_luu_tru h WITH(UPDLOCK,HOLDLOCK) JOIN phong_hotel p ON"
                        + " p.ma_phong=h.ma_phong WHERE h.ma_hop_dong=? AND"
                        + " h.trang_thai='DANG_O' AND h.ma_hoa_don IS NULL",
                r ->
                        new ThongTinThanhToanHotel(
                                DuLieu.chuyenLocalDateTime(r[0]),
                                DuLieu.chuyenLocalDateTime(r[1]),
                                DuLieu.layGiaTri(r[2]),
                                DuLieu.layGiaTri(r[3])),
                maHopDong);
    }

    @Override
    public List<HotelDTO.DongNhatKyChiTiet> layNhatKyChiTiet(String maHopDong) {
        return truyVanDanhSach(
                "SELECT j.thoi_gian,n.ma_nhan_vien,n.ho_ten,j.an_uong,j.da_thuc_hien,j.tinh_trang"
                    + " FROM nhat_ky_cham_soc j JOIN nhan_vien n ON n.ma_nhan_vien=j.ma_nhan_vien"
                    + " WHERE j.ma_hop_dong=? ORDER BY j.thoi_gian DESC",
                r -> {
                    NhanVien nv = new NhanVien(DuLieu.chuyenThanhChuoi(r[1]));
                    nv.setHoTen(DuLieu.chuyenThanhChuoi(r[2]));
                    return new HotelDTO.DongNhatKyChiTiet(
                            nv,
                            DuLieu.chuyenLocalDateTime(r[0]),
                            DuLieu.chuyenThanhChuoi(r[3]),
                            DuLieu.chuyenThanhChuoi(r[4]),
                            DuLieu.chuyenThanhChuoi(r[5]));
                },
                maHopDong);
    }

    @Override
    public int danhDauPhongKhaDung(String maPhong) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='TRONG' WHERE ma_phong=? AND"
                        + " trang_thai='DANG_DON'",
                maPhong);
    }

    @Override
    public int datMucCoc(long muc, String maHopDong) {
        return capNhat(
                "UPDATE hop_dong_luu_tru SET coc_yeu_cau=? WHERE ma_hop_dong=?", muc, maHopDong);
    }

    @Override
    public List<HotelDTO.DongLichSuCoc> layLichSuThuCoc(String maHopDong) {
        List<Object[]> rows =
                truyVan(
                        "SELECT l.phuong_thuc,l.so_tien,l.khach_dua,l.tien_thua,l.tao_luc,"
                                + " COALESCE(p.ten_ca,l.ma_phien) FROM lich_su_thu_coc_hotel l LEFT"
                                + " JOIN phien_lam_viec p ON p.ma_phien=l.ma_phien WHERE"
                                + " l.ma_hop_dong=? ORDER BY l.tao_luc",
                        maHopDong);
        List<HotelDTO.DongLichSuCoc> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            ketQua.add(
                    new HotelDTO.DongLichSuCoc(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.layGiaTri(r[1]),
                            DuLieu.layGiaTri(r[2]),
                            DuLieu.layGiaTri(r[3]),
                            DuLieu.chuyenLocalDateTime(r[4]),
                            DuLieu.chuyenThanhChuoi(r[5])));
        }
        return ketQua;
    }

    @Override
    public List<MucCocHopDong> layMucCoc(String maHopDong) {
        return truyVanDanhSach(
                "SELECT COALESCE(coc_yeu_cau,tien_coc),CASE WHEN da_nhan_coc=1 THEN"
                        + " tien_coc ELSE 0 END FROM hop_dong_luu_tru WHERE ma_hop_dong=?",
                r -> new MucCocHopDong(DuLieu.layGiaTri(r[0]), DuLieu.layGiaTri(r[1])),
                maHopDong);
    }

    @Override
    public List<TrangThaiCocHopDong> khoaHopDongThuCoc(String maHopDong) {
        return truyVanDanhSach(
                "SELECT trang_thai,COALESCE(coc_yeu_cau,tien_coc),CASE WHEN da_nhan_coc=1"
                        + " THEN tien_coc ELSE 0 END FROM hop_dong_luu_tru"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_hop_dong=?",
                r ->
                        new TrangThaiCocHopDong(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.layGiaTri(r[1]),
                                DuLieu.layGiaTri(r[2])),
                maHopDong);
    }

    @Override
    public long demKhoaThuCoc(String maHopDong, String tienToKhoa) {
        return laySo(
                "SELECT COUNT(*) FROM lich_su_thu_coc_hotel WHERE ma_hop_dong=? AND"
                        + " khoa_thu_coc=?",
                maHopDong,
                tienToKhoa);
    }

    @Override
    public int ghiNhanTongCoc(long tongCoc, String maNhanVien, String maPhien, String maHopDong) {
        return capNhat(
                "UPDATE hop_dong_luu_tru SET tien_coc=?,da_nhan_coc=1,nhan_coc_luc=SYSDATETIME(),"
                        + " nhan_coc_boi=?, ma_phien_nhan_coc=COALESCE(ma_phien_nhan_coc, ?) WHERE"
                        + " ma_hop_dong=?",
                tongCoc,
                maNhanVien,
                maPhien,
                maHopDong);
    }

    @Override
    public List<ThongTinDoiCoc> khoaHopDongDoiCoc(String maHopDong) {
        return truyVanDanhSach(
                "SELECT ngay_vao,ngay_ra_du_kien,COALESCE(gia_dem,(SELECT p.gia_dem FROM"
                        + " phong_hotel p WHERE p.ma_phong=h.ma_phong)),CASE WHEN da_nhan_coc=1"
                        + " THEN tien_coc ELSE 0 END,trang_thai FROM hop_dong_luu_tru h"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE h.ma_hop_dong=?",
                r ->
                        new ThongTinDoiCoc(
                                DuLieu.chuyenLocalDateTime(r[0]),
                                DuLieu.chuyenLocalDateTime(r[1]),
                                DuLieu.layGiaTri(r[2]),
                                DuLieu.layGiaTri(r[3]),
                                DuLieu.chuyenThanhChuoi(r[4])),
                maHopDong);
    }

    @Override
    public int dieuChinhMucCoc(long muc, String lyDo, String maHopDong) {
        return capNhat(
                "UPDATE hop_dong_luu_tru SET coc_yeu_cau=?,ly_do_doi_coc=? WHERE ma_hop_dong=?",
                muc,
                lyDo,
                maHopDong);
    }

    @Override
    public void ghiThuCoc(
            String maHopDong,
            String maPhien,
            String maNhanVienThu,
            String khoa,
            PhuongThuc phuongThuc,
            long soTien,
            long khachDua) {
        if (maHopDong == null || phuongThuc == null || soTien <= 0) return;
        Object thoiDiemThu = layGiaTriDon("SELECT SYSDATETIME()");
        String pt = phuongThuc.name();
        long tienThua = phuongThuc == PhuongThuc.TIEN_MAT ? Math.max(0L, khachDua - soTien) : 0L;
        capNhat(
                "INSERT INTO lich_su_thu_coc_hotel"
                        + "(ma_hop_dong,khoa_thu_coc,phuong_thuc,so_tien,khach_dua,tien_thua,"
                        + "ma_phien,ma_nhan_vien_thu,tao_luc)"
                        + " VALUES(?,?,?,?,?,?,?,?,?)",
                maHopDong,
                khoa,
                pt,
                soTien,
                khachDua,
                tienThua,
                maPhien,
                maNhanVienThu,
                thoiDiemThu);
        capNhat(
                "UPDATE hop_dong_luu_tru SET phuong_thuc_coc=?, khoa_thu_coc=? WHERE"
                        + " ma_hop_dong=?",
                pt,
                khoa,
                maHopDong);
    }

    @Override
    public List<LichSuThuCocHotel> layLichSuTheoHopDong(String maHopDong) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_hop_dong, khoa_thu_coc, phuong_thuc, so_tien, khach_dua,"
                            + " tien_thua, ma_phien, ma_nhan_vien_thu, tao_luc FROM"
                            + " lich_su_thu_coc_hotel WHERE ma_hop_dong = ? ORDER BY tao_luc ASC",
                        maHopDong);
        List<LichSuThuCocHotel> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            LichSuThuCocHotel item = new LichSuThuCocHotel();
            if (r[0] != null && !r[0].toString().isBlank()) {
                HopDongLuuTru hd = new HopDongLuuTru();
                hd.setMaHopDong(r[0].toString());
                item.setHopDong(hd);
            }
            item.setKhoaThuCoc(r[1] != null ? r[1].toString() : null);
            item.setPhuongThuc(r[2] != null ? r[2].toString() : null);
            item.setSoTien(r[3] != null ? ((Number) r[3]).longValue() : 0L);
            item.setKhachDua(r[4] != null ? ((Number) r[4]).longValue() : 0L);
            item.setTienThua(r[5] != null ? ((Number) r[5]).longValue() : 0L);
            if (r[6] != null && !r[6].toString().isBlank()) {
                PhienLamViec phien = new PhienLamViec();
                phien.setMaPhien(r[6].toString());
                item.setPhienLamViec(phien);
            }
            if (r[7] != null && !r[7].toString().isBlank()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(r[7].toString());
                item.setNhanVienThu(nv);
            }
            if (r[8] instanceof Timestamp ts) {
                item.setTaoLuc(ts.toLocalDateTime());
            }
            ketQua.add(item);
        }
        return ketQua;
    }

    @Override
    public Optional<HopDongLuuTru> timTheoMa(String maHopDong) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_hop_dong, ma_khach_hang, ma_thu_cung, ma_phong,"
                                + " ngay_vao, ngay_ra_du_kien, ngay_ra_thuc_te, tien_coc,"
                                + " phuong_thuc_coc, khoa_thu_coc, trang_thai, gia_dem, ghi_chu,"
                                + " ma_hoa_don, da_nhan_coc, nhan_coc_luc, nhan_coc_boi,"
                                + " ma_phien_nhan_coc, coc_yeu_cau, ly_do_doi_coc FROM"
                                + " hop_dong_luu_tru WHERE ma_hop_dong = ?",
                        maHopDong);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        HopDongLuuTru hd = new HopDongLuuTru();
        hd.setMaHopDong(DuLieu.chuyenThanhChuoi(r[0]));
        String maKh = DuLieu.chuyenThanhChuoi(r[1]);
        if (!maKh.isBlank()) {
            hd.setKhachHang(new KhachHang(maKh));
        }
        String maTc = DuLieu.chuyenThanhChuoi(r[2]);
        if (!maTc.isBlank()) {
            hd.setThuCung(new ThuCung(maTc));
        }
        String maPhong = DuLieu.chuyenThanhChuoi(r[3]);
        if (!maPhong.isBlank()) {
            hd.setPhongHotel(new PhongHotel(maPhong));
        }
        hd.setNgayVao(DuLieu.chuyenLocalDateTime(r[4]));
        hd.setNgayRaDuKien(DuLieu.chuyenLocalDateTime(r[5]));
        hd.setNgayRaThucTe(DuLieu.chuyenLocalDateTime(r[6]));
        hd.setTienCoc(r[7] instanceof Number n ? n.longValue() : 0L);
        hd.setPhuongThucCoc(r[8] == null ? null : r[8].toString());
        hd.setKhoaThuCoc(r[9] == null ? null : r[9].toString());
        hd.setTrangThai(TrangThaiHopDong.tuMaDB(DuLieu.chuyenThanhChuoi(r[10])));
        hd.setGiaDem(r[11] instanceof Number n ? n.longValue() : 0L);
        hd.setGhiChu(DuLieu.chuyenThanhChuoi(r[12]));
        if (r[13] != null && !r[13].toString().isBlank()) {
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHoaDon(r[13].toString());
            hd.setHoaDon(hoaDon);
        }
        hd.setDaNhanCoc(
                Boolean.TRUE.equals(r[14]) || (r[14] instanceof Number n && n.intValue() != 0));
        hd.setNhanCocLuc(DuLieu.chuyenLocalDateTime(r[15]));
        if (r[16] != null && !r[16].toString().isBlank()) {
            NhanVien nvCoc = new NhanVien();
            nvCoc.setMaNhanVien(r[16].toString());
            hd.setNhanCocBoi(nvCoc);
        }
        if (r[17] != null && !r[17].toString().isBlank()) {
            PhienLamViec phienCoc = new PhienLamViec();
            phienCoc.setMaPhien(r[17].toString());
            hd.setPhienNhanCoc(phienCoc);
        }
        hd.setCocYeuCau(r[18] instanceof Number n ? n.longValue() : null);
        hd.setLyDoDoiCoc(r[19] == null ? null : r[19].toString());
        return Optional.of(hd);
    }
}
