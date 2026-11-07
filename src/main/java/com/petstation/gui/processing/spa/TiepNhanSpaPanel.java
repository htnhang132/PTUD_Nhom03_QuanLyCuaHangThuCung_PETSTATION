package com.petstation.gui.processing.spa;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.SpaService;

import java.awt.Dialog;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class TiepNhanSpaPanel extends NghiepVuSpaPanel {
    public TiepNhanSpaPanel(
        GioHang banHang,
        Consumer<MaManHinh> dieuHuong
    ) {
        super(
            LoaiNghiepVu.TIEP_NHAN_SPA,
            banHang,
            dieuHuong
        );

        themNut(
            pnlCacThaoTac,
            "+ Lập phiếu Spa",
            this::tiepNhan
        );
        themNut(pnlCacThaoTac, "Phiếu Spa đang treo", () -> moLapPhieu(true));

        themNut(
            pnlCacThaoTac,
            "Thêm dịch vụ",
            this::themDichVu
        );

        themNut(
            pnlCacThaoTac,
            "Bắt đầu làm",
            () -> SpaService.layTrangThaiSpa(
                layDongChon(),
                "DANG_THUC_HIEN"
            )
        );

        themNut(
            pnlCacThaoTac,
            "Hoàn thành",
            () -> SpaService.layTrangThaiSpa(
                layDongChon(),
                "HOAN_TAT"
            )
        );

        themNut(
            pnlCacThaoTac,
            "Thanh toán",
            () -> quyetToan("SPA")
        );

        themNut(
            pnlCacThaoTac,
            "Chi tiết dịch vụ",
            this::laySpaChiTiet
        );
    }

    @Override
    protected void tiepNhan() {
        moLapPhieu(false);
    }

    private void moLapPhieu(boolean moPhieuTreo) {
        JDialog hopThoai = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Lập phiếu tiếp nhận Spa",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        LapPhieuSpaPanel bieuMau = new LapPhieuSpaPanel(
            id -> {
                banHang.getQuyTrinh().chonSpa(id);
                hopThoai.dispose();
            },

            () -> chonLichHen(
                (LapPhieuSpaPanel)
                    hopThoai.getContentPane()
            ),
            hopThoai::dispose
        );

        hopThoai.setContentPane(bieuMau);

        hopThoai.setDefaultCloseOperation(
            WindowConstants.DO_NOTHING_ON_CLOSE
        );
        hopThoai.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    if (bieuMau.xacNhanDong()) hopThoai.dispose();
                } catch (RuntimeException loi) {
                    com.petstation.gui.components.HopThoai.hienThiCanhBao(bieuMau, loi.getMessage());
                }
            }
        });

        hopThoai.setMinimumSize(
            new Dimension(800, 600)
        );

        hopThoai.setSize(1050, 750);
        hopThoai.setLocationRelativeTo(this);
        if (moPhieuTreo) SwingUtilities.invokeLater(bieuMau::moDanhSachPhieuTreo);
        hopThoai.setVisible(true);
    }

    private void chonLichHen(LapPhieuSpaPanel bieuMau) {
        JDialog lich = new JDialog(
            SwingUtilities.getWindowAncestor(bieuMau),
            "Chọn lịch hẹn Spa",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        lich.setContentPane(
            new LichHenSpaPanel(
                banHang,
                dieuHuong,
                id -> {
                    bieuMau.nhanLichHen(id);
                    lich.dispose();
                }
            )
        );

        lich.setDefaultCloseOperation(
            WindowConstants.DISPOSE_ON_CLOSE
        );

        lich.setSize(1100, 700);
        lich.setLocationRelativeTo(bieuMau);
        lich.setVisible(true);
    }
}
