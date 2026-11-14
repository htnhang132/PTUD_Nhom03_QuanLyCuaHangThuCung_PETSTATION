package com.petstation.dao.chung.impl;

import static com.petstation.database.ThaoTacSQL.layGiaTriDon;
import static com.petstation.database.ThaoTacSQL.truyVan;

import com.petstation.dao.chung.QuyTrinhDAO;
import com.petstation.dto.HotelDTO;
import com.petstation.dto.SpaDTO;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.spa.LichHen;
import com.petstation.util.DuLieu;

import java.util.List;
import java.util.Optional;

// Triển khai truy vấn SQL cho quy trình Spa/Lưu trú và màn hình Sơ đồ phòng lưu trú.
public final class QuyTrinhDAOImpl implements QuyTrinhDAO {
    public QuyTrinhDAOImpl() {}

    // 1. TRUY VẤN QUY TRÌNH SPA & LỊCH HẸN

    // Truy vấn trạng thái phiếu Spa và cờ kiểm tra đã có dịch vụ trong phiếu hay
    // chưa
    @Override
    public Optional<SpaDTO.ThongTinBuocSpa> laySpa(String maPhieu) {
        List<Object[]> dong =
                truyVan(
                        "SELECT trang_thai, CASE WHEN (ma_goi IS NOT NULL OR don_gia>0 OR"
                                + " tong_tien>0) THEN 1 ELSE 0 END FROM phieu_tiep_nhan_spa"
                                + " WHERE ma_phieu = ?",
                        maPhieu);
        if (dong.isEmpty()) return Optional.empty();
        Object[] r = dong.get(0);
        return Optional.of(
                new SpaDTO.ThongTinBuocSpa(
                        DuLieu.chuyenThanhChuoi(r[0]),
                        r[1] != null && ((Number) r[1]).longValue() > 0));
    }

    // Truy vấn mã lịch hẹn và mã thú cưng của lịch hẹn đang ở trạng thái
    // DA_XAC_NHAN
    @Override
    public Optional<LichHen> layLichHen(String maLichHen) {
        List<Object[]> dong =
                truyVan(
                        "SELECT l.ma_lich_hen, p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                                + " p.ma_thu_cung=l.ma_thu_cung WHERE l.ma_lich_hen = ? AND"
                                + " l.trang_thai='DA_XAC_NHAN'",
                        maLichHen);
        if (dong.isEmpty()) return Optional.empty();
        Object[] r = dong.get(0);
        LichHen lh = new LichHen(DuLieu.chuyenThanhChuoi(r[0]));
        lh.setThuCung(new ThuCung(DuLieu.chuyenThanhChuoi(r[1])));
        return Optional.of(lh);
    }

    // 2. TRUY VẤN QUY TRÌNH LƯU TRÚ & SƠ ĐỒ PHÒNG HOTEL

    // Truy vấn trạng thái của một hợp đồng lưu trú theo mã hoặc ID
    @Override
    public String layTrangThaiHotel(String maHopDong) {
        return DuLieu.chuyenThanhChuoi(
                layGiaTriDon(
                        "SELECT trang_thai FROM hop_dong_luu_tru WHERE ma_hop_dong = ?",
                        maHopDong));
    }

