package com.petstation.gui;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.kho.KiemKePanel;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.KiemKeService;

import org.junit.jupiter.api.Test;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

class KiemKeHoiQuyTest {
    private static final class BangThu extends CoSoPanel {
        JTable tao() {
            return layBang(
                    taoBangDinhDang(new String[] {"Sản phẩm"}, new Object[][] {{"Hàng mẫu"}}));
        }
    }

    @Test
    void bangLocTrongVeThongBaoNgayKhiBangDuocVeLai() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    JTable bang = new BangThu().tao();
                    @SuppressWarnings("unchecked")
                    TableRowSorter<DefaultTableModel> loc =
                            (TableRowSorter<DefaultTableModel>) bang.getRowSorter();
                    loc.setRowFilter(RowFilter.regexFilter("khongcoQA"));
                    bang.setSize(500, 300);
                    BufferedImage anh = new BufferedImage(500, 300, BufferedImage.TYPE_INT_RGB);
                    var g = anh.createGraphics();
                    bang.paint(g);
                    g.dispose();
                    int diemChu = 0;
                    for (int y = 130; y < 170; y++)
                        for (int x = 20; x < 480; x++) {
                            int rgb = anh.getRGB(x, y);
                            if (((rgb >> 16) & 255) < 160
                                    && ((rgb >> 8) & 255) < 160
                                    && (rgb & 255) < 160) diemChu++;
                        }
                    assertThat(bang.getRowCount()).isZero();
                    assertThat(diemChu)
                            .as("Thông báo phải có ngay khi JTable tự vẽ lại sau lọc")
                            .isGreaterThan(30);
                });
    }

    private static Object goi(Object panel, String ten, Class<?>[] kieu, Object... args)
            throws Exception {
        Method m = KiemKePanel.class.getDeclaredMethod(ten, kieu);
        m.setAccessible(true);
        return m.invoke(panel, args);
    }

    @Test
    void quetLanDauVaLocVanGiuDuHaiLo() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        TaiKhoan taiKhoan = new TaiKhoan();
                        taiKhoan.setVaiTro(VaiTro.QUAN_LY_CUA_HANG);
                        PhienDangNhapService.macDinh()
                                .datPhienDangNhap(
                                        new PhienDangNhap(
                                                taiKhoan,
                                                new NhanVien("TEST"),
                                                EnumSet.allOf(Quyen.class)));
                        KiemKePanel panel = new KiemKePanel(null, id -> {});
                        DefaultTableModel model =
                                (DefaultTableModel)
                                        goi(panel, "taoMoHinhBangKiemKe", new Class<?>[] {});
                        JTable table = new JTable(model);
                        List<Object[]> lo =
                                List.of(
                                        new Object[] {
                                            "L1", "SP1", "Sản phẩm A", "123", "A", null, 10, "Nhóm"
                                        },
                                        new Object[] {
                                            "L2", "SP2", "Sản phẩm B", "456", "B", null, 4, "Nhóm"
                                        });
                        goi(
                                panel,
                                "dienDuLieuBangKiemKe",
                                new Class<?>[] {
                                    List.class,
                                    DefaultTableModel.class,
                                    String.class,
                                    boolean.class,
                                    Runnable.class
                                },
                                lo,
                                model,
                                "",
                                false,
                                (Runnable) () -> {});
                        assertThat(model.getValueAt(0, 7)).isEqualTo("");
                        JTextField scan = new JTextField("123");
                        KeyAdapter listener =
                                (KeyAdapter)
                                        goi(
                                                panel,
                                                "taoBoQuetBarcodeKiemKe",
                                                new Class<?>[] {
                                                    JTextField.class,
                                                    DefaultTableModel.class,
                                                    JTable.class,
                                                    JLabel.class
                                                },
                                                scan,
                                                model,
                                                table,
                                                new JLabel());
                        listener.keyPressed(
                                new KeyEvent(
                                        scan, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER, '\n'));
                        assertThat(model.getValueAt(0, 7)).isEqualTo(1);
                        model.setValueAt("Đếm thực tế", 0, 9);
                        model.setValueAt(4, 1, 7);
                        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                        table.setRowSorter(sorter);
                        sorter.setRowFilter(RowFilter.regexFilter("B", 4));
                        assertThat(table.getRowCount()).isEqualTo(1);
                        @SuppressWarnings("unchecked")
                        List<KiemKeService.DongKiemKe> dong =
                                (List<KiemKeService.DongKiemKe>)
                                        goi(
                                                panel,
                                                "thuThapDongKiemKe",
                                                new Class<?>[] {
                                                    javax.swing.JDialog.class,
                                                    DefaultTableModel.class,
                                                    JTable.class
                                                },
                                                null,
                                                model,
                                                table);
                        assertThat(dong).extracting(d -> d.maLo).containsExactly("L1", "L2");
                    } catch (Exception ex) {
                        throw new AssertionError(ex);
                    } finally {
                        PhienDangNhapService.macDinh().datPhienDangNhap(null);
                    }
                });
    }

    @Test
    void nhanVienChuaCoMaVaKhoaBamSauKhiGanMa() {
        NhanVien a = new NhanVien();
        NhanVien b = new NhanVien();
        assertThat(a).isNotEqualTo(b);
        HashSet<NhanVien> tap = new HashSet<>();
        tap.add(a);
        a.setMaNhanVien("NV1");
        assertThat(tap).contains(a);
        b.setMaNhanVien("NV1");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
