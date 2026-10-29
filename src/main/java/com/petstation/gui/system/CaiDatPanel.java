package com.petstation.gui.system;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.CaiDatService;

import java.awt.*;
import java.io.File;
import java.util.*;

import javax.print.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Cung cấp giao diện cài đặt hệ thống dạng Tabbed Pane 4 nhóm trực quan: 1. Cửa hàng & Hóa đơn 2.
 * Kho & Hàng hóa 3. Nhân sự & Phân lịch 4. Thư mục lưu trữ & Bảo trì
 */
public final class CaiDatPanel extends CoSoPanel implements HoTroThanhCongCu {
    private final Map<String, JTextField> cacDauVao = new LinkedHashMap<>();
    private final JCheckBox chkBlindCountDefault =
            new JCheckBox("Mặc định bật chế độ kiểm kê mù (Blind Count) cho nhân viên kho");
    private final JComboBox<String> cboMayIn = new JComboBox<>();
    private final boolean coTheSua;

    private final Map<String, String> giaTriDaNap = new LinkedHashMap<>();
    private Object mayInDaNap;
    private boolean blindCountDaNap;

    public CaiDatPanel(boolean coTheSua) {
        this.coTheSua = coTheSua;
        setLayout(new BorderLayout(0, 12));
        add(taoTieuDeMuc("Cài đặt hệ thống"), BorderLayout.NORTH);

        // 1. Tạo JTabbedPane chứa 4 Tab chức năng
        JTabbedPane tabCaiDat = new JTabbedPane();
        tabCaiDat.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // --- TAB 1: Cửa hàng & Hóa đơn ---
        JPanel tab1 = taoTabPanel();
        JPanel pnlThongTinCuaHang = layNhomBox("Thông tin pháp nhân & Liên hệ");
        taoTruong(
                pnlThongTinCuaHang,
                "TEN_CUA_HANG",
                "Tên cửa hàng",
                "Tên hiển thị trên hóa đơn và chứng từ");
        taoTruong(
                pnlThongTinCuaHang,
                "DIA_CHI_CUA_HANG",
                "Địa chỉ cửa hàng",
                "Địa chỉ in trên tiêu đề hóa đơn");
        taoTruong(pnlThongTinCuaHang, "SO_DIEN_THOAI_CUA_HANG", "Số điện thoại / Hotline", "Hotline liên hệ");
        taoTruong(
                pnlThongTinCuaHang, "EMAIL_CUA_HANG", "Email cửa hàng", "Hộp thư chăm sóc khách hàng");
        taoTruong(pnlThongTinCuaHang, "WEBSITE_CUA_HANG", "Website", "Địa chỉ website cửa hàng");
        taoTruong(pnlThongTinCuaHang, "MA_SO_THUE", "Mã số thuế", "Mã số thuế doanh nghiệp");

        JPanel pnlHoaDonVaMayIn = layNhomBox("Bán hàng, Thuế & Thiết bị");
        taoTruong(
                pnlHoaDonVaMayIn,
                "THUE_VAT_PHAN_TRAM",
                "Thuế VAT (%)",
                "Thuế suất GTGT áp dụng chung (0 - 100%)");

        // Máy in
        for (PrintService dichVu : PrintServiceLookup.lookupPrintServices(null, null)) {
            cboMayIn.addItem(dichVu.getName());
        }
        if (cboMayIn.getItemCount() == 0) cboMayIn.addItem("Không có máy in");
        cboMayIn.setEnabled(coTheSua);
        cboMayIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboMayIn.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlHoaDonVaMayIn.add(new JLabel("Máy in hóa đơn mặc định"));
        pnlHoaDonVaMayIn.add(Box.createVerticalStrut(4));
        pnlHoaDonVaMayIn.add(cboMayIn);
        pnlHoaDonVaMayIn.add(Box.createVerticalStrut(6));

        NutDinhDang btnKiemThu =
                new NutDinhDang("In thử trang mẫu", NutDinhDang.KieuNut.TRONG_SUOT);
        btnKiemThu.addActionListener(e -> inThu());
        pnlHoaDonVaMayIn.add(btnKiemThu);
        pnlHoaDonVaMayIn.add(Box.createVerticalStrut(12));

        // VietQR
        taoTruong(
                pnlHoaDonVaMayIn,
                "MA_BIN_NGAN_HANG",
                "Mã BIN ngân hàng VietQR",
                "Mã BIN 6 số ngân hàng (VD: 970436 - Vietcombank)");
        taoTruong(
                pnlHoaDonVaMayIn,
                "SO_TAI_KHOAN_NGAN_HANG",
                "Số tài khoản nhận tiền",
                "Số tài khoản ngân hàng thụ hưởng");
        taoTruong(
                pnlHoaDonVaMayIn,
                "TEN_CHU_TAI_KHOAN",
                "Chủ tài khoản (IN HOA)",
                "Tên chủ tài khoản viết hoa không dấu");

        tab1.add(pnlThongTinCuaHang);
        tab1.add(pnlHoaDonVaMayIn);
        tabCaiDat.addTab("Cửa hàng & Hóa đơn", tab1);

        // --- TAB 2: Kho & Hàng hóa ---
        JPanel tab2 = taoTabPanel();
        JPanel pnlKho1 = layNhomBox("Cảnh báo tồn kho & Hạn dùng");
        taoTruong(
                pnlKho1,
                "NGUONG_TON_KHO_THAP",
                "Ngưỡng tồn thấp cảnh báo",
                "Sản phẩm có tồn kho dưới mức này sẽ hiện cảnh báo");
        taoTruong(
                pnlKho1,
                "SO_NGAY_CANH_BAO_HET_HAN",
                "Cảnh báo trước hạn dùng (ngày)",
                "Lô hàng sắp hết hạn trong khoảng số ngày này sẽ được tô đỏ");
        taoTruong(
                pnlKho1,
                "SO_NGAY_TON_DONG",
                "Ngưỡng hàng tồn đọng / chôn vốn (ngày)",
                "Hàng nhập kho quá số ngày này chưa bán được xem là tồn đọng");

        JPanel pnlKho2 = layNhomBox("Xuất hủy & Kiểm kê");
        taoTruong(
                pnlKho2,
                "HAN_MUC_XUAT_HUY_CAN_DUYET",
                "Hạn mức xuất hủy cần duyệt (đồng)",
                "Phiếu xuất hủy vượt giá trị này cần Quản lý phê duyệt");

        chkBlindCountDefault.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkBlindCountDefault.setEnabled(coTheSua);
        chkBlindCountDefault.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlKho2.add(Box.createVerticalStrut(10));
        pnlKho2.add(chkBlindCountDefault);

        tab2.add(pnlKho1);
        tab2.add(pnlKho2);
        tabCaiDat.addTab("Kho & Hàng hóa", tab2);

        // --- TAB 3: Nhân sự & Phân lịch ---
        JPanel tab3 = taoTabPanel();
        JPanel pnlNhanSu1 = layNhomBox("Chính sách làm việc & Ca trực");
        taoTruong(
                pnlNhanSu1,
                "SO_GIO_TOI_DA_MOI_TUAN",
                "Số giờ làm việc tối đa mỗi tuần (giờ)",
                "Cảnh báo khi phân ca vượt quá số giờ quy định");
        taoTruong(
                pnlNhanSu1,
                "SO_GIO_BAO_TRUOC_DOI_CA",
                "Thời gian báo trước khi đổi ca (giờ)",
                "Nhân viên phải gửi yêu cầu đổi ca trước ca làm ít nhất");

        JPanel pnlNhanSu2 = layNhomBox("Quy định dịch vụ & Đổi trả");
        taoTruong(
                pnlNhanSu2,
                "PHI_TRA_PHONG_TRE",
                "Phí trả phòng lưu trú trễ (đồng/ngày)",
                "Phí phụ thu khi khách nhận thú cưng trễ ngày hẹn");
        taoTruong(
                pnlNhanSu2,
                "SO_NGAY_DOI_TRA",
                "Thời hạn đổi trả phụ kiện (ngày)",
                "Số ngày tối đa cho phép khách hàng đổi trả phụ kiện kể từ ngày mua");

        tab3.add(pnlNhanSu1);
        tab3.add(pnlNhanSu2);
        tabCaiDat.addTab("Nhân sự & Phân lịch", tab3);

        // --- TAB 4: Thư mục lưu trữ & Bảo trì ---
        JPanel tab4 = taoTabPanel();
        JPanel pnlHeThong1 = layNhomBox("Đường dẫn dữ liệu");
        taoTruongThuMuc(
                pnlHeThong1,
                "THU_MUC_ANH_CHAM_SOC",
                "Thư mục lưu trữ hình ảnh chăm sóc thú cưng",
                "Mặc định thư mục PetStation trong thư mục người dùng");

        JPanel pnlHeThong2 = layNhomBox("Khôi phục cấu hình");
        JLabel lblGhiChuBaoTri =
                new JLabel(
                        "<html><i>Có thể khôi phục các tham số hệ thống về giá trị mặc định của nhà"
                                + " sản xuất.</i></html>");
        lblGhiChuBaoTri.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlHeThong2.add(lblGhiChuBaoTri);
        pnlHeThong2.add(Box.createVerticalStrut(14));

        NutDinhDang btnKhoiPhucMacDinh =
                new NutDinhDang("Khôi phục thiết lập chuẩn", NutDinhDang.KieuNut.PHU);
        btnKhoiPhucMacDinh.setEnabled(coTheSua);
        btnKhoiPhucMacDinh.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnKhoiPhucMacDinh.addActionListener(e -> khoiPhucMacDinh());
        pnlHeThong2.add(btnKhoiPhucMacDinh);

        tab4.add(pnlHeThong1);
        tab4.add(pnlHeThong2);
        tabCaiDat.addTab("Lưu trữ & Bảo trì", tab4);

        add(tabCaiDat, BorderLayout.CENTER);

        // 2. Thanh thao tác phía dưới
        JPanel pnlCacThaoTac = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        NutDinhDang btnTaiLai = new NutDinhDang("Làm mới", NutDinhDang.KieuNut.TRONG_SUOT);
        btnTaiLai.setToolTipText("Đọc lại cài đặt đã lưu trong SQL Server");
        btnTaiLai.addActionListener(
                suKien -> {
                    if (coThayDoiChuaLuu()
                            && !HopThoai.xacNhan(
                                    this, "Bỏ thay đổi cài đặt chưa lưu và đọc lại dữ liệu?")) {
                        return;
                    }
                    nap();
                });
        pnlCacThaoTac.add(btnTaiLai);

        NutDinhDang btnLuu = new NutDinhDang("Lưu cài đặt", NutDinhDang.KieuNut.CHINH);
        btnLuu.setEnabled(coTheSua);
        btnLuu.addActionListener(suKien -> xuLyLuu());
        pnlCacThaoTac.add(btnLuu);

        add(pnlCacThaoTac, BorderLayout.SOUTH);
        nap();
    }

