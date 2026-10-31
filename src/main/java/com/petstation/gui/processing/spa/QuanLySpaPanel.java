package com.petstation.gui.processing.spa;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.model.GioHang;
import com.petstation.model.MaManHinh;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.*;

/** Ba công việc độc lập; lịch hẹn không phải bước bắt buộc của lập phiếu. */
public final class QuanLySpaPanel extends CoSoPanel {
    private final JTabbedPane cacTab = new JTabbedPane();
    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final LapPhieuSpaPanel lapPhieu;

    public QuanLySpaPanel(GioHang gio, Consumer<MaManHinh> dieuHuong, int tabDau) {
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        setLayout(new BorderLayout(0, 12));
        add(taoTieuDeMuc("Dịch vụ Spa"), BorderLayout.NORTH);
        cacTab.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        cacTab.setBackground(MauUngDung.NEN_KEM);
        cacTab.setForeground(MauUngDung.NAU);
        lapPhieu = new LapPhieuSpaPanel(id -> {
            gio.layQuyTrinh().chonSpa(id);
            cacTab.setSelectedIndex(2);
        }, () -> cacTab.setSelectedIndex(1));
        cacTab.addTab("Lập phiếu", lapPhieu);
        cacTab.addTab("Lịch hẹn", new JPanel());
        cacTab.addTab("Đang phục vụ", new JPanel());
        cacTab.addChangeListener(e -> taiTab());
        add(cacTab, BorderLayout.CENTER);
        cacTab.setSelectedIndex(tabDau);
    }

    private void taiTab() {
        int tab = cacTab.getSelectedIndex();
        if (tab == 1) {
            cacTab.setComponentAt(1, new LichHenSpaPanel(gio, dieuHuong, id -> {
                lapPhieu.nhanLichHen(id);
                cacTab.setSelectedIndex(0);
            }));
        } else if (tab == 2) {
            cacTab.setComponentAt(2, new TienDoSpaPanel(gio, dieuHuong));
        }
    }

    @Override
    public void xuLyHienThi() {
        // Giữ nội dung form đang nhập; chỉ tải lại danh sách tại tab đang mở.
        taiTab();
    }

    @Override
    public void xuLyTimKiem() {
        if (cacTab.getSelectedComponent() instanceof HoTroThanhCongCu manHinh)
            manHinh.xuLyTimKiem();
    }
}
