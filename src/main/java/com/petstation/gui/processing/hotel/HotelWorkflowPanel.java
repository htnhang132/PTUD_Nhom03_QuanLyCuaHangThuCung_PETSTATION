package com.petstation.gui.processing.hotel;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.gui.components.WorkflowFields.*;

import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.components.common.SqlForm;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.HotelService;
import com.petstation.service.SalesService;
import com.petstation.service.WorkflowLookupService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public abstract class HotelWorkflowPanel extends WorkflowTablePanel {
    protected HotelWorkflowPanel(WorkflowType type, SaleContext sale, Consumer<ScreenId> navigate) {
        super(type, sale, navigate);
    }

    protected void book() {
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Đặt phòng Pet Hotel",
                        pets(),
                        options("room", "Phòng", WorkflowLookupService.optionsRoom()),
                        textField("start", "Ngày vào (yyyy-MM-dd HH:mm)", now()),
                        textField(
                                "end",
                                "Ngày ra (yyyy-MM-dd HH:mm)",
                                LocalDateTime.now().plusDays(1).withSecond(0).format(DATE_TIME)),
                        textField("deposit", "Tiền cọc (đồng)", "0"),
                        textField("note", "Ghi chú", ""));
        if (v != null)
            HotelService.book(
                    v.get("pet"),
                    v.get("room"),
                    time(v, "start"),
                    time(v, "end"),
                    amount(v, "deposit"),
                    v.get("note"));
    }

    protected void journal() {
        long booking = selected();
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Nhật ký chăm sóc",
                        textField("food", "Ăn uống", ""),
                        textField("hygiene", "Vệ sinh", ""),
                        textField("health", "Tình trạng thú cưng", ""));
        if (v != null)
            HotelService.journal(booking, v.get("food"), v.get("hygiene"), v.get("health"));
    }

    protected void journalDetails() {
        details(
                "Nhật ký lưu trú",
                new String[] {"Thời gian", "Nhân viên", "Ăn uống", "Vệ sinh", "Tình trạng"},
                HotelService.journalDetails(selected()));
    }

    protected void checkout(String kind) {
        if (!sale.isEmpty())
            throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
        SalesService.checkout(kind, selected(), sale);
        navigate.accept(ScreenId.PAYMENT);
    }
}
