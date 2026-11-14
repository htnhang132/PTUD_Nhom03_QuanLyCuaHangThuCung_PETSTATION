package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.dao.banhang.PhienLamViecDAO;
import com.petstation.gui.banhang.CaThuNganPanel;
import com.petstation.service.banhang.CaThuNganService;

import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.Proxy;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

class CaThuNganPanelTest {
    @Test
    void giaoDienCaThuNganGonTrongMotTrangKhongCuonVaKhongDemMenhGia() throws Exception {
        PhienLamViecDAO daoGia =
                (PhienLamViecDAO)
                        Proxy.newProxyInstance(
                                PhienLamViecDAO.class.getClassLoader(),
                                new Class<?>[] {PhienLamViecDAO.class},
                                (proxy, method, args) ->
                                        method.getReturnType().equals(Optional.class)
                                                ? Optional.empty()
                                                : null);
        SwingUtilities.invokeAndWait(
                () -> {
                    CaThuNganPanel panel = new CaThuNganPanel(new CaThuNganService(daoGia));
                    assertThat(coThanhPhan(panel, JScrollPane.class)).isFalse();
                    assertThat(coThanhPhan(panel, JCheckBox.class)).isFalse();

                    Dimension ktPanel = panel.getPreferredSize();
                    assertThat(ktPanel.height).isLessThanOrEqualTo(485);
                    assertThat(ktPanel.width).isLessThanOrEqualTo(1060);

                    panel.setSize(1050, 488);
                    panel.doLayout();
                    assertThat(panel.getHeight()).isEqualTo(488);

                    panel.setSize(1320, 560);
                    panel.doLayout();
                    assertThat(panel.getWidth()).isEqualTo(1320);
                });
    }

    private static boolean coThanhPhan(Container container, Class<?> kieu) {
        for (Component con : container.getComponents()) {
            if (kieu.isInstance(con)) return true;
            if (con instanceof Container nhom && coThanhPhan(nhom, kieu)) return true;
        }
        return false;
    }
}
