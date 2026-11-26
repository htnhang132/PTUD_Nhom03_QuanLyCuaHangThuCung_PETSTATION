package com.petstation.gui.hethong;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.print.PrinterJob;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

// Màn hình: Cài đặt cửa hàng & tham số (Mã: CAI_DAT)
// Luồng xử lý: GUI -> CaiDatService -> CaiDatHeThongDAOImpl -> SQL (cai_dat_he_thong)
// Mô tả: Cấu hình thông tin cửa hàng hiển thị trên biên lai, tỷ lệ quy đổi điểm thưởng và tham số
// vận hành
// Phân hệ: hethong
public final class CaiDatPanel extends CoSoPanel {
    // 1. THUỘC TÍNH & CẤU HÌNH

    private final Map<String, JTextField> cacDauVao = new LinkedHashMap<>();
    private final JCheckBox hopChonMacDinhKiemDemMu =
            new JCheckBox("Mặc định ẩn tồn sổ sách khi đếm");
    private final JComboBox<String> cboMayIn = new JComboBox<>();
    private final JComboBox<String> cboNganHangVietQR = new JComboBox<>();
    private final boolean coTheSua;

    private static final String MUC_MAC_DINH_KHONG_CO_MAY_IN = "Không có máy in";

    private static final String[][] NGAN_HANG_VIETQR = {
        {"970436", "Vietcombank (VCB) - 970436"},
        {"970422", "MB Bank (MB) - 970422"},
        {"970407", "Techcombank (TCB) - 970407"},
        {"970418", "BIDV - 970418"},
        {"970415", "VietinBank - 970415"},
        {"970416", "ACB - 970416"},
        {"970432", "VPBank - 970432"},
        {"970423", "TPBank - 970423"},
        {"970403", "Sacombank - 970403"},
        {"970405", "Agribank - 970405"},
        {"970437", "HDBank - 970437"},
        {"970441", "VIB - 970441"},
        {"970443", "SHB - 970443"},
        {"970426", "MSB - 970426"},
        {"970448", "OCB - 970448"},
        {"970440", "SeABank - 970440"},
        {"970431", "Eximbank - 970431"},
        {"970449", "LPBank - 970449"},
        {"970428", "Nam A Bank - 970428"},
        {"", "Ngân hàng khác (nhập BIN thủ công)"}
    };

    private final Map<String, String> giaTriDaNap = new LinkedHashMap<>();
    private JTabbedPane tabCaiDat;
    private NutDinhDang btnLuu;
    private Object mayInDaNap;
    private boolean kiemDemMuDaNap;
    private boolean daNapXong;
    private int luotTai;

    // 2. HÀM KHỞI TẠO & XÂY DỰNG CÁC TAB

    public CaiDatPanel(boolean coTheSua) {
        this.coTheSua = coTheSua;
        setLayout(new BorderLayout(0, 12));
        add(
                taoTieuDeManHinh(
                        "Cài đặt hệ thống",
                        "Cấu hình thông tin cửa hàng, hóa đơn, kho hàng, nhân sự và bảo trì"),
                BorderLayout.NORTH);
        tabCaiDat = new JTabbedPane();
        tabCaiDat.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabCaiDat.addTab("Cửa hàng và thanh toán", bocCuon(taoTabCuaHangVaHoaDon()));
        tabCaiDat.addTab("Vận hành", bocCuon(taoTabVanHanh()));
        add(tabCaiDat, BorderLayout.CENTER);
        add(taoThanhThaoTacCaiDat(), BorderLayout.SOUTH);
        nap();
    }

