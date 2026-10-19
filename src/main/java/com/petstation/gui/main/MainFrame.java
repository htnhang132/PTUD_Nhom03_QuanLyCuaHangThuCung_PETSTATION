package com.petstation.gui.main;

import static com.petstation.service.SessionService.*;

import com.petstation.ApplicationContext;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.DesktopMenuBar;
import com.petstation.gui.components.DesktopStatusBar;
import com.petstation.gui.components.DesktopTitleBar;
import com.petstation.gui.components.DesktopToolBar;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.FlowBar;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.components.WindowResizer;
import com.petstation.gui.navigation.ScreenDefinition;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.navigation.ScreenRegistry;
import com.petstation.gui.system.LoginFrame;
import com.petstation.model.UserSession;
import com.petstation.service.AuditService;
import com.petstation.service.AuthorizationException;
import com.petstation.service.SessionService;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public final class MainFrame extends JFrame {
    private static final int RESIZE_EDGE = 5;
    private static final int MAX_HISTORY = 30;

    private final ApplicationContext context;
    private final UserSession session;
    private final CardLayout screenLayout = new CardLayout();
    private final JPanel workArea = new JPanel(screenLayout);
    private final Map<ScreenId, JPanel> screenCache = new EnumMap<>(ScreenId.class);
    private final ScreenRegistry screenRegistry;
    private final DesktopToolBar toolBar;
    private final DesktopStatusBar statusBar;
    private final DesktopMenuBar menuBar;
    private final FlowBar flowBar;
    private final JPanel root = new JPanel(new BorderLayout());
    private final Deque<ScreenId> history = new ArrayDeque<>();
    private ScreenId current;

    public MainFrame(ApplicationContext context) {
        this.context = context;
        this.session = context.sessionContext().requireCurrent();
        SessionService.setSession(session);
        this.screenRegistry = new ScreenRegistry(session, this::navigate);

        setTitle("Pet Station – " + session.role().displayName());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 680));
        setMaximizedBounds(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        setLocationRelativeTo(null);

        toolBar = new DesktopToolBar(() -> navigate(ScreenId.DASHBOARD), this::goBack);
        statusBar = new DesktopStatusBar(session, screenRegistry.shift());
        flowBar = new FlowBar(this::navigate);
        menuBar =
                new DesktopMenuBar(screenRegistry, this::navigate, this::logout, this::requestExit);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(new DesktopTitleBar(this, this::navigate, this::requestExit));
        top.add(menuBar);
        top.add(toolBar);
        top.add(flowBar);

        workArea.setBackground(AppColors.BG_CREAM);

        // Viền 5px là vùng kéo đổi kích thước (cửa sổ không dùng viền hệ thống).
        root.setBackground(new Color(0x8C, 0x7A, 0x6E));
        root.setBorder(
                BorderFactory.createEmptyBorder(
                        RESIZE_EDGE, RESIZE_EDGE, RESIZE_EDGE, RESIZE_EDGE));
        WindowResizer.install(this, root);
        addWindowStateListener(
                event -> {
                    boolean maximized = (event.getNewState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
                    int edge = maximized ? 0 : RESIZE_EDGE;
                    root.setBorder(BorderFactory.createEmptyBorder(edge, edge, edge, edge));
                });
        root.add(top, BorderLayout.NORTH);
        root.add(workArea, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);
        setContentPane(root);

        registerWindowShortcuts();
        addWindowListener(
                new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent event) {
                        requestExit();
                    }
                });

        screenRegistry
                .shift()
                .addListener(
                        () -> {
                            if (screenRegistry.shift().isOpen() && pendingAfterShift != null) {
                                ScreenId target = pendingAfterShift;
                                pendingAfterShift = null;
                                screenRegistry.shift().setPendingTitle("");
                                SwingUtilities.invokeLater(() -> navigate(target));
                            }
                        });

        AuditService.setUser(session.displayName());
        AuditService.log("Đăng nhập", "Hệ thống");
        navigate(ScreenId.DASHBOARD);
    }

    public void navigate(ScreenId id) {
        ScreenDefinition definition = screenRegistry.definition(id);
        if (definition == null) return;
        try {
            if (definition.permission() != null) {
                context.authorizationService().require(definition.permission());
            }
        } catch (AuthorizationException exception) {
            Dialogs.showMessageDialog(
                    this, exception.getMessage(), "Không đủ quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (screenRegistry.requiresOpenShift(id) && !screenRegistry.shift().isOpen()) {
            promptOpenShift(id, definition);
            return;
        }

        if (id != ScreenId.CASH_SHIFT) {
            pendingAfterShift = null;
            screenRegistry.shift().setPendingTitle("");
        }
        try {
            show(id, definition, true);
        } catch (RuntimeException exception) {
            Dialogs.error(this, "Không tải được dữ liệu: " + exception.getMessage());
        }
    }

    private ScreenId pendingAfterShift;

    private void promptOpenShift(ScreenId targetId, ScreenDefinition target) {
        if (!screenRegistry.canAccess(ScreenId.CASH_SHIFT)) {
            Dialogs.warn(
                    this,
                    "Chưa có ca làm việc đang mở nên chưa thể dùng \""
                            + target.title()
                            + "\".\nHãy nhờ Cửa hàng trưởng mở ca.");
            return;
        }
        // Đi thẳng tới màn Mở ca (không hỏi lại) và nhớ chức năng đang muốn vào để tự quay về sau
        // khi mở ca.
        pendingAfterShift = targetId;
        screenRegistry.shift().setPendingTitle(target.title());
        show(ScreenId.CASH_SHIFT, screenRegistry.definition(ScreenId.CASH_SHIFT), true);
    }

    private void show(ScreenId id, ScreenDefinition definition, boolean recordHistory) {
        if (recordHistory && current != null && current != id) {
            history.push(current);
            while (history.size() > MAX_HISTORY) history.removeLast();
        }
        current = id;
        JPanel screen =
                screenCache.computeIfAbsent(
                        id,
                        key -> {
                            JPanel created = screenRegistry.create(key);
                            workArea.add(created, key.name());
                            return created;
                        });
        screenLayout.show(workArea, id.name());
        if (screen instanceof ToolbarAware aware) aware.onShow();
        toolBar.setActivePanel(screen);
        toolBar.setCanGoBack(!history.isEmpty());
        flowBar.show(id);
        statusBar.setHint(definition.hint());
        setTitle("Pet Station – " + definition.title());
    }

    private void goBack() {
        while (!history.isEmpty()) {
            ScreenId previous = history.pop();
            ScreenDefinition definition = screenRegistry.definition(previous);
            if (definition != null && screenRegistry.canAccess(previous)) {
                show(previous, definition, false);
                return;
            }
        }
        toolBar.setCanGoBack(false);
    }

    private void registerWindowShortcuts() {
        JRootPane root = getRootPane();
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_DOWN_MASK),
                "home",
                () -> navigate(ScreenId.DASHBOARD));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK),
                "back",
                this::goBack);
        bind(root, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "pos", () -> navigate(ScreenId.POS));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
                "orders",
                () -> navigate(ScreenId.ORDERS));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),
                "pay",
                () -> navigate(ScreenId.PAYMENT));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
                "new",
                () -> toolBar.trigger(DesktopToolBar.Action.NEW));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                "save",
                () -> toolBar.trigger(DesktopToolBar.Action.SAVE));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
                "find",
                () -> toolBar.trigger(DesktopToolBar.Action.FIND));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "print",
                () -> toolBar.trigger(DesktopToolBar.Action.PRINT));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
                "undo",
                () -> toolBar.trigger(DesktopToolBar.Action.UNDO));
        bind(
                root,
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                "help",
                () -> toolBar.trigger(DesktopToolBar.Action.HELP));

        bindMenu(root, KeyEvent.VK_H, 'H');
        bindMenu(root, KeyEvent.VK_D, 'D');
        bindMenu(root, KeyEvent.VK_T, 'T');
        bindMenu(root, KeyEvent.VK_X, 'X');
        bindMenu(root, KeyEvent.VK_K, 'K');
    }

    private void bindMenu(JRootPane root, int keyCode, char mnemonic) {
        bind(
                root,
                KeyStroke.getKeyStroke(keyCode, KeyEvent.ALT_DOWN_MASK),
                "menu-" + mnemonic,
                () -> menuBar.openMenu(mnemonic));
    }

    private void bind(JRootPane root, KeyStroke keyStroke, String name, Runnable command) {
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, name);
        root.getActionMap()
                .put(
                        name,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                command.run();
                            }
                        });
    }

    private void logout() {
        int result =
                Dialogs.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            AuditService.log("Đăng xuất", "Hệ thống");
            context.sessionContext().clear();
            SessionService.setSession(null);
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(context).setVisible(true));
        }
    }

    private void requestExit() {
        int result =
                Dialogs.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn thoát Pet Station?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
