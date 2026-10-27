package com.petstation.gui.catalog;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.service.DanhMucService;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho lịch sử giá vùng hiển thị.
 *
 * <p>Sản phẩm ▸ Lịch sử giá: chỉ đọc, tự thêm dòng mỗi khi giá bán đổi ở tab Danh sách.
 */
public final class LichSuGiaPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Mô hình. */
    private final DefaultTableModel moHinh =
            new DefaultTableModel(
                    new String[] {
                        "Thời gian", "Mã SP", "Tên sản phẩm", "Giá cũ", "Giá mới", "Người thay đổi"
                    },
                    0) {
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

    private final javax.swing.table.TableRowSorter<DefaultTableModel> sapXep =
            new javax.swing.table.TableRowSorter<>(moHinh);

    private final javax.swing.JComboBox<String> cboSanPham = new javax.swing.JComboBox<>();
    private final com.petstation.gui.components.common.BoChonKhoangNgay pnlKhoangNgay =
            new com.petstation.gui.components.common.BoChonKhoangNgay();
    private final javax.swing.JLabel lblKetQua = new javax.swing.JLabel();
    private JTable tblLichSu;
    private JTextField txtTuKhoa;
    private java.time.LocalDate tuNgay, denNgay;

    public LichSuGiaPanel() {
        setLayout(new BorderLayout(0, 10));
        // Tìm kiếm.
        JTextField txtTimKiem = taoOTimKiem("Tìm theo mã hoặc tên sản phẩm...");
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        // Bảng.
        JTable tblBang = layBang(scrCuon);
        tblLichSu = tblBang;
        txtTuKhoa = txtTimKiem;
        tblBang.setRowSorter(sapXep);
        txtTimKiem
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                apDungBoLoc();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                apDungBoLoc();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                apDungBoLoc();
                            }
                        });
        cboSanPham.addItem("Tất cả");
        cboSanPham.addActionListener(e -> apDungBoLoc());
        pnlKhoangNgay.datNgayBatDau(java.time.LocalDate.now().minusDays(29));
        pnlKhoangNgay.datNgayKetThuc(java.time.LocalDate.now());
        tuNgay = pnlKhoangNgay.layNgayBatDau();
        denNgay = pnlKhoangNgay.layNgayKetThuc();
        // Trên.
        javax.swing.JPanel pnlTren = taoVungTrongSuot(new BorderLayout(10, 0));
        pnlTren.add(
                com.petstation.gui.components.ThanhDanhSach.tieuDe(
                        taoTieuDeMuc("Lịch sử thay đổi giá bán"), lblKetQua),
                BorderLayout.CENTER);

        javax.swing.JPanel pnlDau = taoVungTrongSuot(new BorderLayout(0, 8));
        com.petstation.gui.components.ThanhDanhSach pnlLoc =
                new com.petstation.gui.components.ThanhDanhSach();
        pnlLoc.add(txtTimKiem);
        pnlLoc.themBoLoc(pnlKhoangNgay);
        cboSanPham.setPreferredSize(new java.awt.Dimension(150, 36));
        pnlLoc.themBoLoc("Sản phẩm:", cboSanPham);
        javax.swing.JButton btnLoc = new javax.swing.JButton("Xem lịch sử");
        btnLoc.addActionListener(
                e -> {
                    if (pnlKhoangNgay.layNgayBatDau() == null
                            || pnlKhoangNgay.layNgayKetThuc() == null
                            || pnlKhoangNgay
                                    .layNgayBatDau()
                                    .isAfter(pnlKhoangNgay.layNgayKetThuc())) {
                        com.petstation.gui.components.HopThoai.hienThiCanhBao(
                                this,
                                "Chọn đủ hai ngày; ngày bắt đầu không được sau ngày kết thúc.");
                        return;
                    }
                    tuNgay = pnlKhoangNgay.layNgayBatDau();
                    denNgay = pnlKhoangNgay.layNgayKetThuc();
                    apDungBoLoc();
                });
        pnlLoc.add(btnLoc);
        javax.swing.JButton btnXoa = new javax.swing.JButton("Xóa bộ lọc");
        btnXoa.addActionListener(
                e -> {
                    tuNgay = null;
                    denNgay = null;
                    cboSanPham.setSelectedIndex(0);
                    txtTimKiem.setText("");
                    apDungBoLoc();
                });
        pnlLoc.themBoLoc(btnXoa);
        javax.swing.JButton btnXuat = new javax.swing.JButton("Xuất Excel");
        btnXuat.addActionListener(
                e ->
                        com.petstation.gui.components.common.XuatExcel.xuatBang(
                                this, tblBang, "Lich_su_gia"));
        pnlLoc.add(btnXuat);

        pnlDau.add(pnlTren, BorderLayout.NORTH);
        pnlDau.add(pnlLoc, BorderLayout.SOUTH);
        add(pnlDau, BorderLayout.NORTH);
        add(scrCuon, BorderLayout.CENTER);
        taiLai();
        apDungBoLoc();
        DanhMucService.themBoLangNgheGia(
                () ->
                        SwingUtilities.invokeLater(
                                () -> {
                                    if (isShowing()
                                            && com.petstation.service.PhienDangNhapService
                                                            .layPhienHienTai()
                                                    != null) taiLai();
                                }));
    }

    private void taiLai() {
        moHinh.setRowCount(0);
        java.util.List<Object[]> cacDong = new java.util.ArrayList<>(DanhMucService.layLichSuGia());

        Object daChon = cboSanPham.getSelectedItem();
        cboSanPham.removeAllItems();
        cboSanPham.addItem("Tất cả");
        cacDong.stream()
                .map(d -> d[1] + " — " + d[2])
                .distinct()
                .sorted()
                .forEach(cboSanPham::addItem);
        if (daChon != null) cboSanPham.setSelectedItem(daChon);
        for (Object[] dongDuLieu : cacDong) {
            moHinh.addRow(
                    new Object[] {
                        dongDuLieu[0],
                        dongDuLieu[1],
                        dongDuLieu[2],
                        String.valueOf(dongDuLieu[3]).isBlank()
                                ? "—"
                                : TienTe.dinhDang(TienTe.doc(String.valueOf(dongDuLieu[3]))),
                        TienTe.dinhDang(TienTe.doc(String.valueOf(dongDuLieu[4]))),
                        dongDuLieu[5]
                    });
        }
    }

    private void apDungBoLoc() {
        if (tblLichSu == null) return;
        sapXep.setRowFilter(
                new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                    public boolean include(
                            Entry<? extends DefaultTableModel, ? extends Integer> dong) {
                        String tu = txtTuKhoa.getText().trim().toLowerCase(java.util.Locale.ROOT);
                        String sanPham = dong.getStringValue(1) + " — " + dong.getStringValue(2);
                        if (cboSanPham.getSelectedIndex() > 0
                                && !sanPham.equals(cboSanPham.getSelectedItem())) return false;
                        if (!sanPham.toLowerCase(java.util.Locale.ROOT).contains(tu)) return false;
                        if (tuNgay != null) {
                            String ngay = dong.getStringValue(0);
                            java.time.LocalDate d;
                            try {
                                d =
                                        java.time.LocalDate.parse(
                                                ngay.substring(0, 10),
                                                java.time.format.DateTimeFormatter.ofPattern(
                                                        "dd/MM/yyyy"));
                            } catch (RuntimeException e) {
                                return false;
                            }
                            if (d.isBefore(tuNgay) || d.isAfter(denNgay)) return false;
                        }
                        return true;
                    }
                });
        lblKetQua.setText(
                com.petstation.gui.components.ThanhDanhSach.soKetQua(
                        tblLichSu.getRowCount(), moHinh.getRowCount()));
    }

    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Lịch sử giá";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chỉ xem. Giá được duyệt tại Phê duyệt yêu cầu sẽ tự ghi lịch sử. Lọc sản phẩm và"
                + " khoảng ngày để tra cứu hoặc xuất Excel.";
    }
}
