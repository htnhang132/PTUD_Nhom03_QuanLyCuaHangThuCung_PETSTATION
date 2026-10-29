package com.petstation.gui.navigation;

import com.petstation.model.MaManHinh;

import java.util.List;
import java.util.Optional;

/**
 * Cung cấp giao diện và xử lý tương tác cho màn hình các quy trình.
 *
 * <p>Các quy trình nhiều bước; thanh luồng ở khung chính hiển thị bước hiện tại.
 */
public final class QuyTrinhManHinh {
    /**
     * Cung cấp giao diện và xử lý tương tác cho quy trình.
     *
     * @param ten tên
     * @param cacBuoc các bước
     * @param cacNhan các nhãn
     * @param tuanTu tuần tự
     */
    public record QuyTrinh(
            String ten, List<MaManHinh> cacBuoc, List<String> cacNhan, boolean tuanTu) {
        public int layChiSoCua(MaManHinh id) {
            if (id == MaManHinh.QUAN_LY_SPA) id = MaManHinh.TIEP_NHAN_SPA;
            return cacBuoc.indexOf(id);
        }

        public Optional<MaManHinh> layTiepTheo(MaManHinh id) {
            // Chỉ số.
            int chiSo = layChiSoCua(id);
            return chiSo >= 0 && chiSo + 1 < cacBuoc.size()
                    ? Optional.of(cacBuoc.get(chiSo + 1))
                    : Optional.empty();
        }

        public Optional<MaManHinh> layTruocDo(MaManHinh id) {
            // Chỉ số.
            int chiSo = layChiSoCua(id);
            return chiSo > 0 ? Optional.of(cacBuoc.get(chiSo - 1)) : Optional.empty();
        }

        public boolean kiemTraCuoiCung(MaManHinh id) {
            return layChiSoCua(id) == cacBuoc.size() - 1;
        }
    }

    /** Các quy trình. */
    private static final List<QuyTrinh> CAC_QUY_TRINH =
            List.of(
                    new QuyTrinh(
                            "Bán hàng",
                            List.of(MaManHinh.LAP_HOA_DON, MaManHinh.THANH_TOAN, MaManHinh.IN_HOA_DON),
                            List.of("Lập hóa đơn", "Thanh toán", "Xem hóa đơn"),
                            false),
                    new QuyTrinh(
                            "Lập phiếu Spa",
                            List.of(
                                    MaManHinh.DAT_LICH_HEN_SPA,
                                    MaManHinh.TIEP_NHAN_SPA,
                                    MaManHinh.CHON_GOI_SPA,
                                    MaManHinh.CAP_NHAT_TIEN_DO_SPA,
                                    MaManHinh.HOAN_TAT_SPA),
                            List.of(
                                    "Lịch hẹn",
                                    "Tiếp nhận",
                                    "Chọn dịch vụ",
                                    "Tiến độ",
                                    "Đóng phiếu"),
                            true),
                    new QuyTrinh(
                            "Quản lý lưu trú",
                            List.of(
                                    MaManHinh.DAT_PHONG_LUU_TRU,
                                    MaManHinh.NHAT_KY_CHAM_SOC,
                                    MaManHinh.TRA_PHONG_LUU_TRU),
                            List.of("Đặt phòng", "Nhật ký lưu trú", "Trả phòng"),
                            true));

    private QuyTrinhManHinh() {}

    public static Optional<QuyTrinh> tim(MaManHinh id) {
        return CAC_QUY_TRINH.stream()
                .filter(truongDangXet -> truongDangXet.layChiSoCua(id) >= 0)
                .findFirst();
    }
}
