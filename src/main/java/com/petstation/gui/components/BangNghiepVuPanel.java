package com.petstation.gui.components;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.common.XuatExcel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.NghiepVuService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Cung cấp giao diện và xử lý tương tác cho nghiệp vụ bảng vùng hiển thị. */
public abstract class BangNghiepVuPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Kiểu nghiệp vụ. */
    protected final LoaiNghiepVu kieuNghiepVu;

    /** Bán hàng. */
    protected final GioHang banHang;

    /** Điều hướng. */
    protected final Consumer<MaManHinh> dieuHuong;

    /** Mô hình. */
    protected final DefaultTableModel moHinh;

    /** Bảng. */
    protected final JTable tblBang;

    /** Các thao tác. */
    protected final JPanel pnlCacThaoTac;

    protected final JTextField txtTimKiem;

    /** Số lượng. */
    private final JLabel lblSoLuong = new JLabel();

    protected BangNghiepVuPanel(
            LoaiNghiepVu kieuNghiepVu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        this.kieuNghiepVu = kieuNghiepVu;
        this.banHang = banHang;
        this.dieuHuong = dieuHuong;
        // Hiển thị.
        NghiepVuService.DinhNghia hienThi = NghiepVuService.layDinhNghia(kieuNghiepVu);
        setLayout(new BorderLayout(0, 12));
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout());
        pnlTren.add(
                ThanhDanhSach.tieuDe(taoTieuDeMuc(hienThi.tieuDe()), lblSoLuong),
                BorderLayout.NORTH);
        pnlCacThaoTac = new ThanhDanhSach();
        // Tìm kiếm.
        txtTimKiem = taoOTimKiem("Tìm trong danh sách...");
        pnlCacThaoTac.add(txtTimKiem);
        NutDinhDang btnLamMoi = new NutDinhDang("Làm mới", NutDinhDang.KieuNut.TRONG_SUOT);
        btnLamMoi.setToolTipText("Đọc lại dữ liệu mới từ SQL Server");
        btnLamMoi.addActionListener(
                e -> {
                    try {
                        datLaiBoLoc();
                        taiLaiNen();
                    } catch (RuntimeException loi) {
                        HopThoai.hienThiCanhBao(this, loi.getMessage());
                    }
                });
        ((ThanhDanhSach) pnlCacThaoTac).themBoLoc(btnLamMoi);
        pnlTren.add(pnlCacThaoTac, BorderLayout.CENTER);
        add(pnlTren, BorderLayout.NORTH);
        moHinh =
                new DefaultTableModel(hienThi.cacCot(), 0) {
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
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        tblBang = layBang(scrCuon);
        tblBang.removeColumn(tblBang.getColumnModel().getColumn(0));
        if (kieuNghiepVu != LoaiNghiepVu.HOA_DON) lienKetBoLoc(txtTimKiem, tblBang);
        tblBang.getRowSorter()
                .addRowSorterListener(
                        e ->
                                lblSoLuong.setText(
                                        ThanhDanhSach.soKetQua(
                                                tblBang.getRowCount(), moHinh.getRowCount())));
        add(scrCuon, BorderLayout.CENTER);
        // Xuất.
        NutDinhDang btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(suKien -> XuatExcel.xuatBang(this, tblBang, hienThi.tieuDe()));
        pnlCacThaoTac.add(btnXuat);
        tblBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting() || tblBang.getSelectedRow() < 0) return;
                            long id = layDongChon();
                            switch (kieuNghiepVu) {
                                case LICH_HEN -> banHang.layQuyTrinh().chonLichHen(id);
                                case PHIEU_SPA, TIEP_NHAN_SPA, GOI_DICH_VU_SPA, TIEN_DO_SPA, HOAN_TAT_SPA ->
                                        banHang.layQuyTrinh().chonSpa(id);
                                case HOP_DONG_LUU_TRU, DAT_PHONG_LUU_TRU, NHAT_KY_LUU_TRU, TRA_PHONG_LUU_TRU ->
                                        banHang.layQuyTrinh().chonHotel(id);
                                default -> {}
                            }
                            capNhatThaoTac();
                        });
        lamMoi();
    }

    protected void themNut(JPanel pnlVungChua, String nhan, Runnable thaoTac) {
        // Nút.
        NutDinhDang btnNut =
                new NutDinhDang(
                        nhan,
                        nhan.startsWith("+")
                                ? NutDinhDang.KieuNut.CHINH
                                : NutDinhDang.KieuNut.TRONG_SUOT);
        btnNut.addActionListener(
                suKien -> {
                    try {
                        thaoTac.run();
                        lamMoi();
                    } catch (RuntimeException ngoaiLe) {
                        HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
                    }
                });
        pnlVungChua.add(btnNut);
        capNhatThaoTac();
    }

    protected void capNhatThaoTac() {}

    public boolean dangTaiDuLieu() {
        return dangTai;
    }

    private boolean dangTai;

    protected void lamMoi() {
        if (SwingUtilities.isEventDispatchThread()
                && com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()) {
            taiLaiNen();
            return;
        }
        Object[][] duLieu = NghiepVuService.layCacDong(kieuNghiepVu).toArray(Object[][]::new);
        apDungDuLieu(duLieu);
    }

    private void apDungDuLieu(Object[][] duLieu) {
        Long daChon = tblBang.getSelectedRow() < 0 ? null : layDongChon();
        var khoaSapXep = new java.util.ArrayList<>(tblBang.getRowSorter().getSortKeys());
        moHinh.setRowCount(0);
        for (Object[] dong : duLieu) moHinh.addRow(dong);
        tblBang.getRowSorter().setSortKeys(khoaSapXep);

        lblSoLuong.setText(ThanhDanhSach.soKetQua(tblBang.getRowCount(), moHinh.getRowCount()));
        long giu =
                switch (kieuNghiepVu) {
                    case LICH_HEN -> banHang.layQuyTrinh().layLichHen();
                    case PHIEU_SPA, TIEP_NHAN_SPA, GOI_DICH_VU_SPA, TIEN_DO_SPA, HOAN_TAT_SPA ->
                            banHang.layQuyTrinh().layPhieuSpa();
                    case HOP_DONG_LUU_TRU, DAT_PHONG_LUU_TRU, NHAT_KY_LUU_TRU, TRA_PHONG_LUU_TRU ->
                            banHang.layQuyTrinh().layHopDongHotel();
                    default -> 0;
                };
        if (daChon != null) giu = daChon;
        for (int dong = 0; giu > 0 && dong < moHinh.getRowCount(); dong++)
            if (((Number) moHinh.getValueAt(dong, 0)).longValue() == giu) {
                int hienThi = tblBang.convertRowIndexToView(dong);
                if (hienThi >= 0) tblBang.setRowSelectionInterval(hienThi, hienThi);
                break;
            }
        capNhatThaoTac();
        sauKhiTaiLai();
        com.petstation.gui.navigation.NguCanhMoBanGhi.apDung(this);
    }

    protected void sauKhiTaiLai() {}

    protected void datLaiBoLoc() {}

    private int phienTaiLai;

    protected void taiLaiNen() {
        if (!com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()) {
            lamMoi();
            return;
        }
        dangTai = true;
        int token = ++phienTaiLai;
        var session = com.petstation.service.PhienDangNhapService.layPhienHienTai();
        new SwingWorker<Object[][], Void>() {
            protected Object[][] doInBackground() {
                return NghiepVuService.layCacDong(kieuNghiepVu).toArray(Object[][]::new);
            }

            protected void done() {
                if (token != phienTaiLai
                        || com.petstation.service.PhienDangNhapService.layPhienHienTai() != session)
                    return;
                try {
                    apDungDuLieu(get());
                    dangTai = false;
                } catch (Exception ex) {
                    dangTai = false;
                    HopThoai.hienThiCanhBao(
                            BangNghiepVuPanel.this,
                            ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    protected long layDongChon() {
        if (tblBang.getSelectedRow() < 0) throw new IllegalArgumentException("Hãy chọn một dòng.");
        return ((Number)
                        moHinh.getValueAt(
                                tblBang.convertRowIndexToModel(tblBang.getSelectedRow()), 0))
                .longValue();
    }

    protected String layNoiDungDongChon(int cot) {
        layDongChon();
        return chuyenThanhChuoi(
                moHinh.getValueAt(tblBang.convertRowIndexToModel(tblBang.getSelectedRow()), cot));
    }

    protected void hienThiChiTiet(String tieuDe, String[] cacCot, List<Object[]> cacDong) {
        // Chi tiết.
        DefaultTableModel chiTiet =
                new DefaultTableModel(cacDong.toArray(Object[][]::new), cacCot) {
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
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(chiTiet);
        scrCuon.setPreferredSize(new Dimension(800, 320));
        HopThoai.hienThiThongBaoHopThoai(this, scrCuon, tieuDe, JOptionPane.PLAIN_MESSAGE);
    }

    public void xuLyHienThi() {
        lamMoi();
    }

    protected String layNoiDungTroGiup() {
        return "Chọn dòng trước khi xử lý. Các thay đổi được lưu vào SQL Server; nút Làm mới lấy dữ"
                + " liệu mới nhất.";
    }
}
