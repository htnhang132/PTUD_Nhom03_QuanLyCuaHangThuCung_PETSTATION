package com.petstation.gui.khachhang;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.khachhang.TichDiemService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public final class BoChonKhachHang extends JPanel {
    private final GioHang banHang;

    private final Runnable khiThayDoi;

    private final JTextField oDauVao = new JTextField();

    private final JPanel khungNhan = new JPanel();

    private final JLabel nhanVanBanNhan = new JLabel();

    private final TichDiemService tichDiemService = new TichDiemService();

    public BoChonKhachHang(GioHang banHang, Runnable khiThayDoi) {
        this.banHang = banHang;
        this.khiThayDoi = khiThayDoi;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nhanHienThi =
                new JLabel("Khách hàng (SĐT, mã hoặc tên — Enter để tìm; bỏ trống = khách lẻ)");
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanHienThi.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanHienThi.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(nhanHienThi);
        JPanel khungDong = new JPanel(new BorderLayout(6, 0));
        khungDong.setOpaque(false);
        khungDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        oDauVao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oDauVao.getAccessibleContext().setAccessibleName("Tìm khách hàng");
        oDauVao.addActionListener(suKien -> timKiem());
        JButton btnTim = new JButton("Tìm");
        btnTim.setFocusPainted(false);
        btnTim.addActionListener(suKien -> timKiem());
        khungDong.add(oDauVao, BorderLayout.CENTER);
        khungDong.add(btnTim, BorderLayout.EAST);
        add(khungDong);
        khungNhan.setLayout(new BorderLayout(8, 0));
        khungNhan.setBackground(MauUngDung.mau(0xEAF3FF));
        khungNhan.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.mau(0xBFD6F5)),
                        new EmptyBorder(6, 10, 6, 10)));
        khungNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        nhanVanBanNhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        JButton btnXoa = new JButton("Bỏ chọn");
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(suKien -> xoa());
        JPanel khungSuDungDiem = new JPanel();
        khungSuDungDiem.setOpaque(false);
        khungSuDungDiem.setLayout(new BoxLayout(khungSuDungDiem, BoxLayout.Y_AXIS));
        JLabel nhanNhanSuDung = new JLabel("Đổi 200 điểm → voucher Spa 150.000đ / 60 ngày");
        nhanNhanSuDung.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanNhanSuDung.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungSuDungDiem.add(nhanNhanSuDung);
        khungSuDungDiem.add(Box.createVerticalStrut(4));
        JButton btnDoiVoucher = new JButton("Đổi voucher");
        btnDoiVoucher.setFocusPainted(false);
        btnDoiVoucher.addActionListener(e -> doiVoucher());
        JPanel khungDongDoiVoucher = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        khungDongDoiVoucher.setOpaque(false);
        khungDongDoiVoucher.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungDongDoiVoucher.add(btnDoiVoucher);
        khungSuDungDiem.add(khungDongDoiVoucher);
        JPanel khungVanBan = new JPanel();
        khungVanBan.setOpaque(false);
        khungVanBan.setLayout(new BoxLayout(khungVanBan, BoxLayout.Y_AXIS));
        khungVanBan.add(nhanVanBanNhan);
        khungVanBan.add(Box.createVerticalStrut(5));
        khungVanBan.add(khungSuDungDiem);
        khungNhan.add(khungVanBan, BorderLayout.CENTER);
        khungNhan.add(btnXoa, BorderLayout.EAST);
        khungNhan.setVisible(false);
        add(Box.createVerticalStrut(4));
        add(khungNhan);
        dongBo();
    }

    private String khoaDoiVoucher;
    private int luotTim;
    private int luotDongBo;

    private void doiVoucher() {
        String maKh = banHang.getMaKhachHang();
        if (maKh.isBlank()) return;
        if (!HopThoai.xacNhanDongY(
                this, "Trừ 200 điểm để nhận voucher Spa 150.000đ, hạn 60 ngày?", "Đổi voucher"))
            return;
        if (khoaDoiVoucher == null) khoaDoiVoucher = "DOI-" + UUID.randomUUID();
        String khoaTam = khoaDoiVoucher;
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> tichDiemService.doiVoucher(maKh, khoaTam),
                ma -> {
                    khoaDoiVoucher = null;
                    banHang.setKhachHang(
                            banHang.getMaKhachHang(),
                            banHang.getTenKhachHang(),
                            banHang.getKhachHangDiem() - 200);
                    dongBo();
                    khiThayDoi.run();
                    HopThoai.hienThiThongTin(
                            this,
                            "Voucher: " + ma + "\nDùng tại thanh toán Spa của khách hàng này.");
                });
    }

    private void timKiem() {
        String tuKhoa = oDauVao.getText().trim();
        if (tuKhoa.isEmpty()) {
            xoa();
            return;
        }
        int luot = ++luotTim;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTim,
                () -> timKhachHangPhuHop(tuKhoa),
                cacKetQua -> {
                    if (cacKetQua.isEmpty()) {
                        moThemKhachMoiNeuCan(tuKhoa);
                        return;
                    }
                    Object[] daChon =
                            cacKetQua.size() == 1
                                    ? cacKetQua.get(0)
                                    : chonTrongNhieuKetQua(cacKetQua);
                    if (daChon == null) return;
                    apDungKhachHangDaChon(daChon);
                });
    }

    private static List<Object[]> timKhachHangPhuHop(String tuKhoa) {
        String cacChuSo = tuKhoa.replaceAll("[\\s.]", "");
        List<Object[]> cacKetQua = new ArrayList<>();
        for (Object[] dongDuLieu : DanhMucService.macDinh().layKhachHang()) {
            if ("Ngừng hoạt động".equals(dongDuLieu[8])) continue;
            if (String.valueOf(dongDuLieu[2]).equals(cacChuSo)
                    || String.valueOf(dongDuLieu[0]).equalsIgnoreCase(tuKhoa)) {
                cacKetQua.clear();
                cacKetQua.add(dongDuLieu);
                break;
            }
            if (String.valueOf(dongDuLieu[1]).toLowerCase().contains(tuKhoa.toLowerCase()))
                cacKetQua.add(dongDuLieu);
        }
        return cacKetQua;
    }

    private void moThemKhachMoiNeuCan(String tuKhoa) {
        if (!HopThoai.xacNhan(
                this,
                "Không tìm thấy khách hàng \""
                        + tuKhoa
                        + "\". Bạn có muốn thêm khách hàng mới không?")) return;
        try {
            String sdt = tuKhoa.replaceAll("[\\s.]", "");
            if (!sdt.matches("0\\d{9}")) sdt = "";
            Object[] moi = DanhMucKhachHangPanel.moThem(this, sdt);
            if (moi == null) return;
            if (!"Đang hoạt động".equals(moi[8])) {
                HopThoai.hienThiCanhBao(
                        this,
                        "Khách vừa lưu đã ngừng hoạt động. Vui lòng chọn hồ sơ đang hoạt động.");
                return;
            }
            apDungKhachHangDaChon(moi);
        } catch (Exception loi) {
            HopThoai.hienThiCanhBao(this, "Không thể mở thêm khách hàng: " + loi.getMessage());
        }
    }

    private Object[] chonTrongNhieuKetQua(List<Object[]> cacKetQua) {
        String[] cacLuaChon =
                cacKetQua.stream()
                        .map(d -> d[0] + " - " + d[1] + " (" + d[2] + ")")
                        .toArray(String[]::new);
        Object chon =
                HopThoai.chonMuc(
                        this,
                        "Có nhiều khách phù hợp, chọn một:",
                        "Chọn khách hàng",
                        cacLuaChon,
                        cacLuaChon[0]);
        if (chon == null) return null;
        for (int chiSo = 0; chiSo < cacLuaChon.length; chiSo++)
            if (cacLuaChon[chiSo].equals(chon)) return cacKetQua.get(chiSo);
        return cacKetQua.get(0);
    }

    private void apDungKhachHangDaChon(Object[] khach) {
        banHang.setKhachHang(
                String.valueOf(khach[0]),
                String.valueOf(khach[1]),
                DinhDang.doc(String.valueOf(khach[7])));
        oDauVao.setText("");
        dongBo();
        khiThayDoi.run();
    }

    public void xoa() {
        banHang.setKhachHang("", "", 0);
        oDauVao.setText("");
        dongBo();
        khiThayDoi.run();
    }

    public void dongBo() {
        String maKh = banHang.getMaKhachHang();
        boolean co = !maKh.isEmpty();
        khungNhan.setVisible(co);
        if (co) {
            nhanVanBanNhan.setText(
                    banHang.getTenKhachHang() + "  ·  " + banHang.getKhachHangDiem() + " điểm");
            int luot = ++luotDongBo;
            TacVuNen.chayCoPhien(
                    this,
                    () -> luot == luotDongBo && maKh.equals(banHang.getMaKhachHang()),
                    () -> {
                        for (Object[] dongDuLieu : DanhMucService.macDinh().layKhachHang()) {
                            if (dongDuLieu[0].equals(maKh)) return String.valueOf(dongDuLieu[6]);
                        }
                        return "";
                    },
                    hang ->
                            nhanVanBanNhan.setText(
                                    banHang.getTenKhachHang()
                                            + "  ·  Hạng "
                                            + hang
                                            + "  ·  "
                                            + banHang.getKhachHangDiem()
                                            + " điểm"));
        }
        revalidate();
        repaint();
    }
}
