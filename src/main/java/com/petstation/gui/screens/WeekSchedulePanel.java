package com.petstation.gui.screens;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.data.DemoStore;
import com.petstation.gui.data.ScheduleStore;
import com.petstation.gui.data.ScheduleStore.Entry;
import com.petstation.gui.navigation.AuditTrail;

/**
 * Lịch làm việc theo tuần dạng lưới: hàng là ca (Sáng, Chiều, Tối), cột là các ngày trong tuần, mỗi ô chứa thẻ nhân viên.
 * Một giao diện cho 3 màn: xem toàn cửa hàng, xem cá nhân (kèm Đăng ký nghỉ) và phân lịch (thêm/bớt nhân viên trong ô).
 */
public final class WeekSchedulePanel extends BasePanel implements ToolbarAware {
    public enum Mode { VIEW_ALL, PERSONAL, EDIT }

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
    private static final String[] DOW = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
    private static final Color HEADER_BG = new Color(0xEA, 0xF1, 0xFB);
    private static final Color HEADER_FG = new Color(0x1F, 0x6F, 0xB5);
    private static final Color ROW_LABEL_BG = new Color(0xFF, 0xFF, 0xCC);
    private static final Color SALES = new Color(0xE6, 0xEA, 0xEE);
    private static final Color SPA = new Color(0x72, 0xD1, 0x2F);
    private static final Color WAREHOUSE = new Color(0x92, 0xD2, 0xF6);
    private static final Color MANAGER = new Color(0xFF, 0xF2, 0x8C);
    private static final Color LEAVE = new Color(0xE0, 0x4B, 0x3A);

    private final Mode mode;
    private final String currentUser;
    private LocalDate monday = ScheduleStore.mondayOf(LocalDate.now());
    private final JTextField dateField = new JTextField(10);
    private final JRadioButton all = new JRadioButton("Tất cả", true);
    private final JRadioButton work = new JRadioButton("Ca làm việc");
    private final JRadioButton leave = new JRadioButton("Nghỉ phép");
    private final JPanel gridHost = new JPanel(new BorderLayout());
    private final JLabel summary = new JLabel(" ");

    public WeekSchedulePanel(Mode mode, String currentUser) {
        this.mode = mode;
        this.currentUser = currentUser == null ? "" : currentUser;
        setLayout(new BorderLayout(0, 10));

        String title = switch (mode) {
            case VIEW_ALL -> "Lịch làm việc theo tuần";
            case PERSONAL -> "Lịch làm việc cá nhân theo tuần";
            case EDIT -> "Phân lịch nhân viên theo tuần";
        };
        JLabel heading = sectionTitle(title);
        JPanel north = transparent(new BorderLayout(0, 8));
        north.add(heading, BorderLayout.NORTH);
        north.add(buildToolbar(), BorderLayout.CENTER);
        summary.setFont(AppColors.font(Font.PLAIN, 12));
        summary.setForeground(AppColors.TEXT_GRAY);
        north.add(summary, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        gridHost.setOpaque(false);
        JScrollPane scroll = new JScrollPane(gridHost);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);

        ScheduleStore.addListener(() -> SwingUtilities.invokeLater(this::rebuild));
        rebuild();
    }

    // ───────────── Thanh công cụ (giống form lịch tuần)
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (JRadioButton r : new JRadioButton[]{all, work, leave}) {
            r.setOpaque(false);
            r.setFont(AppColors.font(Font.PLAIN, 13));
            r.addActionListener(e -> rebuild());
            group.add(r);
            bar.add(r);
        }
        dateField.setFont(AppColors.font(Font.PLAIN, 13));
        dateField.setHorizontalAlignment(SwingConstants.CENTER);
        dateField.setToolTipText("Nhập ngày dd/MM/yyyy rồi Enter để xem tuần chứa ngày đó");
        dateField.addActionListener(e -> jumpToTypedDate());
        bar.add(dateField);

