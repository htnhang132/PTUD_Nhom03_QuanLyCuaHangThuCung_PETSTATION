package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.model.GioHang;
import com.petstation.service.DanhMucService;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho khách hàng bộ chọn.
 *
 * <p>Chọn khách cho hóa đơn bằng MỘT ô tìm (SĐT, mã hoặc tên). Chọn xong hiện hạng, điểm và ô "Dùng
 * điểm". Không chọn thì là khách lẻ.
 */
public final class BoChonKhachHang extends JPanel {
    /** Bán hàng. */
    private final GioHang banHang;

    /** Khi thay đổi. */
    private final Runnable khiThayDoi;

    /** Đầu vào. */
    private final JTextField txtDauVao = new JTextField();

    /** Nhãn. */
    private final JPanel pnlNhan = new JPanel();

    /** Văn bản nhãn. */
    private final JLabel lblVanBanNhan = new JLabel();

    /** Điểm. */
    private final JSpinner spnDiem = new JSpinner(new SpinnerNumberModel(0, 0, 0, 1));

    /** Điểm gợi ý. */
    private final JLabel lblDiemGoiY = new JLabel();

    /** Đang cập nhật. */
    private boolean dangCapNhat;

    /**
     * Khởi tạo BoChonKhachHang với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param khiThayDoi khi thay đổi
     */
    public BoChonKhachHang(GioHang banHang, Runnable khiThayDoi) {
        this.banHang = banHang;
        this.khiThayDoi = khiThayDoi;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nhãn.
        JLabel lblNhan =
                new JLabel("Khách hàng (SĐT, mã hoặc tên — Enter để tìm; bỏ trống = khách lẻ)");
        lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblNhan.setForeground(MauUngDung.VAN_BAN_XAM);
        lblNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblNhan);

        // Dòng.
        JPanel pnlDong = new JPanel(new BorderLayout(6, 0));
        pnlDong.setOpaque(false);
        pnlDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtDauVao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtDauVao.getAccessibleContext().setAccessibleName("Tìm khách hàng");
        txtDauVao.addActionListener(suKien -> timKiem());
        // Tìm.
        JButton btnTim = new JButton("Tìm");
        btnTim.setFocusPainted(false);
        btnTim.addActionListener(suKien -> timKiem());
        pnlDong.add(txtDauVao, BorderLayout.CENTER);
        pnlDong.add(btnTim, BorderLayout.EAST);
        add(pnlDong);

