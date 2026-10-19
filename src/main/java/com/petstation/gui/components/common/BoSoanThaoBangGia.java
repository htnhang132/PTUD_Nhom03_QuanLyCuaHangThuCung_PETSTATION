package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho giá bảng bộ soạn thảo.
 *
 * <p>Bảng giá dịch vụ theo loài và khoảng cân nặng. Lưu dạng "Loài|Cân nặng|Giá;Loài|Cân nặng|Giá".
 */
public final class BoSoanThaoBangGia implements BoSoanThaoTruong {
    /** Mô hình. */
    private final DefaultTableModel moHinh =
            new DefaultTableModel(new String[] {"Loài", "Khoảng cân nặng", "Giá (₫)"}, 0);

    /** Bảng. */
    private final JTable tblBang = new JTable(moHinh);

    /** Vùng hiển thị. */
    private final JPanel pnlVungHienThi = new JPanel(new BorderLayout(0, 4));

    /** Thêm. */
    private final JButton btnThem = new JButton("+ Thêm dòng");

    /** Xóa. */
    private final JButton btnXoa = new JButton("Xóa dòng");

    /** Khởi tạo BoSoanThaoBangGia với các giá trị được cung cấp. */
    public BoSoanThaoBangGia() {
        DinhDangBang.apDung(tblBang);
        tblBang.setRowHeight(30);
        // Khung cuộn.
        JScrollPane scrKhungCuon = new JScrollPane(tblBang);
        scrKhungCuon.setPreferredSize(new Dimension(300, 150));
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        btnThem.addActionListener(suKien -> moHinh.addRow(new Object[] {"Chó", "", ""}));
        btnXoa.addActionListener(
                suKien -> {
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (tblBang.isEditing()) tblBang.getCellEditor().stopCellEditing();
                    // Dòng dữ liệu.
                    int dongDuLieu = tblBang.getSelectedRow();
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (dongDuLieu >= 0)
                        moHinh.removeRow(tblBang.convertRowIndexToModel(dongDuLieu));
                });
        // Thanh.
        JPanel pnlThanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlThanh.setOpaque(false);
        pnlThanh.add(btnThem);
        pnlThanh.add(btnXoa);
        pnlVungHienThi.setOpaque(false);
        pnlVungHienThi.add(scrKhungCuon, BorderLayout.CENTER);
        pnlVungHienThi.add(pnlThanh, BorderLayout.SOUTH);
    }

    /**
     * Lấy thành phần trong BoSoanThaoBangGia.
     *
     * @return kết quả lấy thành phần
     */
    @Override
    public JComponent layThanhPhan() {
        return pnlVungHienThi;
    }

    /**
     * Lấy trong BoSoanThaoBangGia.
     *
     * @return kết quả lấy
     */
    @Override
    public String lay() {
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (tblBang.isEditing()) tblBang.getCellEditor().stopCellEditing();
        // Bộ ghép chuỗi.
        StringBuilder boGhepChuoi = new StringBuilder();
        for (int dongDuLieu = 0; dongDuLieu < moHinh.getRowCount(); dongDuLieu++) {
            // Loài.
            String loai = String.valueOf(moHinh.getValueAt(dongDuLieu, 0)).trim();
            // Cân nặng.
            String canNang = String.valueOf(moHinh.getValueAt(dongDuLieu, 1)).trim();
            // Giá.
            long gia = TienTe.doc(String.valueOf(moHinh.getValueAt(dongDuLieu, 2)));
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (loai.isEmpty() && canNang.isEmpty() && gia < 0) continue;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (boGhepChuoi.length() > 0) boGhepChuoi.append(';');
            boGhepChuoi
                    .append(loai)
                    .append('|')
                    .append(canNang)
                    .append('|')
                    .append(Math.max(gia, 0));
        }
        return boGhepChuoi.toString();
    }

    /**
     * Đặt trong BoSoanThaoBangGia.
     *
     * @param giaTri giá trị
     */
    @Override
    public void dat(String giaTri) {
        moHinh.setRowCount(0);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri == null || giaTri.isBlank()) return;
        for (String phan : giaTri.split(";")) {
            // Trường đang xét.
            String[] truongDangXet = phan.split("\\|", -1);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (truongDangXet.length == 3)
                moHinh.addRow(
                        new Object[] {
                            truongDangXet[0],
                            truongDangXet[1],
                            truongDangXet[2].isEmpty()
                                    ? ""
                                    : TienTe.dinhDang(TienTe.doc(truongDangXet[2]))
                        });
        }
    }

    /**
     * Đặt được bật trong BoSoanThaoBangGia.
     *
     * @param duocBat được bật
     */
    @Override
    public void datDuocBat(boolean duocBat) {
        tblBang.setEnabled(duocBat);
        btnThem.setEnabled(duocBat);
        btnXoa.setEnabled(duocBat);
    }

    /**
     * Lấy giá nhỏ nhất trong BoSoanThaoBangGia.
     *
     * <p>Giá thấp nhất trong bảng giá, -1 nếu chưa có.
     *
     * @param giaTri giá trị
     * @return kết quả lấy giá nhỏ nhất
     */
    public static long layGiaNhoNhat(String giaTri) {
        // Nhỏ nhất.
        long nhoNhat = -1;
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri == null) return nhoNhat;
        for (String phan : giaTri.split(";")) {
            // Trường đang xét.
            String[] truongDangXet = phan.split("\\|", -1);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (truongDangXet.length == 3) {
                // Vùng đang xét.
                long vungDangXet = TienTe.doc(truongDangXet[2]);
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (vungDangXet >= 0 && (nhoNhat < 0 || vungDangXet < nhoNhat))
                    nhoNhat = vungDangXet;
            }
        }
        return nhoNhat;
    }

    /**
     * Lấy giá cho trong BoSoanThaoBangGia.
     *
     * <p>Tìm giá theo loài và cân nặng (kg); trả -1 nếu không có dòng phù hợp. Dùng cho bước Chọn
     * dịch vụ Spa.
     *
     * @param giaTri giá trị
     * @param loai loài
     * @param canNangKg cân nặng kg
     * @return kết quả lấy giá cho
     */
    public static long layGiaCho(String giaTri, String loai, double canNangKg) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri == null) return -1;
        for (String phan : giaTri.split(";")) {
            // Trường đang xét.
            String[] truongDangXet = phan.split("\\|", -1);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (truongDangXet.length != 3 || !truongDangXet[0].trim().equalsIgnoreCase(loai))
                continue;
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (layKhopCanNang(truongDangXet[1], canNangKg)) return TienTe.doc(truongDangXet[2]);
        }
        return -1;
    }

    /**
     * Lấy khớp cân nặng trong BoSoanThaoBangGia.
     *
     * @param khoang khoảng
     * @param kg kg
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private static boolean layKhopCanNang(String khoang, double kg) {
        // Dòng dữ liệu.
        String dongDuLieu = khoang.toLowerCase().replace("kg", "").replace(" ", "");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (dongDuLieu.isEmpty() || dongDuLieu.contains("mọi")) return true;
        try {
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (dongDuLieu.startsWith("<")) return kg < Double.parseDouble(dongDuLieu.substring(1));
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (dongDuLieu.startsWith(">")) return kg > Double.parseDouble(dongDuLieu.substring(1));
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (dongDuLieu.contains("-")) {
                // Vùng đang xét.
                String[] vungDangXet = dongDuLieu.split("-");
                return kg >= Double.parseDouble(vungDangXet[0])
                        && kg <= Double.parseDouble(vungDangXet[1]);
            }
        } catch (NumberFormatException boQua) {
        }
        return false;
    }
}
