package com.petstation.gui.hethong;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.hethong.CaiDatService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

// Màn hình: Cài đặt cửa hàng & tham số (Mã: CAI_DAT)
// Luồng xử lý: GUI -> CaiDatService -> CaiDatHeThongDAOImpl -> SQL (cai_dat_he_thong)
// Mô tả: Cấu hình thông tin cửa hàng hiển thị trên biên lai, tỷ lệ quy đổi điểm thưởng và tham số
// vận hành
// Phân hệ: hethong
public final class CaiDatPanel extends CoSoPanel {
    private final Map<String, JTextField> cacDauVao = new LinkedHashMap<>();
    private final JCheckBox hopChonMacDinhKiemDemMu =
            new JCheckBox("Mặc định bật chế độ kiểm kê mù (Blind Count) cho nhân viên kho");
    private final JComboBox<String> cboMayIn = new JComboBox<>();
    private final boolean coTheSua;

    private final Map<String, String> giaTriDaNap = new LinkedHashMap<>();
    private Object mayInDaNap;
    private boolean kiemDemMuDaNap;
    private int luotTai;

    public CaiDatPanel(boolean coTheSua) {
        this.coTheSua = coTheSua;
        setLayout(new BorderLayout(0, 12));
        add(taoTieuDeMuc("Cài đặt hệ thống"), BorderLayout.NORTH);
        JTabbedPane tabCaiDat = new JTabbedPane();
        tabCaiDat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabCaiDat.addTab("Cửa hàng & Hóa đơn", taoTabCuaHangVaHoaDon());
        tabCaiDat.addTab("Kho & Hàng hóa", taoTabKhoVaHangHoa());
        tabCaiDat.addTab("Nhân sự & Phân lịch", taoTabNhanSuVaQuyDinh());
        tabCaiDat.addTab("Lưu trữ & Bảo trì", taoTabLuuTruVaBaoTri());
        add(tabCaiDat, BorderLayout.CENTER);
        add(taoThanhThaoTacCaiDat(), BorderLayout.SOUTH);
        nap();
    }

    private JPanel taoTabCuaHangVaHoaDon() {
        JPanel tab1 = taoKhungTab();
        JPanel khungThongTinCuaHang = layNhomBox("Thông tin pháp nhân & Liên hệ");
        taoTruong(
                khungThongTinCuaHang,
                "TEN_CUA_HANG",
                "Tên cửa hàng",
                "Tên hiển thị trên hóa đơn và chứng từ");
        taoTruong(
                khungThongTinCuaHang,
                "DIA_CHI_CUA_HANG",
                "Địa chỉ cửa hàng",
                "Địa chỉ in trên tiêu đề hóa đơn");
        taoTruong(
                khungThongTinCuaHang,
                "SO_DIEN_THOAI_CUA_HANG",
                "Số điện thoại / Hotline",
                "Hotline liên hệ");
        taoTruong(
                khungThongTinCuaHang,
                "EMAIL_CUA_HANG",
                "Email cửa hàng",
                "Hộp thư chăm sóc khách hàng");
        taoTruong(khungThongTinCuaHang, "WEBSITE_CUA_HANG", "Website", "Địa chỉ website cửa hàng");
        taoTruong(khungThongTinCuaHang, "MA_SO_THUE", "Mã số thuế", "Mã số thuế doanh nghiệp");
        JPanel khungHoaDonVaMayIn = layNhomBox("Bán hàng, Thuế & Thiết bị");
        taoTruong(
                khungHoaDonVaMayIn,
                "THUE_VAT_PHAN_TRAM",
                "Thuế VAT (%)",
                "Thuế suất GTGT áp dụng chung (0 - 100%)");
        for (PrintService dichVu : PrintServiceLookup.lookupPrintServices(null, null))
            cboMayIn.addItem(dichVu.getName());
        if (cboMayIn.getItemCount() == 0) cboMayIn.addItem("Không có máy in");
        cboMayIn.setEnabled(coTheSua);
        cboMayIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboMayIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungHoaDonVaMayIn.add(new JLabel("Máy in hóa đơn mặc định"));
        khungHoaDonVaMayIn.add(Box.createVerticalStrut(4));
        khungHoaDonVaMayIn.add(cboMayIn);
        khungHoaDonVaMayIn.add(Box.createVerticalStrut(6));
        NutDinhDang btnKiemThu =
                new NutDinhDang("In thử trang mẫu", NutDinhDang.KieuNut.TRONG_SUOT);
        btnKiemThu.addActionListener(e -> inThu());
        khungHoaDonVaMayIn.add(btnKiemThu);
        khungHoaDonVaMayIn.add(Box.createVerticalStrut(12));
        taoTruong(
                khungHoaDonVaMayIn,
                "MA_BIN_NGAN_HANG",
                "Mã BIN ngân hàng VietQR",
                "Mã BIN 6 số ngân hàng (VD: 970436 - Vietcombank)");
        taoTruong(
                khungHoaDonVaMayIn,
                "SO_TAI_KHOAN_NGAN_HANG",
                "Số tài khoản nhận tiền",
                "Số tài khoản ngân hàng thụ hưởng");
        taoTruong(
                khungHoaDonVaMayIn,
                "TEN_CHU_TAI_KHOAN",
                "Chủ tài khoản (IN HOA)",
                "Tên chủ tài khoản viết hoa không dấu");
        tab1.add(khungThongTinCuaHang);
        tab1.add(khungHoaDonVaMayIn);
        return tab1;
    }

