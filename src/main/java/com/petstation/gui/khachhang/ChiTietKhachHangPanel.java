package com.petstation.gui.khachhang;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dto.ChiTietBienLai;
import com.petstation.gui.banhang.HoaDonDocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.khachhang.LichSuKhachHangService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
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

    private final String maKhachHang;
    private int luotTai;

    public ChiTietKhachHangPanel(
            String ma, String ten, String soDienThoai, String diaChi, String hang, String diem) {
        this.maKhachHang = lamSachChuoi(ma);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout(0, 10));
        add(
                taoTieuDeMuc(taoTieuDeKhachHang(ma, ten, soDienThoai, diaChi, hang, diem)),
                BorderLayout.NORTH);
        JTabbedPane tabCacTab = new JTabbedPane();
        tabCacTab.addTab(
                "Thú cưng",
                taoTabLichSu(taoBangDinhDang(moHinhThuCung), "ThuCung_" + maKhachHang, false));
        tabCacTab.addTab(
                "Hóa đơn",
                taoTabLichSu(taoBangDinhDang(moHinhHoaDon), "HoaDon_" + maKhachHang, true));
        tabCacTab.addTab(
                "Spa", taoTabLichSu(taoBangDinhDang(moHinhSpa), "Spa_" + maKhachHang, false));
        tabCacTab.addTab(
                "Lưu trú",
                taoTabLichSu(taoBangDinhDang(moHinhLuuTru), "LuuTru_" + maKhachHang, false));
        tabCacTab.addTab(
                "Voucher cá nhân",
                taoTabLichSu(taoBangDinhDang(moHinhVoucher), "Voucher_" + maKhachHang, false));
        add(tabCacTab, BorderLayout.CENTER);
        lamMoiDuLieu();
    }

    public void lamMoiDuLieu() {
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> taiDuLieuChiTiet(maKhachHang),
                this::apDungDuLieuChiTiet);
    }

    private static String taoTieuDeKhachHang(
            String ma, String ten, String soDienThoai, String diaChi, String hang, String diem) {
        List<String> phan = new ArrayList<>();
        themNeuCo(phan, ten);
        themNeuCo(phan, ma);
        themNeuCo(phan, soDienThoai);
        themNeuCo(phan, diaChi);
        themNeuCo(phan, hang);
        String diemSach = lamSachChuoi(diem);
        phan.add((diemSach.isEmpty() ? "0" : diemSach) + " điểm");
        return String.join(" · ", phan);
    }

    private static void themNeuCo(List<String> danhSach, String giaTri) {
        String sach = lamSachChuoi(giaTri);
        if (!sach.isEmpty()) danhSach.add(sach);
    }

    private static String lamSachChuoi(String giaTri) {
        if (giaTri == null) return "";
        String cat = giaTri.trim();
        return "null".equalsIgnoreCase(cat) ? "" : cat;
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

    private JPanel taoTabLichSu(JScrollPane scrBang, String tenXuat, boolean laHoaDon) {
        JPanel khung = taoVungTrongSuot(new BorderLayout(0, 8));
        khung.add(scrBang, BorderLayout.CENTER);
        JPanel thanhNut = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        if (laHoaDon) {
            JTable bangHoaDon = layBang(scrBang);
            bangHoaDon.addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 2 && bangHoaDon.getSelectedRow() >= 0) {
                                xemHoaDonChon(scrBang);
                            }
                        }
                    });
            NutDinhDang btnXemTruoc = new NutDinhDang("Xem / In hóa đơn", NutDinhDang.KieuNut.PHU);
            btnXemTruoc.addActionListener(suKien -> xemHoaDonChon(scrBang));
            thanhNut.add(btnXemTruoc);
        }
        NutDinhDang btnXuatExcel = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.PHU);
        btnXuatExcel.addActionListener(
                suKien -> XuatExcelDialog.xuatBang(this, layBang(scrBang), tenXuat));
        thanhNut.add(btnXuatExcel);
        khung.add(thanhNut, BorderLayout.SOUTH);
        return khung;
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
                () -> new BanHangService().layChiTietBienLai(maHoaDon),
                chiTiet -> hienThiHopThoaiHoaDon(maHoaDon, chiTiet));
    }

    private void hienThiHopThoaiHoaDon(String maHoaDon, ChiTietBienLai chiTiet) {
        if (chiTiet == null || chiTiet.getBienLai() == null) {
            HopThoai.hienThiCanhBao(this, "Không tìm thấy chi tiết hóa đơn " + maHoaDon + ".");
            return;
        }
        HoaDonDocPanel khungXem = new HoaDonDocPanel();
        khungXem.datDuLieu(chiTiet);
        JScrollPane scrXem = new JScrollPane(khungXem);
        scrXem.setPreferredSize(new Dimension(440, 520));
        NutDinhDang btnIn = new NutDinhDang("In hóa đơn", NutDinhDang.KieuNut.CHINH);
        btnIn.addActionListener(suKien -> inHoaDon(maHoaDon, chiTiet));
        JPanel khungTam = new JPanel(new BorderLayout(0, 8));
        khungTam.add(scrXem, BorderLayout.CENTER);
        khungTam.add(btnIn, BorderLayout.SOUTH);
        HopThoai.hienThiNoiDung(this, khungTam, "Hóa đơn " + maHoaDon);
    }

    private void inHoaDon(String maHoaDon, ChiTietBienLai chiTiet) {
        try {
            HoaDonDocPanel banIn = new HoaDonDocPanel();
            banIn.datDuLieu(chiTiet);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Hóa đơn " + maHoaDon);
            job.setPrintable(banIn);
            if (!job.printDialog()) return;
            TacVuNen.chay(
                    () -> {
                        job.print();
                        return null;
                    },
                    v -> HopThoai.hienThiThanhCong(this, "Đã gửi lệnh in hóa đơn " + maHoaDon),
                    ngoaiLe -> HopThoai.hienThiLoi(this, TacVuNen.thongDiepLoi(ngoaiLe)));
        } catch (Exception ngoaiLe) {
            HopThoai.hienThiLoi(this, ngoaiLe.getMessage());
        }
    }

    private static String dinhDangGio(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DinhDang.NGAY_GIO);
    }
}
