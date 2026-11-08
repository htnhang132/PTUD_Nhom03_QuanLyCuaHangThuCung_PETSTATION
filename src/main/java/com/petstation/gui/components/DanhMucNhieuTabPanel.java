package com.petstation.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class DanhMucNhieuTabPanel extends CoSoPanel {
    private final JTabbedPane tabCacTab = new JTabbedPane();

    private final JPanel[] cacThanhPhanCon;

    public DanhMucNhieuTabPanel(String[] cacTieuDe, JPanel[] cacThanhPhanCon) {
        this.cacThanhPhanCon = cacThanhPhanCon;
        setLayout(new BorderLayout());
        tabCacTab.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        for (int chiSo = 0; chiSo < cacTieuDe.length; chiSo++) {
            cacThanhPhanCon[chiSo].setBorder(
                    javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
            tabCacTab.addTab(cacTieuDe[chiSo], cacThanhPhanCon[chiSo]);
        }
        tabCacTab.addChangeListener(suKien -> xuLyHienThi());
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
    protected String layTieuDeTroGiup() {
        return "Danh mục";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chuyển giữa các tab ở phía trên.";
    }
}
