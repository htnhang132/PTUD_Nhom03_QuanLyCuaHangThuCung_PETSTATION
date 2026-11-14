package com.petstation.gui.khachhang;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.gui.chung.thanhphan.BoSoanThaoTruong;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

public final class BoSoanThaoTiemPhong implements BoSoanThaoTruong {

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new String[] {"Loại vắc-xin", "Ngày tiêm", "Ngày hết hạn", "Ghi chú"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

    private final JTable bang = new JTable(model);
    private final JPanel panel = new JPanel(new BorderLayout(5, 6));

    private final JComboBox<String> cboLoai =
            new JComboBox<>(
                    new String[] {
                        "Vắc-xin phòng Dại (Rabies)",
                        "Vắc-xin 7 bệnh (Chó - Care, Parvo,...)",
                        "Vắc-xin 5 bệnh (Chó)",
                        "Vắc-xin 4 bệnh (Mèo)",
                        "Vắc-xin tổng hợp",
                        "Mũi nhắc lại định kỳ"
                    });

    private final JTextField oNgayTiem = new JTextField(LocalDate.now().format(DinhDang.NGAY), 9);
    private final JTextField oNgayHan =
            new JTextField(LocalDate.now().plusYears(1).format(DinhDang.NGAY), 9);
    private final JTextField oGhiChu = new JTextField(12);

    private final JButton btnThem = new NutDinhDang("+ Thêm mũi tiêm", NutDinhDang.KieuNut.PHU);
    private final JButton btnSua = new NutDinhDang("Sửa mũi tiêm", NutDinhDang.KieuNut.PHU);
    private final JButton btnXoa = new NutDinhDang("Xóa mũi tiêm", NutDinhDang.KieuNut.NGUY_HIEM);

    public BoSoanThaoTiemPhong() {
        DinhDangBang.apDung(bang);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.setOpaque(false);
        panel.add(taoVungNhapTiemPhong(), BorderLayout.NORTH);
        if (bang.getColumnModel().getColumnCount() >= 4) {
            int[] rongCot = {130, 85, 85, 100};
            for (int i = 0; i < rongCot.length; i++)
                bang.getColumnModel().getColumn(i).setPreferredWidth(rongCot[i]);
        }
        JScrollPane cuon = DinhDangBang.taoBangKhongCuon(bang, new Dimension(320, 110));
        panel.add(cuon, BorderLayout.CENTER);
        this.cuon = cuon;
        ganSuKienBoSoanTiemPhong();
        capNhatKichThuocBang();
    }

    private JPanel taoVungNhapTiemPhong() {
        cboLoai.setEditable(true);
        cboLoai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        cboLoai.setPreferredSize(new Dimension(160, 32));
        oNgayTiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oNgayTiem.setPreferredSize(new Dimension(100, 32));
        oNgayHan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oNgayHan.setPreferredSize(new Dimension(100, 32));
        oGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oGhiChu.setPreferredSize(new Dimension(140, 32));
        oGhiChu.putClientProperty("JTextField.placeholderText", "Ghi chú / Số lô / Sổ tiêm...");
        JPanel nhap = new JPanel(new GridLayout(2, 2, 8, 6));
        nhap.setOpaque(false);
        nhap.add(DinhDangBang.taoHangNhap("Loại vắc-xin", cboLoai, 3, 12));
        nhap.add(DinhDangBang.taoHangNhap("Ghi chú / Phòng khám", oGhiChu, 3, 12));
        nhap.add(DinhDangBang.taoHangNhap("Ngày tiêm *", taoOTruongCoNutLich(oNgayTiem), 3, 12));
        nhap.add(
                DinhDangBang.taoHangNhap(
                        "Ngày hết hạn (tùy chọn)", taoOTruongCoNutLich(oNgayHan), 3, 12));
        Border viNut = BorderFactory.createEmptyBorder(4, 8, 4, 8);
        btnThem.setBorder(viNut);
        btnSua.setBorder(viNut);
        btnXoa.setBorder(viNut);
        JPanel nut = new JPanel(new GridLayout(1, 3, 8, 0));
        nut.setOpaque(false);
        nut.add(btnThem);
        nut.add(btnSua);
        nut.add(btnXoa);
        JPanel dau = new JPanel(new BorderLayout(0, 8));
        dau.setOpaque(false);
        dau.add(nhap, BorderLayout.CENTER);
        dau.add(nut, BorderLayout.SOUTH);
        return dau;
    }

    private static JPanel taoOTruongCoNutLich(JTextField oNgay) {
        JButton btnLich = BoChonNgay.taoNutLich(oNgay, DinhDang.NGAY);
        btnLich.setPreferredSize(new Dimension(32, 32));
        JPanel khungTam = new JPanel(new BorderLayout(4, 0));
        khungTam.setOpaque(false);
        khungTam.add(oNgay, BorderLayout.CENTER);
        khungTam.add(btnLich, BorderLayout.EAST);
        return khungTam;
    }

    private void ganSuKienBoSoanTiemPhong() {
        btnThem.addActionListener(e -> capNhat(false));
        btnSua.addActionListener(e -> capNhat(true));
        btnXoa.addActionListener(
                e -> DinhDangBang.xoaDongDangChon(bang, model, this::capNhatKichThuocBang));
        bang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting() || bang.getSelectedRow() < 0) return;
                            int r = bang.convertRowIndexToModel(bang.getSelectedRow());
                            cboLoai.setSelectedItem(String.valueOf(model.getValueAt(r, 0)));
                            oNgayTiem.setText(String.valueOf(model.getValueAt(r, 1)));
                            oNgayHan.setText(
                                    model.getValueAt(r, 2) == null
                                            ? ""
                                            : String.valueOf(model.getValueAt(r, 2)));
                            oGhiChu.setText(
                                    model.getValueAt(r, 3) == null
                                            ? ""
                                            : String.valueOf(model.getValueAt(r, 3)));
                        });
    }

    private final JScrollPane cuon;

    private void capNhatKichThuocBang() {
        DinhDangBang.coGianChieuCaoBangTheoNoiDung(bang, cuon, panel, model.getRowCount());
    }

    private void capNhat(boolean laSua) {
        try {
            Object chon = cboLoai.getSelectedItem();
            String loai = chon == null ? "" : chon.toString().trim();
            if (loai.isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn hoặc nhập tên loại vắc-xin.");
            }
            LocalDate ngayTiem =
                    BoChonNgay.docNgayLinhHoat(oNgayTiem.getText().trim(), DinhDang.NGAY);
            if (ngayTiem == null) {
                throw new IllegalArgumentException(
                        "Ngày tiêm không hợp lệ (định dạng dd/MM/yyyy).");
            }
            if (ngayTiem.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày tiêm không được ở tương lai.");
            }
            String hanStr = oNgayHan.getText().trim();
            LocalDate ngayHan =
                    hanStr.isEmpty() ? null : BoChonNgay.docNgayLinhHoat(hanStr, DinhDang.NGAY);
            if (ngayHan != null && ngayHan.isBefore(ngayTiem)) {
                throw new IllegalArgumentException("Ngày hết hạn không được trước ngày tiêm.");
            }
            int dong =
                    laSua && bang.getSelectedRow() >= 0
                            ? bang.convertRowIndexToModel(bang.getSelectedRow())
                            : -1;
            if (laSua && dong < 0) {
                throw new IllegalArgumentException(
                        "Hãy chọn một mũi tiêm trong bảng bên dưới để sửa.");
            }
            Object[] duLieu = {
                loai,
                ngayTiem.format(DinhDang.NGAY),
                ngayHan == null ? "" : ngayHan.format(DinhDang.NGAY),
                oGhiChu.getText().trim()
            };
            DinhDangBang.capNhatHoacThemDong(bang, model, laSua, dong, duLieu);
            capNhatKichThuocBang();
        } catch (Exception ex) {
            HopThoai.hienThiCanhBao(panel, ex.getMessage());
        }
    }

    @Override
    public JComponent layThanhPhan() {
        return panel;
    }

    @Override
    public String lay() {
        StringBuilder kq = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i > 0) kq.append(';');
            String loai =
                    String.valueOf(model.getValueAt(i, 0)).replace("|", "/").replace(";", ",");
            String ngayTiemStr = String.valueOf(model.getValueAt(i, 1));
            LocalDate nt = BoChonNgay.docNgayLinhHoat(ngayTiemStr, DinhDang.NGAY);
            String ntIso = nt == null ? ngayTiemStr : nt.toString();
            String ngayHanStr =
                    model.getValueAt(i, 2) == null
                            ? ""
                            : String.valueOf(model.getValueAt(i, 2)).trim();
            LocalDate nh =
                    ngayHanStr.isEmpty()
                            ? null
                            : BoChonNgay.docNgayLinhHoat(ngayHanStr, DinhDang.NGAY);
            String nhIso = nh == null ? "" : nh.toString();
            String ghiChu =
                    model.getValueAt(i, 3) == null
                            ? ""
                            : String.valueOf(model.getValueAt(i, 3))
                                    .replace("|", "/")
                                    .replace(";", ",");
            kq.append(loai)
                    .append('|')
                    .append(ntIso)
                    .append('|')
                    .append(nhIso)
                    .append('|')
                    .append(ghiChu);
        }
        return kq.toString();
    }

    @Override
    public void dat(String giaTri) {
        model.setRowCount(0);
        if (giaTri != null && !giaTri.isBlank()) {
            for (String dong : giaTri.split(";")) {
                if (dong.isBlank()) continue;
                String[] p = dong.split("\\|", -1);
                if (p.length >= 2) {
                    String loai = p[0].trim();
                    LocalDate nt = BoChonNgay.docNgayLinhHoat(p[1].trim(), DinhDang.NGAY);
                    String ntHienThi = nt != null ? nt.format(DinhDang.NGAY) : p[1].trim();
                    String nhHienThi = "";
                    if (p.length >= 3 && !p[2].isBlank()) {
                        LocalDate nh = BoChonNgay.docNgayLinhHoat(p[2].trim(), DinhDang.NGAY);
                        nhHienThi = nh != null ? nh.format(DinhDang.NGAY) : p[2].trim();
                    }
                    String ghiChu = p.length >= 4 ? p[3].trim() : "";
                    model.addRow(new Object[] {loai, ntHienThi, nhHienThi, ghiChu});
                }
            }
        }
        capNhatKichThuocBang();
    }

    @Override
    public void datDuocBat(boolean duocBat) {
        bang.setEnabled(duocBat);
        cboLoai.setEnabled(duocBat);
        oNgayTiem.setEnabled(duocBat);
        oNgayHan.setEnabled(duocBat);
        oGhiChu.setEnabled(duocBat);
        btnThem.setEnabled(duocBat);
        btnSua.setEnabled(duocBat);
        btnXoa.setEnabled(duocBat);
    }
}
