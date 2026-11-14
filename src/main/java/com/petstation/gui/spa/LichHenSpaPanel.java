package com.petstation.gui.spa;

import static com.petstation.gui.chung.TruongNghiepVu.docThoiGian;
import static com.petstation.gui.chung.TruongNghiepVu.taoLuaChonMuc;

import com.petstation.dto.GioHang;
import com.petstation.dto.MucLuaChon;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NguCanhMoBanGhi;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiLichHen;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.spa.LichHen;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.spa.SpaService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Lịch hẹn dịch vụ Spa (Mã: LICH_HEN_SPA)
// Luồng xử lý: GUI -> LichHenSpaService -> LichHenSpaDAOImpl -> SQL (lich_hen, khach_hang,
// thu_cung)
// Mô tả: Đặt lịch hẹn làm đẹp trước cho thú cưng, phân công chuyên viên thực hiện
// Phân hệ: spa
public final class LichHenSpaPanel extends NghiepVuSpaPanel {
    private final JComboBox<String> cheDo =
            new JComboBox<>(new String[] {"Danh sách", "Ngày", "Tuần"});
    private final JComboBox<String> nhanVien = new JComboBox<>(),
            trangThai =
                    new JComboBox<>(
                            new String[] {
                                "Tất cả",
                                TrangThaiLichHen.DA_XAC_NHAN.maDB(),
                                TrangThaiPhieuSpa.TIEP_NHAN.maDB(),
                                TrangThaiLichHen.HOAN_TAT.maDB(),
                                TrangThaiLichHen.DA_HUY.maDB(),
                                "KHONG_DEN"
                            });
    private final BoChonNgay ngay = new BoChonNgay();
    private final JPanel lich = new JPanel();
    private final JPanel content = new JPanel(new CardLayout());
    private boolean dangNap;

    private Consumer<String> khiKhachDen;

    public LichHenSpaPanel(GioHang gio, Consumer<MaManHinh> nav, Consumer<String> khiKhachDen) {
        this(gio, nav);
        this.khiKhachDen = khiKhachDen;
    }

    @Override
    protected void datLichHen() {
        DatLichSpaDialog.mo(this, this::lamMoi);
    }

    @Override
    protected void tiepNhan() {
        if (khiKhachDen != null) khiKhachDen.accept(layDongChon());
        else super.tiepNhan();
    }

