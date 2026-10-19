package com.petstation.gui.processing.spa;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.gui.components.WorkflowFields.*;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.components.common.SqlForm;
import com.petstation.gui.components.common.SqlForm.Field;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.SalesService;
import com.petstation.service.SpaService;
import com.petstation.service.WorkflowLookupService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

public abstract class SpaWorkflowPanel extends WorkflowTablePanel {
    protected SpaWorkflowPanel(WorkflowType type, SaleContext sale, Consumer<ScreenId> navigate) {
        super(type, sale, navigate);
    }

    protected void appointment() {
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Đặt lịch Spa",
                        pets(),
                        packages(),
                        options("staff", "Nhân viên", WorkflowLookupService.optionsStaff()),
                        textField("start", "Bắt đầu (yyyy-MM-dd HH:mm)", now()));
        if (v != null)
            SpaService.appointment(
                    v.get("pet"), v.get("package"), v.get("staff"), time(v, "start"));
    }

    protected void cancelAppointment() {
        if (Dialogs.confirm(this, "Hủy lịch hẹn đã chọn?"))
            SpaService.cancelAppointment(selected(), selectedText(1));
    }

    protected void intake() {
        String appointment = "", pet = "";
        if (workflowType == WorkflowType.APPOINTMENTS && table.getSelectedRow() >= 0) {
            appointment = selectedText(1);
            pet = text(SpaService.appointmentPetCode(selected()));
        }
        Field petField =
                pet.isEmpty()
                        ? pets()
                        : Field.choice(
                                "pet", "Thú cưng", List.<Object[]>of(new Object[] {pet, pet}));
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Tiếp nhận Spa",
                        petField,
                        textField("weight", "Cân nặng thực tế (kg)", ""),
                        textField("note", "Tình trạng / Lưu ý", ""));
        if (v != null)
            SpaService.intake(
                    v.get("pet"),
                    appointment,
                    Double.parseDouble(v.get("weight").replace(',', '.')),
                    v.get("note"));
    }

    protected void service() {
        long ticket = selected();
        Map<String, String> v = SqlForm.ask(this, "Thêm dịch vụ vào phiếu", packages());
        if (v != null) SpaService.addSpaService(ticket, v.get("package"));
    }

    protected void spaDetails() {
        details(
                "Chi tiết phiếu Spa",
                new String[] {"Dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"},
                SpaService.ticketDetails(selected()));
    }

    protected void checkout(String kind) {
        if (!sale.isEmpty())
            throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
        SalesService.checkout(kind, selected(), sale);
        navigate.accept(ScreenId.PAYMENT);
    }
}
