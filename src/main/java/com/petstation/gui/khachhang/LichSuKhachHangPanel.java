package com.petstation.gui.khachhang;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.service.chung.DanhMucService;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.IntFunction;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Lịch sử giao dịch khách hàng (Mã: LICH_SU_KHACH_HANG)
// Luồng xử lý: GUI -> KhachHangService -> KhachHangDAOImpl, HoaDonDAOImpl -> SQL (khach_hang,
// hoa_don, thu_cung)
// Mô tả: Xem toàn bộ lịch sử mua sắm, sử dụng dịch vụ và thú cưng đăng ký của khách hàng
// Phân hệ: khachhang
public final class LichSuKhachHangPanel extends CoSoPanel {
    private static final String CHON = "pick";

    private static final String CHI_TIET = "detail";

    private final CardLayout cacThe = new CardLayout();

    private final JPanel khungGoc = new JPanel(cacThe);

    private final JPanel khungChiTietVungChua = new JPanel(new BorderLayout());

    private JTable bangBang;

    private DefaultTableModel moHinh;

    private int luotTai;

    private void taiLaiCacKhachHangNen() {
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> DanhMucService.macDinh().layKhachHang(),
                cacKhachHang -> {
                    moHinh.setRowCount(0);
                    for (Object[] dongDuLieu : cacKhachHang) {
                        moHinh.addRow(
                                new Object[] {
                                    dongDuLieu[0],
                                    dongDuLieu[1],
                                    dongDuLieu[2],
                                    dongDuLieu[4],
                                    dongDuLieu[6],
                                    dongDuLieu[7]
                                });
                    }
                });
    }

    private final NutDinhDang btnHienThi =
            new NutDinhDang("Xem lịch sử", NutDinhDang.KieuNut.CHINH);

    public LichSuKhachHangPanel() {
        setLayout(new BorderLayout());
        khungGoc.setOpaque(false);
        khungGoc.add(taoVungChonKhachHang(), CHON);
        khungGoc.add(taoVungChiTietKhachHang(), CHI_TIET);
        add(khungGoc, BorderLayout.CENTER);
    }

    private JPanel taoVungChonKhachHang() {
        JPanel khungChon = new JPanel(new BorderLayout(0, 12));
        khungChon.setOpaque(false);
        JTextField oTimKiem = taoOTimKiem("Nhập tên, SĐT hoặc mã khách...");
        btnHienThi.setEnabled(false);
        JPanel khungPhai = new ThanhDanhSach();
        khungPhai.setOpaque(false);
        khungPhai.add(oTimKiem);
        khungPhai.add(btnHienThi);
        JPanel khungThanh = new JPanel(new BorderLayout(10, 0));
        khungThanh.setOpaque(false);
        khungThanh.add(taoTieuDeMuc("Chọn khách hàng cần xem lịch sử"), BorderLayout.NORTH);
        khungThanh.add(khungPhai, BorderLayout.CENTER);
        khungChon.add(khungThanh, BorderLayout.NORTH);
        JScrollPane scrCuon = taoBangKhachHang(oTimKiem);
        khungChon.add(scrCuon, BorderLayout.CENTER);
        JLabel nhanGoiY =
                new JLabel("Bấm 2 lần vào một dòng hoặc chọn dòng rồi bấm \"Xem lịch sử\".");
        nhanGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        khungChon.add(nhanGoiY, BorderLayout.SOUTH);
        return khungChon;
    }

    private JScrollPane taoBangKhachHang(JTextField oTimKiem) {
        String[] cacCot = {"Mã KH", "Họ và tên", "Số điện thoại", "Địa chỉ", "Hạng thẻ", "Điểm"};
        DefaultTableModel moHinhMoi = DinhDangBang.taoMoHinhChiDoc(cacCot);
        this.moHinh = moHinhMoi;
        taiLaiCacKhachHangNen();
        JScrollPane scrCuon = taoBangDinhDang(moHinhMoi);
        bangBang = layBang(scrCuon);
        DinhDangBang.apDung(bangBang);
        cauHinhSapXepBangKhach();
        lienKetBoLoc(oTimKiem, bangBang);
        bangBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> btnHienThi.setEnabled(bangBang.getSelectedRow() >= 0));
        btnHienThi.addActionListener(suKien -> moDaChon());
        bangBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (suKien.getClickCount() == 2 && bangBang.getSelectedRow() >= 0)
                            moDaChon();
                    }
                });
        return scrCuon;
    }

    private void cauHinhSapXepBangKhach() {
        @SuppressWarnings("unchecked")
        TableRowSorter<TableModel> boSapXep = (TableRowSorter<TableModel>) bangBang.getRowSorter();
        boSapXep.setComparator(
                4,
                Comparator.comparingInt(
                        giaTri ->
                                switch (String.valueOf(giaTri).trim().toLowerCase(Locale.ROOT)) {
                                    case "đồng" -> 0;
                                    case "bạc" -> 1;
                                    case "vàng" -> 2;
                                    case "bạch kim" -> 3;
                                    case "kim cương" -> 4;
                                    default -> 5;
                                }));
        boSapXep.setComparator(
                5,
                Comparator.comparingLong(giaTri -> Long.parseLong(String.valueOf(giaTri).trim())));
    }

    private JPanel taoVungChiTietKhachHang() {
        JPanel khungChiTiet = new JPanel(new BorderLayout(0, 8));
        khungChiTiet.setOpaque(false);
        NutDinhDang btnQuayLai = new NutDinhDang("Chọn khách khác", NutDinhDang.KieuNut.TRONG_SUOT);
        btnQuayLai.addActionListener(suKien -> cacThe.show(khungGoc, CHON));
        JPanel khungQuayLaiThanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        khungQuayLaiThanh.setOpaque(false);
        khungQuayLaiThanh.setBorder(new EmptyBorder(0, 0, 4, 0));
        khungQuayLaiThanh.add(btnQuayLai);
        khungChiTiet.add(khungQuayLaiThanh, BorderLayout.NORTH);
        khungChiTietVungChua.setOpaque(false);
        khungChiTiet.add(khungChiTietVungChua, BorderLayout.CENTER);
        return khungChiTiet;
    }

    private void moDaChon() {
        int dong = bangBang.getSelectedRow();
        if (dong < 0) return;
        int moHinhDangXet = bangBang.convertRowIndexToModel(dong);
        IntFunction<String> giaTri =
                giaTriDangXet ->
                        String.valueOf(
                                bangBang.getModel().getValueAt(moHinhDangXet, giaTriDangXet));
        khungChiTietVungChua.removeAll();
        khungChiTietVungChua.add(
                new ChiTietKhachHangPanel(
                        giaTri.apply(0),
                        giaTri.apply(1),
                        giaTri.apply(2),
                        giaTri.apply(3),
                        giaTri.apply(4),
                        giaTri.apply(5)),
                BorderLayout.CENTER);
        khungChiTietVungChua.revalidate();
        khungChiTietVungChua.repaint();
        cacThe.show(khungGoc, CHI_TIET);
    }

    @Override
    public void xuLyHienThi() {
        taiLaiCacKhachHangNen();
        cacThe.show(khungGoc, CHON);
        bangBang.clearSelection();
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
