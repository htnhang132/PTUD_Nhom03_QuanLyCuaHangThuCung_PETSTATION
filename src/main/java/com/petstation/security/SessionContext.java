package com.petstation.security;

import java.util.Optional;

public final class SessionContext {
    private UserSession session;

    public synchronized void start(UserSession session) {
        this.session = session;
    }

    public synchronized Optional<UserSession> current() {
        return Optional.ofNullable(session);
    }

    public synchronized UserSession requireCurrent() {
        if (session == null) {
            throw new AuthorizationException("Phiên đăng nhập đã hết hạn.");
        }
        return session;
    }

    public synchronized void clear() {
        session = null;
    }
}
