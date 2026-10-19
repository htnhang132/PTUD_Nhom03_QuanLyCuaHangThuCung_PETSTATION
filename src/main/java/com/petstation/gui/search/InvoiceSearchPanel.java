package com.petstation.gui.search;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.processing.sales.SalesWorkflowPanel;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.ApprovalService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class InvoiceSearchPanel extends SalesWorkflowPanel {
    public InvoiceSearchPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.INVOICES, sale, navigate);

        button(actions, "Xem / In lại", this::showInvoice);
        button(
                actions,
                "Yêu cầu sửa / hủy",
                () -> {
                    ApprovalService.selectedInvoice = selectedText(1);
                    navigate.accept(ScreenId.INVOICE_EDIT_REQUEST);
                });
    }
}
