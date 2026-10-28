package com.petstation.gui.search;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.processing.sales.InHoaDonPanel;
import com.petstation.service.BanHangService;
import com.petstation.service.LichSuKhachHangService;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Cung cấp giao diện và xử lý tương tác cho khách hàng chi tiết vùng hiển thị. */
public final class ChiTietKhachHangPanel extends CoSoPanel {
    public ChiTietKhachHangPanel(
            String ma, String ten, String soDienThoai, String diaChi, String hang, String diem) {
        setLayout(new BorderLayout(0, 10));
        add(
                taoTieuDeMuc(
                        ten
                                + " · "
                                + ma
                                + " · "
                                + soDienThoai
                                + " · "
                                + hang
                                + " · "
                                + diem
                                + " điểm"),
                BorderLayout.NORTH);
        // Khách hàng.
        long khachHang =
                com.petstation.service.DanhMucService.layId("khach_hang", "ma_khach_hang", ma);
        // Các tab.
        JTabbedPane tabCacTab = new JTabbedPane();
        tabCacTab.addTab(
                "Thú cưng",
                layBang(
                        new String[] {
                            "Mã thú",
                            "Tên",
                            "Loài",
                            "Giống",
                            "Ngày sinh",
                            "Cân nặng",
                            "Tiêm chủng",
                            "Lưu ý sức khỏe"
                        },
                        LichSuKhachHangService.layThuCung(khachHang)));
        // Các hóa đơn.
        List<Object[]> cacHoaDon = LichSuKhachHangService.layCacHoaDon(khachHang);
        // Hóa đơn bảng.
        JScrollPane scrHoaDonBang =
                layBang(
                        new String[] {
                            "Số hóa đơn", "Ngày", "Tổng tiền", "Thanh toán", "Trạng thái"
                        },
                        cacHoaDon);
        // Các đơn hàng.
        JPanel pnlCacDonHang = taoVungTrongSuot(new BorderLayout(0, 8));
        pnlCacDonHang.add(scrHoaDonBang, BorderLayout.CENTER);
        // Xem trước.
        NutDinhDang btnXemTruoc = new NutDinhDang("Xem / In hóa đơn", NutDinhDang.KieuNut.PHU);
        btnXemTruoc.addActionListener(
                suKien -> {
                    JTable tblGiaTriTam = layBang(scrHoaDonBang);
                    if (tblGiaTriTam.getSelectedRow() < 0) {
                        HopThoai.hienThiCanhBao(this, "Chọn hóa đơn cần xem.");
                        return;
                    }
                    String khong =
                            chuyenThanhChuoi(
                                    tblGiaTriTam
                                            .getModel()
                                            .getValueAt(
                                                    tblGiaTriTam.convertRowIndexToModel(
                                                            tblGiaTriTam.getSelectedRow()),
                                                    0));
                    // Văn bản.
                    JTextArea txtVanBan =
                            new JTextArea(
                                    InHoaDonPanel.hienThi(BanHangService.layBienLai(khong)),
                                    28,
                                    48);
                    txtVanBan.setEditable(false);
                    // In.
                    NutDinhDang btnIn = new NutDinhDang("In", NutDinhDang.KieuNut.CHINH);
                    btnIn.addActionListener(
                            suKien2 -> {
                                try {
                                    txtVanBan.print();
                                } catch (java.awt.print.PrinterException ngoaiLe) {
                                    HopThoai.hienThiLoi(this, ngoaiLe.getMessage());
                                }
                            });
                    JPanel pnlVungDangXet = new JPanel(new BorderLayout());
                    pnlVungDangXet.add(new JScrollPane(txtVanBan), BorderLayout.CENTER);
                    pnlVungDangXet.add(btnIn, BorderLayout.SOUTH);
                    HopThoai.hienThiThongBaoHopThoai(
                            this, pnlVungDangXet, "Hóa đơn " + khong, JOptionPane.PLAIN_MESSAGE);
                });
        pnlCacDonHang.add(btnXemTruoc, BorderLayout.SOUTH);
        tabCacTab.addTab("Hóa đơn", pnlCacDonHang);
        tabCacTab.addTab(
                "Spa",
                layBang(
                        new String[] {"Mã phiếu", "Thú cưng", "Ngày", "Tổng tiền", "Trạng thái"},
                        LichSuKhachHangService.layLichSuSpa(khachHang)));
        tabCacTab.addTab(
                "Lưu trú",
                layBang(
                        new String[] {
                            "Hợp đồng",
                            "Thú cưng",
                            "Phòng",
                            "Ngày vào",
                            "Ngày ra dự kiến",
                            "Trạng thái"
                        },
                        LichSuKhachHangService.layLichSuLuuTru(khachHang)));
        tabCacTab.addTab(
                "Điểm",
                layBang(
                        new String[] {"Thời gian", "Thay đổi", "Số dư sau", "Lý do"},
                        LichSuKhachHangService.layLichSuDiem(khachHang)));
        tabCacTab.addTab(
                "Voucher cá nhân",
                layBang(
                        new String[] {
                            "Mã voucher",
                            "Mệnh giá",
                            "Hạn dùng",
                            "Đã dùng",
                            "Số lượng",
                            "Trạng thái"
                        },
                        LichSuKhachHangService.layVoucher(khachHang)));
        add(tabCacTab, BorderLayout.CENTER);
    }

    private JScrollPane layBang(String[] cacCot, List<Object[]> cacDong) {
        // Mô hình.
        DefaultTableModel moHinh =
                new DefaultTableModel(cacDong.toArray(Object[][]::new), cacCot) {
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
        return taoBangDinhDang(moHinh);
    }
}
