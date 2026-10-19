package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.*;

import com.petstation.gui.components.common.HoaDonDocPanel;
import com.petstation.model.*;
import com.petstation.service.CaThuNganService;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.util.*;

class HoaDonDocPanelTest {
    @Test
    void hoaDonDoc80mmNgatTrangKhongMatTrangCuoi() throws Exception {
        var dong = new ArrayList<GioHang.DongHang>();
        for (int i = 0; i < 40; i++)
            dong.add(
                    new GioHang.DongHang(
                            "SP" + i,
                            "Thức ăn chăm sóc thú cưng tên dài cần hiển thị đầy đủ — quy cách 800g",
                            123000,
                            2));
        var bienLai =
                new GioHang.BienLai(
                        "HD-TEST",
                        LocalDateTime.of(2026, 10, 20, 8, 30),
                        dong,
                        9840000,
                        0,
                        9840000,
                        CaThuNganService.PhuongThuc.CASH,
                        10000000,
                        160000,
                        "Khách lẻ",
                        "",
                        "Thu ngân");
        var thongTin =
                new ChiTietBienLai(
                        bienLai,
                        "Pet Station",
                        "TP. Hồ Chí Minh",
                        "0900000000",
                        "",
                        984,
                        null,
                        "PRODUCT",
                        "",
                        0,
                        "");
        HoaDonDocPanel giay = new HoaDonDocPanel();
        giay.datDuLieu(thongTin);
        giay.setSize(420, giay.getPreferredSize().height);
        assertThat(giay.getPreferredSize().height).isGreaterThan(420);
        assertThat(giay.getScrollableTracksViewportWidth()).isTrue();
        Paper kho = new Paper();
        kho.setSize(HoaDonDocPanel.RONG_GIAY, 300);
        kho.setImageableArea(5, 5, HoaDonDocPanel.RONG_GIAY - 10, 290);
        PageFormat trang = new PageFormat();
        trang.setPaper(kho);
        var anh = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_RGB);
        var net = anh.createGraphics();
        int soTrang = 0;
        while (giay.print(net, trang, soTrang) == Printable.PAGE_EXISTS && soTrang < 100) soTrang++;
        assertThat(soTrang).isGreaterThan(1).isLessThan(100);
        assertThat(giay.print(net, trang, soTrang)).isEqualTo(Printable.NO_SUCH_PAGE);
        net.dispose();
        assertThat(thongTin.tongSoLuong()).isEqualTo(80);
        assertThat(thongTin.soDuDiemSau()).isNull();
    }
}
