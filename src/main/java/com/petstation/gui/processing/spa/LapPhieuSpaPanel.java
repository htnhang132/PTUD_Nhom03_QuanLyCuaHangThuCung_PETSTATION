package com.petstation.gui.processing.spa;

import com.petstation.gui.catalog.DanhMucKhachHangPanel;
import com.petstation.gui.catalog.DanhMucThuCungPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL.LuaChon;
import com.petstation.model.Quyen;
import com.petstation.model.PhieuSpaTreo;
import com.petstation.service.PhieuSpaTreoService;
import com.petstation.service.DanhMucService;
import com.petstation.service.PhienDangNhapService;
import com.petstation.service.LapPhieuSpaService;
import com.petstation.service.LuaChonNghiepVuService;
import com.petstation.service.QuyTrinhService;
import com.petstation.service.SpaService;
import com.petstation.service.TiemPhongService;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.util.TienTe;
import java.util.function.LongConsumer;

import java.awt.*;
import java.util.List;
import javax.swing.*;

public final class LapPhieuSpaPanel extends CoSoPanel {
    private final JTextField txtSoDienThoai = new JTextField(16);
    private final JLabel lblKhachHang = new JLabel("Chưa chọn khách hàng");
    private final JComboBox<LuaChon> cboThuCung = new JComboBox<>();
    private final JTextField txtCanNang = new JTextField();
    private final JTextArea txtGhiChu = new JTextArea(3, 28);
    private Object[] khachHang;
    private final JComboBox<LuaChon> cboDichVu = new JComboBox<>();
    private final JCheckBox chkSucKhoe = new JCheckBox("Đã kiểm tra thực tế, đủ điều kiện tiếp nhận");
    private final JLabel lblGia = new JLabel("Chọn dịch vụ và cân nặng để xem giá");
    private final JLabel lblNguon = new JLabel("Khách đến trực tiếp");
    private final JTextArea txtYeuCau = new JTextArea(2, 28);
    private final LongConsumer khiLuu;
    private final Runnable khiTreo;
    private long idPhieuTreo;
    private String maHen = "";

