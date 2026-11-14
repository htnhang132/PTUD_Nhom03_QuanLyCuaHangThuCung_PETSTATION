package com.petstation.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.petstation.dao.spa.impl.PhieuTiepNhanSpaDAOImpl;

import org.junit.jupiter.api.Test;

class BaoGiaSpaTest {
    private static final String GIA =
            "DOG|< 5kg|0.00|5.00|225000.00;"
                    + "DOG|5 - 10kg|5.00|10.00|450000.00;DOG|>= 10kg|10.00|NULL|675000.00;"
                    + "CAT|< 5kg|0.00|5.00|225000.00;CAT|5 - 10kg|5.00|10.00|450000.00;"
                    + "CAT|>= 10kg|10.00|NULL|675000.00";

    @Test
    void docGiaHaiLoaiVaCacMocCanNang() {
        for (String loai : new String[] {"DOG", "CAT", "Chó", "Mèo"}) {
            assertEquals(225000, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang(GIA, loai, 3));
            assertEquals(450000, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang(GIA, loai, 5));
            assertEquals(675000, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang(GIA, loai, 10));
            assertEquals(675000, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang(GIA, loai, 20));
        }
        assertEquals(-1, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang(GIA, "BIRD", 3));
    }

    @Test
    void vanHoTroGiaCuVaKhongChonSaiLoai() {
        assertEquals(150000, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang("DOG:0-5:150000", "DOG", 3));
        assertEquals(-1, PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang("DOG:0-5:150000", "CAT", 3));
        assertEquals(
                -1,
                PhieuTiepNhanSpaDAOImpl.timGiaTheoCanNang(
                        "DOG|> 10kg|10.00|NULL|280000.00", "DOG", 10));
    }
}
