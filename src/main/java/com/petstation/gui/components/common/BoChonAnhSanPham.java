package com.petstation.gui.components.common;

import com.petstation.util.TaiAnhSanPham;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Ô ảnh trong form danh mục; chỉ lưu đường dẫn tương đối sau khi sao chép thành công. */
public final class BoChonAnhSanPham implements BoSoanThaoTruong {
    private final JPanel pnlAnh = new JPanel(new BorderLayout(0, 4));
    private final AnhSanPhamPanel pnlXem = new AnhSanPhamPanel(100);
    private final JButton btnChon = new JButton("Chọn ảnh từ máy…");
    private String duongDan = "";
    private boolean duocSua;
    private boolean dangTai;
    private long phienBan;

    public BoChonAnhSanPham() {
        pnlAnh.setOpaque(false);
        pnlXem.setOpaque(false);
        pnlXem.setBorder(null);

        JPanel pnlXemBao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlXemBao.setOpaque(false);
        pnlXemBao.add(pnlXem);

        JPanel pnlNutBao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        pnlNutBao.setOpaque(false);
        btnChon.setPreferredSize(new Dimension(120, 26));
        btnChon.setFont(com.petstation.gui.components.MauUngDung.taoPhongChu(java.awt.Font.PLAIN, 11));
        pnlNutBao.add(btnChon);

        pnlAnh.add(pnlXemBao, BorderLayout.CENTER);
        pnlAnh.add(pnlNutBao, BorderLayout.SOUTH);
        btnChon.addActionListener(
                e -> {
                    JFileChooser chon = new JFileChooser();
                    chon.setAcceptAllFileFilterUsed(false);
                    chon.setFileFilter(
                            new FileNameExtensionFilter("Ảnh JPG, PNG", "jpg", "jpeg", "png"));
                    if (chon.showOpenDialog(pnlAnh) != JFileChooser.APPROVE_OPTION) return;
                    dangTai = true;
                    btnChon.setEnabled(false);
                    long lanChon = phienBan;
                    new SwingWorker<String, Void>() {
                        @Override
                        protected String doInBackground() throws Exception {
                            return TaiAnhSanPham.saoChep(chon.getSelectedFile().toPath());
                        }

                        @Override
                        protected void done() {
                            try {
                                String moi = get();
                                if (duocSua && phienBan == lanChon) dat(moi);
                            } catch (Exception loi) {
                                JOptionPane.showMessageDialog(
                                        pnlAnh,
                                        "Không nạp được ảnh: "
                                                + (loi.getCause() == null
                                                        ? loi.getMessage()
                                                        : loi.getCause().getMessage()),
                                        "Ảnh sản phẩm",
                                        JOptionPane.ERROR_MESSAGE);
                            } finally {
                                dangTai = false;
                                btnChon.setEnabled(duocSua);
                            }
                        }
                    }.execute();
                });
    }

    @Override
    public JComponent layThanhPhan() {
        return pnlAnh;
    }

    @Override
    public String lay() {
        if (dangTai) throw new IllegalStateException("Vui lòng chờ ảnh tải xong.");
        return duongDan;
    }

    @Override
    public void dat(String giaTri) {
        phienBan++;
        duongDan = giaTri == null || "null".equals(giaTri) ? "" : giaTri;
        pnlXem.datAnh(duongDan);
    }

    @Override
    public void datDuocBat(boolean giaTri) {
        duocSua = giaTri;
        btnChon.setEnabled(giaTri && !dangTai);
    }
}
