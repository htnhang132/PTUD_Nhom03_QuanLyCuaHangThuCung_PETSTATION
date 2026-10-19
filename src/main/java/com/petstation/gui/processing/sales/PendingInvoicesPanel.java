package com.petstation.gui.processing.sales;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.SalesService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class PendingInvoicesPanel extends WorkflowTablePanel {
    public PendingInvoicesPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.PENDING, sale, navigate);

        button(
                actions,
                "Mở lại đơn",
                () -> {
                    if (!sale.isEmpty())
                        throw new IllegalArgumentException(
                                "Hãy treo hoặc thanh toán giỏ hiện tại trước.");
                    SalesService.resume(selected(), sale);
                    navigate.accept(ScreenId.POS);
                });
        button(
                actions,
                "Xóa đơn treo",
                () -> {
                    if (Dialogs.confirm(this, "Xóa đơn treo đã chọn?"))
                        SalesService.deleteHold(selected());
                });
    }
}
