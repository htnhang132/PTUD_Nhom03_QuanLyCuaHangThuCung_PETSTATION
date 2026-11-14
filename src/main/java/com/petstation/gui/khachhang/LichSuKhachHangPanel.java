package com.petstation.gui.khachhang;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.service.chung.DanhMucService;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
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

    private JTextField oTimKiem;

    private final JLabel nhanSoLuong = new JLabel();

    private final JComboBox<String> cboHangThe = new JComboBox<>(new String[] {"Tất cả"});

    private final JComboBox<String> cboMucDiem =
            new JComboBox<>(
                    new String[] {
                        "Tất cả", "Có điểm (> 0)", "Từ 100 điểm", "Từ 500 điểm", "Từ 1.000 điểm"
                    });

    private boolean dangDatLaiBoLoc;

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
                    lamMoiLuaChonHangThe();
                    apDungBoLoc();
                });
    }

    private void lamMoiLuaChonHangThe() {
        Object daChon = cboHangThe.getSelectedItem();
        List<String> dsHang = new ArrayList<>();
        for (int r = 0; r < moHinh.getRowCount(); r++) {
            Object val = moHinh.getValueAt(r, 4);
            String hang = val == null ? "" : val.toString().trim();
            if (!hang.isEmpty() && !dsHang.contains(hang)) dsHang.add(hang);
        }
        dsHang.sort(
                Comparator.comparingInt(LichSuKhachHangPanel::thuTuHangThe).thenComparing(s -> s));
        List<String> moi = new ArrayList<>();
        moi.add("Tất cả");
        moi.addAll(dsHang);
        dangDatLaiBoLoc = true;
        try {
            cboHangThe.setModel(new DefaultComboBoxModel<>(moi.toArray(String[]::new)));
            cboHangThe.setSelectedItem(daChon != null && moi.contains(daChon) ? daChon : "Tất cả");
        } finally {
            dangDatLaiBoLoc = false;
        }
    }

    private static int thuTuHangThe(String hang) {
        return switch (String.valueOf(hang).trim().toLowerCase(Locale.ROOT)) {
            case "đồng" -> 0;
            case "bạc" -> 1;
            case "vàng" -> 2;
            case "bạch kim" -> 3;
            case "kim cương" -> 4;
            default -> 5;
        };
    }

    private final NutDinhDang btnHienThi =
            new NutDinhDang("Xem lịch sử", NutDinhDang.KieuNut.CHINH);

    private final NutDinhDang btnXuat =
            new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);

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
        oTimKiem = taoOTimKiem("Nhập tên, SĐT hoặc mã khách...");
        btnHienThi.setEnabled(false);
        btnXuat.putClientProperty("luonCuoi", Boolean.TRUE);
        btnXuat.addActionListener(
                suKien -> XuatExcelDialog.xuatBang(this, bangBang, "Lịch sử khách hàng"));
        cboHangThe.addActionListener(
                suKien -> {
                    if (!dangDatLaiBoLoc) apDungBoLoc();
                });
        cboMucDiem.addActionListener(
                suKien -> {
                    if (!dangDatLaiBoLoc) apDungBoLoc();
                });
        ThanhDanhSach khungPhai = new ThanhDanhSach();
        khungPhai.add(oTimKiem);
        khungPhai.themBoLoc("Hạng thẻ:", cboHangThe);
        khungPhai.themBoLoc("Điểm:", cboMucDiem);
        khungPhai.add(btnHienThi);
        khungPhai.add(btnXuat);
        JPanel khungThanh = new JPanel(new BorderLayout(10, 8));
        khungThanh.setOpaque(false);
        JPanel khungTieuDe = new JPanel(new BorderLayout(0, 3));
        khungTieuDe.setOpaque(false);
        khungTieuDe.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh("Lịch sử khách hàng"), nhanSoLuong),
                BorderLayout.NORTH);
        JLabel nhanMoTa =
                new JLabel("Chọn khách hàng để xem thú cưng, hóa đơn, spa, lưu trú và voucher");
        nhanMoTa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanMoTa.setForeground(MauUngDung.VAN_BAN_XAM);
        khungTieuDe.add(nhanMoTa, BorderLayout.SOUTH);
        khungThanh.add(khungTieuDe, BorderLayout.NORTH);
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
        thayBoLangNgheTaiLieu(oTimKiem, this::apDungBoLoc);
        bangBang.getRowSorter().addRowSorterListener(e -> capNhatSoKetQua());
        capNhatSoKetQua();
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

    private void capNhatSoKetQua() {
        if (bangBang != null && moHinh != null) {
            nhanSoLuong.setText(
                    ThanhDanhSach.soKetQua(bangBang.getRowCount(), moHinh.getRowCount()));
        }
    }

    @SuppressWarnings("unchecked")
    private void apDungBoLoc() {
        if (bangBang == null || !(bangBang.getRowSorter() instanceof TableRowSorter<?> raw)) return;
        TableRowSorter<TableModel> boSapXep = (TableRowSorter<TableModel>) raw;
        String tuKhoa = oTimKiem == null ? "" : oTimKiem.getText().trim();
        Pattern mau =
                tuKhoa.isEmpty()
                        ? null
                        : Pattern.compile(
                                Pattern.quote(tuKhoa),
                                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        String hangChon = String.valueOf(cboHangThe.getSelectedItem());
        int mucDiemChon = cboMucDiem.getSelectedIndex();
        boSapXep.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                        if (mau != null && !khopTuKhoa(entry, mau)) return false;
                        if (!"Tất cả".equals(hangChon)
                                && !hangChon.equalsIgnoreCase(entry.getStringValue(4).trim())) {
                            return false;
                        }
                        return khopMucDiem(entry.getValue(5), mucDiemChon);
                    }
                });
        capNhatSoKetQua();
    }

    private static boolean khopTuKhoa(
            RowFilter.Entry<? extends TableModel, ? extends Integer> entry, Pattern mau) {
        for (int i = 0; i < entry.getValueCount(); i++) {
            if (mau.matcher(entry.getStringValue(i)).find()) return true;
        }
        return false;
    }

    private static boolean khopMucDiem(Object giaTriDiem, int mucDiemChon) {
        if (mucDiemChon <= 0) return true;
        long diem = 0;
        if (giaTriDiem != null) {
            try {
                diem = Long.parseLong(String.valueOf(giaTriDiem).trim());
            } catch (NumberFormatException ignored) {
                diem = 0;
            }
        }
        return switch (mucDiemChon) {
            case 1 -> diem > 0;
            case 2 -> diem >= 100;
            case 3 -> diem >= 500;
            case 4 -> diem >= 1000;
            default -> true;
        };
    }

    private void cauHinhSapXepBangKhach() {
        @SuppressWarnings("unchecked")
        TableRowSorter<TableModel> boSapXep = (TableRowSorter<TableModel>) bangBang.getRowSorter();
        boSapXep.setComparator(
                4, Comparator.comparingInt(giaTri -> thuTuHangThe(String.valueOf(giaTri))));
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
    public void xuLyLamMoi() {
        dangDatLaiBoLoc = true;
        try {
            if (oTimKiem != null && !oTimKiem.getText().isEmpty()) oTimKiem.setText("");
            if (cboHangThe.getItemCount() > 0) cboHangThe.setSelectedIndex(0);
            if (cboMucDiem.getItemCount() > 0) cboMucDiem.setSelectedIndex(0);
        } finally {
            dangDatLaiBoLoc = false;
        }
        apDungBoLoc();
        xuLyHienThi();
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
