package com.petstation.gui.chung.thanhphan;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public final class BieuMauTruong {
    public static final class LuaChon {
        private final String giaTri;
        private final String nhan;

        public LuaChon(String giaTri, String nhan) {
            this.giaTri = giaTri;
            this.nhan = nhan;
        }

        public String giaTri() {
            return giaTri;
        }

        public String nhan() {
            return nhan;
        }

        @Override
        public String toString() {
            return nhan;
        }
    }

    public static final class TruongDuLieu {
        private final String khoa;
        private final String nhan;
        private final String banDau;
        private final List<LuaChon> cacLuaChon;

        public TruongDuLieu(String khoa, String nhan, String banDau, List<LuaChon> cacLuaChon) {
            this.khoa = khoa;
            this.nhan = nhan;
            this.banDau = banDau;
            this.cacLuaChon = cacLuaChon;
        }

        public String khoa() {
            return khoa;
        }

        public String nhan() {
            return nhan;
        }

        public String banDau() {
            return banDau;
        }

        public List<LuaChon> cacLuaChon() {
            return cacLuaChon;
        }

        public static TruongDuLieu chuyenThanhChuoi(String khoa, String nhan, String banDau) {
            return new TruongDuLieu(khoa, nhan, banDau, null);
        }

        public static TruongDuLieu layLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
            return new TruongDuLieu(
                    khoa,
                    nhan,
                    "",
                    cacDong.stream()
                            .map(
                                    dongDuLieu ->
                                            new LuaChon(
                                                    String.valueOf(dongDuLieu[0]),
                                                    String.valueOf(dongDuLieu[1])))
                            .toList());
        }
    }

    private BieuMauTruong() {}

    public static Map<String, String> layYeuCauNhap(
            Component cha, String tieuDe, TruongDuLieu... cacTruong) {
        return layYeuCauNhapCoKiemTra(cha, tieuDe, tieuDe, giaTri -> null, cacTruong);
    }

    public static Map<String, String> layYeuCauNhapCoKiemTra(
            Component cha,
            String tieuDe,
            String nhanXacNhan,
            Function<Map<String, String>, String> kiemTra,
            TruongDuLieu... cacTruong) {
        JPanel khungBieuMau = new JPanel(new GridBagLayout());
        khungBieuMau.setBackground(MauUngDung.NEN_KEM);
        khungBieuMau.setBorder(new EmptyBorder(6, 6, 6, 6));
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(6, 6, 6, 6);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        Map<String, JComponent> cacDauVao = new LinkedHashMap<>();
        for (int chiSo = 0; chiSo < cacTruong.length; chiSo++) {
            TruongDuLieu truongDangXet = cacTruong[chiSo];
            giaTriDangXet.gridy = chiSo;
            giaTriDangXet.gridx = 0;
            giaTriDangXet.weightx = 0;
            JLabel nhan = new JLabel(truongDangXet.nhan());
            nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
            nhan.setForeground(MauUngDung.NAU);
            khungBieuMau.add(nhan, giaTriDangXet);
            JComponent dauVao;
            if (truongDangXet.cacLuaChon() == null)
                dauVao = new JTextField(truongDangXet.banDau(), 28);
            else {
                if (truongDangXet.cacLuaChon().isEmpty()) {
                    HopThoai.hienThiCanhBao(
                            cha,
                            "Chưa có "
                                    + truongDangXet.nhan().toLowerCase()
                                    + " phù hợp. Hãy bổ sung dữ liệu trước.");
                    return null;
                }
                dauVao = new JComboBox<>(truongDangXet.cacLuaChon().toArray(LuaChon[]::new));
            }
            dauVao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            dauVao.setPreferredSize(new Dimension(280, 34));
            if (dauVao instanceof JComboBox<?> cbo && truongDangXet.banDau() != null) {
                for (int i = 0; i < cbo.getItemCount(); i++)
                    if (cbo.getItemAt(i) instanceof LuaChon chon
                            && chon.giaTri().equals(truongDangXet.banDau()))
                        cbo.setSelectedIndex(i);
            }
            nhan.setLabelFor(dauVao);
            dauVao.setName(truongDangXet.khoa());
            dauVao.getAccessibleContext().setAccessibleName(truongDangXet.nhan());
            cacDauVao.put(truongDangXet.khoa(), dauVao);
            giaTriDangXet.gridx = 1;
            giaTriDangXet.weightx = 1;
            khungBieuMau.add(dauVao, giaTriDangXet);
        }
        if (!cacDauVao.isEmpty()) {
            JComponent dauVaoDauTien = cacDauVao.values().iterator().next();
            dauVaoDauTien.addAncestorListener(
                    new AncestorListener() {
                        @Override
                        public void ancestorAdded(AncestorEvent event) {
                            SwingUtilities.invokeLater(dauVaoDauTien::requestFocusInWindow);
                        }

                        @Override
                        public void ancestorRemoved(AncestorEvent event) {}

                        @Override
                        public void ancestorMoved(AncestorEvent event) {}
                    });
        }
        return hienThiVaKiemTra(
                cha, tieuDe, nhanXacNhan, kiemTra, cacTruong, khungBieuMau, cacDauVao);
    }

    private static Map<String, String> hienThiVaKiemTra(
            Component cha,
            String tieuDe,
            String nhanXacNhan,
            Function<Map<String, String>, String> kiemTra,
            TruongDuLieu[] cacTruong,
            JPanel khungBieuMau,
            Map<String, JComponent> cacDauVao) {
        while (true) {
            if (HopThoai.tuyChon(
                            cha, khungBieuMau, tieuDe, new String[] {nhanXacNhan, "Hủy"}, false)
                    != 0) return null;
            Map<String, String> cacGiaTri = new LinkedHashMap<>();
            for (TruongDuLieu truongDangXet : cacTruong) {
                JComponent dauVao = cacDauVao.get(truongDangXet.khoa());
                String giaTri = "";
                if (dauVao instanceof JTextField oGiaTriTam) {
                    giaTri = oGiaTriTam.getText().trim();
                } else if (dauVao instanceof JComboBox<?> cbo
                        && cbo.getSelectedItem() instanceof LuaChon chon) {
                    giaTri = chon.giaTri();
                }
                cacGiaTri.put(truongDangXet.khoa(), giaTri);
            }
            String truongLoi = kiemTra.apply(cacGiaTri);
            if (truongLoi == null) return cacGiaTri;
            JComponent dauVaoLoi = cacDauVao.get(truongLoi);
            if (dauVaoLoi != null) SwingUtilities.invokeLater(dauVaoLoi::requestFocusInWindow);
        }
    }
}
