package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.components.common.ExcelExporter;
import com.petstation.gui.components.common.FieldEditor;
import com.petstation.gui.navigation.AuditTrail;

/**
 * Màn Danh mục chuẩn (chỉ CRUD): danh sách bên trái, form chi tiết bên phải.
 * Nút Thêm, Sửa, Xóa (xóa mềm), Lưu, Tìm kiếm. Kiểm tra bắt buộc, trùng, định dạng SĐT/email/ngày/tiền.
 * Mọi thay đổi ghi vào Nhật ký hệ thống. Dữ liệu là danh sách chia sẻ trong {@code DemoStore}.
 */
public class EntityPanel extends BasePanel implements ToolbarAware {
    public enum Type { TEXT, PHONE, EMAIL, CCCD, DATE, MONEY, INT, COMBO, AREA, FILE, CUSTOM }

    /** Mô tả một trường dữ liệu. Giá trị lưu dạng chuỗi (tiền lưu chuỗi số, ngày lưu dd/MM/yyyy). */
    public static final class Field {
        final String label;
        final Type type;
        boolean required, unique, readOnly, inList = true;
        String[] options = new String[0];
        Supplier<List<String>> dynamicOptions;
        String defaultValue = "";
        Supplier<FieldEditor> editorFactory;
        Predicate<String> editableWhen;
        Function<String, String> display;

        private Field(String label, Type type) { this.label = label; this.type = type; }

        public static Field of(String label, Type type) { return new Field(label, type); }
        public static Field text(String label) { return new Field(label, Type.TEXT); }
        public Field req() { this.required = true; return this; }
        public Field unique() { this.unique = true; return this; }
        public Field ro() { this.readOnly = true; return this; }
        public Field hide() { this.inList = false; return this; }
        public Field opts(String... o) { this.options = o; return this; }
        public Field dyn(Supplier<List<String>> s) { this.dynamicOptions = s; return this; }
        public Field def(String v) { this.defaultValue = v; return this; }
        public Field editor(Supplier<FieldEditor> f) { this.editorFactory = f; return this; }
        /** Chỉ cho sửa tay khi giá trị hiện tại thỏa điều kiện (ví dụ phòng chỉ chỉnh tay khi Trống/Bảo trì). */
        public Field editableWhen(Predicate<String> p) { this.editableWhen = p; return this; }
        public Field show(Function<String, String> f) { this.display = f; return this; }
    }

    /** Thao tác thêm ngoài CRUD (ví dụ Khóa/Mở khóa tài khoản). Trả về thông báo hiện ở thanh trạng thái. */
    public record Extra(String label, Function<Object[], String> handler) {}

    public static final class Config {
        String title, searchHint = "Tìm kiếm...", entityName = "bản ghi", codePrefix = "";
        int statusIndex = -1;
        String activeLabel = "Đang hoạt động", inactiveLabel = "Ngừng hoạt động";
        Consumer<Object[]> normalizer;
        BiConsumer<Object[], Object[]> onSaved;
        Function<Object[], String> deleteGuard;
        Function<Object[], String> extraValidator;
        final List<Extra> extras = new ArrayList<>();
        String exportName = "Danh_muc";

        public static Config of(String title, String entityName) {
            Config c = new Config(); c.title = title; c.entityName = entityName; c.exportName = title.replace(' ', '_'); return c;
        }
        public Config search(String hint) { this.searchHint = hint; return this; }
        public Config prefix(String p) { this.codePrefix = p; return this; }
        public Config status(int index, String active, String inactive) { this.statusIndex = index; this.activeLabel = active; this.inactiveLabel = inactive; return this; }
        public Config normalizer(Consumer<Object[]> n) { this.normalizer = n; return this; }
        public Config onSaved(BiConsumer<Object[], Object[]> f) { this.onSaved = f; return this; }
        public Config deleteGuard(Function<Object[], String> g) { this.deleteGuard = g; return this; }
        public Config validator(Function<Object[], String> v) { this.extraValidator = v; return this; }
        public Config extra(String label, Function<Object[], String> handler) { extras.add(new Extra(label, handler)); return this; }
    }

