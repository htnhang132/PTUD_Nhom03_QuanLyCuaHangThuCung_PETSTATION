package com.petstation.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Cung cấp giao diện và xử lý tương tác cho các thẻ danh mục vùng hiển thị.
 *
 * <p>Màn có nhiều tab (ví dụ Sản phẩm + Lịch sử giá). Chuyển onShow xuống các tab con.
 */
public class DanhMucNhieuTabPanel extends CoSoPanel {
    /** Các tab. */
    private final JTabbedPane tabCacTab = new JTabbedPane();

    /** Các thành phần con. */
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
