package com.petstation.gui.catalog;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.CaiDatService;
import com.petstation.service.NhatKyHeThongService;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho tích lũy các quy tắc vùng hiển thị.
 *
 * <p>Điểm thưởng và voucher ▸ Quy tắc điểm: bao nhiêu đồng được 1 điểm, 1 điểm đổi bao nhiêu đồng,
 * ngưỡng hạng thành viên.
 */
public final class QuyTacTichDiemPanel extends CoSoPanel {
    /** Tích điểm. */
    private final JTextField txtTichDiem = taoTruong();

    /** Đổi điểm. */
    private final JTextField txtDoiDiem = taoTruong();

    /** Các hạng. */
    private final JTextField[] cacHang = {taoTruong(), taoTruong(), taoTruong(), taoTruong()};

    /** Hạng các tên. */
    private static final String[] HANG_CAC_TEN = {"Bạc", "Vàng", "Bạch Kim", "Kim Cương"};

    /** Thông báo. */
    private final JLabel lblThongBao = new JLabel(" ");

    public QuyTacTichDiemPanel() {
        setLayout(new BorderLayout(0, 10));
        add(taoTieuDeMuc("Quy tắc tích điểm và hạng thành viên"), BorderLayout.NORTH);
        // Thẻ.
        JPanel pnlThe = new JPanel(new GridBagLayout());
        pnlThe.setBackground(java.awt.Color.WHITE);
        pnlThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(16, 18, 16, 18)));
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(6, 0, 6, 12);
        giaTriDangXet.anchor = GridBagConstraints.WEST;
        // Tọa độ dọc.
        int toaDoDoc = 0;
        layDong(
                pnlThe,
                giaTriDangXet,
                toaDoDoc++,
                "Chi tiêu bao nhiêu đồng được 1 điểm",
                txtTichDiem,
                "₫");
        layDong(
                pnlThe,
                giaTriDangXet,
                toaDoDoc++,
                "200 điểm đổi voucher Spa (hạn 60 ngày)",
                txtDoiDiem,
                "₫");
        // Đầu.
        JLabel lblDau = new JLabel("Hạng thành viên (theo tổng chi tiêu từ):");
        lblDau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.gridwidth = 3;
        giaTriDangXet.insets = new Insets(16, 0, 4, 0);
        pnlThe.add(lblDau, giaTriDangXet);
        giaTriDangXet.gridwidth = 1;
        giaTriDangXet.insets = new Insets(6, 0, 6, 12);
        for (int chiSo = 0; chiSo < 4; chiSo++)
            layDong(
                    pnlThe,
                    giaTriDangXet,
                    toaDoDoc++,
                    "Hạng " + HANG_CAC_TEN[chiSo],
                    cacHang[chiSo],
                    "₫");
        lblThongBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.gridwidth = 3;
        pnlThe.add(lblThongBao, giaTriDangXet);
        // Lưu.
        NutDinhDang btnLuu = new NutDinhDang("Lưu quy tắc", NutDinhDang.KieuNut.THANH_CONG);
        btnLuu.setEnabled(
                com.petstation.service.PhienDangNhapService.layPhien()
                        .kiemTraCo(com.petstation.model.Quyen.QUAN_LY_TAI_KHOAN));
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
        pnlThe.add(btnLuu, giaTriDangXet);
        // Vùng chứa.
        JPanel pnlVungChua = taoVungTrongSuot(new BorderLayout());
        pnlVungChua.add(pnlThe, BorderLayout.NORTH);
        add(pnlVungChua, BorderLayout.CENTER);
        nap();
    }

    private static JTextField taoTruong() {
        JTextField txtGiaTriTam = new JTextField(14);
        txtGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtGiaTriTam.setHorizontalAlignment(JTextField.RIGHT);
        return txtGiaTriTam;
    }

    private void layDong(
            JPanel pnlThe,
            GridBagConstraints giaTriDangXet,
            int toaDoDoc,
            String nhan,
            JTextField txtTruongDangXet,
            String donVi) {
        giaTriDangXet.gridy = toaDoDoc;
        giaTriDangXet.gridx = 0;
        pnlThe.add(new JLabel(nhan), giaTriDangXet);
        giaTriDangXet.gridx = 1;
        pnlThe.add(txtTruongDangXet, giaTriDangXet);
        giaTriDangXet.gridx = 2;
        pnlThe.add(new JLabel(donVi), giaTriDangXet);
    }

    private void nap() {
        txtTichDiem.setText(TienTe.dinhDang(CaiDatService.laySoTienChoMoiDiemTichLuy()));
        txtDoiDiem.setText("150.000");
        txtTichDiem.setEditable(false);
        txtDoiDiem.setEditable(false);
        long[] giaTriTam = CaiDatService.layHangChiTieu();
        for (int chiSo = 0; chiSo < 4; chiSo++)
            cacHang[chiSo].setText(TienTe.dinhDang(giaTriTam[chiSo]));
    }

    private void luu() {
        // Sự kiện.
        long suKien = TienTe.doc(txtTichDiem.getText());
        // Dòng dữ liệu.
        long dongDuLieu = 0;
        long[] giaTriTam = new long[4];
        for (int chiSo = 0; chiSo < 4; chiSo++)
            giaTriTam[chiSo] = TienTe.doc(cacHang[chiSo].getText());
        if (suKien != 10000) {
            HopThoai.hienThiCanhBao(this, "Số đồng cho 1 điểm và giá trị 1 điểm phải lớn hơn 0.");
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
        CaiDatService.capNhatTichLuy(suKien, dongDuLieu, giaTriTam);
        nap();
        NhatKyHeThongService.ghiNhatKy("Sửa", "Quy tắc tích điểm và hạng thành viên");
        lblThongBao.setForeground(MauUngDung.XANH_LA);
        lblThongBao.setText("Đã lưu quy tắc.");
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Quy tắc điểm";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Điểm của khách tự cộng/trừ theo hóa đơn, không sửa tay.";
    }
}
