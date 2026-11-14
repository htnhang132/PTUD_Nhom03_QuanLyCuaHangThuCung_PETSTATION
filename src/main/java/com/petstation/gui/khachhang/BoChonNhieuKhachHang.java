package com.petstation.gui.khachhang;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BoSoanThaoTruong;
import com.petstation.service.chung.DanhMucService;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
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

public final class BoChonNhieuKhachHang implements BoSoanThaoTruong {
    private final Supplier<List<String>> nguonLuaChon;
    private final JPanel khungChinh = new JPanel(new BorderLayout(0, 6));
    private final JTextField oTimKiem = new JTextField();
    private final JCheckBox chkChonTatCa = new JCheckBox("Chọn tất cả");
    private final JLabel nhanDem = new JLabel("Đã chọn: 0");
    private final JPanel khungDanhSach = new JPanel();
    private final JScrollPane scrCuon;
    private final Map<String, JCheckBox> mapHopKiem = new LinkedHashMap<>();
    private List<String> danhSachNguon;
    private Set<String> choChon = Set.of();
    private int luotTai;
    private boolean duocBat = false;
    private boolean dangCapNhat = false;

    public BoChonNhieuKhachHang() {
        this(() -> DanhMucService.macDinh().layLuaChonKhachHang());
    }

    public BoChonNhieuKhachHang(Supplier<List<String>> nguonLuaChon) {
        this.nguonLuaChon = nguonLuaChon;
        khungChinh.setOpaque(false);
        oTimKiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        oTimKiem.setPreferredSize(new Dimension(0, 32));
        oTimKiem.putClientProperty("JTextField.padding", new Insets(4, 8, 4, 8));
        oTimKiem.putClientProperty("JTextField.placeholderText", "Tìm mã hoặc tên khách hàng...");
        CoSoPanel.themBoLangNgheTaiLieu(oTimKiem, this::locDanhSach);
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
        nhanDem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanDem.setForeground(MauUngDung.VAN_BAN_XAM);
        JPanel khungThanhTacVu = new JPanel(new BorderLayout(8, 0));
        khungThanhTacVu.setOpaque(false);
        khungThanhTacVu.add(chkChonTatCa, BorderLayout.WEST);
        khungThanhTacVu.add(nhanDem, BorderLayout.EAST);
        JPanel khungDau = new JPanel(new BorderLayout(0, 4));
        khungDau.setOpaque(false);
        khungDau.add(oTimKiem, BorderLayout.NORTH);
        khungDau.add(khungThanhTacVu, BorderLayout.SOUTH);
        khungDanhSach.setLayout(new BoxLayout(khungDanhSach, BoxLayout.Y_AXIS));
        khungDanhSach.setBackground(Color.WHITE);
        khungDanhSach.setBorder(new EmptyBorder(4, 6, 4, 6));
        scrCuon =
                new JScrollPane(
                        khungDanhSach,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrCuon.setPreferredSize(new Dimension(320, 150));
        scrCuon.setMinimumSize(new Dimension(200, 120));
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.getVerticalScrollBar().setUnitIncrement(16);
        khungChinh.add(khungDau, BorderLayout.NORTH);
        khungChinh.add(scrCuon, BorderLayout.CENTER);
        napDanhSach(Set.of());
    }

    private void napDanhSach(Set<String> daChon) {
        this.choChon = new LinkedHashSet<>(daChon);
        if (danhSachNguon != null) {
            apDungDanhSach(danhSachNguon, this.choChon);
        } else {
            apDungDanhSach(List.of(), this.choChon);
        }
        if (nguonLuaChon == null) return;
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                khungChinh,
                () -> luot == luotTai,
                nguonLuaChon::get,
                tuNguon -> {
                    danhSachNguon = tuNguon != null ? new ArrayList<>(tuNguon) : List.of();
                    apDungDanhSach(danhSachNguon, this.choChon);
                },
                ex ->
                        LoggerFactory.getLogger(BoChonNhieuKhachHang.class)
                                .warn("Không tải được danh sách chọn nhiều.", ex));
    }

    private void apDungDanhSach(List<String> nguon, Set<String> daChon) {
        List<String> luaChon = new ArrayList<>(nguon);
        for (String muc : daChon) {
            if (!muc.isBlank() && !luaChon.contains(muc)) {
                luaChon.add(muc);
            }
        }
        mapHopKiem.clear();
        khungDanhSach.removeAll();
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
            khungDanhSach.add(chk);
        }
        chkChonTatCa.setEnabled(duocBat && !mapHopKiem.isEmpty());
        locDanhSach();
        capNhatTrangThaiDem();
    }

    private void locDanhSach() {
        String tuKhoa = boDau(oTimKiem.getText().trim());
        for (Map.Entry<String, JCheckBox> entry : mapHopKiem.entrySet()) {
            boolean khop = tuKhoa.isEmpty() || boDau(entry.getKey()).contains(tuKhoa);
            entry.getValue().setVisible(khop);
        }
        khungDanhSach.revalidate();
        khungDanhSach.repaint();
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
        nhanDem.setText("Đã chọn: " + tongDaChon + "/" + mapHopKiem.size());
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
        return khungChinh;
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
        oTimKiem.setText("");
        napDanhSach(daChon);
    }

    @Override
    public void datDuocBat(boolean bat) {
        this.duocBat = bat;
        oTimKiem.setEnabled(bat);
        chkChonTatCa.setEnabled(bat && !mapHopKiem.isEmpty());
        Color nen = bat ? Color.WHITE : MauUngDung.XAM_TRUNG_TINH;
        khungDanhSach.setBackground(nen);
        scrCuon.getViewport().setBackground(nen);
        for (JCheckBox chk : mapHopKiem.values()) {
            chk.setEnabled(bat);
        }
    }
}
