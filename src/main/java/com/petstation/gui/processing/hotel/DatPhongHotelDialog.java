package com.petstation.gui.processing.hotel;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.service.LuaChonNghiepVuService;
import com.petstation.service.LuuTruService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Biểu mẫu đặt/sửa phòng có chọn ngày giờ và xem trước giá, cọc. */
public final class DatPhongHotelDialog {
    private final Component parent;
    private final Long hopDongId;
    private final JDialog dialog;
    private final JComboBox<BieuMauSQL.LuaChon> cboThuCung;
    private final JComboBox<BieuMauSQL.LuaChon> cboPhong;
    private final JSpinner spNgayVao;
    private final JSpinner spNgayRa;
    private final JTextArea txtGhiChu = new JTextArea(3, 24);
    private final JLabel lblSoNgay = new JLabel("—");
    private final JLabel lblTongTien = new JLabel("—");
    private final JLabel lblCoc = new JLabel("—");
    private final JLabel lblTrangThai = new JLabel(" ");
    private final JButton btnLuu = new JButton("Lưu đặt phòng");
    private Long ketQua;

    private DatPhongHotelDialog(Component parent, Long hopDongId, String maPhongMacDinh) {
        this.parent = parent;
        this.hopDongId = hopDongId;
        this.dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                hopDongId == null ? "Đặt phòng Pet Hotel" : "Sửa đặt phòng Pet Hotel",
                Dialog.ModalityType.APPLICATION_MODAL);

        List<BieuMauSQL.LuaChon> pets =
                LuaChonNghiepVuService.layLuaChonThuCung().stream()
                        .map(d -> new BieuMauSQL.LuaChon(
                                String.valueOf(d[0]), String.valueOf(d[1])))
                        .toList();
        List<BieuMauSQL.LuaChon> rooms =
                LuaChonNghiepVuService.layLuaChonPhong().stream()
                        .map(d -> new BieuMauSQL.LuaChon(
                                String.valueOf(d[0]), String.valueOf(d[1])))
                        .toList();

        if (pets.isEmpty())
            throw new IllegalArgumentException("Chưa có thú cưng đang hoạt động để đặt lưu trú.");
        if (rooms.isEmpty())
            throw new IllegalArgumentException("Không có phòng đang sẵn sàng nhận đặt chỗ.");

        cboThuCung = new JComboBox<>(pets.toArray(BieuMauSQL.LuaChon[]::new));
        cboPhong = new JComboBox<>(rooms.toArray(BieuMauSQL.LuaChon[]::new));

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        spNgayVao = taoChonNgay(now);
        spNgayRa = taoChonNgay(now.plusDays(1));

        if (hopDongId != null) napDuLieuSua();
        else chonGiaTri(cboPhong, maPhongMacDinh);

