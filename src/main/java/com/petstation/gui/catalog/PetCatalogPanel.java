package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;

import java.util.List;

public final class PetCatalogPanel extends EntityPanel {
    public PetCatalogPanel() {
        super(config(), fields(), CatalogService.pets());
    }

    private static Config config() {
        Config cfg =
                Config.of("Hồ sơ thú cưng", "thú cưng")
                        .prefix("TC")
                        .search("Tìm theo tên thú, giống, chủ nuôi...")
                        .status(13, "Đang hoạt động", "Ngừng hoạt động")
                        .validator(
                                r -> {
                                    String w = String.valueOf(r[6]).trim();
                                    return w.isEmpty() || w.matches("\\d{1,3}([.,]\\d{1,2})?")
                                            ? null
                                            : "Cân nặng phải là số (kg), ví dụ 5.2.";
                                });
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã thú", Type.TEXT).ro(),
                Field.of("Tên thú cưng", Type.TEXT).req(),
                Field.of("Loài", Type.COMBO).req().opts("Chó", "Mèo"),
                Field.of("Giống", Type.TEXT).hide(),
                Field.of("Giới tính", Type.COMBO).opts("Đực", "Cái").hide(),
                Field.of("Ngày sinh", Type.DATE).hide(),
                Field.of("Cân nặng (kg)", Type.TEXT).hide(),
                Field.of("Màu lông", Type.TEXT).hide(),
                Field.of("Chủ nuôi", Type.COMBO).req().dyn(CatalogService::customerOptions),
                Field.of("Tiêm chủng", Type.AREA).hide(),
                Field.of("Dị ứng / lưu ý sức khỏe", Type.AREA).hide(),
                Field.of("Tính cách", Type.AREA).hide(),
                Field.of("Ảnh", Type.FILE).hide(),
                Field.of("Trạng thái", Type.COMBO).opts("Đang hoạt động", "Ngừng hoạt động"));
    }
}
