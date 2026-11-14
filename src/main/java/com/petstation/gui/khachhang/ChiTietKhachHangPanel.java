package com.petstation.gui.khachhang;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.gui.banhang.InHoaDonPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.khachhang.LichSuKhachHangService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public final class ChiTietKhachHangPanel extends CoSoPanel {
    private static final class DuLieuChiTietKhachHang {
        final List<Object[]> thuCung;
        final List<Object[]> hoaDon;
        final List<Object[]> spa;
        final List<Object[]> luuTru;
        final List<Object[]> voucher;

        DuLieuChiTietKhachHang(
                List<Object[]> thuCung,
                List<Object[]> hoaDon,
                List<Object[]> spa,
                List<Object[]> luuTru,
                List<Object[]> voucher) {
            this.thuCung = thuCung;
            this.hoaDon = hoaDon;
            this.spa = spa;
            this.luuTru = luuTru;
            this.voucher = voucher;
        }
    }

    private final DefaultTableModel moHinhThuCung =
            taoMoHinhChiDoc(
                    new String[] {
                        "Mã thú",
                        "Tên",
                        "Loài",
                        "Giống",
                        "Ngày sinh",
                        "Cân nặng",
                        "Tiêm chủng",
                        "Lưu ý sức khỏe"
                    });
    private final DefaultTableModel moHinhHoaDon =
            taoMoHinhChiDoc(
                    new String[] {"Số hóa đơn", "Ngày", "Tổng tiền", "Thanh toán", "Trạng thái"});
    private final DefaultTableModel moHinhSpa =
            taoMoHinhChiDoc(
                    new String[] {"Mã phiếu", "Thú cưng", "Ngày", "Tổng tiền", "Trạng thái"});
    private final DefaultTableModel moHinhLuuTru =
            taoMoHinhChiDoc(
                    new String[] {
                        "Hợp đồng", "Thú cưng", "Phòng", "Ngày vào", "Ngày ra dự kiến", "Trạng thái"
                    });
    private final DefaultTableModel moHinhVoucher =
            taoMoHinhChiDoc(
                    new String[] {
                        "Mã voucher", "Mệnh giá", "Hạn dùng", "Đã dùng", "Số lượng", "Trạng thái"
                    });

    public ChiTietKhachHangPanel(
            String ma, String ten, String soDienThoai, String diaChi, String hang, String diem) {
        setLayout(new BorderLayout(0, 10));
        add(
                taoTieuDeMuc(
                        ten
                                + " · "
                                + ma
                                + " · "
                                + soDienThoai
                                + " · "
                                + hang
                                + " · "
                                + diem
                                + " điểm"),
                BorderLayout.NORTH);
        JTabbedPane tabCacTab = new JTabbedPane();
        tabCacTab.addTab("Thú cưng", taoBangDinhDang(moHinhThuCung));
        tabCacTab.addTab("Hóa đơn", taoTabHoaDon(taoBangDinhDang(moHinhHoaDon)));
        tabCacTab.addTab("Spa", taoBangDinhDang(moHinhSpa));
        tabCacTab.addTab("Lưu trú", taoBangDinhDang(moHinhLuuTru));
        tabCacTab.addTab("Voucher cá nhân", taoBangDinhDang(moHinhVoucher));
        add(tabCacTab, BorderLayout.CENTER);
        TacVuNen.chayCoPhien(
                this, () -> true, () -> taiDuLieuChiTiet(ma), this::apDungDuLieuChiTiet);
    }

    private static DefaultTableModel taoMoHinhChiDoc(String[] cacCot) {
        return new DefaultTableModel(cacCot, 0) {
            @Override
            public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                return false;
            }
        };
    }

    private static DuLieuChiTietKhachHang taiDuLieuChiTiet(String khachHang) {
        DanhMucService.macDinh().xacNhanTonTai("khach_hang", "ma_khach_hang", khachHang);
        LichSuKhachHangService lichSuService = new LichSuKhachHangService();
        List<Object[]> thuCung =
                lichSuService.layThuCung(khachHang).stream()
                        .map(
                                t ->
                                        new Object[] {
                                            t.maThuCung,
                                            t.ten,
                                            t.loai,
                                            t.giong,
                                            t.ngaySinh,
                                            t.canNang,
                                            t.tiemChung,
                                            t.diUngBenhLy
                                        })
                        .toList();
        List<Object[]> hoaDon =
                lichSuService.layCacHoaDon(khachHang).stream()
                        .map(
                                hd ->
                                        new Object[] {
                                            hd.maHoaDon,
                                            dinhDangGio(hd.thanhToanLuc),
                                            hd.tongTien,
                                            hd.hinhThucThanhToan,
                                            hd.trangThai
                                        })
                        .toList();
        List<Object[]> spa =
                lichSuService.layLichSuSpa(khachHang).stream()
                        .map(
                                s ->
                                        new Object[] {
                                            s.maPhieu,
                                            s.tenThuCung,
                                            dinhDangGio(s.taoLuc),
                                            s.tongTien,
                                            s.trangThai
                                        })
                        .toList();
        return new DuLieuChiTietKhachHang(
                thuCung,
                hoaDon,
                spa,
                taiLuuTru(lichSuService, khachHang),
                taiVoucher(lichSuService, khachHang));
    }

    private static List<Object[]> taiLuuTru(
            LichSuKhachHangService lichSuService, String khachHang) {
        return lichSuService.layLichSuLuuTru(khachHang).stream()
                .map(
                        l ->
                                new Object[] {
                                    l.maHopDong,
                                    l.tenThuCung,
                                    l.maPhong,
                                    dinhDangGio(l.ngayVao),
                                    dinhDangGio(l.ngayRaDuKien),
                                    l.trangThai
                                })
                .toList();
    }

    private static List<Object[]> taiVoucher(
            LichSuKhachHangService lichSuService, String khachHang) {
        return lichSuService.layVoucher(khachHang).stream()
                .map(
                        v ->
                                new Object[] {
                                    v.maVoucher,
                                    v.menhGia,
                                    dinhDangGio(v.hieuLucDen),
                                    v.daDung,
                                    v.soLuong,
                                    v.trangThai
                                })
                .toList();
    }

    private void apDungDuLieuChiTiet(DuLieuChiTietKhachHang duLieu) {
        dienBang(moHinhThuCung, duLieu.thuCung);
        dienBang(moHinhHoaDon, duLieu.hoaDon);
        dienBang(moHinhSpa, duLieu.spa);
        dienBang(moHinhLuuTru, duLieu.luuTru);
        dienBang(moHinhVoucher, duLieu.voucher);
    }

    private static void dienBang(DefaultTableModel moHinh, List<Object[]> cacDong) {
        moHinh.setRowCount(0);
        for (Object[] dong : cacDong) moHinh.addRow(dong);
    }

    private JPanel taoTabHoaDon(JScrollPane scrHoaDonBang) {
        JPanel khungCacDonHang = taoVungTrongSuot(new BorderLayout(0, 8));
        khungCacDonHang.add(scrHoaDonBang, BorderLayout.CENTER);
        NutDinhDang btnXemTruoc = new NutDinhDang("Xem / In hóa đơn", NutDinhDang.KieuNut.PHU);
        btnXemTruoc.addActionListener(suKien -> xemHoaDonChon(scrHoaDonBang));
        khungCacDonHang.add(btnXemTruoc, BorderLayout.SOUTH);
        return khungCacDonHang;
    }

    private void xemHoaDonChon(JScrollPane scrHoaDonBang) {
        JTable bangGiaTriTam = layBang(scrHoaDonBang);
        if (bangGiaTriTam.getSelectedRow() < 0) {
            HopThoai.hienThiCanhBao(this, "Chọn hóa đơn cần xem.");
            return;
        }
        String maHoaDon =
                chuyenThanhChuoi(
                        bangGiaTriTam
                                .getModel()
                                .getValueAt(
                                        bangGiaTriTam.convertRowIndexToModel(
                                                bangGiaTriTam.getSelectedRow()),
                                        0));
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> InHoaDonPanel.hienThi(new BanHangService().layBienLai(maHoaDon)),
                noiDung -> {
                    JTextArea oVanBan = new JTextArea(noiDung, 28, 48);
                    oVanBan.setEditable(false);
                    NutDinhDang btnIn = new NutDinhDang("In", NutDinhDang.KieuNut.CHINH);
                    btnIn.addActionListener(
                            suKien -> {
                                try {
                                    oVanBan.print();
                                } catch (PrinterException ngoaiLe) {
                                    HopThoai.hienThiLoi(this, ngoaiLe.getMessage());
                                }
                            });
                    JPanel khungTam = new JPanel(new BorderLayout());
                    khungTam.add(new JScrollPane(oVanBan), BorderLayout.CENTER);
                    khungTam.add(btnIn, BorderLayout.SOUTH);
                    HopThoai.hienThiNoiDung(this, khungTam, "Hóa đơn " + maHoaDon);
                });
    }

    private static String dinhDangGio(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DinhDang.NGAY_GIO_SQL);
    }
}
