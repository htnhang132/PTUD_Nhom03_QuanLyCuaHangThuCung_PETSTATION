package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.util.TienTe;

import java.awt.*;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho linh hoạt giảm giá vùng hiển thị.
 *
 * <p>Panel giảm giá linh hoạt: % hoặc số tiền cố định
 */
public class GiamGiaLinhHoatPanel extends JPanel {

    /** Cung cấp giao diện và xử lý tương tác cho kiểu giảm giá. */
    public enum LoaiGiamGia {
        PHAN_TRAM("Giảm %"),
        SO_TIEN("Giảm số tiền");

        /** Nhãn. */
        private String nhan;

        /**
         * Khởi tạo LoaiGiamGia với các giá trị được cung cấp.
         *
         * @param nhan nhãn
         */
        LoaiGiamGia(String nhan) {
            this.nhan = nhan;
        }

        /**
         * Lấy nhãn trong LoaiGiamGia.
         *
         * @return kết quả lấy nhãn
         */
        public String layNhan() {
            return nhan;
        }
    }

    /** Kiểu hộp chọn. */
    private JComboBox<String> cboKieuHopChon;

    /** Trường giá trị. */
    private JTextField txtTruongGiaTri;

    /** Nhãn lỗi. */
    private JLabel lblNhanLoi;

    /** Nhãn xem trước. */
    private JLabel lblNhanXemTruoc;

    /** Tạm tính. */
    private long tamTinh = 0;

    /** Khởi tạo GiamGiaLinhHoatPanel với các giá trị được cung cấp. */
    public GiamGiaLinhHoatPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        // Nhãn.
        JLabel lblNhan = new JLabel("Giảm giá:");
        lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        add(lblNhan);

        // Bộ chọn loại
        cboKieuHopChon =
                new JComboBox<>(
                        new String[] {
                            LoaiGiamGia.PHAN_TRAM.layNhan(), LoaiGiamGia.SO_TIEN.layNhan()
                        });
        cboKieuHopChon.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        cboKieuHopChon.addActionListener(
                suKien -> {
                    capNhatGoiYNhap();
                    tinhXemTruoc();
                });
        add(cboKieuHopChon);

        // Trường nhập giá trị
        txtTruongGiaTri = new JTextField(8);
        txtTruongGiaTri.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtTruongGiaTri
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            /**
                             * Lấy đã thay đổi cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            public void changedUpdate(javax.swing.event.DocumentEvent suKien) {
                                tinhXemTruoc();
                            }

                            /**
                             * Xóa cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            public void removeUpdate(javax.swing.event.DocumentEvent suKien) {
                                tinhXemTruoc();
                            }

                            /**
                             * Thêm cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            public void insertUpdate(javax.swing.event.DocumentEvent suKien) {
                                tinhXemTruoc();
                            }
                        });
        add(txtTruongGiaTri);

        // Nhãn xem trước
        lblNhanXemTruoc = new JLabel();
        lblNhanXemTruoc.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblNhanXemTruoc.setForeground(new Color(34, 139, 34));
        add(lblNhanXemTruoc);

        // Nhãn lỗi bên dưới
        lblNhanLoi = new JLabel("");
        lblNhanLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblNhanLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);

        capNhatGoiYNhap();
    }

    /** Cập nhật gợi ý nhập trong GiamGiaLinhHoatPanel. */
    private void capNhatGoiYNhap() {
        // Kiểu.
        LoaiGiamGia kieu = layLoaiDuocChon();
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (kieu == LoaiGiamGia.PHAN_TRAM) {
            txtTruongGiaTri.setToolTipText("Nhập % (0-100)");
        } else {
            txtTruongGiaTri.setToolTipText("Nhập số tiền giảm");
        }
    }

    /** Tính xem trước trong GiamGiaLinhHoatPanel. */
    private void tinhXemTruoc() {
        try {
            // Giảm giá.
            long giamGia = tinhGiamGia();
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (giamGia > 0) {
                lblNhanXemTruoc.setText("(- " + TienTe.dinhDang(giamGia) + ")");
            } else {
                lblNhanXemTruoc.setText("");
            }
            lblNhanLoi.setText("");
        } catch (Exception loi) {
            lblNhanXemTruoc.setText("");
            // Nhãn lỗi được cập nhật khi tính số tiền giảm giá
        }
    }

