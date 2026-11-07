package com.petstation.service;

import com.petstation.dao.QuyTrinhDAO;
import com.petstation.dao.impl.QuyTrinhDAOImpl;
import com.petstation.model.GioHang;
import com.petstation.model.Quyen;

import java.util.List;

public final class QuyTrinhService {
    private static final QuyTrinhDAO quyTrinhDAO = new QuyTrinhDAOImpl();

    private QuyTrinhService() {}

    public static void chuyenBuoc(String tu, String den, GioHang gio) {
        var q = gio.getQuyTrinh();
        if (List.of(
                        "DAT_LICH_HEN_SPA",
                        "TIEP_NHAN_SPA",
                        "CHON_GOI_SPA",
                        "CAP_NHAT_TIEN_DO_SPA",
                        "HOAN_TAT_SPA")
                .contains(den)) {
            PhienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
            if (den.equals("DAT_LICH_HEN_SPA") || den.equals("TIEP_NHAN_SPA")) return;
            var dong = quyTrinhDAO.laySpa(q.getPhieuSpa());
            if (dong.isEmpty())
                throw new IllegalArgumentException(
                        "Hãy chọn hoặc tiếp nhận một phiếu Spa trước khi chuyển bước.");
            String trangThai = dong.get(0)[0].toString();
            if (trangThai.equals("DA_THANH_TOAN"))
                throw new IllegalArgumentException("Phiếu Spa đã thanh toán. Hãy chọn phiếu khác.");
            if (den.equals("CAP_NHAT_TIEN_DO_SPA") && ((Number) dong.get(0)[1]).longValue() == 0)
                throw new IllegalArgumentException(
                        "Cần thêm dịch vụ vào phiếu trước khi chuyển sang tiến độ.");
            if (den.equals("HOAN_TAT_SPA") && !trangThai.equals("HOAN_TAT"))
                throw new IllegalArgumentException("Cần hoàn thành dịch vụ trước khi đóng phiếu.");
        }
        if (den.equals("NHAT_KY_CHAM_SOC") || den.equals("TRA_PHONG_LUU_TRU")) {
            PhienDangNhapService.yeuCau(Quyen.QUAN_LY_LUU_TRU);
            String trangThai = quyTrinhDAO.layTrangThaiHotel(q.getHopDongHotel());
            if (!List.of("DA_DAT", "DANG_O").contains(trangThai))
                throw new IllegalArgumentException(
                        "Hãy chọn hợp đồng Hotel đang đặt hoặc đang lưu trú.");
            if (den.equals("TRA_PHONG_LUU_TRU") && !trangThai.equals("DANG_O"))
                throw new IllegalArgumentException("Hợp đồng chưa nhận phòng.");
        }
        if (den.equals("THANH_TOAN")
                && (tu.equals("HOAN_TAT_SPA") || tu.equals("TRA_PHONG_LUU_TRU"))) {
            String loai = tu.equals("HOAN_TAT_SPA") ? "SPA" : "HOTEL";
            long id = loai.equals("SPA") ? q.getPhieuSpa() : q.getHopDongHotel();
            if (id <= 0) throw new IllegalArgumentException("Hãy chọn bản ghi cần thanh toán.");
            if (!gio.kiemTraTrong() && !(loai.equals(gio.getKieuNguon()) && id == gio.getIdNguon()))
                throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
            BanHangService.quyetToan(loai, id, gio);
        }
    }

    public static List<Object[]> layLichHen(long id) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        return quyTrinhDAO.layLichHen(id);
    }

    public static List<Object[]> layPhong() {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_LUU_TRU);
        return quyTrinhDAO.layPhong();
    }

    public static List<com.petstation.model.PhongHotel> layPhongHotel() {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_LUU_TRU);
        var lich = quyTrinhDAO.layLichPhong();
        return quyTrinhDAO.layPhongChiTiet().stream()
                .<com.petstation.model.PhongHotel>map(
                        p -> {
                            String maPhong = p[0] != null ? p[0].toString() : "";
                            var cacLich =
                                    lich.stream()
                                            .filter(l -> l[0] != null && l[0].toString().equals(maPhong))
                                            .toList();
                            java.util.function.Function<
                                            Object[], com.petstation.model.PhongHotel.LichPhong>
                                    doi =
                                            l ->
                                                    new com.petstation.model.PhongHotel.LichPhong(
                                                            l[1] != null ? l[1].toString() : "",
                                                            l[2] != null ? l[2].toString() : "",
                                                            l[3] != null ? l[3].toString() : "",
                                                            l[4] != null ? ((java.sql.Timestamp) l[4]).toLocalDateTime() : null,
                                                            l[5] != null ? ((java.sql.Timestamp) l[5]).toLocalDateTime() : null);
                            var dangO =
                                    cacLich.stream()
                                            .filter(l -> "DANG_O".equals(l[6]))
                                            .map(doi)
                                            .findFirst()
                                            .orElse(null);
                            return new com.petstation.model.PhongHotel(
                                    maPhong,
                                    p[1] != null ? p[1].toString() : "",
                                    p[2] != null ? ((Number) p[2]).longValue() : 0L,
                                    p[3] != null ? p[3].toString() : "",
                                    p[4] != null ? ((Number) p[4]).intValue() : 1,
                                    dangO,
                                    cacLich.stream()
                                            .filter(l -> "DA_DAT".equals(l[6]))
                                            .map(doi)
                                            .toList());
                        })
                .toList();
    }
}
