package com.petstation.gui.components;

import com.petstation.gui.navigation.CauTrucMenu;
import com.petstation.gui.navigation.DangKyManHinh;
import com.petstation.gui.navigation.DinhNghiaManHinh;
import com.petstation.model.MaManHinh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/** Cung cấp giao diện và xử lý tương tác cho màn hình thanh menu. */
public final class ThanhMenuChinh extends JPanel {
    /** Bộ đăng ký. */
    private final DangKyManHinh boDangKy;

    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Đăng xuất. */
    private final Runnable dangXuat;

    /** Thoát. */
    private final Runnable thoat;

    /** Menu các nút. */
    private final Map<Character, JButton> menuCacNut = new LinkedHashMap<>();

    /** Các menu bật lên. */
    private final Map<Character, JPopupMenu> cacMenuBatLen = new LinkedHashMap<>();

    public ThanhMenuChinh(
            DangKyManHinh boDangKy,
            Consumer<MaManHinh> boDieuHuong,
            Runnable dangXuat,
            Runnable thoat) {
        this.boDangKy = boDangKy;
        this.boDieuHuong = boDieuHuong;
        this.dangXuat = dangXuat;
        this.thoat = thoat;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(0, 30));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));

        for (CauTrucMenu.Nhom nhom : CauTrucMenu.layCacNhom()) {
            themMenu(nhom.phimTruyCap(), nhom.nhan(), nhom.cacMuc());
        }
    }

    public void moMenu(char phimTruyCap) {
        // Nút.
        JButton btnNut = menuCacNut.get(Character.toUpperCase(phimTruyCap));
        // Menu bật lên.
        JPopupMenu pmnMenuBatLen = cacMenuBatLen.get(Character.toUpperCase(phimTruyCap));
        if (btnNut != null && pmnMenuBatLen != null && btnNut.isEnabled()) {
            pmnMenuBatLen.show(btnNut, 0, btnNut.getHeight());
        }
    }

    private void themMenu(char phimTruyCap, String nhan, List<CauTrucMenu.Muc> cacMuc) {
        // Gạch dưới.
        int gachDuoi = Math.max(0, nhan.toLowerCase().indexOf(Character.toLowerCase(phimTruyCap)));
        // Nút.
        JButton btnNut =
                new JButton(
                        "<html>"
                                + nhan.substring(0, gachDuoi)
                                + "<u>"
                                + nhan.charAt(gachDuoi)
                                + "</u>"
                                + nhan.substring(gachDuoi + 1)
                                + "</html>") {
                    /**
                     * Vẽ thành phần trong lớp xử lý giao diện.
                     *
                     * @param doHoa đồ họa
                     */
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        if (getModel().isRollover()) {
                            doHoa.setColor(MauUngDung.DO_NHAT);
                            doHoa.fillRect(0, 0, getWidth(), getHeight());
                        }
                        super.paintComponent(doHoa);
                    }
                };
        btnNut.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        btnNut.setForeground(MauUngDung.VAN_BAN_TOI);
        btnNut.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnNut.setOpaque(false);
        btnNut.setContentAreaFilled(false);
        btnNut.setFocusPainted(false);
        btnNut.setRolloverEnabled(true);

        // Menu bật lên.
        JPopupMenu pmnMenuBatLen = new JPopupMenu();
        for (CauTrucMenu.Muc muc : cacMuc) {
            if (muc.kiemTraPhanCach()) {
                if (pmnMenuBatLen.getComponentCount() > 0
                        && !(pmnMenuBatLen.getComponent(pmnMenuBatLen.getComponentCount() - 1)
                                instanceof JPopupMenu.Separator)) {
                    pmnMenuBatLen.addSeparator();
                }
                continue;
            }
            // Định nghĩa.
            DinhNghiaManHinh dinhNghia = boDangKy.layDinhNghia(muc.manHinh());
            if (!boDangKy.coTheTruyCap(muc.manHinh())) continue;
            // Mục.
            JMenuItem btnMuc = new JMenuItem(muc.nhan());
            btnMuc.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            btnMuc.setEnabled(dinhNghia.daTrienKhai());
            btnMuc.setToolTipText(
                    dinhNghia.daTrienKhai()
                            ? dinhNghia.goiY()
                            : "Chức năng sẽ được triển khai ở giai đoạn tiếp theo");
            if (muc.phimTat() != null) {
                btnMuc.setAccelerator(docPhimTat(muc.phimTat()));
            }
            btnMuc.addActionListener(suKien -> boDieuHuong.accept(muc.manHinh()));
            pmnMenuBatLen.add(btnMuc);
        }
        catCuoiPhanCach(pmnMenuBatLen);
        if (phimTruyCap == 'H') {
            if (pmnMenuBatLen.getComponentCount() > 0) pmnMenuBatLen.addSeparator();
            // Đăng xuất mục.
            JMenuItem btnDangXuatMuc = new JMenuItem("Đăng xuất");
            btnDangXuatMuc.addActionListener(suKien -> dangXuat.run());
            pmnMenuBatLen.add(btnDangXuatMuc);
            // Thoát mục.
            JMenuItem btnThoatMuc = new JMenuItem("Thoát");
            btnThoatMuc.addActionListener(suKien -> thoat.run());
            pmnMenuBatLen.add(btnThoatMuc);
        }

        btnNut.setEnabled(pmnMenuBatLen.getComponentCount() > 0);
        btnNut.getAccessibleContext().setAccessibleName("Menu " + nhan);
        btnNut.addActionListener(suKien -> pmnMenuBatLen.show(btnNut, 0, btnNut.getHeight()));
        menuCacNut.put(phimTruyCap, btnNut);
        cacMenuBatLen.put(phimTruyCap, pmnMenuBatLen);
        add(btnNut);
    }

    private void catCuoiPhanCach(JPopupMenu pmnMenuBatLen) {
        // Số phần tử.
        int soPhanTu = pmnMenuBatLen.getComponentCount();
        if (soPhanTu > 0
                && pmnMenuBatLen.getComponent(soPhanTu - 1) instanceof JPopupMenu.Separator)
            pmnMenuBatLen.remove(soPhanTu - 1);
    }

    private KeyStroke docPhimTat(String phimTat) {
        return KeyStroke.getKeyStroke(phimTat);
    }
}
