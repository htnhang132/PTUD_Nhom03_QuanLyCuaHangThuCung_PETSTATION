package com.petstation.gui.system;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.model.Quyen;
import com.petstation.model.VaiTro;
import com.petstation.service.QuyenVaiTroService;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho quyền ma trận vùng hiển thị.
 *
 * <p>Ma trận quyền theo chức năng cho 4 vai trò (chỉ xem; quyền gán theo vai trò).
 */
public final class MaTranQuyenPanel extends CoSoPanel {
    /** Khởi tạo MaTranQuyenPanel với các giá trị được cung cấp. */
    public MaTranQuyenPanel() {
        setLayout(new BorderLayout(0, 10));
        // Các vai trò.
        VaiTro[] cacVaiTro = VaiTro.values();
        // Các cột.
        String[] cacCot = new String[cacVaiTro.length + 1];
        cacCot[0] = "Chức năng";
        for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++)
            cacCot[chiSo + 1] = cacVaiTro[chiSo].layTenHienThi();
        // Mô hình.
        DefaultTableModel moHinh =
                new DefaultTableModel(cacCot, 0) {
                    /**
                     * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                     *
                     * @param dongDuLieu dòng dữ liệu
                     * @param giaTriDangXet giá trị đang xét
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                        return false;
                    }
                };
        for (Quyen vungDangXet : Quyen.values()) {
            // Dòng.
            Object[] dong = new Object[cacCot.length];
            dong[0] = QuyenVaiTroService.layNhan(vungDangXet);
            for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++)
                dong[chiSo + 1] = QuyenVaiTroService.tao(cacVaiTro[chiSo]).contains(vungDangXet);
            moHinh.addRow(dong);
        }
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        // Bảng.
        JTable tblBang = layBang(scrCuon);
        tblBang.getColumnModel().getColumn(0).setMinWidth(245);
        tblBang.getColumnModel().getColumn(0).setPreferredWidth(285);
        tblBang.setAutoCreateRowSorter(false);
        tblBang.setRowSorter(null);
        // Giữa.
        javax.swing.table.DefaultTableCellRenderer lblGiua =
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public java.awt.Component getTableCellRendererComponent(
                            JTable bang,
                            Object giaTri,
                            boolean chon,
                            boolean tapTrung,
                            int dong,
                            int cot) {
                        super.getTableCellRendererComponent(bang, "", chon, tapTrung, dong, cot);
                        boolean co = Boolean.TRUE.equals(giaTri);
                        setIcon(
                                new com.petstation.gui.components.BieuTuongGiaoDien(
                                        co
                                                ? com.petstation.gui.components.BieuTuongGiaoDien
                                                        .Kieu.CHO_PHEP
                                                : com.petstation.gui.components.BieuTuongGiaoDien
                                                        .Kieu.KHONG_CHO_PHEP));
                        setToolTipText(co ? "Được phép" : "Không được phép");
                        return this;
                    }
                };
        lblGiua.setHorizontalAlignment(JLabel.CENTER);
        for (int chiSo = 1; chiSo < cacCot.length; chiSo++)
            tblBang.getColumnModel().getColumn(chiSo).setCellRenderer(lblGiua);
        add(taoTieuDeMuc("Ma trận quyền theo vai trò (4 vai trò)"), BorderLayout.NORTH);
        add(scrCuon, BorderLayout.CENTER);
    }

    /**
     * Lấy tiêu đề trợ giúp trong MaTranQuyenPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Ma trận quyền";
    }

    /**
     * Lấy nội dung trợ giúp trong MaTranQuyenPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Mỗi tài khoản có đúng 1 vai trò; quyền theo vai trò như bảng.";
    }
}
