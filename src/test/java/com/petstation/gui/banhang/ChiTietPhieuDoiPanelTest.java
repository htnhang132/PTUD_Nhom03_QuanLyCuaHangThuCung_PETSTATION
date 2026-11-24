package com.petstation.gui.banhang;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.dto.ChiTietXemTruocPhieuDoi;
import com.petstation.gui.chung.HopThoai;
import com.petstation.model.banhang.*;
import com.petstation.model.kho.*;
import com.petstation.model.nhansu.NhanVien;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

class ChiTietPhieuDoiPanelTest {
    @Test
    void tieuDeChiVietHoaChuDauVaGiuMaChungTu() {
        assertThat(HopThoai.chuanHoaTieuDe("  KIỂM KÊ   TỒN KHO  ")).isEqualTo("Kiểm kê tồn kho");
        assertThat(HopThoai.chuanHoaTieuDe("Chi Tiết Phiếu DT00000001"))
                .isEqualTo("Chi tiết phiếu DT00000001");
    }

    @Test
    void chiTietXuLyTenDaiVaTienChenhLechRoRang() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    ChiTietPhieuDoiPanel panel = new ChiTietPhieuDoiPanel();
                    panel.hienThi(duLieu());
                    for (int width : new int[] {350, 460}) {
                        panel.setSize(width, 1050);
                        for (int i = 0; i < 4; i++) layout(panel);
                        assertThat(timNhan(panel, "Khách trả thêm")).isTrue();
                        kiemTraChuKhongBiCat(panel);
                        BufferedImage image =
                                new BufferedImage(width, 1050, BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics = image.createGraphics();
                        panel.paint(graphics);
                        graphics.dispose();
                        try {
                            File folder = new File("target/ui-revision");
                            folder.mkdirs();
                            ImageIO.write(
                                    image,
                                    "png",
                                    new File(folder, "chi-tiet-doi-tra-" + width + ".png"));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    private static ChiTietXemTruocPhieuDoi duLieu() {
        PhieuDoiTra p = new PhieuDoiTra("DT00000001");
        p.setHoaDonGoc(new HoaDon("HD2610190009"));
        p.setHoaDonThayThe(new HoaDon("HD2610190010"));
        NhanVien nv = new NhanVien("NV01");
        nv.setHoTen("Phan Trần Thảo Vy");
        p.setNguoiLap(nv);
        p.setTrangThai("HOAN_TAT");
        p.setGiaTriDuocDoi(240000);
        p.setChenhLech(10000);
        p.setLyDo("Khách đổi cỡ vòng cổ lớn hơn cho cún cưng");
        SanPham sp = new SanPham("SP01");
        sp.setTenSanPham("Vòng cổ da bò kèm chuông Hand in Hand dành cho thú cưng");
        ChiTietHoaDon goc = new ChiTietHoaDon();
        goc.setSanPham(sp);
        ChiTietDoiTra tra = new ChiTietDoiTra();
        tra.setChiTietHoaDon(goc);
        LoHang lo = new LoHang();
        lo.setMaLo("LO00000009");
        tra.setLoHang(lo);
        tra.setSoLuong(1);
        tra.setTienHoan(240000);
        ChiTietHoaDon moi = new ChiTietHoaDon();
        moi.setTenHang("Dây dắt kèm yếm ngực phản quang đi dạo cao cấp cho chó cỡ vừa");
        moi.setSoLuong(1);
        moi.setDonGia(250000);
        return new ChiTietXemTruocPhieuDoi(p, List.of(tra), List.of(moi));
    }

    private static void layout(Component component) {
        component.doLayout();
        if (component instanceof Container container)
            for (Component child : container.getComponents()) layout(child);
    }

    private static boolean timNhan(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label && text.equals(label.getText())) return true;
            if (component instanceof Container child && timNhan(child, text)) return true;
        }
        return false;
    }

    private static void kiemTraChuKhongBiCat(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextArea text && !text.getText().isBlank()) {
                assertThat(text.getHeight())
                        .as(text.getText())
                        .isGreaterThanOrEqualTo(text.getPreferredSize().height);
                assertThat(text.getWidth()).isPositive();
            }
            if (component instanceof Container child) kiemTraChuKhongBiCat(child);
        }
    }
}
