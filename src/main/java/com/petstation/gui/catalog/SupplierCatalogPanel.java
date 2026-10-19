package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;

import java.util.List;

public final class SupplierCatalogPanel extends EntityPanel {
    public SupplierCatalogPanel() {
        super(config(), fields(), CatalogService.suppliers());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục nhà cung cấp", "nhà cung cấp")
                        .prefix("NCC")
                        .search("Tìm theo tên, người liên hệ, MST...")
                        .status(7, "Đang hợp tác", "Ngừng hợp tác");
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã NCC", Type.TEXT).ro(),
                Field.of("Tên nhà cung cấp", Type.TEXT).req(),
                Field.of("Người liên hệ", Type.TEXT),
                Field.of("Số điện thoại", Type.PHONE).req(),
                Field.of("Địa chỉ", Type.TEXT),
                Field.of("Mã số thuế", Type.TEXT).unique(),
                Field.of("Điều khoản thanh toán", Type.COMBO)
                        .opts("Trả ngay", "15 ngày", "30 ngày", "45 ngày"),
                Field.of("Trạng thái", Type.COMBO).opts("Đang hợp tác", "Ngừng hợp tác"));
    }
}
