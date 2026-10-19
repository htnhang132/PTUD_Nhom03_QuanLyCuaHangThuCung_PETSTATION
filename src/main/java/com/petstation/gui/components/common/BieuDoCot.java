package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 * Cung cấp giao diện và xử lý tương tác cho đơn giản thanh biểu đồ.
 *
 * <p>Biểu đồ cột đơn giản vẽ bằng Java2D (không cần thư viện ngoài).
 */
public final class BieuDoCot extends JComponent {
    /** Các nhãn. */
    private String[] cacNhan = new String[0];

    /** Các giá trị. */
    private double[] cacGiaTri = new double[0];

    /** Đơn vị. */
    private String donVi = "";

    /** Tiền tệ. */
    private boolean tienTe = true;

    /** Màu. */
    private Color mau = MauUngDung.DO_THUONG_HIEU;

    /** Khởi tạo BieuDoCot với các giá trị được cung cấp. */
    public BieuDoCot() {
        setPreferredSize(new Dimension(400, 240));
        setOpaque(false);
    }

    /**
     * Đặt dữ liệu trong BieuDoCot.
     *
     * @param cacNhan các nhãn
     * @param cacGiaTri các giá trị
     * @param tienTe tiền tệ
     * @param donVi đơn vị
     */
    public void datDuLieu(String[] cacNhan, double[] cacGiaTri, boolean tienTe, String donVi) {
        this.cacNhan = cacNhan;
        this.cacGiaTri = cacGiaTri;
        this.tienTe = tienTe;
        this.donVi = donVi == null ? "" : donVi;
        repaint();
    }

    /**
     * Đặt màu thanh trong BieuDoCot.
     *
     * @param giaTriDangXet giá trị đang xét
     */
    public void datMauThanh(Color giaTriDangXet) {
        this.mau = giaTriDangXet;
        repaint();
    }

    /**
     * Định dạng trong BieuDoCot.
     *
     * @param giaTri giá trị
     * @return kết quả định dạng
     */
    private String dinhDang(double giaTri) {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (!tienTe)
            return giaTri == Math.rint(giaTri)
                    ? String.valueOf((long) giaTri)
                    : String.format("%.1f", giaTri);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTri >= 1_000_000)
            return String.format("%.1ftr", giaTri / 1_000_000).replace(".0tr", "tr");
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTri >= 1_000) return String.format("%.0fk", giaTri / 1_000);
        return String.valueOf((long) giaTri);
    }

    /**
     * Vẽ thành phần trong BieuDoCot.
     *
     * @param doHoa0 đồ họa 0
     */
    @Override
    protected void paintComponent(Graphics doHoa0) {
        // Đồ họa.
        Graphics2D doHoa = (Graphics2D) doHoa0.create();
        doHoa.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        doHoa.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Chiều rộng.
        int chieuRong = getWidth(), chieuCao = getHeight();
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (cacGiaTri.length == 0) {
            doHoa.setColor(MauUngDung.VAN_BAN_XAM);
            doHoa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            doHoa.drawString("Chưa có dữ liệu", chieuRong / 2 - 40, chieuCao / 2);
            doHoa.dispose();
            return;
        }
        // Trái.
        int trai = 10, phai = 10, tren = 26, duoi = 30;
        // Lớn nhất.
        double lonNhat = 0;
        for (double giaTri : cacGiaTri) lonNhat = Math.max(lonNhat, giaTri);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (lonNhat <= 0) lonNhat = 1;
        doHoa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        // Thông số phông chữ.
        FontMetrics thongSoPhongChu = doHoa.getFontMetrics();
        // Biểu đồ chiều cao.
        int bieuDoChieuCao = chieuCao - tren - duoi;
        doHoa.setColor(MauUngDung.VIEN);
        doHoa.setStroke(new BasicStroke(1f));
        doHoa.drawLine(trai, tren + bieuDoChieuCao, chieuRong - phai, tren + bieuDoChieuCao);
        // Số phần tử.
        int soPhanTu = cacGiaTri.length;
        // Ô lịch.
        double oLich = (chieuRong - trai - phai) / (double) soPhanTu;
        // Thanh chiều rộng.
        int thanhChieuRong = (int) Math.max(8, Math.min(60, oLich * 0.6));
        for (int chiSo = 0; chiSo < soPhanTu; chiSo++) {
            // Chiều cao nút.
            int chieuCaoNut = (int) Math.round(cacGiaTri[chiSo] / lonNhat * (bieuDoChieuCao - 4));
            // Tọa độ ngang.
            int toaDoNgang = (int) (trai + oLich * chiSo + (oLich - thanhChieuRong) / 2);
            // Tọa độ dọc.
            int toaDoDoc = tren + bieuDoChieuCao - chieuCaoNut;
            doHoa.setColor(mau);
            doHoa.fillRoundRect(
                    toaDoNgang, toaDoDoc, thanhChieuRong, Math.max(chieuCaoNut, 1), 6, 6);
            doHoa.setColor(MauUngDung.VAN_BAN_TOI);
            // Giá trị.
            String giaTri = dinhDang(cacGiaTri[chiSo]) + (donVi.isEmpty() ? "" : " " + donVi);
            doHoa.drawString(
                    giaTri,
                    toaDoNgang + (thanhChieuRong - thongSoPhongChu.stringWidth(giaTri)) / 2,
                    toaDoDoc - 4);
            doHoa.setColor(MauUngDung.VAN_BAN_XAM);
            // Dòng đang xét.
            String dongDangXet = chiSo < cacNhan.length ? cacNhan[chiSo] : "";
            // Lớn nhất các ký tự.
            int lonNhatCacKyTu = Math.max(4, (int) (oLich / 6.5));
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (dongDangXet.length() > lonNhatCacKyTu)
                dongDangXet = dongDangXet.substring(0, Math.max(1, lonNhatCacKyTu - 1)) + "…";
            doHoa.drawString(
                    dongDangXet,
                    (int)
                            (trai
                                    + oLich * chiSo
                                    + (oLich - thongSoPhongChu.stringWidth(dongDangXet)) / 2),
                    tren + bieuDoChieuCao + 16);
        }
        doHoa.dispose();
    }
}