        pnlNhan.setLayout(new BorderLayout(8, 0));
        pnlNhan.setBackground(new Color(0xEA, 0xF3, 0xFF));
        pnlNhan.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xBF, 0xD6, 0xF5)),
                        new EmptyBorder(6, 10, 6, 10)));
        pnlNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        lblVanBanNhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        // Xóa.
        JButton btnXoa = new JButton("Bỏ chọn");
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(suKien -> xoa());
        // Sử dụng điểm.
        JPanel pnlSuDungDiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlSuDungDiem.setOpaque(false);
        // Nhãn sử dụng.
        JLabel lblNhanSuDung = new JLabel("Dùng điểm:");
        lblNhanSuDung.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        spnDiem.setPreferredSize(new Dimension(80, 26));
        spnDiem.addChangeListener(
                suKien -> {
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (dangCapNhat) return;
                    banHang.datDiemDaDung(((Number) spnDiem.getValue()).longValue());
                    lamMoiDiemGoiY();
                    khiThayDoi.run();
                });
        lblDiemGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblDiemGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlSuDungDiem.add(lblNhanSuDung);
        pnlSuDungDiem.add(spnDiem);
        pnlSuDungDiem.add(lblDiemGoiY);
        // Văn bản.
        JPanel pnlVanBan = new JPanel(new BorderLayout(0, 2));
        pnlVanBan.setOpaque(false);
        pnlVanBan.add(lblVanBanNhan, BorderLayout.NORTH);
        pnlVanBan.add(pnlSuDungDiem, BorderLayout.CENTER);
        pnlNhan.add(pnlVanBan, BorderLayout.CENTER);
        pnlNhan.add(btnXoa, BorderLayout.EAST);
        pnlNhan.setVisible(false);
        add(javax.swing.Box.createVerticalStrut(4));
        add(pnlNhan);
        dongBo();
    }

    /** Tìm kiếm trong BoChonKhachHang. */
    private void timKiem() {
        // Từ khóa.
        String tuKhoa = txtDauVao.getText().trim();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (tuKhoa.isEmpty()) {
            xoa();
            return;
        }
        // Các chữ số.
        String cacChuSo = tuKhoa.replaceAll("[\\s.]", "");
        // Các kết quả.
        List<Object[]> cacKetQua = new ArrayList<>();
        for (Object[] dongDuLieu : DanhMucService.layKhachHang()) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if ("Ngừng hoạt động".equals(dongDuLieu[8])) continue;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (String.valueOf(dongDuLieu[2]).equals(cacChuSo)
                    || String.valueOf(dongDuLieu[0]).equalsIgnoreCase(tuKhoa)) {
                cacKetQua.clear();
                cacKetQua.add(dongDuLieu);
                break;
            }
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (String.valueOf(dongDuLieu[1]).toLowerCase().contains(tuKhoa.toLowerCase()))
                cacKetQua.add(dongDuLieu);
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cacKetQua.isEmpty()) {
            HopThoai.hienThiThongTin(
                    this,
                    "Không tìm thấy khách \""
                            + tuKhoa
                            + "\".\n"
                            + "Thêm khách mới tại Danh mục ▸ Khách hàng, hoặc để trống để bán cho"
                            + " khách lẻ.");
            return;
        }
        // Đã chọn.
        Object[] daChon = cacKetQua.get(0);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (cacKetQua.size() > 1) {
            // Các lựa chọn.
            String[] cacLuaChon =
                    cacKetQua.stream()
                            .map(
                                    dongDuLieu ->
                                            dongDuLieu[0]
                                                    + " - "
                                                    + dongDuLieu[1]
                                                    + " ("
                                                    + dongDuLieu[2]
                                                    + ")")
                            .toArray(String[]::new);
            // Chọn.
            Object chon =
                    JOptionPane.showInputDialog(
                            this,
                            "Có nhiều khách phù hợp, chọn một:",
                            "Chọn khách hàng",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            cacLuaChon,
                            cacLuaChon[0]);
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (chon == null) return;
            for (int chiSo = 0;
                    chiSo < cacLuaChon.length;
                    chiSo++) // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (cacLuaChon[chiSo].equals(chon)) daChon = cacKetQua.get(chiSo);
        }
        banHang.datKhachHang(
                String.valueOf(daChon[0]),
                String.valueOf(daChon[1]),
                TienTe.doc(String.valueOf(daChon[7])));
        txtDauVao.setText("");
        dongBo();
        khiThayDoi.run();
    }

    /** Xóa trong BoChonKhachHang. */
    public void xoa() {
        banHang.datKhachHang("", "", 0);
        txtDauVao.setText("");
        dongBo();
        khiThayDoi.run();
    }

    /**
     * Đồng bộ trong BoChonKhachHang.
     *
     * <p>Đồng bộ giao diện theo hóa đơn hiện tại (ví dụ sau khi thanh toán xong hóa đơn được xóa).
     */
    public void dongBo() {
        // Có.
        boolean co = !banHang.layMaKhachHang().isEmpty();
        pnlNhan.setVisible(co);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (co) {
            // Hạng.
            String hang = "";
            for (Object[] dongDuLieu : DanhMucService.layKhachHang())
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (dongDuLieu[0].equals(banHang.layMaKhachHang()))
                    hang = String.valueOf(dongDuLieu[6]);
            lblVanBanNhan.setText(
                    banHang.layTenKhachHang()
                            + "  ·  Hạng "
                            + hang
                            + "  ·  "
                            + banHang.layKhachHangDiem()
                            + " điểm");
            dangCapNhat = true;
            // Lớn nhất.
            long lonNhat =
                    Math.max(
                            0,
                            Math.min(
                                    banHang.layKhachHangDiem(),
                                    (banHang.tinhTamTinh()
                                                    - banHang.layKhuyenMaiGiamGia()
                                                    - banHang.laySoTienVoucher())
                                            / Math.max(
                                                    1,
                                                    com.petstation.service.CaiDatService
                                                            .layGiaTriQuyDoiMoiDiem())));
            spnDiem.setModel(
                    new SpinnerNumberModel(
                            (int) Math.min(banHang.layDiemDaDung(), lonNhat), 0, (int) lonNhat, 1));
            dangCapNhat = false;
            lamMoiDiemGoiY();
        }
        revalidate();
        repaint();
    }

    /** Làm mới điểm gợi ý trong BoChonKhachHang. */
    private void lamMoiDiemGoiY() {
        lblDiemGoiY.setText(
                "= "
                        + TienTe.dinhDang(banHang.layGiaTriDiem())
                        + " (1 điểm = "
                        + TienTe.dinhDang(
                                com.petstation.service.CaiDatService.layGiaTriQuyDoiMoiDiem())
                        + ")");
    }
}
