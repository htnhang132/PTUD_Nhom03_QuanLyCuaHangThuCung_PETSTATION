package com.petstation.gui.search;

import com.petstation.gui.components.WeekSchedulePanel;

public final class PersonalSchedulePanel extends WeekSchedulePanel {
    public PersonalSchedulePanel(String staffCode) {
        super(Mode.PERSONAL, staffCode);
    }
}
