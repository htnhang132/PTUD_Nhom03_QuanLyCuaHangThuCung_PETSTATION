package com.petstation.gui.chung.thanhphan;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.util.DinhDang;
import com.petstation.util.GhiExcel;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Hộp thoại giao diện người dùng hỗ trợ chọn đường dẫn file và xuất Excel. Gọi trực tiếp sang tầng
 * tiện ích GhiExcel để thực hiện ghi file.
 */
public final class XuatExcelDialog {
    private XuatExcelDialog() {}

    public static void xuatBang(JComponent cha, JTable bang, String ten) {
        if (bang == null || bang.getColumnCount() == 0) {
            HopThoai.hienThiCanhBao(cha, "Bảng chưa có dữ liệu hoặc cấu trúc cột để xuất Excel.");
            return;
        }
        if (bang.getRowCount() == 0) {
            HopThoai.hienThiCanhBao(cha, "Bảng hiện không có dữ liệu để xuất.");
            return;
        }
        JFileChooser chon = new JFileChooser();
        chon.setDialogTitle("Xuất Excel (.xlsx)");
        chon.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chon.setSelectedFile(
                new File(
                        ten.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "-")
                                + "_"
                                + LocalDateTime.now().format(DinhDang.TEP_THOI_GIAN)
                                + ".xlsx"));
        if (chon.showSaveDialog(cha) != JFileChooser.APPROVE_OPTION) return;
        File tep = chon.getSelectedFile();
        if (!tep.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            tep = new File(tep + ".xlsx");
        }
        if (tep.exists()
                && !HopThoai.xacNhanDongY(cha, "Ghi đè tệp " + tep.getName() + "?", "Xuất Excel")) {
            return;
        }
        String[] cot = new String[bang.getColumnCount()];
        Object[][] dong = new Object[bang.getRowCount()][cot.length];
        for (int c = 0; c < cot.length; c++) {
            cot[c] = bang.getColumnName(c);
            for (int r = 0; r < dong.length; r++) {
                dong[r][c] = DinhDangBang.giaTriXuatExcel(bang, r, c);
            }
        }
        final File dich = tep;
        final int soDong = dong.length;
        TacVuNen.chay(
                () -> {
                    GhiExcel.ghiDuLieu(cot, dong, dich, ten);
                    return null;
                },
                v ->
                        HopThoai.hienThiThanhCong(
                                cha,
                                "Đã xuất "
                                        + soDong
                                        + " dòng sang file:\n"
                                        + dich.getAbsolutePath()),
                ex ->
                        HopThoai.hienThiLoi(
                                cha, "Không xuất được Excel: " + TacVuNen.thongDiepLoi(ex)));
    }
}
