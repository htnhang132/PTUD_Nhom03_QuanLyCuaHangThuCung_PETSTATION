package com.petstation.gui.components;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.components.common.XuatExcel;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
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
    private final DefaultTableModel moHinh;

    /** Bảng. */
    protected final JTable tblBang;

    /** Các thao tác. */
    protected final JPanel pnlCacThaoTac;

    /** Số lượng. */
    private final JLabel lblSoLuong = new JLabel();

    /**
     * Khởi tạo BangNghiepVuPanel với các giá trị được cung cấp.
     *
     * @param kieuNghiepVu kiểu nghiệp vụ
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
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
        pnlTren.add(taoTieuDeMuc(hienThi.tieuDe()), BorderLayout.NORTH);
        pnlCacThaoTac = taoVungTrongSuot(new GridLayout(0, 4, 8, 6));
        // Tìm kiếm.
        JTextField txtTimKiem = taoOTimKiem("Tìm trong danh sách...");
        pnlCacThaoTac.add(txtTimKiem);
        themNut(pnlCacThaoTac, "Tải lại", this::lamMoi);
        pnlCacThaoTac.add(lblSoLuong);
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
        lienKetBoLoc(txtTimKiem, tblBang);
        add(scrCuon, BorderLayout.CENTER);
        // Xuất.
        NutDinhDang btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(suKien -> XuatExcel.xuatBang(this, tblBang, hienThi.tieuDe()));
        // Chân trang.
        JPanel pnlChanTrang = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT));
        pnlChanTrang.add(btnXuat);
        add(pnlChanTrang, BorderLayout.SOUTH);
        lamMoi();
    }

    /**
     * Thêm nút trong BangNghiepVuPanel.
     *
     * @param pnlVungChua vùng chứa
     * @param nhan nhãn
     * @param thaoTac thao tác
     */
    protected void themNut(JPanel pnlVungChua, String nhan, Runnable thaoTac) {
        // Nút.
        NutDinhDang btnNut = new NutDinhDang(nhan, NutDinhDang.KieuNut.PHU);
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
    }

    /** Làm mới trong BangNghiepVuPanel. */
    protected void lamMoi() {
        moHinh.setRowCount(0);
        for (Object[] dong : NghiepVuService.layCacDong(kieuNghiepVu)) moHinh.addRow(dong);
        lblSoLuong.setText("  " + moHinh.getRowCount() + " bản ghi");
    }

    /**
     * Lấy dòng chọn trong BangNghiepVuPanel.
     *
     * @return kết quả lấy dòng chọn
     */
    protected long layDongChon() {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (tblBang.getSelectedRow() < 0) throw new IllegalArgumentException("Hãy chọn một dòng.");
        return ((Number)
                        moHinh.getValueAt(
                                tblBang.convertRowIndexToModel(tblBang.getSelectedRow()), 0))
                .longValue();
    }

    /**
     * Lấy nội dung dòng chọn trong BangNghiepVuPanel.
     *
     * @param cot cột
     * @return kết quả lấy nội dung dòng chọn
     */
    protected String layNoiDungDongChon(int cot) {
        layDongChon();
        return chuyenThanhChuoi(
                moHinh.getValueAt(tblBang.convertRowIndexToModel(tblBang.getSelectedRow()), cot));
    }

    /**
     * Hiển thị chi tiết trong BangNghiepVuPanel.
     *
     * @param tieuDe tiêu đề
     * @param cacCot các cột
     * @param cacDong các dòng
     */
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

    /** Xử lý hiển thị trong BangNghiepVuPanel. */
    public void xuLyHienThi() {
        lamMoi();
    }

    /**
     * Lấy nội dung trợ giúp trong BangNghiepVuPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    protected String layNoiDungTroGiup() {
        return "Chọn dòng trước khi xử lý. Các thay đổi được lưu vào SQL Server; nút Tải lại lấy dữ"
                + " liệu mới nhất.";
    }
}
