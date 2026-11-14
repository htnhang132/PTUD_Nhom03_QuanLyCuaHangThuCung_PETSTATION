package com.petstation.gui.kho;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BoSoanThaoTruong;
import com.petstation.util.TaiAnhSanPham;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class BoChonAnhSanPham implements BoSoanThaoTruong {
    private final JPanel khungAnh = new JPanel(new BorderLayout(0, 4));
    private final AnhSanPhamPanel khungXem = new AnhSanPhamPanel(100);
    private final NutDinhDang btnChon =
            new NutDinhDang("Chọn ảnh từ máy…", NutDinhDang.KieuNut.TRONG_SUOT);
    private String duongDan = "";
    private boolean duocSua;
    private boolean dangTai;
    private long phienBan;

    public BoChonAnhSanPham() {
        khungAnh.setOpaque(false);
        khungAnh.setBorder(null);
        khungXem.setOpaque(false);
        khungXem.setBorder(null);
        JPanel khungXemBao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        khungXemBao.setOpaque(false);
        khungXemBao.setBorder(null);
        khungXemBao.add(khungXem);
        JPanel khungNutBao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        khungNutBao.setOpaque(false);
        btnChon.setPreferredSize(new Dimension(150, 32));
        btnChon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        khungNutBao.add(btnChon);
        khungAnh.add(khungXemBao, BorderLayout.CENTER);
        khungAnh.add(khungNutBao, BorderLayout.SOUTH);
        btnChon.addActionListener(
                e -> {
                    JFileChooser chon = new JFileChooser();
                    chon.setAcceptAllFileFilterUsed(false);
                    chon.setFileFilter(
                            new FileNameExtensionFilter("Ảnh JPG, PNG", "jpg", "jpeg", "png"));
                    if (chon.showOpenDialog(khungAnh) != JFileChooser.APPROVE_OPTION) return;
                    dangTai = true;
                    btnChon.setEnabled(false);
                    long lanChon = phienBan;
                    TacVuNen.chay(
                            () -> TaiAnhSanPham.saoChep(chon.getSelectedFile().toPath()),
                            moi -> {
                                dangTai = false;
                                btnChon.setEnabled(duocSua);
                                if (duocSua && phienBan == lanChon) dat(moi);
                            },
                            loi -> {
                                dangTai = false;
                                btnChon.setEnabled(duocSua);
                                HopThoai.hienThiLoi(
                                        khungAnh,
                                        "Không nạp được ảnh: " + TacVuNen.thongDiepLoi(loi));
                            });
                });
    }

    @Override
    public JComponent layThanhPhan() {
        return khungAnh;
    }

    @Override
    public String lay() {
        if (dangTai) throw new IllegalArgumentException("Vui lòng chờ ảnh tải xong.");
        return duongDan;
    }

    @Override
    public void dat(String giaTri) {
        phienBan++;
        duongDan = giaTri == null || "null".equals(giaTri) ? "" : giaTri;
        khungXem.datAnh(duongDan);
    }

    @Override
    public void datDuocBat(boolean giaTri) {
        duocSua = giaTri;
        btnChon.setEnabled(giaTri && !dangTai);
    }
}