    public LapPhieuSpaPanel(LongConsumer khiLuu, Runnable chonLich, Runnable khiTreo) {
        this.khiLuu = khiLuu;
        this.khiTreo = khiTreo;
        JPanel bieuMau = new JPanel(new GridBagLayout());
        bieuMau.setOpaque(false);
        lblNguon.setForeground(MauUngDung.NAU);
        chkSucKhoe.setOpaque(false);
        JPanel timKhach = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        timKhach.add(txtSoDienThoai);
        timKhach.add(nut("Tìm khách", this::timKhach));
        JButton themKhach = nut("Thêm khách hàng", this::themKhach);
        themKhach.setEnabled(PhienDangNhapService.layPhien().kiemTraCo(Quyen.QUAN_LY_KHACH_HANG));
        timKhach.add(themKhach);
        txtSoDienThoai.addActionListener(e -> anToan(this::timKhach));
        themDong(bieuMau, "Nguồn tiếp nhận", lblNguon);
        themDong(bieuMau, "", nut("Khách có hẹn — Chọn lịch hẹn", chonLich));
        themDong(bieuMau, "Số điện thoại", timKhach);

        themDong(bieuMau, "Khách hàng", lblKhachHang);

        themDong(bieuMau, "Thú cưng", cboThuCung);
        JButton hoSo = nut("Hồ sơ / Thêm thú cưng", this::moHoSoThuCung);
        hoSo.setEnabled(PhienDangNhapService.layPhien().kiemTraCo(Quyen.QUAN_LY_THU_CUNG));
        themDong(bieuMau, "Hồ sơ thú cưng", hoSo);
        themDong(bieuMau, "Tuổi / Tiêm phòng", nut("Kiểm tra hồ sơ", () -> {
            TiemPhongService.kiemTraNhanSpa(thuDaChon().giaTri());
            HopThoai.hienThiThongTin(this, "Đạt điều kiện hồ sơ. Tiếp tục kiểm tra sức khỏe thực tế.");
        }));
        themDong(bieuMau, "Sức khỏe thực tế", chkSucKhoe);

        themDong(bieuMau, "Cân nặng thực tế (kg)", txtCanNang);

        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        themDong(bieuMau, "Tình trạng khi nhận", new JScrollPane(txtGhiChu));
        txtYeuCau.setLineWrap(true);
        txtYeuCau.setWrapStyleWord(true);
        themDong(bieuMau, "Yêu cầu / Vật dụng", new JScrollPane(txtYeuCau));
        for (Object[] goi : LuaChonNghiepVuService.layLuaChonGoiDichVu())
            cboDichVu.addItem(new LuaChon(String.valueOf(goi[0]), String.valueOf(goi[1])));
        cboDichVu.setSelectedIndex(-1);
        themDong(bieuMau, "Dịch vụ", cboDichVu);
        themDong(bieuMau, "Giá dự kiến", lblGia);
        themDong(bieuMau, "", nut("Xem giá", this::xemGia));
        cboDichVu.addActionListener(e -> lblGia.setText("Nhấn Xem giá để cập nhật"));
        txtCanNang.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { doiCanNang(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { doiCanNang(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { doiCanNang(); }
            private void doiCanNang() { lblGia.setText("Nhấn Xem giá để cập nhật"); }
        });
        cboThuCung.addActionListener(e -> {
            chkSucKhoe.setSelected(false);
            lblGia.setText("Nhấn Xem giá để cập nhật");
        });
        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        thaoTac.setOpaque(false);
        thaoTac.add(nut("Phiếu đang treo", this::moDanhSachPhieuTreo));
        thaoTac.add(nut("Treo phiếu", this::treoPhieu));
        thaoTac.add(nut("Lập phiếu mới", this::xoaForm));
        thaoTac.add(nut("Tạo phiếu tiếp nhận", this::luu));
        JScrollPane cuon = new JScrollPane(bieuMau);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.getVerticalScrollBar().setUnitIncrement(18);
        add(cuon, BorderLayout.CENTER);
        add(thaoTac, BorderLayout.SOUTH);
    }

    private void themDong(JPanel panel, String nhan, Component dauVao) {
        int dong = panel.getComponentCount() / 2;
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = dong;
        c.insets = new Insets(7, 6, 7, 6);
        c.anchor = GridBagConstraints.NORTHWEST;
        JLabel label = new JLabel(nhan);
        label.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        label.setForeground(MauUngDung.VAN_BAN_TOI);
        panel.add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dauVao, c);
    }

    public void xoaForm() {
        if (chupNhap().coNoiDung() && !HopThoai.xacNhan(this, "Bỏ nội dung đang nhập để lập phiếu mới? Phiếu đã treo vẫn được giữ.")) return;
        datLaiForm();
    }

    private void datLaiForm() {
        idPhieuTreo = 0;
        maHen = "";
        cboDichVu.setEnabled(true);
        cboThuCung.setEnabled(true);
        lblNguon.setText("Khách đến trực tiếp");
        txtSoDienThoai.setText("");
        chonKhach(null);
        txtCanNang.setText("");
        txtGhiChu.setText("");
        txtYeuCau.setText("");
        cboDichVu.setSelectedIndex(-1);
        chkSucKhoe.setSelected(false);
    }

    private PhieuSpaTreo chupNhap() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        return new PhieuSpaTreo(khachHang == null ? "" : String.valueOf(khachHang[0]),
            txtSoDienThoai.getText(), thu == null ? "" : thu.giaTri(), goi == null ? "" : goi.giaTri(),
            maHen, txtCanNang.getText(), txtGhiChu.getText(), txtYeuCau.getText());
    }

    private void treoPhieu() {
        PhieuSpaTreoService.treo(idPhieuTreo, chupNhap());

        datLaiForm();
        HopThoai.hienThiThanhCong(this, "Đã treo phiếu Spa. Chọn Phiếu đang treo để tiếp tục sau.");
        khiTreo.run();
    }

    public boolean xacNhanDong() {
        if (!chupNhap().coNoiDung()) return true;
        String[] luaChon = {"Treo phiếu", "Bỏ thay đổi", "Tiếp tục nhập"};
        int chon = JOptionPane.showOptionDialog(this, "Bạn muốn lưu phiếu đang nhập để tiếp tục sau?",
            "Đóng phiếu Spa", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, luaChon, luaChon[0]);
        if (chon == 0) { treoPhieu(); return true; }
        return chon == 1;
    }