    private JPanel taoTabCuaHangVaHoaDon() {
        JPanel tab1 = taoKhungTab();
        JPanel khungThongTinCuaHang = layNhomBox("Thông tin cửa hàng");
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
        JPanel khungHoaDonVaMayIn = layNhomBox("In hóa đơn và thuế");
        taoTruong(
                khungHoaDonVaMayIn,
                "THUE_VAT_PHAN_TRAM",
                "Thuế VAT (%)",
                "Thuế suất GTGT áp dụng chung (0 - 100%)");
        cboMayIn.addItem("Đang tải danh sách máy in...");
        cboMayIn.setEnabled(false);
        cboMayIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, MauUngDung.CAO_DAU_VAO));
        cboMayIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nhanMayIn = new JLabel("Máy in hóa đơn mặc định");
        nhanMayIn.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanMayIn.setForeground(MauUngDung.NAU);
        nhanMayIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungHoaDonVaMayIn.add(nhanMayIn);
        khungHoaDonVaMayIn.add(Box.createVerticalStrut(4));
        khungHoaDonVaMayIn.add(cboMayIn);
        khungHoaDonVaMayIn.add(Box.createVerticalStrut(6));
        NutDinhDang btnKiemThu =
                new NutDinhDang("In thử trang mẫu", NutDinhDang.KieuNut.TRONG_SUOT);
        btnKiemThu.setEnabled(coTheSua);
        btnKiemThu.addActionListener(e -> inThu());
        khungHoaDonVaMayIn.add(btnKiemThu);
        khungHoaDonVaMayIn.add(Box.createVerticalStrut(12));
        JPanel khungNhanTien = layNhomBox("Nhận chuyển khoản");
        themChonNganHangVietQR(khungNhanTien);
        taoTruong(
                khungNhanTien,
                "MA_BIN_NGAN_HANG",
                "Mã BIN ngân hàng VietQR",
                "Mã BIN 6 số ngân hàng (tự điền khi chọn ngân hàng phía trên)");
        taoTruong(
                khungNhanTien,
                "SO_TAI_KHOAN_NGAN_HANG",
                "Số tài khoản nhận tiền",
                "Số tài khoản ngân hàng thụ hưởng");
        taoTruong(
                khungNhanTien,
                "TEN_CHU_TAI_KHOAN",
                "Chủ tài khoản (IN HOA)",
                "Tên chủ tài khoản viết hoa không dấu");
        tab1.add(taoCotNhom(khungThongTinCuaHang));
        tab1.add(taoCotNhom(khungNhanTien, khungHoaDonVaMayIn));
        return tab1;
    }

    private JPanel taoTabVanHanh() {
        JPanel tab = taoKhungTab();
        tab.add(taoNhomKhoVaHangHoa());
        tab.add(taoCotNhom(taoNhomNhanSu(), taoNhomDichVuVaAnh()));
        return tab;
    }

    private JPanel taoNhomKhoVaHangHoa() {
        JPanel khungKho1 = layNhomBox("Cảnh báo tồn kho và hạn dùng");
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
        JPanel khungKho2 = layNhomBox("Xuất hủy và kiểm kê");
        taoTruong(
                khungKho2,
                "HAN_MUC_XUAT_HUY_CAN_DUYET",
                "Hạn mức xuất hủy cần duyệt (₫)",
                "Phiếu xuất hủy vượt giá trị này cần Quản lý phê duyệt");
        hopChonMacDinhKiemDemMu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        hopChonMacDinhKiemDemMu.setEnabled(coTheSua);
        hopChonMacDinhKiemDemMu.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungKho2.add(Box.createVerticalStrut(10));
        khungKho2.add(hopChonMacDinhKiemDemMu);
        return taoCotNhom(khungKho1, khungKho2);
    }

    private JPanel taoNhomNhanSu() {
        JPanel khungNhanSu1 = layNhomBox("Nhân sự và phân ca");
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
        return khungNhanSu1;
    }

    private JPanel taoNhomDichVuVaAnh() {
        JPanel khungNhanSu2 = layNhomBox("Dịch vụ, đổi trả và ảnh chăm sóc");
        taoTruong(
                khungNhanSu2,
                "PHI_TRA_PHONG_TRE",
                "Phí trả phòng lưu trú trễ (₫/ngày)",
                "Phí phụ thu khi khách nhận thú cưng trễ ngày hẹn");
        taoTruong(
                khungNhanSu2,
                "SO_NGAY_DOI_TRA",
                "Thời hạn đổi trả phụ kiện (ngày)",
                "Số ngày tối đa cho phép khách hàng đổi trả phụ kiện kể từ ngày mua");
        taoTruongThuMuc(
                khungNhanSu2,
                "THU_MUC_ANH_CHAM_SOC",
                "Thư mục ảnh chăm sóc thú cưng",
                "Mặc định thư mục PetStation trong thư mục người dùng");
        return khungNhanSu2;
    }

    private JPanel taoCotNhom(JPanel... cacNhom) {
        JPanel cot = taoVungTrongSuot(new GridBagLayout());
        cot.setName("cotNhomCaiDat");
        for (int i = 0; i < cacNhom.length; i++) {
            JPanel nhom = cacNhom[i];
            for (Component thanhPhan : nhom.getComponents()) {
                thanhPhan.setMaximumSize(
                        new Dimension(
                                thanhPhan instanceof JLabel
                                        ? Integer.MAX_VALUE
                                        : thanhPhan.getMaximumSize().width,
                                thanhPhan.getPreferredSize().height));
            }
            nhom.add(Box.createVerticalGlue());
            GridBagConstraints viTri = new GridBagConstraints();
            viTri.gridx = 0;
            viTri.gridy = i;
            viTri.weightx = 1;
            viTri.weighty = i == cacNhom.length - 1 ? 1 : 0;
            viTri.fill = GridBagConstraints.BOTH;
            viTri.insets = new Insets(i == 0 ? 0 : 16, 0, 0, 0);
            cot.add(nhom, viTri);
        }
        return cot;
    }

    private JPanel taoThanhThaoTacCaiDat() {
        JPanel khungCacThaoTac = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        NutDinhDang btnKhoiPhucMacDinh =
                new NutDinhDang("Đặt lại cấu hình", NutDinhDang.KieuNut.PHU);
        btnKhoiPhucMacDinh.setEnabled(coTheSua);
        btnKhoiPhucMacDinh.setToolTipText(
                "Điền lại các thông số mặc định. Bấm Lưu cài đặt để áp dụng.");
        btnKhoiPhucMacDinh.addActionListener(e -> khoiPhucMacDinh());
        khungCacThaoTac.add(btnKhoiPhucMacDinh);
        btnLuu = new NutDinhDang("Lưu cài đặt", NutDinhDang.KieuNut.CHINH);
        btnLuu.setEnabled(false);
        btnLuu.addActionListener(suKien -> xuLyLuu());
        khungCacThaoTac.add(btnLuu);
        return khungCacThaoTac;
    }

    private JScrollPane bocCuon(JPanel tab) {
        JPanel neoTren =
                new JPanel(new BorderLayout()) {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension kichThuoc = super.getPreferredSize();
                        if (getParent() != null && getParent().getWidth() > 0) {
                            kichThuoc.width = getParent().getWidth();
                        }
                        return kichThuoc;
                    }
                };
        neoTren.setOpaque(false);
        neoTren.add(tab, BorderLayout.NORTH);
        JScrollPane cuon =
                new JScrollPane(
                        neoTren,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.setOpaque(false);
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.getVerticalScrollBar().setUnitIncrement(20);
        return cuon;
    }

    private JPanel taoKhungTab() {
        JPanel khungTam = new JPanel(new GridLayout(1, 2, 20, 0));
        khungTam.setOpaque(false);
        khungTam.setBorder(new EmptyBorder(14, 4, 14, 4));
        return khungTam;
    }

    private JPanel layNhomBox(String tieuDe) {
        JPanel khungTam = layThe(new BorderLayout());
        khungTam.setLayout(new BoxLayout(khungTam, BoxLayout.Y_AXIS));
        khungTam.setBorder(new EmptyBorder(16, 18, 16, 18));
        JLabel nhanNhom = new JLabel(tieuDe);
        nhanNhom.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        nhanNhom.setForeground(MauUngDung.NAU);
        nhanNhom.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanNhom);
        khungTam.add(Box.createVerticalStrut(12));
        return khungTam;
    }

    // 3. THAO TÁC TRÊN FORM & IN THỬ

    private void themChonNganHangVietQR(JPanel khungTam) {
        JLabel nhanNganHang = new JLabel("Ngân hàng nhận tiền (VietQR)");
        nhanNganHang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanNganHang.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanNganHang);
        khungTam.add(Box.createVerticalStrut(4));
        cboNganHangVietQR.removeAllItems();
        for (String[] nh : NGAN_HANG_VIETQR) {
            cboNganHangVietQR.addItem(nh[1]);
        }
        cboNganHangVietQR.setEnabled(coTheSua);
        cboNganHangVietQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboNganHangVietQR.setMaximumSize(new Dimension(Integer.MAX_VALUE, MauUngDung.CAO_DAU_VAO));
        cboNganHangVietQR.addActionListener(
                e -> {
                    int idx = cboNganHangVietQR.getSelectedIndex();
                    if (idx >= 0 && idx < NGAN_HANG_VIETQR.length) {
                        String bin = NGAN_HANG_VIETQR[idx][0];
                        JTextField oBin = cacDauVao.get("MA_BIN_NGAN_HANG");
                        if (!bin.isEmpty() && oBin != null) {
                            oBin.setText(bin);
                        }
                    }
                });
        khungTam.add(cboNganHangVietQR);
        khungTam.add(Box.createVerticalStrut(10));
    }

    private void dongBoChonNganHangTheoBin(String bin) {
        String b = bin == null ? "" : bin.trim();
        for (int i = 0; i < NGAN_HANG_VIETQR.length; i++) {
            if (!NGAN_HANG_VIETQR[i][0].isEmpty() && NGAN_HANG_VIETQR[i][0].equals(b)) {
                cboNganHangVietQR.setSelectedIndex(i);
                return;
            }
        }
        if (cboNganHangVietQR.getItemCount() > 0) {
            cboNganHangVietQR.setSelectedIndex(NGAN_HANG_VIETQR.length - 1);
        }
    }

    private static String chuanHoaChuoiSo(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[.,\\s₫đ]+", "").trim();
    }

    private void taoTruong(JPanel khungTam, String khoa, String nhan, String tooltip) {
        JLabel nhanChu = new JLabel(nhan);
        nhanChu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanChu.setToolTipText(tooltip);
        nhanChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanChu);
        khungTam.add(Box.createVerticalStrut(4));
        JTextField oNhapLieu = new JTextField(24);
        oNhapLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oNhapLieu.setToolTipText(tooltip);
        oNhapLieu.setEnabled(coTheSua);
        oNhapLieu.setAlignmentX(Component.LEFT_ALIGNMENT);
        oNhapLieu.setMaximumSize(new Dimension(Integer.MAX_VALUE, MauUngDung.CAO_DAU_VAO));
        oNhapLieu.setPreferredSize(new Dimension(280, MauUngDung.CAO_DAU_VAO));
        cacDauVao.put(khoa, oNhapLieu);
        khungTam.add(oNhapLieu);
        khungTam.add(Box.createVerticalStrut(10));
    }

    private void taoTruongThuMuc(JPanel khungTam, String khoa, String nhan, String tooltip) {
        JLabel nhanChu = new JLabel(nhan);
        nhanChu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanChu.setToolTipText(tooltip);
        nhanChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanChu);
        khungTam.add(Box.createVerticalStrut(4));
        JPanel khungDuongKe = new JPanel(new BorderLayout(8, 0));
        khungDuongKe.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungDuongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, MauUngDung.CAO_DAU_VAO));
        khungDuongKe.setOpaque(false);
        JTextField oNhapLieu = new JTextField(24);
        oNhapLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
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
        String tenCuaHang = layGiaTriForm("TEN_CUA_HANG", "Pet Station");
        String diaChi = layGiaTriForm("DIA_CHI_CUA_HANG", "");
        String hotline = layGiaTriForm("SO_DIEN_THOAI_CUA_HANG", "");
        String noiDung =
                tenCuaHang
                        + "\n"
                        + diaChi
                        + "\nHotline: "
                        + hotline
                        + "\n--------------------------------\n"
                        + "IN THỬ PET STATION POS\n"
                        + DinhDang.ngayGio(LocalDateTime.now())
                        + "\n--------------------------------\n"
                        + "Cảm ơn quý khách!\n";
        JTextArea oTrang = new JTextArea(noiDung);
        try {
            PrinterJob congViec = PrinterJob.getPrinterJob();
            congViec.setJobName("In thử Pet Station");
            ganMayInChoJob(congViec);
            congViec.setPrintable(oTrang.getPrintable(null, null));
            if (!congViec.printDialog()) return;
            TacVuNen.chay(
                    () -> {
                        congViec.print();
                        return null;
                    },
                    v -> {},
                    ngoaiLe ->
                            HopThoai.hienThiLoi(
                                    this, "Lỗi in thử: " + TacVuNen.thongDiepLoi(ngoaiLe)));
        } catch (Exception ngoaiLe) {
            HopThoai.hienThiLoi(this, "Lỗi in thử: " + ngoaiLe.getMessage());
        }
    }

    private String layGiaTriForm(String khoa, String macDinh) {
        JTextField oNhap = cacDauVao.get(khoa);
        if (oNhap == null) return macDinh;
        String vanBan = oNhap.getText().trim();
        return vanBan.isEmpty() ? macDinh : vanBan;
    }

    private void ganMayInChoJob(PrinterJob congViec) {
        Object mucChon = cboMayIn.getSelectedItem();
        if (mucChon == null || MUC_MAC_DINH_KHONG_CO_MAY_IN.equals(mucChon)) return;
        String tenMayIn = mucChon.toString().trim();
        if (tenMayIn.isEmpty()) return;
        for (PrintService dichVu : PrintServiceLookup.lookupPrintServices(null, null)) {
            if (tenMayIn.equalsIgnoreCase(dichVu.getName())) {
                try {
                    congViec.setPrintService(dichVu);
                } catch (Exception ngoaiLe) {
                    LoggerFactory.getLogger(CaiDatPanel.class)
                            .debug(
                                    "Không chọn được máy in '{}': {}",
                                    tenMayIn,
                                    ngoaiLe.getMessage());
                }
                break;
            }
        }
    }

    private void khoiPhucMacDinh() {
        if (!HopThoai.xacNhan(
                this,
                "Đặt lại các thông số cấu hình về giá trị mặc định?"
                        + "\nBấm Lưu cài đặt sau đó để áp dụng thay đổi.")) {
            return;
        }
        datGiaTriNeuCo("THUE_VAT_PHAN_TRAM", "8");
        datGiaTriNeuCo("NGUONG_TON_KHO_THAP", "10");
        datGiaTriNeuCo("SO_NGAY_CANH_BAO_HET_HAN", "30");
        datGiaTriNeuCo("SO_NGAY_TON_DONG", "45");
        datGiaTriNeuCo("HAN_MUC_XUAT_HUY_CAN_DUYET", "500000");
        datGiaTriNeuCo("SO_GIO_TOI_DA_MOI_TUAN", "40");
        datGiaTriNeuCo("SO_GIO_BAO_TRUOC_DOI_CA", "12");
        datGiaTriNeuCo("PHI_TRA_PHONG_TRE", "100000");
        datGiaTriNeuCo("SO_NGAY_DOI_TRA", "3");
        datGiaTriNeuCo("THU_MUC_ANH_CHAM_SOC", "");
        hopChonMacDinhKiemDemMu.setSelected(false);
    }

    private void datGiaTriNeuCo(String khoa, String giaTri) {
        JTextField oNhap = cacDauVao.get(khoa);
        if (oNhap != null) oNhap.setText(giaTri);
    }

    // 4. NẠP DỮ LIỆU & ĐỒNG BỘ TRẠNG THÁI

    private static final class DuLieuCaiDat {
        private final Map<String, String> giaTri;
        private final List<String> mayIn;

        private DuLieuCaiDat(Map<String, String> giaTri, List<String> mayIn) {
            this.giaTri = giaTri;
            this.mayIn = mayIn;
        }

        private Map<String, String> giaTri() {
            return giaTri;
        }

        private List<String> mayIn() {
            return mayIn;
        }
    }

    private void nap() {
        int luot = ++luotTai;
        List<String> khoaList = new ArrayList<>(cacDauVao.keySet());
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> taiDuLieuCaiDat(khoaList),
                this::apDungGiaTriCaiDat);
    }

    private static DuLieuCaiDat taiDuLieuCaiDat(List<String> khoaList) {
        Map<String, String> ketQua = taiGiaTriCaiDat(khoaList);
        List<String> dsMayIn = new ArrayList<>();
        for (PrintService dichVu : PrintServiceLookup.lookupPrintServices(null, null)) {
            dsMayIn.add(dichVu.getName());
        }
        return new DuLieuCaiDat(ketQua, dsMayIn);
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

    private void apDungGiaTriCaiDat(DuLieuCaiDat duLieu) {
        daNapXong = false;
        Map<String, String> ketQua = duLieu.giaTri();
        String daChon = ketQua.getOrDefault("MAY_IN", "").trim();
        cboMayIn.removeAllItems();
        if (duLieu.mayIn().isEmpty()) {
            if (!daChon.isEmpty()) cboMayIn.addItem(daChon);
            cboMayIn.addItem(MUC_MAC_DINH_KHONG_CO_MAY_IN);
        } else {
            for (String tenMayIn : duLieu.mayIn()) cboMayIn.addItem(tenMayIn);
            if (!daChon.isEmpty() && !duLieu.mayIn().contains(daChon)) {
                cboMayIn.insertItemAt(daChon, 0);
            }
        }
        cboMayIn.setEnabled(coTheSua);
        for (var muc : cacDauVao.entrySet()) {
            muc.getValue().setText(ketQua.getOrDefault(muc.getKey(), ""));
        }
        dongBoChonNganHangTheoBin(ketQua.getOrDefault("MA_BIN_NGAN_HANG", ""));
        hopChonMacDinhKiemDemMu.setSelected(
                Boolean.parseBoolean(ketQua.getOrDefault("MAC_DINH_KIEM_KE_MU", "false")));
        if (!daChon.isEmpty()) {
            cboMayIn.setSelectedItem(daChon);
        } else if (cboMayIn.getItemCount() > 0) {
            cboMayIn.setSelectedIndex(0);
        }
        ghiNhanDaNap();
        daNapXong = true;
        if (btnLuu != null) btnLuu.setEnabled(coTheSua);
    }

    private boolean coThayDoiChuaLuu() {
        if (!daNapXong) return false;
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

    // 5. KIỂM TRA, LƯU & VÒNG ĐỜI PANEL

    @Override
    public void xuLyLuu() {
        if (!coTheSua || !kiemTraHopLeTruocKhiLuu()) return;
        Map<String, String> giaTri = new LinkedHashMap<>();
        cacDauVao.forEach(
                (khoa, truong) -> {
                    String val = truong.getText().trim();
                    if ("HAN_MUC_XUAT_HUY_CAN_DUYET".equals(khoa)
                            || "SO_GIO_TOI_DA_MOI_TUAN".equals(khoa)
                            || "SO_GIO_BAO_TRUOC_DOI_CA".equals(khoa)
                            || "SO_NGAY_DOI_TRA".equals(khoa)
                            || "PHI_TRA_PHONG_TRE".equals(khoa)) {
                        val = chuanHoaChuoiSo(val);
                    }
                    giaTri.put(khoa, val);
                });
        giaTri.put("MAC_DINH_KIEM_KE_MU", String.valueOf(hopChonMacDinhKiemDemMu.isSelected()));
        Object mucMayIn = cboMayIn.getSelectedItem();
        String mayIn =
                mucMayIn == null || MUC_MAC_DINH_KHONG_CO_MAY_IN.equals(mucMayIn)
                        ? ""
                        : mucMayIn.toString().trim();
        if (btnLuu != null) btnLuu.setEnabled(false);
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> CaiDatService.macDinh().luuTatCa(giaTri, mayIn),
                () -> {
                    if (btnLuu != null) btnLuu.setEnabled(coTheSua);
                    nap();
                    HopThoai.hienThiThanhCong(this, "Đã lưu thành công cài đặt hệ thống.");
                },
                loi -> {
                    if (btnLuu != null) btnLuu.setEnabled(coTheSua);
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(loi));
                });
    }

    private boolean kiemTraHopLeTruocKhiLuu() {
        JTextField tenCuaHang = cacDauVao.get("TEN_CUA_HANG");
        if (tenCuaHang != null && tenCuaHang.getText().trim().isEmpty()) {
            baoLoiTruong(0, tenCuaHang, "Vui lòng nhập Tên cửa hàng (Tab Cửa hàng & Hóa đơn).");
            return false;
        }
        return kiemTraSoTrongTab("THUE_VAT_PHAN_TRAM", "Thuế VAT (%)", 0, 0, 100)
                && kiemTraSoTrongTab(
                        "NGUONG_TON_KHO_THAP", "Ngưỡng tồn thấp cảnh báo", 1, 0, Long.MAX_VALUE)
                && kiemTraSoTrongTab(
                        "SO_NGAY_CANH_BAO_HET_HAN",
                        "Cảnh báo trước hạn dùng (ngày)",
                        1,
                        0,
                        Long.MAX_VALUE)
                && kiemTraSoTrongTab(
                        "SO_NGAY_TON_DONG", "Ngưỡng hàng tồn đọng (ngày)", 1, 0, Long.MAX_VALUE)
                && kiemTraSoTrongTab(
                        "HAN_MUC_XUAT_HUY_CAN_DUYET",
                        "Hạn mức xuất hủy cần duyệt",
                        1,
                        0,
                        Long.MAX_VALUE)
                && kiemTraSoTrongTab("SO_GIO_TOI_DA_MOI_TUAN", "Số giờ tối đa mỗi tuần", 2, 1, 168)
                && kiemTraSoTrongTab(
                        "SO_GIO_BAO_TRUOC_DOI_CA",
                        "Thời gian báo trước đổi ca (giờ)",
                        2,
                        0,
                        Long.MAX_VALUE)
                && kiemTraSoTrongTab("PHI_TRA_PHONG_TRE", "Phí trả phòng trễ", 2, 0, Long.MAX_VALUE)
                && kiemTraSoTrongTab(
                        "SO_NGAY_DOI_TRA",
                        "Thời hạn đổi trả phụ kiện (ngày)",
                        2,
                        0,
                        Long.MAX_VALUE);
    }

    private boolean kiemTraSoTrongTab(
            String khoa, String nhanTruong, int chiSoTab, long min, long max) {
        JTextField truong = cacDauVao.get(khoa);
        if (truong == null) return true;
        String tenTab = tabCaiDat.getTitleAt(chiSoTab);
        try {
            long giaTri = Long.parseLong(chuanHoaChuoiSo(truong.getText().trim()));
            if (giaTri < min || giaTri > max) {
                String phamVi =
                        max == Long.MAX_VALUE
                                ? "không được nhỏ hơn " + min
                                : "phải trong khoảng " + min + " đến " + max;
                baoLoiTruong(chiSoTab, truong, nhanTruong + " (" + tenTab + ") " + phamVi + ".");
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            baoLoiTruong(
                    chiSoTab,
                    truong,
                    "Vui lòng nhập số nguyên hợp lệ cho mục \""
                            + nhanTruong
                            + "\" ("
                            + tenTab
                            + ").");
            return false;
        }
    }

    private void baoLoiTruong(int chiSoTab, JTextField truong, String thongBao) {
        tabCaiDat.setSelectedIndex(chiSoTab);
        truong.requestFocusInWindow();
        truong.selectAll();
        HopThoai.hienThiCanhBao(this, thongBao);
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, coTheSua, false, false, false, true);
    }

    @Override
    public boolean coTheRoiManHinh() {
        if (!coTheSua || !coThayDoiChuaLuu()) return true;
        return HopThoai.xacNhan(
                this, "Cài đặt hệ thống có thay đổi chưa lưu. Bạn có chắc muốn rời màn hình?");
    }

    @Override
    public void xuLyLamMoi() {
        if (coTheSua
                && coThayDoiChuaLuu()
                && !HopThoai.xacNhan(this, "Bỏ thay đổi cài đặt chưa lưu và đọc lại dữ liệu?")) {
            return;
        }
        nap();
    }

    @Override
    public void xuLyHienThi() {
        if (!coThayDoiChuaLuu()) nap();
    }
}
