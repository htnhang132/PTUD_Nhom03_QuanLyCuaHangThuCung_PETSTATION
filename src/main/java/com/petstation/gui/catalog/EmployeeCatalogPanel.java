package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;

import java.util.List;

public final class EmployeeCatalogPanel extends EntityPanel {
    public EmployeeCatalogPanel() {
        super(config(), fields(), CatalogService.staff());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục nhân viên", "nhân viên")
                        .prefix("NV")
                        .search("Tìm theo tên, SĐT, mã NV...")
                        .status(6, "Đang làm", "Nghỉ việc");
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã NV", Type.TEXT).ro(),
                Field.of("Họ và tên", Type.TEXT).req(),
                Field.of("Số điện thoại", Type.PHONE).req().unique(),
                Field.of("CCCD", Type.CCCD).req().unique(),
                Field.of("Chức vụ", Type.COMBO).req().opts(roleNames()),
                Field.of("Ngày vào làm", Type.DATE).req(),
                Field.of("Trạng thái", Type.COMBO).opts("Đang làm", "Nghỉ việc"));
    }
}
