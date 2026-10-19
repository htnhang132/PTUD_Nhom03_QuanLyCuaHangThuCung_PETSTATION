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

public final class StocktakePanel extends InventoryWorkflowPanel {
    public StocktakePanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.STOCKTAKE, sale, navigate);
        button(actions, "+ Kiểm kê lô", this::stocktake);
    }
}
