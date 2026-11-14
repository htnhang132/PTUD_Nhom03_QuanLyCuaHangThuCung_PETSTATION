package com.petstation.gui.khachhang;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

// Màn hình con (Tab 1) trong Danh mục > Điểm thưởng và voucher
public final class QuyTacTichDiemPanel extends CoSoPanel {

    // 1. KHAI BÁO CÁC TRƯỜNG NHẬP LIỆU & DANH SÁCH HẠNG THÀNH VIÊN

    private final JTextField oTichDiem = taoTruong();
    private final JTextField oDoiDiem = taoTruong();
    private final JTextField[] cacHang = {taoTruong(), taoTruong(), taoTruong(), taoTruong()};
    private static final String[] HANG_CAC_TEN = {"Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
    private final JLabel nhanThongBao = new JLabel(" ");
    private int luotTai;

    // 2. KHỞI TẠO BỐ CỤC BIỂU MẪU CẤU HÌNH QUY TẮC TÍCH ĐIỂM & HẠNG

    public QuyTacTichDiemPanel() {
        setLayout(new BorderLayout(0, 10));
        add(taoTieuDeMuc("Quy tắc tích điểm và hạng thành viên"), BorderLayout.NORTH);
        JPanel khungThe = new JPanel(new GridBagLayout());
        khungThe.setBackground(Color.WHITE);
        khungThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(16, 18, 16, 18)));
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
        btnLuu.setEnabled(
                PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_TAI_KHOAN));
        btnLuu.addActionListener(
                suKien -> {
                    try {
                        luu();
                    } catch (RuntimeException loi) {
                        HopThoai.hienThiCanhBao(this, loi.getMessage());
                    }
                });
        giaTriDangXet.gridy = toaDoDoc;
        giaTriDangXet.fill = GridBagConstraints.NONE;
        khungThe.add(btnLuu, giaTriDangXet);
        JPanel khungVungChua = taoVungTrongSuot(new BorderLayout());
        khungVungChua.add(khungThe, BorderLayout.NORTH);
        add(khungVungChua, BorderLayout.CENTER);
        nap();
    }

    private static JTextField taoTruong() {
        JTextField oGiaTriTam = new JTextField(14);
        oGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
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
        khungThe.add(new JLabel(nhan), giaTriDangXet);
        giaTriDangXet.gridx = 1;
        khungThe.add(oTruongDangXet, giaTriDangXet);
        giaTriDangXet.gridx = 2;
        khungThe.add(new JLabel(donVi), giaTriDangXet);
    }

    // 3. NẠP CẤU HÌNH HIỆN TẠI & LƯU NGƯỠNG HẠNG THÀNH VIÊN VÀO CSDL

    private void nap() {
        oDoiDiem.setText("150.000");
        oTichDiem.setEditable(true);
        oDoiDiem.setEditable(false);
        oDoiDiem.setToolTipText("Quy đổi voucher Spa cố định: 200 điểm = 150.000₫.");
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> {
                    long moiDiem = CaiDatService.macDinh().laySoTienChoMoiDiemTichLuy();
                    long[] hang = CaiDatService.macDinh().layHangChiTieu();
                    return new long[] {moiDiem, hang[0], hang[1], hang[2], hang[3]};
                },
                ketQua -> {
                    oTichDiem.setText(DinhDang.dinhDang(ketQua[0]));
                    for (int chiSo = 0; chiSo < 4; chiSo++) {
                        cacHang[chiSo].setText(DinhDang.dinhDang(ketQua[chiSo + 1]));
                    }
                });
    }

    private void luu() {
        long suKien = DinhDang.doc(oTichDiem.getText());
        long dongDuLieu = 0;
        long[] giaTriTam = new long[4];
        for (int chiSo = 0; chiSo < 4; chiSo++)
            giaTriTam[chiSo] = DinhDang.doc(cacHang[chiSo].getText());
        if (suKien <= 0) {
            HopThoai.hienThiCanhBao(this, "Số đồng cho 1 điểm phải lớn hơn 0.");
            return;
        }
        for (int chiSo = 0; chiSo < 4; chiSo++) {
            if (giaTriTam[chiSo] < 0) {
                HopThoai.hienThiCanhBao(
                        this, "Ngưỡng hạng " + HANG_CAC_TEN[chiSo] + " không hợp lệ.");
                return;
            }
            if (chiSo > 0 && giaTriTam[chiSo] <= giaTriTam[chiSo - 1]) {
                HopThoai.hienThiCanhBao(
                        this,
                        "Ngưỡng hạng phải tăng dần: "
                                + HANG_CAC_TEN[chiSo - 1]
                                + " < "
                                + HANG_CAC_TEN[chiSo]
                                + ".");
                return;
            }
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> {
                    CaiDatService.macDinh().capNhatTichLuy(suKien, dongDuLieu, giaTriTam);
                    NhatKyHeThongService.macDinh()
                            .ghiNhatKy("Sửa", "Quy tắc tích điểm và hạng thành viên");
                },
                () -> {
                    nap();
                    nhanThongBao.setForeground(MauUngDung.XANH_LA);
                    nhanThongBao.setText("Đã lưu quy tắc.");
                });
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
