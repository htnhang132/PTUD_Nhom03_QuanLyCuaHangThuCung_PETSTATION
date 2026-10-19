package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.dao.impl.JdbcWorkflowSupportDao;

import java.time.*;
import java.util.*;

final class WorkflowSupport {
    private WorkflowSupport() {}

    static long id(String table, String key, String code) {
        return CatalogService.id(table, key, code);
    }

    static long value(Object value) {
        return ((Number) value).longValue();
    }

    static void positive(long qty) {
        if (qty <= 0) throw new IllegalArgumentException("Số lượng/giá trị phải lớn hơn 0.");
    }

    static Object[] pet(String code) {
        List<Object[]> rows = JdbcWorkflowSupportDao.findPet(code);
        if (rows.isEmpty())
            throw new IllegalArgumentException("Thú cưng hoặc chủ nuôi không còn hoạt động.");
        return rows.get(0);
    }
}
