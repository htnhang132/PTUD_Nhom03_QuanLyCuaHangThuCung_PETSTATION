package com.petstation.gui.components;

import com.petstation.gui.navigation.ScreenFlows;
import com.petstation.gui.navigation.ScreenId;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Thanh quy trình: hiển thị bước hiện tại của luồng nhiều bước (bán hàng, Spa, Hotel); bấm để
 * chuyển bước.
 */
public final class FlowBar extends JPanel {
    private final JLabel name = new JLabel();
    private final StepIndicator indicator = new StepIndicator(java.util.List.of(""));
    private final Consumer<ScreenId> navigator;
    private final JPanel holder = new JPanel(new BorderLayout());
    private ScreenFlows.Flow flow;
    private ScreenId currentScreen;
    private final StyledButton prev = new StyledButton("◀ Bước trước", StyledButton.Variant.GHOST);
    private final StyledButton next = new StyledButton("Bước tiếp ▶", StyledButton.Variant.PRIMARY);
    private final JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));

    public FlowBar(Consumer<ScreenId> navigator) {
        super(new BorderLayout(12, 0));
        this.navigator = navigator;
        setBackground(Color.WHITE);
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER),
                        new EmptyBorder(0, 14, 0, 14)));
        name.setFont(AppColors.font(Font.BOLD, 12));
        name.setForeground(AppColors.BROWN);
        name.setPreferredSize(new Dimension(110, 40));
        add(name, BorderLayout.WEST);
        holder.setOpaque(false);
        add(holder, BorderLayout.CENTER);
        nav.setOpaque(false);
        nav.add(prev);
        nav.add(next);
        add(nav, BorderLayout.EAST);
        prev.addActionListener(
                e -> {
                    if (flow != null) flow.previous(currentScreen).ifPresent(navigator);
                });
        next.addActionListener(
                e -> {
                    if (flow == null) return;
                    if (flow.isLast(currentScreen)) navigator.accept(ScreenId.PAYMENT);
                    else flow.next(currentScreen).ifPresent(navigator);
                });
        setPreferredSize(new Dimension(0, 44));
        setVisible(false);
    }

    public void show(ScreenId screen) {
        Optional<ScreenFlows.Flow> found = ScreenFlows.find(screen);
        if (found.isEmpty()) {
            flow = null;
            setVisible(false);
            return;
        }
        ScreenFlows.Flow f = found.get();
        if (f != flow) {
            flow = f;
            holder.removeAll();
            StepIndicator fresh = new StepIndicator(f.labels());
            fresh.setOnClick(index -> navigator.accept(f.steps().get(index)));
            holder.add(fresh, BorderLayout.CENTER);
            name.setText(f.name());
            holder.putClientProperty("indicator", fresh);
        }
        StepIndicator current = (StepIndicator) holder.getClientProperty("indicator");
        current.setCurrent(f.indexOf(screen));
        currentScreen = screen;
        nav.setVisible(f.linear());
        prev.setVisible(f.previous(screen).isPresent());
        next.setText(f.isLast(screen) ? "Thanh toán ▶" : "Bước tiếp ▶");
        setVisible(true);
        revalidate();
        repaint();
    }
}
