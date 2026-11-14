package com.petstation.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Savepoint;

public class ThaoTacSQLTest {
    public static AutoCloseable chayTrongKetNoiGia() {
        Savepoint diemLuuGia =
                (Savepoint)
                        Proxy.newProxyInstance(
                                Savepoint.class.getClassLoader(),
                                new Class<?>[] {Savepoint.class},
                                (proxy, method, args) -> null);
        Connection ketNoiGia =
                (Connection)
                        Proxy.newProxyInstance(
                                Connection.class.getClassLoader(),
                                new Class<?>[] {Connection.class},
                                (proxy, method, args) -> {
                                    if ("setSavepoint".equals(method.getName())) return diemLuuGia;
                                    return null;
                                });
        ThaoTacSQL.ganKetNoiHienTai(ketNoiGia);
        return () -> ThaoTacSQL.ganKetNoiHienTai(null);
    }

    @Test
    @DisplayName("demSoThamSo đếm chính xác số lượng dấu ? ngoài chuỗi và comment")
    void testDemSoThamSo() {
        assertThat(ThaoTacSQL.demSoThamSo(null)).isZero();
        assertThat(ThaoTacSQL.demSoThamSo("")).isZero();
        assertThat(ThaoTacSQL.demSoThamSo("SELECT * FROM san_pham")).isZero();
        assertThat(ThaoTacSQL.demSoThamSo("SELECT * FROM san_pham WHERE ma_san_pham=?"))
                .isEqualTo(1);
        assertThat(
                        ThaoTacSQL.demSoThamSo(
                                "SELECT * FROM san_pham WHERE ma_san_pham=? AND gia_ban > ?"))
                .isEqualTo(2);

        // Chuỗi ký tự chứa dấu ?
        assertThat(
                        ThaoTacSQL.demSoThamSo(
                                "SELECT 'Dấu ? trong chuỗi' FROM san_pham WHERE ma_san_pham=?"))
                .isEqualTo(1);
        assertThat(
                        ThaoTacSQL.demSoThamSo(
                                "SELECT 'Dấu ? thứ nhất', 'Dấu ? thứ hai' FROM t WHERE a=? AND"
                                        + " b=?"))
                .isEqualTo(2);

        // Escape chuỗi nháy đơn ''
        assertThat(ThaoTacSQL.demSoThamSo("SELECT 'It''s a ? mark' FROM t WHERE id=?"))
                .isEqualTo(1);

        // Comment dòng --
        assertThat(ThaoTacSQL.demSoThamSo("SELECT * FROM t -- Đây là comment chứa ?\n WHERE id=?"))
                .isEqualTo(1);

        // Comment khối /* ... */
        assertThat(
                        ThaoTacSQL.demSoThamSo(
                                "SELECT * FROM t /* Comment khối ? */ WHERE id=? AND ten=?"))
                .isEqualTo(2);

        // Câu lệnh 10 tham số như phieu_spa_treo
        String sql10 =
                """
INSERT INTO phieu_spa_treo(ma_phieu, ma_nhan_vien, ma_khach, so_dien_thoai, ma_thu,
    ma_goi, ma_hen, can_nang, tinh_trang, yeu_cau) VALUES(?,?,?,?,?,?,?,?,?,?)
""";
        assertThat(ThaoTacSQL.demSoThamSo(sql10)).isEqualTo(10);
    }
}