    /**
     * Tính giảm giá trong GiamGiaLinhHoatPanel.
     *
     * @return kết quả tính giảm giá
     */
    public long tinhGiamGia() {
        // Giá trị.
        String giaTri = txtTruongGiaTri.getText().trim();

        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri.isEmpty()) {
            lblNhanLoi.setText("");
            return 0;
        }

        try {
            // Kiểu.
            LoaiGiamGia kieu = layLoaiDuocChon();

            // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
            if (kieu == LoaiGiamGia.PHAN_TRAM) {
                // Phần trăm.
                double phanTram = Double.parseDouble(giaTri);

                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (phanTram < 0) {
                    lblNhanLoi.setText("% không thể âm");
                    return 0;
                }

                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (phanTram > 100) {
                    lblNhanLoi.setText("% không thể > 100");
                    return 0;
                }

                lblNhanLoi.setText("");
                return (long) (tamTinh * phanTram / 100);

            } else { // AMOUNT
                // Số tiền.
                long soTien = Long.parseLong(giaTri.replace(",", "").replace(".", ""));

                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (soTien < 0) {
                    lblNhanLoi.setText("Số tiền không thể âm");
                    return 0;
                }

                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (soTien > tamTinh) {
                    lblNhanLoi.setText("Giảm giá không thể > tổng tiền");
                    return tamTinh;
                }

                lblNhanLoi.setText("");
                return soTien;
            }

        } catch (NumberFormatException loi) {
            lblNhanLoi.setText("Giá trị không hợp lệ");
            return 0;
        }
    }

    /**
     * Đặt tạm tính trong GiamGiaLinhHoatPanel.
     *
     * @param tamTinh tạm tính
     */
    public void datTamTinh(long tamTinh) {
        this.tamTinh = tamTinh;
        tinhXemTruoc();
    }

    /**
     * Lấy tạm tính trong GiamGiaLinhHoatPanel.
     *
     * @return kết quả lấy tạm tính
     */
    public long layTamTinh() {
        return tamTinh;
    }

    /**
     * Lấy loại được chọn trong GiamGiaLinhHoatPanel.
     *
     * @return kết quả lấy loại được chọn
     */
    public LoaiGiamGia layLoaiDuocChon() {
        // Chỉ số.
        int chiSo = cboKieuHopChon.getSelectedIndex();
        return chiSo == 0 ? LoaiGiamGia.PHAN_TRAM : LoaiGiamGia.SO_TIEN;
    }

    /**
     * Lấy văn bản giá trị trong GiamGiaLinhHoatPanel.
     *
     * @return kết quả lấy văn bản giá trị
     */
    public String layVanBanGiaTri() {
        return txtTruongGiaTri.getText().trim();
    }

    /**
     * Đặt giá trị trong GiamGiaLinhHoatPanel.
     *
     * @param giaTri giá trị
     */
    public void datGiaTri(String giaTri) {
        txtTruongGiaTri.setText(giaTri);
    }

    /**
     * Đặt kiểu trong GiamGiaLinhHoatPanel.
     *
     * @param kieu kiểu
     */
    public void datKieu(LoaiGiamGia kieu) {
        cboKieuHopChon.setSelectedIndex(kieu == LoaiGiamGia.PHAN_TRAM ? 0 : 1);
    }

    /** Xóa trong GiamGiaLinhHoatPanel. */
    public void xoa() {
        txtTruongGiaTri.setText("");
        lblNhanLoi.setText("");
        lblNhanXemTruoc.setText("");
    }

    /**
     * Lấy nhãn lỗi trong GiamGiaLinhHoatPanel.
     *
     * @return kết quả lấy nhãn lỗi
     */
    public JLabel layNhanLoi() {
        return lblNhanLoi;
    }

    /**
     * Kiểm tra có lỗi trong GiamGiaLinhHoatPanel.
     *
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public boolean kiemTraCoLoi() {
        return !lblNhanLoi.getText().trim().isEmpty();
    }

    /**
     * Tạo nhãn với lỗi trong GiamGiaLinhHoatPanel.
     *
     * <p>Tạo panel với error label ở dưới
     *
     * @return kết quả tạo nhãn với lỗi
     */
    public JPanel taoNhanVoiLoi() {
        // Vùng bao.
        JPanel pnlVungBao = new JPanel(new BorderLayout());
        pnlVungBao.setOpaque(false);
        pnlVungBao.add(this, BorderLayout.NORTH);
        pnlVungBao.add(lblNhanLoi, BorderLayout.SOUTH);
        return pnlVungBao;
    }
}
