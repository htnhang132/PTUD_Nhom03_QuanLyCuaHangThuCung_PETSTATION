package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.TichDiemDAO;
import com.petstation.util.DuLieu;

import java.util.List;

// Triển khai truy vấn SQL cho nghiệp vụ Tích điểm thành viên & Quản lý/Áp dụng Voucher.
public final class TichDiemDAOImpl implements TichDiemDAO {
    public TichDiemDAOImpl() {}

    // 1. KIỂM TRA RÀNG BUỘC & TRA CỨU THÔNG TIN VOUCHER

    // Truy vấn số lượt đã dùng, khóa đổi điểm và mã khách hàng của voucher (có khóa UPDLOCK)
    @Override
    public List<Object[]> layRangBuocVoucher(String ma) {
        return truyVan(
                "SELECT da_dung, khoa_doi_diem, ma_khach_hang FROM voucher WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE ma_voucher=?",
                ma);
    }

    // Truy vấn mệnh giá, hạn dùng, số lượt, đơn tối thiểu, trạng thái và phạm vi của voucher
    @Override
    public List<Object[]> timVoucher(String ma) {
        return truyVan(
                "SELECT"
                    + " v.menh_gia, v.hieu_luc_den, v.da_dung, v.so_luong, v.don_toi_thieu, v.trang_thai, v.chi_ap_dung_spa, COALESCE(v.ma_khach_hang,'')"
                    + " FROM voucher v WHERE v.ma_voucher=?",
                ma);
    }

    // Truy vấn danh sách các mã voucher được cấp cho một khách hàng cụ thể
    @Override
    public List<Object[]> layCuaKhach(String ma) {
        return truyVan(
                "SELECT v.ma_voucher, v.menh_gia, v.hieu_luc_den, CASE WHEN v.da_dung>=v.so_luong THEN"
                    + " N'Đã dùng' WHEN v.hieu_luc_den<=SYSDATETIME() THEN N'Hết hạn' ELSE N'Còn"
                    + " hiệu lực' END FROM voucher v"
                    + " WHERE (v.ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(v.ma_khach_hang, PATINDEX('%[0-9]%', v.ma_khach_hang), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY v.ma_voucher DESC",
                ma, ma);
    }

    // 2. NGHIỆP VỤ ĐỔI 200 ĐIỂM TÍCH LŨY LẤY VOUCHER SPA 150.000Đ

    // Truy vấn điểm tích lũy hiện tại và trạng thái hoạt động của khách hàng
    @Override
    public List<Object[]> timKhachHang(String ma) {
        List<Object[]> rows = truyVan(
                "SELECT ma_khach_hang, diem_tich_luy, dang_hoat_dong FROM khach_hang WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                ma, ma);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    // Kiểm tra xem khóa đổi điểm (idempotency key) đã từng được dùng để tạo voucher hay chưa
    @Override
    public List<Object[]> timTheoKhoa(String khoa) {
        List<Object[]> rows = truyVan(
                "SELECT ma_voucher, ma_khach_hang FROM voucher WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " khoa_doi_diem=?",
                khoa);
        for (Object[] r : rows) {
            if (r[1] != null) {
                r[1] = DuLieu.layIdCauNoi(null, String.valueOf(r[1]));
            }
        }
        return rows;
    }

    // Phát hành mới voucher Spa 150.000đ (hạn 60 ngày) và trừ 200 điểm + ghi lịch sử điểm khách hàng
    @Override
    public String taoVoucher(long khachHang, String khoa) {
        String ma = taoMa("VOUCHER");
        String maKH = String.valueOf(khachHang);
        List<Object[]> rKh = truyVan(
                "SELECT ma_khach_hang FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                khachHang, khachHang);
        if (!rKh.isEmpty()) {
            maKH = String.valueOf(rKh.get(0)[0]);
        }
        capNhat(
                "INSERT"
                    + " voucher(ma_voucher, ma_khach_hang, menh_gia, hieu_luc_den, chi_ap_dung_spa, so_luong, da_dung, don_toi_thieu, trang_thai, khoa_doi_diem)"
                    + " VALUES(?,?,150000,DATEADD(day,60,SYSDATETIME()),1,1,0,0,'CON_HAN',?)",
                ma,
                maKH,
                khoa);
        capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-200,"
                    + " lich_su_diem = CASE WHEN lich_su_diem IS NULL OR lich_su_diem=N'' THEN N''"
                    + " ELSE lich_su_diem + N';' END + CONVERT(NVARCHAR(19), SYSDATETIME(), 120) +"
                    + " N'||-200|' + CAST(diem_tich_luy-200 AS NVARCHAR(20)) + N'|Đổi 200 điểm lấy"
                    + " voucher ' + ? WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND diem_tich_luy>=200",
                ma,
                khachHang,
                khachHang);
        return ma;
    }
}
