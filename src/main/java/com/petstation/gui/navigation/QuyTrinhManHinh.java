package com.petstation.gui.navigation;

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
        /**
         * Lấy chỉ số của trong QuyTrinh.
         *
         * @param id id
         * @return kết quả lấy chỉ số của
         */
        public int layChiSoCua(MaManHinh id) {
            return cacBuoc.indexOf(id);
        }

        /**
         * Lấy tiếp theo trong QuyTrinh.
         *
         * @param id id
         * @return kết quả lấy tiếp theo
         */
        public Optional<MaManHinh> layTiepTheo(MaManHinh id) {
            // Chỉ số.
            int chiSo = layChiSoCua(id);
            return chiSo >= 0 && chiSo + 1 < cacBuoc.size()
                    ? Optional.of(cacBuoc.get(chiSo + 1))
                    : Optional.empty();
        }

        /**
         * Lấy trước đó trong QuyTrinh.
         *
         * @param id id
         * @return kết quả lấy trước đó
         */
        public Optional<MaManHinh> layTruocDo(MaManHinh id) {
            // Chỉ số.
            int chiSo = layChiSoCua(id);
            return chiSo > 0 ? Optional.of(cacBuoc.get(chiSo - 1)) : Optional.empty();
        }

        /**
         * Kiểm tra cuối cùng trong QuyTrinh.
         *
         * @param id id
         * @return kết quả kiểm tra điều kiện xử lý
         */
        public boolean kiemTraCuoiCung(MaManHinh id) {
            return layChiSoCua(id) == cacBuoc.size() - 1;
        }
    }

    /** Các quy trình. */
    private static final List<QuyTrinh> CAC_QUY_TRINH =
            List.of(
                    new QuyTrinh(
                            "Bán hàng",
                            List.of(MaManHinh.POS, MaManHinh.PAYMENT, MaManHinh.PRINT_INVOICE),
                            List.of("Lập đơn", "Thanh toán", "In hóa đơn"),
                            false),
                    new QuyTrinh(
                            "Dịch vụ Spa",
                            List.of(
                                    MaManHinh.SPA_SCHEDULE,
                                    MaManHinh.SPA_INTAKE,
                                    MaManHinh.SPA_SERVICE_SELECTION,
                                    MaManHinh.SPA_PROGRESS,
                                    MaManHinh.SPA_CLOSE),
                            List.of(
                                    "Lịch hẹn",
                                    "Tiếp nhận",
                                    "Chọn dịch vụ",
                                    "Tiến độ",
                                    "Đóng phiếu"),
                            true),
                    new QuyTrinh(
                            "Pet Hotel",
                            List.of(
                                    MaManHinh.HOTEL_BOOKING,
                                    MaManHinh.HOTEL_JOURNAL,
                                    MaManHinh.CHECKOUT),
                            List.of("Đặt phòng", "Nhật ký lưu trú", "Trả phòng"),
                            true));

    /** Khởi tạo QuyTrinhManHinh với các giá trị được cung cấp. */
    private QuyTrinhManHinh() {}

    /**
     * Tìm trong QuyTrinhManHinh.
     *
     * @param id id
     * @return kết quả tìm
     */
    public static Optional<QuyTrinh> tim(MaManHinh id) {
        return CAC_QUY_TRINH.stream()
                .filter(truongDangXet -> truongDangXet.layChiSoCua(id) >= 0)
                .findFirst();
    }
}
