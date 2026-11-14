package com.petstation.gui.banhang;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.petstation.dto.ChiTietBienLai;
import com.petstation.dto.GioHang;
import com.petstation.model.chung.PhuongThuc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

class HoaDonDocPanelPdfTest {

    @Test
    void xuatPdfTaoTepPdfHopLe(@TempDir Path thuMucTam) throws Exception {
        var dong = new GioHang.DongHang("SP001", "Hạt Royal Canin 2kg", 250000L, 2);
        var bienLai =
                new GioHang.BienLai(
                        "HD000123",
                        LocalDateTime.of(2026, 11, 14, 15, 30),
                        List.of(dong),
                        500000L,
                        0L,
                        500000L,
                        PhuongThuc.TIEN_MAT,
                        500000L,
                        0L,
                        "Nguyễn Văn An",
                        "",
                        "Huỳnh Tấn Nhàng");
        var chiTiet =
                new ChiTietBienLai(
                        bienLai,
                        "PET STATION",
                        "123 Nguyễn Văn Cừ, TP.HCM",
                        "0901234567",
                        "0909888777",
                        5,
                        125L,
                        "BAN_HANG",
                        "",
                        0L,
                        "");
        Path pdfPath = thuMucTam.resolve("HoaDon_HD000123.pdf");
        HoaDonDocPanel.xuatPdf(chiTiet, pdfPath.toFile());

        assertTrue(Files.exists(pdfPath));
        byte[] bytes = Files.readAllBytes(pdfPath);
        assertTrue(bytes.length > 500);
        String text = new String(bytes, StandardCharsets.US_ASCII);
        assertTrue(text.startsWith("%PDF-1.4\n"));
        assertTrue(text.contains("/Subtype /Image"));
        assertTrue(text.contains("/Filter /FlateDecode"));
        assertTrue(text.contains("xref\n"));
        assertTrue(text.endsWith("%%EOF\n"));
    }
}
