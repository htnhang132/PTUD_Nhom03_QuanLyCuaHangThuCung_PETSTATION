package com.petstation.model;

import java.util.Set;

public record UserSession(UserAccount account, Employee employee, Set<Permission> permissions) {
    public Role role() {
        return account.role();
    }

    public String displayName() {
        return employee.fullName();
    }

    public boolean has(Permission permission) {
        return permissions.contains(permission);
    }
}
