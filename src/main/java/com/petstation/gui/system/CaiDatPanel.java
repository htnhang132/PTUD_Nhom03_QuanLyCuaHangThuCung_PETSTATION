package com.petstation.gui.system;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.CaiDatService;

import java.awt.*;
import java.util.*;

import javax.print.*;
import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho cài đặt vùng hiển thị. */
public final class CaiDatPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Các đầu vào. */
    private final Map<String, JTextField> cacDauVao = new LinkedHashMap<>();

    /** Máy in. */
    private final JComboBox<String> cboMayIn = new JComboBox<>();

    /** Có thể sửa. */
    private final boolean coTheSua;

    private final Map<String, String> giaTriDaNap = new LinkedHashMap<>();
    private Object mayInDaNap;

    public CaiDatPanel(boolean coTheSua) {
        this.coTheSua = coTheSua;
        setLayout(new BorderLayout(0, 12));
        add(taoTieuDeMuc("Cài đặt hệ thống"), BorderLayout.NORTH);
        // Các nhóm.
        JPanel pnlCacNhom = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCacNhom.setOpaque(false);
        // Cửa hàng.
        JPanel pnlCuaHang = layNhom("Thông tin cửa hàng"),
                pnlNghiepVu = layNhom("Quy tắc nghiệp vụ");
        taoTruong(pnlCuaHang, "STORE_NAME", "Tên cửa hàng");
        taoTruong(pnlCuaHang, "STORE_ADDRESS", "Địa chỉ");
        taoTruong(pnlCuaHang, "STORE_PHONE", "Số điện thoại");
        taoTruong(pnlCuaHang, "STORE_EMAIL", "Email");
        taoTruong(pnlCuaHang, "STORE_WEBSITE", "Website");
        taoTruong(
                pnlCuaHang,
                "CARE_DATA_DIR",
                "Thư mục lưu ảnh chăm sóc (trống: PetStation trong thư mục người dùng)");
        taoTruong(pnlCuaHang, "STORE_TAX_CODE", "Mã số thuế");
        for (PrintService dichVu : PrintServiceLookup.lookupPrintServices(null, null))
            cboMayIn.addItem(dichVu.getName());
        if (cboMayIn.getItemCount() == 0) cboMayIn.addItem("Không có máy in");
        cboMayIn.setEnabled(coTheSua);
        pnlCuaHang.add(new JLabel("Máy in hóa đơn"));
        cboMayIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboMayIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCuaHang.add(cboMayIn);
        // Kiểm thử.
        NutDinhDang btnKiemThu = new NutDinhDang("In thử", NutDinhDang.KieuNut.TRONG_SUOT);
        btnKiemThu.addActionListener(
                suKien -> {
                    // Trang.
                    JTextArea txtTrang =
                            new JTextArea(
                                    CaiDatService.lay("STORE_NAME")
                                            + "\n"
                                            + CaiDatService.lay("STORE_ADDRESS")
                                            + "\nIn thử Pet Station\n"
                                            + java.time.LocalDateTime.now());
                    try {
                        txtTrang.print();
                    } catch (java.awt.print.PrinterException ngoaiLe) {
                        HopThoai.hienThiLoi(this, ngoaiLe.getMessage());
                    }
                });
        pnlCuaHang.add(btnKiemThu);
        taoTruong(pnlCuaHang, "BANK_BIN", "BIN ngân hàng (6 số; Vietcombank: 970436)");
        taoTruong(pnlCuaHang, "BANK_ACCOUNT", "Số tài khoản nhận chuyển khoản");
        taoTruong(pnlCuaHang, "BANK_ACCOUNT_NAME", "Chủ tài khoản thực tế (viết HOA không dấu)");
        taoTruong(pnlNghiepVu, "VAT_PERCENT", "Thuế VAT (%)");
        taoTruong(pnlNghiepVu, "LOW_STOCK_THRESHOLD", "Ngưỡng tồn thấp");
        taoTruong(pnlNghiepVu, "EXPIRY_WARN_DAYS", "Cảnh báo hạn dùng (ngày)");
        taoTruong(pnlNghiepVu, "LATE_CHECKOUT_FEE", "Phí trả phòng trễ / ngày (đồng)");
        taoTruong(pnlNghiepVu, "RETURN_WINDOW_DAYS", "Thời hạn đổi trả (ngày)");
        taoTruong(pnlNghiepVu, "WRITEOFF_APPROVAL_LIMIT", "Giá trị xuất hủy cần duyệt (đồng)");
        pnlCacNhom.add(pnlCuaHang);
        pnlCacNhom.add(pnlNghiepVu);
        // Cuộn.
        JScrollPane scrCuon = new JScrollPane(pnlCacNhom);
        scrCuon.setBorder(null);
        add(scrCuon, BorderLayout.CENTER);
        // Các thao tác.
        JPanel pnlCacThaoTac = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT));
        // Tải lại.
        NutDinhDang btnTaiLai = new NutDinhDang("Làm mới", NutDinhDang.KieuNut.TRONG_SUOT);
        btnTaiLai.setToolTipText("Đọc lại cài đặt đã lưu trong SQL Server");
        btnTaiLai.addActionListener(
                suKien -> {
                    if (coThayDoiChuaLuu()
                            && !HopThoai.xacNhan(
                                    this, "Bỏ thay đổi cài đặt chưa lưu và đọc lại dữ liệu?"))
                        return;
                    nap();
                });
        pnlCacThaoTac.add(btnTaiLai);
        // Lưu.
        NutDinhDang btnLuu = new NutDinhDang("Lưu cài đặt", NutDinhDang.KieuNut.CHINH);
        btnLuu.setEnabled(coTheSua);
        btnLuu.addActionListener(suKien -> xuLyLuu());
        pnlCacThaoTac.add(btnLuu);
        add(pnlCacThaoTac, BorderLayout.SOUTH);
        nap();
    }

    private JPanel layNhom(String tieuDe) {
        // Vùng hiển thị.
        JPanel pnlVungHienThi = layThe(new BorderLayout());
        pnlVungHienThi.setLayout(new BoxLayout(pnlVungHienThi, BoxLayout.Y_AXIS));
        pnlVungHienThi.add(taoTieuDeMuc(tieuDe));
        pnlVungHienThi.add(Box.createVerticalStrut(12));
        return pnlVungHienThi;
    }

    private void taoTruong(JPanel pnlVungHienThi, String khoa, String nhan) {
        pnlVungHienThi.add(new JLabel(nhan));
        // Đầu vào.
        JTextField txtDauVao = new JTextField(24);
        txtDauVao.setEnabled(coTheSua);
        txtDauVao.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDauVao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtDauVao.setPreferredSize(new Dimension(280, 36));
        cacDauVao.put(khoa, txtDauVao);
        pnlVungHienThi.add(txtDauVao);
        pnlVungHienThi.add(Box.createVerticalStrut(10));
    }

    private void nap() {
        for (var muc : cacDauVao.entrySet())
            muc.getValue()
                    .setText(
                            (muc.getKey().startsWith("BANK_")
                                            || muc.getKey().equals("CARE_DATA_DIR"))
                                    ? CaiDatService.layTuyChon(muc.getKey())
                                    : CaiDatService.lay(muc.getKey()));
        // Đã chọn.
        cacDauVao.get("RETURN_WINDOW_DAYS").setText("3");
        cacDauVao.get("RETURN_WINDOW_DAYS").setEditable(false);
        String daChon = CaiDatService.lay("PRINTER");
        if (!daChon.isBlank()) cboMayIn.setSelectedItem(daChon);
        ghiNhanDaNap();
    }

    /** Giữ nguyên bản nháp khi điều hướng sang màn hình khác rồi quay lại. */
    private boolean coThayDoiChuaLuu() {
        return !Objects.equals(mayInDaNap, cboMayIn.getSelectedItem())
                || cacDauVao.entrySet().stream()
                        .anyMatch(
                                e ->
                                        !Objects.equals(
                                                e.getValue().getText(),
                                                giaTriDaNap.get(e.getKey())));
    }

    private void ghiNhanDaNap() {
        giaTriDaNap.clear();
        cacDauVao.forEach((k, v) -> giaTriDaNap.put(k, v.getText()));
        mayInDaNap = cboMayIn.getSelectedItem();
    }

    @Override
    public void xuLyLuu() {
        if (!coTheSua) return;
        try {
            Map<String, String> giaTri = new LinkedHashMap<>();
            cacDauVao.forEach((khoa, truong) -> giaTri.put(khoa, truong.getText()));
            String mayIn =
                    "Không có máy in".equals(cboMayIn.getSelectedItem())
                            ? ""
                            : String.valueOf(cboMayIn.getSelectedItem());
            CaiDatService.luuTatCa(giaTri, mayIn);
            nap();
            HopThoai.hienThiThanhCong(this, "Đã lưu cài đặt.");
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
        }
    }

    @Override
    public void xuLyHienThi() {
        if (!coThayDoiChuaLuu()) nap();
    }
}
