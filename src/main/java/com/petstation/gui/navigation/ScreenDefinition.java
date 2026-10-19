package com.petstation.gui.navigation;

import com.petstation.model.Permission;

public record ScreenDefinition(
        ScreenId id, String title, String hint, Permission permission, boolean implemented) {}
