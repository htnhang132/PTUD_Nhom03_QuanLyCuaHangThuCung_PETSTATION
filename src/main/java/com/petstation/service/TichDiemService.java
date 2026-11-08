package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.TichDiemDAO;
import com.petstation.dao.impl.TichDiemDAOImpl;
import com.petstation.model.Quyen;

import java.time.LocalDateTime;
import java.util.List;

// Tầng xử lý nghiệp vụ Tích điểm & Voucher khách hàng (Service)
public final class TichDiemService {
    private static final TichDiemDAO tichDiemDAO = new TichDiemDAOImpl();

    public static final long SO_TIEN_MOI_DIEM = 10_000;
    public static final int DIEM_DOI_VOUCHER = 200;

    private TichDiemService() {}

    // 1. KIỂM TRA QUYỀN & RÀNG BUỘC CHỈNH SỬA VOUCHER TRÊN DANH MỤC

    // Kiểm tra một mã voucher có được phép chỉnh sửa trên DanhMucVoucherPanel hay không
    public static boolean coTheSuaDanhMuc(String ma) {
        if (!PhienDangNhapService.layPhien().kiemTraCo(Quyen.QUAN_LY_KHUYEN_MAI)) return false;
        var dong = tichDiemDAO.layRangBuocVoucher(ma);
        return !dong.isEmpty()
                && dong.get(0)[1] == null
                && ((Number) dong.get(0)[0]).longValue() == 0;
    }

    // 2. NGHIỆP VỤ ĐỔI ĐIỂM TÍCH LŨY LẤY VOUCHER SPA (200 ĐIỂM = 150.000Đ)

    // Đổi 200 điểm tích lũy của khách hàng lấy 1 mã Voucher Spa trị giá 150.000đ (hạn 60 ngày).
    public static String doiVoucher(String maKhachHang, String khoa) {
        PhienDangNhapService.yeuCau(Quyen.BAN_HANG);
        if (khoa == null || khoa.isBlank() || khoa.length() > 80)
            throw new IllegalArgumentException("Thiếu khóa đổi voucher.");
        return thucHienGiaoDich(
                ketNoi -> {
                    var khach = tichDiemDAO.timKhachHang(maKhachHang);
                    Object ttKhach = khach.isEmpty() ? null : khach.get(0)[2];
                    boolean dangHoatDong =
                            Boolean.TRUE.equals(ttKhach)
                                    || (ttKhach instanceof Number n && n.intValue() != 0);
                    if (!dangHoatDong)
                        throw new IllegalArgumentException("Khách hàng không hoạt động.");
                    long id = ((Number) khach.get(0)[0]).longValue();
                    var cu = tichDiemDAO.timTheoKhoa(khoa);
                    if (!cu.isEmpty()) {
                        if (((Number) cu.get(0)[1]).longValue() != id)
                            throw new IllegalArgumentException(
                                    "Khóa đổi voucher đã thuộc khách khác.");
                        return cu.get(0)[0].toString();
                    }
                    if (((Number) khach.get(0)[1]).longValue() < DIEM_DOI_VOUCHER)
                        throw new IllegalArgumentException(
                                "Cần đủ 200 điểm để đổi voucher Spa 150.000đ.");
                    String ma = tichDiemDAO.taoVoucher(id, khoa);
                    NhatKyHeThongService.ghiNhatKy("Đổi voucher", maKhachHang + " / " + ma);
                    return ma;
                });
    }

    // 3. TRA CỨU & KIỂM TRA ĐIỀU KIỆN ÁP DỤNG VOUCHER KHI THANH TOÁN

    // Kiểm tra tính hợp lệ của mã voucher khi áp dụng vào hóa đơn
    public static long kiemTraVoucher(String ma, String khachHang, String nguon, long tamTinh) {
        PhienDangNhapService.layPhien();
        var cacDong = tichDiemDAO.timVoucher(ma);
        if (cacDong.isEmpty()) throw new IllegalArgumentException("Mã voucher không tồn tại.");
        Object[] v = cacDong.get(0);
        if (!"CON_HAN".equals(v[5]) || ((Number) v[2]).longValue() >= ((Number) v[3]).longValue())
            throw new IllegalArgumentException("Voucher đã dùng hết lượt hoặc ngừng áp dụng.");
        if (!((java.sql.Timestamp) v[1]).toLocalDateTime().isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Voucher đã hết hạn.");
        if (!v[7].toString().isEmpty() && !v[7].toString().equals(khachHang))
            throw new IllegalArgumentException("Voucher không thuộc khách hàng đang chọn.");
        boolean chiSpa =
                Boolean.TRUE.equals(v[6]) || (v[6] instanceof Number n && n.intValue() != 0);
        if (chiSpa && !"SPA".equals(nguon))
            throw new IllegalArgumentException("Voucher chỉ áp dụng cho thanh toán Spa.");
        if (tamTinh < ((Number) v[4]).longValue())
            throw new IllegalArgumentException("Chưa đủ giá trị đơn tối thiểu của voucher.");
        return ((Number) v[0]).longValue();
    }

    // Lấy danh sách các mã voucher của một khách hàng cụ thể
    public static List<Object[]> layVoucher(String maKhachHang) {
        PhienDangNhapService.yeuCau(Quyen.BAN_HANG);
        return tichDiemDAO.layCuaKhach(maKhachHang);
    }
}
