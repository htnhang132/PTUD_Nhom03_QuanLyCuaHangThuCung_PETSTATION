package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;

import java.util.List;

public final class CustomerCatalogPanel extends EntityPanel {
    public CustomerCatalogPanel() {
        super(config(), fields(), CatalogService.customers());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục khách hàng", "khách hàng")
                        .prefix("KH")
                        .search("Tìm theo tên, SĐT, mã khách...")
                        .status(8, "Đang hoạt động", "Ngừng hoạt động");
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã khách", Type.TEXT).ro(),
                Field.of("Họ và tên", Type.TEXT).req(),
                Field.of("Số điện thoại", Type.PHONE).req().unique(),
                Field.of("Email", Type.EMAIL).hide(),
                Field.of("Địa chỉ", Type.TEXT),
                Field.of("Ghi chú", Type.AREA).hide(),
                Field.of("Hạng", Type.TEXT).ro().def("Đồng"),
                Field.of("Điểm", Type.INT).ro().def("0"),
                Field.of("Trạng thái", Type.COMBO).opts("Đang hoạt động", "Ngừng hoạt động"));
    }
}
