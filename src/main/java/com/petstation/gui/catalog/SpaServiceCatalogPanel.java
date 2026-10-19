package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.gui.components.common.PriceTableEditor;
import com.petstation.service.CatalogService;
import com.petstation.util.Money;

import java.util.List;

public final class SpaServiceCatalogPanel extends EntityPanel {
    public SpaServiceCatalogPanel() {
        super(config(), fields(), CatalogService.spaServices());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục dịch vụ Spa", "dịch vụ Spa")
                        .prefix("DV")
                        .search("Tìm theo mã, tên dịch vụ...")
                        .status(5, "Đang hoạt động", "Ngừng hoạt động")
                        .validator(
                                r ->
                                        PriceTableEditor.minPrice(String.valueOf(r[4])) < 0
                                                ? "Bảng giá cần ít nhất 1 dòng có giá."
                                                : null);
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã DV", Type.TEXT).ro(),
                Field.of("Tên dịch vụ", Type.TEXT).req().unique(),
                Field.of("Mô tả", Type.AREA).hide(),
                Field.of("Thời gian dự kiến (phút)", Type.INT).req(),
                Field.of("Bảng giá theo loài / cân nặng", Type.CUSTOM)
                        .editor(PriceTableEditor::new)
                        .show(
                                v -> {
                                    long m = PriceTableEditor.minPrice(v);
                                    return m < 0 ? "—" : "Từ " + Money.format(m);
                                }),
                Field.of("Trạng thái", Type.COMBO).opts("Đang hoạt động", "Ngừng hoạt động"));
    }
}
