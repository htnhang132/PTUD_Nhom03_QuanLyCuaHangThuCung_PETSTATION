package com.petstation.gui.banhang;

import com.petstation.dto.DonDoiTreo;
import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.banhang.DonDoiTreoService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public final class DoiPhuKienDialog extends JDialog {
    private final DoiTraService doiTraService = new DoiTraService();
    private final DonDoiTreoService donDoiTreoService = new DonDoiTreoService();
    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final JTextField timHoaDon = new JTextField(24);
    private final JTextField timHang = new JTextField(24);
    private final JTextField lyDo = new JTextField(30);
    private final JSpinner soLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JSpinner soLuongMoi = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JCheckBox tem = new JCheckBox("Nguyên tem mác và bao bì");
    private final JCheckBox chuaDung = new JCheckBox("Chưa qua sử dụng, không rách bẩn");
    private final JLabel trangThai = new JLabel("Nhập mã hóa đơn hoặc SĐT để bắt đầu.");
    private final JLabel tongKet = new JLabel("Chưa chọn hàng đổi và hàng thay thế.");
    private final NutDinhDang tiepTuc =
            new NutDinhDang("Tiếp tục thanh toán", NutDinhDang.KieuNut.CHINH);
    private final DefaultTableModel moHinhHoaDon =
            model("Hóa đơn", "Khách hàng", "Ngày lập", "Tổng tiền");
    private final DefaultTableModel moHinhDong =
            model("Sản phẩm", "Nhóm", "Đã mua", "Đã đổi", "Còn đổi", "Giá bán gốc");
    private final DefaultTableModel moHinhHang =
            model("Mã phụ kiện", "Tên phụ kiện", "Đơn giá", "Tồn khả dụng");
    private final JTable hoaDonBang = bang(moHinhHoaDon);
    private final JTable dongBang = bang(moHinhDong);
    private final JTable hangBang = bang(moHinhHang);
    private List<Object[]> hoaDons = List.of(), dongs = List.of(), hangs = List.of();
    private boolean dangChay;
    private boolean dangKhoiPhuc;
    private boolean xuLyXong;
    private boolean treoDonFlag;
    private boolean dongBoSoLuongMoi = true;
    private final String maDonTreo;

    private long phien;
    private long giaTriCu = -1;

    public boolean daXuLyXong() {
        return xuLyXong;
    }

    public boolean daTreoDon() {
        return treoDonFlag;
    }

    public DoiPhuKienDialog(Component cha, GioHang gio, Consumer<MaManHinh> dieuHuong) {
        this(cha, gio, dieuHuong, "", null);
    }

    public DoiPhuKienDialog(
            Component cha,
            GioHang gio,
            Consumer<MaManHinh> dieuHuong,
            String maDonTreo,
            DonDoiTreo nhap) {
        super(
                SwingUtilities.getWindowAncestor(cha),
                "Đổi phụ kiện — không hoàn tiền",
                ModalityType.APPLICATION_MODAL);
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        this.maDonTreo = maDonTreo == null ? "" : maDonTreo;
        getContentPane().setBackground(MauUngDung.NEN_KEM);
        add(taoPhanDauHopThoai(), BorderLayout.NORTH);
        cauHinhTruongNhapDoi();
        add(taoNoiDungChinhHopThoai(), BorderLayout.CENTER);
        add(taoChanHopThoai(), BorderLayout.SOUTH);
        ganSuKienHopThoai(nhap);
        thietLapKichThuocHopThoai();
    }

    private JPanel taoPhanDauHopThoai() {
        JPanel dau = new JPanel(new BorderLayout(0, 4));
        dau.setOpaque(false);
        dau.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        JLabel tieuDe = new JLabel("Đổi phụ kiện");
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        tieuDe.setForeground(MauUngDung.NAU);
        dau.add(tieuDe, BorderLayout.NORTH);
        dau.add(
                goiY("Đổi trong 72 giờ • Hàng thay thế có giá bằng hoặc cao hơn • Không hoàn tiền"),
                BorderLayout.SOUTH);
        return dau;
    }

    private void cauHinhTruongNhapDoi() {
        timHoaDon.putClientProperty(
                "JTextField.placeholderText", "Mã hóa đơn hoặc số điện thoại khách hàng");
        timHang.putClientProperty("JTextField.placeholderText", "Mã hoặc tên phụ kiện thay thế");
        lyDo.putClientProperty("JTextField.placeholderText", "Nhập lý do khách đổi hàng");
        timHoaDon.getAccessibleContext().setAccessibleName("Tìm hóa đơn gốc");
        timHang.getAccessibleContext().setAccessibleName("Tìm phụ kiện thay thế");
        lyDo.getAccessibleContext().setAccessibleName("Lý do đổi hàng");
        for (JTextField o : new JTextField[] {timHoaDon, timHang, lyDo}) {
            o.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            o.setPreferredSize(new Dimension(200, 34));
        }
        soLuong.setPreferredSize(new Dimension(90, 34));
        soLuongMoi.setPreferredSize(new Dimension(90, 34));
        tem.setOpaque(false);
        chuaDung.setOpaque(false);
        trangThai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        trangThai.setForeground(MauUngDung.VAN_BAN_XAM);
    }

    private JScrollPane taoNoiDungChinhHopThoai() {
        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setBackground(MauUngDung.NEN_KEM);
        noiDung.setBorder(BorderFactory.createEmptyBorder(0, 20, 12, 20));
        noiDung.add(
                nhom(
                        "1. Chọn hóa đơn gốc",
                        thanhTim(timHoaDon, nut("Tìm hóa đơn", this::timHoaDon)),
                        cuon(hoaDonBang, 100)));
        noiDung.add(Box.createVerticalStrut(12));
        JPanel thongTinDoi = new JPanel(new BorderLayout(16, 0));
        thongTinDoi.setOpaque(false);
        thongTinDoi.add(hang(new JLabel("Số lượng đổi"), soLuong), BorderLayout.WEST);
        JPanel nhapLyDo = new JPanel(new BorderLayout(8, 0));
        nhapLyDo.setOpaque(false);
        JLabel nhanLyDo = new JLabel("Lý do");
        nhanLyDo.setLabelFor(lyDo);
        nhapLyDo.add(nhanLyDo, BorderLayout.WEST);
        nhapLyDo.add(lyDo, BorderLayout.CENTER);
        thongTinDoi.add(nhapLyDo, BorderLayout.CENTER);
        noiDung.add(
                nhom(
                        "2. Chọn hàng cần đổi",
                        cuon(dongBang, 130),
                        thongTinDoi,
                        hang(tem, chuaDung)));
        noiDung.add(Box.createVerticalStrut(12));
        noiDung.add(
                nhom(
                        "3. Chọn phụ kiện thay thế",
                        thanhTim(timHang, nut("Tìm phụ kiện", this::timPhuKien)),
                        cuon(hangBang, 150),
                        hang(new JLabel("Số lượng thay thế"), soLuongMoi),
                        goiY("Tổng tiền hàng thay thế phải bằng hoặc lớn hơn hàng trả.")));
        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cuon.getVerticalScrollBar().setUnitIncrement(18);
        return cuon;
    }

    private JPanel taoChanHopThoai() {
        JPanel chan = new JPanel(new BorderLayout(0, 10));
        chan.setBackground(MauUngDung.NEN_KEM);
        chan.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
                        BorderFactory.createEmptyBorder(12, 20, 14, 20)));
        tongKet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        tongKet.setForeground(MauUngDung.NAU);
        chan.add(
                nhom(
                        "Tổng kết đổi hàng",
                        tongKet,
                        goiY(
                                "Giữ nguyên điểm cũ • Tích điểm trên tiền trả thêm • Không áp dụng"
                                        + " voucher Spa")),
                BorderLayout.NORTH);
        JPanel trangThaiDong = new JPanel(new BorderLayout(0, 6));
        trangThaiDong.setOpaque(false);
        trangThaiDong.add(trangThai, BorderLayout.NORTH);
        JPanel nutDong = new JPanel(new BorderLayout());
        nutDong.setOpaque(false);
        nutDong.add(nut("Hủy", this::huy), BorderLayout.WEST);
        nutDong.add(
                hang(
                        nut("Treo đơn đổi", this::treoDon),
                        nut("Kiểm tra chênh lệch", this::tinhGia),
                        tiepTuc),
                BorderLayout.EAST);
        trangThaiDong.add(nutDong, BorderLayout.CENTER);
        chan.add(trangThaiDong, BorderLayout.CENTER);
        return chan;
    }

    private void ganSuKienHopThoai(DonDoiTreo nhap) {
        tiepTuc.setEnabled(false);
        tiepTuc.addActionListener(e -> chuyenThanhToan());
        timHoaDon.addActionListener(e -> timHoaDon());
        timHang.addActionListener(e -> timPhuKien());
        hoaDonBang
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && !dangChay && !dangKhoiPhuc)
                                chonHoaDon();
                        });
        dongBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && !dangKhoiPhuc) {
                                tem.setSelected(false);
                                chuaDung.setSelected(false);
                                voHieuGia();
                            }
                        });
        hangBang.getSelectionModel().addListSelectionListener(e -> voHieuGia());
        soLuong.addChangeListener(
                e -> {
                    if (!dangKhoiPhuc && dongBoSoLuongMoi) {
                        soLuongMoi.setValue(soLuong.getValue());
                    }
                    voHieuGia();
                });
        soLuongMoi.addChangeListener(
                e -> {
                    if (!dangKhoiPhuc) {
                        dongBoSoLuongMoi =
                                Integer.valueOf((Integer) soLuong.getValue())
                                        .equals(soLuongMoi.getValue());
                    }
                    voHieuGia();
                });
        tem.addActionListener(e -> voHieuGia());
        chuaDung.addActionListener(e -> voHieuGia());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        huy();
                    }
                });
        if (nhap != null) SwingUtilities.invokeLater(() -> khoiPhuc(nhap));
    }

    private void thietLapKichThuocHopThoai() {
        Rectangle vung = getGraphicsConfiguration().getBounds();
        Insets le = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setSize(
                Math.min(1060, vung.width - le.left - le.right - 32),
                Math.min(900, vung.height - le.top - le.bottom - 32));
        setMinimumSize(new Dimension(820, 550));
        HopThoai.chuanHoaCuaSo(this, getOwner());
        getRootPane()
                .registerKeyboardAction(
                        e -> huy(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private DonDoiTreo chupNhap() throws ParseException {
        soLuong.commitEdit();
        soLuongMoi.commitEdit();
        int h = hoaDonBang.getSelectedRow(),
                d = dongBang.getSelectedRow(),
                m = hangBang.getSelectedRow();
        return new DonDoiTreo(
                h < 0 ? timHoaDon.getText() : hoaDons.get(h)[1].toString(),
                d < 0 ? "" : String.valueOf(dongs.get(d)[0]),
                m < 0 ? "" : hangs.get(m)[0].toString(),
                timHang.getText(),
                (Integer) soLuong.getValue(),
                (Integer) soLuongMoi.getValue(),
                lyDo.getText(),
                tem.isSelected(),
                chuaDung.isSelected());
    }

    private void treoDon() {
        if (dangChay) return;
        try {
            DonDoiTreo nhap = chupNhap();
            chayNen(
                    () -> donDoiTreoService.treo(maDonTreo, nhap),
                    id -> {
                        xuLyXong = true;
                        treoDonFlag = true;
                        dispose();
                        dieuHuong.accept(MaManHinh.LAP_HOA_DON);
                        HopThoai.hienThiThanhCong(
                                getOwner(),
                                "Đã treo đơn đổi. Mở lại tại Đơn đang treo để tiếp tục.");
                    });
        } catch (Exception e) {
            baoLoi(e);
        }
    }

    private static final class DuLieuNhap {
        private final List<Object[]> hoaDon;
        private final List<Object[]> dong;
        private final List<Object[]> hang;
        private final int chonHoaDon;

        private DuLieuNhap(
                List<Object[]> hoaDon, List<Object[]> dong, List<Object[]> hang, int chonHoaDon) {
            this.hoaDon = hoaDon;
            this.dong = dong;
            this.hang = hang;
            this.chonHoaDon = chonHoaDon;
        }
    }

    private void khoiPhuc(DonDoiTreo nhap) {
        dangKhoiPhuc = true;
        try {
            timHoaDon.setText(nhap.timHoaDon());
            timHang.setText(nhap.timHang());
            lyDo.setText(nhap.lyDo());
            soLuong.setValue(nhap.soLuong());
            soLuongMoi.setValue(nhap.soLuongMoi());
            dongBoSoLuongMoi = nhap.soLuongMoi() == nhap.soLuong();
            tem.setSelected(nhap.nguyenTem());
            chuaDung.setSelected(nhap.chuaDung());
        } finally {
            dangKhoiPhuc = false;
        }
        chayNen(
                () -> {
                    var hd = doiTraService.timHoaDon(nhap.timHoaDon());
                    List<Object[]> dong = List.of();
                    int chon = -1;
                    for (int i = 0; i < hd.size(); i++) {
                        if (nhap.dongGoc().isBlank()) {
                            if (hd.get(i)[1].toString().equals(nhap.timHoaDon())) {
                                dong = doiTraService.layDongHoaDon(String.valueOf(hd.get(i)[0]));
                                chon = i;
                            }
                            continue;
                        }
                        var ds = doiTraService.layDongHoaDon(String.valueOf(hd.get(i)[0]));
                        if (ds.stream()
                                .anyMatch(d -> String.valueOf(d[0]).equals(nhap.dongGoc()))) {
                            dong = ds;
                            chon = i;
                            break;
                        }
                    }
                    var hang =
                            doiTraService.timPhuKien(
                                    nhap.maThayThe().isBlank() ? nhap.timHang() : nhap.maThayThe());
                    return new DuLieuNhap(hd, dong, hang, chon);
                },
                dl -> {
                    dangKhoiPhuc = true;
                    try {
                        hoaDons = dl.hoaDon;
                        dongs = dl.dong;
                        hangs = dl.hang;
                        moHinhHoaDon.setRowCount(0);
                        moHinhDong.setRowCount(0);
                        moHinhHang.setRowCount(0);
                        for (Object[] h : hoaDons)
                            moHinhHoaDon.addRow(new Object[] {h[1], h[2], h[3], tien(h[4])});
                        if (dl.chonHoaDon >= 0)
                            hoaDonBang.setRowSelectionInterval(dl.chonHoaDon, dl.chonHoaDon);
                        for (int i = 0; i < dongs.size(); i++) {
                            Object[] d = dongs.get(i);
                            moHinhDong.addRow(
                                    new Object[] {
                                        d[1], d[2], d[3], d[4], so(d[3]) - so(d[4]), tien(d[5])
                                    });
                            if (String.valueOf(d[0]).equals(nhap.dongGoc()))
                                dongBang.setRowSelectionInterval(i, i);
                        }
                        for (int i = 0; i < hangs.size(); i++) {
                            Object[] h = hangs.get(i);
                            moHinhHang.addRow(new Object[] {h[0], h[1], tien(h[2]), h[3]});
                            if (h[0].toString().equals(nhap.maThayThe()))
                                hangBang.setRowSelectionInterval(i, i);
                        }
                        tem.setSelected(nhap.nguyenTem());
                        chuaDung.setSelected(nhap.chuaDung());
                        trangThai.setText(
                                "Đã mở đơn đổi treo. Chọn hàng và kiểm tra lại chênh lệch trước khi"
                                        + " thanh toán.");
                        voHieuGia();
                    } finally {
                        dangKhoiPhuc = false;
                    }
                });
    }

    private void timHoaDon() {
        if (dangChay) return;
        voHieuGia();
        hoaDons = List.of();
        dongs = List.of();
        moHinhHoaDon.setRowCount(0);
        moHinhDong.setRowCount(0);
        String tuKhoa = timHoaDon.getText();
        chayNen(
                () -> doiTraService.timHoaDon(tuKhoa),
                ds -> {
                    hoaDons = ds;
                    for (Object[] h : ds)
                        moHinhHoaDon.addRow(new Object[] {h[1], h[2], h[3], tien(h[4])});
                    trangThai.setText(
                            ds.isEmpty()
                                    ? "Không tìm thấy hóa đơn đã thanh toán."
                                    : "Chọn hóa đơn để xem hàng đã mua.");
                    if (!ds.isEmpty()) hoaDonBang.setRowSelectionInterval(0, 0);
                });
    }

    private void chonHoaDon() {
        voHieuGia();
        dongs = List.of();
        moHinhDong.setRowCount(0);
        int i = hoaDonBang.getSelectedRow();
        if (i < 0) return;
        String maHoaDon = String.valueOf(hoaDons.get(i)[0]);
        chayNen(
                () -> doiTraService.layDongHoaDon(maHoaDon),
                ds -> {
                    dongs = ds;
                    for (Object[] d : ds)
                        moHinhDong.addRow(
                                new Object[] {
                                    d[1], d[2], d[3], d[4], so(d[3]) - so(d[4]), tien(d[5])
                                });
                    trangThai.setText(
                            "Chọn hàng và xác nhận tình trạng thực tế. Hạn đổi: 72 giờ từ ngày lập"
                                    + " hóa đơn.");
                });
    }

    private void timPhuKien() {
        if (dangChay) return;
        voHieuGia();
        hangs = List.of();
        moHinhHang.setRowCount(0);
        String tuKhoa = timHang.getText();
        chayNen(
                () -> doiTraService.timPhuKien(tuKhoa),
                ds -> {
                    hangs = ds;
                    for (Object[] h : ds)
                        moHinhHang.addRow(new Object[] {h[0], h[1], tien(h[2]), h[3]});
                    if (ds.isEmpty()) trangThai.setText("Không tìm thấy phụ kiện đang kinh doanh.");
                });
    }

    private void tinhGia() {
        if (dangChay) return;
        try {
            soLuong.commitEdit();
            soLuongMoi.commitEdit();
            Object[] cu = dongChon();
            Object[] moi = hangChon();
            int sl = (Integer) soLuong.getValue();
            int slMoi = (Integer) soLuongMoi.getValue();
            kiemTraTinhTrang();
            if (sl > so(cu[3]) - so(cu[4]))
                throw new IllegalArgumentException("Số lượng vượt phần chưa đổi.");
            if (slMoi > so(moi[3]))
                throw new IllegalStateException("Phụ kiện thay thế không đủ tồn.");
            String maDong = String.valueOf(cu[0]);
            long giaMoi = so(moi[2]);
            long luot = ++phien;
            chayNen(
                    () -> doiTraService.layGiaTriDuocDoi(maDong, sl),
                    cuTien -> {
                        if (luot != phien) return;
                        long moiTien = Math.multiplyExact(giaMoi, slMoi);
                        if (moiTien < cuTien) {
                            baoLoi(
                                    new IllegalArgumentException(
                                            "Hàng thay thế phải bằng hoặc cao giá hơn hàng cũ."));
                            return;
                        }
                        giaTriCu = cuTien;
                        long chenhlech = moiTien - cuTien;
                        tongKet.setText(
                                "Hàng cũ: "
                                        + tien(cuTien)
                                        + "    Hàng mới: "
                                        + tien(moiTien)
                                        + "    Cần thu: "
                                        + tien(chenhlech)
                                        + "    Điểm thêm: "
                                        + DoiTraService.tinhDiemChenhLech(chenhlech));
                        trangThai.setText("Đủ điều kiện. Hệ thống sẽ kiểm tra lại khi thanh toán.");
                        tiepTuc.setEnabled(true);
                    });
        } catch (Exception e) {
            baoLoi(e);
        }
    }

    private void chuyenThanhToan() {
        if (dangChay || giaTriCu < 0) return;
        try {
            soLuong.commitEdit();
            soLuongMoi.commitEdit();
            kiemTraTinhTrang();
            if (lyDo.getText().isBlank())
                throw new IllegalArgumentException("Vui lòng nhập lý do đổi.");
            if (!gio.kiemTraTrong()) {
                if (HopThoai.xacNhanDongY(
                        this,
                        "Giỏ hàng bán lẻ hiện tại đang có sản phẩm chưa thanh toán.\n"
                                + "Bạn có muốn làm trống giỏ hàng để chuyển sang giỏ đổi hàng"
                                + " ngay không?",
                        "Giỏ hàng đang có sản phẩm")) {
                    gio.xoa();
                } else {
                    return;
                }
            }
            String dong = String.valueOf(dongChon()[0]);
            String ma = hangChon()[0].toString();
            int sl = (Integer) soLuong.getValue();
            int slMoi = (Integer) soLuongMoi.getValue();
            String xacNhan =
                    " | Đã kiểm tra: nguyên tem mác, bao bì; chưa sử dụng, không rách bẩn.";
            String ghiChu =
                    lyDo.getText().trim().endsWith(xacNhan)
                            ? lyDo.getText().trim()
                            : lyDo.getText().trim() + xacNhan;
            chayNen(
                    () -> {
                        donDoiTreoService.chuyenThanhToan(
                                maDonTreo, gio, dong, sl, slMoi, ma, ghiChu);
                        return true;
                    },
                    ok -> {
                        xuLyXong = true;
                        dispose();
                        dieuHuong.accept(MaManHinh.THANH_TOAN);
                    });
        } catch (Exception e) {
            baoLoi(e);
        }
    }

    private void kiemTraTinhTrang() {
        if (!tem.isSelected() || !chuaDung.isSelected())
            throw new IllegalArgumentException(
                    "Chỉ nhận hàng nguyên tem mác, bao bì, chưa sử dụng và không rách bẩn.");
    }

    private Object[] dongChon() {
        int i = dongBang.getSelectedRow();
        if (i < 0) throw new IllegalArgumentException("Chọn hàng trên hóa đơn gốc.");
        return dongs.get(i);
    }

    private Object[] hangChon() {
        int i = hangBang.getSelectedRow();
        if (i < 0) throw new IllegalArgumentException("Chọn phụ kiện thay thế.");
        return hangs.get(i);
    }

    private void voHieuGia() {
        phien++;
        giaTriCu = -1;
        tiepTuc.setEnabled(false);
        tongKet.setText("Chọn hàng cần đổi và phụ kiện thay thế, sau đó kiểm tra chênh lệch.");
    }

    private void huy() {
        if (dangChay) return;
        if (!timHoaDon.getText().isBlank() || !dongs.isEmpty() || !lyDo.getText().isBlank()) {
            String[] luaChon = {"Treo đơn đổi", "Bỏ thay đổi", "Tiếp tục nhập"};
            int chon =
                    HopThoai.tuyChon(
                            this,
                            !maDonTreo.isBlank()
                                    ? "Lưu thay đổi vào đơn treo trước khi đóng? Bản đã lưu vẫn"
                                            + " được giữ."
                                    : "Bạn muốn treo đơn để tiếp tục sau?",
                            "Đóng đơn đổi",
                            luaChon,
                            true);
            if (chon == 0) {
                treoDon();
                return;
            }
            if (chon != 1) return;
        }
        dispose();
    }

    private <T> void chayNen(Callable<T> viec, Consumer<T> xong) {
        if (dangChay) return;
        dangChay = true;
        tiepTuc.setEnabled(false);
        khoa(getContentPane(), false);
        var phienDangNhap = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chay(
                () -> PhienDangNhapService.macDinh().trongPhien(phienDangNhap, viec),
                kq -> {
                    dangChay = false;
                    khoa(getContentPane(), true);
                    tiepTuc.setEnabled(false);
                    xong.accept(kq);
                },
                loi -> {
                    dangChay = false;
                    khoa(getContentPane(), true);
                    tiepTuc.setEnabled(false);
                    baoLoi(loi);
                });
    }

    private static void khoa(Container c, boolean mo) {
        for (Component v : c.getComponents()) {
            v.setEnabled(mo);
            if (v instanceof Container con) khoa(con, mo);
        }
    }

    private void baoLoi(Throwable loi) {
        String thongDiep = TacVuNen.thongDiepLoi(loi);
        trangThai.setText(thongDiep);
        HopThoai.hienThiCanhBao(this, thongDiep);
    }

    private static long so(Object v) {
        return ((Number) v).longValue();
    }

    private static String tien(Object v) {
        return DinhDang.dinhDang(so(v));
    }

    private static DefaultTableModel model(String... cot) {
        return new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private static JTable bang(DefaultTableModel m) {
        JTable t = new JTable(m);
        DinhDangBang.apDung(t);
        t.setAutoCreateRowSorter(false);
        t.setRowSorter(null);
        t.setRowHeight(36);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < t.getColumnCount(); i++) {
            String ten = t.getColumnName(i);
            int rong =
                    ten.equals("Sản phẩm") || ten.equals("Tên phụ kiện")
                            ? 300
                            : ten.equals("Đã mua") || ten.equals("Đã đổi") || ten.equals("Còn đổi")
                                    ? 65
                                    : 150;
            t.getColumnModel().getColumn(i).setPreferredWidth(rong);
        }
        return t;
    }

    private static JScrollPane cuon(JTable bang, int cao) {
        JScrollPane s = new JScrollPane(bang);
        s.setPreferredSize(new Dimension(760, cao));
        s.setMinimumSize(new Dimension(0, cao));
        s.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        s.getViewport().setBackground(MauUngDung.THE_NEN);
        s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return s;
    }

    private static JPanel hang(Component... cac) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        for (Component c : cac) p.add(c);
        return p;
    }

    private static JPanel nhom(String ten, Component... cac) {
        JPanel p = new BoGocPanel(14, MauUngDung.THE_NEN);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JLabel nhan = new JLabel(ten);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhan.setForeground(MauUngDung.NAU);
        nhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(nhan);
        for (Component c : cac) {
            p.add(Box.createVerticalStrut(8));
            if (c instanceof JComponent j) {
                j.setAlignmentX(Component.LEFT_ALIGNMENT);
                j.setMaximumSize(new Dimension(Integer.MAX_VALUE, j.getPreferredSize().height));
            }
            p.add(c);
        }
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private static JPanel thanhTim(JTextField truong, JButton nut) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.add(truong, BorderLayout.CENTER);
        p.add(nut, BorderLayout.EAST);
        return p;
    }

    private static JLabel goiY(String noiDung) {
        JLabel nhan = new JLabel(noiDung);
        nhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_XAM);
        return nhan;
    }

    private static JButton nut(String ten, Runnable viec) {
        JButton b = new NutDinhDang(ten, NutDinhDang.KieuNut.PHU);
        b.addActionListener(e -> viec.run());
        return b;
    }
}
