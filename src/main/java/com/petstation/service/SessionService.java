package com.petstation.service;

import com.petstation.model.UserSession;

public final class SessionService {
    private SessionService() {}

    private static volatile UserSession session;

    public static void setSession(UserSession value) {
        session = value;
    }

    public static UserSession currentSession() {
        return session;
    }

    public static UserSession session() {
        if (session == null)
            throw new IllegalStateException("Bạn cần đăng nhập để thực hiện thao tác này.");
        return session;
    }

    public static long accountId() {
        return session().account().id();
    }

    public static long employeeId() {
        return session().employee().id();
    }

    public static void require(com.petstation.model.Permission permission) {
        if (!session().has(permission))
            throw new com.petstation.service.AuthorizationException(
                    "Bạn không có quyền thực hiện thao tác này.");
    }
}
