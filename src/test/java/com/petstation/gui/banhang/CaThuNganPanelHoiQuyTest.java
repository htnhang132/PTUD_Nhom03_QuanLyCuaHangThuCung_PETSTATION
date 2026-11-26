package com.petstation.gui.banhang;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.dao.banhang.PhienLamViecDAO;
import com.petstation.dto.PhienDangNhap;
import com.petstation.dto.TomTatCaLamViec;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.hethong.PhienDangNhapService;

import org.junit.jupiter.api.Test;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Proxy;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class CaThuNganPanelHoiQuyTest {
    @Test
    void ketCaChanThieuTienDemLyDoVaNguoiNhanTruocKhiGhi() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    AtomicInteger soLanGhi = new AtomicInteger();
                    try {
                        TaiKhoan tk = new TaiKhoan("QA");
                        tk.setVaiTro(VaiTro.QUAN_LY_CUA_HANG);
                        PhienDangNhapService.macDinh()
                                .datPhienDangNhap(
                                        new PhienDangNhap(
                                                tk,
                                                new NhanVien("QA"),
                                                EnumSet.allOf(Quyen.class)));
                        PhienLamViecDAO dao =
                                (PhienLamViecDAO)
                                        Proxy.newProxyInstance(
                                                PhienLamViecDAO.class.getClassLoader(),
                                                new Class<?>[] {PhienLamViecDAO.class},
                                                (p, m, a) -> {
                                                    if (m.getName().equals("dongCoBanGiao"))
                                                        soLanGhi.incrementAndGet();
                                                    if (m.getReturnType() == Optional.class)
                                                        return Optional.empty();
                                                    if (m.getReturnType() == List.class)
                                                        return List.of();
                                                    if (m.getReturnType() == long.class) return 0L;
                                                    if (m.getReturnType() == int.class) return 0;
                                                    return null;
                                                });
                        CaThuNganPanel panel = new CaThuNganPanel(new CaThuNganService(dao));
                        var apDung =
                                CaThuNganPanel.class.getDeclaredMethod(
                                        "apDungTomTatCa", TomTatCaLamViec.class);
                        apDung.setAccessible(true);
                        apDung.invoke(
                                panel,
                                new TomTatCaLamViec(
                                        true,
                                        "Ca sáng",
                                        LocalTime.of(7, 30),
                                        100000,
                                        100000,
                                        50000,
                                        50000,
                                        250000,
                                        "",
                                        "",
                                        List.of()));
                        chupNeuYeuCau(panel, "ca-thu-ngan-doi-soat");
                        JTextField thucDem = truong(panel, "oTienThucDem", JTextField.class);
                        JLabel loi = truong(panel, "nhanDongLoi", JLabel.class);
                        JButton ketCa = truong(panel, "btnNutDong", JButton.class);
                        assertThat(thucDem.getText()).isEmpty();
                        ketCa.doClick();
                        assertThat(loi.getText()).isNotBlank();
                        thucDem.setText("200000");
                        ketCa.doClick();
                        assertThat(loi.getText()).contains("ghi chú");
                        thucDem.setText("250000");
                        truong(panel, "cboHinhThuc", JComboBox.class).setSelectedIndex(1);
                        ketCa.doClick();
                        assertThat(loi.getText()).contains("người nhận");
                        assertThat(soLanGhi.get()).isZero();
                    } catch (Exception ex) {
                        throw new AssertionError(ex);
                    } finally {
                        PhienDangNhapService.macDinh().datPhienDangNhap(null);
                    }
                });
    }

    private static <T> T truong(CaThuNganPanel panel, String ten, Class<T> kieu) throws Exception {
        var f = CaThuNganPanel.class.getDeclaredField(ten);
        f.setAccessible(true);
        return kieu.cast(f.get(panel));
    }

    private static void chupNeuYeuCau(CaThuNganPanel panel, String ten) throws Exception {
        String thuMuc = System.getProperty("petstation.chupManHinhThuMuc");
        if (thuMuc == null) return;
        new File(thuMuc).mkdirs();
        for (int[] kichThuoc : new int[][] {{1100, 680}, {1366, 768}}) {
            panel.setSize(kichThuoc[0], kichThuoc[1]);
            boTri(panel);
            BufferedImage anh =
                    new BufferedImage(kichThuoc[0], kichThuoc[1], BufferedImage.TYPE_INT_RGB);
            var g = anh.createGraphics();
            panel.printAll(g);
            g.dispose();
            ImageIO.write(anh, "png", new File(thuMuc, ten + "-" + kichThuoc[0] + ".png"));
        }
    }

    private static void boTri(Container khung) {
        khung.doLayout();
        for (var con : khung.getComponents()) if (con instanceof Container c) boTri(c);
    }
}
