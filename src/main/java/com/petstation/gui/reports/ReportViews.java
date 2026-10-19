package com.petstation.gui.reports;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.util.Money;

import java.util.*;
import java.util.function.IntFunction;

public final class ReportViews {
    private ReportViews() {}

    public static View view(
            String title,
            String[] columns,
            int label,
            int metric,
            boolean money,
            IntFunction<List<Object[]>> query) {
        return new View(
                title,
                days -> {
                    List<Object[]> rows = query.apply((int) days);
                    String[] labels = new String[rows.size()];
                    double[] values = new double[rows.size()];
                    double total = 0;
                    for (int i = 0; i < rows.size(); i++) {
                        labels[i] = String.valueOf(rows.get(i)[label]);
                        Object value = rows.get(i)[metric];
                        values[i] = value == null ? 0 : ((Number) value).doubleValue();
                        total += values[i];
                    }
                    return new Data(
                            labels,
                            values,
                            money,
                            "",
                            columns,
                            rows.toArray(Object[][]::new),
                            new String[] {"Số bản ghi", "Tổng", "Kỳ báo cáo", "Đến ngày"},
                            new String[] {
                                "" + rows.size(),
                                money
                                        ? Money.format(Math.round(total))
                                        : String.format(java.util.Locale.ROOT, "%.0f", total),
                                (int) days + " ngày",
                                java.time.LocalDate.now()
                                        .format(
                                                java.time.format.DateTimeFormatter.ofPattern(
                                                        "dd/MM/yyyy"))
                            });
                });
    }
}