    private JPanel taoTabKhoVaHangHoa() {
        JPanel tab2 = taoKhungTab();
        JPanel khungKho1 = layNhomBox("Cảnh báo tồn kho & Hạn dùng");
        taoTruong(
                khungKho1,
                "NGUONG_TON_KHO_THAP",
                "Ngưỡng tồn thấp cảnh báo",
                "Sản phẩm có tồn kho dưới mức này sẽ hiện cảnh báo");
        taoTruong(
                khungKho1,
                "SO_NGAY_CANH_BAO_HET_HAN",
                "Cảnh báo trước hạn dùng (ngày)",
                "Lô hàng sắp hết hạn trong khoảng số ngày này sẽ được tô đỏ");
        taoTruong(
                khungKho1,
                "SO_NGAY_TON_DONG",
                "Ngưỡng hàng tồn đọng / chôn vốn (ngày)",
                "Hàng nhập kho quá số ngày này chưa bán được xem là tồn đọng");
        JPanel khungKho2 = layNhomBox("Xuất hủy & Kiểm kê");
        taoTruong(
                khungKho2,
                "HAN_MUC_XUAT_HUY_CAN_DUYET",
                "Hạn mức xuất hủy cần duyệt (đồng)",
                "Phiếu xuất hủy vượt giá trị này cần Quản lý phê duyệt");
        hopChonMacDinhKiemDemMu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hopChonMacDinhKiemDemMu.setEnabled(coTheSua);
        hopChonMacDinhKiemDemMu.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungKho2.add(Box.createVerticalStrut(10));
        khungKho2.add(hopChonMacDinhKiemDemMu);
        tab2.add(khungKho1);
        tab2.add(khungKho2);
        return tab2;
    }

    private JPanel taoTabNhanSuVaQuyDinh() {
        JPanel tab3 = taoKhungTab();
        JPanel khungNhanSu1 = layNhomBox("Chính sách làm việc & Ca trực");
        taoTruong(
                khungNhanSu1,
                "SO_GIO_TOI_DA_MOI_TUAN",
                "Số giờ làm việc tối đa mỗi tuần (giờ)",
                "Cảnh báo khi phân ca vượt quá số giờ quy định");
        taoTruong(
                khungNhanSu1,
                "SO_GIO_BAO_TRUOC_DOI_CA",
                "Thời gian báo trước khi đổi ca (giờ)",
                "Nhân viên phải gửi yêu cầu đổi ca trước ca làm ít nhất");
        JPanel khungNhanSu2 = layNhomBox("Quy định dịch vụ & Đổi trả");
        taoTruong(
                khungNhanSu2,
                "PHI_TRA_PHONG_TRE",
                "Phí trả phòng lưu trú trễ (đồng/ngày)",
                "Phí phụ thu khi khách nhận thú cưng trễ ngày hẹn");
        taoTruong(
                khungNhanSu2,
                "SO_NGAY_DOI_TRA",
                "Thời hạn đổi trả phụ kiện (ngày)",
                "Số ngày tối đa cho phép khách hàng đổi trả phụ kiện kể từ ngày mua");
        tab3.add(khungNhanSu1);
        tab3.add(khungNhanSu2);
        return tab3;
    }

