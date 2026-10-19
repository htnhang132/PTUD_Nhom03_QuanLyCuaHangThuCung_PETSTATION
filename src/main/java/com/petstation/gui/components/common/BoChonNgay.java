package com.petstation.gui.components.common;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho ngày bộ chọn.
 *
 * <p>Custom BoChonNgay component with calendar popup Thay thế cho việc nhập tay ngày tháng vào
 * JTextField
 */
public class BoChonNgay extends JPanel {
    /** Trường văn bản. */
    private JTextField txtTruongVanBan;

    /** Nút lịch. */
    private JButton btnNutLich;

    /** Lịch hộp thoại. */
    private JDialog lichHopThoai;

    /** Ngày định dạng. */
    private SimpleDateFormat ngayDinhDang = new SimpleDateFormat("dd/MM/yyyy");

    /** Ngày được chọn. */
    private LocalDate ngayDuocChon;

    /** Nhãn tháng năm. */
    private JLabel lblNhanThangNam;

    /** Số ngày vùng hiển thị. */
    private JPanel pnlSoNgayVungHienThi;

    /** Hiện tại tháng. */
    private int hienTaiThang;

    /** Hiện tại năm. */
    private int hienTaiNam;

    /** Khởi tạo BoChonNgay với các giá trị được cung cấp. */
    public BoChonNgay() {
        this(LocalDate.now());
    }

    /**
     * Khởi tạo BoChonNgay với các giá trị được cung cấp.
     *
     * @param ngayBanDau ngày ban đầu
     */
    public BoChonNgay(LocalDate ngayBanDau) {
        setLayout(new BorderLayout(5, 0));
        this.ngayDuocChon = ngayBanDau;
        this.hienTaiThang = ngayBanDau.getMonthValue();
        this.hienTaiNam = ngayBanDau.getYear();

        // Trường nhập để hiển thị ngày đã chọn
        txtTruongVanBan = new JTextField(10);
        txtTruongVanBan.setEditable(false);
        txtTruongVanBan.setBackground(Color.WHITE);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayDuocChon != null) {
            txtTruongVanBan.setText(dinhDangNgay(ngayDuocChon));
        }

        // Nút mở lịch
        btnNutLich = new JButton("Lịch");
        btnNutLich.setPreferredSize(new Dimension(35, 25));
        btnNutLich.setFocusPainted(false);
        btnNutLich.addActionListener(suKien -> hienThiLich());