    public void moDanhSachPhieuTreo() {
        if (khiTreo != null) {
            khiTreo.run();
            return;
        }
        var cacPhieu = PhieuSpaTreoService.danhSach();
        if (cacPhieu.isEmpty()) {
            HopThoai.hienThiThongTin(this, "Chưa có phiếu Spa đang treo.");
            return;
        }
        var moHinh = new javax.swing.table.DefaultTableModel(
            new String[] {"ID", "Mã phiếu treo", "Khách hàng", "Số điện thoại", "Thú cưng", "Dịch vụ", "Cập nhật lúc"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cacPhieu.forEach(moHinh::addRow);
        JScrollPane cuon = taoBangDinhDang(moHinh);
        JTable bang = layBang(cuon);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.removeColumn(bang.getColumnModel().getColumn(0));
        JDialog ds = new JDialog(SwingUtilities.getWindowAncestor(this), "Phiếu Spa đang treo",
            Dialog.ModalityType.APPLICATION_MODAL);
        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.setBackground(MauUngDung.NEN_KEM);
        noiDung.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        noiDung.add(cuon, BorderLayout.CENTER);
        java.util.function.LongSupplier chonId = () -> {
            if (bang.getSelectedRow() < 0) throw new IllegalArgumentException("Chọn một phiếu đang treo.");
            return ((Number) moHinh.getValueAt(bang.convertRowIndexToModel(bang.getSelectedRow()), 0)).longValue();
        };
        JPanel nutDong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nutDong.setOpaque(false);
        nutDong.add(nut("Xóa phiếu treo", () -> {
            long id = chonId.getAsLong();
            if (!HopThoai.xacNhan(ds, "Xóa phiếu Spa đang treo đã chọn?")) return;
            PhieuSpaTreoService.xoa(id);
            if (idPhieuTreo == id) idPhieuTreo = 0;
            moHinh.setRowCount(0);
            PhieuSpaTreoService.danhSach().forEach(moHinh::addRow);
        }));
        nutDong.add(nut("Đóng", ds::dispose));
        nutDong.add(nut("Mở lại phiếu", () -> {
            long id = chonId.getAsLong();
            if (chupNhap().coNoiDung() && !HopThoai.xacNhan(ds,
                "Thay nội dung trên biểu mẫu bằng phiếu treo đã chọn? Nội dung chưa treo sẽ bị bỏ.")) return;
            khoiPhucPhieu(id);
            ds.dispose();
        }));
        noiDung.add(nutDong, BorderLayout.SOUTH);
        ds.setContentPane(noiDung);
        ds.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ds.setSize(1000, 480);
        HopThoai.chuanHoaCuaSo(ds, this);
        ds.setVisible(true);
    }

    public void khoiPhucPhieu(long id) {
        PhieuSpaTreo p = PhieuSpaTreoService.lay(id);

        Object[] khach = p.maKhach().isBlank() ? null : DanhMucService.layKhachHang().stream()
            .filter(k -> p.maKhach().equals(String.valueOf(k[0])) && "Đang hoạt động".equals(k[8]))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Khách của phiếu treo không còn hoạt động."));
        if (!p.maThu().isBlank() && DanhMucService.layThuCung().stream().noneMatch(t ->
            p.maThu().equals(String.valueOf(t[0])) && "Đang hoạt động".equals(t[13])
                && p.maKhach().equals(DanhMucService.layMaTuLuaChon(t[8]))))
            throw new IllegalArgumentException("Thú cưng của phiếu treo không còn thuộc hồ sơ khách đang hoạt động.");
        if (!p.maGoi().isBlank()) {
            boolean coGoi = false;
            for (int i = 0; i < cboDichVu.getItemCount(); i++)
                if (p.maGoi().equals(cboDichVu.getItemAt(i).giaTri())) coGoi = true;
            if (!coGoi) throw new IllegalArgumentException("Dịch vụ của phiếu treo không còn hoạt động.");
        }
        datLaiForm();
        chonKhach(khach);
        cboThuCung.setSelectedIndex(-1);
        for (int i = 0; i < cboThuCung.getItemCount(); i++)
            if (p.maThu().equals(cboThuCung.getItemAt(i).giaTri())) cboThuCung.setSelectedIndex(i);
        for (int i = 0; i < cboDichVu.getItemCount(); i++)
            if (p.maGoi().equals(cboDichVu.getItemAt(i).giaTri())) cboDichVu.setSelectedIndex(i);
        maHen = p.maHen();
        cboThuCung.setEnabled(maHen.isBlank());
        cboDichVu.setEnabled(maHen.isBlank());
        lblNguon.setText(maHen.isBlank() ? "Khách đến trực tiếp — đang sửa phiếu treo"
            : "Từ lịch hẹn: " + maHen + " — đang sửa phiếu treo");
        txtSoDienThoai.setText(p.soDienThoai());
        txtCanNang.setText(p.canNang());
        txtGhiChu.setText(p.tinhTrang());
        txtYeuCau.setText(p.yeuCau());
        chkSucKhoe.setSelected(false);
        lblGia.setText("Kiểm tra lại sức khỏe thực tế và nhấn Xem giá trước khi tạo phiếu");
        idPhieuTreo = id;
    }

    public void nhanLichHen(long id) {
        if (khachHang != null && !HopThoai.xacNhan(this, "Thay nội dung đang nhập bằng lịch hẹn đã chọn?")) return;
        var hen = QuyTrinhService.layLichHen(id);
        if (hen.isEmpty()) throw new IllegalArgumentException("Lịch hẹn không còn chờ tiếp nhận.");
        var chiTiet = SpaService.layChiTietLichHen(id);
        Object[] thu = DanhMucService.layThuCung().stream()
            .filter(t -> String.valueOf(t[0]).equals(String.valueOf(chiTiet[0])))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ thú cưng."));
        String maChu = DanhMucService.layMaTuLuaChon(thu[8]);
        Object[] chu = DanhMucService.layKhachHang().stream()
            .filter(k -> String.valueOf(k[0]).equals(maChu)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));
        chonKhach(chu);
        for (int i = 0; i < cboThuCung.getItemCount(); i++)
            if (cboThuCung.getItemAt(i).giaTri().equals(String.valueOf(chiTiet[0]))) cboThuCung.setSelectedIndex(i);
        cboDichVu.setSelectedIndex(-1);
        for (int i = 0; i < cboDichVu.getItemCount(); i++)
            if (cboDichVu.getItemAt(i).giaTri().equals(String.valueOf(chiTiet[1]))) cboDichVu.setSelectedIndex(i);
        if (cboDichVu.getSelectedItem() == null)
            throw new IllegalArgumentException("Dịch vụ của lịch hẹn không còn hoạt động.");
        maHen = String.valueOf(hen.get(0)[0]);
        lblNguon.setText("Từ lịch hẹn: " + maHen + " — vẫn cần kiểm tra và cân lại tại quầy");
        cboThuCung.setEnabled(false);
        cboDichVu.setEnabled(false);
        txtCanNang.setText("");
        txtGhiChu.setText("");
        txtYeuCau.setText("");
        chkSucKhoe.setSelected(false);
    }

    private LuaChon thuDaChon() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null) throw new IllegalArgumentException("Cần chọn thú cưng.");
        return thu;
    }

    private double canNang() {
        try { return Double.parseDouble(txtCanNang.getText().trim().replace(',', '.')); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Cân nặng phải là số, ví dụ 5,2."); }
    }

    private void xemGia() {
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        if (goi == null) throw new IllegalArgumentException("Cần chọn dịch vụ.");
        long gia = LapPhieuSpaService.baoGia(thuDaChon().giaTri(), goi.giaTri(), canNang());
        lblGia.setText(TienTe.dinhDang(gia));
    }

    private JButton nut(String nhan, Runnable xuLy) {
        JButton nut = new NutDinhDang(nhan, nhan.equals("Tạo phiếu tiếp nhận")
            ? NutDinhDang.KieuNut.CHINH : NutDinhDang.KieuNut.PHU);
        nut.addActionListener(e -> anToan(xuLy));
        return nut;
    }

    private void anToan(Runnable xuLy) {
        try {
            xuLy.run();
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    private String soDienThoai() {
        return txtSoDienThoai.getText().replaceAll("[\\s.]", "");
    }

    private void timKhach() {
        if (!maHen.isBlank())
            throw new IllegalArgumentException("Nhấn Lập phiếu mới để chuyển sang khách đến trực tiếp.");
        chonKhach(null);
        String sdt = soDienThoai();
        if (!sdt.matches("0\\d{9}"))
            throw new IllegalArgumentException("Nhập số điện thoại gồm 10 chữ số, bắt đầu bằng 0.");
        List<Object[]> ketQua = DanhMucService.layKhachHang().stream()
            .filter(k -> sdt.equals(String.valueOf(k[2]))).toList();
        if (!ketQua.isEmpty()) {
            Object[] khach = ketQua.get(0);
            if (!"Đang hoạt động".equals(khach[8]))
                throw new IllegalArgumentException("Hồ sơ khách đã ngừng hoạt động; cần khôi phục tại danh mục.");
            chonKhach(khach);
        } else if (HopThoai.xacNhan(this, "Chưa có khách với số điện thoại này. Thêm khách hàng mới?")) {
            themKhach();
        }
    }

    private void themKhach() {
        if (!maHen.isBlank())
            throw new IllegalArgumentException("Nhấn Lập phiếu mới để tiếp nhận khách khác.");
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        Object[] moi = DanhMucKhachHangPanel.moThem(this, soDienThoai());
        if (moi != null) {
            if (!"Đang hoạt động".equals(moi[8]))
                throw new IllegalArgumentException("Khách vừa lưu đã ngừng hoạt động. Chọn hồ sơ đang hoạt động.");
            chonKhach(moi);
        }
    }

    private void chonKhach(Object[] khach) {
        khachHang = khach;
        lblKhachHang.setText(khach == null ? "Chưa chọn khách hàng"
            : khach[0] + " - " + khach[1] + " | " + khach[2]);
        if (khach != null) txtSoDienThoai.setText(String.valueOf(khach[2]));
        taiThuCung();
    }

    private void taiThuCung() {
        LuaChon cu = (LuaChon) cboThuCung.getSelectedItem();
        cboThuCung.removeAllItems();
        if (khachHang == null) return;
        String maKhach = String.valueOf(khachHang[0]);
        for (Object[] thu : DanhMucService.layThuCung()) {
            if ("Đang hoạt động".equals(thu[13])
                && maKhach.equals(DanhMucService.layMaTuLuaChon(thu[8])))
                cboThuCung.addItem(new LuaChon(String.valueOf(thu[0]), thu[0] + " - " + thu[1]));
        }
        if (cu != null)
            for (int i = 0; i < cboThuCung.getItemCount(); i++)
                if (cboThuCung.getItemAt(i).giaTri().equals(cu.giaTri())) cboThuCung.setSelectedIndex(i);
    }

    private void moHoSoThuCung() {
        if (khachHang == null)
            throw new IllegalArgumentException("Chọn khách hàng trước khi thêm thú cưng.");

        Object[] thuMoi = DanhMucThuCungPanel.moThem(this, String.valueOf(khachHang[0]));
        if (thuMoi != null) {
            taiThuCung();
            String maThuMoi = String.valueOf(thuMoi[0]);
            for (int i = 0; i < cboThuCung.getItemCount(); i++) {
                if (cboThuCung.getItemAt(i).giaTri().equals(maThuMoi)) {
                    cboThuCung.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void luu() {
        if (khachHang == null)
            throw new IllegalArgumentException("Tìm hoặc thêm khách hàng trước khi lưu phiếu.");
        if (!soDienThoai().equals(String.valueOf(khachHang[2])))
            throw new IllegalArgumentException("Số điện thoại đã thay đổi. Nhấn Tìm khách để chọn lại.");
        LuaChon thuCung = (LuaChon) cboThuCung.getSelectedItem();
        if (thuCung == null)
            throw new IllegalArgumentException("Khách chưa có thú cưng phù hợp. Bổ sung hồ sơ thú cưng trước.");
        if (!chkSucKhoe.isSelected())
            throw new IllegalArgumentException("Cần kiểm tra sức khỏe thực tế trước khi nhận.");
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        if (goi == null) throw new IllegalArgumentException("Cần chọn dịch vụ.");
        long id = PhieuSpaTreoService.taoPhieu(idPhieuTreo, thuCung.giaTri(), goi.giaTri(), maHen,
            canNang(), txtGhiChu.getText(), txtYeuCau.getText());

        khachHang = null;
        datLaiForm();
        HopThoai.hienThiThanhCong(this, "Đã lập phiếu Spa. Có thể xem trong danh sách phiếu Spa.");
        khiLuu.accept(id);
    }
}