        taoGiaoDien();
        cboThuCung.addActionListener(e -> capNhatDuKien());
        cboPhong.addActionListener(e -> capNhatDuKien());
        spNgayVao.addChangeListener(e -> capNhatDuKien());
        spNgayRa.addChangeListener(e -> capNhatDuKien());
        capNhatDuKien();

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(680, 570);
        dialog.setMinimumSize(new Dimension(620, 520));
        dialog.setLocationRelativeTo(parent);
    }

    /** Mở biểu mẫu và trả về ID hợp đồng vừa tạo/sửa; null khi người dùng đóng. */
    public static Long mo(Component parent, Long hopDongId, String maPhongMacDinh) {
        DatPhongHotelDialog form = new DatPhongHotelDialog(parent, hopDongId, maPhongMacDinh);
        form.dialog.setVisible(true);
        return form.ketQua;
    }

    private void taoGiaoDien() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel(
                hopDongId == null ? "THÔNG TIN ĐẶT PHÒNG" : "CẬP NHẬT ĐẶT PHÒNG");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(7, 5, 7, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        int row = 0;
        themDong(form, gc, row++, "Thú cưng *", cboThuCung);
        themDong(form, gc, row++, "Phòng *", cboPhong);
        themDong(form, gc, row++, "Ngày/giờ nhận phòng *", spNgayVao);
        themDong(form, gc, row++, "Ngày/giờ trả phòng *", spNgayRa);

        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        themDong(form, gc, row++, "Ghi chú", new JScrollPane(txtGhiChu));

        JPanel estimate = new JPanel(new GridLayout(0, 2, 12, 8));
        estimate.setBorder(BorderFactory.createTitledBorder("Dự kiến chi phí"));
        estimate.add(new JLabel("Số ngày tính phí:"));
        estimate.add(lblSoNgay);
        estimate.add(new JLabel("Tổng tiền phòng:"));
        estimate.add(lblTongTien);
        estimate.add(new JLabel("Tiền cọc yêu cầu (50%):"));
        estimate.add(lblCoc);

        JPanel lower = new JPanel(new BorderLayout(8, 8));
        lower.add(estimate, BorderLayout.NORTH);
        lblTrangThai.setForeground(new Color(150, 40, 35));
        lower.add(lblTrangThai, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHuy = new JButton("Đóng");
        btnHuy.addActionListener(e -> dialog.dispose());
        btnLuu.addActionListener(e -> luu());
        actions.add(btnHuy);
        actions.add(btnLuu);
        lower.add(actions, BorderLayout.SOUTH);

        root.add(new JScrollPane(form), BorderLayout.CENTER);
        root.add(lower, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(btnLuu);
    }

    private static void themDong(
            JPanel panel, GridBagConstraints gc, int row, String nhan, Component component) {
        gc.gridy = row;
        gc.gridx = 0;
        gc.weightx = 0;
        gc.gridwidth = 1;
        panel.add(new JLabel(nhan), gc);
        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(component, gc);
    }

    private static JSpinner taoChonNgay(LocalDateTime value) {
        Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
        JSpinner spinner = new JSpinner(
                new SpinnerDateModel(date, null, null, Calendar.MINUTE));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm"));
        return spinner;
    }

    private static LocalDateTime layNgay(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                .withSecond(0).withNano(0);
    }

    private static boolean chonGiaTri(
            JComboBox<BieuMauSQL.LuaChon> combo, String giaTri) {
        if (giaTri == null || giaTri.isBlank()) return false;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).giaTri().equals(giaTri)) {
                combo.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Giữ nguyên lựa chọn cũ khi bản ghi đã không còn nằm trong danh sách chọn mới.
     * Không tự thay thú cưng/phòng bằng phần tử đầu tiên của JComboBox.
     */
    private static void chonHoacThemGiaTriCu(
            JComboBox<BieuMauSQL.LuaChon> combo, String giaTri, String nhan) {
        if (!chonGiaTri(combo, giaTri)) {
            combo.addItem(new BieuMauSQL.LuaChon(giaTri, nhan));
            chonGiaTri(combo, giaTri);
        }
    }

    private String giaTriDangChon(JComboBox<BieuMauSQL.LuaChon> combo) {
        Object selected = combo.getSelectedItem();
        return selected instanceof BieuMauSQL.LuaChon value ? value.giaTri() : "";
    }

    private void napDuLieuSua() {
        Object[] data = LuuTruService.layThongTinDatPhong(hopDongId);
        String petCu = String.valueOf(data[1]);
        String roomCu = String.valueOf(data[2]);
        chonHoacThemGiaTriCu(
                cboThuCung, petCu,
                petCu + " - Thú cưng đang gắn với hợp đồng (cần kiểm tra trạng thái)");
        chonHoacThemGiaTriCu(
                cboPhong, roomCu,
                roomCu + " - Phòng hiện tại (cần kiểm tra trạng thái)");
        spNgayVao.setValue(Date.from(
                ((java.sql.Timestamp) data[3]).toLocalDateTime()
                        .atZone(ZoneId.systemDefault()).toInstant()));
        spNgayRa.setValue(Date.from(
                ((java.sql.Timestamp) data[4]).toLocalDateTime()
                        .atZone(ZoneId.systemDefault()).toInstant()));
        txtGhiChu.setText(data[5] == null ? "" : data[5].toString());

        boolean daNhanCoc =
                data[7] instanceof Boolean b
                        ? b
                        : data[7] instanceof Number n && n.intValue() != 0;
        long daThu = daNhanCoc ? ((Number) data[6]).longValue() : 0;
        if (daThu > 0) {
            lblTrangThai.setText(
                    "Đã nhận cọc: " + TienTe.dinhDang(daThu)
                    + ". Khi sửa, hệ thống giữ nguyên số tiền đã thu.");
        }
        btnLuu.setText("Lưu thay đổi");
    }

    private void capNhatDuKien() {
        try {
            String maPhong = giaTriDangChon(cboPhong);
            LocalDateTime vao = layNgay(spNgayVao);
            LocalDateTime ra = layNgay(spNgayRa);
            if (!ra.isAfter(vao))
                throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");

            long[] estimate =
                    LuuTruService.tinhTienPhongDuKien(maPhong, vao, ra);
            lblSoNgay.setText(estimate[0] + " ngày");
            lblTongTien.setText(TienTe.dinhDang(estimate[1]));
            lblCoc.setText(TienTe.dinhDang(estimate[2]));
            if (lblTrangThai.getText().isBlank()) lblTrangThai.setText(" ");
            btnLuu.setEnabled(true);
        } catch (RuntimeException ex) {
            lblSoNgay.setText("—");
            lblTongTien.setText("—");
            lblCoc.setText("—");
            if (hopDongId == null || lblTrangThai.getText().isBlank())
                lblTrangThai.setText(ex.getMessage());
            btnLuu.setEnabled(false);
        }
    }

    private void luu() {
        try {
            String pet = giaTriDangChon(cboThuCung);
            String room = giaTriDangChon(cboPhong);
            LocalDateTime start = layNgay(spNgayVao);
            LocalDateTime end = layNgay(spNgayRa);
            String note = txtGhiChu.getText().trim();

            if (pet.isBlank() || room.isBlank())
                throw new IllegalArgumentException("Vui lòng chọn thú cưng và phòng.");
            if (!end.isAfter(start))
                throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");

            btnLuu.setEnabled(false);
            if (hopDongId == null) {
                ketQua = LuuTruService.giuPhong(pet, room, start, end, note);
            } else {
                LuuTruService.suaDatPhong(hopDongId, pet, room, start, end, note);
                ketQua = hopDongId;
            }
            dialog.dispose();
        } catch (RuntimeException ex) {
            btnLuu.setEnabled(true);
            HopThoai.hienThiCanhBao(dialog, ex.getMessage());
        }
    }
}