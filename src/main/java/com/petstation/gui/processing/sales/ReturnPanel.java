package com.petstation.gui.processing.sales;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class ReturnPanel extends SalesWorkflowPanel {
    public ReturnPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.RETURNS, sale, navigate);
        button(actions, "+ Lập phiếu trả", this::returnItem);
    }
}