        StyledButton today = new StyledButton("Hiện tại", StyledButton.Variant.PRIMARY);
        today.addActionListener(e -> { monday = ScheduleStore.mondayOf(LocalDate.now()); rebuild(); });
        StyledButton print = new StyledButton("In lịch", StyledButton.Variant.PRIMARY);
        print.addActionListener(e -> printSchedule());
        StyledButton back = new StyledButton("‹ Trở về", StyledButton.Variant.PRIMARY);
        back.addActionListener(e -> { monday = monday.minusWeeks(1); rebuild(); });
        StyledButton next = new StyledButton("Tiếp ›", StyledButton.Variant.PRIMARY);
        next.addActionListener(e -> { monday = monday.plusWeeks(1); rebuild(); });
        bar.add(today);
        bar.add(print);
        bar.add(back);
        bar.add(next);
        if (mode == Mode.PERSONAL) {
            StyledButton ask = new StyledButton("Đăng ký nghỉ phép", StyledButton.Variant.SECONDARY);
            ask.addActionListener(e -> QuickFlows.leave(this));
            bar.add(ask);
        }
        return bar;
    }

    private void jumpToTypedDate() {
        try {
            monday = ScheduleStore.mondayOf(LocalDate.parse(dateField.getText().trim(), DAY));
            rebuild();
        } catch (DateTimeParseException ex) {
            Dialogs.warn(this, "Ngày phải theo dạng dd/MM/yyyy, ví dụ 09/10/2026.");
            dateField.setText(LocalDate.now().format(DAY));
        }
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        legend.setOpaque(false);
        legend.add(chip(SALES, "Nhân viên bán hàng"));
        legend.add(chip(SPA, "Nhân viên Spa/Hotel"));
        legend.add(chip(WAREHOUSE, "Nhân viên kho"));
        legend.add(chip(MANAGER, "Cửa hàng trưởng"));
        legend.add(chip(LEAVE, "Nghỉ phép"));
        JLabel warn = new JLabel("Viền đỏ = ca thiếu người");
        warn.setForeground(LEAVE);
        warn.setFont(AppColors.font(Font.BOLD, 12));
        legend.add(warn);
        return legend;
    }

    private static JPanel chip(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setBackground(c);
        box.setPreferredSize(new Dimension(44, 14));
        box.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 40)));
        JLabel l = new JLabel(text);
        l.setFont(AppColors.font(Font.PLAIN, 12));
        l.setForeground(AppColors.TEXT_GRAY);
        p.add(box);
        p.add(l);
        return p;
    }

    // ───────────── Lưới
    private void rebuild() {
        dateField.setText(monday.format(DAY));
        gridHost.removeAll();
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);

        c.gridy = 0; c.gridx = 0; c.weightx = 0; c.weighty = 0;
        grid.add(headerCell("Ca làm", null, false), c);
        for (int d = 0; d < 7; d++) {
            LocalDate date = monday.plusDays(d);
            c.gridx = d + 1; c.weightx = 1;
            grid.add(headerCell(DOW[d], date.format(DAY), date.equals(LocalDate.now())), c);
        }
        for (int s = 0; s < ScheduleStore.SHIFTS.length; s++) {
            c.gridy = s + 1; c.gridx = 0; c.weightx = 0; c.weighty = 1;
            grid.add(rowLabel(s), c);
            for (int d = 0; d < 7; d++) {
                c.gridx = d + 1; c.weightx = 1;
                grid.add(cell(monday.plusDays(d), s), c);
            }
        }
        gridHost.add(grid, BorderLayout.CENTER);
        updateSummary();
        gridHost.revalidate();
        gridHost.repaint();
    }

    private JPanel headerCell(String top, String bottom, boolean today) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(today ? new Color(0xD3, 0xE6, 0xFA) : HEADER_BG);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(0xC9, 0xD6, 0xE5)));
        p.setPreferredSize(new Dimension(bottom == null ? 76 : 104, 52));
        JLabel l = new JLabel("<html><div style='text-align:center'>" + top + (bottom == null ? "" : "<br>" + bottom) + "</div></html>", SwingConstants.CENTER);
        l.setFont(AppColors.font(Font.BOLD, 14));
        l.setForeground(HEADER_FG);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private JPanel rowLabel(int shift) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ROW_LABEL_BG);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0xE3, 0xE3, 0xB5)));
        JLabel l = new JLabel("<html><div style='text-align:center'><b>" + ScheduleStore.SHIFTS[shift] + "</b><br><span style='font-size:9px;color:#666'>"
            + ScheduleStore.SHIFT_TIMES[shift] + "</span></div></html>", SwingConstants.CENTER);
        l.setFont(AppColors.font(Font.BOLD, 14));
        l.setForeground(AppColors.TEXT_GRAY);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private boolean visible(Entry e) {
        if (work.isSelected() && e.isLeave()) return false;
        if (leave.isSelected() && !e.isLeave()) return false;
        if (mode == Mode.PERSONAL) return staffOf(e.staffCode) != null && String.valueOf(staffOf(e.staffCode)[1]).equalsIgnoreCase(currentUser);
        return true;
    }

    private JPanel cell(LocalDate date, int shift) {
        JPanel p = new JPanel() {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(104, d.width), Math.max(130, d.height));
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(date.equals(LocalDate.now()) ? new Color(0xFC, 0xFD, 0xFF) : Color.WHITE);
        boolean short1 = mode != Mode.PERSONAL && ScheduleStore.workCount(date, shift) < ScheduleStore.MIN_STAFF[shift];
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0xE3, 0xE7, 0xEC)),
            short1 && !leave.isSelected() ? BorderFactory.createLineBorder(LEAVE, 2) : new EmptyBorder(2, 2, 2, 2)));

        List<Entry> entries = new ArrayList<>();
        for (Entry e : ScheduleStore.cell(date, shift)) if (visible(e)) entries.add(e);
        for (Entry e : entries) {
            p.add(card(e));
            p.add(javax.swing.Box.createVerticalStrut(4));
        }
        if (short1 && !leave.isSelected()) {
            long need = ScheduleStore.MIN_STAFF[shift] - ScheduleStore.workCount(date, shift);
            JLabel warn = new JLabel("Thiếu " + need + " người");
            warn.setFont(AppColors.font(Font.BOLD, 11));
            warn.setForeground(LEAVE);
            warn.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(warn);
        }
        if (mode == Mode.EDIT) {
            JLabel add = new JLabel("+ Thêm nhân viên");
            add.setFont(AppColors.font(Font.BOLD, 11));
            add.setForeground(HEADER_FG);
            add.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add.setAlignmentX(Component.LEFT_ALIGNMENT);
            add.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { addStaff(date, shift); }
            });
            p.add(add);
        }
        return p;
    }

    private JPanel card(Entry e) {
        Object[] staff = staffOf(e.staffCode);
        String name = staff == null ? e.staffCode : String.valueOf(staff[1]);
        String fullPosition = staff == null ? "" : String.valueOf(staff[4]);
        String position = fullPosition.contains("bán hàng") ? "Bán hàng" : fullPosition.contains("Spa") ? "Spa / Hotel" : fullPosition.contains("kho") ? "Kho" : fullPosition;
        Color bg = e.isLeave() ? LEAVE : fullPosition.contains("Spa") ? SPA : fullPosition.contains("kho") ? WAREHOUSE
            : fullPosition.contains("trưởng") ? MANAGER : SALES;
        Color fg = e.isLeave() ? Color.WHITE : new Color(0x1B, 0x2A, 0x3A);
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 45)), new EmptyBorder(5, 6, 5, 6)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        String body = "<html><body style='width:88px'><b>" + name + "</b><br>" + position + "<br>"
            + (e.isLeave() ? "<b>Nghỉ phép</b>" : ScheduleStore.SHIFT_TIMES[e.shift]) + "</body></html>";
        JLabel l = new JLabel(body);
        l.setFont(AppColors.font(Font.PLAIN, 12));
        l.setForeground(fg);
        card.add(l, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 4));
        if (mode == Mode.EDIT) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.setToolTipText("Bấm để bỏ " + name + " khỏi ca này");
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent ev) { removeStaff(e, name); }
            });
        }
        return card;
    }

    private static Object[] staffOf(String code) {
        for (Object[] s : DemoStore.staff()) if (String.valueOf(s[0]).equals(code)) return s;
        return null;
    }

    // ───────────── Phân lịch
    private void addStaff(LocalDate date, int shift) {
        List<String> options = new ArrayList<>();
        for (String o : DemoStore.staffOptions()) {
            String code = o.substring(0, o.indexOf(' '));
            if (ScheduleStore.cell(date, shift).stream().noneMatch(e -> e.staffCode.equals(code))) options.add(o);
        }
        if (options.isEmpty()) { Dialogs.info(this, "Mọi nhân viên đã có trong ca này."); return; }
        Object pick = JOptionPane.showInputDialog(this,
            "Thêm nhân viên vào ca " + ScheduleStore.SHIFTS[shift] + " ngày " + date.format(DAY) + ":", "Phân lịch",
            JOptionPane.QUESTION_MESSAGE, null, options.toArray(), options.get(0));
        if (pick == null) return;
        String code = String.valueOf(pick).substring(0, String.valueOf(pick).indexOf(' '));
        String error = ScheduleStore.assign(date, shift, code);
        if (error != null) { Dialogs.warn(this, error); return; }
        AuditTrail.log("Sửa", "Phân lịch: " + pick + " vào ca " + ScheduleStore.SHIFTS[shift] + " " + date.format(DAY));
    }

    private void removeStaff(Entry e, String name) {
        if (!Dialogs.confirmDanger(this, "Bỏ " + name + " khỏi ca " + ScheduleStore.SHIFTS[e.shift] + " ngày " + e.date.format(DAY) + "?", "Bỏ khỏi ca")) return;
        ScheduleStore.remove(e);
        AuditTrail.log("Sửa", "Phân lịch: bỏ " + name + " khỏi ca " + ScheduleStore.SHIFTS[e.shift] + " " + e.date.format(DAY));
    }

    private void updateSummary() {
        LocalDate end = monday.plusDays(6);
        String week = "Tuần " + monday.format(DAY) + " – " + end.format(DAY);
        if (mode == Mode.PERSONAL) {
            Object[] me = null;
            for (Object[] s : DemoStore.staff()) if (String.valueOf(s[1]).equalsIgnoreCase(currentUser)) me = s;
            if (me == null) { summary.setText(week + " · Chưa tìm thấy hồ sơ nhân viên của bạn nên chưa có ca nào."); return; }
            final Object[] mine = me;
            long leaves = ScheduleStore.weekEntries(monday).stream().filter(e -> e.staffCode.equals(mine[0]) && e.isLeave()).count();
            summary.setText(week + " · Tổng giờ làm trong tháng " + monday.getMonthValue() + ": " + ScheduleStore.monthHours(String.valueOf(me[0]), monday.plusDays(3))
                + " giờ · Nghỉ phép tuần này: " + leaves + " ca");
            return;
        }
        int short1 = 0;
        for (int d = 0; d < 7; d++) for (int s = 0; s < 3; s++) if (ScheduleStore.workCount(monday.plusDays(d), s) < ScheduleStore.MIN_STAFF[s]) short1++;
        summary.setText(week + " · " + (short1 == 0 ? "Đủ người ở mọi ca" : short1 + " ca thiếu người (viền đỏ)")
            + (mode == Mode.EDIT ? " · Bấm \"+ Thêm nhân viên\" để phân ca, bấm vào thẻ để bỏ khỏi ca" : ""));
    }

    // ───────────── In lịch
    private void printSchedule() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Lịch làm việc " + monday.format(DAY));
        job.setPrintable(new Printable() {
            @Override public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
                if (page > 0) return NO_SUCH_PAGE;
                Component target = gridHost;
                Graphics2D g2 = (Graphics2D) g;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                double sx = pf.getImageableWidth() / target.getWidth();
                double sy = pf.getImageableHeight() / target.getHeight();
                double s = Math.min(1, Math.min(sx, sy));
                g2.scale(s, s);
                target.printAll(g2);
                return PAGE_EXISTS;
            }
        });
        try {
            if (job.printDialog()) {
                job.print();
                AuditTrail.log("In lịch", "Lịch làm việc tuần " + monday.format(DAY));
            }
        } catch (PrinterException ex) {
            Dialogs.error(this, "Không in được: " + ex.getMessage());
        } catch (java.awt.HeadlessException ex) {
            Dialogs.error(this, "Không tìm thấy máy in.");
        }
    }

    @Override public void onShow() { rebuild(); }

    @Override protected String helpTitle() { return "Lịch làm việc theo tuần"; }

    @Override protected String helpText() {
        return switch (mode) {
            case VIEW_ALL -> "Lịch cả cửa hàng theo tuần. Dùng Trở về / Tiếp, nhập ngày hoặc Hiện tại để đổi tuần. Ô viền đỏ là ca thiếu người.";
            case PERSONAL -> "Ca của bạn trong tuần, tổng giờ trong tháng. Bấm \"Đăng ký nghỉ phép\" để gửi yêu cầu sang Cửa hàng trưởng duyệt.";
            case EDIT -> "Chọn tuần rồi bấm \"+ Thêm nhân viên\" trong ô để phân ca; bấm vào thẻ nhân viên để bỏ khỏi ca. Ô viền đỏ là ca thiếu người.";
        };
    }
}
