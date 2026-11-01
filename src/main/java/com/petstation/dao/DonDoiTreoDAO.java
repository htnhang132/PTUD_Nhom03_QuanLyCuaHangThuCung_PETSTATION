package com.petstation.dao;

import com.petstation.model.DonDoiTreo;

public interface DonDoiTreoDAO {
    DonDoiTreo lay(long id);
    void luu(long id, DonDoiTreo don);
    String layMaHoaDonGoc(long dong);
}
