package com.petstation;

import com.petstation.dao.DatabaseConfig;
import com.petstation.dao.DatabaseConnection;
import com.petstation.dao.SqlDatabase;
import com.petstation.dao.TransactionManager;
import com.petstation.dao.impl.JdbcAuditLogDao;
import com.petstation.dao.impl.JdbcEmployeeDao;
import com.petstation.dao.impl.JdbcUserAccountDao;
import com.petstation.service.AuthenticationService;
import com.petstation.service.AuthorizationService;
import com.petstation.service.SessionContext;

public final class ApplicationContext {
    private final DatabaseConnection databaseConnection;
    private final SessionContext sessionContext;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    private ApplicationContext(
            DatabaseConnection databaseConnection,
            SessionContext sessionContext,
            AuthenticationService authenticationService,
            AuthorizationService authorizationService) {
        this.databaseConnection = databaseConnection;
        this.sessionContext = sessionContext;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
    }

    public static ApplicationContext create() {
        DatabaseConnection database = new DatabaseConnection(DatabaseConfig.load());
        SqlDatabase.configure(database);
        TransactionManager transactions = new TransactionManager(database);
        SessionContext sessions = new SessionContext();
        AuthenticationService authentication =
                new AuthenticationService(
                        transactions,
                        new JdbcUserAccountDao(),
                        new JdbcEmployeeDao(),
                        new JdbcAuditLogDao());
        return new ApplicationContext(
                database, sessions, authentication, new AuthorizationService(sessions));
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
