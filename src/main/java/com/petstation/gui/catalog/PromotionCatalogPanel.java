package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;
import com.petstation.util.Money;

import java.time.LocalDate;
import java.util.List;

public final class PromotionCatalogPanel extends EntityPanel {
    public PromotionCatalogPanel() {
        super(config(), fields(), CatalogService.promotions());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục khuyến mãi", "khuyến mãi")
                        .prefix("KM")
                        .search("Tìm theo mã, tên chương trình...")
                        .status(8, "Đang áp dụng", "Ngừng áp dụng")
                        .normalizer(
                                r -> {
                                    LocalDate today = LocalDate.now();
                                    LocalDate from = date(r[6]);
                                    LocalDate to = date(r[7]);
                                    String status = String.valueOf(r[8]);
                                    if (to != null && to.isBefore(today)) r[8] = "Ngừng áp dụng";
                                    else if (!"Ngừng áp dụng".equals(status))
                                        r[8] =
                                                from != null && from.isAfter(today)
                                                        ? "Chưa áp dụng"
                                                        : "Đang áp dụng";
                                })
                        .validator(
                                r -> {
                                    LocalDate from = date(r[6]);
                                    LocalDate to = date(r[7]);
                                    if (from != null && to != null && to.isBefore(from))
                                        return "Đến ngày phải sau hoặc bằng Từ ngày.";
                                    if ("Giảm %".equals(r[2])) {
                                        long v = Money.parse(String.valueOf(r[4]));
                                        if (v < 1 || v > 100)
                                            return "Giá trị giảm % phải từ 1 đến 100.";
                                    }
                                    return null;
                                });
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã KM", Type.TEXT).ro(),
                Field.of("Tên chương trình", Type.TEXT).req(),
                Field.of("Loại", Type.COMBO).opts("Giảm %", "Giảm tiền"),
                Field.of("Phạm vi", Type.COMBO).opts("Sản phẩm", "Dịch vụ", "Cả đơn"),
                Field.of("Giá trị (% hoặc ₫)", Type.INT).req().hide(),
                Field.of("Đơn tối thiểu", Type.MONEY).hide().def("0"),
                Field.of("Từ ngày", Type.DATE).req(),
                Field.of("Đến ngày", Type.DATE).req(),
                Field.of("Trạng thái", Type.COMBO)
                        .opts("Chưa áp dụng", "Đang áp dụng", "Ngừng áp dụng"));
    }
}
