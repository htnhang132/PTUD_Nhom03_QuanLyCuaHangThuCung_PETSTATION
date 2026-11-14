package com.petstation.gui.khachhang;

import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

// Màn hình con (Tab 1) trong Danh mục > Điểm thưởng và voucher
public final class QuyTacTichDiemPanel extends CoSoPanel {

    // 1. KHAI BÁO CÁC TRƯỜNG NHẬP LIỆU & DANH SÁCH HẠNG THÀNH VIÊN

    private final JTextField oTichDiem = taoTruong();
    private final JTextField oDoiDiem = taoTruong();
    private final JTextField[] cacHang = {taoTruong(), taoTruong(), taoTruong(), taoTruong()};
    private static final String[] HANG_CAC_TEN = {"Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
    private final JLabel nhanThongBao = new JLabel(" ");
    private final boolean coQuyenSua =
            PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_TAI_KHOAN);
    private long soTienMoiDiemDoi = 1000L;
    private int luotTai;

    // 2. KHỞI TẠO BỐ CỤC BIỂU MẪU CẤU HÌNH QUY TẮC TÍCH ĐIỂM & HẠNG

    public QuyTacTichDiemPanel() {
        setLayout(new BorderLayout(0, 14));
        add(taoTieuDeManHinh("Quy tắc tích điểm và hạng thành viên"), BorderLayout.NORTH);
        BoGocPanel khungThe = new BoGocPanel(12, Color.WHITE, true);
        khungThe.setLayout(new GridBagLayout());
        khungThe.setBorder(new EmptyBorder(18, 22, 18, 22));
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(6, 0, 6, 12);
        giaTriDangXet.anchor = GridBagConstraints.WEST;
        int toaDoDoc = 0;
        layDong(
                khungThe,
                giaTriDangXet,
                toaDoDoc++,
                "Chi tiêu bao nhiêu đồng được 1 điểm",
                oTichDiem,
                "₫");
        layDong(
                khungThe,
                giaTriDangXet,
                toaDoDoc++,
                "200 điểm đổi voucher Spa (hạn 60 ngày)",
                oDoiDiem,
                "₫");
        JLabel nhanDau = new JLabel("Hạng thành viên (theo tổng chi tiêu từ):");
        nhanDau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.gridwidth = 3;
        giaTriDangXet.insets = new Insets(16, 0, 4, 0);
        khungThe.add(nhanDau, giaTriDangXet);
        giaTriDangXet.gridwidth = 1;
        giaTriDangXet.insets = new Insets(6, 0, 6, 12);
        for (int chiSo = 0; chiSo < 4; chiSo++)
            layDong(
                    khungThe,
                    giaTriDangXet,
                    toaDoDoc++,
                    "Hạng " + HANG_CAC_TEN[chiSo],
                    cacHang[chiSo],
                    "₫");
        nhanThongBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.gridwidth = 3;
        khungThe.add(nhanThongBao, giaTriDangXet);
        NutDinhDang btnLuu = new NutDinhDang("Lưu quy tắc", NutDinhDang.KieuNut.THANH_CONG);
        btnLuu.setEnabled(coQuyenSua);
        btnLuu.addActionListener(suKien -> xuLyLuu());
        oTichDiem.addActionListener(suKien -> xuLyLuu());
        for (JTextField oHang : cacHang) {
            oHang.setEditable(coQuyenSua);
            oHang.addActionListener(suKien -> xuLyLuu());
        }
        giaTriDangXet.gridy = toaDoDoc;
        giaTriDangXet.fill = GridBagConstraints.NONE;
        khungThe.add(btnLuu, giaTriDangXet);
        JPanel khungVungChua = taoVungTrongSuot(new BorderLayout());
        khungVungChua.add(khungThe, BorderLayout.NORTH);
        JScrollPane scrCuon = new JScrollPane(khungVungChua);
        scrCuon.setBorder(null);
        scrCuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        scrCuon.getVerticalScrollBar().setUnitIncrement(16);
        add(scrCuon, BorderLayout.CENTER);
        nap();
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, coQuyenSua, false, false, coQuyenSua, true);
    }

    @Override
    public void xuLyLuu() {
        if (!coQuyenSua) return;
        try {
            luu();
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    @Override
    public void xuLyHoanTac() {
        nap();
    }

    @Override
    public void xuLyLamMoi() {
        nap();
    }

    private static JTextField taoTruong() {
        JTextField oGiaTriTam = new JTextField(14);
        oGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oGiaTriTam.setPreferredSize(new Dimension(200, 34));
        oGiaTriTam.setHorizontalAlignment(JTextField.RIGHT);
        return oGiaTriTam;
    }

    private void layDong(
            JPanel khungThe,
            GridBagConstraints giaTriDangXet,
            int toaDoDoc,
            String nhan,
            JTextField oTruongDangXet,
            String donVi) {
        giaTriDangXet.gridy = toaDoDoc;
        giaTriDangXet.gridx = 0;
        JLabel nhanDong = new JLabel(nhan);
        nhanDong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanDong.setForeground(MauUngDung.NAU);
        khungThe.add(nhanDong, giaTriDangXet);
        giaTriDangXet.gridx = 1;
        khungThe.add(oTruongDangXet, giaTriDangXet);
        giaTriDangXet.gridx = 2;
        JLabel nhanDonVi = new JLabel(donVi);
        nhanDonVi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanDonVi.setForeground(MauUngDung.VAN_BAN_XAM);
        khungThe.add(nhanDonVi, giaTriDangXet);
    }

    // 3. NẠP CẤU HÌNH HIỆN TẠI & LƯU NGƯỠNG HẠNG THÀNH VIÊN VÀO CSDL

    private void nap() {
        oDoiDiem.setText("150.000");
        oTichDiem.setEditable(coQuyenSua);
        oDoiDiem.setEditable(false);
        oDoiDiem.setToolTipText("Quy đổi voucher Spa cố định: 200 điểm = 150.000₫.");
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> {
                    long moiDiem = CaiDatService.macDinh().laySoTienChoMoiDiemTichLuy();
                    long doiDiem = docSoTienDoiDiemHienTai();
                    long[] hang = CaiDatService.macDinh().layHangChiTieu();
                    return new long[] {moiDiem, doiDiem, hang[0], hang[1], hang[2], hang[3]};
                },
                ketQua -> {
                    soTienMoiDiemDoi = ketQua[1] > 0 ? ketQua[1] : 1000L;
                    oTichDiem.setText(DinhDang.dinhDang(ketQua[0]));
                    for (int chiSo = 0; chiSo < 4; chiSo++) {
                        cacHang[chiSo].setText(DinhDang.dinhDang(ketQua[chiSo + 2]));
                    }
                });
    }

    private static long docSoTienDoiDiemHienTai() {
        String giaTri =
                CaiDatService.macDinh().layTuyChonHoacMacDinh("SO_TIEN_MOI_DIEM_DOI", "1000");
        long so = DinhDang.doc(giaTri);
        return so > 0 ? so : 1000L;
    }

    private static long docTienTeHopLe(String vanBan) {
        String raw = vanBan == null ? "" : vanBan.trim();
        if (raw.isEmpty()) return -1;
        String khongDonVi = raw.replaceAll("(?i)[₫đ\\s]+$", "").trim();
        if (!khongDonVi.matches("\\d+(?:[.,]\\d{3})*")) return -1;
        return DinhDang.doc(khongDonVi);
    }

    private void luu() {
        long tichDiemMoi = docTienTeHopLe(oTichDiem.getText());
        long[] cacMucHang = new long[4];
        for (int chiSo = 0; chiSo < 4; chiSo++) {
            cacMucHang[chiSo] = docTienTeHopLe(cacHang[chiSo].getText());
        }
        if (tichDiemMoi <= 0) {
            HopThoai.hienThiCanhBao(this, "Số đồng cho 1 điểm phải là số nguyên dương.");
            oTichDiem.requestFocusInWindow();
            return;
        }
        for (int chiSo = 0; chiSo < 4; chiSo++) {
            if (cacMucHang[chiSo] < 0) {
                HopThoai.hienThiCanhBao(
                        this, "Ngưỡng hạng " + HANG_CAC_TEN[chiSo] + " không hợp lệ.");
                cacHang[chiSo].requestFocusInWindow();
                return;
            }
            if (chiSo > 0 && cacMucHang[chiSo] <= cacMucHang[chiSo - 1]) {
                HopThoai.hienThiCanhBao(
                        this,
                        "Ngưỡng hạng phải tăng dần: "
                                + HANG_CAC_TEN[chiSo - 1]
                                + " < "
                                + HANG_CAC_TEN[chiSo]
                                + ".");
                cacHang[chiSo].requestFocusInWindow();
                return;
            }
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> {
                    long doiDiemGiuNguyen =
                            soTienMoiDiemDoi > 0 ? soTienMoiDiemDoi : docSoTienDoiDiemHienTai();
                    CaiDatService.macDinh()
                            .capNhatTichLuy(tichDiemMoi, doiDiemGiuNguyen, cacMucHang);
                    NhatKyHeThongService.macDinh()
                            .ghiNhatKy("Sửa", "Quy tắc tích điểm và hạng thành viên");
                },
                () -> {
                    nap();
                    hienThiThongBaoTam("Đã lưu quy tắc.");
                });
    }

    private void hienThiThongBaoTam(String vanBan) {
        nhanThongBao.setForeground(MauUngDung.XANH_LA);
        nhanThongBao.setText(vanBan);
        Timer boHen =
                new Timer(
                        3500,
                        suKien -> {
                            if (vanBan.equals(nhanThongBao.getText())) {
                                nhanThongBao.setText(" ");
                            }
                        });
        boHen.setRepeats(false);
        boHen.start();
    }

    // 4. THÔNG TIN TRỢ GIÚP MÀN HÌNH

    @Override
    protected String layTieuDeTroGiup() {
        return "Quy tắc điểm";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Điểm của khách tự cộng/trừ theo hóa đơn, không sửa tay.";
    }
}
