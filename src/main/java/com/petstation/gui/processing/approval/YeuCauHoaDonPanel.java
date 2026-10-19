package com.petstation.gui.processing.approval;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.service.PheDuyetService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho hóa đơn yêu cầu vùng hiển thị.
 *
 * <p>Yêu cầu sửa/hủy hóa đơn đã chốt: gửi sang Phê duyệt, không sửa trực tiếp.
 */
public final class YeuCauHoaDonPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Hóa đơn. */
    private final JTextField txtHoaDon = new JTextField();

    /** Kiểu. */
    private final JComboBox<String> cboKieu =
            new JComboBox<>(new String[] {"Sửa ghi chú hóa đơn", "Hủy hóa đơn"});

    /** Lý do. */
    private final JTextArea txtLyDo = new JTextArea(3, 30);

    /** Thay đổi. */
    private final JTextArea txtThayDoi = new JTextArea(4, 30);

    /**
     * Khởi tạo YeuCauHoaDonPanel với các giá trị được cung cấp.
     *
     * @param boDieuHuong bộ điều hướng
     */
    public YeuCauHoaDonPanel(Consumer<MaManHinh> boDieuHuong) {
        this.boDieuHuong = boDieuHuong;
        setLayout(new BorderLayout(0, 12));
        add(taoTieuDeMuc("Yêu cầu sửa / hủy hóa đơn"), BorderLayout.NORTH);

        // Thẻ.
        JPanel pnlThe = new JPanel(new GridBagLayout());
        pnlThe.setBackground(Color.WHITE);
        pnlThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(16, 18, 16, 18)));
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.gridx = 0;
        giaTriDangXet.weightx = 1;
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        giaTriDangXet.insets = new Insets(4, 0, 2, 0);
        // Tọa độ dọc.
        int toaDoDoc = 0;
        txtHoaDon.setEditable(false);
        toaDoDoc = them(pnlThe, giaTriDangXet, toaDoDoc, "Hóa đơn", txtHoaDon);
        toaDoDoc = them(pnlThe, giaTriDangXet, toaDoDoc, "Loại yêu cầu", cboKieu);
        toaDoDoc = them(pnlThe, giaTriDangXet, toaDoDoc, "Lý do *", new JScrollPane(txtLyDo));
        toaDoDoc =
                them(
                        pnlThe,
                        giaTriDangXet,
                        toaDoDoc,
                        "Ghi chú mới (bắt buộc khi chọn Sửa)",
                        new JScrollPane(txtThayDoi));
        // Ghi chú.
        JLabel lblGhiChu =
                new JLabel(
                        "Hóa đơn đã thanh toán bị khóa. Yêu cầu sẽ gửi Cửa hàng trưởng duyệt; khi"
                                + " duyệt hệ thống tự thực hiện.");
        lblGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblGhiChu.setForeground(MauUngDung.VAN_BAN_XAM);
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.insets = new Insets(10, 0, 4, 0);
        pnlThe.add(lblGhiChu, giaTriDangXet);
        // Các nút.
        JPanel pnlCacNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlCacNut.setOpaque(false);
        // Hủy.
        NutDinhDang btnHuy = new NutDinhDang("Quay lại Tìm kiếm", NutDinhDang.KieuNut.TRONG_SUOT);
        // Gửi.
        NutDinhDang btnGui = new NutDinhDang("Gửi phê duyệt", NutDinhDang.KieuNut.CHINH);
        btnHuy.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.ORDERS));
        btnGui.addActionListener(suKien -> gui());
        pnlCacNut.add(btnHuy);
        pnlCacNut.add(btnGui);
        giaTriDangXet.gridy = toaDoDoc;
        giaTriDangXet.insets = new Insets(6, 0, 0, 0);
        pnlThe.add(pnlCacNut, giaTriDangXet);
        // Vùng chứa.
        JPanel pnlVungChua = taoVungTrongSuot(new BorderLayout());
        pnlVungChua.add(pnlThe, BorderLayout.NORTH);
        add(pnlVungChua, BorderLayout.CENTER);
    }

    /**
     * Thêm trong YeuCauHoaDonPanel.
     *
     * @param pnlVungDangXet vùng đang xét
     * @param giaTriDangXet giá trị đang xét
     * @param toaDoDoc tọa độ dọc
     * @param nhan nhãn
     * @param thanhPhan thành phần
     * @return kết quả thêm
     */
    private int them(
            JPanel pnlVungDangXet,
            GridBagConstraints giaTriDangXet,
            int toaDoDoc,
            String nhan,
            java.awt.Component thanhPhan) {
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(nhan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.insets = new Insets(8, 0, 1, 0);
        pnlVungDangXet.add(lblDongDangXet, giaTriDangXet);
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.insets = new Insets(0, 0, 0, 0);
        pnlVungDangXet.add(thanhPhan, giaTriDangXet);
        return toaDoDoc;
    }

    /** Gửi trong YeuCauHoaDonPanel. */
    private void gui() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (txtHoaDon.getText().isBlank()) {
            HopThoai.hienThiCanhBao(
                    this,
                    "Chưa chọn hóa đơn. Hãy vào Tìm kiếm ▸ Hóa đơn, chọn hóa đơn rồi bấm \"Yêu cầu"
                            + " sửa/hủy\".");
            return;
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (txtLyDo.getText().isBlank()) {
            HopThoai.hienThiCanhBao(this, "Cần nhập lý do.");
            txtLyDo.requestFocusInWindow();
            return;
        }
        // Sửa.
        boolean sua = cboKieu.getSelectedIndex() == 0;
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (sua && txtThayDoi.getText().isBlank()) {
            HopThoai.hienThiCanhBao(this, "Hãy mô tả nội dung cần sửa.");
            txtThayDoi.requestFocusInWindow();
            return;
        }
        // Mã.
        String ma = txtHoaDon.getText();
        try {
            com.petstation.service.PheDuyetService.layHoaDonYeuCau(
                    ma, !sua, txtLyDo.getText().trim(), txtThayDoi.getText().trim());
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
            return;
        }
        HopThoai.hienThiThanhCong(
                this,
                "Đã gửi yêu cầu " + (sua ? "sửa" : "hủy") + " hóa đơn " + ma + " sang Phê duyệt.");
        txtLyDo.setText("");
        txtThayDoi.setText("");
        boDieuHuong.accept(MaManHinh.ORDERS);
    }

    /** Xử lý hiển thị trong YeuCauHoaDonPanel. */
    @Override
    public void xuLyHienThi() {
        txtHoaDon.setText(PheDuyetService.daChonHoaDon);
    }

    /**
     * Lấy tiêu đề trợ giúp trong YeuCauHoaDonPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Yêu cầu sửa/hủy hóa đơn";
    }

    /**
     * Lấy nội dung trợ giúp trong YeuCauHoaDonPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn hóa đơn ở Tìm kiếm, nhập lý do rồi gửi phê duyệt.";
    }
}
