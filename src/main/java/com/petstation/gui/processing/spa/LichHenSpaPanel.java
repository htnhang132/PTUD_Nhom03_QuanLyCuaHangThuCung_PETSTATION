package com.petstation.gui.processing.spa;

import static com.petstation.gui.components.TruongNghiepVu.*;

import com.petstation.gui.components.*;
import com.petstation.gui.components.common.*;
import com.petstation.model.*;
import com.petstation.model.MaManHinh;
import com.petstation.service.*;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.*;

/** Lịch ngày/tuần và danh sách dùng chung selection, tìm kiếm và hành động. */
public final class LichHenSpaPanel extends NghiepVuSpaPanel {
    private final JComboBox<String> cheDo =
            new JComboBox<>(new String[] {"Danh sách", "Ngày", "Tuần"});
    private final JComboBox<String> nhanVien = new JComboBox<>(),
            trangThai =
                    new JComboBox<>(
                            new String[] {
                                "Tất cả",
                                "SCHEDULED",
                                "RECEIVED",
                                "COMPLETED",
                                "CANCELLED",
                                "NO_SHOW"
                            });
    private final BoChonNgay ngay = new BoChonNgay();
    private final JPanel lich = new JPanel();
    private final JPanel content = new JPanel(new CardLayout());
    private boolean dangNap, choNap;

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
        ThanhDanhSach bar = (ThanhDanhSach) pnlCacThaoTac;
        bar.themBoLoc(cheDo);
        bar.themBoLoc("Ngày:", ngay);
        bar.themBoLoc("Nhân viên:", nhanVien);
        bar.themBoLoc("Trạng thái:", trangThai);
        themNut(bar, "+ Đặt lịch hẹn", this::datLichHen);
        themNut(bar, "Sửa lịch", this::suaLich);
        themNut(bar, "Tiếp nhận", this::tiepNhan);
        themNut(bar, "Hủy lịch", this::huyLichHen);
        for (var l :
                ((javax.swing.text.AbstractDocument) txtTimKiem.getDocument())
                        .getDocumentListeners()) txtTimKiem.getDocument().removeDocumentListener(l);
        txtTimKiem
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                loc();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                loc();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                loc();
                            }
                        });
        trangThai.setRenderer(
                new javax.swing.DefaultListCellRenderer() {
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

    private void napNhanVien() {
        dangNap = true;
        Object selected = nhanVien.getSelectedItem();
        nhanVien.removeAllItems();
        nhanVien.addItem("Tất cả");
        Set<String> names = new TreeSet<>();
        for (int r = 0; r < moHinh.getRowCount(); r++)
            names.add(String.valueOf(moHinh.getValueAt(r, 5)));
        for (String n : names) nhanVien.addItem(n);
        if (selected != null && names.contains(selected)) nhanVien.setSelectedItem(selected);
        dangNap = false;
        var request = com.petstation.gui.navigation.NguCanhMoBanGhi.lay();
        if (request != null && request.manHinh() == MaManHinh.DAT_LICH_HEN_SPA) {
            for (int r = 0; r < moHinh.getRowCount(); r++)
                if (((Number) moHinh.getValueAt(r, 0)).longValue() == request.id())
                    ngay.datNgayDuocChon(time(moHinh.getValueAt(r, 6)).toLocalDate());
        }
        loc();
        com.petstation.gui.navigation.NguCanhMoBanGhi.apDung(this);
    }

    private static LocalDateTime time(Object o) {
        return ((java.sql.Timestamp) o).toLocalDateTime();
    }

    @SuppressWarnings("unchecked")
    private void loc() {
        if (dangNap) return;
        String mode = String.valueOf(cheDo.getSelectedItem());
        LocalDate date = ngay.layNgayDuocChon();
        if (date == null) return;
        LocalDate start =
                mode.equals("Tuần") ? date.minusDays(date.getDayOfWeek().getValue() - 1) : date;
        LocalDate end = start.plusDays(mode.equals("Tuần") ? 7 : 1);
        String query = txtTimKiem.getText().trim().toLowerCase(Locale.ROOT);
        ((TableRowSorter<TableModel>) tblBang.getRowSorter())
                .setRowFilter(
                        new RowFilter<>() {
                            public boolean include(
                                    Entry<? extends TableModel, ? extends Integer> e) {
                                StringBuilder text = new StringBuilder();
                                for (int i = 0; i < e.getValueCount(); i++)
                                    text.append(e.getStringValue(i)).append(' ');
                                LocalDate d = time(e.getValue(6)).toLocalDate();
                                return text.toString().toLowerCase(Locale.ROOT).contains(query)
                                        && ("Tất cả".equals(nhanVien.getSelectedItem())
                                                || e.getStringValue(5)
                                                        .equals(nhanVien.getSelectedItem()))
                                        && ("Tất cả".equals(trangThai.getSelectedItem())
                                                || e.getStringValue(8)
                                                        .equals(trangThai.getSelectedItem()))
                                        && (mode.equals("Danh sách")
                                                || !d.isBefore(start) && d.isBefore(end));
                            }
                        });
        ((CardLayout) content.getLayout())
                .show(content, mode.equals("Danh sách") ? "Danh sách" : "Lịch");
        lich.removeAll();
        lich.setLayout(new GridLayout(1, mode.equals("Tuần") ? 7 : 1, 8, 0));
        lich.setBackground(MauUngDung.NEN_KEM);
        for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setBackground(Color.WHITE);
            column.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JLabel title =
                    new JLabel(
                            d.format(
                                    DateTimeFormatter.ofPattern(
                                            "EEE dd/MM", Locale.forLanguageTag("vi"))));
            column.add(title);
            column.add(Box.createVerticalStrut(8));
            java.util.List<Integer> rows = new ArrayList<>();
            for (int i = 0; i < tblBang.getRowCount(); i++) {
                int r = tblBang.convertRowIndexToModel(i);
                if (time(moHinh.getValueAt(r, 6)).toLocalDate().equals(d)) rows.add(r);
            }
            rows.sort(Comparator.comparing(r -> time(moHinh.getValueAt(r, 6))));
            for (int r : rows) {
                String time =
                        time(moHinh.getValueAt(r, 6)).toLocalTime()
                                + "–"
                                + time(moHinh.getValueAt(r, 7)).toLocalTime();
                String text =
                        time
                                + " · "
                                + moHinh.getValueAt(r, 3)
                                + " · "
                                + moHinh.getValueAt(r, 4)
                                + " · "
                                + moHinh.getValueAt(r, 5)
                                + " · "
                                + moHinh.getValueAt(r, 8);
                JButton card =
                        new JButton(
                                "<html><b>"
                                        + time
                                        + "</b><br>"
                                        + esc(moHinh.getValueAt(r, 3))
                                        + "<br>"
                                        + esc(moHinh.getValueAt(r, 4))
                                        + "<br>"
                                        + esc(moHinh.getValueAt(r, 5))
                                        + "<br>"
                                        + esc(moHinh.getValueAt(r, 8))
                                        + "</html>");
                card.setHorizontalAlignment(SwingConstants.LEFT);
                card.setToolTipText(text);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
                card.setPreferredSize(new Dimension(mode.equals("Tuần") ? 145 : 400, 125));
                card.addActionListener(
                        e -> {
                            int view = tblBang.convertRowIndexToView(r);
                            if (view >= 0) tblBang.setRowSelectionInterval(view, view);
                            card.setBorder(
                                    BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 2));
                        });
                column.add(card);
                column.add(Box.createVerticalStrut(6));
            }
            column.add(Box.createVerticalGlue());
            lich.add(column);
        }
        lich.revalidate();
        lich.repaint();
    }

    private static String esc(Object o) {
        return String.valueOf(o).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static BieuMauSQL.TruongDuLieu macDinh(BieuMauSQL.TruongDuLieu f, Object value) {
        return new BieuMauSQL.TruongDuLieu(
                f.khoa(), f.nhan(), value == null ? "" : value.toString(), f.cacLuaChon());
    }

    private void suaLich() {
        long id = layDongChon();
        Object[] old = SpaService.layChiTietLichHen(id);
        var values =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Sửa lịch Spa",
                        macDinh(layThuCung(), old[0]),
                        macDinh(layCacGoiDichVu(), old[1]),
                        macDinh(
                                taoLuaChon(
                                        "staff",
                                        "Nhân viên",
                                        LuaChonNghiepVuService.layLuaChonNhanVien()),
                                old[2]),
                        layTruongVanBan(
                                "start",
                                "Bắt đầu (yyyy-MM-dd HH:mm)",
                                time(old[3])
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        if (values != null)
            SpaService.suaLichHen(
                    id,
                    values.get("pet"),
                    values.get("package"),
                    values.get("staff"),
                    docThoiGian(values, "start"));
    }
}
