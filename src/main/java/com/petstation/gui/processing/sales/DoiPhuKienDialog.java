package com.petstation.gui.processing.sales;

import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.BoGocPanel;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.model.GioHang;
import com.petstation.model.DonDoiTreo;
import com.petstation.model.MaManHinh;
import com.petstation.service.DoiTraService;
import com.petstation.service.DonDoiTreoService;
import com.petstation.gui.components.HopThoai;
import com.petstation.util.TienTe;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class DoiPhuKienDialog extends JDialog {
    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final JTextField timHoaDon = new JTextField(24);
    private final JTextField timHang = new JTextField(24);
    private final JTextField lyDo = new JTextField(30);
    private final JSpinner soLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JCheckBox tem = new JCheckBox("Nguyên tem mác và bao bì");
    private final JCheckBox chuaDung = new JCheckBox("Chưa qua sử dụng, không rách bẩn");
    private final JLabel trangThai = new JLabel("Nhập mã hóa đơn hoặc SĐT để bắt đầu.");
    private final JLabel tongKet = new JLabel("Chưa chọn hàng đổi và hàng thay thế.");
    private final NutDinhDang tiepTuc = new NutDinhDang("Tiếp tục thanh toán", NutDinhDang.KieuNut.CHINH);
    private final DefaultTableModel hoaDonModel = model("Hóa đơn", "Khách hàng", "Ngày lập", "Tổng tiền");
    private final DefaultTableModel dongModel = model("Sản phẩm", "Nhóm", "Đã mua", "Đã đổi", "Còn đổi", "Giá bán gốc");
    private final DefaultTableModel hangModel = model("Mã phụ kiện", "Tên phụ kiện", "Đơn giá", "Tồn khả dụng");
    private final JTable hoaDonBang = bang(hoaDonModel);
    private final JTable dongBang = bang(dongModel);
    private final JTable hangBang = bang(hangModel);
    private List<Object[]> hoaDons = List.of(), dongs = List.of(), hangs = List.of();
    private boolean dangChay;
    private boolean dangKhoiPhuc;
    private final long idDonTreo;

    private long phien;
    private long giaTriCu = -1;

    public DoiPhuKienDialog(Component cha, GioHang gio, Consumer<MaManHinh> dieuHuong) {
        this(cha, gio, dieuHuong, 0, null);
    }

    public DoiPhuKienDialog(Component cha, GioHang gio, Consumer<MaManHinh> dieuHuong,
            long idDonTreo, DonDoiTreo nhap) {
        super(SwingUtilities.getWindowAncestor(cha), "Đổi phụ kiện — không hoàn tiền", ModalityType.APPLICATION_MODAL);
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        this.idDonTreo = idDonTreo;
        getContentPane().setBackground(MauUngDung.NEN_KEM);
        JPanel dau = new JPanel(new BorderLayout(0, 4));
        dau.setOpaque(false);
        dau.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        JLabel tieuDe = new JLabel("Đổi phụ kiện");
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        tieuDe.setForeground(MauUngDung.NAU);
        dau.add(tieuDe, BorderLayout.NORTH);
        dau.add(goiY("Đổi trong 72 giờ • Hàng thay thế có giá bằng hoặc cao hơn • Không hoàn tiền"), BorderLayout.SOUTH);
        add(dau, BorderLayout.NORTH);

        timHoaDon.putClientProperty("JTextField.placeholderText", "Mã hóa đơn hoặc số điện thoại khách hàng");
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
        tem.setOpaque(false);
        chuaDung.setOpaque(false);
        trangThai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        trangThai.setForeground(MauUngDung.VAN_BAN_XAM);

        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setBackground(MauUngDung.NEN_KEM);
        noiDung.setBorder(BorderFactory.createEmptyBorder(0, 20, 12, 20));
        noiDung.add(nhom("1. Chọn hóa đơn gốc",
            thanhTim(timHoaDon, nut("Tìm hóa đơn", this::timHoaDon)), cuon(hoaDonBang, 100)));
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
        noiDung.add(nhom("2. Chọn hàng cần đổi", cuon(dongBang, 130),
            thongTinDoi, hang(tem, chuaDung)));
        noiDung.add(Box.createVerticalStrut(12));
        noiDung.add(nhom("3. Chọn phụ kiện thay thế",
            thanhTim(timHang, nut("Tìm phụ kiện", this::timPhuKien)), cuon(hangBang, 150),
            goiY("Số lượng hàng thay thế bằng số lượng hàng cần đổi.")));
        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cuon.getVerticalScrollBar().setUnitIncrement(18);
        add(cuon, BorderLayout.CENTER);

        JPanel chan = new JPanel(new BorderLayout(0, 10));
        chan.setBackground(MauUngDung.NEN_KEM);
        chan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
            BorderFactory.createEmptyBorder(12, 20, 14, 20)));
        tongKet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        tongKet.setForeground(MauUngDung.NAU);
        chan.add(nhom("Tổng kết đổi hàng", tongKet,
            goiY("Giữ nguyên điểm cũ • Tích điểm trên tiền trả thêm • Không áp dụng voucher Spa")), BorderLayout.NORTH);
        JPanel trangThaiDong = new JPanel(new BorderLayout(0, 6));
        trangThaiDong.setOpaque(false);
        trangThaiDong.add(trangThai, BorderLayout.NORTH);
        JPanel nutDong = new JPanel(new BorderLayout());
        nutDong.setOpaque(false);
        nutDong.add(nut("Hủy", this::huy), BorderLayout.WEST);
        nutDong.add(hang(nut("Treo đơn đổi", this::treoDon),
            nut("Kiểm tra chênh lệch", this::tinhGia), tiepTuc), BorderLayout.EAST);
        trangThaiDong.add(nutDong, BorderLayout.CENTER);
        chan.add(trangThaiDong, BorderLayout.CENTER);
        add(chan, BorderLayout.SOUTH);
        tiepTuc.setEnabled(false);
        tiepTuc.addActionListener(e -> chuyenThanhToan());
        timHoaDon.addActionListener(e -> timHoaDon());
        timHang.addActionListener(e -> timPhuKien());
        hoaDonBang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !dangChay && !dangKhoiPhuc) chonHoaDon();
        });
        dongBang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { tem.setSelected(false); chuaDung.setSelected(false); voHieuGia(); }
        });
        hangBang.getSelectionModel().addListSelectionListener(e -> voHieuGia());
        soLuong.addChangeListener(e -> voHieuGia());
        tem.addActionListener(e -> voHieuGia());
        chuaDung.addActionListener(e -> voHieuGia());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { huy(); }
        });
        Rectangle vung = getGraphicsConfiguration().getBounds();
        Insets le = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setSize(Math.min(1060, vung.width - le.left - le.right - 32),
            Math.min(900, vung.height - le.top - le.bottom - 32));
        setMinimumSize(new Dimension(820, 550));
        setLocationRelativeTo(cha);
        if (nhap != null) SwingUtilities.invokeLater(() -> khoiPhuc(nhap));
    }

    private DonDoiTreo chupNhap() throws java.text.ParseException {
        soLuong.commitEdit();
        int h = hoaDonBang.getSelectedRow(), d = dongBang.getSelectedRow(), m = hangBang.getSelectedRow();
        return new DonDoiTreo(h < 0 ? timHoaDon.getText() : hoaDons.get(h)[1].toString(),
            d < 0 ? 0 : so(dongs.get(d)[0]), m < 0 ? "" : hangs.get(m)[0].toString(),
            timHang.getText(), (Integer) soLuong.getValue(), lyDo.getText(),
            tem.isSelected(), chuaDung.isSelected());
    }

    private void treoDon() {
        if (dangChay) return;
        try {
            DonDoiTreo nhap = chupNhap();
            chayNen(() -> DonDoiTreoService.treo(idDonTreo, nhap), id -> {
                dispose();
                dieuHuong.accept(MaManHinh.LAP_HOA_DON);
                HopThoai.hienThiThanhCong(getOwner(), "Đã treo đơn đổi. Mở lại tại Đơn đang treo để tiếp tục.");
            });
        } catch (Exception e) { baoLoi(e); }
    }

    private record DuLieuNhap(List<Object[]> hoaDon, List<Object[]> dong, List<Object[]> hang, int chonHoaDon) {}

    private void khoiPhuc(DonDoiTreo nhap) {
        timHoaDon.setText(nhap.timHoaDon());
        timHang.setText(nhap.timHang());
        lyDo.setText(nhap.lyDo());
        soLuong.setValue(nhap.soLuong());
        tem.setSelected(nhap.nguyenTem());
        chuaDung.setSelected(nhap.chuaDung());
        chayNen(() -> {
            var hd = DoiTraService.timHoaDon(nhap.timHoaDon());
            List<Object[]> dong = List.of();
            int chon = -1;
            for (int i = 0; i < hd.size(); i++) {
                if (nhap.dongGoc() == 0) {
                    if (hd.get(i)[1].toString().equals(nhap.timHoaDon())) {
                        dong = DoiTraService.layDongHoaDon(so(hd.get(i)[0])); chon = i;
                    }
                    continue;
                }
                var ds = DoiTraService.layDongHoaDon(so(hd.get(i)[0]));
                if (ds.stream().anyMatch(d -> so(d[0]) == nhap.dongGoc())) {
                    dong = ds; chon = i; break;
                }
            }
            var hang = DoiTraService.timPhuKien(nhap.maThayThe().isBlank() ? nhap.timHang() : nhap.maThayThe());
            return new DuLieuNhap(hd, dong, hang, chon);
        }, dl -> {
            dangKhoiPhuc = true;
            try {
                hoaDons = dl.hoaDon(); dongs = dl.dong(); hangs = dl.hang();
                hoaDonModel.setRowCount(0); dongModel.setRowCount(0); hangModel.setRowCount(0);
                for (Object[] h : hoaDons)
                    hoaDonModel.addRow(new Object[] {h[1], h[2], h[3], tien(h[4])});
                if (dl.chonHoaDon() >= 0)
                    hoaDonBang.setRowSelectionInterval(dl.chonHoaDon(), dl.chonHoaDon());
                for (int i = 0; i < dongs.size(); i++) {
                    Object[] d = dongs.get(i);
                    dongModel.addRow(new Object[] {d[1], d[2], d[3], d[4], so(d[3])-so(d[4]), tien(d[5])});
                    if (so(d[0]) == nhap.dongGoc()) dongBang.setRowSelectionInterval(i, i);
                }
                for (int i = 0; i < hangs.size(); i++) {
                    Object[] h = hangs.get(i);
                    hangModel.addRow(new Object[] {h[0], h[1], tien(h[2]), h[3]});
                    if (h[0].toString().equals(nhap.maThayThe())) hangBang.setRowSelectionInterval(i, i);
                }
                tem.setSelected(nhap.nguyenTem()); chuaDung.setSelected(nhap.chuaDung());
                trangThai.setText("Đã mở đơn đổi treo. Chọn hàng và kiểm tra lại chênh lệch trước khi thanh toán.");
                voHieuGia();
            } finally { dangKhoiPhuc = false; }
        });
    }

    private void timHoaDon() {
        if (dangChay) return;
        voHieuGia();
        hoaDons = List.of(); dongs = List.of();
        hoaDonModel.setRowCount(0); dongModel.setRowCount(0);
        String tuKhoa = timHoaDon.getText();
        chayNen(() -> DoiTraService.timHoaDon(tuKhoa), ds -> {
            hoaDons = ds;
            for (Object[] h : ds) hoaDonModel.addRow(new Object[]{h[1], h[2], h[3], tien(h[4])});
            trangThai.setText(ds.isEmpty() ? "Không tìm thấy hóa đơn đã thanh toán." : "Chọn hóa đơn để xem hàng đã mua.");
            if (!ds.isEmpty()) hoaDonBang.setRowSelectionInterval(0, 0);
        });
    }

    private void chonHoaDon() {
        voHieuGia();
        dongs = List.of(); dongModel.setRowCount(0);
        int i = hoaDonBang.getSelectedRow();
        if (i < 0) return;
        long id = so(hoaDons.get(i)[0]);
        chayNen(() -> DoiTraService.layDongHoaDon(id), ds -> {
            dongs = ds;
            for (Object[] d : ds) dongModel.addRow(new Object[]{d[1],d[2],d[3],d[4],so(d[3])-so(d[4]),tien(d[5])});
            trangThai.setText("Chọn hàng và xác nhận tình trạng thực tế. Hạn đổi: 72 giờ từ ngày lập hóa đơn.");
        });
    }

    private void timPhuKien() {
        if (dangChay) return;
        voHieuGia(); hangs = List.of(); hangModel.setRowCount(0);
        String tuKhoa = timHang.getText();
        chayNen(() -> DoiTraService.timPhuKien(tuKhoa), ds -> {
            hangs = ds;
            for (Object[] h : ds) hangModel.addRow(new Object[]{h[0],h[1],tien(h[2]),h[3]});
            if (ds.isEmpty()) trangThai.setText("Không tìm thấy phụ kiện đang kinh doanh.");
        });
    }

    private void tinhGia() {
        if (dangChay) return;
        try {
            soLuong.commitEdit();
            Object[] cu = dongChon(); Object[] moi = hangChon();
            int sl = (Integer) soLuong.getValue();
            kiemTraTinhTrang();
            if (sl > so(cu[3])-so(cu[4])) throw new IllegalArgumentException("Số lượng vượt phần chưa đổi.");
            if (sl > so(moi[3])) throw new IllegalArgumentException("Phụ kiện thay thế không đủ tồn.");
            long id = so(cu[0]), giaMoi = so(moi[2]);
            long luot = ++phien;
            chayNen(() -> DoiTraService.layGiaTriDuocDoi(id, sl), cuTien -> {
                if (luot != phien) return;
                long moiTien = Math.multiplyExact(giaMoi, sl);
                if (moiTien < cuTien) throw new IllegalArgumentException("Hàng thay thế phải bằng hoặc cao giá hơn hàng cũ.");
                giaTriCu = cuTien;
                long chenhlech = moiTien-cuTien;
                tongKet.setText("Hàng cũ: " + tien(cuTien) + "    Hàng mới: " + tien(moiTien)
                    + "    Cần thu: " + tien(chenhlech) + "    Điểm thêm: " + chenhlech/10000);
                trangThai.setText("Đủ điều kiện. Hệ thống sẽ kiểm tra lại khi thanh toán.");
                tiepTuc.setEnabled(true);
            });
        } catch (Exception e) { baoLoi(e); }
    }

    private void chuyenThanhToan() {
        if (dangChay || giaTriCu < 0) return;
        try {
            kiemTraTinhTrang();
            if (lyDo.getText().isBlank()) throw new IllegalArgumentException("Vui lòng nhập lý do đổi.");
            long dong = so(dongChon()[0]);
            String ma = hangChon()[0].toString();
            int sl = (Integer) soLuong.getValue();
            String xacNhan = " | Đã kiểm tra: nguyên tem mác, bao bì; chưa sử dụng, không rách bẩn.";
            String ghiChu = lyDo.getText().trim().endsWith(xacNhan) ? lyDo.getText().trim()
                : lyDo.getText().trim() + xacNhan;
            chayNen(() -> {
                DonDoiTreoService.chuyenThanhToan(idDonTreo, gio, dong, sl, ma, ghiChu);
                return true;
            }, ok -> { dispose(); dieuHuong.accept(MaManHinh.THANH_TOAN); });
        } catch (Exception e) { baoLoi(e); }
    }

    private void kiemTraTinhTrang() {
        if (!tem.isSelected() || !chuaDung.isSelected())
            throw new IllegalArgumentException("Chỉ nhận hàng nguyên tem mác, bao bì, chưa sử dụng và không rách bẩn.");
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
        phien++; giaTriCu = -1; tiepTuc.setEnabled(false);
        tongKet.setText("Chọn hàng cần đổi và phụ kiện thay thế, sau đó kiểm tra chênh lệch.");
    }
    private void huy() {
        if (dangChay) return;
        if (!timHoaDon.getText().isBlank() || !dongs.isEmpty() || !lyDo.getText().isBlank()) {
            String[] luaChon = {"Treo đơn đổi", "Bỏ thay đổi", "Tiếp tục nhập"};
            int chon = JOptionPane.showOptionDialog(this,
                idDonTreo > 0 ? "Lưu thay đổi vào đơn treo trước khi đóng? Bản đã lưu vẫn được giữ."
                    : "Bạn muốn treo đơn để tiếp tục sau?",
                "Đóng đơn đổi", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, luaChon, luaChon[0]);
            if (chon == 0) { treoDon(); return; }
            if (chon != 1) return;
        }
        dispose();
    }
    private <T> void chayNen(Callable<T> viec, Consumer<T> xong) {
        if (dangChay) return;
        dangChay = true; tiepTuc.setEnabled(false);

        khoa(getContentPane(), false);
        var phienDangNhap = com.petstation.service.PhienDangNhapService.layPhienHienTai();
        new SwingWorker<T,Void>() {
            protected T doInBackground() throws Exception {
                return com.petstation.service.PhienDangNhapService.trongPhien(phienDangNhap, viec);
            }
            protected void done() {
                dangChay = false; khoa(getContentPane(), true); tiepTuc.setEnabled(false);
                try { xong.accept(get()); } catch (Exception e) { baoLoi(e); }
            }
        }.execute();
    }
    private static void khoa(Container c, boolean mo) {
        for (Component v : c.getComponents()) {
            v.setEnabled(mo);
            if (v instanceof Container con) khoa(con, mo);
        }
    }
    private void baoLoi(Exception e) {
        Throwable loi = e;
        while (loi.getCause() != null) loi = loi.getCause();
        trangThai.setText(loi.getMessage());
        JOptionPane.showMessageDialog(this, loi.getMessage(), "Đổi phụ kiện", JOptionPane.WARNING_MESSAGE);
    }
    private static long so(Object v) { return ((Number)v).longValue(); }
    private static String tien(Object v) { return TienTe.dinhDang(so(v)); }
    private static DefaultTableModel model(String... cot) {
        return new DefaultTableModel(cot,0) {
            public boolean isCellEditable(int r,int c) { return false; }
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
            int rong = ten.equals("Sản phẩm") || ten.equals("Tên phụ kiện") ? 300
                : ten.equals("Đã mua") || ten.equals("Đã đổi") || ten.equals("Còn đổi") ? 65 : 150;
            t.getColumnModel().getColumn(i).setPreferredWidth(rong);
        }
        return t;
    }
    private static JScrollPane cuon(JTable bang, int cao) {
        JScrollPane s = new JScrollPane(bang); s.setPreferredSize(new Dimension(760,cao));
        s.setMinimumSize(new Dimension(0, cao));
        s.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        s.getViewport().setBackground(MauUngDung.THE_NEN);
        s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); return s;
    }
    private static JPanel hang(Component... cac) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); p.setOpaque(false);
        for (Component c : cac) p.add(c); return p;
    }
    private static JPanel nhom(String ten, Component... cac) {
        JPanel p = new BoGocPanel(14, MauUngDung.THE_NEN);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(12,14,12,14));
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
        p.setAlignmentX(Component.LEFT_ALIGNMENT); return p;
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
        JButton b = new NutDinhDang(ten,NutDinhDang.KieuNut.PHU);
        b.addActionListener(e -> viec.run()); return b;
    }
}
