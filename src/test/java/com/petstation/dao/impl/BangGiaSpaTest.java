package com.petstation.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.petstation.dao.chung.impl.DanhMucDAOImpl;
import com.petstation.gui.spa.BoSoanThaoBangGia;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

class BangGiaSpaTest {
    @Test
    void docGiaSqlNamPhanVaNapDayDuVaoBoSoanThao() throws Exception {
        String sql =
                "DOG|< 5kg|0.00|5.00|150000.00;"
                        + "DOG|> 10kg|10.00|NULL|280000.00;"
                        + "CAT|Mọi cân nặng|0.00|NULL|180000.00";
        String ui = DanhMucDAOImpl.chuyenBangGiaSpaSangUI(sql);
        assertEquals("Chó|< 5kg|150000;Chó|> 10kg|280000;Mèo|Mọi cân nặng|180000", ui);
        assertEquals(sql, DanhMucDAOImpl.chuyenBangGiaSpaSangCSDL(ui));
        assertEquals(150000, BoSoanThaoBangGia.layGiaNhoNhat(ui));
        SwingUtilities.invokeAndWait(
                () -> {
                    BoSoanThaoBangGia editor = new BoSoanThaoBangGia();
                    editor.dat(ui);
                    assertEquals(ui, editor.lay());
                });
    }

    @Test
    void luuLaiSauKhiSuaGiaVanDungDinhDangSql() throws Exception {
        String sql =
                "DOG|< 5kg|0.00|5.00|225000.00;DOG|5 - 10kg|5.00|10.00|450000.00;"
                        + "DOG|>= 10kg|10.00|NULL|675000.00;CAT|< 5kg|0.00|5.00|225000.00;"
                        + "CAT|5 - 10kg|5.00|10.00|450000.00;CAT|>= 10kg|10.00|NULL|675000.00";
        String ui = DanhMucDAOImpl.chuyenBangGiaSpaSangUI(sql);
        SwingUtilities.invokeAndWait(
                () -> {
                    BoSoanThaoBangGia editor = new BoSoanThaoBangGia();
                    editor.dat(ui);
                    assertEquals(sql, DanhMucDAOImpl.chuyenBangGiaSpaSangCSDL(editor.lay()));
                    editor.dat(ui.replace("675000", "700000"));
                    assertEquals(
                            sql.replace("675000.00", "700000.00"),
                            DanhMucDAOImpl.chuyenBangGiaSpaSangCSDL(editor.lay()));
                });
        assertEquals(sql, DanhMucDAOImpl.chuyenBangGiaSpaSangCSDL(sql));
    }

    @Test
    void vanDocDuocDinhDangCu() {
        assertEquals("Chó|0-5kg|150000", DanhMucDAOImpl.chuyenBangGiaSpaSangUI("Chó|0-5kg|150000"));
        assertEquals("Chó|< 5kg|150000", DanhMucDAOImpl.chuyenBangGiaSpaSangUI("DOG:0-5:150000"));
        assertEquals("", DanhMucDAOImpl.chuyenBangGiaSpaSangUI(null));
    }
}
