package com.petstation.gui.processing.spa;

import com.petstation.gui.catalog.DanhMucKhachHangPanel;
import com.petstation.gui.catalog.DanhMucThuCungPanel;
import com.petstation.gui.components.BoGocPanel;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BieuMauSQL.LuaChon;
import com.petstation.model.PhieuSpaTreo;
import com.petstation.model.Quyen;
import com.petstation.service.DanhMucService;
import com.petstation.service.LapPhieuSpaService;
import com.petstation.service.LuaChonNghiepVuService;
import com.petstation.service.PhienDangNhapService;
import com.petstation.service.PhieuSpaTreoService;
import com.petstation.service.QuyTrinhService;
import com.petstation.service.SpaService;
import com.petstation.service.TiemPhongService;
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
    private final JLabel lblGia = new JLabel("Chọn dịch vụ và cân nặng để xem giá");
    private final JLabel lblNguon = new JLabel("Khách đến trực tiếp");
    private final JTextArea txtYeuCau = new JTextArea(2, 28);

    private final JLabel lblHuyHieuHoSo = new JLabel("CHƯA CHỌN THÚ CƯNG");
    private final JLabel lblChiTietHoSo = new JLabel("Chọn thú cưng bên trên để hệ thống tự động kiểm tra điều kiện tuổi và tiêm phòng.");
    private final JPanel pnlTheHoSo = new BoGocPanel(10, new Color(248, 249, 250), true);
    private final JButton btnCapNhatSoTiem;

    private final JComboBox<String> cboKiemTraDa = new JComboBox<>(new String[]{
            "Không thấy bất thường",
            "Có vết trầy xước / Vết thương cũ",
            "Dấu hiệu nghi nấm / Gàu ngứa",
            "Khác (mô tả bên dưới)"
    });
    private final JComboBox<String> cboKiemTraVe = new JComboBox<>(new String[]{
            "Không phát hiện",
            "Có ve / Rận / Bọ chét"
    });
    private final JComboBox<String> cboKiemTraMatTai = new JComboBox<>(new String[]{
            "Sạch sẽ, bình thường",
            "Mắt đỏ / Tai hôi / Chảy dịch",
            "Khác (mô tả bên dưới)"
    });
    private final JComboBox<String> cboKiemTraLong = new JComboBox<>(new String[]{
            "Lông mượt, không rối",
            "Rối nhẹ",
            "Rối vừa",
            "Bết dính nặng (Cần gỡ rối/cạo)"
    });
    private final JComboBox<String> cboKetLuanTiepNhan = new JComboBox<>(new String[]{
            "Đủ điều kiện tiếp nhận",
            "Cần điều chỉnh dịch vụ / Chăm sóc đặc biệt",
            "Từ chối tiếp nhận (có vết thương hở / sức khỏe yếu)"
    });

    private final LongConsumer khiLuu;
    private final Runnable khiTreo;
    private long idPhieuTreo;
    private String maHen = "";

    public LapPhieuSpaPanel(LongConsumer khiLuu, Runnable chonLich, Runnable khiTreo) {
        this.khiLuu = khiLuu;
        this.khiTreo = khiTreo;

        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setOpaque(false);

        lblNguon.setForeground(MauUngDung.NAU);
        lblNguon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));

        JButton btnChonLich = nut("Khách có hẹn — Chọn lịch hẹn", chonLich);
        btnChonLich.setPreferredSize(new Dimension(240, 32));

        txtSoDienThoai.setPreferredSize(new Dimension(160, 32));
        JButton btnTim = nut("Tìm khách", this::timKhach);
        btnTim.setPreferredSize(new Dimension(95, 32));
        JButton themKhach = nut("+ Thêm khách", this::themKhach);
        themKhach.setPreferredSize(new Dimension(125, 32));
        themKhach.setEnabled(PhienDangNhapService.layPhien().kiemTraCo(Quyen.QUAN_LY_KHACH_HANG));
        txtSoDienThoai.addActionListener(e -> anToan(this::timKhach));

        lblKhachHang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblKhachHang.setForeground(MauUngDung.DO_THUONG_HIEU);

        cboThuCung.setPreferredSize(new Dimension(280, 32));
        JButton hoSo = nut("+ Thêm thú cưng", this::moHoSoThuCung);
        hoSo.setPreferredSize(new Dimension(145, 32));
        hoSo.setEnabled(PhienDangNhapService.layPhien().kiemTraCo(Quyen.QUAN_LY_THU_CUNG));

        btnCapNhatSoTiem = nut("Xem / Sửa sổ tiêm", this::moSoTiemThuCung);
        btnCapNhatSoTiem.setPreferredSize(new Dimension(150, 26));
        btnCapNhatSoTiem.setEnabled(false);

        for (Object[] goi : LuaChonNghiepVuService.layLuaChonGoiDichVu())
            cboDichVu.addItem(new LuaChon(String.valueOf(goi[0]), String.valueOf(goi[1])));
        cboDichVu.setSelectedIndex(-1);

        txtCanNang.setPreferredSize(new Dimension(85, 32));
        cboDichVu.setPreferredSize(new Dimension(280, 32));

        lblGia.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblGia.setForeground(MauUngDung.DO_THUONG_HIEU);

        txtGhiChu.setRows(4);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        JScrollPane spGhiChu = new JScrollPane(txtGhiChu);
        spGhiChu.setPreferredSize(new Dimension(840, 85));
        spGhiChu.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true));

        txtYeuCau.setRows(3);
        txtYeuCau.setLineWrap(true);
        txtYeuCau.setWrapStyleWord(true);
        txtYeuCau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        JScrollPane spYeuCau = new JScrollPane(txtYeuCau);
        spYeuCau.setPreferredSize(new Dimension(840, 75));
        spYeuCau.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true));

        cboDichVu.addActionListener(e -> capNhatGiaTuDong());
        txtCanNang.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                capNhatGiaTuDong();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                capNhatGiaTuDong();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                capNhatGiaTuDong();
            }
        });
        cboThuCung.addActionListener(e -> {
            LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
            if (thu != null && txtCanNang.getText().trim().isEmpty()) {
                DanhMucService.layThuCung().stream()
                        .filter(t -> thu.giaTri().equals(String.valueOf(t[0])))
                        .findFirst()
                        .ifPresent(t -> {
                            String cn = t[6] == null ? "" : String.valueOf(t[6]).trim();
                            if (!cn.isEmpty())
                                txtCanNang.setText(cn);
                        });
            }
            capNhatGiaTuDong();
            capNhatDanhGiaHoSo();
        });

        // Nhóm 1: Nguồn tiếp nhận & Khách hàng
        noiDung.add(nhom(
                "1. Nguồn tiếp nhận & Khách hàng",
                hang(nhan("Nguồn tiếp nhận:"), lblNguon, Box.createHorizontalStrut(12), btnChonLich),
                hang(nhan("Số điện thoại:"), txtSoDienThoai, btnTim, themKhach),
                hang(nhan("Khách hàng:"), lblKhachHang)));
        noiDung.add(Box.createVerticalStrut(12));

        // Khung trạng thái hồ sơ tiêm phòng tự động
        pnlTheHoSo.setLayout(new BorderLayout(8, 6));
        pnlTheHoSo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        pnlTheHoSo.setPreferredSize(new Dimension(840, 68));
        pnlTheHoSo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel pnlDauHoSo = new JPanel(new BorderLayout(8, 0));
        pnlDauHoSo.setOpaque(false);
        lblHuyHieuHoSo.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlDauHoSo.add(lblHuyHieuHoSo, BorderLayout.WEST);
        pnlDauHoSo.add(btnCapNhatSoTiem, BorderLayout.EAST);

        lblChiTietHoSo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblChiTietHoSo.setForeground(MauUngDung.VAN_BAN_TOI);

        pnlTheHoSo.add(pnlDauHoSo, BorderLayout.NORTH);
        pnlTheHoSo.add(lblChiTietHoSo, BorderLayout.CENTER);

        // Bảng kiểm tra thực tế tại quầy (Pre-Grooming Assessment)
        cboKiemTraDa.setPreferredSize(new Dimension(220, 30));
        cboKiemTraVe.setPreferredSize(new Dimension(190, 30));
        cboKiemTraMatTai.setPreferredSize(new Dimension(220, 30));
        cboKiemTraLong.setPreferredSize(new Dimension(190, 30));
        cboKetLuanTiepNhan.setPreferredSize(new Dimension(260, 30));

        JPanel hangKt1 = hang(
                nhan("Da & Lông:"), cboKiemTraDa, Box.createHorizontalStrut(8),
                nhan("Ký sinh trùng:"), cboKiemTraVe);
        JPanel hangKt2 = hang(
                nhan("Mắt & Tai:"), cboKiemTraMatTai, Box.createHorizontalStrut(8),
                nhan("Độ rối lông:"), cboKiemTraLong);
        JPanel hangKl = hang(
                nhan("Đánh giá tại quầy:"), cboKetLuanTiepNhan);

        // Nhóm 2: Hồ sơ thú cưng & Đánh giá thực tế tại quầy
        noiDung.add(nhom(
                "2. Hồ sơ thú cưng & Đánh giá thực tế tại quầy",
                hang(nhan("Thú cưng:"), cboThuCung, hoSo),
                pnlTheHoSo,
                Box.createVerticalStrut(4),
                hangKt1,
                hangKt2,
                hangKl,
                goiY("Hệ thống tự động kiểm tra tuổi và sổ tiêm. Đánh giá nhanh tình trạng thực tế trước khi nhận bé vào dịch vụ.")));
        noiDung.add(Box.createVerticalStrut(12));

        // Nhóm 3: Dịch vụ Spa & Cân nặng
        JPanel pnlDichVuGia = hang(
                nhan("Dịch vụ & Cân nặng:"),
                new JLabel("Cân nặng:"), txtCanNang, new JLabel("kg"), Box.createHorizontalStrut(12),
                new JLabel("Gói dịch vụ:"), cboDichVu);
        noiDung.add(nhom(
                "3. Dịch vụ Spa & Cân nặng",
                pnlDichVuGia,
                hang(nhan("Giá dự kiến:"), lblGia),
                goiY("Giá được tính tự động theo loài thú cưng (Chó/Mèo), cân nặng và bảng giá của gói dịch vụ.")));
        noiDung.add(Box.createVerticalStrut(12));

        // Nhóm 4: Tình trạng tiếp nhận & Yêu cầu
        JLabel lblTieuDeGhiChu = new JLabel("Ghi chú thêm về sức khỏe / lưu ý đặc biệt (nếu có):");
        lblTieuDeGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblTieuDeGhiChu.setForeground(MauUngDung.VAN_BAN_XAM);

        JLabel lblTieuDeYeuCau = new JLabel("Yêu cầu của khách / Vật dụng gửi kèm:");
        lblTieuDeYeuCau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblTieuDeYeuCau.setForeground(MauUngDung.VAN_BAN_XAM);

        spGhiChu.setPreferredSize(new Dimension(840, 85));
        spYeuCau.setPreferredSize(new Dimension(840, 75));

        noiDung.add(nhom(
                "4. Ghi chú sức khỏe & Yêu cầu gửi kèm",
                lblTieuDeGhiChu,
                spGhiChu,
                lblTieuDeYeuCau,
                spYeuCau));

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        thaoTac.setBackground(Color.WHITE);
        thaoTac.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
                BorderFactory.createEmptyBorder(6, 20, 6, 20)));

        thaoTac.add(nut("Phiếu đang treo", this::moDanhSachPhieuTreo));
        thaoTac.add(nut("Lập phiếu mới", this::xoaForm));
        thaoTac.add(nut("Treo phiếu", this::treoPhieu));
        JButton btnTaoPhieu = nut("Tạo phiếu tiếp nhận", this::luu);
        btnTaoPhieu.setPreferredSize(new Dimension(180, 36));
        thaoTac.add(btnTaoPhieu);

        JPanel pnlCanGiua = new JPanel(new GridBagLayout());
        pnlCanGiua.setBackground(MauUngDung.NEN_KEM);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(16, 20, 24, 20);
        noiDung.setPreferredSize(new Dimension(880, 890));
        pnlCanGiua.add(noiDung, gbc);

        JScrollPane cuon = new JScrollPane(pnlCanGiua);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.getVerticalScrollBar().setUnitIncrement(18);

        add(cuon, BorderLayout.CENTER);
        add(thaoTac, BorderLayout.SOUTH);
    }

    private static JLabel nhan(String vanBan) {
        JLabel l = new JLabel(vanBan);
        l.setPreferredSize(new Dimension(140, 32));
        l.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        l.setForeground(MauUngDung.VAN_BAN_TOI);
        return l;
    }

    private static JPanel hang(Component... cac) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setOpaque(false);
        for (Component c : cac)
            p.add(c);
        return p;
    }

    private static JPanel nhom(String ten, Component... cac) {
        JPanel p = new BoGocPanel(14, MauUngDung.THE_NEN);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JLabel nhan = new JLabel(ten);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhan.setForeground(MauUngDung.NAU);
        nhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(nhan);
        for (Component c : cac) {
            p.add(Box.createVerticalStrut(8));
            if (c instanceof JComponent j) {
                j.setAlignmentX(Component.LEFT_ALIGNMENT);
                j.setMaximumSize(new Dimension(Integer.MAX_VALUE, j.getPreferredSize().height));
            }
            p.add(c);
        }
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private static JLabel goiY(String noiDung) {
        JLabel nhan = new JLabel(noiDung);
        nhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_XAM);
        return nhan;
    }

    public void xoaForm() {
        if (chupNhap().coNoiDung()
                && !HopThoai.xacNhan(this, "Bỏ nội dung đang nhập để lập phiếu mới? Phiếu đã treo vẫn được giữ."))
            return;
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
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatDanhGiaHoSo();
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

    public void moDanhSachPhieuTreo() {
        if (khiTreo != null) {
            khiTreo.run();
        }
    }

    public void khoiPhucPhieu(long id) {
        PhieuSpaTreo p = PhieuSpaTreoService.lay(id);

        Object[] khach = p.maKhach().isBlank() ? null
                : DanhMucService.layKhachHang().stream()
                        .filter(k -> p.maKhach().equals(String.valueOf(k[0])) && "Đang hoạt động".equals(k[8]))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Khách của phiếu treo không còn hoạt động."));
        if (!p.maThu().isBlank() && DanhMucService.layThuCung().stream()
                .noneMatch(t -> p.maThu().equals(String.valueOf(t[0])) && "Đang hoạt động".equals(t[13])
                        && p.maKhach().equals(DanhMucService.layMaTuLuaChon(t[8]))))
            throw new IllegalArgumentException("Thú cưng của phiếu treo không còn thuộc hồ sơ khách đang hoạt động.");
        if (!p.maGoi().isBlank()) {
            boolean coGoi = false;
            for (int i = 0; i < cboDichVu.getItemCount(); i++)
                if (p.maGoi().equals(cboDichVu.getItemAt(i).giaTri()))
                    coGoi = true;
            if (!coGoi)
                throw new IllegalArgumentException("Dịch vụ của phiếu treo không còn hoạt động.");
        }
        datLaiForm();
        chonKhach(khach);
        cboThuCung.setSelectedIndex(-1);
        for (int i = 0; i < cboThuCung.getItemCount(); i++)
            if (p.maThu().equals(cboThuCung.getItemAt(i).giaTri()))
                cboThuCung.setSelectedIndex(i);
        for (int i = 0; i < cboDichVu.getItemCount(); i++)
            if (p.maGoi().equals(cboDichVu.getItemAt(i).giaTri()))
                cboDichVu.setSelectedIndex(i);
        maHen = p.maHen();
        cboThuCung.setEnabled(maHen.isBlank());
        cboDichVu.setEnabled(maHen.isBlank());
        lblNguon.setText(maHen.isBlank() ? "Khách đến trực tiếp — đang sửa phiếu treo"
                : "Từ lịch hẹn: " + maHen + " — đang sửa phiếu treo");
        txtSoDienThoai.setText(p.soDienThoai());
        txtCanNang.setText(p.canNang());
        txtGhiChu.setText(p.tinhTrang());
        txtYeuCau.setText(p.yeuCau());
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatDanhGiaHoSo();
        lblGia.setText("Kiểm tra lại sức khỏe thực tế và nhấn Xem giá trước khi tạo phiếu");
        idPhieuTreo = id;
    }

    public void nhanLichHen(long id) {
        if (khachHang != null && !HopThoai.xacNhan(this, "Thay nội dung đang nhập bằng lịch hẹn đã chọn?"))
            return;
        var hen = QuyTrinhService.layLichHen(id);
        if (hen.isEmpty())
            throw new IllegalArgumentException("Lịch hẹn không còn chờ tiếp nhận.");
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
            if (cboThuCung.getItemAt(i).giaTri().equals(String.valueOf(chiTiet[0])))
                cboThuCung.setSelectedIndex(i);
        cboDichVu.setSelectedIndex(-1);
        for (int i = 0; i < cboDichVu.getItemCount(); i++)
            if (cboDichVu.getItemAt(i).giaTri().equals(String.valueOf(chiTiet[1])))
                cboDichVu.setSelectedIndex(i);
        if (cboDichVu.getSelectedItem() == null)
            throw new IllegalArgumentException("Dịch vụ của lịch hẹn không còn hoạt động.");
        maHen = String.valueOf(hen.get(0)[0]);
        lblNguon.setText("Từ lịch hẹn: " + maHen + " — vẫn cần kiểm tra và cân lại tại quầy");
        cboThuCung.setEnabled(false);
        cboDichVu.setEnabled(false);
        txtCanNang.setText("");
        txtGhiChu.setText("");
        txtYeuCau.setText("");
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatDanhGiaHoSo();
    }

    private LuaChon thuDaChon() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null)
            throw new IllegalArgumentException("Cần chọn thú cưng.");
        return thu;
    }

    private double canNang() {
        try {
            double cn = Double.parseDouble(txtCanNang.getText().trim().replace(',', '.'));
            if (!Double.isFinite(cn) || cn <= 0)
                throw new IllegalArgumentException("Cân nặng phải lớn hơn 0, ví dụ 5,2.");
            return cn;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cân nặng phải là số, ví dụ 5,2.");
        }
    }

    private void capNhatGiaTuDong() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        String cnStr = txtCanNang.getText().trim().replace(',', '.');
        if (thu == null || goi == null || cnStr.isEmpty()) {
            lblGia.setText("Chọn thú cưng, dịch vụ và nhập cân nặng để tự động tính giá");
            return;
        }
        try {
            double cn = Double.parseDouble(cnStr);
            if (cn <= 0) {
                lblGia.setText("Cân nặng phải lớn hơn 0");
                return;
            }
            long gia = LapPhieuSpaService.baoGia(thu.giaTri(), goi.giaTri(), cn);
            lblGia.setText(TienTe.dinhDang(gia));
        } catch (RuntimeException ex) {
            lblGia.setText(ex.getMessage());
        }
    }

    private void xemGia() {
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        if (goi == null)
            throw new IllegalArgumentException("Cần chọn dịch vụ.");
        long gia = LapPhieuSpaService.baoGia(thuDaChon().giaTri(), goi.giaTri(), canNang());
        lblGia.setText(TienTe.dinhDang(gia));
    }

    private JButton nut(String nhan, Runnable xuLy) {
        JButton nut = new NutDinhDang(nhan, nhan.equals("Tạo phiếu tiếp nhận")
                ? NutDinhDang.KieuNut.CHINH
                : NutDinhDang.KieuNut.PHU);
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
        if (khach != null)
            txtSoDienThoai.setText(String.valueOf(khach[2]));
        taiThuCung();
    }

    private void taiThuCung() {
        LuaChon cu = (LuaChon) cboThuCung.getSelectedItem();
        cboThuCung.removeAllItems();
        if (khachHang == null)
            return;
        String maKhach = String.valueOf(khachHang[0]);
        for (Object[] thu : DanhMucService.layThuCung()) {
            if ("Đang hoạt động".equals(thu[13])
                    && maKhach.equals(DanhMucService.layMaTuLuaChon(thu[8])))
                cboThuCung.addItem(new LuaChon(String.valueOf(thu[0]), thu[0] + " - " + thu[1]));
        }
        if (cu != null)
            for (int i = 0; i < cboThuCung.getItemCount(); i++)
                if (cboThuCung.getItemAt(i).giaTri().equals(cu.giaTri()))
                    cboThuCung.setSelectedIndex(i);
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

    private void moSoTiemThuCung() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null)
            return;
        DanhMucThuCungPanel.moSua(this, thu.giaTri());
        capNhatDanhGiaHoSo();
    }

    private void capNhatDanhGiaHoSo() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null) {
            lblHuyHieuHoSo.setText("CHƯA CHỌN THÚ CƯNG");
            lblHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_XAM);
            lblChiTietHoSo.setText("Chọn thú cưng bên trên để hệ thống tự động kiểm tra điều kiện tuổi và tiêm phòng.");
            pnlTheHoSo.setBackground(new Color(248, 249, 250));
            btnCapNhatSoTiem.setEnabled(false);
            return;
        }
        btnCapNhatSoTiem.setEnabled(true);
        TiemPhongService.KetQuaTiemPhong kq = TiemPhongService.danhGiaNhanSpa(thu.giaTri());
        if (kq.duDieuKien()) {
            lblHuyHieuHoSo.setText("✓ ĐẠT ĐIỀU KIỆN HỒ SƠ TIÊM PHÒNG");
            lblHuyHieuHoSo.setForeground(new Color(0x06, 0x5f, 0x46));
            pnlTheHoSo.setBackground(new Color(0xec, 0xfd, 0xf5));
            lblChiTietHoSo.setText(kq.thongDiep());
        } else {
            boolean thieuHoSo = kq.soMui() == 0;
            lblHuyHieuHoSo.setText(thieuHoSo ? "⚠ CHƯA CÓ HỒ SƠ TIÊM PHÒNG" : "✕ CHƯA THỂ TIẾP NHẬN");
            lblHuyHieuHoSo.setForeground(thieuHoSo ? new Color(0xb4, 0x53, 0x09) : MauUngDung.DO_THUONG_HIEU);
            pnlTheHoSo.setBackground(thieuHoSo ? new Color(0xff, 0xfb, 0xeb) : new Color(0xfe, 0xf2, 0xf2));
            lblChiTietHoSo.setText(kq.thongDiep());
        }
        pnlTheHoSo.revalidate();
        pnlTheHoSo.repaint();
    }

    private void luu() {
        if (khachHang == null)
            throw new IllegalArgumentException("Tìm hoặc thêm khách hàng trước khi lưu phiếu.");
        if (!soDienThoai().equals(String.valueOf(khachHang[2])))
            throw new IllegalArgumentException("Số điện thoại đã thay đổi. Nhấn Tìm khách để chọn lại.");
        LuaChon thuCung = (LuaChon) cboThuCung.getSelectedItem();
        if (thuCung == null)
            throw new IllegalArgumentException("Khách chưa có thú cưng phù hợp. Bổ sung hồ sơ thú cưng trước.");
        TiemPhongService.KetQuaTiemPhong kq = TiemPhongService.danhGiaNhanSpa(thuCung.giaTri());
        if (!kq.duDieuKien())
            throw new IllegalArgumentException(kq.thongDiep());
        if (cboKetLuanTiepNhan.getSelectedIndex() == 2)
            throw new IllegalArgumentException("Thú cưng không đủ điều kiện tiếp nhận thực tế tại quầy (bị từ chối do vết thương hở hoặc sức khỏe yếu).");
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        if (goi == null)
            throw new IllegalArgumentException("Cần chọn dịch vụ.");

        StringBuilder dg = new StringBuilder();
        if (cboKiemTraDa.getSelectedIndex() > 0)
            dg.append("Da/lông: ").append(cboKiemTraDa.getSelectedItem()).append("; ");
        if (cboKiemTraVe.getSelectedIndex() > 0)
            dg.append("Ký sinh: ").append(cboKiemTraVe.getSelectedItem()).append("; ");
        if (cboKiemTraMatTai.getSelectedIndex() > 0)
            dg.append("Mắt/tai: ").append(cboKiemTraMatTai.getSelectedItem()).append("; ");
        if (cboKiemTraLong.getSelectedIndex() > 0)
            dg.append("Độ rối lông: ").append(cboKiemTraLong.getSelectedItem()).append("; ");
        dg.append("Kết luận quầy: ").append(cboKetLuanTiepNhan.getSelectedItem());
        String ghiChuNhap = txtGhiChu.getText().trim();
        String tinhTrangLuu = ghiChuNhap.isEmpty() ? dg.toString() : dg + " | " + ghiChuNhap;

        long id = PhieuSpaTreoService.taoPhieu(idPhieuTreo, thuCung.giaTri(), goi.giaTri(), maHen,
                canNang(), tinhTrangLuu, txtYeuCau.getText());

        khachHang = null;
        datLaiForm();
        khiLuu.accept(id);
    }
}
