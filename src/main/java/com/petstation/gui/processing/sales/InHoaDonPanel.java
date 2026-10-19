package com.petstation.gui.processing.sales;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.service.CaThuNganService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho in hóa đơn vùng hiển thị.
 *
 * <p>Xem trước và in hóa đơn vừa thanh toán.
 */
public final class InHoaDonPanel extends CoSoPanel {
    /** Chiều rộng. */
    private static final int CHIEU_RONG = 40;

    /** Bán hàng. */
    private final GioHang banHang;

    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Các thẻ. */
    private final CardLayout cacThe = new CardLayout();

    /** Gốc. */
    private final JPanel pnlGoc = new JPanel(cacThe);

    /** Xem trước. */
    private final JTextArea txtXemTruoc = new JTextArea();

    /** Biểu ngữ. */
    private final JLabel lblBieuNgu = new JLabel();

    /**
     * Khởi tạo InHoaDonPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param boDieuHuong bộ điều hướng
     */
    public InHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        this.boDieuHuong = boDieuHuong;
        setLayout(new BorderLayout(0, 14));

        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("In hóa đơn");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        add(lblTieuDe, BorderLayout.NORTH);

        pnlGoc.setOpaque(false);

        lblBieuNgu.setOpaque(true);
        lblBieuNgu.setBackground(new Color(0xE6, 0xF4, 0xEA));
        lblBieuNgu.setForeground(new Color(0x15, 0x57, 0x24));
        lblBieuNgu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblBieuNgu.setBorder(new EmptyBorder(10, 14, 10, 14));

        txtXemTruoc.setEditable(false);
        txtXemTruoc.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        txtXemTruoc.setBackground(Color.WHITE);
        txtXemTruoc.setBorder(new EmptyBorder(16, 20, 16, 20));
        // Cuộn.
        JScrollPane scrCuon = new JScrollPane(txtXemTruoc);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.setPreferredSize(new Dimension(430, 0));
        // Giấy.
        JPanel pnlGiay = new JPanel(new BorderLayout());
        pnlGiay.setOpaque(false);
        pnlGiay.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Ở giữa.
        JPanel pnlOGiua = new JPanel(new GridBagLayout());
        pnlOGiua.setOpaque(false);
        // Ràng buộc lưới.
        GridBagConstraints rangBuocLuoi = new GridBagConstraints();
        rangBuocLuoi.fill = GridBagConstraints.VERTICAL;
        rangBuocLuoi.weighty = 1;
        pnlOGiua.add(scrCuon, rangBuocLuoi);
        pnlGiay.add(pnlOGiua, BorderLayout.CENTER);

