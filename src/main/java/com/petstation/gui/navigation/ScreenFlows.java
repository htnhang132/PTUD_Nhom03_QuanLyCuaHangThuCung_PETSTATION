package com.petstation.gui.navigation;

import java.util.List;
import java.util.Optional;

/** Các quy trình nhiều bước; thanh luồng ở khung chính hiển thị bước hiện tại. */
public final class ScreenFlows {
    public record Flow(String name, List<ScreenId> steps, List<String> labels, boolean linear) {
        public int indexOf(ScreenId id) {
            return steps.indexOf(id);
        }

        public Optional<ScreenId> next(ScreenId id) {
            int i = indexOf(id);
            return i >= 0 && i + 1 < steps.size()
                    ? Optional.of(steps.get(i + 1))
                    : Optional.empty();
        }

        public Optional<ScreenId> previous(ScreenId id) {
            int i = indexOf(id);
            return i > 0 ? Optional.of(steps.get(i - 1)) : Optional.empty();
        }

        public boolean isLast(ScreenId id) {
            return indexOf(id) == steps.size() - 1;
        }
    }

    private static final List<Flow> FLOWS =
            List.of(
                    new Flow(
                            "Bán hàng",
                            List.of(ScreenId.POS, ScreenId.PAYMENT, ScreenId.PRINT_INVOICE),
                            List.of("Lập đơn", "Thanh toán", "In hóa đơn"),
                            false),
                    new Flow(
                            "Dịch vụ Spa",
                            List.of(
                                    ScreenId.SPA_SCHEDULE,
                                    ScreenId.SPA_INTAKE,
                                    ScreenId.SPA_SERVICE_SELECTION,
                                    ScreenId.SPA_PROGRESS,
                                    ScreenId.SPA_CLOSE),
                            List.of(
                                    "Lịch hẹn",
                                    "Tiếp nhận",
                                    "Chọn dịch vụ",
                                    "Tiến độ",
                                    "Đóng phiếu"),
                            true),
                    new Flow(
                            "Pet Hotel",
                            List.of(
                                    ScreenId.HOTEL_BOOKING,
                                    ScreenId.HOTEL_JOURNAL,
                                    ScreenId.CHECKOUT),
                            List.of("Đặt phòng", "Nhật ký lưu trú", "Trả phòng"),
                            true));

    private ScreenFlows() {}

    public static Optional<Flow> find(ScreenId id) {
        return FLOWS.stream().filter(f -> f.indexOf(id) >= 0).findFirst();
    }
}
