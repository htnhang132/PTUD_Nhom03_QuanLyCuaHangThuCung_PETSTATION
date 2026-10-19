package com.petstation.gui.processing.sales;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.gui.components.WorkflowFields.*;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.components.common.SqlForm;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.ReturnService;
import com.petstation.service.SalesService;
import com.petstation.service.WorkflowLookupService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public abstract class SalesWorkflowPanel extends WorkflowTablePanel {
    protected SalesWorkflowPanel(WorkflowType type, SaleContext sale, Consumer<ScreenId> navigate) {
        super(type, sale, navigate);
    }

    protected void returnItem() {
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Lập phiếu trả hàng",
                        options("item", "Hàng trên hóa đơn", WorkflowLookupService.optionsItem()),
                        textField("qty", "Số lượng trả", "1"),
                        textField("reason", "Lý do", ""));
        if (v != null) {
            ReturnService.returnItem(amount(v, "item"), qty(v, "qty"), v.get("reason"));
            Dialogs.success(
                    this, "Đã lưu phiếu trả. Phiếu vượt hạn mức hoặc thời hạn sẽ chờ duyệt.");
        }
    }

    protected void showInvoice() {
        sale.setLastReceipt(SalesService.receipt(selectedText(1)));
        navigate.accept(ScreenId.PRINT_INVOICE);
    }

    protected void checkout(String kind) {
        if (!sale.isEmpty())
            throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
        SalesService.checkout(kind, selected(), sale);
        navigate.accept(ScreenId.PAYMENT);
    }
}
