package com.petstation.gui.processing.spa;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class SpaSchedulePanel extends SpaWorkflowPanel {
    public SpaSchedulePanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.APPOINTMENTS, sale, navigate);

        button(actions, "+ Đặt lịch hẹn", this::appointment);
        button(actions, "Tiếp nhận", this::intake);
        button(actions, "Hủy lịch", this::cancelAppointment);
    }
}
