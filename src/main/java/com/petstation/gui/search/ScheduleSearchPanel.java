package com.petstation.gui.search;

import com.petstation.gui.components.WeekSchedulePanel;

public final class ScheduleSearchPanel extends WeekSchedulePanel {
    public ScheduleSearchPanel(String staffCode) {
        super(Mode.VIEW_ALL, staffCode);
    }
}
