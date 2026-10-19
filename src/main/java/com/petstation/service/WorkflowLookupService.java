package com.petstation.service;

import com.petstation.dao.impl.JdbcWorkflowLookupDao;

import java.util.List;

public final class WorkflowLookupService {
    private WorkflowLookupService() {}

    public static List<Object[]> optionsPet() {
        return JdbcWorkflowLookupDao.optionsPet();
    }

    public static List<Object[]> optionsPackage() {
        return JdbcWorkflowLookupDao.optionsPackage();
    }

    public static List<Object[]> optionsBatch() {
        return JdbcWorkflowLookupDao.optionsBatch();
    }

    public static List<Object[]> optionsRoom() {
        return JdbcWorkflowLookupDao.optionsRoom();
    }

    public static List<Object[]> optionsSupplier() {
        return JdbcWorkflowLookupDao.optionsSupplier();
    }

    public static List<Object[]> optionsProduct() {
        return JdbcWorkflowLookupDao.optionsProduct();
    }

    public static List<Object[]> optionsItem() {
        return JdbcWorkflowLookupDao.optionsItem();
    }

    public static List<Object[]> optionsStaff() {
        return JdbcWorkflowLookupDao.optionsStaff();
    }
}
