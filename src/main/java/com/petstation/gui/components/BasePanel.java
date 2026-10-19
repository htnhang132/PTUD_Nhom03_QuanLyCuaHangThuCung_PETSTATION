package com.petstation.gui.components;

import com.petstation.gui.components.common.TableStyler;

import java.awt.*;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Lớp nền cho mọi màn hình nghiệp vụ. Tự đăng ký ô tìm kiếm và bảng đầu tiên để thanh công cụ (Tìm,
 * In) hoạt động thật, và tự nối ô tìm kiếm với bộ lọc của bảng.
 */
public abstract class BasePanel extends JPanel implements ToolbarAware {
    private JTextField primarySearch;
    private JTable primaryTable;
    private JTextField unboundSearch;

    public BasePanel() {
        setBackground(AppColors.BG_CREAM);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));
    }

    // ── Thanh công cụ ─────────────────────────────────────────────
    @Override
    public ToolbarCapabilities toolbarCapabilities() {
        return new ToolbarCapabilities(
                false, false, primarySearch != null, primaryTable != null, false, true);
    }

    @Override
    public void onFind() {
        if (primarySearch != null) {
            primarySearch.requestFocusInWindow();
            primarySearch.selectAll();
        }
    }

    @Override
    public void onPrint() {
        if (primaryTable == null) return;
        try {
            primaryTable.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat(helpTitle()),
                    new MessageFormat("Trang {0}"));
        } catch (java.awt.print.PrinterException | HeadlessException exception) {
            Dialogs.showMessageDialog(
                    this,
                    "Không thể in: " + exception.getMessage(),
                    "In",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void onHelp() {
        Dialogs.showMessageDialog(this, helpText(), helpTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    protected String helpTitle() {
        return "Trợ giúp";
    }

    protected String helpText() {
        return primarySearch != null
                ? "Gõ vào ô tìm kiếm (Ctrl+F) để lọc danh sách. Bấm tiêu đề cột để sắp xếp."
                : "Dùng các nút trên màn hình để thao tác.";
    }

    // ── Thẻ thống kê ──────────────────────────────────────────────
    /** Số liệu thường dùng màu chữ trung tính; chỉ trạng thái bất thường mới có màu. */
    protected JPanel statCard(String label, String value, String ignoredIcon, Color accent) {
        RoundedPanel card = new RoundedPanel(12, AppColors.CARD_BG, true);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppColors.font(Font.PLAIN, 12));
        lbl.setForeground(AppColors.TEXT_GRAY);

        JLabel val = new JLabel(value);
        val.setFont(AppColors.font(Font.BOLD, 22));
        val.setForeground(accent == null ? AppColors.TEXT_DARK : accent);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    protected JPanel statCard(String label, String value) {
        return statCard(label, value, null, AppColors.TEXT_DARK);
    }

    // ── Tiêu đề ───────────────────────────────────────────────────
    protected JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.font(Font.BOLD, 16));
        l.setForeground(AppColors.TEXT_DARK);
        return l;
    }

    // ── Bảng ──────────────────────────────────────────────────────
    protected JScrollPane styledTable(String[] cols, Object[][] data) {
        DefaultTableModel model =
                new DefaultTableModel(data, cols) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
        return styledTable(model);
    }

    protected JScrollPane styledTable(TableModel model) {
        JTable table = new JTable(model);
        TableStyler.apply(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER, 1));
        sp.getViewport().setBackground(AppColors.CARD_BG);

        if (primaryTable == null) primaryTable = table;
        if (unboundSearch != null) {
            bindFilter(unboundSearch, table);
            unboundSearch = null;
        }
        return sp;
    }

    protected static final String SOFT_DELETE_NOTE =
            "Bản ghi sẽ chuyển sang \"Ngừng hoạt động\" (xóa mềm), vẫn giữ lịch sử giao dịch.";

    protected static JTable tableOf(JScrollPane scroll) {
        return (JTable) scroll.getViewport().getView();
    }

    /** Lọc các dòng chứa chuỗi tìm kiếm (không phân biệt hoa thường) trên mọi cột. */
    protected void bindFilter(JTextField search, JTable table) {
        search.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            private void apply() {
                                String q = search.getText().trim();
                                @SuppressWarnings("unchecked")
                                TableRowSorter<TableModel> sorter =
                                        (TableRowSorter<TableModel>) table.getRowSorter();
                                if (sorter == null) return;
                                sorter.setRowFilter(
                                        q.isEmpty()
                                                ? null
                                                : RowFilter.regexFilter(
                                                        "(?iu)"
                                                                + java.util.regex.Pattern.quote(
                                                                        q)));
                            }

                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                apply();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                apply();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                apply();
                            }
                        });
    }

    // ── Ô tìm kiếm ────────────────────────────────────────────────
    /** Ô tìm kiếm có gợi ý mờ (vẽ đè, không chèn chữ giả vào nội dung). */
    protected JTextField searchBar(String placeholder) {
        JTextField f =
                new JTextField() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (getText().isEmpty() && !isFocusOwner()) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(
                                    RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            g2.setColor(AppColors.DISABLED);
                            g2.setFont(getFont());
                            Insets in = getInsets();
                            g2.drawString(
                                    placeholder,
                                    in.left,
                                    (getHeight()
                                                    + g2.getFontMetrics().getAscent()
                                                    - g2.getFontMetrics().getDescent())
                                            / 2);
                            g2.dispose();
                        }
                    }
                };
        f.setFont(AppColors.font(Font.PLAIN, 13));
        f.setForeground(AppColors.TEXT_DARK);
        f.setPreferredSize(new Dimension(280, 36));
        f.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
                        new EmptyBorder(4, 12, 4, 12)));
        f.getAccessibleContext().setAccessibleName(placeholder);
        f.addFocusListener(
                new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        f.repaint();
                    }

                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        f.repaint();
                    }
                });
        if (primarySearch == null) primarySearch = f;
        unboundSearch = f;
        return f;
    }

    // ── Huy hiệu / thẻ ────────────────────────────────────────────
    protected JLabel badge(String text, Color bg, Color fg) {
        JLabel l =
                new JLabel(text) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bg);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
        l.setFont(AppColors.font(Font.BOLD, 11));
        l.setForeground(fg);
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        l.setOpaque(false);
        return l;
    }

    protected RoundedPanel card(LayoutManager layout) {
        RoundedPanel p = new RoundedPanel(12, AppColors.CARD_BG, true);
        p.setLayout(layout);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));
        return p;
    }

    protected JPanel hbox(Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        for (Component c : comps) p.add(c);
        return p;
    }

    protected JPanel transparent(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    /** Trạng thái rỗng thân thiện: tiêu đề, giải thích và một nút gợi ý bước tiếp theo. */
    protected JPanel emptyState(String title, String message, JButton action) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(AppColors.font(Font.BOLD, 18));
        t.setForeground(AppColors.TEXT_DARK);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel m = new JLabel(message);
        m.setFont(AppColors.font(Font.PLAIN, 13));
        m.setForeground(AppColors.TEXT_GRAY);
        m.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(t);
        box.add(Box.createVerticalStrut(6));
        box.add(m);
        if (action != null) {
            action.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(Box.createVerticalStrut(16));
            box.add(action);
        }
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(box);
        return center;
    }
}
