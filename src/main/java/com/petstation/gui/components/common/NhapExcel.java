package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho Excel bộ nhập.
 *
 * <p>Utility để nhập dữ liệu từ file Excel (CSV format) Hỗ trợ validate và xử lý lỗi
 */
public class NhapExcel {

    /**
     * Cung cấp giao diện và xử lý tương tác cho nhập kết quả.
     *
     * <p>Kết quả import
     */
    public static class KetQuaNhap {
        /** Các tiêu đề. */
        public String[] cacTieuDe;

        /** Các dòng. */
        public List<String[]> cacDong;

        /** Tổng các dòng. */
        public int tongCacDong;

        /** Các lỗi. */
        public List<String> cacLoi;

        /** Khởi tạo KetQuaNhap với các giá trị được cung cấp. */
        public KetQuaNhap() {
            cacDong = new ArrayList<>();
            cacLoi = new ArrayList<>();
        }

        /**
         * Kiểm tra có lỗi trong KetQuaNhap.
         *
         * @return kết quả kiểm tra điều kiện xử lý
         */
        public boolean kiemTraCoLoi() {
            return !cacLoi.isEmpty();
        }

        /**
         * Kiểm tra thành công trong KetQuaNhap.
         *
         * @return kết quả kiểm tra điều kiện xử lý
         */
        public boolean kiemTraThanhCong() {
            return cacLoi.isEmpty() && tongCacDong > 0;
        }
    }

    /**
     * Nhập từ CSV trong NhapExcel.
     *
     * <p>Import từ file CSV
     *
     * @param cha cha
     * @return kết quả nhập từ CSV
     */
    public static KetQuaNhap nhapTuCSV(JComponent cha) {
        // Tệp bộ chọn.
        JFileChooser tepBoChon = new JFileChooser();
        tepBoChon.setDialogTitle("Chọn file Excel để nhập");
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
                                || truongDangXet.getName().toLowerCase().endsWith(".csv")
                                || truongDangXet.getName().toLowerCase().endsWith(".xls")
                                || truongDangXet.getName().toLowerCase().endsWith(".xlsx");
                    }