    private enum Mode { VIEW, EDIT, NEW }

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);

    private final Config cfg;
    private final List<Field> fields;
    private final List<Object[]> rows;
    private final List<Object[]> shown = new ArrayList<>();
    private final DefaultTableModel model;
    private final JTable table;
    private final JCheckBox showInactive = new JCheckBox("Hiện cả bản ghi ngừng hoạt động");
    private final JComponent[] inputs;
    private final FieldEditor[] editors;
    private final JLabel formTitle = new JLabel();
    private final JLabel message = new JLabel(" ");
    private final StyledButton btnEdit = new StyledButton("Sửa", StyledButton.Variant.SECONDARY);
    private final StyledButton btnDelete = new StyledButton("Xóa", StyledButton.Variant.DANGER);
    private final StyledButton btnSave = new StyledButton("Lưu", StyledButton.Variant.SUCCESS);
    private final StyledButton btnCancel = new StyledButton("Hủy", StyledButton.Variant.GHOST);
    private final List<StyledButton> extraButtons = new ArrayList<>();
    private final List<Integer> listFields = new ArrayList<>();
    private Mode mode = Mode.VIEW;
    private Object[] current;
    private boolean selecting;

    public EntityPanel(Config cfg, List<Field> fields, List<Object[]> rows) {
        this.cfg = cfg;
        this.fields = fields;
        this.rows = rows;
        setLayout(new BorderLayout(0, 12));
        for (int i = 0; i < fields.size(); i++) if (fields.get(i).inList) listFields.add(i);
        for (Object[] r : rows) normalize(r);

        String[] cols = listFields.stream().map(i -> fields.get(i).label).toArray(String[]::new);
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane scroll = styledTable(model);
        table = tableOf(scroll);

        // ── Thanh trên: tiêu đề, tìm kiếm, thao tác
        JTextField search = searchBar(cfg.searchHint);
        bindFilter(search, table);
        StyledButton add = new StyledButton("+ Thêm", StyledButton.Variant.PRIMARY);
        add.addActionListener(e -> startNew());
        btnEdit.addActionListener(e -> startEdit());
        btnDelete.addActionListener(e -> softDelete());
        StyledButton export = new StyledButton("Xuất Excel", StyledButton.Variant.GHOST);
        export.addActionListener(e -> ExcelExporter.exportTable(this, table, cfg.exportName));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(search);
        actions.add(add);
        actions.add(btnEdit);
        actions.add(btnDelete);
        for (Extra ex : cfg.extras) {
            StyledButton b = new StyledButton(ex.label(), StyledButton.Variant.GHOST);
            b.addActionListener(e -> runExtra(ex));
            extraButtons.add(b);
            actions.add(b);
        }
        actions.add(export);

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.add(sectionTitle(cfg.title), BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        showInactive.setOpaque(false);
        showInactive.setFont(AppColors.font(Font.PLAIN, 12));
        showInactive.addActionListener(e -> reload(current));
        JPanel top = transparent(new BorderLayout(0, 4));
        top.add(header, BorderLayout.NORTH);
        top.add(showInactive, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Danh sách trái, form phải
        inputs = new JComponent[fields.size()];
        editors = new FieldEditor[fields.size()];
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, buildForm());
        split.setResizeWeight(0.62);
        split.setBorder(null);
        split.setOpaque(false);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || selecting) return;
            onSelectionChanged();
        });
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && current != null && mode == Mode.VIEW) startEdit();
            }
        });
        reload(null);
        setMode(Mode.VIEW);
    }

    // ───────────────────────── Form
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(14, 16, 14, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(2, 0, 2, 0);
        int y = 0;
        formTitle.setFont(AppColors.font(Font.BOLD, 15));
        c.gridy = y++; form.add(formTitle, c);
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            JLabel label = new JLabel(f.label + (f.required ? " *" : ""));
            label.setFont(AppColors.font(Font.PLAIN, 12));
            label.setForeground(AppColors.TEXT_GRAY);
            c.gridy = y++; c.insets = new Insets(8, 0, 1, 0); form.add(label, c);
            JComponent in = createInput(i, f);
            inputs[i] = in;
            c.gridy = y++; c.insets = new Insets(0, 0, 0, 0); form.add(in, c);
        }
        message.setFont(AppColors.font(Font.BOLD, 12));
        c.gridy = y++; c.insets = new Insets(10, 0, 0, 0); form.add(message, c);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        btnCancel.addActionListener(e -> cancelEdit());
        btnSave.addActionListener(e -> save());
        buttons.add(btnCancel);
        buttons.add(btnSave);
        c.gridy = y++; c.insets = new Insets(6, 0, 0, 0); form.add(buttons, c);
        c.gridy = y; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        form.add(new JPanel() {{ setOpaque(false); }}, c);

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setPreferredSize(new Dimension(360, 0));
        JPanel holder = new JPanel(new BorderLayout());
        holder.add(sp);
        return holder;
    }

    private JComponent createInput(int index, Field f) {
        switch (f.type) {
            case COMBO -> {
                JComboBox<String> box = new JComboBox<>(optionsOf(f));
                box.setFont(AppColors.font(Font.PLAIN, 13));
                return box;
            }
            case AREA -> {
                JTextArea area = new JTextArea(3, 20);
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setFont(AppColors.font(Font.PLAIN, 13));
                JScrollPane s = new JScrollPane(area);
                s.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
                return s;
            }
            case CUSTOM -> {
                FieldEditor ed = f.editorFactory.get();
                editors[index] = ed;
                return ed.component();
            }
            case FILE -> {
                JPanel p = new JPanel(new BorderLayout(6, 0));
                p.setOpaque(false);
                JTextField t = textInput(f);
                StyledButton pick = new StyledButton("Chọn ảnh...", StyledButton.Variant.GHOST);
                pick.addActionListener(e -> {
                    JFileChooser ch = new JFileChooser();
                    ch.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Ảnh (jpg, png)", "jpg", "jpeg", "png"));
                    if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) t.setText(ch.getSelectedFile().getAbsolutePath());
                });
                p.add(t, BorderLayout.CENTER);
                p.add(pick, BorderLayout.EAST);
                p.putClientProperty("text", t);
                return p;
            }
            default -> { return textInput(f); }
        }
    }

    private JTextField textInput(Field f) {
        JTextField t = new JTextField();
        t.setFont(AppColors.font(Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), new EmptyBorder(5, 8, 5, 8)));
        if (f.type == Type.DATE) t.setToolTipText("Định dạng dd/MM/yyyy");
        return t;
    }

    private String[] optionsOf(Field f) {
        if (f.dynamicOptions != null) return f.dynamicOptions.get().toArray(String[]::new);
        return f.options;
    }

    private String getInput(int i) {
        Field f = fields.get(i);
        JComponent in = inputs[i];
        return switch (f.type) {
            case COMBO -> { Object s = ((JComboBox<?>) in).getSelectedItem(); yield s == null ? "" : s.toString(); }
            case AREA -> ((JTextArea) ((JScrollPane) in).getViewport().getView()).getText().trim();
            case CUSTOM -> editors[i].get();
            case FILE -> ((JTextField) in.getClientProperty("text")).getText().trim();
            case MONEY -> { long m = Money.parse(((JTextField) in).getText()); yield m < 0 ? ((JTextField) in).getText().trim().isEmpty() ? "" : "?" : String.valueOf(m); }
            default -> ((JTextField) in).getText().trim();
        };
    }

    @SuppressWarnings("unchecked")
    private void setInput(int i, String value) {
        Field f = fields.get(i);
        JComponent in = inputs[i];
        String v = value == null ? "" : value;
        switch (f.type) {
            case COMBO -> {
                JComboBox<String> box = (JComboBox<String>) in;
                box.setModel(new javax.swing.DefaultComboBoxModel<>(optionsOf(f)));
                box.setSelectedItem(v);
                if (box.getSelectedItem() == null || !box.getSelectedItem().equals(v)) {
                    if (!v.isEmpty()) { box.addItem(v); box.setSelectedItem(v); }
                    else if (box.getItemCount() > 0) box.setSelectedIndex(0);
                }
            }
            case AREA -> ((JTextArea) ((JScrollPane) in).getViewport().getView()).setText(v);
            case CUSTOM -> editors[i].set(v);
            case FILE -> ((JTextField) in.getClientProperty("text")).setText(v);
            case MONEY -> ((JTextField) in).setText(v.isEmpty() ? "" : Money.format(Money.parse(v)));
            default -> ((JTextField) in).setText(v);
        }
    }

    private void setInputEnabled(int i, boolean enabled) {
        Field f = fields.get(i);
        JComponent in = inputs[i];
        if (f.type == Type.CUSTOM) editors[i].setEnabled(enabled);
        else if (f.type == Type.AREA) ((JTextArea) ((JScrollPane) in).getViewport().getView()).setEditable(enabled);
        else if (f.type == Type.FILE) {
            for (java.awt.Component k : ((JPanel) in).getComponents()) k.setEnabled(enabled);
        } else in.setEnabled(enabled);
    }

    // ───────────────────────── Chế độ & chọn dòng
    private void setMode(Mode m) {
        this.mode = m;
        boolean editing = m != Mode.VIEW;
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            boolean allowed = editing && !f.readOnly;
            if (allowed && f.editableWhen != null && m == Mode.EDIT) allowed = f.editableWhen.test(String.valueOf(current[i]));
            setInputEnabled(i, allowed);
        }
        btnSave.setEnabled(editing);
        btnCancel.setEnabled(editing);
        boolean has = current != null && m == Mode.VIEW;
        btnEdit.setEnabled(has);
        btnDelete.setEnabled(has && !isInactive(current));
        for (StyledButton b : extraButtons) b.setEnabled(has);
        formTitle.setText(switch (m) {
            case NEW -> "Thêm " + cfg.entityName + " mới";
            case EDIT -> "Sửa " + cfg.entityName;
            case VIEW -> current == null ? "Chọn một dòng hoặc bấm + Thêm" : "Chi tiết " + cfg.entityName;
        });
    }

    private void onSelectionChanged() {
        int viewRow = table.getSelectedRow();
        Object[] target = viewRow < 0 ? null : shown.get(table.convertRowIndexToModel(viewRow));
        if (mode != Mode.VIEW && target != current && isDirty()) {
            if (!Dialogs.confirm(this, "Bỏ các thay đổi chưa lưu?")) {
                selecting = true;
                selectRow(current);
                selecting = false;
                return;
            }
        }
        showRecord(target);
    }

    private void showRecord(Object[] rec) {
        current = rec;
        clearMessage();
        for (int i = 0; i < fields.size(); i++) setInput(i, rec == null ? "" : String.valueOf(rec[i]));
        setMode(Mode.VIEW);
    }

    private boolean isDirty() {
        for (int i = 0; i < fields.size(); i++) {
            String now = getInput(i);
            String before = mode == Mode.NEW ? defaultOf(i) : String.valueOf(current[i]);
            if (!now.equals(before)) return true;
        }
        return false;
    }

    private String defaultOf(int i) {
        Field f = fields.get(i);
        if (f.type == Type.COMBO) {
            String[] o = optionsOf(f);
            return f.defaultValue.isEmpty() ? (o.length > 0 ? o[0] : "") : f.defaultValue;
        }
        return f.defaultValue;
    }

    private void startNew() {
        if (mode != Mode.VIEW && isDirty() && !Dialogs.confirm(this, "Bỏ các thay đổi chưa lưu?")) return;
        selecting = true;
        table.clearSelection();
        selecting = false;
        current = null;
        clearMessage();
        for (int i = 0; i < fields.size(); i++) {
            setInput(i, i == 0 && fields.get(i).readOnly ? nextCode() : defaultOf(i));
        }
        setMode(Mode.NEW);
        focusFirstEditable();
    }

    private void startEdit() {
        if (current == null) { Dialogs.info(this, "Chọn một dòng để sửa."); return; }
        if (isInactive(current)) {
            // Cho phép khôi phục bản ghi đã ngừng hoạt động
            showRecord(current);
        }
        clearMessage();
        setMode(Mode.EDIT);
        focusFirstEditable();
    }

    private void cancelEdit() {
        showRecord(current);
        selectRow(current);
    }

    private void focusFirstEditable() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i].isEnabled() && fields.get(i).type != Type.CUSTOM && fields.get(i).type != Type.FILE) {
                    inputs[i].requestFocusInWindow();
                    if (inputs[i] instanceof JTextField t) t.selectAll();
                    return;
                }
            }
        });
    }

    // ───────────────────────── Lưu / Xóa mềm
    private void save() {
        Object[] values = new Object[fields.size()];
        List<String> errors = new ArrayList<>();
        int firstBad = -1;
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            String v = getInput(i);
            if (i == 0 && f.readOnly) v = mode == Mode.NEW ? String.valueOf(getInput(0)) : String.valueOf(current[0]);
            values[i] = v;
            if (f.readOnly && !(i == 0)) values[i] = mode == Mode.NEW ? defaultOf(i) : current[i];
            String err = validateField(i, f, String.valueOf(values[i]));
            if (err != null) { errors.add(err); if (firstBad < 0) firstBad = i; }
        }
        if (errors.isEmpty() && cfg.extraValidator != null) {
            String err = cfg.extraValidator.apply(values);
            if (err != null) errors.add(err);
        }
        if (!errors.isEmpty()) {
            Dialogs.warn(this, "Chưa thể lưu:\n• " + String.join("\n• ", errors));
            if (firstBad >= 0) inputs[firstBad].requestFocusInWindow();
            return;
        }
        if (cfg.normalizer != null) cfg.normalizer.accept(values);
        Object[] before = mode == Mode.EDIT ? current.clone() : null;
        Object[] saved;
        if (mode == Mode.NEW) {
            rows.add(values);
            saved = values;
            AuditTrail.log("Thêm", cfg.entityName + " " + values[0] + " - " + values[1]);
        } else {
            System.arraycopy(values, 0, current, 0, values.length);
            saved = current;
            AuditTrail.log("Sửa", cfg.entityName + " " + saved[0] + " - " + saved[1]);
        }
        if (cfg.onSaved != null) cfg.onSaved.accept(before, saved);
        reload(saved);
        flash("Đã lưu " + cfg.entityName + " " + saved[0] + ".");
    }

    private String validateField(int i, Field f, String v) {
        String name = f.label;
        if (f.required && v.isBlank()) return name + " không được để trống.";
        if (v.isBlank()) return null;
        switch (f.type) {
            case PHONE -> { if (!v.replaceAll("[\\s.]", "").matches("0\\d{9}")) return name + " phải gồm 10 chữ số, bắt đầu bằng 0."; }
            case EMAIL -> { if (!v.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) return name + " không đúng định dạng (vd: ten@email.com)."; }
            case CCCD -> { if (!v.matches("\\d{12}")) return name + " phải gồm đúng 12 chữ số."; }
            case DATE -> { try { LocalDate.parse(v, DATE); } catch (DateTimeParseException ex) { return name + " phải theo dạng dd/MM/yyyy."; } }
            case MONEY -> { if (v.equals("?")) return name + " không phải số tiền hợp lệ."; }
            case INT -> { if (!v.matches("\\d{1,9}")) return name + " phải là số nguyên không âm."; }
            default -> { }
        }
        if (f.unique) {
            String key = v.replaceAll("[\\s.]", "").toLowerCase();
            for (Object[] r : rows) {
                if (r == current && mode == Mode.EDIT) continue;
                if (String.valueOf(r[i]).replaceAll("[\\s.]", "").toLowerCase().equals(key)) {
                    return name + " \"" + v + "\" đã tồn tại (" + r[0] + ").";
                }
            }
        }
        return null;
    }

    private void softDelete() {
        if (current == null) return;
        if (cfg.statusIndex < 0) { Dialogs.info(this, "Danh mục này không hỗ trợ ngừng hoạt động."); return; }
        if (cfg.deleteGuard != null) {
            String block = cfg.deleteGuard.apply(current);
            if (block != null) { Dialogs.warn(this, block); return; }
        }
        if (!Dialogs.confirmDanger(this, "Chuyển " + cfg.entityName + " " + current[0] + " - " + current[1]
            + " sang \"" + cfg.inactiveLabel + "\"?\nDữ liệu và lịch sử giao dịch vẫn được giữ (xóa mềm).", cfg.inactiveLabel)) return;
        Object[] before = current.clone();
        current[cfg.statusIndex] = cfg.inactiveLabel;
        AuditTrail.log("Xóa", cfg.entityName + " " + current[0] + " - " + current[1] + " (" + cfg.inactiveLabel + ")");
        if (cfg.onSaved != null) cfg.onSaved.accept(before, current);
        reload(current);
        flash(cfg.entityName + " " + current[0] + " đã chuyển sang " + cfg.inactiveLabel + ".");
    }

    private void runExtra(Extra ex) {
        if (current == null) return;
        String result = ex.handler().apply(current);
        reload(current);
        if (result != null) flash(result);
    }

    private boolean isInactive(Object[] r) {
        return cfg.statusIndex >= 0 && cfg.inactiveLabel.equals(String.valueOf(r[cfg.statusIndex]));
    }

    private void normalize(Object[] r) { if (cfg.normalizer != null) cfg.normalizer.accept(r); }

    private String nextCode() {
        int max = 0;
        for (Object[] r : rows) {
            String digits = String.valueOf(r[0]).replaceAll("\\D", "");
            if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
        }
        return cfg.codePrefix + String.format("%03d", max + 1);
    }

    // ───────────────────────── Bảng
    private void reload(Object[] keep) {
        for (Object[] r : rows) normalize(r);
        shown.clear();
        model.setRowCount(0);
        for (Object[] r : rows) {
            if (!showInactive.isSelected() && isInactive(r)) continue;
            shown.add(r);
            Object[] line = new Object[listFields.size()];
            for (int k = 0; k < line.length; k++) {
                int i = listFields.get(k);
                Field f = fields.get(i);
                String v = String.valueOf(r[i]);
                line[k] = f.display != null ? f.display.apply(v)
                    : f.type == Type.MONEY && !v.isBlank() ? Money.format(Money.parse(v)) : v;
            }
            model.addRow(line);
        }
        selectRow(keep);
        if (keep != null && !shown.contains(keep)) showRecord(null);
        else if (keep != null) showRecord(keep);
    }

    private void selectRow(Object[] rec) {
        selecting = true;
        try {
            int idx = rec == null ? -1 : shown.indexOf(rec);
            if (idx < 0) table.clearSelection();
            else {
                int view = table.convertRowIndexToView(idx);
                if (view >= 0) table.setRowSelectionInterval(view, view);
            }
        } finally { selecting = false; }
    }

    private void flash(String text) {
        message.setForeground(AppColors.GREEN);
        message.setText(text);
        Timer t = new Timer(3500, e -> { if (message.getText().equals(text)) clearMessage(); });
        t.setRepeats(false);
        t.start();
    }

    private void clearMessage() { message.setText(" "); }

    @Override public void onShow() {
        // Danh sách chọn (ví dụ chủ nuôi) và trạng thái tự tính theo ngày có thể đã đổi
        Object[] keep = current;
        if (mode == Mode.VIEW) reload(keep);
    }

    @Override protected String helpTitle() { return cfg.title; }

    @Override protected String helpText() {
        return "Danh mục chỉ thêm, sửa, xóa (xóa mềm), lưu và tìm kiếm.\n"
            + "Chọn dòng để xem, bấm đúp hoặc \"Sửa\" để chỉnh; ô * là bắt buộc.\n"
            + "Bản ghi đã phát sinh giao dịch không xóa thật, chỉ chuyển sang \"" + cfg.inactiveLabel + "\".";
    }
}
