package com.petstation.gui.screens;

import java.awt.Component;
import java.util.List;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.FormDialog;
import com.petstation.gui.components.Money;
import com.petstation.gui.data.ApprovalStore;
import com.petstation.gui.navigation.AppSettings;
import com.petstation.gui.navigation.AuditTrail;

/** Các luồng nhập nhanh có quy tắc duyệt: Xuất hủy, Kiểm kê, Nghỉ phép. Vượt ngưỡng hoặc có chênh lệch thì gửi Phê duyệt. */
public final class QuickFlows {
    private QuickFlows() {}

    public static void writeOff(Component parent) {
        List<String> v = FormDialog.ask(parent, "Xuất hủy",
            new String[]{"Sản phẩm / lô *", "Số lượng *", "Giá trị (₫) *", "Lý do *"},
            new String[]{"", "1", "", "Hết hạn"});
        if (v == null) return;
        long qty = Money.parse(v.get(1));
        long value = Money.parse(v.get(2));
        if (v.get(0).isBlank() || v.get(3).isBlank() || qty <= 0 || value < 0) {
            Dialogs.warn(parent, "Nhập đủ sản phẩm/lô, số lượng lớn hơn 0, giá trị và lý do.");
            return;
        }
        String what = v.get(0) + " x" + qty + " (" + Money.format(value) + "), lý do: " + v.get(3);
        if (value >= AppSettings.writeOffApprovalLimit()) {
            ApprovalStore.submit(ApprovalStore.Kind.WRITE_OFF, AuditTrail.user(), v.get(3),
                "Tồn kho còn " + v.get(0), "Xuất hủy " + qty + " (" + Money.format(value) + ")",
                () -> AuditTrail.log("Xuất hủy", what + " (đã duyệt)"));
            Dialogs.info(parent, "Giá trị từ " + Money.format(AppSettings.writeOffApprovalLimit()) + " nên cần Cửa hàng trưởng duyệt.\nĐã gửi yêu cầu sang Phê duyệt.");
        } else {
            AuditTrail.log("Xuất hủy", what);
            Dialogs.success(parent, "Đã ghi nhận xuất hủy: " + what);
        }
    }

    public static void stocktake(Component parent) {
        List<String> v = FormDialog.ask(parent, "Kiểm kê",
            new String[]{"Sản phẩm / lô *", "Tồn sổ sách *", "Tồn thực tế *", "Lý do chênh lệch"},
            new String[]{"", "", "", ""});
        if (v == null) return;
        long book = Money.parse(v.get(1));
        long real = Money.parse(v.get(2));
        if (v.get(0).isBlank() || book < 0 || real < 0) {
            Dialogs.warn(parent, "Nhập đủ sản phẩm/lô, tồn sổ sách và tồn thực tế (số không âm).");
            return;
        }
        long diff = real - book;
        if (diff == 0) {
            AuditTrail.log("Kiểm kê", v.get(0) + ": khớp " + real);
            Dialogs.success(parent, "Tồn thực tế khớp sổ sách, không cần điều chỉnh.");
            return;
        }
        if (v.get(3).isBlank()) { Dialogs.warn(parent, "Có chênh lệch " + diff + ": cần ghi lý do."); return; }
        ApprovalStore.submit(ApprovalStore.Kind.STOCKTAKE, AuditTrail.user(), v.get(3),
            v.get(0) + ": tồn sổ sách " + book, v.get(0) + ": tồn thực tế " + real + " (chênh " + (diff > 0 ? "+" : "") + diff + ")",
            () -> AuditTrail.log("Kiểm kê", "Điều chỉnh tồn " + v.get(0) + " từ " + book + " về " + real));
        Dialogs.info(parent, "Có chênh lệch " + (diff > 0 ? "+" : "") + diff + ". Đã gửi duyệt; được duyệt mới điều chỉnh tồn.");
    }

    public static void leave(Component parent) {
        List<String> v = FormDialog.ask(parent, "Đăng ký nghỉ phép",
            new String[]{"Ngày nghỉ (dd/MM/yyyy) *", "Lý do *"}, new String[]{"", ""});
        if (v == null) return;
        boolean validDate;
        try {
            java.time.LocalDate.parse(v.get(0), java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT));
            validDate = true;
        } catch (java.time.format.DateTimeParseException ex) {
            validDate = false;
        }
        if (!validDate || v.get(1).isBlank()) {
            Dialogs.warn(parent, "Nhập ngày theo dạng dd/MM/yyyy và lý do.");
            return;
        }
        ApprovalStore.submit(ApprovalStore.Kind.LEAVE, AuditTrail.user(), v.get(1),
            "Ca làm việc ngày " + v.get(0), "Nghỉ phép ngày " + v.get(0),
            () -> {
                AuditTrail.log("Duyệt", "Nghỉ phép " + v.get(0) + " của " + AuditTrail.user());
                for (Object[] st : com.petstation.gui.data.DemoStore.staff()) {
                    if (String.valueOf(st[1]).equalsIgnoreCase(AuditTrail.user())) {
                        com.petstation.gui.data.ScheduleStore.markLeave(String.valueOf(st[0]),
                            java.time.LocalDate.parse(v.get(0), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    }
                }
            });
        Dialogs.success(parent, "Đã gửi yêu cầu nghỉ phép sang Phê duyệt.");
    }
}
