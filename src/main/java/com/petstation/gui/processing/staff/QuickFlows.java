package com.petstation.gui.processing.staff;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.common.SqlForm;
import com.petstation.service.ApprovalService;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Leave requests store the employee and date, independent of the current approver/session. */
public final class QuickFlows {
    private QuickFlows() {}

    public static void leave(Component parent) {
        var values =
                SqlForm.ask(
                        parent,
                        "Đăng ký nghỉ phép",
                        SqlForm.Field.text(
                                "date",
                                "Ngày nghỉ (dd/MM/yyyy)",
                                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        SqlForm.Field.text("reason", "Lý do", ""));
        if (values == null) return;
        try {
            LocalDate date =
                    LocalDate.parse(
                            values.get("date"),
                            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                    .withResolverStyle(java.time.format.ResolverStyle.STRICT));
            ApprovalService.submitAction(
                    ApprovalService.Kind.LEAVE,
                    values.get("reason"),
                    "Lịch làm việc ngày " + date,
                    "Nghỉ phép",
                    "LEAVE",
                    employeeId(),
                    date.toString());
            Dialogs.success(parent, "Đã gửi yêu cầu nghỉ phép.");
        } catch (RuntimeException exception) {
            Dialogs.warn(parent, exception.getMessage());
        }
    }
}
