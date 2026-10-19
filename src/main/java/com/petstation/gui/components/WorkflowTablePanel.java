package com.petstation.gui.components;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.common.ExcelExporter;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.model.WorkflowType;
import com.petstation.service.WorkflowService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public abstract class WorkflowTablePanel extends BasePanel implements ToolbarAware {
    protected final WorkflowType workflowType;
    protected final SaleContext sale;
    protected final Consumer<ScreenId> navigate;
    private final DefaultTableModel model;
    protected final JTable table;
    protected final JPanel actions;
    private final JLabel count = new JLabel();

    protected WorkflowTablePanel(
            WorkflowType workflowType, SaleContext sale, Consumer<ScreenId> navigate) {
        this.workflowType = workflowType;
        this.sale = sale;
        this.navigate = navigate;
        WorkflowService.Definition view = WorkflowService.definition(workflowType);
        setLayout(new BorderLayout(0, 12));
        JPanel top = transparent(new BorderLayout());
        top.add(sectionTitle(view.title()), BorderLayout.NORTH);
        actions = transparent(new GridLayout(0, 4, 8, 6));
        JTextField search = searchBar("Tìm trong danh sách...");
        actions.add(search);
        button(actions, "Tải lại", this::refresh);
        actions.add(count);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        model =
                new DefaultTableModel(view.columns(), 0) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
        JScrollPane scroll = styledTable(model);
        table = tableOf(scroll);
        table.removeColumn(table.getColumnModel().getColumn(0));
        bindFilter(search, table);
        add(scroll, BorderLayout.CENTER);
        StyledButton export = new StyledButton("Xuất Excel", StyledButton.Variant.GHOST);
        export.addActionListener(e -> ExcelExporter.exportTable(this, table, view.title()));
        JPanel footer = transparent(new FlowLayout(FlowLayout.RIGHT));
        footer.add(export);
        add(footer, BorderLayout.SOUTH);
        refresh();
    }

    protected void button(JPanel holder, String label, Runnable action) {
        StyledButton button = new StyledButton(label, StyledButton.Variant.SECONDARY);
        button.addActionListener(
                e -> {
                    try {
                        action.run();
                        refresh();
                    } catch (RuntimeException exception) {
                        Dialogs.warn(this, exception.getMessage());
                    }
                });
        holder.add(button);
    }

    protected void refresh() {
        model.setRowCount(0);
        for (Object[] row : WorkflowService.rows(workflowType)) model.addRow(row);
        count.setText("  " + model.getRowCount() + " bản ghi");
    }

    protected long selected() {
        if (table.getSelectedRow() < 0) throw new IllegalArgumentException("Hãy chọn một dòng.");
        return ((Number) model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0))
                .longValue();
    }

    protected String selectedText(int col) {
        selected();
        return text(model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), col));
    }

    protected void details(String title, String[] columns, List<Object[]> rows) {
        DefaultTableModel details =
                new DefaultTableModel(rows.toArray(Object[][]::new), columns) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
        JScrollPane scroll = styledTable(details);
        scroll.setPreferredSize(new Dimension(800, 320));
        Dialogs.showMessageDialog(this, scroll, title, JOptionPane.PLAIN_MESSAGE);
    }

    public void onShow() {
        refresh();
    }

    protected String helpText() {
        return "Chọn dòng trước khi xử lý. Các thay đổi được lưu vào SQL Server; nút Tải lại lấy dữ"
                + " liệu mới nhất.";
    }
}
