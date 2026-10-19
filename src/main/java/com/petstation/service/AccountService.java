package com.petstation.service;

import static com.petstation.service.SessionService.*;

import com.petstation.dao.impl.JdbcAccountDao;

public final class AccountService {
    private AccountService() {}

    public static void changePassword(String current, String next) {
        if (current == null || next == null || next.length() < 8)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        com.petstation.dao.SqlDatabase.transaction(
                c -> {
                    long account = SessionService.accountId();
                    String hash =
                            com.petstation.dao.SqlDatabase.text(
                                    JdbcAccountDao.passwordHash(account));
                    if (!org.mindrot.jbcrypt.BCrypt.checkpw(current, hash))
                        throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
                    JdbcAccountDao.updatePassword(
                            org.mindrot.jbcrypt.BCrypt.hashpw(
                                    next, org.mindrot.jbcrypt.BCrypt.gensalt()),
                            account);
                    AuditService.log("Đổi mật khẩu", "Tài khoản hiện tại");
                    return null;
                });
    }
}
