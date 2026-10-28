package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.TichDiemDAO;
import com.petstation.dao.impl.TichDiemDAOImpl;
import com.petstation.model.Quyen;

import java.time.LocalDateTime;
import java.util.List;

/** Quy tắc tích điểm và đổi voucher Spa của khách hàng. */
public final class TichDiemService {
    private static final TichDiemDAO tichDiemDAO = new TichDiemDAOImpl();

    public static final long SO_TIEN_MOI_DIEM = 10_000;
    public static final int DIEM_DOI_VOUCHER = 200;

    private TichDiemService() {}

    public static boolean coTheSuaDanhMuc(String ma) {
        if (!PhienDangNhapService.layPhien().kiemTraCo(Quyen.PROMOTION_MANAGE)) return false;
        var dong = tichDiemDAO.layRangBuocVoucher(ma);
        return !dong.isEmpty()
                && dong.get(0)[1] == null
                && ((Number) dong.get(0)[0]).longValue() == 0;
    }

    /** Khóa thao tác được giữ nguyên khi gửi lại để tránh trừ điểm hai lần. */
    public static String doiVoucher(String maKhachHang, String khoa) {
        PhienDangNhapService.yeuCau(Quyen.POS_SELL);
        if (khoa == null || khoa.isBlank() || khoa.length() > 80)
            throw new IllegalArgumentException("Thiếu khóa đổi voucher.");
        return thucHienGiaoDich(
                ketNoi -> {
                    var khach = tichDiemDAO.timKhachHang(maKhachHang);
                    if (khach.isEmpty() || !Boolean.TRUE.equals(khach.get(0)[2]))
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

    /**
     * Kiểm tra trước khi hiển thị giảm giá; thanh toán còn giữ khóa và nhận lượt dùng trong
     * transaction.
     */
    public static long kiemTraVoucher(String ma, String khachHang, String nguon, long tamTinh) {
        PhienDangNhapService.layPhien();
        var cacDong = tichDiemDAO.timVoucher(ma);
        if (cacDong.isEmpty()) throw new IllegalArgumentException("Mã voucher không tồn tại.");
        Object[] v = cacDong.get(0);
        if (!"ACTIVE".equals(v[5]) || ((Number) v[2]).longValue() >= ((Number) v[3]).longValue())
            throw new IllegalArgumentException("Voucher đã dùng hết lượt hoặc ngừng áp dụng.");
        if (!((java.sql.Timestamp) v[1]).toLocalDateTime().isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Voucher đã hết hạn.");
        if (!v[7].toString().isEmpty() && !v[7].toString().equals(khachHang))
            throw new IllegalArgumentException("Voucher không thuộc khách hàng đang chọn.");
        if (Boolean.TRUE.equals(v[6]) && !"SPA".equals(nguon))
            throw new IllegalArgumentException("Voucher chỉ áp dụng cho thanh toán Spa.");
        if (tamTinh < ((Number) v[4]).longValue())
            throw new IllegalArgumentException("Chưa đủ giá trị đơn tối thiểu của voucher.");
        return ((Number) v[0]).longValue();
    }

    public static List<Object[]> layVoucher(String maKhachHang) {
        PhienDangNhapService.yeuCau(Quyen.POS_SELL);
        return tichDiemDAO.layCuaKhach(maKhachHang);
    }
}
