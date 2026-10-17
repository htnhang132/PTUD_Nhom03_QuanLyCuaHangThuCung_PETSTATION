package com.petstation.gui.components;

public record ToolbarCapabilities(
    boolean canCreate,
    boolean canSave,
    boolean canFind,
    boolean canPrint,
    boolean canUndo,
    boolean canHelp
) {
    public static ToolbarCapabilities none() {
        return new ToolbarCapabilities(false, false, false, false, false, false);
    }

    public static ToolbarCapabilities helpOnly() {
        return new ToolbarCapabilities(false, false, false, false, false, true);
    }
}
