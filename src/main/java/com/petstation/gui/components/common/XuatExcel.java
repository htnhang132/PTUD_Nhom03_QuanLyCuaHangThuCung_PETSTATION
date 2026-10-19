package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho Excel bộ xuất.
 *
 * <p>Utility để xuất dữ liệu từ JTable ra file Excel (CSV format) Support xuất cả từ TableModel
 * hoặc từ data 2D array
 */
public class XuatExcel {

    /**
     * Xuất bảng trong XuatExcel.
     *
     * <p>Xuất JTable ra file Excel (CSV)
     *
     * @param cha cha
     * @param tblBang bảng
     * @param tenMacDinhTep tên mặc định tệp
     */
    public static void xuatBang(JComponent cha, JTable tblBang, String tenMacDinhTep) {
        // Tệp bộ chọn.
        JFileChooser tepBoChon = new JFileChooser();
        tepBoChon.setDialogTitle("Xuất Excel");

        // Tạo tên tệp mặc định có thời điểm xuất
        // Mốc thời gian.
        String mocThoiGian = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        tepBoChon.setSelectedFile(new File(tenMacDinhTep + "_" + mocThoiGian + ".csv"));

        tepBoChon.setFileFilter(
                new javax.swing.filechooser.FileFilter() {
                    /**
                     * Tiếp nhận trong lớp xử lý giao diện.
                     *
                     * @param truongDangXet trường đang xét
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean accept(File truongDangXet) {
                        return truongDangXet.isDirectory()
                                || truongDangXet.getName().toLowerCase().endsWith(".csv");
                    }

                    /**
                     * Lấy mô tả trong lớp xử lý giao diện.
                     *
                     * @return kết quả lấy mô tả
                     */
                    @Override
                    public String getDescription() {
                        return "CSV Files (*.csv)";
                    }
                });

        // Kết quả.
        int ketQua = tepBoChon.showSaveDialog(cha);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (ketQua == JFileChooser.APPROVE_OPTION) {
            // Tệp.
            File tep = tepBoChon.getSelectedFile();

            // Thêm .csv nếu chưa có
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!tep.getName().toLowerCase().endsWith(".csv")) {
                tep = new File(tep.getAbsolutePath() + ".csv");
            }

            try {
                xuatBangDenTep(tblBang, tep);
                HopThoai.hienThiThongBaoHopThoai(
                        cha,
                        "Xuất file thành công!\nĐường dẫn: " + tep.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException loi) {
                HopThoai.hienThiThongBaoHopThoai(
                        cha,
                        "Lỗi khi xuất file: " + loi.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xuất bảng đến tệp trong XuatExcel.
     *
     * <p>Xuất table ra file cụ thể
     *
     * @param tblBang bảng
     * @param tep tệp
     * @throws IOException nếu thao tác không thể hoàn tất
     */
    private static void xuatBangDenTep(JTable tblBang, File tep) throws IOException {
        // Mô hình.
        TableModel moHinh = tblBang.getModel();

        try (BufferedWriter boGhi =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tep), "UTF-8"))) {

            // Ghi dấu BOM UTF-8 để Excel nhận diện đúng tiếng Việt
            boGhi.write('\ufeff');

            // Ghi tiêu đề
            for (int chiSo = 0; chiSo < moHinh.getColumnCount(); chiSo++) {
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (chiSo > 0) boGhi.write(",");
                boGhi.write(thoatCSV(moHinh.getColumnName(chiSo)));
            }
            boGhi.newLine();

            // Ghi dữ liệu
            for (int dong = 0; dong < moHinh.getRowCount(); dong++) {
                for (int cot = 0; cot < moHinh.getColumnCount(); cot++) {
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (cot > 0) boGhi.write(",");

                    // Giá trị.
                    Object giaTri = moHinh.getValueAt(dong, cot);
                    // Giá trị chuỗi.
                    String giaTriChuoi = giaTri == null ? "" : giaTri.toString();
                    boGhi.write(thoatCSV(giaTriChuoi));
                }
                boGhi.newLine();
            }
        }
    }

    /**
     * Thoát CSV trong XuatExcel.
     *
     * <p>Escape giá trị cho CSV format
     *
     * @param giaTri giá trị
     * @return kết quả thoát CSV
     */
    private static String thoatCSV(String giaTri) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri == null) return "";

