package com.petstation.gui.components;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.common.SqlForm.Field;
import com.petstation.service.WorkflowLookupService;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.swing.*;

public final class WorkflowFields {
    private WorkflowFields() {}

    public static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    public static Field options(String key, String label, List<Object[]> rows) {
        return Field.choice(key, label, rows);
    }

    public static Field textField(String key, String label, String initial) {
        return Field.text(key, label, initial);
    }

    public static long amount(Map<String, String> v, String key) {
        return Long.parseLong(v.get(key));
    }

    public static int qty(Map<String, String> v, String key) {
        return Integer.parseInt(v.get(key));
    }

    public static LocalDateTime time(Map<String, String> v, String key) {
        return LocalDateTime.parse(v.get(key), DATE_TIME);
    }

    public static String now() {
        return LocalDateTime.now().withSecond(0).format(DATE_TIME);
    }

    public static Field pets() {
        return options("pet", "Thú cưng", WorkflowLookupService.optionsPet());
    }

    public static Field packages() {
        return options("package", "Dịch vụ", WorkflowLookupService.optionsPackage());
    }

    public static Field batches() {
        return options("batch", "Lô hàng", WorkflowLookupService.optionsBatch());
    }
}
