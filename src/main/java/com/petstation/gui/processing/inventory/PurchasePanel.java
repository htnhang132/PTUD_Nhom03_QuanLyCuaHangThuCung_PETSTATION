package com.petstation.gui.processing.inventory;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class PurchasePanel extends InventoryWorkflowPanel {
    public PurchasePanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.PURCHASE, sale, navigate);

        button(actions, "+ Nhập kho", this::purchase);
        button(actions, "Chi tiết", this::purchaseDetails);
        button(actions, "Trả công nợ", this::paySupplier);
    }
}