        // Nếu có dấu phẩy, dấu ngoặc kép, hoặc xuống dòng -> bọc trong dấu ngoặc kép
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTri.contains(",")
                || giaTri.contains("\"")
                || giaTri.contains("\n")
                || giaTri.contains("\r")) {
            giaTri = giaTri.replace("\"", "\"\""); // Nhân đôi dấu ngoặc kép để mã hóa CSV
            return "\"" + giaTri + "\"";
        }

        return giaTri;
    }

    /**
     * Xuất dữ liệu trong XuatExcel.
     *
     * <p>Xuất data 2D array ra Excel
     *
     * @param cha cha
     * @param cacTieuDe các tiêu đề
     * @param duLieu dữ liệu
     * @param tenMacDinhTep tên mặc định tệp
     */
    public static void xuatDuLieu(
            JComponent cha, String[] cacTieuDe, Object[][] duLieu, String tenMacDinhTep) {
        // Tệp bộ chọn.
        JFileChooser tepBoChon = new JFileChooser();
        tepBoChon.setDialogTitle("Xuất Excel");

        // Mốc thời gian.
        String mocThoiGian = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        tepBoChon.setSelectedFile(new File(tenMacDinhTep + "_" + mocThoiGian + ".csv"));

        tepBoChon.setFileFilter(
                new javax.swing.filechooser.FileFilter() {
                    /**
                     * Tiếp nhận trong lớp xử lý giao diện.
                     *
                     * @param truongDangXet trường đang xét
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean accept(File truongDangXet) {
                        return truongDangXet.isDirectory()
                                || truongDangXet.getName().toLowerCase().endsWith(".csv");
                    }

                    /**
                     * Lấy mô tả trong lớp xử lý giao diện.
                     *
                     * @return kết quả lấy mô tả
                     */
                    @Override
                    public String getDescription() {
                        return "CSV Files (*.csv)";
                    }
                });

        // Kết quả.
        int ketQua = tepBoChon.showSaveDialog(cha);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (ketQua == JFileChooser.APPROVE_OPTION) {
            // Tệp.
            File tep = tepBoChon.getSelectedFile();

            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!tep.getName().toLowerCase().endsWith(".csv")) {
                tep = new File(tep.getAbsolutePath() + ".csv");
            }

            try {
                xuatDuLieuDenTep(cacTieuDe, duLieu, tep);
                HopThoai.hienThiThongBaoHopThoai(
                        cha,
                        "Xuất file thành công!\nĐường dẫn: " + tep.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException loi) {
                HopThoai.hienThiThongBaoHopThoai(
                        cha,
                        "Lỗi khi xuất file: " + loi.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xuất dữ liệu đến tệp trong XuatExcel.
     *
     * @param cacTieuDe các tiêu đề
     * @param duLieu dữ liệu
     * @param tep tệp
     * @throws IOException nếu thao tác không thể hoàn tất
     */
    private static void xuatDuLieuDenTep(String[] cacTieuDe, Object[][] duLieu, File tep)
            throws IOException {
        try (BufferedWriter boGhi =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tep), "UTF-8"))) {

            boGhi.write('\ufeff'); // UTF-8 BOM

            // Ghi các tiêu đề
            for (int chiSo = 0; chiSo < cacTieuDe.length; chiSo++) {
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (chiSo > 0) boGhi.write(",");
                boGhi.write(thoatCSV(cacTieuDe[chiSo]));
            }
            boGhi.newLine();

            // Ghi dữ liệu
            for (Object[] dong : duLieu) {
                for (int chiSo = 0; chiSo < dong.length; chiSo++) {
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (chiSo > 0) boGhi.write(",");
                    // Giá trị.
                    String giaTri = dong[chiSo] == null ? "" : dong[chiSo].toString();
                    boGhi.write(thoatCSV(giaTri));
                }
                boGhi.newLine();
            }
        }
    }

    /**
     * Tạo nút xuất trong XuatExcel.
     *
     * <p>Tạo nút "Xuất Excel"chuẩn
     *
     * @param cha cha
     * @param tblBang bảng
     * @param tenTep tên tệp
     * @return kết quả tạo nút xuất
     */
    public static JButton taoNutXuat(JComponent cha, JTable tblBang, String tenTep) {
        // Nút.
        JButton btnNut = new JButton("Xuất Excel");
        btnNut.setFocusPainted(false);
        btnNut.addActionListener(suKien -> xuatBang(cha, tblBang, tenTep));
        return btnNut;
    }

    /**
     * Tạo nút xuất trong XuatExcel.
     *
     * <p>Tạo nút "Xuất Excel"với custom tooltip
     *
     * @param cha cha
     * @param tblBang bảng
     * @param tenTep tên tệp
     * @param goiYKhiReChuot gợi ý khi rê chuột
     * @return kết quả tạo nút xuất
     */
    public static JButton taoNutXuat(
            JComponent cha, JTable tblBang, String tenTep, String goiYKhiReChuot) {
        // Nút.
        JButton btnNut = taoNutXuat(cha, tblBang, tenTep);
        btnNut.setToolTipText(goiYKhiReChuot);
        return btnNut;
    }
}
