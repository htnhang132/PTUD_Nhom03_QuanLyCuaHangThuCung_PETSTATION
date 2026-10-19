package com.petstation.service;

import static com.petstation.service.SessionService.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.model.AccountStatus;
import com.petstation.model.Employee;
import com.petstation.model.Permission;
import com.petstation.model.Role;
import com.petstation.model.UserAccount;
import com.petstation.model.UserSession;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class AuthorizationServiceTest {
    @Test
    void rejectsMissingPermission() {
        SessionContext sessions = new SessionContext();
        sessions.start(
                new UserSession(
                        new UserAccount(
                                1,
                                1,
                                "sales",
                                "hash",
                                Role.SALES_STAFF,
                                AccountStatus.ACTIVE,
                                0,
                                null,
                                false),
                        new Employee(
                                1,
                                "NV001",
                                "Nguyễn Văn An",
                                null,
                                "0900000000",
                                null,
                                "Nhân viên bán hàng",
                                LocalDate.now(),
                                true),
                        Set.of(Permission.POS_SELL)));

        AuthorizationService service = new AuthorizationService(sessions);

        assertThatThrownBy(() -> service.require(Permission.ACCOUNT_MANAGE))
                .isInstanceOf(AuthorizationException.class);
    }
}
