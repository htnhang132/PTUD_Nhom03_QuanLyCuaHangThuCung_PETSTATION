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

public final class GoodsWriteOffPanel extends InventoryWorkflowPanel {
    public GoodsWriteOffPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.WRITE_OFF, sale, navigate);
        button(actions, "+ Xuất kho / Hủy", this::writeOff);
    }
}
