package com.petstation.gui.navigation;

import com.petstation.model.*;
import com.petstation.security.UserSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ScreenRegistryTest {
    @Test
    void managerCanAccessProtectedScreens() {
        ScreenRegistry registry = new ScreenRegistry(
            session(Role.STORE_MANAGER, EnumSet.allOf(Permission.class)),
            ignored -> {}
        );

        assertThat(registry.canAccess(ScreenId.STAFF)).isTrue();
        assertThat(registry.canAccess(ScreenId.AUDIT_LOG)).isTrue();
        assertThat(registry.definition(ScreenId.DASHBOARD).title()).isEqualTo("Màn hình chính");
    }

    @Test
    void salesStaffCannotAccessAccountManagement() {
        ScreenRegistry registry = new ScreenRegistry(
            session(Role.SALES_STAFF, Set.of(Permission.DASHBOARD_VIEW, Permission.POS_SELL)),
            ignored -> {}
        );

        assertThat(registry.canAccess(ScreenId.POS)).isTrue();
        assertThat(registry.canAccess(ScreenId.STAFF)).isFalse();
    }

    private UserSession session(Role role, Set<Permission> permissions) {
        return new UserSession(
            new UserAccount(1, 1, "user", "hash", role, AccountStatus.ACTIVE, 0, null, false),
            new Employee(1, "NV001", "Nguyễn Gia Vĩ", null, "0900000000", null,
                role.displayName(), LocalDate.now(), true),
            permissions
        );
    }
}
