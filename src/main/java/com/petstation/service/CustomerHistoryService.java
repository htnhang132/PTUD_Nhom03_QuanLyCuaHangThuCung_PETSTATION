package com.petstation.service;

import com.petstation.dao.impl.JdbcCustomerHistoryDao;

import java.util.List;

public final class CustomerHistoryService {
    private CustomerHistoryService() {}

    public static List<Object[]> pets(long customer) {
        SessionService.require(com.petstation.model.Permission.CUSTOMER_MANAGE);
        return JdbcCustomerHistoryDao.pets(customer);
    }

    public static List<Object[]> invoices(long customer) {
        SessionService.require(com.petstation.model.Permission.CUSTOMER_MANAGE);
        return JdbcCustomerHistoryDao.invoices(customer);
    }

    public static List<Object[]> spaHistory(long customer) {
        SessionService.require(com.petstation.model.Permission.CUSTOMER_MANAGE);
        return JdbcCustomerHistoryDao.spaHistory(customer);
    }

    public static List<Object[]> hotelHistory(long customer) {
        SessionService.require(com.petstation.model.Permission.CUSTOMER_MANAGE);
        return JdbcCustomerHistoryDao.hotelHistory(customer);
    }

    public static List<Object[]> pointsHistory(long customer) {
        SessionService.require(com.petstation.model.Permission.CUSTOMER_MANAGE);
        return JdbcCustomerHistoryDao.pointsHistory(customer);
    }

    public static List<Object[]> vouchers(long customer) {
        SessionService.require(com.petstation.model.Permission.CUSTOMER_MANAGE);
        return JdbcCustomerHistoryDao.vouchers(customer);
    }
}