    private JPanel taoTabLuuTruVaBaoTri() {
        JPanel tab4 = taoKhungTab();
        JPanel khungHeThong1 = layNhomBox("Đường dẫn dữ liệu");
        taoTruongThuMuc(
                khungHeThong1,
                "THU_MUC_ANH_CHAM_SOC",
                "Thư mục lưu trữ hình ảnh chăm sóc thú cưng",
                "Mặc định thư mục PetStation trong thư mục người dùng");
        JPanel khungHeThong2 = layNhomBox("Khôi phục cấu hình");
        JLabel nhanGhiChuBaoTri =
                new JLabel(
                        "<html><i>Có thể khôi phục các tham số hệ thống về giá trị mặc định của nhà"
                                + " sản xuất.</i></html>");
        nhanGhiChuBaoTri.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungHeThong2.add(nhanGhiChuBaoTri);
        khungHeThong2.add(Box.createVerticalStrut(14));
        NutDinhDang btnKhoiPhucMacDinh =
                new NutDinhDang("Khôi phục thiết lập chuẩn", NutDinhDang.KieuNut.PHU);
        btnKhoiPhucMacDinh.setEnabled(coTheSua);
        btnKhoiPhucMacDinh.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnKhoiPhucMacDinh.addActionListener(e -> khoiPhucMacDinh());
        khungHeThong2.add(btnKhoiPhucMacDinh);
        tab4.add(khungHeThong1);
        tab4.add(khungHeThong2);
        return tab4;
    }

