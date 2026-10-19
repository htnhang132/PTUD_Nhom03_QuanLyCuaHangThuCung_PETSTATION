package com.petstation.gui.reports;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BieuDoCot;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.gui.components.common.XuatExcel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.DoubleFunction;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho báo cáo vùng hiển thị.
 *
 * <p>Màn Thống kê chuẩn: bộ lọc khoảng thời gian, chọn góc nhìn, biểu đồ, bảng số liệu, nút Xuất
 * báo cáo (Excel). Bộ lọc chạy thật: đổi kỳ hoặc góc nhìn là biểu đồ, bảng và thẻ tổng hợp tính lại
 * ngay. Số liệu được truy vấn theo kỳ từ SQL Server.
 */
public class BaoCaoPanel extends CoSoPanel implements HoTroThanhCongCu {
    /**
     * Cung cấp giao diện và xử lý tương tác cho dữ liệu.
     *
     * <p>Kết quả một góc nhìn.
     *
     * @param bieuDoCacNhan biểu đồ các nhãn
     * @param bieuDoCacGiaTri biểu đồ các giá trị
     * @param tienTe tiền tệ
     * @param donVi đơn vị
     * @param cacCot các cột
     * @param cacDong các dòng
     * @param theCacTieuDe thẻ các tiêu đề
     * @param theCacGiaTri thẻ các giá trị
     */
    public record DuLieu(
            String[] bieuDoCacNhan,
            double[] bieuDoCacGiaTri,
            boolean tienTe,
            String donVi,
            String[] cacCot,
            Object[][] cacDong,
            String[] theCacTieuDe,
            String[] theCacGiaTri) {}

    /**
     * Cung cấp giao diện và xử lý tương tác cho hiển thị.
     *
     * @param ten tên
     * @param tao tạo
     */
    public record KieuHienThi(String ten, DoubleFunction<DuLieu> tao) {}

    /** Các kỳ. */
    private static final String[] CAC_KY = {
        "Hôm nay", "7 ngày qua", "30 ngày qua", "Tháng này", "Năm nay"
    };

    /** Số ngày. */
    private static final int[] SO_NGAY = {1, 7, 30, 0, 0};

    /** Các kiểu hiển thị. */
    private final List<KieuHienThi> cacKieuHienThi;

    /** Kỳ. */
    private final JComboBox<String> cboKy = new JComboBox<>(CAC_KY);

    /** Hiển thị hộp. */
    private final JComboBox<String> cboHienThiHop = new JComboBox<>();

    /** Các thẻ. */
    private final JPanel pnlCacThe = new JPanel(new GridLayout(1, 4, 12, 0));

    /** Biểu đồ. */
    private final BieuDoCot bieuDo = new BieuDoCot();

    /** Mô hình. */
    private final DefaultTableModel moHinh = new DefaultTableModel();

    /** Bảng. */
    private final JTable tblBang = new JTable(moHinh);

    /** Khoảng. */
    private final JLabel lblKhoang = new JLabel();

    /** Tên xuất. */
    private final String tenXuat;

    /**
     * Khởi tạo BaoCaoPanel với các giá trị được cung cấp.
     *
     * @param tieuDe tiêu đề
     * @param tieuDePhu tiêu đề phụ
     * @param tenXuat tên xuất
     * @param cacKieuHienThi các kiểu hiển thị
     */
    public BaoCaoPanel(
            String tieuDe, String tieuDePhu, String tenXuat, List<KieuHienThi> cacKieuHienThi) {
        this.cacKieuHienThi = cacKieuHienThi;
        this.tenXuat = tenXuat;
        setLayout(new BorderLayout(0, 12));

        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc(tieuDe);
        // Phần phụ.
        JLabel lblPhanPhu = new JLabel(tieuDePhu);
        lblPhanPhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblPhanPhu.setForeground(MauUngDung.VAN_BAN_XAM);
        // Tiêu đề hộp.
        JPanel pnlTieuDeHop = taoVungTrongSuot(new BorderLayout(0, 2));
        pnlTieuDeHop.add(lblTieuDe, BorderLayout.NORTH);
        pnlTieuDeHop.add(lblPhanPhu, BorderLayout.SOUTH);

        for (KieuHienThi giaTri : cacKieuHienThi) cboHienThiHop.addItem(giaTri.ten());
        cboKy.setPreferredSize(new Dimension(140, 34));
        cboHienThiHop.setPreferredSize(new Dimension(190, 34));
        cboKy.addActionListener(suKien -> dungLai());
        cboHienThiHop.addActionListener(suKien -> dungLai());
        lblKhoang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblKhoang.setForeground(MauUngDung.VAN_BAN_XAM);
        // Xuất.
        NutDinhDang btnXuat = new NutDinhDang("Xuất báo cáo (Excel)", NutDinhDang.KieuNut.CHINH);
        btnXuat.addActionListener(suKien -> XuatExcel.xuatBang(this, tblBang, tenXuat));

        // Các bộ lọc.
        JPanel pnlCacBoLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlCacBoLoc.setOpaque(false);
        pnlCacBoLoc.add(new JLabel("Kỳ:"));
        pnlCacBoLoc.add(cboKy);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (cacKieuHienThi.size() > 1) {
            pnlCacBoLoc.add(new JLabel("Xem theo:"));
            pnlCacBoLoc.add(cboHienThiHop);
        }
        pnlCacBoLoc.add(lblKhoang);
        pnlCacBoLoc.add(btnXuat);

        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(10, 8));
        pnlTren.add(pnlTieuDeHop, BorderLayout.NORTH);
        pnlTren.add(pnlCacBoLoc, BorderLayout.SOUTH);
        pnlCacThe.setOpaque(false);
        // Trên.
        JPanel pnlTren2 = taoVungTrongSuot(new BorderLayout(0, 10));
        pnlTren2.add(pnlTren, BorderLayout.NORTH);
        pnlTren2.add(pnlCacThe, BorderLayout.SOUTH);
        add(pnlTren2, BorderLayout.NORTH);

