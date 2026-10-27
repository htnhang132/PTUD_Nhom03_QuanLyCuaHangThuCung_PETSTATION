package com.petstation.service;

import com.petstation.dao.QuyTrinhDAO;
import com.petstation.dao.impl.QuyTrinhImpl;
import com.petstation.model.GioHang;
import com.petstation.model.Quyen;

import java.util.List;

/** Kiểm tra chuyển bước bằng trạng thái lưu trong SQL Server. */
public final class QuyTrinhService {
    private static final QuyTrinhDAO quyTrinhDAO = new QuyTrinhImpl();

    private QuyTrinhService() {}

    public static void chuyenBuoc(String tu, String den, GioHang gio) {
        var q = gio.layQuyTrinh();
        if (den.startsWith("SPA_")) {
            PhienDangNhapService.yeuCau(Quyen.SPA_MANAGE);
            if (den.equals("SPA_SCHEDULE") || den.equals("SPA_INTAKE")) return;
            var dong = quyTrinhDAO.laySpa(q.layPhieuSpa());
            if (dong.isEmpty())
                throw new IllegalArgumentException(
                        "Hãy chọn hoặc tiếp nhận một phiếu Spa trước khi chuyển bước.");
            String trangThai = dong.get(0)[0].toString();
            if (trangThai.equals("PAID"))
                throw new IllegalArgumentException("Phiếu Spa đã thanh toán. Hãy chọn phiếu khác.");
            if (den.equals("SPA_PROGRESS") && ((Number) dong.get(0)[1]).longValue() == 0)
                throw new IllegalArgumentException(
                        "Cần thêm dịch vụ vào phiếu trước khi chuyển sang tiến độ.");
            if (den.equals("SPA_CLOSE") && !trangThai.equals("COMPLETED"))
                throw new IllegalArgumentException("Cần hoàn thành dịch vụ trước khi đóng phiếu.");
        }
        if (den.equals("HOTEL_JOURNAL") || den.equals("CHECKOUT")) {
            PhienDangNhapService.yeuCau(Quyen.HOTEL_MANAGE);
            String trangThai = quyTrinhDAO.layTrangThaiHotel(q.layHopDongHotel());
            if (!List.of("BOOKED", "CHECKED_IN").contains(trangThai))
                throw new IllegalArgumentException(
                        "Hãy chọn hợp đồng Hotel đang đặt hoặc đang lưu trú.");
            if (den.equals("CHECKOUT") && !trangThai.equals("CHECKED_IN"))
                throw new IllegalArgumentException("Hợp đồng chưa nhận phòng.");
        }
        if (den.equals("PAYMENT") && (tu.equals("SPA_CLOSE") || tu.equals("CHECKOUT"))) {
            String loai = tu.equals("SPA_CLOSE") ? "SPA" : "HOTEL";
            long id = loai.equals("SPA") ? q.layPhieuSpa() : q.layHopDongHotel();
            if (id <= 0) throw new IllegalArgumentException("Hãy chọn bản ghi cần thanh toán.");
            if (!gio.kiemTraTrong() && !(loai.equals(gio.layKieuNguon()) && id == gio.layIdNguon()))
                throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
            BanHangService.quyetToan(loai, id, gio);
        }
    }

    public static List<Object[]> layLichHen(long id) {
        PhienDangNhapService.yeuCau(Quyen.SPA_MANAGE);
        return quyTrinhDAO.layLichHen(id);
    }

    public static List<Object[]> layPhong() {
        PhienDangNhapService.yeuCau(Quyen.HOTEL_MANAGE);
        return quyTrinhDAO.layPhong();
    }

    public static List<com.petstation.model.ThongTinPhong> layThongTinPhong() {
        PhienDangNhapService.yeuCau(Quyen.HOTEL_MANAGE);
        var lich = quyTrinhDAO.layLichPhong();
        return quyTrinhDAO.layPhongChiTiet().stream()
                .map(
                        p -> {
                            long id = ((Number) p[0]).longValue();
                            var cacLich =
                                    lich.stream()
                                            .filter(l -> ((Number) l[0]).longValue() == id)
                                            .toList();
                            java.util.function.Function<
                                            Object[], com.petstation.model.ThongTinPhong.LichPhong>
                                    doi =
                                            l ->
                                                    new com.petstation.model.ThongTinPhong
                                                            .LichPhong(
                                                            ((Number) l[1]).longValue(),
                                                            l[2].toString(),
                                                            l[3].toString(),
                                                            ((java.sql.Timestamp) l[4])
                                                                    .toLocalDateTime(),
                                                            ((java.sql.Timestamp) l[5])
                                                                    .toLocalDateTime());
                            var dangO =
                                    cacLich.stream()
                                            .filter(l -> l[6].equals("CHECKED_IN"))
                                            .map(doi)
                                            .findFirst()
                                            .orElse(null);
                            return new com.petstation.model.ThongTinPhong(
                                    id,
                                    p[1].toString(),
                                    p[2].toString(),
                                    ((Number) p[3]).longValue(),
                                    p[4].toString(),
                                    ((Number) p[5]).intValue(),
                                    dangO,
                                    cacLich.stream()
                                            .filter(l -> l[6].equals("BOOKED"))
                                            .map(doi)
                                            .toList());
                        })
                .toList();
    }
}
