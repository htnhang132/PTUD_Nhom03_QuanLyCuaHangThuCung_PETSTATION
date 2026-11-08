package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.QuyTrinhDAO;
import com.petstation.util.DuLieu;

import java.util.List;

// Triển khai truy vấn SQL cho quy trình Spa/Lưu trú và màn hình Sơ đồ phòng lưu trú.
public final class QuyTrinhDAOImpl implements QuyTrinhDAO {
    public QuyTrinhDAOImpl() {}

    // 1. TRUY VẤN QUY TRÌNH SPA & LỊCH HẸN

    // Truy vấn trạng thái phiếu Spa và cờ kiểm tra đã có dịch vụ trong phiếu hay chưa
    public List<Object[]> laySpa(long id) {
        return truyVan(
                "SELECT trang_thai, CASE WHEN (ma_goi IS NOT NULL OR don_gia>0 OR tong_tien>0)"
                        + " THEN 1 ELSE 0 END FROM phieu_tiep_nhan_spa t WHERE (t.ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(t.ma_phieu, PATINDEX('%[0-9]%', t.ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    // Truy vấn mã lịch hẹn và mã thú cưng của lịch hẹn đang ở trạng thái DA_XAC_NHAN
    public List<Object[]> layLichHen(long id) {
        return truyVan(
                "SELECT l.ma_lich_hen, p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.ma_thu_cung=l.ma_thu_cung WHERE (l.ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_lich_hen, PATINDEX('%[0-9]%', l.ma_lich_hen), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND l.trang_thai='DA_XAC_NHAN'",
                id, id);
    }

    // 2. TRUY VẤN QUY TRÌNH LƯU TRÚ & SƠ ĐỒ PHÒNG HOTEL

    // Truy vấn trạng thái của một hợp đồng lưu trú theo mã hoặc ID
    public String layTrangThaiHotel(long id) {
        return DuLieu.chuyenThanhChuoi(
                layGiaTriDon("SELECT trang_thai FROM hop_dong_luu_tru WHERE (ma_hop_dong = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, PATINDEX('%[0-9]%', ma_hop_dong), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", id, id));
    }

    // Truy vấn danh sách phòng lưu trú kèm số lượt đặt trước và hợp đồng ưu tiên (DANG_O / DA_DAT)
    public List<Object[]> layPhong() {
        List<Object[]> rows = truyVan(
                "SELECT p.ma_phong AS ma_khoa, p.ma_phong, p.loai_phong, p.gia_dem, p.trang_thai, "
                    + "(SELECT COUNT(*) FROM hop_dong_luu_tru h WHERE h.ma_phong=p.ma_phong AND h.trang_thai='DA_DAT'), "
                    + "COALESCE((SELECT TOP 1 h.ma_hop_dong FROM hop_dong_luu_tru h WHERE h.ma_phong=p.ma_phong AND h.trang_thai IN('DA_DAT','DANG_O') ORDER BY CASE h.trang_thai WHEN 'DANG_O' THEN 0 ELSE 1 END, h.ngay_vao), '') "
                    + "FROM phong_hotel p ORDER BY p.ma_phong");
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
            if (r[6] != null && !r[6].toString().isEmpty() && !"0".equals(r[6].toString())) {
                r[6] = DuLieu.layIdCauNoi(null, String.valueOf(r[6]));
            } else {
                r[6] = 0L;
            }
        }
        return rows;
    }

    // Truy vấn thông tin gốc của tất cả các phòng từ bảng phong_hotel (phục vụ Sơ đồ phòng)
    public java.util.List<Object[]> layPhongChiTiet() {
        return truyVan(
                "SELECT ma_phong, loai_phong, gia_dem, trang_thai, suc_chua FROM phong_hotel ORDER BY ma_phong");
    }

    // Truy vấn danh sách lịch lưu trú đang ở (DANG_O) và đặt trước (DA_DAT) của từng phòng
    public java.util.List<Object[]> layLichPhong() {
        return truyVan(
                "SELECT h.ma_phong, h.ma_hop_dong, t.ten, k.ho_ten, h.ngay_vao, h.ngay_ra_du_kien, h.trang_thai "
                        + "FROM hop_dong_luu_tru h JOIN thu_cung t ON t.ma_thu_cung=h.ma_thu_cung JOIN "
                        + "khach_hang k ON k.ma_khach_hang=h.ma_khach_hang WHERE h.trang_thai "
                        + "IN('DA_DAT','DANG_O') ORDER BY h.ngay_vao, h.ma_hop_dong");
    }
}
