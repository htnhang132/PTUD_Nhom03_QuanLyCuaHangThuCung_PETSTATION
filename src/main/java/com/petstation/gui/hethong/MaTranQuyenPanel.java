package com.petstation.gui.hethong;

import com.petstation.gui.chung.BieuTuong;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.VaiTro;
import com.petstation.service.hethong.QuyenVaiTroService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class MaTranQuyenPanel extends CoSoPanel {
    private final QuyenVaiTroService quyenVaiTroService = new QuyenVaiTroService();

    private final DefaultTableModel moHinh;

    private int luotTai;

    public MaTranQuyenPanel() {
        setLayout(new BorderLayout(0, 10));
        VaiTro[] cacVaiTro = VaiTro.values();
        String[] cacCot = new String[cacVaiTro.length + 1];
        cacCot[0] = "Chức năng";
        for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++)
            cacCot[chiSo + 1] = cacVaiTro[chiSo].getTenHienThi();
        moHinh = DinhDangBang.taoMoHinhChiDoc(cacCot);
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        JTable bangBang = layBang(scrCuon);
        bangBang.getColumnModel().getColumn(0).setMinWidth(245);
        bangBang.getColumnModel().getColumn(0).setPreferredWidth(285);
        bangBang.setAutoCreateRowSorter(false);
        bangBang.setRowSorter(null);
        DefaultTableCellRenderer nhanGiua = taoTrinhVeQuyen();
        for (int chiSo = 1; chiSo < cacCot.length; chiSo++)
            bangBang.getColumnModel().getColumn(chiSo).setCellRenderer(nhanGiua);
        add(
                taoTieuDeManHinh(
                        "Ma trận quyền theo vai trò (4 vai trò)",
                        "Tổng hợp các quyền chức năng mặc định của từng vai trò nhân sự"),
                BorderLayout.NORTH);
        add(scrCuon, BorderLayout.CENTER);
        taiLai();
    }

    private DefaultTableCellRenderer taoTrinhVeQuyen() {
        DefaultTableCellRenderer nhanGiua =
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable bang,
                            Object giaTri,
                            boolean chon,
                            boolean tapTrung,
                            int dong,
                            int cot) {
                        super.getTableCellRendererComponent(bang, "", chon, tapTrung, dong, cot);
                        boolean co = Boolean.TRUE.equals(giaTri);
                        setIcon(
                                new BieuTuong.GiaoDien(
                                        co
                                                ? BieuTuong.GiaoDien.Kieu.CHO_PHEP
                                                : BieuTuong.GiaoDien.Kieu.KHONG_CHO_PHEP));
                        setToolTipText(co ? "Được phép" : "Không được phép");
                        return this;
                    }
                };
        nhanGiua.setHorizontalAlignment(JLabel.CENTER);
        return nhanGiua;
    }

    private void taiLai() {
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this, () -> luot == luotTai, this::taiDuLieuMaTran, this::hienThiMaTran);
    }

    private List<Object[]> taiDuLieuMaTran() {
        VaiTro[] cacVaiTro = VaiTro.values();
        List<Object[]> ketQua = new ArrayList<>();
        for (Quyen quyen : Quyen.values()) {
            Object[] dong = new Object[cacVaiTro.length + 1];
            dong[0] = quyenVaiTroService.layNhan(quyen);
            for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++) {
                dong[chiSo + 1] = quyenVaiTroService.tao(cacVaiTro[chiSo]).contains(quyen);
            }
            ketQua.add(dong);
        }
        return ketQua;
    }

    private void hienThiMaTran(List<Object[]> cacDong) {
        moHinh.setRowCount(0);
        for (Object[] dong : cacDong) moHinh.addRow(dong);
    }

    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    @Override
    public void xuLyLamMoi() {
        taiLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Ma trận quyền";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Mỗi tài khoản có đúng 1 vai trò; quyền theo vai trò như bảng.";
    }
}
