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

public final class VoucherCatalogPanel extends EntityPanel {
    public VoucherCatalogPanel() {
        super(config(), fields(), CatalogService.vouchers());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục voucher", "voucher")
                        .search("Tìm theo mã voucher...")
                        .status(6, "Còn hiệu lực", "Ngừng")
                        .normalizer(
                                r -> {
                                    LocalDate exp = date(r[2]);
                                    String status = String.valueOf(r[6]);
                                    long qty = Money.parse(String.valueOf(r[3]));
                                    long used = Money.parse(String.valueOf(r[4]));
                                    if ("Ngừng".equals(status)) return;
                                    if (exp != null && exp.isBefore(LocalDate.now()))
                                        r[6] = "Hết hạn";
                                    else if (qty >= 0 && used >= qty) r[6] = "Hết lượt";
                                    else r[6] = "Còn hiệu lực";
                                })
                        .validator(
                                r ->
                                        Money.parse(String.valueOf(r[3]))
                                                        < Money.parse(String.valueOf(r[4]))
                                                ? "Số lượng không được nhỏ hơn số đã dùng."
                                                : null);
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã voucher", Type.TEXT).req().unique(),
                Field.of("Giá trị (₫)", Type.MONEY).req(),
                Field.of("Hạn dùng", Type.DATE).req(),
                Field.of("Số lượng", Type.INT).req(),
                Field.of("Đã dùng", Type.INT).ro().def("0"),
                Field.of("Đơn tối thiểu", Type.MONEY).def("0"),
                Field.of("Trạng thái", Type.COMBO)
                        .opts("Còn hiệu lực", "Hết hạn", "Hết lượt", "Ngừng"));
    }
}
