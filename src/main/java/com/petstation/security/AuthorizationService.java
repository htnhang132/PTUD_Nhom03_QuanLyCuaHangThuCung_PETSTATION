package com.petstation.security;

import com.petstation.model.Permission;

public final class AuthorizationService {
    private final SessionContext sessionContext;

    public AuthorizationService(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public void require(Permission permission) {
        if (!sessionContext.requireCurrent().has(permission)) {
            throw new AuthorizationException(
                "Bạn không có quyền sử dụng chức năng này. Liên hệ Cửa hàng trưởng để được cấp quyền."
            );
        }
    }
}
