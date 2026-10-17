package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.FormPanel;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.RoundedPanel;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarCapabilities;
import com.petstation.gui.components.common.CustomerPicker;
import com.petstation.gui.components.common.HoldCartManager;
import com.petstation.gui.navigation.SaleContext;
import com.petstation.gui.navigation.ScreenId;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * Lập đơn hàng POS: tìm/quét sản phẩm, giỏ hàng chỉnh số lượng, khách hàng lookup, treo đơn, giảm giá linh hoạt, chuyển sang thanh toán.
 */
public class POSPanel extends BasePanel {

    private record Product(String code, String name, String category, long price, int stock) {}

    /** Sản phẩm đang kinh doanh lấy từ Danh mục (giá và tồn luôn mới nhất). */
    private static List<Product> products() {
        List<Product> out = new ArrayList<>();
        for (Object[] r : com.petstation.gui.data.DemoStore.products()) {
            if (!"Đang kinh doanh".equals(r[9])) continue;
            out.add(new Product(String.valueOf(r[0]), String.valueOf(r[1]), String.valueOf(r[2]),
                Money.parse(String.valueOf(r[4])), (int) Math.max(0, Money.parse(String.valueOf(r[8])))));
        }
        return out;
    }
    private static final String[] CATEGORIES = {"Tất cả", "Thức ăn", "Phụ kiện", "Grooming", "Vệ sinh", "Thuốc"};

    private final SaleContext sale;
    private final Consumer<ScreenId> navigator;
    private final JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
    private final JPanel cartRows = new JPanel();
    private final JLabel subtotalLabel = new JLabel();
    private final JLabel discountLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();
    private final JLabel emptyCart = new JLabel("Chưa có sản phẩm. Bấm vào thẻ sản phẩm hoặc quét mã.");
    private final JTextField search;
    private final CustomerPicker picker;
    private final JLabel promoLabel = new JLabel("—");
    private final HoldCartManager holdCartManager;
    private final StyledButton payButton = new StyledButton("Thanh toán (F9)", StyledButton.Variant.PRIMARY);
    private final StyledButton holdButton = new StyledButton("Lưu đơn nháp (F7)", StyledButton.Variant.SECONDARY);
    private final StyledButton retrieveButton = new StyledButton("Mở đơn nháp (F8)", StyledButton.Variant.GHOST);
    private final List<SaleContext.Line> cart = new ArrayList<>();
    private final Deque<String> addHistory = new ArrayDeque<>();
    private String category = "Tất cả";

    public POSPanel(SaleContext sale, Consumer<ScreenId> navigator) {
        this.sale = sale;
        this.navigator = navigator;
        this.picker = new CustomerPicker(sale, this::updateTotals);
        this.holdCartManager = new HoldCartManager();

        setBorder(new EmptyBorder(14, 16, 14, 16));
        setLayout(new BorderLayout(16, 0));

        search = searchBar("Quét mã vạch hoặc gõ tên / mã sản phẩm, rồi Enter");
        search.setPreferredSize(new Dimension(0, 40));
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { rebuildGrid(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { rebuildGrid(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { rebuildGrid(); }
        });
        search.addActionListener(e -> addByScan());

        JPanel left = transparent(new BorderLayout(0, 10));
        JPanel top = transparent(new BorderLayout(0, 8));
        top.add(search, BorderLayout.NORTH);
        top.add(categoryChips(), BorderLayout.SOUTH);
        left.add(top, BorderLayout.NORTH);
        grid.setOpaque(false);
        JPanel gridHolder = transparent(new BorderLayout());
        gridHolder.add(grid, BorderLayout.NORTH);
        JScrollPane gridScroll = new JScrollPane(gridHolder);
        gridScroll.setBorder(null);
        gridScroll.setOpaque(false);
        gridScroll.getViewport().setOpaque(false);
        gridScroll.getVerticalScrollBar().setUnitIncrement(18);
        left.add(gridScroll, BorderLayout.CENTER);

        add(left, BorderLayout.CENTER);
        add(buildCart(), BorderLayout.EAST);

        rebuildGrid();
        refreshCart();

        // Register keyboard shortcuts
        registerShortcut("F9", "pay", e -> checkout());
        registerShortcut("F7", "hold", e -> holdCurrentCart());
        registerShortcut("F8", "retrieve", e -> showHoldCarts());
        registerShortcut("Ctrl+K", "search", e -> search.requestFocusInWindow());
    }

    private void registerShortcut(String key, String name, java.awt.event.ActionListener action) {
        KeyStroke ks = KeyStroke.getKeyStroke(key);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(ks, name);
        getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { action.actionPerformed(e); }
        });
    }

