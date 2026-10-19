package com.petstation.gui.components;

import com.petstation.model.Role;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class CatalogFields {
    private CatalogFields() {}

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    public static LocalDate date(Object v) {
        try {
            return LocalDate.parse(String.valueOf(v), DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String[] roleNames() {
        Role[] roles = Role.values();
        String[] out = new String[roles.length];
        for (int i = 0; i < roles.length; i++) out[i] = roles[i].displayName();
        return out;
    }
}