        // In.
        NutDinhDang btnIn = new NutDinhDang("In hóa đơn (Ctrl+P)", NutDinhDang.KieuNut.CHINH);
        btnIn.addActionListener(suKien -> xuLyIn());
        // Tiếp theo.
        NutDinhDang btnTiepTheo = new NutDinhDang("Lập hóa đơn mới (F2)", NutDinhDang.KieuNut.PHU);
        btnTiepTheo.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.POS));
        // Gợi ý.
        JLabel lblGoiY =
                new JLabel(
                        "Xem lại hóa đơn bên trên. Nút In mở hộp thoại máy in (có thể chọn \"Lưu"
                                + " thành PDF\").");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        // Các thao tác.
        JPanel pnlCacThaoTac = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlCacThaoTac.setOpaque(false);
        pnlCacThaoTac.add(btnTiepTheo);
        pnlCacThaoTac.add(btnIn);
        // Dưới.
        JPanel pnlDuoi = taoVungTrongSuot(new BorderLayout(0, 6));
        pnlDuoi.add(pnlCacThaoTac, BorderLayout.CENTER);
        pnlDuoi.add(lblGoiY, BorderLayout.SOUTH);
        lblGoiY.setHorizontalAlignment(SwingConstants.CENTER);

        // Biểu mẫu.
        JPanel pnlBieuMau = taoVungTrongSuot(new BorderLayout(0, 12));
        pnlBieuMau.add(lblBieuNgu, BorderLayout.NORTH);
        pnlBieuMau.add(pnlGiay, BorderLayout.CENTER);
        pnlBieuMau.add(pnlDuoi, BorderLayout.SOUTH);
        pnlGoc.add(pnlBieuMau, "receipt");

        // Đi.
        NutDinhDang btnDi = new NutDinhDang("Về lập đơn hàng", NutDinhDang.KieuNut.CHINH);
        btnDi.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.POS));
        pnlGoc.add(
                layTrongTrangThai(
                        "Chưa có hóa đơn nào để in",
                        "Hóa đơn xuất hiện ở đây sau khi thanh toán.",
                        btnDi),
                "empty");
        add(pnlGoc, BorderLayout.CENTER);
        lamMoi();
    }

    /** Làm mới trong InHoaDonPanel. */
    private void lamMoi() {
        // Dòng dữ liệu.
        GioHang.BienLai dongDuLieu = banHang.layBienLaiCuoi();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (dongDuLieu == null) {
            cacThe.show(pnlGoc, "empty");
            return;
        }
        cacThe.show(pnlGoc, "receipt");
        // Thay đổi.
        String thayDoi =
                dongDuLieu.phuongThuc() == CaThuNganService.PhuongThuc.CASH
                        ? "  ·  Tiền thừa " + TienTe.dinhDang(dongDuLieu.thayDoi())
                        : "";
        lblBieuNgu.setText(
                "✔ Đã thanh toán  "
                        + dongDuLieu.soHoaDon()
                        + "  ·  Tổng "
                        + TienTe.dinhDang(dongDuLieu.tong())
                        + thayDoi);
        txtXemTruoc.setText(hienThi(dongDuLieu));
        txtXemTruoc.setCaretPosition(0);
    }

    /**
     * Hiển thị trong InHoaDonPanel.
     *
     * @param dongDuLieu dòng dữ liệu
     * @return kết quả hiển thị
     */
    public static String hienThi(GioHang.BienLai dongDuLieu) {
        // Bộ ghép chuỗi.
        StringBuilder boGhepChuoi = new StringBuilder();
        boGhepChuoi
                .append(layGiua(com.petstation.service.CaiDatService.lay("STORE_NAME")))
                .append('\n');
        boGhepChuoi
                .append(layGiua(com.petstation.service.CaiDatService.lay("STORE_ADDRESS")))
                .append('\n');
        boGhepChuoi
                .append(layGiua(com.petstation.service.CaiDatService.lay("STORE_PHONE")))
                .append('\n');
        boGhepChuoi.append(layGiua("Hóa đơn bán hàng")).append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append("Số HĐ : ").append(dongDuLieu.soHoaDon()).append('\n');
        boGhepChuoi
                .append("Ngày  : ")
                .append(
                        dongDuLieu
                                .thoiGian()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .append('\n');
        boGhepChuoi.append("Thu ngân: ").append(dongDuLieu.thuNgan()).append('\n');
        boGhepChuoi.append("Khách : ").append(dongDuLieu.khachHang()).append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        for (GioHang.DongHang dongDangXet : dongDuLieu.cacDong()) {
            boGhepChuoi.append(layCat(dongDangXet.ten(), CHIEU_RONG)).append('\n');
            boGhepChuoi
                    .append(
                            layCap(
                                    ""
                                            + dongDangXet.soLuong()
                                            + "x "
                                            + TienTe.dinhDang(dongDangXet.donGia()),
                                    TienTe.dinhDang(dongDangXet.docSoTien())))
                    .append('\n');
        }
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append(layCap("Tạm tính", TienTe.dinhDang(dongDuLieu.tamTinh()))).append('\n');
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (dongDuLieu.giamGia() > 0) {
            boGhepChuoi
                    .append(
                            layCap(
                                    "Giảm giá"
                                            + (dongDuLieu.voucher().isEmpty()
                                                    ? ""
                                                    : "(" + dongDuLieu.voucher() + ")"),
                                    "-" + TienTe.dinhDang(dongDuLieu.giamGia())))
                    .append('\n');
        }
        boGhepChuoi.append(layCap("TỔNG CỘNG", TienTe.dinhDang(dongDuLieu.tong()))).append('\n');
        // Phương thức.
        String phuongThuc =
                switch (dongDuLieu.phuongThuc()) {
                    case CASH -> "Tiền mặt";
                    case TRANSFER -> "Chuyển khoản/QR";
                    case CARD -> "Thẻ";
                };
        boGhepChuoi
                .append(
                        layCap(
                                "Thanh toán (" + phuongThuc + ")",
                                TienTe.dinhDang(dongDuLieu.daNhan())))
                .append('\n');
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (dongDuLieu.phuongThuc() == CaThuNganService.PhuongThuc.CASH)
            boGhepChuoi
                    .append(layCap("Tiền thừa", TienTe.dinhDang(dongDuLieu.thayDoi())))
                    .append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append(layGiua("Cảm ơn quý khách!")).append('\n');
        return boGhepChuoi.toString();
    }

    /**
     * Lấy giữa trong InHoaDonPanel.
     *
     * @param chuoi chuỗi
     * @return kết quả lấy giữa
     */
    private static String layGiua(String chuoi) {
        // Đệm.
        int dem = Math.max(0, (CHIEU_RONG - chuoi.length()) / 2);
        return " ".repeat(dem) + chuoi;
    }

    /**
     * Lấy cắt trong InHoaDonPanel.
     *
     * @param chuoi chuỗi
     * @param lonNhat lớn nhất
     * @return kết quả lấy cắt
     */
    private static String layCat(String chuoi, int lonNhat) {
        return chuoi.length() <= lonNhat ? chuoi : chuoi.substring(0, lonNhat - 1) + "…";
    }

    /**
     * Lấy cặp trong InHoaDonPanel.
     *
     * @param trai trái
     * @param phai phải
     * @return kết quả lấy cặp
     */
    private static String layCap(String trai, String phai) {
        // Khoảng cách.
        int khoangCach = Math.max(1, CHIEU_RONG - trai.length() - phai.length());
        return trai + " ".repeat(khoangCach) + phai;
    }

    /**
     * Lấy khả năng thanh công cụ trong InHoaDonPanel.
     *
     * @return kết quả lấy khả năng thanh công cụ
     */
    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(
                false, false, false, banHang.layBienLaiCuoi() != null, false, true);
    }

    /** Xử lý in trong InHoaDonPanel. */
    @Override
    public void xuLyIn() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (banHang.layBienLaiCuoi() == null) return;
        try {
            txtXemTruoc.print(null, null, true, null, null, true);
        } catch (java.awt.print.PrinterException | HeadlessException ngoaiLe) {
            HopThoai.hienThiThongBaoHopThoai(
                    this,
                    "Không thể in: " + ngoaiLe.getMessage(),
                    "In hóa đơn",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Xử lý hiển thị trong InHoaDonPanel. */
    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    /**
     * Lấy nội dung trợ giúp trong InHoaDonPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Bấm In hóa đơn để gửi tới máy in. Bấm Lập đơn mới để bắt đầu đơn tiếp theo.";
    }

    /**
     * Lấy tiêu đề trợ giúp trong InHoaDonPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "In hóa đơn";
    }
}
