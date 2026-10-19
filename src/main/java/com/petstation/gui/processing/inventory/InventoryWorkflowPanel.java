package com.petstation.gui.processing.inventory;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.gui.components.WorkflowFields.*;
import static com.petstation.service.SessionService.*;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.components.common.SqlForm;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.InventoryService;
import com.petstation.service.WorkflowLookupService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public abstract class InventoryWorkflowPanel extends WorkflowTablePanel {
    protected InventoryWorkflowPanel(
            WorkflowType type, SaleContext sale, Consumer<ScreenId> navigate) {
        super(type, sale, navigate);
    }

    protected void purchase() {
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Nhập kho",
                        options(
                                "supplier",
                                "Nhà cung cấp",
                                WorkflowLookupService.optionsSupplier()),
                        options("product", "Sản phẩm", WorkflowLookupService.optionsProduct()),
                        textField("batch", "Số lô", ""),
                        textField("expiry", "Hạn sử dụng (yyyy-MM-dd, có thể trống)", ""),
                        textField("qty", "Số lượng", "1"),
                        textField("cost", "Giá nhập / đơn vị (đồng)", ""),
                        textField("paid", "Đã trả (đồng)", "0"));
        if (v != null)
            InventoryService.purchase(
                    v.get("supplier"),
                    v.get("product"),
                    v.get("batch"),
                    v.get("expiry").isBlank() ? null : LocalDate.parse(v.get("expiry")),
                    qty(v, "qty"),
                    amount(v, "cost"),
                    amount(v, "paid"));
    }

    protected void paySupplier() {
        require(com.petstation.model.Permission.INVENTORY_RECEIVE);
        long id = selected();
        Map<String, String> v =
                SqlForm.ask(this, "Trả công nợ", textField("paid", "Số tiền trả thêm (đồng)", ""));
        if (v == null) return;
        InventoryService.paySupplier(id, amount(v, "paid"));
    }

    protected void writeOff() {
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Xuất kho / Hủy hàng",
                        batches(),
                        textField("qty", "Số lượng", "1"),
                        textField("reason", "Lý do", ""));
        if (v != null) {
            InventoryService.writeOff(amount(v, "batch"), qty(v, "qty"), v.get("reason"));
            Dialogs.success(this, "Đã lưu phiếu. Phiếu vượt ngưỡng sẽ chờ phê duyệt.");
        }
    }

    protected void stocktake() {
        Map<String, String> v =
                SqlForm.ask(
                        this,
                        "Kiểm kê",
                        batches(),
                        textField("actual", "Tồn thực tế", ""),
                        textField("reason", "Lý do chênh lệch", ""));
        if (v != null) {
            InventoryService.stocktake(amount(v, "batch"), qty(v, "actual"), v.get("reason"));
            Dialogs.success(this, "Đã lưu biên bản. Chênh lệch chỉ điều chỉnh sau phê duyệt.");
        }
    }

    protected void purchaseDetails() {
        details(
                "Chi tiết nhập kho",
                new String[] {"Sản phẩm", "Số lô", "Hạn dùng", "Số lượng", "Giá nhập"},
                InventoryService.purchaseDetails(selected()));
    }
}
