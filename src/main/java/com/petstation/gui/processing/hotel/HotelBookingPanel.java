package com.petstation.gui.processing.hotel;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.HotelService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class HotelBookingPanel extends HotelWorkflowPanel {
    public HotelBookingPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.HOTEL_BOOKING, sale, navigate);

        button(actions, "+ Đặt phòng", this::book);
        button(actions, "Nhận phòng", () -> HotelService.checkIn(selected()));
        button(actions, "Ghi chăm sóc", this::journal);
        button(actions, "Xem nhật ký", this::journalDetails);
        button(actions, "Quyết toán", () -> checkout("HOTEL"));
    }
}