    public LichHenSpaPanel(GioHang gio, Consumer<MaManHinh> nav) {
        super(LoaiNghiepVu.LICH_HEN, gio, nav);
        Component bang = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        remove(bang);
        content.setOpaque(false);
        content.add(bang, "Danh sách");
        JScrollPane scroll = new JScrollPane(lich);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        content.add(scroll, "Lịch");
        add(content);
        ThanhDanhSach bar = (ThanhDanhSach) layKhungThaoTac();
        bar.themBoLoc(cheDo);
        bar.themBoLoc("Ngày:", ngay);
        bar.themBoLoc("Nhân viên:", nhanVien);
        bar.themBoLoc("Trạng thái:", trangThai);
        themNut(bar, "+ Đặt lịch hẹn", NutDinhDang.KieuNut.CHINH, this::datLichHen);
        var btnKhachDen =
                themNut(bar, "Khách đã đến", NutDinhDang.KieuNut.THANH_CONG, this::tiepNhan);
        var btnSuaLich = themNut(bar, "Sửa lịch", NutDinhDang.KieuNut.PHU, this::suaLich);
        var btnHuyLich = themNut(bar, "Hủy lịch", NutDinhDang.KieuNut.NGUY_HIEM, this::huyLichHen);
        Runnable capNhatNutLich =
                () -> {
                    int row = layBangDuLieu().getSelectedRow();
                    String tt =
                            row < 0
                                    ? ""
                                    : String.valueOf(
                                            layMoHinh()
                                                    .getValueAt(
                                                            layBangDuLieu()
                                                                    .convertRowIndexToModel(row),
                                                            8));
                    boolean choXacNhan = TrangThaiLichHen.DA_XAC_NHAN.maDB().equals(tt);
                    btnKhachDen.setEnabled(choXacNhan);
                    btnSuaLich.setEnabled(choXacNhan);
                    btnHuyLich.setEnabled(choXacNhan);
                };
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) capNhatNutLich.run();
                        });
        capNhatNutLich.run();
        ganSuKienDupChuot(
                () -> {
                    int row = layBangDuLieu().getSelectedRow();
                    if (row < 0) return;
                    String tt =
                            String.valueOf(
                                    layMoHinh()
                                            .getValueAt(
                                                    layBangDuLieu().convertRowIndexToModel(row),
                                                    8));
                    if (TrangThaiLichHen.DA_XAC_NHAN.maDB().equals(tt)) tiepNhan();
                });
        thayBoLangNgheTimKiem(this::loc);
        trangThai.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean selected,
                            boolean focus) {
                        return super.getListCellRendererComponent(
                                list,
                                DinhDangBang.tenTrangThai(String.valueOf(value)),
                                index,
                                selected,
                                focus);
                    }
                });
        cheDo.addActionListener(e -> loc());
        nhanVien.addActionListener(e -> loc());
        trangThai.addActionListener(e -> loc());
        ngay.addPropertyChangeListener("date", e -> loc());
        napNhanVien();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (nhanVien != null && cheDo != null) napNhanVien();
    }

    @Override
    protected void datLaiBoLoc() {
        dangNap = true;
        try {
            layOTimKiem().setText("");
            if (cheDo.getItemCount() > 0) cheDo.setSelectedIndex(0);
            ngay.datNgayDuocChon(LocalDate.now());
            if (nhanVien.getItemCount() > 0) nhanVien.setSelectedIndex(0);
            if (trangThai.getItemCount() > 0) trangThai.setSelectedIndex(0);
        } finally {
            dangNap = false;
        }
    }

    private void napNhanVien() {
        dangNap = true;
        Object selected = nhanVien.getSelectedItem();
        nhanVien.removeAllItems();
        nhanVien.addItem("Tất cả");
        Set<String> names = new TreeSet<>();
        for (int r = 0; r < layMoHinh().getRowCount(); r++)
            names.add(String.valueOf(layMoHinh().getValueAt(r, 5)));
        for (String n : names) nhanVien.addItem(n);
        if (selected != null && names.contains(selected)) nhanVien.setSelectedItem(selected);
        dangNap = false;
        var request = NguCanhMoBanGhi.lay();
        if (request != null && request.manHinh() == MaManHinh.DAT_LICH_HEN_SPA) {
            for (int r = 0; r < layMoHinh().getRowCount(); r++)
                if (String.valueOf(layMoHinh().getValueAt(r, 0)).equals(request.ma())
                        || String.valueOf(layMoHinh().getValueAt(r, 1)).equals(request.ma()))
                    ngay.datNgayDuocChon(
                            chuyenThoiGian(layMoHinh().getValueAt(r, 6)).toLocalDate());
        }
        loc();
        NguCanhMoBanGhi.apDung(this);
    }

    private static LocalDateTime chuyenThoiGian(Object o) {
        if (o instanceof Date d) {
            return Instant.ofEpochMilli(d.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        if (o instanceof LocalDateTime ldt) return ldt;
        if (o != null) {
            try {
                return LocalDateTime.parse(String.valueOf(o).trim().replace(' ', 'T'));
            } catch (RuntimeException ex) {
                LoggerFactory.getLogger(LichHenSpaPanel.class)
                        .warn("Không đọc được thời gian lịch hẹn '{}', dùng hiện tại.", o, ex);
            }
        }
        return LocalDateTime.now();
    }

    private void loc() {
        if (dangNap) return;
        String mode = String.valueOf(cheDo.getSelectedItem());
        LocalDate date = ngay.layNgayDuocChon();
        if (date == null) return;
        LocalDate start =
                mode.equals("Tuần") ? date.minusDays(date.getDayOfWeek().getValue() - 1) : date;
        LocalDate end = start.plusDays(mode.equals("Tuần") ? 7 : 1);
        apDungBoLocBangLichHen(mode, start, end);
        ((CardLayout) content.getLayout())
                .show(content, mode.equals("Danh sách") ? "Danh sách" : "Lịch");
        dungLichTuanNgay(mode, start, end);
    }

    @SuppressWarnings("unchecked")
    private void apDungBoLocBangLichHen(String mode, LocalDate start, LocalDate end) {
        String query = layOTimKiem().getText().trim().toLowerCase(Locale.ROOT);
        ((TableRowSorter<TableModel>) layBangDuLieu().getRowSorter())
                .setRowFilter(
                        new RowFilter<>() {
                            @Override
                            public boolean include(
                                    Entry<? extends TableModel, ? extends Integer> e) {
                                StringBuilder text = new StringBuilder();
                                for (int i = 0; i < e.getValueCount(); i++)
                                    text.append(e.getStringValue(i)).append(' ');
                                LocalDate d = chuyenThoiGian(e.getValue(6)).toLocalDate();
                                String ttChon = String.valueOf(trangThai.getSelectedItem());
                                boolean khopTrangThai =
                                        "Tất cả".equals(ttChon)
                                                || ttChon.equalsIgnoreCase(e.getStringValue(8))
                                                || ttChon.equalsIgnoreCase(
                                                        DinhDangBang.tenTrangThai(
                                                                e.getStringValue(8)));
                                return text.toString().toLowerCase(Locale.ROOT).contains(query)
                                        && ("Tất cả".equals(nhanVien.getSelectedItem())
                                                || e.getStringValue(5)
                                                        .equals(nhanVien.getSelectedItem()))
                                        && khopTrangThai
                                        && (mode.equals("Danh sách")
                                                || !d.isBefore(start) && d.isBefore(end));
                            }
                        });
    }

    private void dungLichTuanNgay(String mode, LocalDate start, LocalDate end) {
        lich.removeAll();
        lich.setLayout(new GridLayout(1, mode.equals("Tuần") ? 7 : 1, 8, 0));
        lich.setBackground(MauUngDung.NEN_KEM);
        for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
            lich.add(taoCotNgayLichHen(mode, d));
        }
        lich.revalidate();
        lich.repaint();
    }

    private JPanel taoCotNgayLichHen(String mode, LocalDate d) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(Color.WHITE);
        column.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        column.add(new JLabel(d.format(DinhDang.THU_NGAN_VI)));
        column.add(Box.createVerticalStrut(8));
        List<Integer> rows = new ArrayList<>();
        for (int i = 0; i < layBangDuLieu().getRowCount(); i++) {
            int r = layBangDuLieu().convertRowIndexToModel(i);
            if (chuyenThoiGian(layMoHinh().getValueAt(r, 6)).toLocalDate().equals(d)) rows.add(r);
        }
        rows.sort(Comparator.comparing(r -> chuyenThoiGian(layMoHinh().getValueAt(r, 6))));
        for (int r : rows) {
            column.add(taoTheLichHen(mode, r));
            column.add(Box.createVerticalStrut(6));
        }
        column.add(Box.createVerticalGlue());
        return column;
    }

    private JButton taoTheLichHen(String mode, int r) {
        String chuyenThoiGian =
                chuyenThoiGian(layMoHinh().getValueAt(r, 6)).toLocalTime()
                        + "–"
                        + chuyenThoiGian(layMoHinh().getValueAt(r, 7)).toLocalTime();
        String text =
                chuyenThoiGian
                        + " · "
                        + layMoHinh().getValueAt(r, 3)
                        + " · "
                        + layMoHinh().getValueAt(r, 4)
                        + " · "
                        + layMoHinh().getValueAt(r, 5)
                        + " · "
                        + layMoHinh().getValueAt(r, 8);
        JButton card =
                new JButton(
                        "<html><b>"
                                + chuyenThoiGian
                                + "</b><br>"
                                + esc(layMoHinh().getValueAt(r, 3))
                                + "<br>"
                                + esc(layMoHinh().getValueAt(r, 4))
                                + "<br>"
                                + esc(layMoHinh().getValueAt(r, 5))
                                + "<br>"
                                + esc(layMoHinh().getValueAt(r, 8))
                                + "</html>");
        card.setHorizontalAlignment(SwingConstants.LEFT);
        card.setToolTipText(text);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        card.setPreferredSize(new Dimension(mode.equals("Tuần") ? 145 : 400, 125));
        card.addActionListener(
                e -> {
                    int view = layBangDuLieu().convertRowIndexToView(r);
                    if (view >= 0) layBangDuLieu().setRowSelectionInterval(view, view);
                    card.setBorder(BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 2));
                });
        return card;
    }

    private static String esc(Object o) {
        return String.valueOf(o).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static BieuMauTruong.TruongDuLieu macDinh(BieuMauTruong.TruongDuLieu f, Object value) {
        return new BieuMauTruong.TruongDuLieu(
                f.khoa(), f.nhan(), value == null ? "" : value.toString(), f.cacLuaChon());
    }

    @SuppressWarnings("unchecked")
    private void suaLich() {
        String maLichHen = layDongChon();
        SpaService service = new SpaService();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        new Object[] {
                            service.layChiTietLichHen(maLichHen),
                            layNghiepVuService().layLuaChonThuCung(),
                            layNghiepVuService().layLuaChonGoiDichVu(),
                            layNghiepVuService().layLuaChonNhanVien()
                        },
                null,
                duLieu -> {
                    LichHen old = (LichHen) duLieu[0];
                    var values =
                            BieuMauTruong.layYeuCauNhap(
                                    this,
                                    "Sửa lịch Spa",
                                    macDinh(
                                            taoLuaChonMuc(
                                                    "pet",
                                                    "Thú cưng",
                                                    (List<MucLuaChon>) duLieu[1]),
                                            old.getThuCung() != null
                                                    ? old.getThuCung().getMaThuCung()
                                                    : ""),
                                    macDinh(
                                            taoLuaChonMuc(
                                                    "package",
                                                    "Dịch vụ",
                                                    (List<MucLuaChon>) duLieu[2]),
                                            old.getDichVu() != null
                                                    ? old.getDichVu().getMaGoi()
                                                    : ""),
                                    macDinh(
                                            taoLuaChonMuc(
                                                    "staff",
                                                    "Nhân viên",
                                                    (List<MucLuaChon>) duLieu[3]),
                                            old.getNhanVien() != null
                                                    ? old.getNhanVien().getMaNhanVien()
                                                    : ""),
                                    BieuMauTruong.TruongDuLieu.chuyenThanhChuoi(
                                            "start",
                                            "Bắt đầu (yyyy-MM-dd HH:mm)",
                                            old.getBatDau() == null
                                                    ? ""
                                                    : old.getBatDau()
                                                            .format(DinhDang.NGAY_GIO_ISO)));
                    if (values == null) return;
                    TacVuNen.chayCoPhien(
                            phien,
                            () -> true,
                            () -> {
                                service.suaLichHen(
                                        maLichHen,
                                        values.get("pet"),
                                        values.get("package"),
                                        values.get("staff"),
                                        docThoiGian(values, "start"));
                                return null;
                            },
                            null,
                            v -> lamMoi(),
                            ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }
}
