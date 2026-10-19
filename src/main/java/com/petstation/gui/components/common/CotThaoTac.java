package com.petstation.gui.components.common;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho cột thao tác.
 *
 * <p>Renderer và Editor cho cột "Hành động"trong JTable Hiển thị các nút Edit, Delete, hoặc custom
 * actions
 */
public class CotThaoTac {

    /**
     * Thêm cột sửa xóa trong CotThaoTac.
     *
     * <p>Thêm cột hành động vào table với các nút Edit và Delete
     *
     * @param tblBang bảng
     * @param tenCot tên cột
     * @param khiSua khi sửa
     * @param khiXoa khi xóa
     */
    public static void themCotSuaXoa(
            JTable tblBang, String tenCot, Consumer<Integer> khiSua, Consumer<Integer> khiXoa) {
        themCotThaoTac(
                tblBang,
                tenCot,
                new NutThaoTac[] {
                    new NutThaoTac("", "Sửa", khiSua), new NutThaoTac("", "Xóa", khiXoa)
                });
    }

    /**
     * Thêm cột thao tác trong CotThaoTac.
     *
     * <p>Thêm cột hành động với các nút tùy chỉnh
     *
     * @param tblBang bảng
     * @param tenCot tên cột
     * @param cacNut các nút
     */
    public static void themCotThaoTac(JTable tblBang, String tenCot, NutThaoTac[] cacNut) {
        // Tìm hoặc thêm cột
        // Mô hình.
        DefaultTableModel moHinh = (DefaultTableModel) tblBang.getModel();
        // Chỉ số cột.
        int chiSoCot = -1;

        for (int chiSo = 0; chiSo < moHinh.getColumnCount(); chiSo++) {
            // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
            if (moHinh.getColumnName(chiSo).equals(tenCot)) {
                chiSoCot = chiSo;
                break;
            }
        }

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (chiSoCot == -1) {
            moHinh.addColumn(tenCot);
            chiSoCot = moHinh.getColumnCount() - 1;
        }

        // Thiết lập bộ hiển thị và soạn thảo
        // Cột.
        TableColumn cot = tblBang.getColumnModel().getColumn(chiSoCot);
        cot.setCellRenderer(new BoHienThiCotThaoTac(cacNut));
        cot.setCellEditor(new BoXuLyCotThaoTac(cacNut));
        // Chiều rộng.
        int chieuRong = layChieuRongCua(cacNut);
        cot.setMinWidth(chieuRong);
        cot.setMaxWidth(chieuRong + 40);
        cot.setPreferredWidth(chieuRong);
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho nút thao tác.
     *
     * <p>Class định nghĩa một action button
     */
    public static class NutThaoTac {
        /** Biểu tượng. */
        String bieuTuong;

        /** Gợi ý khi rê chuột. */
        String goiYKhiReChuot;

        /** Thao tác. */
        Consumer<Integer> thaoTac;

        /**
         * Khởi tạo NutThaoTac với các giá trị được cung cấp.
         *
         * @param bieuTuong biểu tượng
         * @param goiYKhiReChuot gợi ý khi rê chuột
         * @param thaoTac thao tác
         */
        public NutThaoTac(String bieuTuong, String goiYKhiReChuot, Consumer<Integer> thaoTac) {
            this.bieuTuong =
                    (bieuTuong == null || bieuTuong.isBlank()) ? goiYKhiReChuot : bieuTuong;
            this.goiYKhiReChuot = goiYKhiReChuot;
            this.thaoTac = thaoTac;
        }
    }

    /**
     * Lấy nút phẳng trong CotThaoTac.
     *
     * <p>Nút dạng chữ phẳng, hòa vào màu dòng (không còn khối nút xám trên nền dòng đang chọn).
     *
     * @param moTa mô tả
     * @return kết quả lấy nút phẳng
     */
    private static JButton layNutPhang(NutThaoTac moTa) {
        // Nút.
        JButton btnNut = new JButton(moTa.bieuTuong);
        btnNut.setToolTipText(moTa.goiYKhiReChuot);
        btnNut.setFocusPainted(false);
        btnNut.setBorderPainted(false);
        btnNut.setContentAreaFilled(false);
        btnNut.setOpaque(false);
        btnNut.setMargin(new Insets(0, 0, 0, 0));
        btnNut.setFont(com.petstation.gui.components.MauUngDung.taoPhongChu(Font.BOLD, 12));
        // Nguy hiểm.
        boolean nguyHiem = moTa.goiYKhiReChuot != null && moTa.goiYKhiReChuot.contains("Xóa");
        btnNut.setForeground(
                nguyHiem
                        ? com.petstation.gui.components.MauUngDung.VAN_BAN_NGUY_HIEM
                        : com.petstation.gui.components.MauUngDung.XANH_DUONG);
        btnNut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Thông số phông chữ.
        FontMetrics thongSoPhongChu = btnNut.getFontMetrics(btnNut.getFont());
        btnNut.setPreferredSize(
                new Dimension(thongSoPhongChu.stringWidth(moTa.bieuTuong) + 14, 28));
        return btnNut;
    }

    /**
     * Lấy thao tác vùng hiển thị trong CotThaoTac.
     *
     * @param cacNut các nút
     * @param noiNhan nơi nhận
     * @return kết quả lấy thao tác vùng hiển thị
     */
    private static JPanel layThaoTacVungHienThi(
            NutThaoTac[] cacNut, java.util.List<JButton> noiNhan) {
        // Vùng hiển thị.
        JPanel pnlVungHienThi = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 5));
        pnlVungHienThi.setOpaque(true);
        for (NutThaoTac moTa : cacNut) {
            // Giá trị sau.
            JButton btnGiaTriSau = layNutPhang(moTa);
            noiNhan.add(btnGiaTriSau);
            pnlVungHienThi.add(btnGiaTriSau);
        }
        return pnlVungHienThi;
    }

    /**
     * Lấy chiều rộng của trong CotThaoTac.
     *
     * @param cacNut các nút
     * @return kết quả lấy chiều rộng của
     */
    private static int layChieuRongCua(NutThaoTac[] cacNut) {
        // Đối tượng đo.
        JButton btnDoiTuongDo = new JButton();
        // Tổng.
        int tong = 16;
        for (NutThaoTac giaTriSau : cacNut) {
            tong +=
                    btnDoiTuongDo
                                    .getFontMetrics(
                                            com.petstation.gui.components.MauUngDung.taoPhongChu(
                                                    Font.BOLD, 12))
                                    .stringWidth(giaTriSau.bieuTuong)
                            + 16;
        }
        return tong;
    }

    /**
     * Lấy dòng nền trong CotThaoTac.
     *
     * @param tblBang bảng
     * @param daChon đã chọn
     * @param dong dòng
     * @return kết quả lấy dòng nền
     */
    private static Color layDongNen(JTable tblBang, boolean daChon, int dong) {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (daChon) return tblBang.getSelectionBackground();
        return dong % 2 == 0
                ? tblBang.getBackground()
                : com.petstation.gui.components.MauUngDung.DONG_XEN_KE;
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho thao tác bộ hiển thị.
     *
     * <p>Renderer hiển thị các nút trong cell.
     */
    static class BoHienThiCotThaoTac implements TableCellRenderer {
        /** Vùng hiển thị. */
        private final JPanel pnlVungHienThi;

        /**
         * Khởi tạo BoHienThiCotThaoTac với các giá trị được cung cấp.
         *
         * @param cacNut các nút
         */
        public BoHienThiCotThaoTac(NutThaoTac[] cacNut) {
            pnlVungHienThi = layThaoTacVungHienThi(cacNut, new java.util.ArrayList<>());
        }

        /**
         * Lấy bảng ô bộ hiển thị thành phần trong BoHienThiCotThaoTac.
         *
         * @param tblBang bảng
         * @param giaTri giá trị
         * @param laDaChon là đã chọn
         * @param coTieuDiem có tiêu điểm
         * @param dong dòng
         * @param cot cột
         * @return kết quả lấy bảng ô bộ hiển thị thành phần
         */
        @Override
        public Component getTableCellRendererComponent(
                JTable tblBang,
                Object giaTri,
                boolean laDaChon,
                boolean coTieuDiem,
                int dong,
                int cot) {
            pnlVungHienThi.setBackground(layDongNen(tblBang, laDaChon, dong));
            return pnlVungHienThi;
        }
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho thao tác bộ soạn thảo.
     *
     * <p>Editor cho phép bấm vào các nút.
     */
    static class BoXuLyCotThaoTac extends AbstractCellEditor implements TableCellEditor {
        /** Vùng hiển thị. */
        private final JPanel pnlVungHienThi;

        /** Hiện tại dòng. */
        private int hienTaiDong;

        /**
         * Khởi tạo BoXuLyCotThaoTac với các giá trị được cung cấp.
         *
         * @param cacNut các nút
         */
        public BoXuLyCotThaoTac(NutThaoTac[] cacNut) {
            // Đã tạo.
            java.util.List<JButton> daTao = new java.util.ArrayList<>();
            pnlVungHienThi = layThaoTacVungHienThi(cacNut, daTao);
            for (int chiSo = 0; chiSo < daTao.size(); chiSo++) {
                // Mô tả.
                NutThaoTac moTa = cacNut[chiSo];
                daTao.get(chiSo)
                        .addActionListener(
                                suKien -> {
                                    fireEditingStopped();
                                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                                    if (moTa.thaoTac != null) {
                                        SwingUtilities.invokeLater(
                                                () -> moTa.thaoTac.accept(hienTaiDong));
                                    }
                                });
            }
        }

        /**
         * Lấy bảng ô bộ soạn thảo thành phần trong BoXuLyCotThaoTac.
         *
         * @param tblBang bảng
         * @param giaTri giá trị
         * @param laDaChon là đã chọn
         * @param dong dòng
         * @param cot cột
         * @return kết quả lấy bảng ô bộ soạn thảo thành phần
         */
        @Override
        public Component getTableCellEditorComponent(
                JTable tblBang, Object giaTri, boolean laDaChon, int dong, int cot) {
            this.hienTaiDong = dong;
            pnlVungHienThi.setBackground(layDongNen(tblBang, true, dong));
            return pnlVungHienThi;
        }

        /**
         * Lấy giá trị ô bộ soạn thảo trong BoXuLyCotThaoTac.
         *
         * @return kết quả lấy giá trị ô bộ soạn thảo
         */
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    /**
     * Thêm cột đánh dấu trong CotThaoTac.
     *
     * <p>Thêm checkbox column cho bulk actions
     *
     * @param tblBang bảng
     * @param tenCot tên cột
     */
    public static void themCotDanhDau(JTable tblBang, String tenCot) {
        // Mô hình.
        DefaultTableModel moHinh = (DefaultTableModel) tblBang.getModel();

        // Thêm cột đánh dấu ở đầu
        moHinh.addColumn(tenCot);

        // Di chuyển cột mới về vị trí đầu tiên
        tblBang.moveColumn(moHinh.getColumnCount() - 1, 0);

        // Thiết lập bộ hiển thị và soạn thảo cho ô đánh dấu
        // Cột.
        TableColumn cot = tblBang.getColumnModel().getColumn(0);
        cot.setCellRenderer(
                new DefaultTableCellRenderer() {
                    /** Kiểm tra hộp. */
                    JCheckBox btnKiemTraHop = new JCheckBox();

                    {
                        btnKiemTraHop.setHorizontalAlignment(JLabel.CENTER);
                    }

                    /**
                     * Lấy bảng ô bộ hiển thị thành phần trong lớp xử lý giao diện.
                     *
                     * @param tblBang bảng
                     * @param giaTri giá trị
                     * @param laDaChon là đã chọn
                     * @param coTieuDiem có tiêu điểm
                     * @param dong dòng
                     * @param cot cột
                     * @return kết quả lấy bảng ô bộ hiển thị thành phần
                     */
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tblBang,
                            Object giaTri,
                            boolean laDaChon,
                            boolean coTieuDiem,
                            int dong,
                            int cot) {
                        btnKiemTraHop.setSelected(giaTri != null && (Boolean) giaTri);
                        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (laDaChon) {
                            btnKiemTraHop.setBackground(tblBang.getSelectionBackground());
                        } else {
                            btnKiemTraHop.setBackground(tblBang.getBackground());
                        }
                        return btnKiemTraHop;
                    }
                });

        cot.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        cot.setMaxWidth(50);
        cot.setMinWidth(50);

        // Thêm ô đánh dấu vào tiêu đề để chọn tất cả
        // Tiêu đề ô đánh dấu.
        JCheckBox btnTieuDeODanhDau = new JCheckBox();
        btnTieuDeODanhDau.addActionListener(
                suKien -> {
                    // Đã chọn.
                    boolean daChon = btnTieuDeODanhDau.isSelected();
                    for (int chiSo = 0; chiSo < moHinh.getRowCount(); chiSo++) {
                        moHinh.setValueAt(daChon, chiSo, 0);
                    }
                });

        // Tiêu đề bộ hiển thị.
        TableCellRenderer tieuDeBoHienThi =
                new DefaultTableCellRenderer() {
                    /**
                     * Lấy bảng ô bộ hiển thị thành phần trong lớp xử lý giao diện.
                     *
                     * @param tblBang bảng
                     * @param giaTri giá trị
                     * @param laDaChon là đã chọn
                     * @param coTieuDiem có tiêu điểm
                     * @param dong dòng
                     * @param cot cột
                     * @return kết quả lấy bảng ô bộ hiển thị thành phần
                     */
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tblBang,
                            Object giaTri,
                            boolean laDaChon,
                            boolean coTieuDiem,
                            int dong,
                            int cot) {
                        btnTieuDeODanhDau.setHorizontalAlignment(JLabel.CENTER);
                        return btnTieuDeODanhDau;
                    }
                };

        cot.setHeaderRenderer(tieuDeBoHienThi);
    }

    /**
     * Lấy đã kiểm tra các dòng trong CotThaoTac.
     *
     * <p>Lấy danh sách các row đã được check
     *
     * @param tblBang bảng
     * @param chiSoCotDanhDau chỉ số cột đánh dấu
     * @return kết quả lấy đã kiểm tra các dòng
     */
    public static java.util.List<Integer> layDaKiemTraCacDong(JTable tblBang, int chiSoCotDanhDau) {
        // Đã kiểm tra các dòng.
        java.util.List<Integer> daKiemTraCacDong = new java.util.ArrayList<>();
        // Mô hình.
        DefaultTableModel moHinh = (DefaultTableModel) tblBang.getModel();

        for (int chiSo = 0; chiSo < moHinh.getRowCount(); chiSo++) {
            // Giá trị.
            Object giaTri = moHinh.getValueAt(chiSo, chiSoCotDanhDau);
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (giaTri != null && (Boolean) giaTri) {
                daKiemTraCacDong.add(chiSo);
            }
        }

        return daKiemTraCacDong;
    }
}
