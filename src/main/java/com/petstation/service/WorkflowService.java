package com.petstation.service;

import com.petstation.dao.impl.JdbcWorkflowDao;
import com.petstation.model.WorkflowType;

import java.util.List;

public final class WorkflowService {
    private WorkflowService() {}

    public record Definition(String title, String[] columns) {}

    public static Definition definition(WorkflowType type) {
        var view = JdbcWorkflowDao.view(type);
        return new Definition(view.title(), view.columns());
    }

    public static List<Object[]> rows(WorkflowType type) {
        return JdbcWorkflowDao.rows(
                type, type == WorkflowType.PENDING ? SessionService.accountId() : 0);
    }
}
