package com.petstation.gui.search;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.WorkflowTablePanel;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.HotelService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class HotelMapPanel extends WorkflowTablePanel {
    public HotelMapPanel(SaleContext sale, Consumer<ScreenId> navigate) {
        super(WorkflowType.ROOMS, sale, navigate);
        button(actions, "Dọn phòng xong", () -> HotelService.completeCleaning(selected()));
    }
}