        add(txtTruongVanBan, BorderLayout.CENTER);
        add(btnNutLich, BorderLayout.EAST);
    }

    /** Hiển thị lịch trong BoChonNgay. */
    private void hienThiLich() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (lichHopThoai == null) {
            taoLichHopThoai();
        }

        // Đặt lại về tháng hiện tại hoặc tháng đã chọn
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayDuocChon != null) {
            hienTaiThang = ngayDuocChon.getMonthValue();
            hienTaiNam = ngayDuocChon.getYear();
        }

        capNhatLich();

        // Hiển thị hộp thoại gần thành phần đang tương tác
        // Vị trí.
        Point viTri = txtTruongVanBan.getLocationOnScreen();
        lichHopThoai.setLocation(viTri.x, viTri.y + txtTruongVanBan.getHeight());
        lichHopThoai.setVisible(true);
    }

    /** Tạo lịch hộp thoại trong BoChonNgay. */
    private void taoLichHopThoai() {
        // Cửa sổ.
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (cuaSo instanceof Frame) {
            lichHopThoai = new JDialog((Frame) cuaSo, "Chọn ngày", true);
        } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (cuaSo instanceof Dialog) {
            lichHopThoai = new JDialog((Dialog) cuaSo, "Chọn ngày", true);
        } else {
            lichHopThoai = new JDialog((Frame) null, "Chọn ngày", true);
        }

        lichHopThoai.setLayout(new BorderLayout());
        lichHopThoai.setUndecorated(false);

        // Tiêu đề với tháng/năm và nút điều hướng
        // Tiêu đề vùng hiển thị.
        JPanel pnlTieuDeVungHienThi = new JPanel(new BorderLayout());
        pnlTieuDeVungHienThi.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Nút trước.
        JButton btnNutTruoc = new JButton("◀");
        btnNutTruoc.addActionListener(
                suKien -> {
                    hienTaiThang--;
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (hienTaiThang < 1) {
                        hienTaiThang = 12;
                        hienTaiNam--;
                    }
                    capNhatLich();
                });

        // Nút tiếp theo.
        JButton btnNutTiepTheo = new JButton("▶");
        btnNutTiepTheo.addActionListener(
                suKien -> {
                    hienTaiThang++;
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (hienTaiThang > 12) {
                        hienTaiThang = 1;
                        hienTaiNam++;
                    }
                    capNhatLich();
                });

        lblNhanThangNam = new JLabel("", SwingConstants.CENTER);
        lblNhanThangNam.setFont(lblNhanThangNam.getFont().deriveFont(Font.BOLD, 14f));

        pnlTieuDeVungHienThi.add(btnNutTruoc, BorderLayout.WEST);
        pnlTieuDeVungHienThi.add(lblNhanThangNam, BorderLayout.CENTER);
        pnlTieuDeVungHienThi.add(btnNutTiepTheo, BorderLayout.EAST);

        // Panel chứa các ngày trong tháng
        pnlSoNgayVungHienThi = new JPanel(new GridLayout(0, 7, 2, 2));
        pnlSoNgayVungHienThi.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Thêm tiêu đề cho các ngày trong tuần
        // Ngày các tên.
        String[] ngayCacTen = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String tenNgay : ngayCacTen) {
            // Nhãn.
            JLabel lblNhan = new JLabel(tenNgay, SwingConstants.CENTER);
            lblNhan.setFont(lblNhan.getFont().deriveFont(Font.BOLD));
            lblNhan.setForeground(Color.BLUE);
            pnlSoNgayVungHienThi.add(lblNhan);
        }

        // Nút hôm nay và hủy
        // Chân trang vùng hiển thị.
        JPanel pnlChanTrangVungHienThi = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // Nút hôm nay.
        JButton btnNutHomNay = new JButton("Hôm nay");
        btnNutHomNay.addActionListener(
                suKien -> {
                    // Hôm nay.
                    LocalDate homNay = LocalDate.now();
                    chonNgay(homNay);
                });

        // Nút xóa.
        JButton btnNutXoa = new JButton("Xóa");
        btnNutXoa.addActionListener(
                suKien -> {
                    ngayDuocChon = null;
                    txtTruongVanBan.setText("");
                    lichHopThoai.setVisible(false);
                });

        pnlChanTrangVungHienThi.add(btnNutHomNay);
        pnlChanTrangVungHienThi.add(btnNutXoa);

        lichHopThoai.add(pnlTieuDeVungHienThi, BorderLayout.NORTH);
        lichHopThoai.add(pnlSoNgayVungHienThi, BorderLayout.CENTER);
        lichHopThoai.add(pnlChanTrangVungHienThi, BorderLayout.SOUTH);

        lichHopThoai.pack();
        lichHopThoai.setSize(300, 280);
    }

    /** Cập nhật lịch trong BoChonNgay. */
    private void capNhatLich() {
        lblNhanThangNam.setText(String.format("Tháng %d/%d", hienTaiThang, hienTaiNam));

        // Xóa các nút ngày cũ (giữ lại tiêu đề)
        // Các thành phần.
        Component[] cacThanhPhan = pnlSoNgayVungHienThi.getComponents();
        for (int chiSo = cacThanhPhan.length - 1; chiSo >= 7; chiSo--) {
            pnlSoNgayVungHienThi.remove(chiSo);
        }

        // Tính toán ngày đầu tiên của tháng
        // Đầu tiên ngày.
        LocalDate dauTienNgay = LocalDate.of(hienTaiNam, hienTaiThang, 1);
        // Đầu tiên ngày của tuần.
        int dauTienNgayCuaTuan = dauTienNgay.getDayOfWeek().getValue() % 7; // 0 = CN, 1 = T2, ...

        // Thêm ô trống cho các ngày trước ngày 1
        for (int chiSo = 0; chiSo < dauTienNgayCuaTuan; chiSo++) {
            pnlSoNgayVungHienThi.add(new JLabel(""));
        }

        // Thêm các ngày trong tháng
        // Số ngày vào tháng.
        int soNgayVaoThang = dauTienNgay.lengthOfMonth();
        for (int ngay = 1; ngay <= soNgayVaoThang; ngay++) {
            // Ngày.
            final LocalDate ngay2 = LocalDate.of(hienTaiNam, hienTaiThang, ngay);
            // Nút ngày.
            JButton btnNutNgay = new JButton(String.valueOf(ngay));
            btnNutNgay.setFocusPainted(false);

            // Làm nổi bật ngày được chọn
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (ngayDuocChon != null && ngay2.equals(ngayDuocChon)) {
                btnNutNgay.setBackground(new Color(66, 133, 244));
                btnNutNgay.setForeground(Color.WHITE);
                btnNutNgay.setOpaque(true);
            }

            // Làm nổi bật ngày hôm nay
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ngay2.equals(LocalDate.now())) {
                btnNutNgay.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }

            btnNutNgay.addActionListener(suKien -> chonNgay(ngay2));
            pnlSoNgayVungHienThi.add(btnNutNgay);
        }

        pnlSoNgayVungHienThi.revalidate();
        pnlSoNgayVungHienThi.repaint();
    }

    /**
     * Chọn ngày trong BoChonNgay.
     *
     * @param ngay ngày
     */
    private void chonNgay(LocalDate ngay) {
        this.ngayDuocChon = ngay;
        txtTruongVanBan.setText(dinhDangNgay(ngay));
        lichHopThoai.setVisible(false);

        // Phát thông báo thay đổi thuộc tính cho các bộ lắng nghe
        firePropertyChange("date", null, ngay);
    }

    /**
     * Định dạng ngày trong BoChonNgay.
     *
     * @param ngay ngày
     * @return kết quả định dạng ngày
     */
    private String dinhDangNgay(LocalDate ngay) {
        return String.format(
                "%02d/%02d/%d", ngay.getDayOfMonth(), ngay.getMonthValue(), ngay.getYear());
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy ngày được chọn trong BoChonNgay.
     *
     * @return kết quả lấy ngày được chọn
     */
    public LocalDate layNgayDuocChon() {
        return ngayDuocChon;
    }

    /**
     * Đặt ngày được chọn trong BoChonNgay.
     *
     * @param ngay ngày
     */
    public void datNgayDuocChon(LocalDate ngay) {
        this.ngayDuocChon = ngay;
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngay != null) {
            txtTruongVanBan.setText(dinhDangNgay(ngay));
            hienTaiThang = ngay.getMonthValue();
            hienTaiNam = ngay.getYear();
        } else {
            txtTruongVanBan.setText("");
        }
    }

    /**
     * Lấy ngày trong BoChonNgay.
     *
     * @return kết quả lấy ngày
     */
    public Date layNgay() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayDuocChon == null) return null;
        return Date.from(ngayDuocChon.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Đặt ngày trong BoChonNgay.
     *
     * @param ngay ngày
     */
    public void datNgay(Date ngay) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngay == null) {
            datNgayDuocChon(null);
        } else {
            datNgayDuocChon(ngay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    /**
     * Lấy ngày SQL trong BoChonNgay.
     *
     * @return kết quả lấy ngày SQL
     */
    public java.sql.Date layNgaySQL() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayDuocChon == null) return null;
        return java.sql.Date.valueOf(ngayDuocChon);
    }

    /**
     * Đặt ngày SQL trong BoChonNgay.
     *
     * @param ngay ngày
     */
    public void datNgaySQL(java.sql.Date ngay) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngay == null) {
            datNgayDuocChon(null);
        } else {
            datNgayDuocChon(ngay.toLocalDate());
        }
    }

    /** Xóa trong BoChonNgay. */
    public void xoa() {
        datNgayDuocChon(null);
    }

    /**
     * Đặt được bật trong BoChonNgay.
     *
     * @param duocBat được bật
     */
    @Override
    public void setEnabled(boolean duocBat) {
        super.setEnabled(duocBat);
        txtTruongVanBan.setEnabled(duocBat);
        btnNutLich.setEnabled(duocBat);
    }
}