    // Truy vấn danh sách phòng lưu trú kèm số lượt đặt trước và hợp đồng ưu tiên
    // (DANG_O / DA_DAT)
    @Override
    public List<HotelDTO.TomTatPhong> layPhong() {
        List<Object[]> rows =
                truyVan(
                        "SELECT p.ma_phong AS ma_khoa, p.ma_phong, p.loai_phong, p.gia_dem,"
                            + " p.trang_thai, (SELECT COUNT(*) FROM hop_dong_luu_tru h WHERE"
                            + " h.ma_phong=p.ma_phong AND h.trang_thai='DA_DAT'), COALESCE((SELECT"
                            + " TOP 1 h.ma_hop_dong FROM hop_dong_luu_tru h WHERE"
                            + " h.ma_phong=p.ma_phong AND h.trang_thai IN('DA_DAT','DANG_O') ORDER"
                            + " BY CASE h.trang_thai WHEN 'DANG_O' THEN 0 ELSE 1 END, h.ngay_vao),"
                            + " '') FROM phong_hotel p ORDER BY p.ma_phong");
        return rows.stream()
                .map(
                        r -> {
                            String maKhoa = DuLieu.chuyenThanhChuoi(r[0]);
                            String maHd = DuLieu.chuyenThanhChuoi(r[6]);
                            PhongHotel phong =
                                    new PhongHotel(
                                            DuLieu.chuyenThanhChuoi(r[1]),
                                            null,
                                            List.of(),
                                            DuLieu.chuyenThanhChuoi(r[2]),
                                            r[3] != null ? ((Number) r[3]).longValue() : 0L,
                                            1,
                                            DuLieu.chuyenThanhChuoi(r[4]));
                            HopDongLuuTru hd = null;
                            if (!maHd.isBlank()) {
                                hd = new HopDongLuuTru();
                                hd.setMaHopDong(maHd);
                            }
                            return new HotelDTO.TomTatPhong(
                                    maKhoa,
                                    phong,
                                    hd,
                                    r[5] != null ? ((Number) r[5]).intValue() : 0);
                        })
                .toList();
    }

    // Truy vấn thông tin gốc của tất cả các phòng từ bảng phong_hotel (phục vụ Sơ
    // đồ phòng)
    @Override
    public List<PhongHotel> layPhongChiTiet() {
        return truyVan(
                        "SELECT ma_phong, loai_phong, gia_dem, trang_thai, suc_chua FROM"
                                + " phong_hotel ORDER BY ma_phong")
                .stream()
                .map(
                        r ->
                                new PhongHotel(
                                        DuLieu.chuyenThanhChuoi(r[0]),
                                        null,
                                        List.of(),
                                        DuLieu.chuyenThanhChuoi(r[1]),
                                        r[2] != null ? ((Number) r[2]).longValue() : 0L,
                                        r[4] != null ? ((Number) r[4]).intValue() : 1,
                                        DuLieu.chuyenThanhChuoi(r[3])))
                .toList();
    }

    // Truy vấn danh sách lịch lưu trú đang ở (DANG_O) và đặt trước (DA_DAT) của
    // từng phòng
    @Override
    public List<HotelDTO.ChiTietLichPhong> layLichPhong() {
        return truyVan(
                        "SELECT h.ma_phong, h.ma_hop_dong, t.ma_thu_cung, t.ten,"
                                + " k.ma_khach_hang, k.ho_ten, h.ngay_vao,"
                                + " h.ngay_ra_du_kien, h.trang_thai FROM hop_dong_luu_tru h JOIN"
                                + " thu_cung t ON t.ma_thu_cung=h.ma_thu_cung JOIN khach_hang k ON"
                                + " k.ma_khach_hang=h.ma_khach_hang WHERE h.trang_thai"
                                + " IN('DA_DAT','DANG_O') ORDER BY h.ngay_vao, h.ma_hop_dong")
                .stream()
                .map(
                        r -> {
                            PhongHotel phong = new PhongHotel();
                            phong.setMaPhong(DuLieu.chuyenThanhChuoi(r[0]));
                            HopDongLuuTru hd = new HopDongLuuTru();
                            hd.setMaHopDong(DuLieu.chuyenThanhChuoi(r[1]));
                            ThuCung tc = new ThuCung(DuLieu.chuyenThanhChuoi(r[2]));
                            tc.setTen(DuLieu.chuyenThanhChuoi(r[3]));
                            KhachHang kh = new KhachHang(DuLieu.chuyenThanhChuoi(r[4]));
                            kh.setHoTen(DuLieu.chuyenThanhChuoi(r[5]));
                            return new HotelDTO.ChiTietLichPhong(
                                    phong,
                                    new HotelDTO.LichPhong(
                                            hd,
                                            tc,
                                            kh,
                                            DuLieu.chuyenLocalDateTime(r[6]),
                                            DuLieu.chuyenLocalDateTime(r[7])),
                                    DuLieu.chuyenThanhChuoi(r[8]));
                        })
                .toList();
    }
}
