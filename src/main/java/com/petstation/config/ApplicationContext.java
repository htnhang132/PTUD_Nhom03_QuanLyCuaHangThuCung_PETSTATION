package com.petstation.config;

import com.petstation.dao.impl.JdbcAuditLogDao;
import com.petstation.dao.impl.JdbcEmployeeDao;
import com.petstation.dao.impl.JdbcUserAccountDao;
import com.petstation.security.AuthorizationService;
import com.petstation.security.SessionContext;
import com.petstation.service.AuthenticationService;

public final class ApplicationContext {
    private final DatabaseConnection databaseConnection;
    private final SessionContext sessionContext;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    private ApplicationContext(
        DatabaseConnection databaseConnection,
        SessionContext sessionContext,
        AuthenticationService authenticationService,
        AuthorizationService authorizationService
    ) {
        this.databaseConnection = databaseConnection;
        this.sessionContext = sessionContext;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
    }

    public static ApplicationContext create() {
        DatabaseConnection database = new DatabaseConnection(DatabaseConfig.load());
        TransactionManager transactions = new TransactionManager(database);
        SessionContext sessions = new SessionContext();
        AuthenticationService authentication = new AuthenticationService(
            transactions,
            new JdbcUserAccountDao(),
            new JdbcEmployeeDao(),
            new JdbcAuditLogDao()
        );
        return new ApplicationContext(
            database,
            sessions,
            authentication,
            new AuthorizationService(sessions)
        );
    }

    public DatabaseConnection databaseConnection() {
        return databaseConnection;
    }

    public SessionContext sessionContext() {
        return sessionContext;
    }

    public AuthenticationService authenticationService() {
        return authenticationService;
    }

    public AuthorizationService authorizationService() {
        return authorizationService;
    }
}
