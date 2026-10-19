package com.petstation.gui.search;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.processing.spa.SpaWorkflowPanel;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.SpaService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class SpaDailyPanel extends SpaWorkflowPanel {
    public SpaDailyPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.SPA_TICKETS, sale, navigate);

        button(actions, "+ Tiếp nhận", this::intake);
        button(actions, "Thêm dịch vụ", this::service);
        button(actions, "Bắt đầu làm", () -> SpaService.spaStatus(selected(), "IN_PROGRESS"));
        button(actions, "Hoàn thành", () -> SpaService.spaStatus(selected(), "COMPLETED"));
        button(actions, "Thanh toán", () -> checkout("SPA"));
        button(actions, "Chi tiết dịch vụ", this::spaDetails);
    }
}
