package com.petstation.gui.catalog;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.CatalogService;

import java.util.List;

public final class ProductListPanel extends EntityPanel {
    public ProductListPanel() {
        super(config(), fields(), CatalogService.products());
    }

    private static Config config() {
        Config cfg =
                Config.of("Danh mục sản phẩm", "sản phẩm")
                        .prefix("SP")
                        .search("Tìm theo mã, tên sản phẩm...")
                        .status(9, "Đang kinh doanh", "Ngừng kinh doanh");
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Mã SP", Type.TEXT).ro(),
                Field.of("Tên sản phẩm", Type.TEXT).req().unique(),
                Field.of("Nhóm hàng", Type.COMBO)
                        .req()
                        .opts("Thức ăn", "Phụ kiện", "Grooming", "Vệ sinh", "Thuốc"),
                Field.of("Đơn vị tính", Type.TEXT).req(),
                Field.of("Giá bán", Type.MONEY).req(),
                Field.of("Giá nhập tham khảo", Type.MONEY).hide(),
                Field.of("Tồn tối thiểu", Type.INT).req().hide().def("5"),
                Field.of("Quản lý lô / HSD", Type.COMBO).opts("Có", "Không").hide(),
                Field.of("Tồn hiện tại (chỉ đổi qua Nhập, Xuất, Kiểm kê)", Type.INT).ro().def("0"),
                Field.of("Trạng thái", Type.COMBO).opts("Đang kinh doanh", "Ngừng kinh doanh"));
    }
}
