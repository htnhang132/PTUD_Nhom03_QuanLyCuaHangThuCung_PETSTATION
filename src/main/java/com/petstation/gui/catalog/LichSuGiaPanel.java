package com.petstation.gui.catalog;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.service.DanhMucService;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho lịch sử giá vùng hiển thị.
 *
 * <p>Sản phẩm ▸ Lịch sử giá: chỉ đọc, tự thêm dòng mỗi khi giá bán đổi ở tab Danh sách.
 */
public final class LichSuGiaPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Mô hình. */
    private final DefaultTableModel moHinh =
            new DefaultTableModel(
                    new String[] {
                        "Thời gian", "Mã SP", "Tên sản phẩm", "Giá cũ", "Giá mới", "Người thay đổi"
                    },
                    0) {
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

    /** Khởi tạo LichSuGiaPanel với các giá trị được cung cấp. */
    public LichSuGiaPanel() {
        setLayout(new BorderLayout(0, 10));
        // Tìm kiếm.
        JTextField txtTimKiem = taoOTimKiem("Tìm theo mã hoặc tên sản phẩm...");
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        // Bảng.
        JTable tblBang = layBang(scrCuon);
        lienKetBoLoc(txtTimKiem, tblBang);
        // Trên.
        javax.swing.JPanel pnlTren = taoVungTrongSuot(new BorderLayout(10, 0));
        pnlTren.add(taoTieuDeMuc("Lịch sử thay đổi giá bán"), BorderLayout.WEST);
        pnlTren.add(txtTimKiem, BorderLayout.EAST);
        add(pnlTren, BorderLayout.NORTH);
        add(scrCuon, BorderLayout.CENTER);
        taiLai();
        DanhMucService.themBoLangNgheGia(() -> SwingUtilities.invokeLater(this::taiLai));
    }

    /** Tải lại trong LichSuGiaPanel. */
    private void taiLai() {
        moHinh.setRowCount(0);
        // Các dòng.
        java.util.List<Object[]> cacDong = new java.util.ArrayList<>(DanhMucService.layLichSuGia());

        for (Object[] dongDuLieu : cacDong) {
            moHinh.addRow(
                    new Object[] {
                        dongDuLieu[0],
                        dongDuLieu[1],
                        dongDuLieu[2],
                        String.valueOf(dongDuLieu[3]).isBlank()
                                ? "—"
                                : TienTe.dinhDang(TienTe.doc(String.valueOf(dongDuLieu[3]))),
                        TienTe.dinhDang(TienTe.doc(String.valueOf(dongDuLieu[4]))),
                        dongDuLieu[5]
                    });
        }
    }

    /** Xử lý hiển thị trong LichSuGiaPanel. */
    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    /**
     * Lấy tiêu đề trợ giúp trong LichSuGiaPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Lịch sử giá";
    }

    /**
     * Lấy nội dung trợ giúp trong LichSuGiaPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Chỉ xem. Giá bán đổi ở tab Danh sách sản phẩm sẽ tự ghi vào đây.";
    }
}