    private JPanel taoThanhThaoTacCaiDat() {
        JPanel khungCacThaoTac = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 10, 8));
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
        khungCacThaoTac.add(btnTaiLai);
        NutDinhDang btnLuu = new NutDinhDang("Lưu cài đặt", NutDinhDang.KieuNut.CHINH);
        btnLuu.setEnabled(coTheSua);
        btnLuu.addActionListener(suKien -> xuLyLuu());
        khungCacThaoTac.add(btnLuu);
        return khungCacThaoTac;
    }

    private JPanel taoKhungTab() {
        JPanel khungTam = new JPanel(new GridLayout(1, 2, 20, 0));
        khungTam.setOpaque(false);
        khungTam.setBorder(new EmptyBorder(14, 8, 14, 8));
        return khungTam;
    }

    private JPanel layNhomBox(String tieuDe) {
        JPanel khungTam = layThe(new BorderLayout());
        khungTam.setLayout(new BoxLayout(khungTam, BoxLayout.Y_AXIS));
        khungTam.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_TRUNG),
                                tieuDe,
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                new Font("Segoe UI", Font.BOLD, 13),
                                MauUngDung.XAM_TIEU_DE),
                        new EmptyBorder(12, 14, 14, 14)));
        return khungTam;
    }

    private void taoTruong(JPanel khungTam, String khoa, String nhan, String tooltip) {
        JLabel nhanChu = new JLabel(nhan);
        nhanChu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nhanChu.setToolTipText(tooltip);
        nhanChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanChu);
        khungTam.add(Box.createVerticalStrut(4));
        JTextField oNhapLieu = new JTextField(24);
        oNhapLieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        oNhapLieu.setToolTipText(tooltip);
        oNhapLieu.setEnabled(coTheSua);
        oNhapLieu.setAlignmentX(Component.LEFT_ALIGNMENT);
        oNhapLieu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        oNhapLieu.setPreferredSize(new Dimension(280, 36));
        cacDauVao.put(khoa, oNhapLieu);
        khungTam.add(oNhapLieu);
        khungTam.add(Box.createVerticalStrut(10));
    }

    private void taoTruongThuMuc(JPanel khungTam, String khoa, String nhan, String tooltip) {
        JLabel nhanChu = new JLabel(nhan);
        nhanChu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nhanChu.setToolTipText(tooltip);
        nhanChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanChu);
        khungTam.add(Box.createVerticalStrut(4));
        JPanel khungDuongKe = new JPanel(new BorderLayout(8, 0));
        khungDuongKe.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungDuongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        khungDuongKe.setOpaque(false);
        JTextField oNhapLieu = new JTextField(24);
        oNhapLieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        oNhapLieu.setToolTipText(tooltip);
        oNhapLieu.setEnabled(coTheSua);
        cacDauVao.put(khoa, oNhapLieu);
        NutDinhDang btnChon = new NutDinhDang("Chọn thư mục...", NutDinhDang.KieuNut.PHU);
        btnChon.setEnabled(coTheSua);
        btnChon.addActionListener(
                e -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (!oNhapLieu.getText().isBlank()) {
                        chooser.setSelectedFile(new File(oNhapLieu.getText().trim()));
                    }
                    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        oNhapLieu.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                });
        khungDuongKe.add(oNhapLieu, BorderLayout.CENTER);
        khungDuongKe.add(btnChon, BorderLayout.EAST);
        khungTam.add(khungDuongKe);
        khungTam.add(Box.createVerticalStrut(10));
    }

    private void inThu() {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () ->
                        CaiDatService.macDinh().lay("TEN_CUA_HANG")
                                + "\n"
                                + CaiDatService.macDinh().lay("DIA_CHI_CUA_HANG")
                                + "\n"
                                + "Hotline: "
                                + CaiDatService.macDinh().lay("SO_DIEN_THOAI_CUA_HANG")
                                + "\n"
                                + "--------------------------------\n"
                                + "IN THỬ PET STATION POS\n"
                                + LocalDateTime.now()
                                + "\n"
                                + "--------------------------------\n"
                                + "Cảm ơn quý khách!\n",
                noiDung -> {
                    JTextArea oTrang = new JTextArea(noiDung);
                    try {
                        oTrang.print();
                    } catch (PrinterException ngoaiLe) {
                        HopThoai.hienThiLoi(this, "Lỗi in thử: " + ngoaiLe.getMessage());
                    }
                });
    }

    private void khoiPhucMacDinh() {
        if (!HopThoai.xacNhan(
                this, "Bạn có chắc muốn đặt lại các thông số cấu hình về giá trị tiêu chuẩn?")) {
            return;
        }
        if (cacDauVao.containsKey("THUE_VAT_PHAN_TRAM"))
            cacDauVao.get("THUE_VAT_PHAN_TRAM").setText("8");
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
        if (cacDauVao.containsKey("SO_NGAY_DOI_TRA")) cacDauVao.get("SO_NGAY_DOI_TRA").setText("3");
        hopChonMacDinhKiemDemMu.setSelected(false);
    }

    private void nap() {
        int luot = ++luotTai;
        List<String> khoaList = new ArrayList<>(cacDauVao.keySet());
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> taiGiaTriCaiDat(khoaList),
                this::apDungGiaTriCaiDat);
    }

    private static Map<String, String> taiGiaTriCaiDat(List<String> khoaList) {
        Map<String, String> ketQua = new LinkedHashMap<>();
        CaiDatService dichVu = CaiDatService.macDinh();
        for (String k : khoaList) {
            if (k.equals("SO_NGAY_TON_DONG")) {
                ketQua.put(k, dichVu.layTuyChonHoacMacDinh(k, "45"));
            } else if (k.equals("SO_GIO_TOI_DA_MOI_TUAN")) {
                ketQua.put(k, dichVu.layTuyChonHoacMacDinh(k, "40"));
            } else if (k.equals("SO_GIO_BAO_TRUOC_DOI_CA")) {
                ketQua.put(k, dichVu.layTuyChonHoacMacDinh(k, "12"));
            } else if (k.equals("SO_NGAY_DOI_TRA")) {
                ketQua.put(k, dichVu.layTuyChonHoacMacDinh(k, "3"));
            } else if (k.equals("MA_BIN_NGAN_HANG")
                    || k.equals("SO_TAI_KHOAN_NGAN_HANG")
                    || k.equals("TEN_CHU_TAI_KHOAN")
                    || k.equals("THU_MUC_ANH_CHAM_SOC")) {
                ketQua.put(k, dichVu.layTuyChon(k));
            } else {
                ketQua.put(k, dichVu.lay(k));
            }
        }
        ketQua.put(
                "MAC_DINH_KIEM_KE_MU",
                dichVu.layTuyChonHoacMacDinh("MAC_DINH_KIEM_KE_MU", "false"));
        ketQua.put("MAY_IN", dichVu.lay("MAY_IN"));
        return ketQua;
    }

    private void apDungGiaTriCaiDat(Map<String, String> ketQua) {
        for (var muc : cacDauVao.entrySet()) {
            muc.getValue().setText(ketQua.getOrDefault(muc.getKey(), ""));
        }
        hopChonMacDinhKiemDemMu.setSelected(
                Boolean.parseBoolean(ketQua.getOrDefault("MAC_DINH_KIEM_KE_MU", "false")));
        String daChon = ketQua.getOrDefault("MAY_IN", "");
        if (!daChon.isBlank()) cboMayIn.setSelectedItem(daChon);
        ghiNhanDaNap();
    }

    private boolean coThayDoiChuaLuu() {
        if (!Objects.equals(mayInDaNap, cboMayIn.getSelectedItem())) return true;
        if (kiemDemMuDaNap != hopChonMacDinhKiemDemMu.isSelected()) return true;
        return cacDauVao.entrySet().stream()
                .anyMatch(
                        e -> !Objects.equals(e.getValue().getText(), giaTriDaNap.get(e.getKey())));
    }

    private void ghiNhanDaNap() {
        giaTriDaNap.clear();
        cacDauVao.forEach((k, v) -> giaTriDaNap.put(k, v.getText()));
        mayInDaNap = cboMayIn.getSelectedItem();
        kiemDemMuDaNap = hopChonMacDinhKiemDemMu.isSelected();
    }

    @Override
    public void xuLyLuu() {
        if (!coTheSua) return;
        try {
            int vat = Integer.parseInt(cacDauVao.get("THUE_VAT_PHAN_TRAM").getText().trim());
            if (vat < 0 || vat > 100)
                throw new IllegalArgumentException("Thuế VAT phải từ 0% đến 100%.");
            long lowStock = Long.parseLong(cacDauVao.get("NGUONG_TON_KHO_THAP").getText().trim());
            if (lowStock < 0) throw new IllegalArgumentException("Ngưỡng tồn thấp không được âm.");
            long expiryDays =
                    Long.parseLong(cacDauVao.get("SO_NGAY_CANH_BAO_HET_HAN").getText().trim());
            if (expiryDays < 0)
                throw new IllegalArgumentException("Số ngày cảnh báo hạn dùng không được âm.");
            Map<String, String> giaTri = new LinkedHashMap<>();
            cacDauVao.forEach((khoa, truong) -> giaTri.put(khoa, truong.getText().trim()));
            giaTri.put("MAC_DINH_KIEM_KE_MU", String.valueOf(hopChonMacDinhKiemDemMu.isSelected()));
            String mayIn =
                    "Không có máy in".equals(cboMayIn.getSelectedItem())
                            ? ""
                            : String.valueOf(cboMayIn.getSelectedItem());
            TacVuNen.chayCoPhien(
                    this,
                    () -> true,
                    () -> CaiDatService.macDinh().luuTatCa(giaTri, mayIn),
                    () -> {
                        nap();
                        HopThoai.hienThiThanhCong(this, "Đã lưu thành công cài đặt hệ thống.");
                    });
        } catch (NumberFormatException ex) {
            HopThoai.hienThiCanhBao(
                    this, "Vui lòng nhập định dạng số hợp lệ cho các trường tham số.");
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
        }
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, coTheSua, false, false, false, true);
    }

    @Override
    public void xuLyHienThi() {
        if (!coThayDoiChuaLuu()) nap();
    }
}
