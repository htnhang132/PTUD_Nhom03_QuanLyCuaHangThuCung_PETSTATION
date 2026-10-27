package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Xuất XLSX thật, giữ thứ tự cột/dòng đang hiển thị và bộ lọc của bảng. */
public final class XuatExcel {
    private XuatExcel() {}

    public static void xuatBang(JComponent cha, JTable bang, String ten) {
        JFileChooser chon = new JFileChooser();
        chon.setDialogTitle("Xuất Excel (.xlsx)");
        chon.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chon.setSelectedFile(
                new File(
                        ten.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "-")
                                + "_"
                                + LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                                + ".xlsx"));
        if (chon.showSaveDialog(cha) != JFileChooser.APPROVE_OPTION) return;
        File tep = chon.getSelectedFile();
        if (!tep.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx"))
            tep = new File(tep + ".xlsx");
        if (tep.exists()
                && JOptionPane.showConfirmDialog(
                                cha,
                                "Ghi đè tệp " + tep.getName() + "?",
                                "Xuất Excel",
                                JOptionPane.YES_NO_OPTION)
                        != JOptionPane.YES_OPTION) return;
        String[] cot = new String[bang.getColumnCount()];
        Object[][] dong = new Object[bang.getRowCount()][cot.length];
        for (int c = 0; c < cot.length; c++) {
            cot[c] = bang.getColumnName(c);
            for (int r = 0; r < dong.length; r++) dong[r][c] = bang.getValueAt(r, c);
        }
        JTable snapshot = new JTable(new javax.swing.table.DefaultTableModel(dong, cot));
        final File dich = tep;
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                ghiBang(snapshot, dich, ten);
                return null;
            }

            protected void done() {
                try {
                    get();
                    HopThoai.hienThiThanhCong(
                            cha,
                            "Đã xuất "
                                    + dong.length
                                    + " dòng đang lọc:\n"
                                    + dich.getAbsolutePath());
                } catch (Exception ex) {
                    HopThoai.hienThiLoi(
                            cha,
                            "Không xuất được Excel: "
                                    + (ex.getCause() == null
                                            ? ex.getMessage()
                                            : ex.getCause().getMessage()));
                }
            }
        }.execute();
    }

    /** Chuỗi luôn được ghi như văn bản, không thực thi công thức từ dữ liệu nhập. */
    public static void ghiBang(JTable bang, File tep, String tieuDe) throws IOException {
        if (bang.getColumnCount() == 0)
            throw new IllegalArgumentException("Bảng chưa có cột để xuất.");
        try (XSSFWorkbook so = new XSSFWorkbook()) {
            Sheet trang = so.createSheet("Du lieu");
            CellStyle dau = so.createCellStyle();
            org.apache.poi.ss.usermodel.Font chu = so.createFont();
            chu.setBold(true);
            chu.setColor(IndexedColors.WHITE.getIndex());
            dau.setFont(chu);
            dau.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            dau.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle tien = so.createCellStyle();
            tien.setDataFormat(so.createDataFormat().getFormat("#,##0 \"₫\""));
            CellStyle ngay = so.createCellStyle();
            ngay.setDataFormat(so.createDataFormat().getFormat("dd/mm/yyyy"));
            CellStyle gio = so.createCellStyle();
            gio.setDataFormat(so.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
            trang.createRow(0).createCell(0).setCellValue(tieuDe);
            if (bang.getColumnCount() > 1)
                trang.addMergedRegion(new CellRangeAddress(0, 0, 0, bang.getColumnCount() - 1));
            trang.createRow(1)
                    .createCell(0)
                    .setCellValue(
                            "Xuất lúc "
                                    + LocalDateTime.now()
                                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                    + " — "
                                    + bang.getRowCount()
                                    + " dòng");
            Row hangDau = trang.createRow(2);
            for (int cot = 0; cot < bang.getColumnCount(); cot++) {
                Cell o = hangDau.createCell(cot);
                o.setCellValue(bang.getColumnName(cot));
                o.setCellStyle(dau);
            }
            for (int dong = 0; dong < bang.getRowCount(); dong++) {
                Row hang = trang.createRow(dong + 3);
                for (int cot = 0; cot < bang.getColumnCount(); cot++) {
                    Cell o = hang.createCell(cot);
                    Object giaTri = bang.getValueAt(dong, cot);
                    if (giaTri == null) continue;
                    String nhan = bang.getColumnName(cot).toLowerCase(Locale.ROOT);
                    boolean laTien =
                            nhan.matches(
                                            ".*(tiền|giá|doanh thu|công nợ|đã trả|phải trả|chênh"
                                                    + " lệch|tạm tính|giảm|cọc|chi tiêu|thực đếm|dư"
                                                    + " nợ|tổng nhập|thu hóa đơn).*")
                                    && !nhan.contains("%");
                    if (giaTri instanceof java.util.Date v) {
                        o.setCellValue(v);
                        o.setCellStyle(giaTri instanceof java.sql.Timestamp ? gio : ngay);
                    } else if (giaTri instanceof LocalDate v) {
                        o.setCellValue(v);
                        o.setCellStyle(ngay);
                    } else if (giaTri instanceof LocalDateTime v) {
                        o.setCellValue(v);
                        o.setCellStyle(gio);
                    } else if (giaTri instanceof Number v) {
                        o.setCellValue(v.doubleValue());
                        if (laTien) o.setCellStyle(tien);
                    } else {
                        String vanBan = giaTri.toString();
                        if (laTien && vanBan.matches("[-−]?[0-9.,\\s]+[₫đ]?")) {
                            o.setCellValue(
                                    Double.parseDouble(
                                            vanBan.replace("−", "-").replaceAll("[.,\\s₫đ]", "")));
                            o.setCellStyle(tien);
                        } else if (nhan.contains("ngày")
                                && vanBan.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
                            o.setCellValue(
                                    LocalDate.parse(
                                            vanBan, DateTimeFormatter.ofPattern("dd/MM/uuuu")));
                            o.setCellStyle(ngay);
                        } else o.setCellValue(vanBan);
                    }
                }
            }
            trang.createFreezePane(0, 3);
            trang.setAutoFilter(
                    new CellRangeAddress(
                            2, Math.max(2, bang.getRowCount() + 2), 0, bang.getColumnCount() - 1));
            for (int cot = 0; cot < bang.getColumnCount(); cot++) {
                trang.autoSizeColumn(cot);
                trang.setColumnWidth(
                        cot, Math.min(14000, Math.max(3500, trang.getColumnWidth(cot) + 500)));
            }
            trang.getPrintSetup().setLandscape(true);
            trang.setFitToPage(true);
            try (var ra = Files.newOutputStream(tep.toPath())) {
                so.write(ra);
            }
        }
    }

    public static void xuatDuLieu(JComponent cha, String[] tieuDe, Object[][] duLieu, String ten) {
        xuatBang(cha, new JTable(duLieu, tieuDe), ten);
    }

    public static JButton taoNutXuat(JComponent cha, JTable bang, String ten) {
        JButton btn = new JButton("Xuất Excel");
        btn.addActionListener(e -> xuatBang(cha, bang, ten));
        return btn;
    }

    public static JButton taoNutXuat(JComponent cha, JTable bang, String ten, String goiY) {
        JButton btn = taoNutXuat(cha, bang, ten);
        btn.setToolTipText(goiY);
        return btn;
    }
}
