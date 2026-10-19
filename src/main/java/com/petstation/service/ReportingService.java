package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import com.petstation.dao.impl.JdbcReportingDao;
import com.petstation.model.Permission;

import java.time.LocalDate;
import java.util.*;

/** Reports aggregate stored transactions; an empty period always returns zeros/empty rows. */
public final class ReportingService {
    private ReportingService() {}

    private static LocalDate from(int days) {
        return LocalDate.now().minusDays(Math.max(1, days) - 1L);
    }

    private static LocalDate until() {
        return LocalDate.now().plusDays(1);
    }

    private static void reportPermission() {
        if (!session().has(Permission.REPORT_VIEW_ALL)) require(Permission.REPORT_VIEW_OWN_SHIFT);
    }

    public static List<Object[]> dailyRevenue(int days) {
        reportPermission();
        boolean all = session().has(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.dailyRevenue(
                from(days), until(), all ? null : accountId(), all ? null : employeeId());
    }

    public static List<Object[]> revenue(int days, String grouping) {
        reportPermission();
        boolean all = session().has(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.revenue(
                from(days), until(), grouping, all ? null : accountId(), all ? null : employeeId());
    }

    public static List<Object[]> topProducts(int days) {
        reportPermission();
        boolean all = session().has(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.topProducts(
                from(days), until(), all ? null : accountId(), all ? null : employeeId());
    }

    public static List<Object[]> inventory() {
        reportPermission();
        return JdbcReportingDao.findInventory();
    }

    public static List<Object[]> expiry() {
        reportPermission();
        return JdbcReportingDao.findExpiry(com.petstation.service.SettingsService.expiryWarnDays());
    }

    public static List<Object[]> spa(int days) {
        reportPermission();
        boolean all = session().has(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.spa(
                from(days), until(), all ? null : accountId(), all ? null : employeeId());
    }

    public static List<Object[]> appointments(int days) {
        reportPermission();
        boolean all = session().has(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.appointments(
                from(days), until(), all ? null : accountId(), all ? null : employeeId());
    }

    public static List<Object[]> roomStatus() {
        reportPermission();
        return JdbcReportingDao.findRoomStatus();
    }

    public static List<Object[]> hotel(int days) {
        reportPermission();
        boolean all = session().has(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.hotel(
                from(days), until(), all ? null : accountId(), all ? null : employeeId());
    }

    public static List<Object[]> customers(int days) {
        require(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.findCustomers(from(days), until());
    }

    public static List<Object[]> vouchers() {
        require(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.findVouchers();
    }

    public static List<Object[]> supplierDebt(int days) {
        require(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.findSupplierDebt(from(days), until());
    }

    public static List<Object[]> staff(int days) {
        require(Permission.REPORT_VIEW_ALL);
        return JdbcReportingDao.findStaff(from(days), until());
    }
}
