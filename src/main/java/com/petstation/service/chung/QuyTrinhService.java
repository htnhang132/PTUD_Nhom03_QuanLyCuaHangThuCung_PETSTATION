package com.petstation.service.chung;

import com.petstation.dao.chung.QuyTrinhDAO;
import com.petstation.dao.chung.impl.QuyTrinhDAOImpl;
import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.dto.SpaDTO;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.spa.LichHen;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Tầng dịch vụ điều phối quy trình nghiệp vụ Spa & Lưu trú Hotel (Service)
public final class QuyTrinhService {
    private final QuyTrinhDAO quyTrinhDAO;
    private final BanHangService banHangService;
    private final PhienDangNhapService phienDangNhapService;

    public QuyTrinhService() {
        this(new QuyTrinhDAOImpl(), new BanHangService(), PhienDangNhapService.macDinh());
    }

    public QuyTrinhService(QuyTrinhDAO quyTrinhDAO) {
        this(quyTrinhDAO, new BanHangService(), PhienDangNhapService.macDinh());
    }

    public QuyTrinhService(
            QuyTrinhDAO quyTrinhDAO,
            BanHangService banHangService,
            PhienDangNhapService phienDangNhapService) {
        this.quyTrinhDAO = Objects.requireNonNull(quyTrinhDAO);
        this.banHangService = Objects.requireNonNull(banHangService);
        this.phienDangNhapService = Objects.requireNonNull(phienDangNhapService);
    }

    // 1. ĐIỀU PHỐI CHUYỂN BƯỚC QUY TRÌNH SPA, LƯU TRÚ HOTEL & QUYẾT TOÁN

    public void chuyenBuoc(String tu, String den, GioHang gio) {
        var q = gio.getQuyTrinh();
        if (List.of(
                        "DAT_LICH_HEN_SPA",
                        "TIEP_NHAN_SPA",
                        "CHON_GOI_SPA",
                        "CAP_NHAT_TIEN_DO_SPA",
                        "HOAN_TAT_SPA")
                .contains(den)) {
            phienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
            if (den.equals("DAT_LICH_HEN_SPA") || den.equals("TIEP_NHAN_SPA")) return;
            SpaDTO.ThongTinBuocSpa dong =
                    quyTrinhDAO
                            .laySpa(q.getPhieuSpa())
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Hãy chọn hoặc tiếp nhận một phiếu Spa trước"
                                                            + " khi chuyển bước."));
            String trangThai = dong.getTrangThai();
            if (trangThai.equals(TrangThaiPhieuSpa.DA_THANH_TOAN.maDB()))
                throw new IllegalStateException("Phiếu Spa đã thanh toán. Hãy chọn phiếu khác.");
            if (den.equals("CAP_NHAT_TIEN_DO_SPA") && !dong.isCoDichVu())
                throw new IllegalArgumentException(
                        "Cần thêm dịch vụ vào phiếu trước khi chuyển sang tiến độ.");
            if (den.equals("HOAN_TAT_SPA") && !trangThai.equals(TrangThaiPhieuSpa.HOAN_TAT.maDB()))
                throw new IllegalArgumentException("Cần hoàn thành dịch vụ trước khi đóng phiếu.");
        }
        if (den.equals("NHAT_KY_CHAM_SOC") || den.equals("TRA_PHONG_LUU_TRU")) {
            phienDangNhapService.yeuCau(Quyen.QUAN_LY_LUU_TRU);
            String trangThai = quyTrinhDAO.layTrangThaiHotel(q.getHopDongHotel());
            if (!List.of(TrangThaiHopDong.DA_DAT.maDB(), TrangThaiHopDong.DANG_O.maDB())
                    .contains(trangThai))
                throw new IllegalArgumentException(
                        "Hãy chọn hợp đồng Hotel đang đặt hoặc đang lưu trú.");
            if (den.equals("TRA_PHONG_LUU_TRU")
                    && !trangThai.equals(TrangThaiHopDong.DANG_O.maDB()))
                throw new IllegalArgumentException("Hợp đồng chưa nhận phòng.");
        }
        if (den.equals("THANH_TOAN")
                && (tu.equals("HOAN_TAT_SPA") || tu.equals("TRA_PHONG_LUU_TRU"))) {
            String loai = tu.equals("HOAN_TAT_SPA") ? "SPA" : "HOTEL";
            String ma = loai.equals("SPA") ? q.getPhieuSpa() : q.getHopDongHotel();
            if (ma == null || ma.isBlank())
                throw new IllegalArgumentException("Hãy chọn bản ghi cần thanh toán.");
            if (!gio.kiemTraTrong()
                    && !(loai.equals(gio.getKieuNguon()) && ma.equals(gio.getMaNguon())))
                throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
            banHangService.quyetToan(loai, ma, gio);
        }
    }

    // Lấy thông tin lịch hẹn Spa đã xác nhận để tiếp nhận thú cưng
    public Optional<LichHen> layLichHen(String maLichHen) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        return quyTrinhDAO.layLichHen(maLichHen);
    }

    // 2. TRUY VẤN & TỔNG HỢP DỮ LIỆU SƠ ĐỒ PHÒNG LƯU TRÚ (TraCuuPhongHotelPanel)

    // Tổng hợp danh sách đối tượng PhongHotel cho màn hình Sơ đồ phòng lưu trú
    public List<PhongHotel> layPhongHotel() {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_LUU_TRU);
        var lich = quyTrinhDAO.layLichPhong();
        return quyTrinhDAO.layPhongChiTiet().stream()
                .map(
                        p -> {
                            String maPhong = p.getMaPhong() != null ? p.getMaPhong() : "";
                            var cacLich =
                                    lich.stream()
                                            .filter(
                                                    l ->
                                                            l.getPhong() != null
                                                                    && maPhong.equals(
                                                                            l.getPhong()
                                                                                    .getMaPhong()))
                                            .toList();
                            var dangO =
                                    cacLich.stream()
                                            .filter(
                                                    l ->
                                                            TrangThaiHopDong.DANG_O
                                                                    .maDB()
                                                                    .equals(
                                                                            l
                                                                                    .getTrangThaiHopDong()))
                                            .map(HotelDTO.ChiTietLichPhong::getLichPhong)
                                            .findFirst()
                                            .orElse(null);
                            return new PhongHotel(
                                    maPhong,
                                    dangO,
                                    cacLich.stream()
                                            .filter(
                                                    l ->
                                                            TrangThaiHopDong.DA_DAT
                                                                    .maDB()
                                                                    .equals(
                                                                            l
                                                                                    .getTrangThaiHopDong()))
                                            .map(HotelDTO.ChiTietLichPhong::getLichPhong)
                                            .toList(),
                                    p.getLoaiPhong(),
                                    p.getGiaDem(),
                                    p.getSucChua(),
                                    p.getTrangThai());
                        })
                .toList();
    }
}
