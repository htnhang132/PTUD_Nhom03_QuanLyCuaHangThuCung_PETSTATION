package com.petstation.gui.luutru;

import static com.petstation.gui.chung.TruongNghiepVu.docSoTien;
import static com.petstation.gui.chung.TruongNghiepVu.layTruongVanBan;

import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.luutru.LuuTruService;
import com.petstation.util.DinhDang;
import com.petstation.util.TaoMaVietQR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public abstract class NghiepVuHotelPanel extends BangNghiepVuPanel {
    // 1. THUỘC TÍNH & HÀM KHỞI TẠO

    protected final LuuTruService luuTruService = new LuuTruService();
    private final BanHangService banHangService = new BanHangService();

    protected NghiepVuHotelPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    // 2. THAO TÁC PHÒNG, ĐẶT PHÒNG & HỦY ĐẶT

    protected void lichSuCoc() {
        lichSuCocChoHopDong(layDongChon());
    }

    public void lichSuCocChoHopDong(String ma) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layCoc(ma),
                dsCoc -> {
                    List<Object[]> dong = new ArrayList<>();
                    for (HotelDTO.DongLichSuCoc c : dsCoc) {
                        dong.add(
                                new Object[] {
                                    c.getPhuongThuc(),
                                    c.getSoTien(),
                                    c.getKhachDua(),
                                    c.getTienThua(),
                                    c.getTaoLuc(),
                                    c.getCa()
                                });
                    }
                    hienThiChiTiet(
                            "Lịch sử thu cọc",
                            new String[] {
                                "Phương thức",
                                "Thực thu",
                                "Khách đưa",
                                "Tiền thừa",
                                "Lúc nhận",
                                "Ca"
                            },
                            dong);
                });
    }

    public void moDonPhong(String maPhong) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                luuTruService::layPhongDangDon,
                dsPhong -> hienThiHopThoaiDonPhong(maPhong, dsPhong));
    }

    public void moDatPhongTheoPhong(String maPhong) {
        if (maPhong != null && !maPhong.isBlank()) {
            layBanHang().getQuyTrinh().chonPhongHotel(maPhong);
        }
        datPhong();
    }

    private void hienThiHopThoaiDonPhong(String maPhong, List<PhongHotel> dsPhong) {
        if (dsPhong.isEmpty()) {
            HopThoai.hienThiThongTin(
                    this, "Hiện không có phòng nào đang ở trạng thái chờ dọn vệ sinh.");
            return;
        }
        List<Object[]> cacPhongDangDon = new ArrayList<>();
        for (PhongHotel p : dsPhong) {
            cacPhongDangDon.add(new Object[] {p.getMaPhong(), p.getMaPhong()});
        }
        var field =
                BieuMauTruong.TruongDuLieu.layLuaChon("room", "Phòng đang dọn", cacPhongDangDon);
        if (maPhong != null && !maPhong.isBlank())
            field =
                    new BieuMauTruong.TruongDuLieu(
                            field.khoa(), field.nhan(), maPhong, field.cacLuaChon());
        var values = BieuMauTruong.layYeuCauNhap(this, "Hoàn thành dọn phòng", field);
        if (values == null) return;
        String phongChon = values.get("room");
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.hoanTatDonPhong(phongChon),
                () -> {
                    taiLaiNen();
                    HopThoai.hienThiThanhCong(
                            this,
                            "Đã xác nhận dọn phòng xong, phòng đã chuyển sang trạng thái Trống.");
                });
    }

    protected long cocConThieu() {
        if (layBangDuLieu().getSelectedRow() < 0 || layMoHinh().getColumnCount() < 11)
            return Long.MAX_VALUE;
        int row = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        return ((Number) layMoHinh().getValueAt(row, 10)).longValue()
                - ((Number) layMoHinh().getValueAt(row, 7)).longValue();
    }

    protected long cocDaThu() {
        if (layBangDuLieu().getSelectedRow() < 0 || layMoHinh().getColumnCount() < 8)
            return Long.MAX_VALUE;
        int row = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        Object value = layMoHinh().getValueAt(row, 7);
        return value instanceof Number n ? n.longValue() : 0;
    }

    protected boolean datPhong() {
        String maPhong = layBanHang().getQuyTrinh().getMaPhongHotel();
        String maHopDong = DatPhongHotelDialog.mo(this, null, maPhong);
        if (maHopDong != null) {
            layBanHang().getQuyTrinh().chonHotel(maHopDong);
            return true;
        }
        return false;
    }

    protected void suaDatPhong() {
        String maHopDong = layDongChon();
        String result = DatPhongHotelDialog.mo(this, maHopDong, null);
        if (result != null) layBanHang().getQuyTrinh().chonHotel(result);
    }

    protected void huyDatPhong() {
        huyDatPhongChoHopDong(layDongChon(), this::taiLaiNen);
    }

    public void huyDatPhongChoHopDong(String maHopDong, Runnable sauKhiHuy) {
        String reason =
                HopThoai.nhapChuoiThuan(
                        this,
                        "Nhập lý do hủy hợp đồng đặt phòng #"
                                + maHopDong
                                + ":\n(Chỉ có thể hủy trực tiếp khi hợp đồng chưa thu cọc.)",
                        "Hủy đặt phòng #" + maHopDong);
        if (reason == null) return;
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.huyDatPhong(maHopDong, reason),
                () -> {
                    layBanHang().getQuyTrinh().chonHotel("");
                    if (sauKhiHuy != null) sauKhiHuy.run();
                });
    }

    // 3. THU CỌC & ĐỐI SOÁT THANH TOÁN CỌC

    protected void thuCoc() {
        thuCocChoHopDong(layDongChon(), layNoiDungDongChon(1), null);
    }

    public void thuCocChoHopDong(String ma, String maHopDongHienThi, Runnable sauKhiXong) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> CaThuNganService.caDangMo() ? luuTruService.layMucCoc(ma) : null,
                muc -> {
                    if (muc == null) {
                        hienThiCanhBaoMoCa(
                                "Ca thu ngân hiện chưa mở. Cần mở ca để ghi nhận phiếu thu tiền"
                                        + " cọc.");
                        return;
                    }
                    nhapVaThuCoc(ma, maHopDongHienThi, muc, sauKhiXong);
                });
    }

    private void hienThiCanhBaoMoCa(String thongDiep) {
        if (HopThoai.xacNhanDongY(
                this,
                thongDiep
                        + "\n"
                        + "Bạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay bây"
                        + " giờ không?",
                "Chưa mở ca làm việc")) {
            layDieuHuong().accept(MaManHinh.CA_THU_NGAN);
        }
    }

    private void nhapVaThuCoc(String ma, String maHopDong, long[] muc, Runnable sauKhiXong) {
        long thieu = muc[0] - muc[1];
        if (thieu <= 0) {
            HopThoai.hienThiThongTin(this, "Hợp đồng đã thu đủ tiền cọc yêu cầu.");
            return;
        }
        String khoa = "COC" + UUID.randomUUID().toString().replace("-", "").substring(0, 22);
        JDialog hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Thu tiền cọc Hotel — " + maHopDong,
                        Dialog.ModalityType.APPLICATION_MODAL);
        JTextField oSoTien = new JTextField(String.valueOf(thieu));
        JTextField oKhachDua = new JTextField(String.valueOf(thieu));
        JComboBox<PhuongThuc> cboPhuongThuc =
                new JComboBox<>(
                        new PhuongThuc[] {
                            PhuongThuc.TIEN_MAT, PhuongThuc.CHUYEN_KHOAN, PhuongThuc.THE_NGAN_HANG
                        });
        Dimension kichThuocNhap = new Dimension(180, MauUngDung.CAO_DAU_VAO);
        oSoTien.setPreferredSize(kichThuocNhap);
        oKhachDua.setPreferredSize(kichThuocNhap);
        cboPhuongThuc.setPreferredSize(kichThuocNhap);
        JLabel nhanTienThua = new JLabel("0 ₫");
        JLabel nhanQr = new JLabel("", SwingConstants.CENTER);
        JCheckBox chkDaNhan = new JCheckBox();
        JTextArea oHuongDan = new JTextArea(4, 30);
        boolean[] qrHopLe = {false};
        String[] loiQr = {""};
        int[] luotQr = {0};
        JPanel noiDung =
                taoNoiDungThuCoc(
                        muc,
                        thieu,
                        oSoTien,
                        oKhachDua,
                        cboPhuongThuc,
                        nhanTienThua,
                        nhanQr,
                        chkDaNhan,
                        oHuongDan);
        Runnable capNhat =
                () ->
                        capNhatCheDoThuCoc(
                                hopThoai,
                                maHopDong,
                                thieu,
                                oSoTien,
                                oKhachDua,
                                cboPhuongThuc,
                                nhanTienThua,
                                nhanQr,
                                chkDaNhan,
                                oHuongDan,
                                qrHopLe,
                                loiQr,
                                luotQr);
        ganSuKienVaMoThuCoc(
                hopThoai,
                ma,
                thieu,
                khoa,
                oSoTien,
                oKhachDua,
                cboPhuongThuc,
                nhanTienThua,
                chkDaNhan,
                qrHopLe,
                loiQr,
                noiDung,
                capNhat,
                sauKhiXong);
    }

    private void ganSuKienVaMoThuCoc(
            JDialog hopThoai,
            String ma,
            long thieu,
            String khoa,
            JTextField oSoTien,
            JTextField oKhachDua,
            JComboBox<PhuongThuc> cboPhuongThuc,
            JLabel nhanTienThua,
            JCheckBox chkDaNhan,
            boolean[] qrHopLe,
            String[] loiQr,
            JPanel noiDung,
            Runnable capNhat,
            Runnable sauKhiXong) {
        cboPhuongThuc.addActionListener(e -> capNhat.run());
        themBoLangNgheTaiLieu(
                oSoTien,
                () -> {
                    if (cboPhuongThuc.getSelectedItem() == PhuongThuc.TIEN_MAT) {
                        oKhachDua.setText(oSoTien.getText());
                    }
                    capNhat.run();
                });
        themBoLangNgheTaiLieu(
                oKhachDua, () -> capNhatTienThuaThuCoc(oSoTien, oKhachDua, nhanTienThua, thieu));
        NutDinhDang btnXacNhan = new NutDinhDang("Xác nhận thu cọc", NutDinhDang.KieuNut.CHINH);
        NutDinhDang btnHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);
        btnHuy.addActionListener(e -> hopThoai.dispose());
        btnXacNhan.addActionListener(
                e ->
                        thucHienThuCocMotBuoc(
                                hopThoai,
                                btnXacNhan,
                                ma,
                                thieu,
                                khoa,
                                oSoTien,
                                oKhachDua,
                                cboPhuongThuc,
                                chkDaNhan,
                                qrHopLe[0],
                                loiQr[0],
                                sauKhiXong));
        JPanel chan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        chan.setOpaque(false);
        chan.add(btnHuy);
        chan.add(btnXacNhan);
        JPanel khung = new JPanel(new BorderLayout(0, 12));
        khung.setBackground(Color.WHITE);
        khung.setBorder(new EmptyBorder(16, 18, 16, 18));
        khung.add(new JScrollPane(noiDung), BorderLayout.CENTER);
        khung.add(chan, BorderLayout.SOUTH);
        hopThoai.setContentPane(khung);
        hopThoai.getRootPane().setDefaultButton(btnXacNhan);
        hopThoai.getRootPane()
                .registerKeyboardAction(
                        e -> hopThoai.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        capNhat.run();
        HopThoai.canChinhKichThuocVuaManHinh(hopThoai, 480, 320);
        HopThoai.chuanHoaCuaSo(hopThoai, this);
        hopThoai.setVisible(true);
    }

    private JPanel taoNoiDungThuCoc(
            long[] muc,
            long thieu,
            JTextField oSoTien,
            JTextField oKhachDua,
            JComboBox<PhuongThuc> cboPhuongThuc,
            JLabel nhanTienThua,
            JLabel nhanQr,
            JCheckBox chkDaNhan,
            JTextArea oHuongDan) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel tomTat =
                new JLabel(
                        "<html>Yêu cầu: <b>"
                                + DinhDang.dinhDang(muc[0])
                                + "</b> · Đã thu: <b>"
                                + DinhDang.dinhDang(muc[1])
                                + "</b> · Còn thiếu: <b style='color:#991B1B'>"
                                + DinhDang.dinhDang(thieu)
                                + "</b></html>");
        p.add(tomTat);
        p.add(Box.createVerticalStrut(10));
        JPanel luoi = new JPanel(new GridLayout(4, 2, 10, 8));
        luoi.setOpaque(false);
        luoi.add(taoNhanTruong("Số tiền thu lần này", true));
        luoi.add(oSoTien);
        luoi.add(taoNhanTruong("Phương thức thu", true));
        luoi.add(cboPhuongThuc);
        luoi.add(taoNhanTruong("Tiền khách đưa:", false));
        luoi.add(oKhachDua);
        luoi.add(taoNhanTruong("Tiền thừa trả khách:", false));
        nhanTienThua.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        luoi.add(nhanTienThua);
        p.add(luoi);
        p.add(Box.createVerticalStrut(10));
        nhanQr.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkDaNhan.setOpaque(false);
        oHuongDan.setEditable(false);
        oHuongDan.setLineWrap(true);
        oHuongDan.setWrapStyleWord(true);
        oHuongDan.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        p.add(nhanQr);
        p.add(chkDaNhan);
        p.add(Box.createVerticalStrut(6));
        p.add(oHuongDan);
        return p;
    }

    private void capNhatCheDoThuCoc(
            JDialog hopThoai,
            String maHopDong,
            long thieu,
            JTextField oSoTien,
            JTextField oKhachDua,
            JComboBox<PhuongThuc> cboPhuongThuc,
            JLabel nhanTienThua,
            JLabel nhanQr,
            JCheckBox chkDaNhan,
            JTextArea oHuongDan,
            boolean[] qrHopLe,
            String[] loiQr,
            int[] luotQr) {
        PhuongThuc pt = (PhuongThuc) cboPhuongThuc.getSelectedItem();
        boolean laTienMat = pt == PhuongThuc.TIEN_MAT;
        oKhachDua.setEnabled(laTienMat);
        chkDaNhan.setSelected(false);
        if (laTienMat) {
            nhanQr.setVisible(false);
            chkDaNhan.setVisible(false);
            oHuongDan.setText("Nhập số tiền thu cọc và tiền khách đưa (nếu trả tiền mặt).");
            capNhatTienThuaThuCoc(oSoTien, oKhachDua, nhanTienThua, thieu);
            capNhatKichThuocThuCoc(hopThoai);
            return;
        }
        nhanTienThua.setText("0 ₫");
        long soThu;
        try {
            soThu = phanTichSoTienCoc(oSoTien.getText(), thieu);
        } catch (RuntimeException ex) {
            qrHopLe[0] = false;
            loiQr[0] = ex.getMessage();
            nhanQr.setVisible(false);
            chkDaNhan.setVisible(false);
            oHuongDan.setText(ex.getMessage());
            capNhatKichThuocThuCoc(hopThoai);
            return;
        }
        if (pt == PhuongThuc.THE_NGAN_HANG) {
            nhanQr.setVisible(false);
            chkDaNhan.setVisible(true);
            chkDaNhan.setEnabled(true);
            chkDaNhan.setText("Đã xác nhận quẹt thẻ thành công trên máy POS");
            oHuongDan.setText("Số tiền cần quẹt thẻ POS: " + DinhDang.dinhDang(soThu));
            capNhatKichThuocThuCoc(hopThoai);
            return;
        }
        taiMaVietQrThuCoc(
                hopThoai, maHopDong, soThu, nhanQr, chkDaNhan, oHuongDan, qrHopLe, loiQr, luotQr);
        capNhatKichThuocThuCoc(hopThoai);
    }

    private void capNhatKichThuocThuCoc(JDialog hopThoai) {
        if (hopThoai.isShowing()) {
            HopThoai.canChinhKichThuocVuaManHinh(hopThoai, 480, 320);
            HopThoai.chuanHoaCuaSo(hopThoai, this);
        }
    }

    private void taiMaVietQrThuCoc(
            JDialog hopThoai,
            String maHopDong,
            long soThu,
            JLabel nhanQr,
            JCheckBox chkDaNhan,
            JTextArea oHuongDan,
            boolean[] qrHopLe,
            String[] loiQr,
            int[] luotQr) {
        int luot = ++luotQr[0];
        qrHopLe[0] = false;
        nhanQr.setVisible(true);
        nhanQr.setIcon(null);
        nhanQr.setText("Đang tạo mã VietQR...");
        chkDaNhan.setVisible(true);
        chkDaNhan.setEnabled(false);
        chkDaNhan.setText("Đã kiểm tra tài khoản và nhận đủ tiền chuyển khoản");
        TacVuNen.chayCoPhien(
                hopThoai,
                () -> luot == luotQr[0],
                () -> {
                    String bin = CaiDatService.macDinh().layTuyChon("BANK_BIN");
                    String tk = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT");
                    String ten = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT_NAME");
                    TaoMaVietQR.kiemTraTaiKhoan(bin, tk, ten);
                    String noiDung = TaoMaVietQR.taoNoiDung(bin, tk, ten, soThu, maHopDong);
                    return new Object[] {
                        new ImageIcon(TaoMaVietQR.taoAnh(noiDung, 180)),
                        "Ngân hàng BIN " + bin + " | STK: " + tk + " | " + ten
                    };
                },
                kq -> {
                    nhanQr.setIcon((ImageIcon) kq[0]);
                    nhanQr.setText("");
                    oHuongDan.setText(String.valueOf(kq[1]));
                    qrHopLe[0] = true;
                    chkDaNhan.setEnabled(true);
                },
                ex -> {
                    loiQr[0] = ex.getMessage() == null ? "Lỗi tạo QR" : ex.getMessage();
                    nhanQr.setText("Không tạo được mã VietQR");
                    oHuongDan.setText("Lý do: " + loiQr[0]);
                });
    }

    private static void capNhatTienThuaThuCoc(
            JTextField oSoTien, JTextField oKhachDua, JLabel nhanTienThua, long thieu) {
        try {
            long canThu = phanTichSoTienCoc(oSoTien.getText(), thieu);
            long khachDua = phanTichSoKhachDua(oKhachDua.getText());
            nhanTienThua.setText(DinhDang.dinhDang(Math.max(0, khachDua - canThu)));
        } catch (RuntimeException ex) {
            nhanTienThua.setText("—");
        }
    }

    private void thucHienThuCocMotBuoc(
            JDialog hopThoai,
            NutDinhDang btnXacNhan,
            String ma,
            long thieu,
            String khoa,
            JTextField oSoTien,
            JTextField oKhachDua,
            JComboBox<PhuongThuc> cboPhuongThuc,
            JCheckBox chkDaNhan,
            boolean qrHopLe,
            String loiQr,
            Runnable sauKhiXong) {
        try {
            long so = phanTichSoTienCoc(oSoTien.getText(), thieu);
            PhuongThuc pt = (PhuongThuc) cboPhuongThuc.getSelectedItem();
            long khachDua =
                    pt == PhuongThuc.TIEN_MAT ? phanTichSoKhachDua(oKhachDua.getText()) : so;
            if (pt == PhuongThuc.TIEN_MAT && khachDua < so) {
                throw new IllegalArgumentException("Tiền khách đưa phải đủ số tiền thu cọc.");
            }
            if (pt == PhuongThuc.CHUYEN_KHOAN && !qrHopLe) {
                throw new IllegalArgumentException("Chưa tạo được mã QR hợp lệ. " + loiQr);
            }
            if (pt != PhuongThuc.TIEN_MAT && !chkDaNhan.isSelected()) {
                throw new IllegalArgumentException("Vui lòng xác nhận đã nhận tiền trước khi lưu.");
            }
            btnXacNhan.setEnabled(false);
            TacVuNen.chayCoPhien(
                    this,
                    () -> true,
                    () -> luuTruService.thuCoc(ma, pt, so, khachDua, khoa),
                    () -> {
                        btnXacNhan.setEnabled(true);
                        hopThoai.dispose();
                        taiLaiNen();
                        if (sauKhiXong != null) {
                            sauKhiXong.run();
                        }
                    },
                    ex -> {
                        btnXacNhan.setEnabled(true);
                        if (ex.getMessage() != null && ex.getMessage().contains("Cần mở ca")) {
                            hienThiCanhBaoMoCa(ex.getMessage());
                        } else {
                            HopThoai.hienThiCanhBao(hopThoai, ex.getMessage());
                        }
                    });
        } catch (RuntimeException ex) {
            btnXacNhan.setEnabled(true);
            HopThoai.hienThiCanhBao(hopThoai, ex.getMessage());
        }
    }

    private static long phanTichSoKhachDua(String raw) {
        String daDon = raw == null ? "" : raw.trim().replaceAll("[.,\\s₫đ]", "");
        if (daDon.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền khách đưa.");
        }
        if (!daDon.matches("\\d+")) {
            throw new IllegalArgumentException("Tiền khách đưa phải là số nguyên hợp lệ.");
        }
        try {
            return Long.parseLong(daDon);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Tiền khách đưa vượt giới hạn hợp lệ.");
        }
    }

    private static long phanTichSoTienCoc(String input, long thieu) {
        String daDon = input == null ? "" : input.trim().replaceAll("[.,\\s₫đ]", "");
        if (!daDon.matches("\\d+")) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ (chỉ gồm chữ số).");
        }
        long so;
        try {
            so = Long.parseLong(daDon);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền thu cọc vượt giới hạn hợp lệ.");
        }
        if (so <= 0 || so > thieu) {
            throw new IllegalArgumentException(
                    "Số thu vượt phần cọc còn thiếu (" + DinhDang.dinhDang(thieu) + ").");
        }
        return so;
    }

    // 4. ĐỔI CỌC, NHẬT KÝ & QUYẾT TOÁN HỢP ĐỒNG

    protected void doiCoc() {
        doiCocChoHopDong(layDongChon(), this::taiLaiNen);
    }

    public void doiCocChoHopDong(String ma, Runnable sauKhiDoi) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layMucCoc(ma)[0],
                mucHienTai -> {
                    Map<String, String> v =
                            BieuMauTruong.layYeuCauNhap(
                                    this,
                                    "Điều chỉnh cọc",
                                    layTruongVanBan(
                                            "muc",
                                            "Mức yêu cầu mới (đồng)",
                                            String.valueOf(mucHienTai)),
                                    layTruongVanBan("lydo", "Lý do", ""));
                    if (v == null) return;
                    long mucMoi = docSoTien(v, "muc");
                    String lyDo = v.get("lydo");
                    TacVuNen.chayCoPhien(
                            this,
                            () -> true,
                            () -> luuTruService.dieuChinhCoc(ma, mucMoi, lyDo),
                            () -> {
                                if (sauKhiDoi != null) sauKhiDoi.run();
                            });
                });
    }

    protected void layNhatKyChiTiet() {
        layNhatKyChiTietChoHopDong(layDongChon());
    }

    public void layNhatKyChiTietChoHopDong(String ma) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layNhatKyChiTiet(ma),
                dsNhatKy -> {
                    List<Object[]> dong = new ArrayList<>();
                    for (HotelDTO.DongNhatKyChiTiet n : dsNhatKy) {
                        dong.add(
                                new Object[] {
                                    n.getThoiGian(),
                                    n.getNhanVien() != null ? n.getNhanVien().getHoTen() : "",
                                    n.getAnUong(),
                                    n.getDaThucHien(),
                                    n.getTinhTrang()
                                });
                    }
                    hienThiChiTiet(
                            "Nhật ký lưu trú",
                            new String[] {
                                "Thời gian", "Nhân viên", "Ăn uống", "Đã thực hiện", "Tình trạng"
                            },
                            dong);
                });
    }

    protected void quyetToan(String loai) {
        quyetToanChoHopDong(loai, layDongChon());
    }

    public void quyetToanChoHopDong(String loai, String ma) {
        if (!layBanHang().kiemTraTrong()) {
            if (HopThoai.xacNhanDongY(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\n"
                            + "Bạn có muốn làm trống giỏ hàng để chuyển sang quyết toán lưu trú"
                            + " không?",
                    "Giỏ hàng đang có sản phẩm")) {
                layBanHang().xoa();
            } else {
                return;
            }
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> banHangService.quyetToan(loai, ma, layBanHang()),
                () -> layDieuHuong().accept(MaManHinh.THANH_TOAN));
    }
}