    private JPanel taoTabPanel() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 20, 0));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(14, 8, 14, 8));
        return pnl;
    }

    private JPanel layNhomBox(String tieuDe) {
        JPanel pnl = layThe(new BorderLayout());
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                                tieuDe,
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                new Font("Segoe UI", Font.BOLD, 13),
                                new Color(30, 41, 59)),
                        new EmptyBorder(12, 14, 14, 14)));
        return pnl;
    }

    private void taoTruong(JPanel pnl, String khoa, String nhan, String tooltip) {
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setToolTipText(tooltip);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lbl);
        pnl.add(Box.createVerticalStrut(4));

        JTextField txt = new JTextField(24);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setToolTipText(tooltip);
        txt.setEnabled(coTheSua);
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txt.setPreferredSize(new Dimension(280, 36));

        cacDauVao.put(khoa, txt);
        pnl.add(txt);
        pnl.add(Box.createVerticalStrut(10));
    }

    private void taoTruongThuMuc(JPanel pnl, String khoa, String nhan, String tooltip) {
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setToolTipText(tooltip);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lbl);
        pnl.add(Box.createVerticalStrut(4));

        JPanel pnlLine = new JPanel(new BorderLayout(8, 0));
        pnlLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pnlLine.setOpaque(false);

        JTextField txt = new JTextField(24);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setToolTipText(tooltip);
        txt.setEnabled(coTheSua);
        cacDauVao.put(khoa, txt);

        NutDinhDang btnChon = new NutDinhDang("Chọn thư mục...", NutDinhDang.KieuNut.PHU);
        btnChon.setEnabled(coTheSua);
        btnChon.addActionListener(
                e -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (!txt.getText().isBlank()) {
                        chooser.setSelectedFile(new File(txt.getText().trim()));
                    }
                    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        txt.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                });

        pnlLine.add(txt, BorderLayout.CENTER);
        pnlLine.add(btnChon, BorderLayout.EAST);

        pnl.add(pnlLine);
        pnl.add(Box.createVerticalStrut(10));
    }

    private void inThu() {
        JTextArea txtTrang =
                new JTextArea(
                        CaiDatService.lay("TEN_CUA_HANG")
                                + "\n"
                                + CaiDatService.lay("DIA_CHI_CUA_HANG")
                                + "\n"
                                + "Hotline: "
                                + CaiDatService.lay("SO_DIEN_THOAI_CUA_HANG")
                                + "\n"
                                + "--------------------------------\n"
                                + "IN THỬ PET STATION POS\n"
                                + java.time.LocalDateTime.now()
                                + "\n"
                                + "--------------------------------\n"
                                + "Cảm ơn quý khách!\n");
        try {
            txtTrang.print();
        } catch (java.awt.print.PrinterException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Lỗi in thử: " + ngoaiLe.getMessage());
        }
    }

    private void khoiPhucMacDinh() {
        if (!HopThoai.xacNhan(
                this, "Bạn có chắc muốn đặt lại các thông số cấu hình về giá trị tiêu chuẩn?")) {
            return;
        }
        if (cacDauVao.containsKey("THUE_VAT_PHAN_TRAM")) cacDauVao.get("THUE_VAT_PHAN_TRAM").setText("8");
        if (cacDauVao.containsKey("NGUONG_TON_KHO_THAP"))
            cacDauVao.get("NGUONG_TON_KHO_THAP").setText("10");
        if (cacDauVao.containsKey("SO_NGAY_CANH_BAO_HET_HAN"))
            cacDauVao.get("SO_NGAY_CANH_BAO_HET_HAN").setText("30");
        if (cacDauVao.containsKey("SO_NGAY_TON_DONG"))
            cacDauVao.get("SO_NGAY_TON_DONG").setText("45");
        if (cacDauVao.containsKey("HAN_MUC_XUAT_HUY_CAN_DUYET"))
            cacDauVao.get("HAN_MUC_XUAT_HUY_CAN_DUYET").setText("500000");
        if (cacDauVao.containsKey("SO_GIO_TOI_DA_MOI_TUAN"))
            cacDauVao.get("SO_GIO_TOI_DA_MOI_TUAN").setText("40");
        if (cacDauVao.containsKey("SO_GIO_BAO_TRUOC_DOI_CA"))
            cacDauVao.get("SO_GIO_BAO_TRUOC_DOI_CA").setText("12");
        if (cacDauVao.containsKey("PHI_TRA_PHONG_TRE"))
            cacDauVao.get("PHI_TRA_PHONG_TRE").setText("100000");
        if (cacDauVao.containsKey("SO_NGAY_DOI_TRA"))
            cacDauVao.get("SO_NGAY_DOI_TRA").setText("3");
        chkBlindCountDefault.setSelected(false);
    }

    private void nap() {
        for (var muc : cacDauVao.entrySet()) {
            String k = muc.getKey();
            if (k.equals("SO_NGAY_TON_DONG")) {
                muc.getValue().setText(CaiDatService.layTuyChonHoacMacDinh(k, "45"));
            } else if (k.equals("SO_GIO_TOI_DA_MOI_TUAN")) {
                muc.getValue().setText(CaiDatService.layTuyChonHoacMacDinh(k, "40"));
            } else if (k.equals("SO_GIO_BAO_TRUOC_DOI_CA")) {
                muc.getValue().setText(CaiDatService.layTuyChonHoacMacDinh(k, "12"));
            } else if (k.equals("SO_NGAY_DOI_TRA")) {
                muc.getValue().setText(CaiDatService.layTuyChonHoacMacDinh(k, "3"));
            } else if (k.equals("MA_BIN_NGAN_HANG")
                    || k.equals("SO_TAI_KHOAN_NGAN_HANG")
                    || k.equals("TEN_CHU_TAI_KHOAN")
                    || k.equals("THU_MUC_ANH_CHAM_SOC")) {
                muc.getValue().setText(CaiDatService.layTuyChon(k));
            } else {
                muc.getValue().setText(CaiDatService.lay(k));
            }
        }

        // Blind count default
        boolean blind =
                Boolean.parseBoolean(
                        CaiDatService.layTuyChonHoacMacDinh("MAC_DINH_KIEM_KE_MU", "false"));
        chkBlindCountDefault.setSelected(blind);

        // Máy in
        String daChon = CaiDatService.lay("MAY_IN");
        if (!daChon.isBlank()) cboMayIn.setSelectedItem(daChon);

        ghiNhanDaNap();
    }

    private boolean coThayDoiChuaLuu() {
        if (!Objects.equals(mayInDaNap, cboMayIn.getSelectedItem())) return true;
        if (blindCountDaNap != chkBlindCountDefault.isSelected()) return true;
        return cacDauVao.entrySet().stream()
                .anyMatch(
                        e -> !Objects.equals(e.getValue().getText(), giaTriDaNap.get(e.getKey())));
    }

    private void ghiNhanDaNap() {
        giaTriDaNap.clear();
        cacDauVao.forEach((k, v) -> giaTriDaNap.put(k, v.getText()));
        mayInDaNap = cboMayIn.getSelectedItem();
        blindCountDaNap = chkBlindCountDefault.isSelected();
    }

    @Override
    public void xuLyLuu() {
        if (!coTheSua) return;
        try {
            // Validation
            int vat = Integer.parseInt(cacDauVao.get("THUE_VAT_PHAN_TRAM").getText().trim());
            if (vat < 0 || vat > 100)
                throw new IllegalArgumentException("Thuế VAT phải từ 0% đến 100%.");

            long lowStock = Long.parseLong(cacDauVao.get("NGUONG_TON_KHO_THAP").getText().trim());
            if (lowStock < 0) throw new IllegalArgumentException("Ngưỡng tồn thấp không được âm.");

            long expiryDays = Long.parseLong(cacDauVao.get("SO_NGAY_CANH_BAO_HET_HAN").getText().trim());
            if (expiryDays < 0)
                throw new IllegalArgumentException("Số ngày cảnh báo hạn dùng không được âm.");

            Map<String, String> giaTri = new LinkedHashMap<>();
            cacDauVao.forEach((khoa, truong) -> giaTri.put(khoa, truong.getText().trim()));
            giaTri.put("MAC_DINH_KIEM_KE_MU", String.valueOf(chkBlindCountDefault.isSelected()));

            String mayIn =
                    "Không có máy in".equals(cboMayIn.getSelectedItem())
                            ? ""
                            : String.valueOf(cboMayIn.getSelectedItem());

            CaiDatService.luuTatCa(giaTri, mayIn);
            nap();
            HopThoai.hienThiThanhCong(this, "Đã lưu thành công cài đặt hệ thống.");
        } catch (NumberFormatException ex) {
            HopThoai.hienThiCanhBao(
                    this, "Vui lòng nhập định dạng số hợp lệ cho các trường tham số.");
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
        }
    }

    @Override
    public void xuLyHienThi() {
        if (!coThayDoiChuaLuu()) nap();
    }
}