    // ── Bên trái: lưới sản phẩm ───────────────────────────────────
    private JPanel categoryChips() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (String name : CATEGORIES) {
            JToggleButton chip = new JToggleButton(name);
            chip.setFont(AppColors.font(Font.PLAIN, 12));
            chip.setFocusPainted(false);
            chip.setBackground(Color.WHITE);
            chip.setSelected(name.equals(category));
            chip.addActionListener(e -> { category = name; rebuildGrid(); });
            group.add(chip);
            row.add(chip);
        }
        return row;
    }

    private void rebuildGrid() {
        grid.removeAll();
        String q = search.getText().trim().toLowerCase();
        for (Product p : products()) {
            boolean inCategory = category.equals("Tất cả") || p.category().equals(category);
            boolean matches = q.isEmpty() || p.name().toLowerCase().contains(q) || p.code().toLowerCase().contains(q);
            if (inCategory && matches) grid.add(productCard(p));
        }
        if (grid.getComponentCount() == 0) {
            JLabel none = new JLabel("Không tìm thấy sản phẩm phù hợp.");
            none.setForeground(AppColors.TEXT_GRAY);
            grid.add(none);
        }
        while (grid.getComponentCount() % 3 != 0 && grid.getComponentCount() > 0) grid.add(Box.createGlue());
        grid.revalidate();
        grid.repaint();
    }

    private JPanel productCard(Product p) {
        boolean out = p.stock() <= 0;
        RoundedPanel card = new RoundedPanel(10, AppColors.CARD_BG, true);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setPreferredSize(new Dimension(150, 112));
        card.setCursor(out ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.getAccessibleContext().setAccessibleName(p.name());

        JLabel code = new JLabel(p.code() + "· " + p.category());
        code.setFont(AppColors.font(Font.PLAIN, 11));
        code.setForeground(AppColors.TEXT_GRAY);
        JLabel name = new JLabel("<html>" + p.name() + "</html>");
        name.setFont(AppColors.font(Font.BOLD, 13));
        name.setForeground(out ? AppColors.DISABLED : AppColors.TEXT_DARK);
        JPanel bottom = transparent(new BorderLayout());
        JLabel price = new JLabel(Money.format(p.price()));
        price.setFont(AppColors.font(Font.BOLD, 14));
        price.setForeground(out ? AppColors.DISABLED : AppColors.RED_BRAND);
        JLabel stock = new JLabel(out ? "Hết hàng" : p.stock() <= 5 ? "Còn " + p.stock() : "Tồn " + p.stock());
        stock.setFont(AppColors.font(Font.BOLD, 11));
        stock.setForeground(out ? AppColors.DANGER_TEXT : p.stock() <= 5 ? AppColors.ORANGE : AppColors.TEXT_GRAY);
        bottom.add(price, BorderLayout.WEST);
        bottom.add(stock, BorderLayout.EAST);

        card.add(code, BorderLayout.NORTH);
        card.add(name, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        if (!out) {
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { addToCart(p); }
            });
        }
        return card;
    }

    private void addByScan() {
        String q = search.getText().trim().toLowerCase();
        if (q.isEmpty()) return;
        List<Product> found = products().stream()
            .filter(p -> p.code().equalsIgnoreCase(q) || p.name().toLowerCase().contains(q)).toList();
        if (found.size() == 1) {
            if (found.get(0).stock() <= 0) {
                Dialogs.showMessageDialog(this, found.get(0).name() + " đã hết hàng.", "Hết hàng", JOptionPane.WARNING_MESSAGE);
            } else {
                addToCart(found.get(0));
                search.setText("");
            }
        } else if (found.isEmpty()) {
            Dialogs.showMessageDialog(this, "Không có sản phẩm mã hoặc tên \"" + search.getText().trim() + "\".", "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Bên phải: giỏ hàng ────────────────────────────────────────
    private RoundedPanel buildCart() {
        RoundedPanel cartPanel = card(new BorderLayout(0, 10));
        cartPanel.setPreferredSize(new Dimension(460, 0));

        // Header with title and hold/retrieve buttons
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.add(sectionTitle("Giỏ hàng"), BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerActions.setOpaque(false);
        holdButton.setPreferredSize(new Dimension(150, 32));
        holdButton.setFont(AppColors.font(Font.BOLD, 11));
        retrieveButton.setPreferredSize(new Dimension(130, 32));
        retrieveButton.setFont(AppColors.font(Font.PLAIN, 11));
        headerActions.add(holdButton);
        headerActions.add(retrieveButton);
        header.add(headerActions, BorderLayout.EAST);
        cartPanel.add(header, BorderLayout.NORTH);

        cartRows.setLayout(new BoxLayout(cartRows, BoxLayout.Y_AXIS));
        cartRows.setOpaque(false);
        emptyCart.setForeground(AppColors.TEXT_GRAY);
        emptyCart.setFont(AppColors.font(Font.PLAIN, 12));
        JPanel rowsHolder = new TrackingWidthPanel();
        rowsHolder.setOpaque(false);
        rowsHolder.add(cartRows, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(rowsHolder);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cartPanel.add(scroll, BorderLayout.CENTER);

        // Bottom section
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Khách hàng: một ô tìm duy nhất, chọn xong hiện hạng, điểm và ô dùng điểm
        bottom.add(picker);
        bottom.add(Box.createVerticalStrut(8));

        // Totals
        bottom.add(sumRow("Tạm tính", subtotalLabel, false));
        bottom.add(sumRow("Khuyến mãi tự áp", promoLabel, false));
        bottom.add(sumRow("Giảm giá (KM + điểm)", discountLabel, false));
        bottom.add(sumRow("Tổng cộng", totalLabel, true));
        bottom.add(Box.createVerticalStrut(10));

        // Action buttons
        StyledButton clearBtn = new StyledButton("Xóa giỏ", StyledButton.Variant.DANGER);
        clearBtn.addActionListener(e -> clearCart(true));
        payButton.addActionListener(e -> checkout());

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setOpaque(false);
        buttons.add(clearBtn);
        buttons.add(payButton);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        payButton.setPreferredSize(new Dimension(150, 44));
        bottom.add(buttons);
        cartPanel.add(bottom, BorderLayout.SOUTH);

        // Set up hold cart actions
        holdButton.addActionListener(e -> holdCurrentCart());
        retrieveButton.addActionListener(e -> showHoldCarts());

        return cartPanel;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.font(Font.PLAIN, 11));
        l.setForeground(AppColors.TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel sumRow(String label, JLabel value, boolean strong) {
        JPanel row = transparent(new BorderLayout());
        JLabel l = new JLabel(label);
        l.setFont(AppColors.font(strong ? Font.BOLD : Font.PLAIN, strong ? 15 : 13));
        value.setFont(AppColors.font(Font.BOLD, strong ? 22 : 13));
        value.setForeground(strong ? AppColors.RED_BRAND : AppColors.TEXT_DARK);
        row.add(l, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, strong ? 36 : 24));
        return row;
    }

    private void addToCart(Product p) {
        for (int i = 0; i < cart.size(); i++) {
            SaleContext.Line l = cart.get(i);
            if (l.code().equals(p.code())) {
                if (l.quantity() >= p.stock()) {
                    Dialogs.showMessageDialog(this, "Chỉ còn " + p.stock() + " " + p.name() + " trong kho.", "Vượt tồn kho", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                cart.set(i, new SaleContext.Line(l.code(), l.name(), l.unitPrice(), l.quantity() + 1));
                addHistory.push(p.code());
                refreshCart();
                return;
            }
        }
        cart.add(new SaleContext.Line(p.code(), p.name(), p.price(), 1));
        addHistory.push(p.code());
        refreshCart();
    }

    private void changeQuantity(String code, int delta) {
        for (int i = 0; i < cart.size(); i++) {
            SaleContext.Line l = cart.get(i);
            if (!l.code().equals(code)) continue;
            int stock = products().stream().filter(p -> p.code().equals(code)).mapToInt(Product::stock).findFirst().orElse(Integer.MAX_VALUE);
            int qty = l.quantity() + delta;
            if (qty > stock) return;
            if (qty <= 0) cart.remove(i);
            else cart.set(i, new SaleContext.Line(l.code(), l.name(), l.unitPrice(), qty));
            break;
        }
        refreshCart();
    }

    private void refreshCart() {
        cartRows.removeAll();
        if (cart.isEmpty()) {
            JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
            holder.setOpaque(false);
            holder.add(emptyCart);
            cartRows.add(holder);
        }
        for (SaleContext.Line line : cart) cartRows.add(cartRow(line));
        cartRows.revalidate();
        cartRows.repaint();
        sale.setLines(cart);
        updateTotals();
    }

    private JPanel cartRow(SaleContext.Line line) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER), new EmptyBorder(8, 10, 8, 10)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel info = transparent(new BorderLayout());
        JLabel name = new JLabel(line.name());
        name.setFont(AppColors.font(Font.PLAIN, 13));
        JLabel unit = new JLabel(Money.format(line.unitPrice()));
        unit.setFont(AppColors.font(Font.PLAIN, 11));
        unit.setForeground(AppColors.TEXT_GRAY);
        info.add(name, BorderLayout.NORTH);
        info.add(unit, BorderLayout.SOUTH);

        JPanel qty = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        qty.setOpaque(false);
        JButton minus = qtyButton("−", "Giảm số lượng " + line.name());
        JButton plus = qtyButton("+", "Tăng số lượng " + line.name());
        minus.addActionListener(e -> changeQuantity(line.code(), -1));
        plus.addActionListener(e -> changeQuantity(line.code(), +1));
        JLabel count = new JLabel(String.valueOf(line.quantity()), SwingConstants.CENTER);
        count.setFont(AppColors.font(Font.BOLD, 13));
        count.setPreferredSize(new Dimension(24, 26));
        qty.add(minus);
        qty.add(count);
        qty.add(plus);

        JLabel amount = new JLabel(Money.format(line.amount()), SwingConstants.RIGHT);
        amount.setFont(AppColors.font(Font.BOLD, 13));
        amount.setPreferredSize(new Dimension(84, 20));

        row.add(info, BorderLayout.CENTER);
        JPanel right = transparent(new BorderLayout(6, 0));
        right.add(qty, BorderLayout.WEST);
        right.add(amount, BorderLayout.EAST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JButton qtyButton(String text, String accessibleName) {
        JButton b = new JButton(text);
        b.setFont(AppColors.font(Font.BOLD, 14));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setPreferredSize(new Dimension(26, 26));
        b.setFocusPainted(false);
        b.setBackground(AppColors.SURFACE_ALT);
        b.getAccessibleContext().setAccessibleName(accessibleName);
        return b;
    }

    private void updateTotals() {
        subtotalLabel.setText(Money.format(sale.subtotal()));
        discountLabel.setText(sale.discount() == 0 ? "0₫" : "−" + Money.format(sale.discount()));
        totalLabel.setText(Money.format(sale.total()));
        SaleContext.Promotion promo = sale.bestPromotion();
        promoLabel.setText(promo == null ? "—" : promo.name() + ": −" + Money.format(promo.amount()));
        promoLabel.setToolTipText(promo == null ? "Chưa có khuyến mãi phù hợp" : promo.code() + " - " + promo.name());
        payButton.setEnabled(!cart.isEmpty());
    }

    private void clearCart(boolean confirm) {
        if (cart.isEmpty()) return;
        if (confirm && Dialogs.showConfirmDialog(this, "Xóa toàn bộ " + cart.size() + " dòng trong giỏ hàng?", "Xóa giỏ hàng",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        cart.clear();
        addHistory.clear();
        sale.clear();
        picker.clear();
        refreshCart();
    }

    private void checkout() {
        updateTotals();
        navigator.accept(ScreenId.PAYMENT);
    }

    private void holdCurrentCart() {
        if (cart.isEmpty()) {
            Dialogs.showMessageDialog(this, "Giỏ hàng trống, chưa có gì để lưu nháp.", "Đơn nháp", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String name = JOptionPane.showInputDialog(this,
            "Đặt tên cho đơn nháp (ví dụ: Khách đi lấy ví, Khách số 3...):",
            "Lưu đơn nháp",
            JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            holdCartManager.holdCart(new ArrayList<>(cart), name.trim());
            clearCart(false);
            Dialogs.showMessageDialog(this, "Đã lưu đơn nháp: " + name.trim(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showHoldCarts() {
        HoldCartManager.HeldCart held = holdCartManager.showSelectDialog(this);
        if (held != null) {
            // Restore the held cart
            cart.clear();
            for (Object item : held.getItems()) {
                if (item instanceof SaleContext.Line line) {
                    cart.add(line);
                }
            }
            picker.clear();
            refreshCart();
        }
    }

    // ── Thanh công cụ ─────────────────────────────────────────────
    @Override
    public ToolbarCapabilities toolbarCapabilities() {
        return new ToolbarCapabilities(true, false, true, false, true, true);
    }

    @Override public void onNew() { clearCart(true); }

    @Override
    public void onUndo() {
        if (addHistory.isEmpty()) return;
        String code = addHistory.pop();
        for (int i = 0; i < cart.size(); i++) {
            SaleContext.Line l = cart.get(i);
            if (!l.code().equals(code)) continue;
            if (l.quantity() <= 1) cart.remove(i);
            else cart.set(i, new SaleContext.Line(l.code(), l.name(), l.unitPrice(), l.quantity() - 1));
            break;
        }
        refreshCart();
    }

    @Override
    public void onShow() {
        // Sau khi thanh toán xong giỏ hàng của SaleContext đã được xóa: đồng bộ lại.
        if (sale.isEmpty() && !cart.isEmpty()) {
            cart.clear();
            addHistory.clear();
            picker.sync();
            refreshCart();
        }
        search.requestFocusInWindow();
    }

    @Override
    protected String helpText() {
        return "Quét mã vạch hoặc gõ mã/tên rồi Enter để thêm vào giỏ.\n" +
               "Bấm thẻ sản phẩm để thêm; dùng − / + để đổi số lượng.\n" +
               "Nhập SĐT khách hàng -> Enter để lookup (tự hiện tên, hạng, điểm).\n" +
               "Giảm giá: chọn % hoặc số tiền (VD: 50000đ).\n" +
               "F7: Treo đơn; F8: Mở đơn treo; F9: Thanh toán; Ctrl+Z: Bỏ SP vừa thêm; Ctrl+K: Tìm SP.";
    }

    @Override protected String helpTitle() { return "Lập đơn hàng"; }

    /** Panel luôn co theo bề rộng khung cuộn, tránh thanh cuộn ngang. */
    private static final class TrackingWidthPanel extends JPanel implements Scrollable {
        TrackingWidthPanel() { super(new BorderLayout()); }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 64; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}