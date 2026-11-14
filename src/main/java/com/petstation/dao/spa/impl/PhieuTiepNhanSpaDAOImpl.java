package com.petstation.dao.spa.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.spa.PhieuTiepNhanSpaDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.SpaDTO;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.LichHen;
import com.petstation.model.spa.PhieuTiepNhanSpa;
import com.petstation.util.DuLieu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PhieuTiepNhanSpaDAOImpl implements PhieuTiepNhanSpaDAO {
    public PhieuTiepNhanSpaDAOImpl() {}

    @Override
    public int capNhatTiepNhan(String maLichHen) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='DANG_THUC_HIEN' WHERE ma_lich_hen = ?", maLichHen);
    }

    @Override
    public int capNhatSucKhoeThuCung(double canNang, String tinhTrang, String maThuCung) {
        return ThaoTacSQL.capNhat(
                "UPDATE thu_cung SET can_nang=?, tinh_trang_suc_khoe=? WHERE ma_thu_cung = ?",
                canNang,
                tinhTrang,
                maThuCung);
    }

    @Override
    public long demTrangThaiSpa(String maPhieu) {
        return ThaoTacSQL.laySo(
                "SELECT CASE WHEN (ma_goi IS NOT NULL OR don_gia>0 OR tong_tien>0) THEN 1 ELSE 0"
                        + " END FROM phieu_tiep_nhan_spa WHERE ma_phieu = ?",
                maPhieu);
    }

    @Override
    public long demLichHen(String maGoi) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE ma_goi = ? AND trang_thai='HOAT_DONG'",
                maGoi);
    }

    @Override
    public long khoaNhanVienDangLam(String maNhanVien) {
        return ThaoTacSQL.laySo(
                "SELECT 1 FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE ma_nhan_vien = ? AND"
                        + " dang_lam_viec=1",
                maNhanVien);
    }

    @Override
    public long layThoiLuongDichVu(String maGoi) {
        return ThaoTacSQL.laySo(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE ma_goi = ?", maGoi);
    }

    @Override
    public long demLichTrung(
            String maNhanVien, String maThuCung, LocalDateTime ketThuc, LocalDateTime batDau) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE (ma_nhan_vien = ? OR"
                        + " ma_thu_cung = ?) AND trang_thai NOT IN ('DA_HUY','HOAN_TAT') AND"
                        + " bat_dau<? AND ket_thuc>?",
                maNhanVien,
                maThuCung,
                ketThuc,
                batDau);
    }

    @Override
    public String themLichHen(
            String maLichHen,
            String maKhachHang,
            String maThuCung,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String maNhanVien,
            String maGoi) {
        ThaoTacSQL.capNhat(
                "INSERT INTO lich_hen(ma_lich_hen, ma_khach_hang, ma_thu_cung,"
                        + " bat_dau, ket_thuc, trang_thai, ma_nhan_vien, ma_goi) VALUES(?, ?, ?,"
                        + " ?, ?, 'DA_XAC_NHAN', ?, ?)",
                maLichHen,
                maKhachHang,
                maThuCung,
                batDau,
                ketThuc,
                maNhanVien,
                maGoi);
        return maLichHen;
    }

    @Override
    public List<ThongTinTiepNhan> timTiepNhan(String maLichHen, String maThuCung) {
        return truyVanDanhSach(
                "SELECT ma_lich_hen, ma_nhan_vien FROM lich_hen WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE ma_lich_hen = ? AND ma_thu_cung = ? AND"
                        + " trang_thai='DA_XAC_NHAN'",
                r ->
                        new ThongTinTiepNhan(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                r[1] == null ? null : DuLieu.chuyenThanhChuoi(r[1])),
                maLichHen,
                maThuCung);
    }

    @Override
    public String themTiepNhan(
            String maPhieu,
            String maLichHen,
            String maThuCung,
            double canNang,
            String maNhanVien,
            String ghiChu) {
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_tiep_nhan_spa(ma_phieu, ma_lich_hen, ma_thu_cung,"
                    + " can_nang_thuc_te, ma_nhan_vien, ghi_chu, trang_thai, tao_luc) VALUES(?, ?,"
                    + " ?, ?, ?, ?, 'TIEP_NHAN', SYSDATETIME())",
                maPhieu,
                maLichHen,
                maThuCung,
                canNang,
                maNhanVien,
                ghiChu);
        return maPhieu;
    }

    @Override
    public String layTiepNhan(String maLichHen) {
        Object giaTri =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT ma_goi FROM lich_hen WHERE ma_lich_hen = ?", maLichHen);
        return giaTri == null ? null : giaTri.toString();
    }

    @Override
    public List<ThongTinThemDichVu> timThemDichVuSpa(String maPhieu) {
        return truyVanDanhSach(
                "SELECT t.can_nang_thuc_te, p.loai, t.trang_thai FROM phieu_tiep_nhan_spa t"
                        + " WITH(UPDLOCK,HOLDLOCK) JOIN thu_cung p ON p.ma_thu_cung ="
                        + " t.ma_thu_cung WHERE t.ma_phieu = ?",
                r ->
                        new ThongTinThemDichVu(
                                r[0] instanceof Number n ? n.doubleValue() : 0.0,
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2])),
                maPhieu);
    }

    @Override
    public List<GiaDichVuCanNang> layGiaDichVuTheoCanNang(
            String maGoi, String loaiThuCung, double canNang) {
        String loai = loaiThuCung == null ? "" : loaiThuCung;
        List<Object[]> goiList =
                ThaoTacSQL.truyVan(
                        "SELECT ma_goi, ten_goi, COALESCE(bang_gia, N'') FROM goi_dich_vu_spa WHERE"
                                + " ma_goi=? AND trang_thai='HOAT_DONG'",
                        maGoi);
        if (goiList.isEmpty()) return List.of();
        Object[] goi = goiList.get(0);
        String maGoiDb = DuLieu.chuyenThanhChuoi(goi[0]);
        String tenGoi = String.valueOf(goi[1]);
        String bangGia = String.valueOf(goi[2]).trim();
        if (bangGia.isEmpty()) return List.of();
        long donGiaChon = timGiaTheoCanNang(bangGia, loai, canNang);
        if (donGiaChon < 0) return List.of();
        return List.of(new GiaDichVuCanNang(maGoiDb, donGiaChon, tenGoi));
    }

    public static long timGiaTheoCanNang(String bangGia, String loaiThuCung, double canNang) {
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
            String[] khoang = namPhan ? new String[] {p[2], p[3]} : p[1].trim().split("-", -1);
            double tu = Double.parseDouble(khoang[0].trim());
            double den =
                    khoang.length < 2
                                    || khoang[1].isBlank()
                                    || "NULL".equalsIgnoreCase(khoang[1].trim())
                            ? Double.POSITIVE_INFINITY
                            : Double.parseDouble(khoang[1].trim());
            long gia = new BigDecimal(p[namPhan ? 4 : 2].trim()).longValueExact();
            String nhan = namPhan ? p[1].trim() : "";
            boolean datTu =
                    nhan.startsWith(">") && !nhan.startsWith(">=") ? canNang > tu : canNang >= tu;
            boolean datDen =
                    nhan.startsWith("<") && !nhan.startsWith("<=") ? canNang < den : canNang <= den;
            if (datTu && datDen && tu >= maxTu) {
                maxTu = tu;
                donGiaChon = gia;
            }
        }
        return donGiaChon;
    }

    @Override
    public int capNhatThemDichVuSpa(String maPhieu, String maGoi, String moTa, long donGia) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET ma_goi=COALESCE(ma_goi, ?), mo_ta_dich_vu=CASE WHEN"
                    + " mo_ta_dich_vu IS NULL OR mo_ta_dich_vu=N'' THEN ? ELSE mo_ta_dich_vu + N';"
                    + " ' + ? END, don_gia=don_gia+?, so_luong=CASE WHEN so_luong<=0 THEN 1 ELSE"
                    + " so_luong END, tong_tien=tong_tien+? WHERE ma_phieu = ?",
                maGoi,
                moTa,
                moTa,
                donGia,
                donGia,
                maPhieu);
    }

    @Override
    public int tinhLaiTongPhieuSpa(String maPhieu) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=don_gia*COALESCE(NULLIF(so_luong,0),1)"
                        + " WHERE ma_phieu = ?",
                maPhieu);
    }

    @Override
    public int capNhatTrangThaiSpa(String trangThaiMoi, String maPhieu, String trangThaiHienTai) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE ma_phieu = ? AND trang_thai=?",
                trangThaiMoi,
                maPhieu,
                trangThaiHienTai);
    }

    @Override
    public int hoanTatLichHen(String maPhieu) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='HOAN_TAT' WHERE ma_lich_hen = (SELECT ma_lich_hen"
                        + " FROM phieu_tiep_nhan_spa WHERE ma_phieu = ?)",
                maPhieu);
    }

    @Override
    public int huyLichHenBanGhi(String maLichHen) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='DA_HUY' WHERE ma_lich_hen = ? AND"
                        + " trang_thai='DA_XAC_NHAN'",
                maLichHen);
    }

    @Override
    public String layMaLichHenThuCung(String maLichHen) {
        Object giaTri =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT ma_thu_cung FROM lich_hen WHERE ma_lich_hen = ?", maLichHen);
        return giaTri == null ? null : giaTri.toString();
    }

    @Override
    public List<SpaDTO.DongChiTietSpa> layPhieuSpaChiTiet(String maPhieu) {
        List<Object[]> rows =
                truyVan(
                        "SELECT COALESCE(p.mo_ta_dich_vu, N'Dịch vụ Spa'),"
                                + " COALESCE(NULLIF(p.don_gia, 0), p.tong_tien),"
                                + " COALESCE(NULLIF(p.so_luong, 0), 1), p.tong_tien,"
                                + " p.can_nang_thuc_te, COALESCE(tc.loai, N'Chó') FROM"
                                + " phieu_tiep_nhan_spa p LEFT JOIN thu_cung tc ON tc.ma_thu_cung ="
                                + " p.ma_thu_cung WHERE p.ma_phieu = ? AND (p.ma_goi IS NOT NULL OR"
                                + " p.don_gia > 0 OR p.tong_tien > 0)",
                        maPhieu);
        if (rows.isEmpty()) return List.of();
        Object[] r0 = rows.get(0);
        String moTa = r0[0] != null ? r0[0].toString().trim() : "";
        if (!moTa.contains(";")) {
            return List.of(chuyenDongChiTiet(r0[0], r0[1], r0[2], r0[3]));
        }
        String[] dsDichVu = moTa.split(";\\s*");
        double canNang = r0[4] instanceof Number n ? n.doubleValue() : 5.0;
        String loai = r0[5] != null ? r0[5].toString().trim() : "Chó";
        long tongTien = r0[3] instanceof Number n ? n.longValue() : 0L;
        List<SpaDTO.DongChiTietSpa> ketQua = new ArrayList<>();
        long tongGiaTinh = 0;
        for (String dv : dsDichVu) {
            String ten = dv.trim();
            if (ten.isEmpty()) continue;
            List<Object[]> goiList =
                    ThaoTacSQL.truyVan(
                            "SELECT COALESCE(bang_gia, N'') FROM goi_dich_vu_spa WHERE ten_goi = ?"
                                    + " OR ma_goi = ?",
                            ten,
                            ten);
            long gia = -1;
            if (!goiList.isEmpty()) {
                String bg = String.valueOf(goiList.get(0)[0]).trim();
                if (!bg.isEmpty()) {
                    gia = timGiaTheoCanNang(bg, loai, canNang);
                }
            }
            if (gia < 0) gia = 0;
            tongGiaTinh += gia;
            ketQua.add(new SpaDTO.DongChiTietSpa(ten, gia, 1, gia));
        }
        if (ketQua.isEmpty()) {
            return List.of(chuyenDongChiTiet(r0[0], r0[1], r0[2], r0[3]));
        }
        if (tongGiaTinh > 0 && tongTien > 0 && tongGiaTinh != tongTien) {
            long chenhLech = tongTien - tongGiaTinh;
            SpaDTO.DongChiTietSpa last = ketQua.get(ketQua.size() - 1);
            long lastGia = last.getDonGia() + chenhLech;
            ketQua.set(
                    ketQua.size() - 1,
                    new SpaDTO.DongChiTietSpa(last.getTenDichVu(), lastGia, 1, lastGia));
        } else if (tongGiaTinh == 0 && tongTien > 0) {
            long chiaDeu = tongTien / ketQua.size();
            for (int i = 0; i < ketQua.size(); i++) {
                long g =
                        (i == ketQua.size() - 1)
                                ? (tongTien - chiaDeu * (ketQua.size() - 1))
                                : chiaDeu;
                ketQua.set(i, new SpaDTO.DongChiTietSpa(ketQua.get(i).getTenDichVu(), g, 1, g));
            }
        }
        return ketQua;
    }

    @Override
    public List<LichHen> layChiTietLichHen(String maLichHen) {
        List<Object[]> rows =
                truyVan(
                        "SELECT l.ma_thu_cung, COALESCE(l.ma_goi, ''), COALESCE(l.ma_nhan_vien,"
                                + " ''), l.bat_dau FROM lich_hen l WHERE l.ma_lich_hen = ?",
                        maLichHen);
        List<LichHen> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            LichHen lh = new LichHen(maLichHen);
            lh.setThuCung(new ThuCung(DuLieu.chuyenThanhChuoi(r[0])));
            lh.setDichVu(new GoiDichVuSpa(DuLieu.chuyenThanhChuoi(r[1])));
            lh.setNhanVien(new NhanVien(DuLieu.chuyenThanhChuoi(r[2])));
            lh.setBatDau(DuLieu.chuyenLocalDateTime(r[3]));
            ketQua.add(lh);
        }
        return ketQua;
    }

    @Override
    public List<String> khoaLichHen(String maLichHen) {
        return truyVanDanhSach(
                "SELECT trang_thai FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE ma_lich_hen = ?",
                r -> DuLieu.chuyenThanhChuoi(r[0]),
                maLichHen);
    }

    @Override
    public long demLichTrungKhiSua(
            String maLichHen,
            String maNhanVien,
            String maThuCung,
            LocalDateTime ketThuc,
            LocalDateTime batDau) {
        return laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE ma_lich_hen <> ? AND"
                        + " (ma_nhan_vien = ? OR ma_thu_cung = ?) AND trang_thai NOT IN"
                        + " ('DA_HUY','HOAN_TAT') AND bat_dau<? AND ket_thuc>?",
                maLichHen,
                maNhanVien,
                maThuCung,
                ketThuc,
                batDau);
    }

    @Override
    public int suaLichHen(
            String maKhachHang,
            String maThuCung,
            String maGoi,
            String maNhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String maLichHen) {
        return capNhat(
                "UPDATE lich_hen SET ma_khach_hang=?, ma_thu_cung=?, ma_goi=?, ma_nhan_vien=?,"
                        + " bat_dau=?, ket_thuc=? WHERE ma_lich_hen = ?",
                maKhachHang,
                maThuCung,
                maGoi,
                maNhanVien,
                batDau,
                ketThuc,
                maLichHen);
    }

    @Override
    public List<SpaDTO.DongTimKiemSpa> timKiem(
            LocalDate tuNgay, LocalDate denNgay, String trangThai) {
        String sql =
                """
SELECT t.ma_phieu AS ma_khoa, t.ma_phieu, k.ma_khach_hang, k.ho_ten, p.ma_thu_cung, p.ten,
       t.can_nang_thuc_te, COALESCE(n.ma_nhan_vien, N''), COALESCE(n.ho_ten, N''),
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
        List<SpaDTO.DongTimKiemSpa> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            String maKhoa = DuLieu.chuyenThanhChuoi(r[0]);
            PhieuTiepNhanSpa phieu = new PhieuTiepNhanSpa();
            phieu.setMaPhieu(DuLieu.chuyenThanhChuoi(r[1]));
            phieu.setCanNangThucTe(r[6] instanceof Number n ? n.doubleValue() : 0.0);
            phieu.setTongTien(r[9] instanceof Number n ? n.longValue() : 0L);
            String ttStr = DuLieu.chuyenThanhChuoi(r[10]);
            phieu.setTrangThai(ttStr.isBlank() ? null : TrangThaiPhieuSpa.tuMaDB(ttStr));
            phieu.setTaoLuc(DuLieu.chuyenLocalDateTime(r[11]));
            KhachHang kh = new KhachHang(DuLieu.chuyenThanhChuoi(r[2]));
            kh.setHoTen(DuLieu.chuyenThanhChuoi(r[3]));
            ThuCung tc = new ThuCung(DuLieu.chuyenThanhChuoi(r[4]));
            tc.setTen(DuLieu.chuyenThanhChuoi(r[5]));
            String maNv = DuLieu.chuyenThanhChuoi(r[7]);
            NhanVien nv = maNv.isBlank() ? null : new NhanVien(maNv);
            if (nv != null) {
                nv.setHoTen(DuLieu.chuyenThanhChuoi(r[8]));
            }
            ketQua.add(new SpaDTO.DongTimKiemSpa(maKhoa, phieu, kh, tc, nv));
        }
        return ketQua;
    }

    private static SpaDTO.DongChiTietSpa chuyenDongChiTiet(
            Object ten, Object gia, Object sl, Object thanhTien) {
        return new SpaDTO.DongChiTietSpa(
                DuLieu.chuyenThanhChuoi(ten),
                gia instanceof Number n ? n.longValue() : 0L,
                sl instanceof Number n ? n.intValue() : 0,
                thanhTien instanceof Number n ? n.longValue() : 0L);
    }

    @Override
    public Optional<PhieuTiepNhanSpa> timTheoMa(String maPhieu) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_phieu, ma_lich_hen, ma_thu_cung, ma_goi, mo_ta_dich_vu, don_gia,"
                                + " so_luong, can_nang_thuc_te, tong_tien, trang_thai, tao_luc,"
                                + " ma_nhan_vien, ghi_chu, anh, ma_hoa_don FROM phieu_tiep_nhan_spa"
                                + " WHERE ma_phieu = ?",
                        maPhieu);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        PhieuTiepNhanSpa phieu = new PhieuTiepNhanSpa();
        phieu.setMaPhieu(DuLieu.chuyenThanhChuoi(r[0]));
        if (r[1] != null && !r[1].toString().isBlank()) {
            LichHen lh = new LichHen();
            lh.setMaLichHen(r[1].toString());
            phieu.setLichHen(lh);
        }
        String maThuCung = DuLieu.chuyenThanhChuoi(r[2]);
        if (!maThuCung.isBlank()) {
            phieu.setThuCung(new ThuCung(maThuCung));
        }
        if (r[3] != null && !r[3].toString().isBlank()) {
            phieu.setGoiDichVu(new GoiDichVuSpa(r[3].toString()));
        }
        phieu.setMoTaDichVu(r[4] == null ? null : r[4].toString());
        phieu.setDonGia(r[5] instanceof Number n ? n.longValue() : 0L);
        phieu.setSoLuong(r[6] instanceof Number n ? n.intValue() : 1);
        phieu.setCanNangThucTe(r[7] instanceof Number n ? n.doubleValue() : 0.0);
        phieu.setTongTien(r[8] instanceof Number n ? n.longValue() : 0L);
        phieu.setTrangThai(TrangThaiPhieuSpa.tuMaDB(DuLieu.chuyenThanhChuoi(r[9])));
        phieu.setTaoLuc(DuLieu.chuyenLocalDateTime(r[10]));
        if (r[11] != null && !r[11].toString().isBlank()) {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(r[11].toString());
            phieu.setNhanVien(nv);
        }
        phieu.setGhiChu(DuLieu.chuyenThanhChuoi(r[12]));
        phieu.setAnh(r[13] == null ? null : r[13].toString());
        if (r[14] != null && !r[14].toString().isBlank()) {
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(r[14].toString());
            phieu.setHoaDon(hd);
        }
        return Optional.of(phieu);
    }
}
