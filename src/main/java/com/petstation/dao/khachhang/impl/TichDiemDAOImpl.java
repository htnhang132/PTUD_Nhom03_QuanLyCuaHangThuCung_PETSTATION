package com.petstation.dao.khachhang.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.khachhang.TichDiemDAO;
import com.petstation.model.khachhang.Voucher;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;

// Triển khai truy vấn SQL cho nghiệp vụ Tích điểm thành viên & Quản lý/Áp dụng Voucher.
public final class TichDiemDAOImpl implements TichDiemDAO {
    public TichDiemDAOImpl() {}

    // 1. KIỂM TRA RÀNG BUỘC & TRA CỨU THÔNG TIN VOUCHER

    // Truy vấn số lượt đã dùng, khóa đổi điểm và mã khách hàng của voucher (có khóa UPDLOCK)
    @Override
    public RangBuocVoucher layRangBuocVoucher(String ma) {
        List<Object[]> rows =
                truyVan(
                        "SELECT da_dung, khoa_doi_diem, ma_khach_hang FROM voucher"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_voucher=?",
                        ma);
        if (rows.isEmpty()) return null;
        Object[] r = rows.get(0);
        return new RangBuocVoucher(
                DuLieu.layGiaTri(r[0]),
                r[1] == null ? null : String.valueOf(r[1]),
                r[2] == null ? null : String.valueOf(r[2]));
    }

    // Truy vấn mệnh giá, hạn dùng, số lượt, đơn tối thiểu, trạng thái và phạm vi của voucher
    @Override
    public ThongTinVoucher timVoucher(String ma) {
        List<Object[]> rows =
                truyVan(
                        "SELECT v.menh_gia, v.hieu_luc_den, v.da_dung, v.so_luong, v.don_toi_thieu,"
                            + " v.trang_thai, v.chi_ap_dung_spa, COALESCE(v.ma_khach_hang,'') FROM"
                            + " voucher v WHERE v.ma_voucher=?",
                        ma);
        if (rows.isEmpty()) return null;
        Object[] v = rows.get(0);
        boolean chiSpa =
                Boolean.TRUE.equals(v[6]) || (v[6] instanceof Number n && n.intValue() != 0);
        return new ThongTinVoucher(
                DuLieu.layGiaTri(v[0]),
                DuLieu.chuyenLocalDateTime(v[1]),
                DuLieu.layGiaTri(v[2]),
                DuLieu.layGiaTri(v[3]),
                DuLieu.layGiaTri(v[4]),
                DuLieu.chuyenThanhChuoi(v[5]),
                chiSpa,
                DuLieu.chuyenThanhChuoi(v[7]));
    }

    // Truy vấn danh sách các mã voucher được cấp cho một khách hàng cụ thể
    @Override
    public List<Voucher> layCuaKhach(String ma) {
        List<Object[]> rows =
                truyVan(
                        "SELECT v.ma_voucher, v.menh_gia, v.hieu_luc_den, CASE WHEN"
                            + " v.da_dung>=v.so_luong THEN N'Đã dùng' WHEN"
                            + " v.hieu_luc_den<=SYSDATETIME() THEN N'Hết hạn' ELSE N'Còn hiệu lực'"
                            + " END FROM voucher v WHERE v.ma_khach_hang = ? ORDER BY v.ma_voucher"
                            + " DESC",
                        ma);
        List<Voucher> ketQua = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            Voucher v = new Voucher(DuLieu.chuyenThanhChuoi(r[0]));
            v.setMenhGia(DuLieu.layGiaTri(r[1]));
            v.setHieuLucDen(DuLieu.chuyenLocalDateTime(r[2]));
            v.setTrangThai(DuLieu.chuyenThanhChuoi(r[3]));
            ketQua.add(v);
        }
        return ketQua;
    }

    // 2. NGHIỆP VỤ ĐỔI 200 ĐIỂM TÍCH LŨY LẤY VOUCHER SPA 150.000Đ

    // Truy vấn điểm tích lũy hiện tại và trạng thái hoạt động của khách hàng
    @Override
    public ThongTinKhachHangDiem timKhachHang(String ma) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_khach_hang, diem_tich_luy, dang_hoat_dong FROM khach_hang"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_khach_hang = ?",
                        ma);
        if (rows.isEmpty()) return null;
        Object[] r = rows.get(0);
        String maKh = DuLieu.chuyenThanhChuoi(r[0]);
        long diem = DuLieu.layGiaTri(r[1]);
        boolean hoatDong =
                Boolean.TRUE.equals(r[2]) || (r[2] instanceof Number n && n.intValue() != 0);
        return new ThongTinKhachHangDiem(maKh, diem, hoatDong);
    }

    // Kiểm tra xem khóa đổi điểm (idempotency key) đã từng được dùng để tạo voucher hay chưa
    @Override
    public ThongTinTheoKhoa timTheoKhoa(String khoa) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_voucher, ma_khach_hang FROM voucher WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " khoa_doi_diem=?",
                        khoa);
        if (rows.isEmpty()) return null;
        Object[] r = rows.get(0);
        return new ThongTinTheoKhoa(DuLieu.chuyenThanhChuoi(r[0]), DuLieu.chuyenThanhChuoi(r[1]));
    }

    // Phát hành mới voucher Spa 150.000đ (hạn 60 ngày) và trừ 200 điểm + ghi lịch sử điểm khách
    // hàng
    @Override
    public String taoVoucher(String maKhachHang, String khoa) {
        String ma = taoMa("VOUCHER");
        capNhat(
                "INSERT voucher(ma_voucher, ma_khach_hang, menh_gia, hieu_luc_den, chi_ap_dung_spa,"
                        + " so_luong, da_dung, don_toi_thieu, trang_thai, khoa_doi_diem)"
                        + " VALUES(?,?,150000,DATEADD(day,60,SYSDATETIME()),1,1,0,0,'CON_HAN',?)",
                ma,
                maKhachHang,
                khoa);
        capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-200"
                        + " WHERE ma_khach_hang = ? AND diem_tich_luy>=200",
                maKhachHang);
        return ma;
    }
}