        // Biểu đồ thẻ.
        JPanel pnlBieuDoThe = new JPanel(new BorderLayout());
        pnlBieuDoThe.setBackground(Color.WHITE);
        pnlBieuDoThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(10, 12, 6, 12)));
        pnlBieuDoThe.add(bieuDo, BorderLayout.CENTER);
        pnlBieuDoThe.setPreferredSize(new Dimension(0, 230));

        DinhDangBang.apDung(tblBang);
        // Cuộn.
        JScrollPane scrCuon = new JScrollPane(tblBang);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.getViewport().setBackground(Color.WHITE);
        // Giữa.
        JPanel pnlGiua = taoVungTrongSuot(new BorderLayout(0, 10));
        pnlGiua.add(pnlBieuDoThe, BorderLayout.NORTH);
        pnlGiua.add(scrCuon, BorderLayout.CENTER);
        add(pnlGiua, BorderLayout.CENTER);
        dungLai();
    }

    /**
     * Lấy tỷ lệ trong BaoCaoPanel.
     *
     * @return kết quả lấy tỷ lệ
     */
    private double layTyLe() {
        // Chỉ số.
        int chiSo = cboKy.getSelectedIndex();
        // Hôm nay.
        LocalDate homNay = LocalDate.now();
        // Số ngày.
        int soNgay = SO_NGAY[chiSo];
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (chiSo == 3) soNgay = homNay.getDayOfMonth();
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (chiSo == 4) soNgay = homNay.getDayOfYear();
        // Trường đang xét.
        DateTimeFormatter truongDangXet = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblKhoang.setText(
                soNgay <= 1
                        ? truongDangXet.format(homNay)
                        : truongDangXet.format(homNay.minusDays(soNgay - 1L))
                                + " – "
                                + truongDangXet.format(homNay));
        return soNgay;
    }

    /** Dựng lại trong BaoCaoPanel. */
    private void dungLai() {
        // Số ngày.
        double soNgay = layTyLe();
        // Chỉ số.
        int chiSo = Math.max(0, cboHienThiHop.getSelectedIndex());
        // Ngày đang xét.
        DuLieu ngayDangXet = cacKieuHienThi.get(chiSo).tao().apply(soNgay);
        bieuDo.datDuLieu(
                ngayDangXet.bieuDoCacNhan(),
                ngayDangXet.bieuDoCacGiaTri(),
                ngayDangXet.tienTe(),
                ngayDangXet.donVi());
        moHinh.setDataVector(ngayDangXet.cacDong(), ngayDangXet.cacCot());
        DinhDangBang.apDung(tblBang);
        pnlCacThe.removeAll();
        // Các tông màu.
        Color[] cacTongMau = {
            MauUngDung.XANH_DUONG, MauUngDung.XANH_LA, MauUngDung.CAM, MauUngDung.DO_THUONG_HIEU
        };
        for (int chiSo2 = 0; chiSo2 < ngayDangXet.theCacTieuDe().length; chiSo2++)
            pnlCacThe.add(
                    layChiSoThe(
                            ngayDangXet.theCacTieuDe()[chiSo2],
                            ngayDangXet.theCacGiaTri()[chiSo2],
                            "",
                            cacTongMau[chiSo2 % cacTongMau.length]));
        pnlCacThe.revalidate();
        pnlCacThe.repaint();
    }

    /** Xử lý hiển thị trong BaoCaoPanel. */
    @Override
    public void xuLyHienThi() {
        dungLai();
    }

    /**
     * Lấy tiêu đề trợ giúp trong BaoCaoPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Thống kê";
    }

    /**
     * Lấy nội dung trợ giúp trong BaoCaoPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn kỳ và góc nhìn: biểu đồ và bảng tự cập nhật. Bấm \"Xuất báo cáo\" để lưu"
                + " Excel.\n"
                + "Màn Thống kê chỉ xem và xuất, không sửa dữ liệu.";
    }
}
