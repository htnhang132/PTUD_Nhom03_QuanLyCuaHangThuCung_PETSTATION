package com.petstation.gui.processing.staff;

import com.petstation.gui.components.WeekSchedulePanel;

public final class StaffSchedulePanel extends WeekSchedulePanel {
    public StaffSchedulePanel(String staffCode) {
        super(Mode.EDIT, staffCode);
    }
}
