package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.*;

import com.petstation.gui.components.common.XuatExcel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.table.*;

class XuatExcelTest {
    @TempDir Path thuMuc;

    @Test
    void xuatDungDongDangLocCotDangHienThiVaKieuDuLieu() throws Exception {
        var moHinh =
                new DefaultTableModel(
                        new Object[][] {
                            {1L, "Ẩn", "100.000₫", LocalDate.of(2026, 10, 19)},
                            {2L, "=1+2", "250.000₫", LocalDate.of(2026, 10, 20)}
                        },
                        new String[] {"ID", "Tên", "Thành tiền", "Ngày"});
        JTable bang = new JTable(moHinh);
        bang.removeColumn(bang.getColumnModel().getColumn(0));
        var loc = new TableRowSorter<>(moHinh);
        bang.setRowSorter(loc);
        loc.setRowFilter(RowFilter.regexFilter("=1", 1));
        var tep = thuMuc.resolve("kiem-thu.xlsx").toFile();
        XuatExcel.ghiBang(bang, tep, "Bảng đang lọc");
        try (var so = new XSSFWorkbook(tep)) {
            var trang = so.getSheetAt(0);
            assertThat(trang.getLastRowNum()).isEqualTo(3);
            assertThat(trang.getRow(2).getCell(0).getStringCellValue()).isEqualTo("Tên");
            assertThat(trang.getRow(3).getCell(0).getCellType()).isEqualTo(CellType.STRING);
            assertThat(trang.getRow(3).getCell(0).getStringCellValue()).isEqualTo("=1+2");
            assertThat(trang.getRow(3).getCell(1).getNumericCellValue()).isEqualTo(250000);
            assertThat(trang.getRow(3).getCell(2).getLocalDateTimeCellValue().toLocalDate())
                    .isEqualTo(LocalDate.of(2026, 10, 20));
        }
    }
}
