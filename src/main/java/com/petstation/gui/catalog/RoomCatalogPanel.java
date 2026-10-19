package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;

import java.util.List;

public final class RoomCatalogPanel extends EntityPanel {
    public RoomCatalogPanel() {
        super(config(), fields(), CatalogService.rooms());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục phòng Pet Hotel", "phòng")
                        .search("Tìm theo mã phòng, loại phòng...")
                        .status(5, "Trống", "Ngừng hoạt động")
                        .deleteGuard(
                                r -> {
                                    String st = String.valueOf(r[5]);
                                    return "Có thú".equals(st) || "Đang dọn".equals(st)
                                            ? "Phòng "
                                                    + r[0]
                                                    + " đang \""
                                                    + st
                                                    + "\", chưa thể ngừng hoạt động."
                                            : null;
                                });
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã phòng", Type.TEXT).req().unique(),
                Field.of("Loại phòng", Type.COMBO).req().opts("Tiêu chuẩn", "VIP", "Mèo"),
                Field.of("Sức chứa (thú)", Type.INT).req().def("1"),
                Field.of("Giá / ngày", Type.MONEY).req(),
                Field.of("Ghi chú", Type.TEXT),
                Field.of("Trạng thái (chỉ chỉnh tay Trống / Bảo trì)", Type.COMBO)
                        .opts("Trống", "Bảo trì")
                        .editableWhen(
                                v ->
                                        v.equals("Trống")
                                                || v.equals("Bảo trì")
                                                || v.equals("Ngừng hoạt động")));
    }
}
