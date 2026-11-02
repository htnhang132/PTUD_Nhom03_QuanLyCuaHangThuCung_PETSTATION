package com.petstation.gui.search;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.DinhDangBang;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho lịch sử khách hàng vùng hiển thị.
 *
 * <p>Tìm kiếm ▸ Lịch sử khách hàng. Bước 1: tìm và chọn khách. Bước 2: xem hóa đơn, phiếu Spa, lưu
 * trú, thú cưng, điểm của khách đó (chỉ đọc).
 */
public final class LichSuKhachHangPanel extends CoSoPanel {
    /** Chọn. */
    private static final String CHON = "pick";

    /** Chi tiết. */
    private static final String CHI_TIET = "detail";

    /** Các thẻ. */
    private final CardLayout cacThe = new CardLayout();

    /** Gốc. */
    private final JPanel pnlGoc = new JPanel(cacThe);

    /** Chi tiết vùng chứa. */
    private final JPanel pnlChiTietVungChua = new JPanel(new BorderLayout());

    /** Bảng. */
    private final JTable tblBang;

    /** Mô hình. */
    private DefaultTableModel moHinh;

    private static void taiLaiCacKhachHang(DefaultTableModel moHinhDangXet) {
        moHinhDangXet.setRowCount(0);
        for (Object[] dongDuLieu : com.petstation.service.DanhMucService.layKhachHang()) {
            moHinhDangXet.addRow(
                    new Object[] {
                        dongDuLieu[0],
                        dongDuLieu[1],
                        dongDuLieu[2],
                        dongDuLieu[4],
                        dongDuLieu[6],
                        dongDuLieu[7]
                    });
        }
    }

    /** Hiển thị. */
    private final NutDinhDang btnHienThi =
            new NutDinhDang("Xem lịch sử ▸", NutDinhDang.KieuNut.CHINH);

    public LichSuKhachHangPanel() {
        setLayout(new BorderLayout());
        pnlGoc.setOpaque(false);

        // ── Bước 1: chọn khách
        // Chọn.
        JPanel pnlChon = new JPanel(new BorderLayout(0, 12));
        pnlChon.setOpaque(false);
        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Chọn khách hàng cần xem lịch sử");
        // Tìm kiếm.
        JTextField txtTimKiem = taoOTimKiem("Nhập tên, SĐT hoặc mã khách...");
        btnHienThi.setEnabled(false);
        // Thanh.
        JPanel pnlThanh = new JPanel(new BorderLayout(10, 0));
        pnlThanh.setOpaque(false);
        pnlThanh.add(lblTieuDe, BorderLayout.NORTH);
        // Phải.
        JPanel pnlPhai = new com.petstation.gui.components.ThanhDanhSach();
        pnlPhai.setOpaque(false);
        pnlPhai.add(txtTimKiem);
        pnlPhai.add(btnHienThi);
        pnlThanh.add(pnlPhai, BorderLayout.CENTER);
        pnlChon.add(pnlThanh, BorderLayout.NORTH);

        // Các cột.
        String[] cacCot = {"Mã KH", "Họ và tên", "Số điện thoại", "Địa chỉ", "Hạng thẻ", "Điểm"};
        // Mô hình.
        DefaultTableModel moHinh =
                new DefaultTableModel(cacCot, 0) {
                    /**
                     * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                     *
                     * @param dongDuLieu dòng dữ liệu
                     * @param giaTriDangXet giá trị đang xét
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                        return false;
                    }
                };
        taiLaiCacKhachHang(moHinh);
        this.moHinh = moHinh;
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        tblBang = layBang(scrCuon);
        DinhDangBang.apDung(tblBang);
        lienKetBoLoc(txtTimKiem, tblBang);
        pnlChon.add(scrCuon, BorderLayout.CENTER);
        // Gợi ý.
        JLabel lblGoiY =
                new JLabel("Bấm 2 lần vào một dòng hoặc chọn dòng rồi bấm \"Xem lịch sử\".");
        lblGoiY.setFont(MauUngDung.taoPhongChu(java.awt.Font.PLAIN, 12));
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlChon.add(lblGoiY, BorderLayout.SOUTH);

        tblBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> btnHienThi.setEnabled(tblBang.getSelectedRow() >= 0));
        btnHienThi.addActionListener(suKien -> moDaChon());
        tblBang.addMouseListener(
                new MouseAdapter() {
                    /**
                     * Lấy chuột đã bấm trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (suKien.getClickCount() == 2 && tblBang.getSelectedRow() >= 0)
                            moDaChon();
                    }
                });

        // ── Bước 2: chi tiết
        // Chi tiết.
        JPanel pnlChiTiet = new JPanel(new BorderLayout(0, 8));
        pnlChiTiet.setOpaque(false);
        // Quay lại.
        NutDinhDang btnQuayLai =
                new NutDinhDang("◀ Chọn khách khác", NutDinhDang.KieuNut.TRONG_SUOT);
        btnQuayLai.addActionListener(suKien -> cacThe.show(pnlGoc, CHON));
        // Quay lại thanh.
        JPanel pnlQuayLaiThanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlQuayLaiThanh.setOpaque(false);
        pnlQuayLaiThanh.setBorder(new EmptyBorder(0, 0, 4, 0));
        pnlQuayLaiThanh.add(btnQuayLai);
        pnlChiTiet.add(pnlQuayLaiThanh, BorderLayout.NORTH);
        pnlChiTiet.add(pnlChiTietVungChua, BorderLayout.CENTER);
        pnlChiTietVungChua.setOpaque(false);

        pnlGoc.add(pnlChon, CHON);
        pnlGoc.add(pnlChiTiet, CHI_TIET);
        add(pnlGoc, BorderLayout.CENTER);
    }

    private void moDaChon() {
        // Dòng.
        int dong = tblBang.getSelectedRow();
        if (dong < 0) return;
        // Mô hình đang xét.
        int moHinhDangXet = tblBang.convertRowIndexToModel(dong);
        java.util.function.IntFunction<String> giaTri =
                giaTriDangXet ->
                        String.valueOf(tblBang.getModel().getValueAt(moHinhDangXet, giaTriDangXet));
        pnlChiTietVungChua.removeAll();
        pnlChiTietVungChua.add(
                new ChiTietKhachHangPanel(
                        giaTri.apply(0),
                        giaTri.apply(1),
                        giaTri.apply(2),
                        giaTri.apply(3),
                        giaTri.apply(4),
                        giaTri.apply(5)),
                BorderLayout.CENTER);
        pnlChiTietVungChua.revalidate();
        pnlChiTietVungChua.repaint();
        cacThe.show(pnlGoc, CHI_TIET);
    }

    @Override
    public void xuLyHienThi() {
        taiLaiCacKhachHang(moHinh);
        cacThe.show(pnlGoc, CHON);
        tblBang.clearSelection();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Lịch sử khách hàng";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Bước 1: tìm và chọn khách hàng.\n"
                + "Bước 2: xem hóa đơn, phiếu Spa, thú cưng, voucher của khách (chỉ đọc).";
    }
}
