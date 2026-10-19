package com.petstation.gui.system;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.CaiDatService;
import com.petstation.service.NhatKyHeThongService;

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

    /**
     * Khởi tạo CaiDatPanel với các giá trị được cung cấp.
     *
     * @param coTheSua có thể sửa
     */
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
        taoTruong(pnlCuaHang, "STORE_TAX_CODE", "Mã số thuế");
        for (PrintService dichVu : PrintServiceLookup.lookupPrintServices(null, null))
            cboMayIn.addItem(dichVu.getName());
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (cboMayIn.getItemCount() == 0) cboMayIn.addItem("Không có máy in");
        cboMayIn.setEnabled(coTheSua);
        pnlCuaHang.add(new JLabel("Máy in hóa đơn"));
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
        NutDinhDang btnTaiLai = new NutDinhDang("Tải lại", NutDinhDang.KieuNut.TRONG_SUOT);
        btnTaiLai.addActionListener(suKien -> nap());
        pnlCacThaoTac.add(btnTaiLai);
        // Lưu.
        NutDinhDang btnLuu = new NutDinhDang("Lưu cài đặt", NutDinhDang.KieuNut.CHINH);
        btnLuu.setEnabled(coTheSua);
        btnLuu.addActionListener(suKien -> xuLyLuu());
        pnlCacThaoTac.add(btnLuu);
        add(pnlCacThaoTac, BorderLayout.SOUTH);
        nap();
    }

    /**
     * Lấy nhóm trong CaiDatPanel.
     *
     * @param tieuDe tiêu đề
     * @return kết quả lấy nhóm
     */
    private JPanel layNhom(String tieuDe) {
        // Vùng hiển thị.
        JPanel pnlVungHienThi = layThe(new GridLayout(0, 1, 5, 5));
        pnlVungHienThi.add(taoTieuDeMuc(tieuDe));
        return pnlVungHienThi;
    }

    /**
     * Tạo trường trong CaiDatPanel.
     *
     * @param pnlVungHienThi vùng hiển thị
     * @param khoa khóa
     * @param nhan nhãn
     */
    private void taoTruong(JPanel pnlVungHienThi, String khoa, String nhan) {
        pnlVungHienThi.add(new JLabel(nhan));
        // Đầu vào.
        JTextField txtDauVao = new JTextField(24);
        txtDauVao.setEnabled(coTheSua);
        cacDauVao.put(khoa, txtDauVao);
        pnlVungHienThi.add(txtDauVao);
    }

    /** Nạp trong CaiDatPanel. */
    private void nap() {
        for (var muc : cacDauVao.entrySet())
            muc.getValue().setText(CaiDatService.lay(muc.getKey()));
        // Đã chọn.
        String daChon = CaiDatService.lay("PRINTER");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (!daChon.isBlank()) cboMayIn.setSelectedItem(daChon);
    }

    /** Xử lý lưu trong CaiDatPanel. */
    @Override
    public void xuLyLuu() {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (!coTheSua) return;
        try {
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (cacDauVao.get("STORE_NAME").getText().isBlank())
                throw new IllegalArgumentException("Cần tên cửa hàng.");
            // VAT.
            int vat = Integer.parseInt(cacDauVao.get("VAT_PERCENT").getText()),
                    thap = Integer.parseInt(cacDauVao.get("LOW_STOCK_THRESHOLD").getText()),
                    hanSuDung = Integer.parseInt(cacDauVao.get("EXPIRY_WARN_DAYS").getText()),
                    doiTra = Integer.parseInt(cacDauVao.get("RETURN_WINDOW_DAYS").getText());
            // Trễ.
            long tre = Long.parseLong(cacDauVao.get("LATE_CHECKOUT_FEE").getText()),
                    gioiHan = Long.parseLong(cacDauVao.get("WRITEOFF_APPROVAL_LIMIT").getText());
            thucHienGiaoDich(
                    ketNoi -> {
                        CaiDatService.capNhat(vat, thap, hanSuDung, tre, doiTra, gioiHan);
                        for (var muc : cacDauVao.entrySet())
                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (muc.getKey().startsWith("STORE_"))
                                CaiDatService.ghi(muc.getKey(), muc.getValue().getText().trim());
                        CaiDatService.ghi(
                                "PRINTER",
                                "Không có máy in".equals(cboMayIn.getSelectedItem())
                                        ? ""
                                        : String.valueOf(cboMayIn.getSelectedItem()));
                        NhatKyHeThongService.ghiNhatKy("Sửa", "Cài đặt hệ thống");
                        return null;
                    });
            HopThoai.hienThiThanhCong(this, "Đã lưu cài đặt.");
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
        }
    }

    /** Xử lý hiển thị trong CaiDatPanel. */
    @Override
    public void xuLyHienThi() {
        nap();
    }
}
