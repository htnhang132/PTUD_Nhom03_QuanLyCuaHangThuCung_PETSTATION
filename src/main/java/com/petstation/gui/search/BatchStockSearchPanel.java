package com.petstation.gui.search;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class BatchStockSearchPanel extends WorkflowTablePanel {
    public BatchStockSearchPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.BATCHES, sale, navigate);
    }
}
