package com.petstation.gui.chung.trogiup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.gui.banhang.ThuTienDialog;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.DanhMucNhieuTabPanel;
import com.petstation.gui.chung.HopThoaiTroGiup;
import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.nhansu.LichTuanPanel;

import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

class TroGiupManHinhTest {
    @Test
    void bieuMauCoNutTroGiupVaPhimAltF1Rieng() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    JDialog dialog = new JDialog();
                    try {
                        var nut = HopThoaiTroGiup.taoNutTroGiup(dialog, ThuTienDialog.class);
                        assertThat(nut.getText()).isEqualTo("Trợ giúp (Alt+F1)");
                        assertThat(nut.getActionListeners()).hasSize(1);
                        assertThat(
                                        dialog.getRootPane()
                                                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                                .get(KeyStroke.getKeyStroke("alt F1")))
                                .isNotNull();
                        assertThat(SoTayTroGiup.layTheoLop(ThuTienDialog.class).tieuDe)
                                .isEqualTo("Thu tiền cọc lưu trú");
                    } finally {
                        dialog.dispose();
                    }
                });
    }

    @Test
    void moiManHinhCuTheDeuCoHuongDanRieng() throws Exception {
        Path goc = Path.of("src/main/java");
        try (var tep = Files.walk(goc.resolve("com/petstation/gui"))) {
            for (Path java : tep.filter(p -> p.toString().endsWith("Panel.java")).toList()) {
                String tenLop =
                        goc.relativize(java).toString().replace('\\', '.').replace('/', '.');
                Class<?> lop =
                        Class.forName(
                                tenLop.replace(".java", ""), false, getClass().getClassLoader());
                if (!CoSoPanel.class.isAssignableFrom(lop)
                        || Modifier.isAbstract(lop.getModifiers())
                        || lop.getPackageName().equals("com.petstation.gui.chung")
                        || DanhMucNhieuTabPanel.class.isAssignableFrom(lop)) continue;
                assertThat(SoTayTroGiup.layCacLopDaCoHuongDan())
                        .as("Thiếu hướng dẫn riêng cho %s", lop.getSimpleName())
                        .contains(lop.getSimpleName());
            }
        }
    }

    @Test
    void tatCaNoiDungCoCungCauTrucVaKhongConChiDanCu() {
        for (String lop : SoTayTroGiup.layCacLopDaCoHuongDan()) {
            NoiDungHuongDan hd = SoTayTroGiup.layDaDangKy(lop);
            assertThat(hd.tieuDe).isNotBlank().doesNotContain("Panel", "Trợ giúp");
            assertThat(hd.mucDich).isNotBlank();
            assertThat(hd.cacBuoc).hasSizeBetween(2, 5).allSatisfy(s -> assertThat(s).isNotBlank());
            assertThat(hd.luuY).isNotEmpty();
            assertThat(hd.phimTat).allSatisfy(p -> assertThat(p).hasSize(2));
            assertThat(hd.phimTat.stream().map(p -> p[0])).contains("Alt + F1", "Esc");
            assertThat(String.join(" ", hd.cacBuoc) + String.join(" ", hd.luuY))
                    .doesNotContain(
                            "10.000",
                            "USB/điện thoại",
                            "Xem thống kê",
                            "Thông tin & VietQR",
                            "Bán hàng, Kho & Lưu trú",
                            "Mở đơn tạm");
        }
        assertThat(SoTayTroGiup.layDaDangKy("ThongKeCongNoPanel").luuY.get(0))
                .contains("ngày nhập", "cập nhật hiện tại", "không phải ảnh chụp");
        assertThat(SoTayTroGiup.layDaDangKy("ThanhToanPanel").luuY.get(0))
                .contains("chưa tự nhận thông báo");
        assertThat(SoTayTroGiup.layDaDangKy("DanhSachTaiKhoanPanel").luuY)
                .anySatisfy(s -> assertThat(s).contains("Không có thao tác xóa"));
    }

    @Test
    void lichThayDoiHuongDanTheoBaCheDo() {
        LichTuanPanel lich = mock(LichTuanPanel.class);
        List<String> tieuDe = new ArrayList<>();
        for (LichTuanPanel.CheDo cheDo : LichTuanPanel.CheDo.values()) {
            when(lich.layCheDo()).thenReturn(cheDo);
            NoiDungHuongDan hd = SoTayTroGiup.lay(lich, "", "");
            tieuDe.add(hd.tieuDe);
            if (cheDo == LichTuanPanel.CheDo.CA_NHAN) {
                assertThat(hd.cacBuoc).anySatisfy(s -> assertThat(s).contains("Yêu cầu đổi ca"));
            }
        }
        assertThat(tieuDe)
                .doesNotHaveDuplicates()
                .containsExactlyInAnyOrder(
                        "Phân lịch nhân viên", "Lịch cá nhân", "Tra cứu lịch làm việc");
    }

    @Test
    void tatCaCuaSoTroGiupXuongDongVaKhongCatChu() throws Exception {
        LookAndFeel cu = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.invokeAndWait(
                    () -> {
                        for (String lop : SoTayTroGiup.layCacLopDaCoHuongDan()) {
                            JPanel khung =
                                    HopThoaiTroGiup.taoGiaoDien(SoTayTroGiup.layDaDangKy(lop));
                            for (int rong : new int[] {540, 624, 850})
                                kiemTraBoCuc(khung, lop, rong);
                        }
                    });
        } finally {
            UIManager.setLookAndFeel(cu);
        }
    }

    private static void kiemTraBoCuc(JPanel khung, String lop, int rong) {
        BufferedImage anh = new BufferedImage(rong, 440, BufferedImage.TYPE_INT_RGB);
        khung.setSize(rong, 440);
        for (int lan = 0; lan < 8; lan++) {
            boTri(khung);
            Graphics2D g = anh.createGraphics();
            g.setColor(MauUngDung.NEN_KEM);
            g.fillRect(0, 0, anh.getWidth(), anh.getHeight());
            khung.printAll(g);
            g.dispose();
        }
        for (JTextArea vanBan : tim(khung, JTextArea.class)) {
            try {
                var cuoi = vanBan.modelToView2D(vanBan.getDocument().getLength());
                assertThat(cuoi).as("%s ở %d px: %s", lop, rong, vanBan.getText()).isNotNull();
                assertThat(cuoi.getMaxY())
                        .as("Không cắt dòng cuối: %s", vanBan.getText())
                        .isLessThanOrEqualTo(vanBan.getHeight() + 1.0);
                assertThat(cuoi.getMaxX()).isLessThanOrEqualTo(vanBan.getWidth() + 1.0);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
        for (JLabel nhan : tim(khung, JLabel.class)) {
            assertThat(nhan.getWidth())
                    .as("Nhãn %s", nhan.getText())
                    .isGreaterThanOrEqualTo(nhan.getPreferredSize().width);
        }
        luuAnh(anh, lop + "-" + rong + ".png");
    }

    private static void boTri(Container goc) {
        goc.invalidate();
        goc.doLayout();
        for (Component con : goc.getComponents()) if (con instanceof Container hop) boTri(hop);
    }

    private static <T extends Component> List<T> tim(Container goc, Class<T> kieu) {
        List<T> ds = new ArrayList<>();
        for (Component con : goc.getComponents()) {
            if (kieu.isInstance(con)) ds.add(kieu.cast(con));
            if (con instanceof Container hop) ds.addAll(tim(hop, kieu));
        }
        return ds;
    }

    private static void luuAnh(BufferedImage anh, String ten) {
        String thuMuc = System.getProperty("petstation.chupManHinhThuMuc");
        if (thuMuc == null) return;
        try {
            Path duongDan = Path.of(thuMuc, "tro-giup", ten);
            Files.createDirectories(duongDan.getParent());
            ImageIO.write(anh, "png", duongDan.toFile());
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
