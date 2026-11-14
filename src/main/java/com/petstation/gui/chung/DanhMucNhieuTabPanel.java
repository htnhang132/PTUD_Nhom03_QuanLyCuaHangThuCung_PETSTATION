package com.petstation.gui.chung;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class DanhMucNhieuTabPanel extends CoSoPanel {
    private final JTabbedPane tabCacTab = new JTabbedPane();

    public DanhMucNhieuTabPanel(String[] cacTieuDe, JPanel[] cacThanhPhanCon) {
        setLayout(new BorderLayout());
        setBackground(MauUngDung.NEN_KEM);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));
        tabCacTab.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        for (int chiSo = 0; chiSo < cacTieuDe.length; chiSo++) {
            cacThanhPhanCon[chiSo].setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
            tabCacTab.addTab(cacTieuDe[chiSo], cacThanhPhanCon[chiSo]);
        }
        tabCacTab.addChangeListener(
                suKien -> {
                    xuLyHienThi();
                    if (SwingUtilities.getWindowAncestor(this) instanceof ChinhFrame khungChinh) {
                        khungChinh.capNhatThanhCongCu();
                    }
                });
        add(tabCacTab, BorderLayout.CENTER);
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            return giaTriTam.layKhaNangThanhCongCu();
        return super.layKhaNangThanhCongCu();
    }

    @Override
    public void xuLyTaoMoi() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyTaoMoi();
    }

    @Override
    public void xuLyLuu() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyLuu();
    }

    @Override
    public void xuLyTimKiem() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyTimKiem();
    }

    @Override
    public void xuLyIn() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyIn();
    }

    @Override
    public void xuLyHoanTac() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyHoanTac();
    }

    @Override
    public void xuLyHienThi() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyHienThi();
    }

    @Override
    public void xuLyLamMoi() {
        if (tabCacTab.getSelectedComponent() instanceof HoTroThanhCongCu giaTriTam)
            giaTriTam.xuLyLamMoi();
    }

    @Override
    public boolean coTheRoiManHinh() {
        Component dangChon = tabCacTab.getSelectedComponent();
        if (dangChon instanceof CoSoPanel coSo && !coSo.coTheRoiManHinh()) return false;
        for (Component thanhPhan : tabCacTab.getComponents()) {
            if (thanhPhan != dangChon
                    && thanhPhan instanceof CoSoPanel coSo
                    && !coSo.coTheRoiManHinh()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Danh mục";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chuyển giữa các tab ở phía trên.";
    }
}
