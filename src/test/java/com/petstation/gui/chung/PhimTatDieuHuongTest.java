package com.petstation.gui.chung;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.model.chung.MaManHinh;

import org.junit.jupiter.api.Test;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

class PhimTatDieuHuongTest {
    @Test
    void phimF1DenF4MoDungManHinhVaKhopMenu() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    Map<String, MaManHinh> mongDoi =
                            Map.of(
                                    "F1", MaManHinh.LAP_HOA_DON,
                                    "F2", MaManHinh.DOI_TRA_HANG_HOA,
                                    "F3", MaManHinh.QUAN_LY_SPA,
                                    "F4", MaManHinh.QUAN_LY_LUU_TRU,
                                    "F6", MaManHinh.TIM_KIEM_HOA_DON);
                    List<CauTrucMenu.Muc> cacMuc =
                            CauTrucMenu.layCacNhom().stream()
                                    .flatMap(n -> n.cacMuc().stream())
                                    .filter(m -> m.phimTat() != null)
                                    .toList();
                    assertThat(cacMuc.stream().map(CauTrucMenu.Muc::phimTat))
                            .doesNotHaveDuplicates();
                    assertThat(cacMuc).hasSize(mongDoi.size());
                    JRootPane goc = new JRootPane();
                    List<MaManHinh> daMo = new ArrayList<>();
                    CauTrucMenu.dangKyPhimTatDieuHuong(goc, daMo::add);
                    for (var muc : cacMuc) {
                        assertThat(muc.manHinh()).isEqualTo(mongDoi.get(muc.phimTat()));
                        Object lenh =
                                goc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                        .get(KeyStroke.getKeyStroke(muc.phimTat()));
                        assertThat(lenh).isNotNull();
                        goc.getActionMap()
                                .get(lenh)
                                .actionPerformed(new ActionEvent(goc, 0, "test"));
                        assertThat(daMo.get(daMo.size() - 1)).isEqualTo(muc.manHinh());
                    }
                    assertThat(
                                    goc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                            .get(KeyStroke.getKeyStroke("alt F1")))
                            .isNull();
                });
    }
}
