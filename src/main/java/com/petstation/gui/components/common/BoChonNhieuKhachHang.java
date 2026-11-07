package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.service.DanhMucService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class BoChonNhieuKhachHang implements BoSoanThaoTruong {
    private final Supplier<List<String>> nguonLuaChon;
    private final JPanel pnlChinh = new JPanel(new BorderLayout(0, 6));
    private final JTextField txtTimKiem = new JTextField();
    private final JCheckBox chkChonTatCa = new JCheckBox("Chọn tất cả");
    private final JLabel lblDem = new JLabel("Đã chọn: 0");
    private final JPanel pnlDanhSach = new JPanel();
    private final JScrollPane scrCuon;
    private final Map<String, JCheckBox> mapHopKiem = new LinkedHashMap<>();
    private boolean duocBat = false;
    private boolean dangCapNhat = false;

    public BoChonNhieuKhachHang() {
        this(DanhMucService::layLuaChonKhachHang);
    }

    public BoChonNhieuKhachHang(Supplier<List<String>> nguonLuaChon) {
        this.nguonLuaChon = nguonLuaChon;
        pnlChinh.setOpaque(false);

        txtTimKiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtTimKiem.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(4, 8, 4, 8)));
        txtTimKiem.putClientProperty(
                "JTextField.placeholderText", "Tìm mã hoặc tên khách hàng...");
        txtTimKiem
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                locDanhSach();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                locDanhSach();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                locDanhSach();
                            }
                        });

        chkChonTatCa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        chkChonTatCa.setOpaque(false);
        chkChonTatCa.setBorder(new EmptyBorder(0, 2, 0, 0));
        chkChonTatCa.addActionListener(
                e -> {
                    if (dangCapNhat) return;
                    boolean chon = chkChonTatCa.isSelected();
                    dangCapNhat = true;
                    try {
                        for (JCheckBox chk : mapHopKiem.values()) {
                            if (chk.isVisible() && chk.isEnabled()) {
                                chk.setSelected(chon);
                            }
                        }
                    } finally {
                        dangCapNhat = false;
                    }
                    capNhatTrangThaiDem();
                });

        lblDem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblDem.setForeground(MauUngDung.VAN_BAN_XAM);

        JPanel pnlThanhTacVu = new JPanel(new BorderLayout(8, 0));
        pnlThanhTacVu.setOpaque(false);
        pnlThanhTacVu.add(chkChonTatCa, BorderLayout.WEST);
        pnlThanhTacVu.add(lblDem, BorderLayout.EAST);

        JPanel pnlDau = new JPanel(new BorderLayout(0, 4));
        pnlDau.setOpaque(false);
        pnlDau.add(txtTimKiem, BorderLayout.NORTH);
        pnlDau.add(pnlThanhTacVu, BorderLayout.SOUTH);

        pnlDanhSach.setLayout(new BoxLayout(pnlDanhSach, BoxLayout.Y_AXIS));
        pnlDanhSach.setBackground(Color.WHITE);
        pnlDanhSach.setBorder(new EmptyBorder(4, 6, 4, 6));

        scrCuon =
                new JScrollPane(
                        pnlDanhSach,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrCuon.setPreferredSize(new Dimension(320, 150));
        scrCuon.setMinimumSize(new Dimension(200, 120));
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.getVerticalScrollBar().setUnitIncrement(16);

        pnlChinh.add(pnlDau, BorderLayout.NORTH);
        pnlChinh.add(scrCuon, BorderLayout.CENTER);

        napDanhSach(Set.of());
    }

    private void napDanhSach(Set<String> daChon) {
        List<String> luaChon = new ArrayList<>();
        try {
            List<String> tuNguon = nguonLuaChon != null ? nguonLuaChon.get() : List.of();
            if (tuNguon != null) {
                luaChon.addAll(tuNguon);
            }
        } catch (RuntimeException ignored) {
        }
        for (String muc : daChon) {
            if (!muc.isBlank() && !luaChon.contains(muc)) {
                luaChon.add(muc);
            }
        }

        mapHopKiem.clear();
        pnlDanhSach.removeAll();
        for (String muc : luaChon) {
            JCheckBox chk = new JCheckBox(muc);
            chk.setName("khach:" + DanhMucService.layMaTuLuaChon(muc));
            chk.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            chk.setOpaque(false);
            chk.setBorder(new EmptyBorder(3, 2, 3, 2));
            chk.setToolTipText(muc);
            chk.setSelected(daChon.contains(muc));
            chk.setEnabled(duocBat);
            chk.addActionListener(e -> capNhatTrangThaiDem());
            mapHopKiem.put(muc, chk);
            pnlDanhSach.add(chk);
        }
        locDanhSach();
        capNhatTrangThaiDem();
    }

    private void locDanhSach() {
        String tuKhoa = boDau(txtTimKiem.getText().trim());
        for (Map.Entry<String, JCheckBox> entry : mapHopKiem.entrySet()) {
            boolean khop = tuKhoa.isEmpty() || boDau(entry.getKey()).contains(tuKhoa);
            entry.getValue().setVisible(khop);
        }
        pnlDanhSach.revalidate();
        pnlDanhSach.repaint();
        capNhatTrangThaiDem();
    }

    private void capNhatTrangThaiDem() {
        int tongDaChon = 0;
        int hienThi = 0;
        int hienThiDaChon = 0;
        for (JCheckBox chk : mapHopKiem.values()) {
            if (chk.isSelected()) {
                tongDaChon++;
            }
            if (chk.isVisible()) {
                hienThi++;
                if (chk.isSelected()) {
                    hienThiDaChon++;
                }
            }
        }
        lblDem.setText("Đã chọn: " + tongDaChon + "/" + mapHopKiem.size());
        dangCapNhat = true;
        try {
            chkChonTatCa.setSelected(hienThi > 0 && hienThiDaChon == hienThi);
        } finally {
            dangCapNhat = false;
        }
    }

    private static String boDau(String chuoi) {
        if (chuoi == null || chuoi.isEmpty()) return "";
        String chuan = Normalizer.normalize(chuoi, Normalizer.Form.NFD);
        return chuan.replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    @Override
    public JComponent layThanhPhan() {
        return pnlChinh;
    }

    @Override
    public String lay() {
        return mapHopKiem.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(";"));
    }

    @Override
    public void dat(String giaTri) {
        Set<String> daChon = new LinkedHashSet<>();
        if (giaTri != null && !giaTri.isBlank()) {
            for (String phan : giaTri.split(";")) {
                String cat = phan.trim();
                if (!cat.isEmpty()) {
                    daChon.add(cat);
                }
            }
        }
        txtTimKiem.setText("");
        napDanhSach(daChon);
    }

    @Override
    public void datDuocBat(boolean bat) {
        this.duocBat = bat;
        txtTimKiem.setEnabled(bat);
        chkChonTatCa.setEnabled(bat && !mapHopKiem.isEmpty());
        Color nen = bat ? Color.WHITE : new Color(245, 245, 245);
        pnlDanhSach.setBackground(nen);
        scrCuon.getViewport().setBackground(nen);
        for (JCheckBox chk : mapHopKiem.values()) {
            chk.setEnabled(bat);
        }
    }
}
