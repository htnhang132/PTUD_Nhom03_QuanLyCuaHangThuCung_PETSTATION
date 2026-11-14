package com.petstation.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

/** Tiện ích ghi dữ liệu bảng ra tệp Excel (.xlsx). */
public final class GhiExcel {
    private static final Logger LOG = LoggerFactory.getLogger(GhiExcel.class);

    private GhiExcel() {}

    public static void ghiDuLieu(String[] cot, Object[][] dong, File tep, String tieuDe)
            throws IOException {
        if (cot == null || cot.length == 0) {
            throw new IllegalArgumentException("Bảng chưa có cột để xuất.");
        }
        Object[][] duLieu = dong == null ? new Object[0][cot.length] : dong;
        try (XSSFWorkbook so = new XSSFWorkbook()) {
            Sheet trang = so.createSheet("Du lieu");
            CellStyle dau = taoStyleTieuDe(so);
            CellStyle tien = taoStyleDinhDang(so, "#,##0 \"₫\"");
            CellStyle ngay = taoStyleDinhDang(so, "dd/mm/yyyy");
            CellStyle gio = taoStyleDinhDang(so, "dd/mm/yyyy hh:mm");
            taoPhanDauTrang(trang, cot, duLieu.length, tieuDe, dau);
            for (int r = 0; r < duLieu.length; r++) {
                Row hang = trang.createRow(r + 3);
                for (int c = 0; c < cot.length; c++) {
                    ghiGiaTriO(hang.createCell(c), duLieu[r][c], cot[c], tien, ngay, gio);
                }
            }
            hoanThienBoCucTrang(trang, cot.length, duLieu.length);
            try (var ra = Files.newOutputStream(tep.toPath())) {
                so.write(ra);
            }
        }
    }

    private static CellStyle taoStyleTieuDe(XSSFWorkbook so) {
        CellStyle dau = so.createCellStyle();
        Font chu = so.createFont();
        chu.setBold(true);
        chu.setColor(IndexedColors.WHITE.getIndex());
        dau.setFont(chu);
        dau.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        dau.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return dau;
    }

    private static CellStyle taoStyleDinhDang(XSSFWorkbook so, String mau) {
        CellStyle style = so.createCellStyle();
        style.setDataFormat(so.createDataFormat().getFormat(mau));
        return style;
    }

    private static void taoPhanDauTrang(
            Sheet trang, String[] cot, int soDong, String tieuDe, CellStyle dau) {
        trang.createRow(0).createCell(0).setCellValue(tieuDe == null ? "" : tieuDe);
        if (cot.length > 1) {
            trang.addMergedRegion(new CellRangeAddress(0, 0, 0, cot.length - 1));
        }
        trang.createRow(1)
                .createCell(0)
                .setCellValue(
                        "Xuất lúc "
                                + LocalDateTime.now().format(DinhDang.NGAY_GIO)
                                + " — "
                                + soDong
                                + " dòng");
        Row hangDau = trang.createRow(2);
        for (int c = 0; c < cot.length; c++) {
            Cell o = hangDau.createCell(c);
            o.setCellValue(cot[c]);
            o.setCellStyle(dau);
        }
    }

    private static void ghiGiaTriO(
            Cell o, Object giaTri, String tenCot, CellStyle tien, CellStyle ngay, CellStyle gio) {
        if (giaTri == null) return;
        String nhan = tenCot == null ? "" : tenCot.toLowerCase(Locale.ROOT);
        boolean laTien =
                nhan.matches(
                                ".*(tiền|giá|doanh thu|công nợ|đã trả|phải trả|chênh"
                                        + " lệch|tạm tính|giảm|cọc|chi tiêu|thực đếm|dư"
                                        + " nợ|tổng nhập|thu hóa đơn).*")
                        && !nhan.contains("%");
        if (giaTri instanceof Date v) {
            o.setCellValue(v);
            o.setCellStyle(giaTri instanceof Timestamp ? gio : ngay);
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
            ghiChuoiO(o, giaTri.toString(), nhan, laTien, tien, ngay);
        }
    }

    private static void ghiChuoiO(
            Cell o, String vanBan, String nhan, boolean laTien, CellStyle tien, CellStyle ngay) {
        if (laTien && vanBan.matches("[-−]?[0-9.,\\s]+[₫đ]?")) {
            try {
                String chuan =
                        vanBan.replace("−", "-")
                                .replaceAll("[\\s₫đ]", "")
                                .replaceFirst("[.,]00$", "");
                o.setCellValue(Double.parseDouble(chuan.replaceAll("[.,]", "")));
                o.setCellStyle(tien);
                return;
            } catch (NumberFormatException ex) {
                LOG.debug("Giữ nguyên dạng chuỗi cho ô tiền '{}': {}", vanBan, ex.getMessage());
            }
        }
        if (nhan.contains("ngày") && vanBan.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            try {
                o.setCellValue(LocalDate.parse(vanBan, DinhDang.NGAY_U));
                o.setCellStyle(ngay);
                return;
            } catch (RuntimeException ex) {
                LOG.debug("Giữ nguyên dạng chuỗi cho ô ngày '{}': {}", vanBan, ex.getMessage());
            }
        }
        o.setCellValue(vanBan);
    }

    private static void hoanThienBoCucTrang(Sheet trang, int soCot, int soDong) {
        trang.createFreezePane(0, 3);
        trang.setAutoFilter(new CellRangeAddress(2, Math.max(2, soDong + 2), 0, soCot - 1));
        for (int c = 0; c < soCot; c++) {
            trang.autoSizeColumn(c);
            trang.setColumnWidth(c, Math.min(14000, Math.max(3500, trang.getColumnWidth(c) + 500)));
        }
        trang.getPrintSetup().setLandscape(true);
        trang.setFitToPage(true);
    }
}