                    /**
                     * Lấy mô tả trong lớp xử lý giao diện.
                     *
                     * @return kết quả lấy mô tả
                     */
                    @Override
                    public String getDescription() {
                        return "Excel Files (*.csv, *.xls, *.xlsx)";
                    }
                });

        // Kết quả.
        int ketQua = tepBoChon.showOpenDialog(cha);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (ketQua == JFileChooser.APPROVE_OPTION) {
            // Tệp.
            File tep = tepBoChon.getSelectedFile();
            try {
                return docCSVTep(tep);
            } catch (IOException loi) {
                // Lỗi kết quả.
                KetQuaNhap loiKetQua = new KetQuaNhap();
                loiKetQua.cacLoi.add("Lỗi đọc file: " + loi.getMessage());
                return loiKetQua;
            }
        }

        return null; // Người dùng đã hủy
    }

    /**
     * Đọc CSV tệp trong NhapExcel.
     *
     * @param tep tệp
     * @return kết quả đọc CSV tệp
     * @throws IOException nếu thao tác không thể hoàn tất
     */
    private static KetQuaNhap docCSVTep(File tep) throws IOException {
        // Kết quả.
        KetQuaNhap ketQua = new KetQuaNhap();

        try (BufferedReader boDoc =
                new BufferedReader(new InputStreamReader(new FileInputStream(tep), "UTF-8"))) {

            // Đọc tiêu đề (dòng đầu tiên)
            // Tiêu đề dòng.
            String tieuDeDong = boDoc.readLine();
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (tieuDeDong == null) {
                ketQua.cacLoi.add("File rỗng hoặc không đúng định dạng");
                return ketQua;
            }

            // Bỏ qua dấu BOM UTF-8 nếu có
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (tieuDeDong.startsWith("\ufeff")) {
                tieuDeDong = tieuDeDong.substring(1);
            }

            ketQua.cacTieuDe = docCSVDong(tieuDeDong);

            // Đọc các dòng dữ liệu
            // Dòng.
            String dong;
            // Dòng số.
            int dongSo = 2; // Bắt đầu từ 2 vì tiêu đề là dòng 1

            while ((dong = boDoc.readLine()) != null) {
                // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                if (dong.trim().isEmpty()) {
                    continue; // Bỏ qua dòng trống
                }

                // Các giá trị.
                String[] cacGiaTri = docCSVDong(dong);

                // Kiểm tra số cột
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (cacGiaTri.length != ketQua.cacTieuDe.length) {
                    ketQua.cacLoi.add(
                            String.format(
                                    "Dòng %d: Số cột không khớp (có %d, cần %d)",
                                    dongSo, cacGiaTri.length, ketQua.cacTieuDe.length));
                } else {
                    ketQua.cacDong.add(cacGiaTri);
                }

                dongSo++;
            }

            ketQua.tongCacDong = ketQua.cacDong.size();

            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (ketQua.tongCacDong == 0 && ketQua.cacLoi.isEmpty()) {
                ketQua.cacLoi.add("File không có dữ liệu");
            }
        }

        return ketQua;
    }

    /**
     * Đọc CSV dòng trong NhapExcel.
     *
     * <p>Parse một dòng CSV với xử lý quotes và commas
     *
     * @param dong dòng
     * @return kết quả đọc CSV dòng
     */
    private static String[] docCSVDong(String dong) {
        // Các giá trị.
        List<String> cacGiaTri = new ArrayList<>();
        // Giá trị hiện tại.
        StringBuilder giaTriHienTai = new StringBuilder();
        // Vào dấu nháy.
        boolean vaoDauNhay = false;

        for (int chiSo = 0; chiSo < dong.length(); chiSo++) {
            // Giá trị đang xét.
            char giaTriDangXet = dong.charAt(chiSo);

            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (giaTriDangXet == '"') {
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (vaoDauNhay && chiSo + 1 < dong.length() && dong.charAt(chiSo + 1) == '"') {
                    // Dấu ngoặc kép đã mã hóa thành cặp dấu
                    giaTriHienTai.append('"');
                    chiSo++; // Bỏ qua dấu ngoặc kép kế tiếp
                } else {
                    // Đảo trạng thái nằm trong dấu ngoặc kép
                    vaoDauNhay = !vaoDauNhay;
                }
            } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (giaTriDangXet == ',' && !vaoDauNhay) {
                // Kết thúc giá trị
                cacGiaTri.add(giaTriHienTai.toString().trim());
                giaTriHienTai = new StringBuilder();
            } else {
                giaTriHienTai.append(giaTriDangXet);
            }
        }

        // Thêm giá trị cuối
        cacGiaTri.add(giaTriHienTai.toString().trim());

        return cacGiaTri.toArray(new String[0]);
    }

    /**
     * Hiển thị nhập kết quả trong NhapExcel.
     *
     * <p>Hiển thị dialog kết quả import
     *
     * @param cha cha
     * @param ketQua kết quả
     */
    public static void hienThiNhapKetQua(JComponent cha, KetQuaNhap ketQua) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ketQua == null) {
            return; // Người dùng đã hủy
        }

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (ketQua.kiemTraThanhCong()) {
            HopThoai.hienThiThongBaoHopThoai(
                    cha,
                    String.format("Nhập thành công %d dòng dữ liệu!", ketQua.tongCacDong),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (ketQua.kiemTraCoLoi()) {
            // Thông báo.
            StringBuilder thongBao = new StringBuilder();
            thongBao.append("Có lỗi khi nhập dữ liệu:\n\n");

            // Lớn nhất các lỗi.
            int lonNhatCacLoi = Math.min(5, ketQua.cacLoi.size());
            for (int chiSo = 0; chiSo < lonNhatCacLoi; chiSo++) {
                thongBao.append("• ").append(ketQua.cacLoi.get(chiSo)).append("\n");
            }

            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ketQua.cacLoi.size() > 5) {
                thongBao.append(String.format("\n... và %d lỗi khác", ketQua.cacLoi.size() - 5));
            }

            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ketQua.tongCacDong > 0) {
                thongBao.append(
                        String.format("\n\nĐã nhập thành công: %d dòng", ketQua.tongCacDong));
            }

            HopThoai.hienThiThongBaoHopThoai(
                    cha, thongBao.toString(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Tạo nút nhập trong NhapExcel.
     *
     * <p>Tạo nút "Nhập Excel"chuẩn
     *
     * @param cha cha
     * @param hamXuLy hàm xử lý
     * @return kết quả tạo nút nhập
     */
    public static JButton taoNutNhap(JComponent cha, XuLySauNhap hamXuLy) {
        // Nút.
        JButton btnNut = new JButton("Nhập Excel");
        btnNut.setFocusPainted(false);
        btnNut.addActionListener(
                suKien -> {
                    // Kết quả.
                    KetQuaNhap ketQua = nhapTuCSV(cha);
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (ketQua != null) {
                        hamXuLy.layKhiNhap(ketQua);
                        hienThiNhapKetQua(cha, ketQua);
                    }
                });
        return btnNut;
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho nhập hàm xử lý.
     *
     * <p>Callback interface cho xử lý dữ liệu import
     */
    public interface XuLySauNhap {
        /**
         * Lấy khi nhập trong XuLySauNhap.
         *
         * @param ketQua kết quả
         */
        void layKhiNhap(KetQuaNhap ketQua);
    }

    /**
     * Lấy tải xuống mẫu trong NhapExcel.
     *
     * <p>Tải template Excel mẫu
     *
     * @param cha cha
     * @param cacTieuDe các tiêu đề
     * @param tenTep tên tệp
     */
    public static void layTaiXuongMau(JComponent cha, String[] cacTieuDe, String tenTep) {
        // Tệp bộ chọn.
        JFileChooser tepBoChon = new JFileChooser();
        tepBoChon.setDialogTitle("Lưu file mẫu");
        tepBoChon.setSelectedFile(new File(tenTep + "_template.csv"));

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
                taoMauTep(cacTieuDe, tep);
                HopThoai.hienThiThongBaoHopThoai(
                        cha,
                        "Tải file mẫu thành công!\nĐường dẫn: " + tep.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException loi) {
                HopThoai.hienThiThongBaoHopThoai(
                        cha,
                        "Lỗi khi tạo file mẫu: " + loi.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Tạo mẫu tệp trong NhapExcel.
     *
     * @param cacTieuDe các tiêu đề
     * @param tep tệp
     * @throws IOException nếu thao tác không thể hoàn tất
     */
    private static void taoMauTep(String[] cacTieuDe, File tep) throws IOException {
        try (BufferedWriter boGhi =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tep), "UTF-8"))) {

            boGhi.write('\ufeff'); // UTF-8 BOM

            // Ghi các tiêu đề
            for (int chiSo = 0; chiSo < cacTieuDe.length; chiSo++) {
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (chiSo > 0) boGhi.write(",");
                boGhi.write(cacTieuDe[chiSo]);
            }
            boGhi.newLine();

            // Ghi dòng mẫu với các giá trị trống
            for (int chiSo = 0; chiSo < cacTieuDe.length; chiSo++) {
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (chiSo > 0) boGhi.write(",");
                boGhi.write(""); // Dòng mẫu trống
            }
            boGhi.newLine();
        }
    }
}
