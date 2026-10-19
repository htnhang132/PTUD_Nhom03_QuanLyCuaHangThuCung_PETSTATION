package com.petstation.gui.system;

import static com.petstation.gui.components.CatalogFields.*;

import com.petstation.gui.components.EntityPanel;
import com.petstation.gui.components.EntityPanel.Config;
import com.petstation.gui.components.EntityPanel.Field;
import com.petstation.gui.components.EntityPanel.Type;
import com.petstation.service.AuditService;
import com.petstation.service.CatalogService;

import java.util.List;

public final class AccountListPanel extends EntityPanel {
    public AccountListPanel() {
        super(config(), fields(), CatalogService.accounts());
    }

    private static Config config() {
        Config cfg =
                Config.of("Tài khoản", "tài khoản")
                        .search("Tìm theo tên đăng nhập, nhân viên...")
                        .status(3, "Hoạt động", "Đã khóa")
                        .extra(
                                "Khóa / Mở khóa",
                                r -> {
                                    boolean locked = "Đã khóa".equals(r[3]);
                                    if ("admin".equals(r[0]) && !locked)
                                        return "Không thể khóa tài khoản admin.";
                                    r[3] = locked ? "Hoạt động" : "Đã khóa";
                                    AuditService.log(
                                            locked ? "Mở khóa" : "Khóa", "Tài khoản " + r[0]);
                                    return (locked ? "Đã mở khóa " : "Đã khóa ") + r[0] + ".";
                                })
                        .extra(
                                "Đặt lại mật khẩu",
                                r -> {
                                    String temp =
                                            "PS-"
                                                    + java.util
                                                            .UUID
                                                            .randomUUID()
                                                            .toString()
                                                            .substring(0, 12);
                                    CatalogService.resetPassword(String.valueOf(r[0]), temp);
                                    AuditService.log("Đặt lại mật khẩu", "Tài khoản " + r[0]);
                                    com.petstation.gui.components.Dialogs.info(
                                            null,
                                            "Mật khẩu tạm của "
                                                    + r[0]
                                                    + ": "
                                                    + temp
                                                    + "\n"
                                                    + "Tài khoản phải đổi mật khẩu ở lần đăng nhập"
                                                    + " kế tiếp.");
                                    return "Đã đặt lại mật khẩu cho " + r[0] + ".";
                                });
        return cfg;
    }

    private static List<Field> fields() {
        return List.of(
                Field.of("Tên đăng nhập", Type.TEXT).req().unique(),
                Field.of("Nhân viên", Type.COMBO).req().unique().dyn(CatalogService::staffOptions),
                Field.of("Vai trò", Type.COMBO).req().opts(roleNames()),
                Field.of("Trạng thái", Type.COMBO).opts("Hoạt động", "Đã khóa"),
                Field.of("Mật khẩu mới (bắt buộc khi thêm tài khoản)", Type.PASSWORD).hide());
    }
}
